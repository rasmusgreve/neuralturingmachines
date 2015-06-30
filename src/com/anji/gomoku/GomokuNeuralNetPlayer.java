/*
 * Copyright (C) 2004 Derek James and Philip Tucker This file is part of ANJI (Another NEAT Java
 * Implementation). ANJI is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA created by Derek James
 */
package com.anji.gomoku;

import com.anji.integration.Activator;
import com.anji.tournament.Player;
import com.anji.ttt.BoardPlayer;

/**
 * Gomoku subject whose moves are determined by a neural net. Neural net input is the board
 * state plus a bias node clamped at 1. Neural net output is the size of the board, where the
 * strongest output corresponding to a legal move is taken as the move.
 * 
 * @author Derek James
 */
public class GomokuNeuralNetPlayer implements BoardPlayer {

private Activator activator;

/**
 * Construct subject from neural net.
 * 
 * @param anActivator
 */
public GomokuNeuralNetPlayer( Activator anActivator ) {
	this.activator = anActivator;
}

/**
 * @see Player#getPlayerId()
 */
public String getPlayerId() {
	return activator.getName();
}

/**
 * Feed board and bias to neural net, get output, and translate it to a move.
 * @see BoardPlayer#move(int[])
 */
public int move( int[] boardState ) {
	if ( ( boardState.length + 1 ) != activator.getInputDimension() )
		throw new IllegalArgumentException( "wrong nnet input dimension, board state size = "
				+ boardState.length + " but nnet input is " + activator.getInputDimension() );

	double[] nnOutput;
	double[] moveList = new double[ boardState.length ];
	double[] gomokuInput = new double[ ( boardState.length + 1 ) ];

	for ( int i = 0; i < ( boardState.length - 1 ); ++i )
		gomokuInput[ i ] = boardState[ i ];
	gomokuInput[ boardState.length ] = 1.0; //bias

	nnOutput = activator.next( gomokuInput );

	for ( int i = 0; i < boardState.length; ++i )
		moveList[ i ] = nnOutput[ i ];

	double max = -Double.MAX_VALUE;
	int myMove = -1;
	for ( int j = 0; j < boardState.length; j++ ) {
		if ( boardState[ j ] == 0 ) {
			if ( max <= moveList[ j ] ) {
				myMove = j;
				max = moveList[ j ];
			}
		}
	}

	return myMove;
}

/**
 * @see com.anji.tournament.Player#reset()
 */
public void reset() {
	activator.reset();
}

/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	return activator.getName();
}

}
