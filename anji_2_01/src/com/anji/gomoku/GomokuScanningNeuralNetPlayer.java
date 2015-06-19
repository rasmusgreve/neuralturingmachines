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
 * Gomoku subject whose moves are determined by a neural net. Neural net input is a 5x5 board
 * grid plus bias clamped at 1. Output is 5x5 grid. Net is activated with all 5x5 grids on bard,
 * and highest output of all activations corrsponding to a legal move is taken as the next move.
 * 
 * @author Derek James
 */
public class GomokuScanningNeuralNetPlayer implements BoardPlayer {

/**
 * Construct subject from neural net.
 * 
 * @param anActivator
 */
public GomokuScanningNeuralNetPlayer( Activator anActivator ) {
	this.activator = anActivator;
}

private Activator activator;

/**
 * @see Player#getPlayerId()
 */
public String getPlayerId() {
	return activator.getName();
}

/**
 * Feed board and bias to neural net, 1 5x5 grid at a time, get output, and translate it to a
 * move.
 * 
 * @see BoardPlayer#move(int[])
 *  
 */
public int move( int[] boardState ) {
	double[][] nnOutput;
	double[] moveList = new double[ boardState.length ];
	int boardSize = (int) Math.sqrt( boardState.length );
	double[][] matrixMoveList = new double[ boardSize ][ boardSize ];
	int[][] matrixBoardState = new int[ boardSize ][ boardSize ];
	int boardIdx = 0;
	double[][] gomokuInput = new double[ 1 ][ 26 ];
	int x = 0;
	int y = 0;

	//translate board from array into matrix
	for ( int i = 0; i < boardSize; i++ ) {
		for ( int j = 0; j < boardSize; j++ ) {
			matrixBoardState[ i ][ j ] = boardState[ boardIdx ];
			boardIdx++;
		}
	}

	for ( int rowIdx = 0; rowIdx < ( boardSize - 4 ); rowIdx++ ) {
		for ( int colIdx = 0; colIdx < ( boardSize - 4 ); colIdx++ ) {
			x = rowIdx;
			int[][] gridToScan = new int[ 5 ][ 5 ];
			int spaceIdx = 0;
			for ( int i = 0; i < 5; i++ ) {
				y = colIdx;
				for ( int j = 0; j < 5; j++ ) {
					gridToScan[ i ][ j ] = matrixBoardState[ x ][ y ];
					y++;
				}
				x++;
				spaceIdx++;
			}
			int inputIdx = 0;
			for ( int i = 0; i < 5; i++ ) {
				for ( int j = 0; j < 5; j++ ) {
					gomokuInput[ 0 ][ inputIdx ] = gridToScan[ i ][ j ];
					inputIdx++;
				}
			}
			gomokuInput[ 0 ][ 25 ] = 1.0; //bias
			nnOutput = activator.next( gomokuInput );
			x = rowIdx;
			y = colIdx;
			int moveIdx = 0;
			for ( int i = 0; i < 5; i++ ) {
				y = colIdx;
				for ( int j = 0; j < 5; j++ ) {
					matrixMoveList[ x ][ y ] += nnOutput[ 0 ][ moveIdx ];
					y++;
					moveIdx++;
				}
				x++;
			}
		}
	}

	int moveIdx = 0;
	for ( int i = 0; i < boardSize; i++ ) {
		for ( int j = 0; j < boardSize; j++ ) {
			moveList[ moveIdx ] = matrixMoveList[ i ][ j ];
			moveIdx++;
		}
	}

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
