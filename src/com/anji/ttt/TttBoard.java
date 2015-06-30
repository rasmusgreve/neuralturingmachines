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

import java.util.Arrays;

/**
 * Tic-Tac-Toe board.
 * @author Derek James
 */
public class TttBoard implements Board {

private final static int BOARD_SIZE = 3;

private int[] boardState;

/**
 * initialize board with all empty spaces
 */
public void initializeBoard() {
	boardState = new int[ ( BOARD_SIZE * BOARD_SIZE ) ];
	Arrays.fill( boardState, 0 );
}

/**
 * @param newMove
 * @return boolean true iff <code>newMove</code> is an empty space
 */
public boolean checkLegalMove( int newMove ) {
	boolean legalMove;
	if ( boardState[ newMove ] == 0 ) {
		legalMove = true;
	}
	else {
		legalMove = false;
	}
	return legalMove;
}

/**
 * Places friendly piece on <code>boardState</code> at position <code>newMove</code>.
 * 
 * @param newMove
 */
public void updateBoard( int newMove ) {
	boardState[ newMove ] = 1;
}

/**
 * @return boolean true if there exists 3 friendly pieces in a row
 */
public boolean checkForWin() {
	boolean wonGame = false;
	int[][] matrixBoardState = new int[ BOARD_SIZE ][ BOARD_SIZE ];
	int boardIdx = 0;
	for ( int i = 0; i < BOARD_SIZE; i++ ) {
		for ( int j = 0; j < BOARD_SIZE; j++ ) {
			matrixBoardState[ i ][ j ] = boardState[ boardIdx ];
			boardIdx++;
		}
	}
	// check rows for win
	for ( int i = 0; i < BOARD_SIZE; i++ ) {
		int rowTotal = 0;
		for ( int j = 0; j < BOARD_SIZE; j++ ) {
			rowTotal += matrixBoardState[ i ][ j ];
		}
		if ( rowTotal == BOARD_SIZE ) {
			wonGame = true;
		}
	}
	// check columns for win
	for ( int i = 0; i < BOARD_SIZE; i++ ) {
		int columnTotal = 0;
		for ( int j = 0; j < BOARD_SIZE; j++ ) {
			columnTotal += matrixBoardState[ j ][ i ];
		}
		if ( columnTotal == 3 ) {
			wonGame = true;
		}
	}
	// check diagonals for win
	int diagTotal = 0;
	for ( int i = 0; i < BOARD_SIZE; i++ ) {
		for ( int j = 0; j < BOARD_SIZE; j++ ) {
			if ( i == j ) {
				diagTotal += matrixBoardState[ i ][ j ];
			}
		}
		if ( diagTotal == BOARD_SIZE ) {
			wonGame = true;
		}
	}

	diagTotal = 0;
	for ( int i = 0; i < BOARD_SIZE; i++ ) {
		for ( int j = 0; j < BOARD_SIZE; j++ ) {
			if ( ( i + j ) == 2 ) {
				diagTotal += matrixBoardState[ i ][ j ];
			}
		}
		if ( diagTotal == BOARD_SIZE ) {
			wonGame = true;
		}
	}

	return wonGame;
}

/**
 * @return boolean true if board is full and neither subject has won
 */
public boolean checkForTie() {
	boolean tieGame = false;
	boolean boardFull = true;
	for ( int i = 0; i < boardState.length; i++ ) {
		if ( boardState[ i ] == 0 ) {
			boardFull = false;
		}
	}
	boolean wonGame = checkForWin();
	if ( wonGame == false && boardFull == true ) {
		tieGame = true;
	}
	return tieGame;
}

/**
 * @return String representation of <code>boardState</code>
 */
public String displayBoard() {
	StringBuffer result = new StringBuffer();

	for ( int i = 0; i < boardState.length; i++ ) {
		if ( boardState[ i ] == -1 )
			result.append( " " );
		else
			result.append( "  " );
		result.append( boardState[ i ] );
		if ( ( i + 1 ) % BOARD_SIZE == 0 )
			result.append( "\n" );
	}
	result.append( "\n\n" );
	return result.toString();
}

/**
 * @see Board#getBoardState()
 */
public int[] getBoardState() {
	return boardState;
}

/**
 * @see com.anji.ttt.Board#swap()
 */
public void swap() {
	for ( int i = 0; i < boardState.length; ++i )
		boardState[ i ] *= -1;
}

/**
 * @see com.anji.ttt.Board#getBoardSize()
 */
public int getBoardSize() {
	return BOARD_SIZE;
}

}
