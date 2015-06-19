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
package com.anji.gomoku;

import org.apache.log4j.Logger;

import com.anji.ttt.Board;

/**
 * Represents the playing area of Gomoku.
 * @author Derek James
 */
public class GomokuBoard implements Board {

private static Logger logger = Logger.getLogger( GomokuBoard.class );

private int[] boardState;

private int boardSize;

/**
 * @see com.anji.ttt.Board#getBoardSize()
 */
public int getBoardSize() {
	return boardSize;
}

/**
 * Initialize board to be a square with each side of length <code>aSize</code>
 * @param aSize
 */
public GomokuBoard( int aSize ) {
	boardSize = aSize;
	boardState = new int[ boardSize * boardSize ];
}

/**
 * initialize all positions to empty.
 * @see Board#initializeBoard()
 */
public void initializeBoard() {
	for ( int i = 0; i < boardState.length; i++ )
		boardState[ i ] = 0;
}

/**
 * Return true if position <code>newMove</code> is empty, false otherwise.
 * @see Board#checkLegalMove(int)
 */
public boolean checkLegalMove( int newMove ) {
	return ( boardState[ newMove ] == 0 );
}

/**
 * Place friendly piece in position <code>newMove</code> in array <code>boardState</code>.
 * @see Board#updateBoard(int)
 */
public void updateBoard( int newMove ) {
	boardState[ newMove ] = 1;
}

/**
 * @return boolean true if board contains 5 1s in a row, horizontal, vertical, or diagonal;
 * false otherwise
 * @see Board#checkForWin()
 */
public boolean checkForWin() {
	boolean wonGame = false;
	int[][] matrixBoardState = new int[ 15 ][ 15 ];
	int boardIdx = 0;
	for ( int i = 0; i < Math.sqrt( boardState.length ); i++ ) {
		for ( int j = 0; j < Math.sqrt( boardState.length ); j++ ) {
			matrixBoardState[ i ][ j ] = boardState[ boardIdx ];
			boardIdx++;
		}
	}

	//check for horizontal 5-in-a-row
	for ( int i = 0; i < Math.sqrt( boardState.length ); i++ ) {
		int startPoint = 0;
		for ( int groupsToCheck = 0; groupsToCheck < 11; groupsToCheck++ ) {
			int fiveSpaceTotal = 0;
			for ( int j = 0; j < 5; j++ ) {
				fiveSpaceTotal = fiveSpaceTotal + matrixBoardState[ i ][ ( startPoint + j ) ];
			}
			if ( fiveSpaceTotal == 5 ) {
				wonGame = true;
			}
			startPoint++;
		}
	}
	//check for vertical 5-in-a-row
	for ( int j = 0; j < Math.sqrt( boardState.length ); j++ ) {
		int startPoint = 0;
		for ( int groupsToCheck = 0; groupsToCheck < ( Math.sqrt( boardState.length ) - 4 ); groupsToCheck++ ) {
			int fiveSpaceTotal = 0;
			for ( int i = 0; i < 5; i++ ) {
				fiveSpaceTotal = fiveSpaceTotal + matrixBoardState[ ( startPoint + i ) ][ j ];
			}
			if ( fiveSpaceTotal == 5 ) {
				wonGame = true;
			}
			startPoint++;
		}
	}

	//check for diagonal 5-in-a-row, left-to-right
	for ( int i = 0; i < 11; i++ ) {
		int startPointX = 0;
		int startPointY = 0;
		for ( int groupsToCheck = 0; groupsToCheck < 11; groupsToCheck++ ) {
			int fiveSpaceTotal = 0;
			startPointX = 0;
			for ( int j = 0; j < 5; j++ ) {
				fiveSpaceTotal = fiveSpaceTotal
						+ matrixBoardState[ ( startPointX + i ) ][ ( startPointY + j ) ];
				startPointX++;
			}
			if ( fiveSpaceTotal == 5 ) {
				wonGame = true;
			}
			startPointY++;
		}
	}

	//check for diagonal 5-in-a-row, right-to-left
	for ( int j = 0; j < 11; j++ ) {
		int startPointX = 14;
		int startPointY = 0;
		for ( int groupsToCheck = 0; groupsToCheck < 11; groupsToCheck++ ) {
			int fiveSpaceTotal = 0;
			startPointY = 0;
			for ( int i = 0; i < 5; i++ ) {
				fiveSpaceTotal = fiveSpaceTotal
						+ matrixBoardState[ ( startPointX - i ) ][ ( startPointY + j ) ];
				startPointY++;
			}
			if ( fiveSpaceTotal == 5 ) {
				wonGame = true;
			}
			startPointX--;
		}
	}

	return wonGame;
}

/**
 * @return boolean true if board contains no empty spaces
 * @see Board#checkForTie()
 */
public boolean checkForTie() {
	boolean tieGame = false;
	boolean boardFull = true;
	for ( int i = 0; i < boardState.length; i++ ) {
		if ( boardState[ i ] == 0 ) {
			boardFull = false;
		}
	}
	if ( boardFull == true ) {
		tieGame = true;
		logger.debug( "tie" );
	}
	return tieGame;
}

/**
 * @see Board#displayBoard()
 */
public String displayBoard() {
	StringBuffer result = new StringBuffer();

	for ( int i = 0; i < boardState.length; i++ ) {
		if ( boardState[ i ] == -1 )
			result.append( " " );
		result.append( boardState[ i ] );
		if ( ( i + 1 ) % Math.sqrt( boardState.length ) == 0 )
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
		boardState[ i ] = ( boardState[ i ] * -1 );
}

}
