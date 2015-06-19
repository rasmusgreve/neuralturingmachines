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
 * Tic-Tac-Toe subject whose moves are determined by a 10x9 neural network which is shown the
 * board 4 times, once for each rotation in the 4 cardinal directions.
 * @author Derek James
 */
public class TttRotatingNeuralNetPlayer implements BoardPlayer {

private Activator activator = null;

/**
 * Create subject whose "brains" are <code>anActivator</code>, 10 inputs and 9 outputs.
 * @param anActivator
 */
public TttRotatingNeuralNetPlayer( Activator anActivator ) {
	this.activator = anActivator;
}

/**
 * @see Player#getPlayerId()
 */
public String getPlayerId() {
	return activator.getName();
}

/**
 * Iteratively pass <code>boardState</code> rotated in each of the for cardinal directions, as
 * input to activator and place token in empty space corresponding to strongest output.
 * @param boardState
 * @see BoardPlayer#move(int[])
 */
public int move( int[] boardState ) {
	double[] tttInput = new double[ 10 ];
	double[] nnOutput;
	double[] moveList = new double[ 9 ];

	for ( int i = 0; i < 9; i++ ) {
		tttInput[ i ] = boardState[ i ];
	}
	tttInput[ 9 ] = 1.0; //bias

	nnOutput = activator.next( tttInput );

	for ( int i = 0; i < 9; i++ ) {
		moveList[ i ] = nnOutput[ i ];
	}

	int[] rotatedBoard = new int[ 9 ];

	rotatedBoard[ 0 ] = boardState[ 6 ];
	rotatedBoard[ 1 ] = boardState[ 3 ];
	rotatedBoard[ 2 ] = boardState[ 0 ];
	rotatedBoard[ 3 ] = boardState[ 7 ];
	rotatedBoard[ 4 ] = boardState[ 4 ];
	rotatedBoard[ 5 ] = boardState[ 1 ];
	rotatedBoard[ 6 ] = boardState[ 8 ];
	rotatedBoard[ 7 ] = boardState[ 5 ];
	rotatedBoard[ 8 ] = boardState[ 2 ];

	for ( int i = 0; i < 9; i++ ) {
		tttInput[ i ] = rotatedBoard[ i ];
	}
	tttInput[ 9 ] = 1.0; //bias

	nnOutput = activator.next( tttInput );

	for ( int i = 0; i < 9; i++ ) {
		moveList[ i ] += nnOutput[ i ];
	}

	rotatedBoard[ 0 ] = boardState[ 8 ];
	rotatedBoard[ 1 ] = boardState[ 7 ];
	rotatedBoard[ 2 ] = boardState[ 6 ];
	rotatedBoard[ 3 ] = boardState[ 5 ];
	rotatedBoard[ 4 ] = boardState[ 4 ];
	rotatedBoard[ 5 ] = boardState[ 3 ];
	rotatedBoard[ 6 ] = boardState[ 2 ];
	rotatedBoard[ 7 ] = boardState[ 1 ];
	rotatedBoard[ 8 ] = boardState[ 0 ];

	for ( int i = 0; i < 9; i++ ) {
		tttInput[ i ] = rotatedBoard[ i ];
	}
	tttInput[ 9 ] = 1.0; //bias

	nnOutput = activator.next( tttInput );

	for ( int i = 0; i < 9; i++ ) {
		moveList[ i ] += nnOutput[ i ];
	}

	rotatedBoard[ 0 ] = boardState[ 2 ];
	rotatedBoard[ 1 ] = boardState[ 5 ];
	rotatedBoard[ 2 ] = boardState[ 8 ];
	rotatedBoard[ 3 ] = boardState[ 1 ];
	rotatedBoard[ 4 ] = boardState[ 4 ];
	rotatedBoard[ 5 ] = boardState[ 7 ];
	rotatedBoard[ 6 ] = boardState[ 0 ];
	rotatedBoard[ 7 ] = boardState[ 3 ];
	rotatedBoard[ 8 ] = boardState[ 6 ];

	for ( int i = 0; i < 9; i++ ) {
		tttInput[ i ] = rotatedBoard[ i ];
	}
	tttInput[ 9 ] = 1.0; //bias

	nnOutput = activator.next( tttInput );

	for ( int i = 0; i < 9; i++ ) {
		moveList[ i ] += nnOutput[ i ];
	}

	double max = -Double.MAX_VALUE;
	int myMove = -1;
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

