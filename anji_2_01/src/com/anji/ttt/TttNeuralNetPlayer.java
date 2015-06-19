/*
 * Copyright (C) 2004 Derek James and Philip Tucker
 * 
 * This file is part of ANJI (Another NEAT Java Implementation).
 * 
 * ANJI is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 * 
 * created by Derek James
 */
package com.anji.ttt;

import com.anji.tournament.Player;
import com.anji.integration.Activator;

/**
 * Tic-Tac-Toe subject whose moves are determined by a 10x9 neural network.
 * @author Derek James
 */
public class TttNeuralNetPlayer implements BoardPlayer {

private Activator activator = null;

/**
 * Create subject whose "brains" are <code>anActivator</code>, 10 inputs and 9 outputs.
 * @param anActivator
 */
public TttNeuralNetPlayer( Activator anActivator ) {
	this.activator = anActivator;
}

/**
 * @see Player#getPlayerId()
 */
public String getPlayerId() {
	return activator.getName();
}

/**
 * Pass <code>boardState</code> as input to activator and place token in empty space
 * corresponding to strongest output.
 * @param boardState
 * @see BoardPlayer#move(int[])
 */
public int move( int[] boardState ) {
	double[] nnOutput;
	double[] moveList = new double[ 9 ];
	double[] tttInput = new double[ 10 ];
	int myMove = -1;

	for ( int i = 0; i < 9; i++ ) {
		tttInput[ i ] = boardState[ i ];
	}
	tttInput[ 9 ] = 1.0; //bias

	nnOutput = activator.next( tttInput );

	for ( int i = 0; i < 9; i++ ) {
		moveList[ i ] = nnOutput[ i ];
	}

	double max = -Double.MAX_VALUE;
	for ( int j = 0; j < 9; j++ ) {
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

