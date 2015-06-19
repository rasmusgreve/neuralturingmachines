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

import com.anji.tournament.Player;
import com.anji.ttt.BoardPlayer;
import com.anji.util.Configurable;
import com.anji.util.Properties;
import com.anji.util.Randomizer;

/**
 * Gomoku player with the following strategy (in order of preference):
 * <ol>
 * <li>complete 5 in a row</li>
 * <li>block opponent's 5 in a row</li>
 * <li>play randomly</li>
 * </ol>
 * 
 * @author Derek James
 */
public class GomokuBtrPlayer implements BoardPlayer, Configurable {

private static final String playerId = "Gomoku Better-Than-Random";

private Randomizer randomizer;

/**
 * should call <code>init()</code> after ctor
 */
public GomokuBtrPlayer() {
	// no-op
}

/**
 * @see Player#getPlayerId()
 */
public String getPlayerId() {
	return playerId;
}

/**
 * Place move with the following criteria: (1) complete 5 in a row (2) block opponent's 5 in a
 * row (3) play randomly
 * 
 * @param boardState
 * @return int new move
 */
public int move( int[] boardState ) {
	int myMove = 0;
	int boardSize = (int) Math.sqrt( boardState.length );
	int[][] matrixBoardState = new int[ boardSize ][ boardSize ];
	int boardIdx = 0;
	boolean winningMoveOpen = false;
	boolean blockingMoveOpen = false;

	//translate board from array into matrix
	for ( int i = 0; i < boardSize; i++ ) {
		for ( int j = 0; j < boardSize; j++ ) {
			matrixBoardState[ i ][ j ] = boardState[ boardIdx ];
			boardIdx++;
		}
	}

	//complete horizontal 5-in-a-row
	for ( int i = 0; i < boardSize; i++ ) {
		int startPoint = 0;
		for ( int groupsToCheck = 0; groupsToCheck < ( boardSize - 4 ); groupsToCheck++ ) {
			int fiveSpaceTotal = 0;
			boolean openWinningSpace = false;
			int winningMove = 0;
			for ( int j = 0; j < 5; j++ ) {
				fiveSpaceTotal = fiveSpaceTotal + matrixBoardState[ i ][ ( startPoint + j ) ];
				if ( matrixBoardState[ i ][ ( startPoint + j ) ] == 0 ) {
					openWinningSpace = true;
					winningMove = ( ( i * boardSize ) + ( startPoint + j ) );
				}
			}
			if ( fiveSpaceTotal == 4 && openWinningSpace == true ) {
				myMove = winningMove;
				winningMoveOpen = true;
			}
			startPoint++;
		}
	}

	//complete vertical 5-in-a-row
	if ( winningMoveOpen == false ) {
		for ( int j = 0; j < boardSize; j++ ) {
			int startPoint = 0;
			for ( int groupsToCheck = 0; groupsToCheck < ( boardSize - 4 ); groupsToCheck++ ) {
				int fiveSpaceTotal = 0;
				boolean openWinningSpace = false;
				int winningMove = 0;
				for ( int i = 0; i < 5; i++ ) {
					fiveSpaceTotal = fiveSpaceTotal + matrixBoardState[ ( startPoint + i ) ][ j ];
					if ( matrixBoardState[ ( startPoint + i ) ][ j ] == 0 ) {
						openWinningSpace = true;
						winningMove = ( ( ( startPoint + i ) * boardSize ) + j );
					}
				}
				if ( fiveSpaceTotal == 4 && openWinningSpace == true ) {
					myMove = winningMove;
					winningMoveOpen = true;
				}
				startPoint++;
			}
		}
	}

	//complete diagonal 5-in-a-row, left-to-right
	if ( winningMoveOpen == false ) {
		for ( int i = 0; i < ( boardSize - 4 ); i++ ) {
			int startPointX = 0;
			int startPointY = 0;
			for ( int groupsToCheck = 0; groupsToCheck < ( boardSize - 4 ); groupsToCheck++ ) {
				int fiveSpaceTotal = 0;
				boolean openWinningSpace = false;
				int winningMove = 0;
				startPointX = 0;
				for ( int j = 0; j < 5; j++ ) {
					fiveSpaceTotal = fiveSpaceTotal
							+ matrixBoardState[ ( startPointX + i ) ][ ( startPointY + j ) ];
					if ( matrixBoardState[ ( startPointX + i ) ][ ( startPointY + j ) ] == 0 ) {
						openWinningSpace = true;
						winningMove = ( ( ( startPointX + i ) * boardSize ) + ( startPointY + j ) );
					}
					startPointX++;
				}
				if ( fiveSpaceTotal == 4 && openWinningSpace == true ) {
					myMove = winningMove;
					winningMoveOpen = true;
				}
				startPointY++;
			}
		}
	}

	//complete diagonal 5-in-a-row, right-to-left
	if ( winningMoveOpen == false ) {
		for ( int j = 0; j < ( boardSize - 4 ); j++ ) {
			int startPointX = ( boardSize - 1 );
			int startPointY = 0;
			for ( int groupsToCheck = 0; groupsToCheck < ( boardSize - 4 ); groupsToCheck++ ) {
				int fiveSpaceTotal = 0;
				boolean openWinningSpace = false;
				int winningMove = 0;
				startPointY = 0;
				for ( int i = 0; i < 5; i++ ) {
					fiveSpaceTotal = fiveSpaceTotal
							+ matrixBoardState[ ( startPointX - i ) ][ ( startPointY + j ) ];
					if ( matrixBoardState[ ( startPointX - i ) ][ ( startPointY + j ) ] == 0 ) {
						openWinningSpace = true;
						winningMove = ( ( ( startPointX - i ) * boardSize ) + ( startPointY + j ) );
					}
					startPointY++;
				}
				if ( fiveSpaceTotal == 4 && openWinningSpace == true ) {
					myMove = winningMove;
					winningMoveOpen = true;
				}
				startPointX--;
			}
		}
	}

	//block opponent's horizontal 5-in-a-row
	if ( winningMoveOpen == false && blockingMoveOpen == false ) {
		for ( int i = 0; i < boardSize; i++ ) {
			int startPoint = 0;
			for ( int groupsToCheck = 0; groupsToCheck < ( boardSize - 4 ); groupsToCheck++ ) {
				int fiveSpaceTotal = 0;
				boolean openBlockingSpace = false;
				int blockingMove = 0;
				for ( int j = 0; j < 5; j++ ) {
					fiveSpaceTotal = fiveSpaceTotal + matrixBoardState[ i ][ ( startPoint + j ) ];
					if ( matrixBoardState[ i ][ ( startPoint + j ) ] == 0 ) {
						openBlockingSpace = true;
						blockingMove = ( ( i * boardSize ) + ( startPoint + j ) );
					}
				}
				if ( fiveSpaceTotal == -4 && openBlockingSpace == true ) {
					myMove = blockingMove;
					blockingMoveOpen = true;
				}
				startPoint++;
			}
		}
	}

	//block opponent's vertical 5-in-a-row
	if ( winningMoveOpen == false && blockingMoveOpen == false ) {
		for ( int j = 0; j < boardSize; j++ ) {
			int startPoint = 0;
			for ( int groupsToCheck = 0; groupsToCheck < ( boardSize - 4 ); groupsToCheck++ ) {
				int fiveSpaceTotal = 0;
				boolean openBlockingSpace = false;
				int blockingMove = 0;
				for ( int i = 0; i < 5; i++ ) {
					fiveSpaceTotal = fiveSpaceTotal + matrixBoardState[ ( startPoint + i ) ][ j ];
					if ( matrixBoardState[ ( startPoint + i ) ][ j ] == 0 ) {
						openBlockingSpace = true;
						blockingMove = ( ( ( startPoint + i ) * boardSize ) + j );
					}
				}
				if ( fiveSpaceTotal == -4 && openBlockingSpace == true ) {
					myMove = blockingMove;
					blockingMoveOpen = true;
				}
				startPoint++;
			}
		}
	}

	//block opponent's diagonal 5-in-a-row, left-to-right
	if ( winningMoveOpen == false && blockingMoveOpen == false ) {
		for ( int i = 0; i < ( boardSize - 4 ); i++ ) {
			int startPointX = 0;
			int startPointY = 0;
			for ( int groupsToCheck = 0; groupsToCheck < ( boardSize - 4 ); groupsToCheck++ ) {
				int fiveSpaceTotal = 0;
				boolean openBlockingSpace = false;
				int blockingMove = 0;
				startPointX = 0;
				for ( int j = 0; j < 5; j++ ) {
					fiveSpaceTotal = fiveSpaceTotal
							+ matrixBoardState[ ( startPointX + i ) ][ ( startPointY + j ) ];
					if ( matrixBoardState[ ( startPointX + i ) ][ ( startPointY + j ) ] == 0 ) {
						openBlockingSpace = true;
						blockingMove = ( ( ( startPointX + i ) * boardSize ) + ( startPointY + j ) );
					}
					startPointX++;
				}
				if ( fiveSpaceTotal == -4 && openBlockingSpace == true ) {
					myMove = blockingMove;
					blockingMoveOpen = true;
				}
				startPointY++;
			}
		}
	}

	//block opponent's diagonal 5-in-a-row, right-to-left
	if ( winningMoveOpen == false && blockingMoveOpen == false ) {
		for ( int j = 0; j < ( boardSize - 4 ); j++ ) {
			int startPointX = ( boardSize - 1 );
			int startPointY = 0;
			for ( int groupsToCheck = 0; groupsToCheck < ( boardSize - 4 ); groupsToCheck++ ) {
				int fiveSpaceTotal = 0;
				boolean openBlockingSpace = false;
				int blockingMove = 0;
				startPointY = 0;
				for ( int i = 0; i < 5; i++ ) {
					fiveSpaceTotal = fiveSpaceTotal
							+ matrixBoardState[ ( startPointX - i ) ][ ( startPointY + j ) ];
					if ( matrixBoardState[ ( startPointX - i ) ][ ( startPointY + j ) ] == 0 ) {
						openBlockingSpace = true;
						blockingMove = ( ( ( startPointX - i ) * boardSize ) + ( startPointY + j ) );
					}
					startPointY++;
				}
				if ( fiveSpaceTotal == -4 && openBlockingSpace == true ) {
					myMove = blockingMove;
					blockingMoveOpen = true;
				}
				startPointX--;
			}
		}
	}

	//otherwise move randomly
	if ( winningMoveOpen == false && blockingMoveOpen == false ) {
		myMove = randomizer.getRand().nextInt( boardState.length );
	}

	return myMove;
}

/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties props ) throws Exception {
	randomizer = (Randomizer) props.singletonObjectProperty( Randomizer.class );
}

/**
 * @see com.anji.tournament.Player#reset()
 */
public void reset() {
	// no-op
}

/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	return playerId;
}

}
