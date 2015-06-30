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

import java.util.Random;

import com.anji.tournament.Player;
import com.anji.util.Configurable;
import com.anji.util.Properties;
import com.anji.util.Randomizer;

/**
 * "Best" tic-tac-toe subject, with the caveate that it can be forked. See <code>move()</code>
 * for details.
 * @author Philip Tucker
 */
public class TttForkablePlayer implements BoardPlayer, Configurable {

private static final String playerId = "TTT Forkable Player";

private Random rand;

/**
 * @see Player#getPlayerId()
 */
public String getPlayerId() {
	return playerId;
}

/**
 * Play according to the following criteria, in order of preference:
 * <ol>
 * <li>If the board is empty, move randomly</li>
 * <li>If winning move is available, make it</li>
 * <li>If opponent has winning move, block it</li>
 * <li>Try to fork opponent</li>
 * <li>Play in center if open</li>
 * <li>Play randomly in open corner</li>
 * <li>Play randomly</li>
 * </ol>
 * @see BoardPlayer#move(int[])
 */
public int move( int[] boardState ) {
	int myMove = 0;
	int randMoveIdx = 0;

	boolean firstMove = true;

	for ( int i = 0; i < 9 && firstMove; i++ ) {
		if ( boardState[ i ] == -1 )
			firstMove = false;
	}

	if ( firstMove )
		myMove = rand.nextInt( 9 );

	else if ( ( boardState[ 0 ] == 1 ) && //complete winning moves
			( boardState[ 1 ] == 1 ) && ( boardState[ 2 ] == 0 ) ) {
		myMove = 2;
	}

	else if ( ( boardState[ 0 ] == 1 ) && ( boardState[ 2 ] == 1 ) && ( boardState[ 1 ] == 0 ) ) {
		myMove = 1;
	}

	else if ( ( boardState[ 1 ] == 1 ) && ( boardState[ 2 ] == 1 ) && ( boardState[ 0 ] == 0 ) ) {
		myMove = 0;
	}

	else if ( ( boardState[ 3 ] == 1 ) && ( boardState[ 4 ] == 1 ) && ( boardState[ 5 ] == 0 ) ) {
		myMove = 5;
	}

	else if ( ( boardState[ 3 ] == 1 ) && ( boardState[ 5 ] == 1 ) && ( boardState[ 4 ] == 0 ) ) {
		myMove = 4;
	}

	else if ( ( boardState[ 4 ] == 1 ) && ( boardState[ 5 ] == 1 ) && ( boardState[ 3 ] == 0 ) ) {
		myMove = 3;
	}

	else if ( ( boardState[ 6 ] == 1 ) && ( boardState[ 7 ] == 1 ) && ( boardState[ 8 ] == 0 ) ) {
		myMove = 8;
	}

	else if ( ( boardState[ 6 ] == 1 ) && ( boardState[ 8 ] == 1 ) && ( boardState[ 7 ] == 0 ) ) {
		myMove = 7;
	}

	else if ( ( boardState[ 7 ] == 1 ) && ( boardState[ 8 ] == 1 ) && ( boardState[ 6 ] == 0 ) ) {
		myMove = 6;
	}

	else if ( ( boardState[ 0 ] == 1 ) && ( boardState[ 3 ] == 1 ) && ( boardState[ 6 ] == 0 ) ) {
		myMove = 6;
	}

	else if ( ( boardState[ 0 ] == 1 ) && ( boardState[ 6 ] == 1 ) && ( boardState[ 3 ] == 0 ) ) {
		myMove = 3;
	}

	else if ( ( boardState[ 3 ] == 1 ) && ( boardState[ 6 ] == 1 ) && ( boardState[ 0 ] == 0 ) ) {
		myMove = 0;
	}

	else if ( ( boardState[ 1 ] == 1 ) && ( boardState[ 4 ] == 1 ) && ( boardState[ 7 ] == 0 ) ) {
		myMove = 7;
	}

	else if ( ( boardState[ 1 ] == 1 ) && ( boardState[ 7 ] == 1 ) && ( boardState[ 4 ] == 0 ) ) {
		myMove = 4;
	}

	else if ( ( boardState[ 4 ] == 1 ) && ( boardState[ 7 ] == 1 ) && ( boardState[ 1 ] == 0 ) ) {
		myMove = 1;
	}

	else if ( ( boardState[ 2 ] == 1 ) && ( boardState[ 5 ] == 1 ) && ( boardState[ 8 ] == 0 ) ) {
		myMove = 8;
	}

	else if ( ( boardState[ 2 ] == 1 ) && ( boardState[ 8 ] == 1 ) && ( boardState[ 5 ] == 0 ) ) {
		myMove = 5;
	}

	else if ( ( boardState[ 5 ] == 1 ) && ( boardState[ 8 ] == 1 ) && ( boardState[ 2 ] == 0 ) ) {
		myMove = 2;
	}

	else if ( ( boardState[ 0 ] == 1 ) && ( boardState[ 4 ] == 1 ) && ( boardState[ 8 ] == 0 ) ) {
		myMove = 8;
	}

	else if ( ( boardState[ 0 ] == 1 ) && ( boardState[ 8 ] == 1 ) && ( boardState[ 4 ] == 0 ) ) {
		myMove = 4;
	}

	else if ( ( boardState[ 4 ] == 1 ) && ( boardState[ 8 ] == 1 ) && ( boardState[ 0 ] == 0 ) ) {
		myMove = 0;
	}

	else if ( ( boardState[ 6 ] == 1 ) && ( boardState[ 4 ] == 1 ) && ( boardState[ 2 ] == 0 ) ) {
		myMove = 2;
	}

	else if ( ( boardState[ 6 ] == 1 ) && ( boardState[ 2 ] == 1 ) && ( boardState[ 4 ] == 0 ) ) {
		myMove = 4;
	}

	else if ( ( boardState[ 4 ] == 1 ) && ( boardState[ 2 ] == 1 ) && ( boardState[ 6 ] == 0 ) ) {
		myMove = 6;
	}

	else if ( ( boardState[ 0 ] == -1 ) && //block opponent's winning moves
			( boardState[ 1 ] == -1 ) && ( boardState[ 2 ] == 0 ) ) {
		myMove = 2;
	}

	else if ( ( boardState[ 0 ] == -1 ) && ( boardState[ 2 ] == -1 ) && ( boardState[ 1 ] == 0 ) ) {
		myMove = 1;
	}

	else if ( ( boardState[ 1 ] == -1 ) && ( boardState[ 2 ] == -1 ) && ( boardState[ 0 ] == 0 ) ) {
		myMove = 0;
	}

	else if ( ( boardState[ 3 ] == -1 ) && ( boardState[ 4 ] == -1 ) && ( boardState[ 5 ] == 0 ) ) {
		myMove = 5;
	}

	else if ( ( boardState[ 3 ] == -1 ) && ( boardState[ 5 ] == -1 ) && ( boardState[ 4 ] == 0 ) ) {
		myMove = 4;
	}

	else if ( ( boardState[ 4 ] == -1 ) && ( boardState[ 5 ] == -1 ) && ( boardState[ 3 ] == 0 ) ) {
		myMove = 3;
	}

	else if ( ( boardState[ 6 ] == -1 ) && ( boardState[ 7 ] == -1 ) && ( boardState[ 8 ] == 0 ) ) {
		myMove = 8;
	}

	else if ( ( boardState[ 6 ] == -1 ) && ( boardState[ 8 ] == -1 ) && ( boardState[ 7 ] == 0 ) ) {
		myMove = 7;
	}

	else if ( ( boardState[ 7 ] == -1 ) && ( boardState[ 8 ] == -1 ) && ( boardState[ 6 ] == 0 ) ) {
		myMove = 6;
	}

	else if ( ( boardState[ 0 ] == -1 ) && ( boardState[ 3 ] == -1 ) && ( boardState[ 6 ] == 0 ) ) {
		myMove = 6;
	}

	else if ( ( boardState[ 0 ] == -1 ) && ( boardState[ 6 ] == -1 ) && ( boardState[ 3 ] == 0 ) ) {
		myMove = 3;
	}

	else if ( ( boardState[ 3 ] == -1 ) && ( boardState[ 6 ] == -1 ) && ( boardState[ 0 ] == 0 ) ) {
		myMove = 0;
	}

	else if ( ( boardState[ 1 ] == -1 ) && ( boardState[ 4 ] == -1 ) && ( boardState[ 7 ] == 0 ) ) {
		myMove = 7;
	}

	else if ( ( boardState[ 1 ] == -1 ) && ( boardState[ 7 ] == -1 ) && ( boardState[ 4 ] == 0 ) ) {
		myMove = 4;
	}

	else if ( ( boardState[ 4 ] == -1 ) && ( boardState[ 7 ] == -1 ) && ( boardState[ 1 ] == 0 ) ) {
		myMove = 1;
	}

	else if ( ( boardState[ 2 ] == -1 ) && ( boardState[ 5 ] == -1 ) && ( boardState[ 8 ] == 0 ) ) {
		myMove = 8;
	}

	else if ( ( boardState[ 2 ] == -1 ) && ( boardState[ 8 ] == -1 ) && ( boardState[ 5 ] == 0 ) ) {
		myMove = 5;
	}

	else if ( ( boardState[ 5 ] == -1 ) && ( boardState[ 8 ] == -1 ) && ( boardState[ 2 ] == 0 ) ) {
		myMove = 2;
	}

	else if ( ( boardState[ 0 ] == -1 ) && ( boardState[ 4 ] == -1 ) && ( boardState[ 8 ] == 0 ) ) {
		myMove = 8;
	}

	else if ( ( boardState[ 0 ] == -1 ) && ( boardState[ 8 ] == -1 ) && ( boardState[ 4 ] == 0 ) ) {
		myMove = 4;
	}

	else if ( ( boardState[ 4 ] == -1 ) && ( boardState[ 8 ] == -1 ) && ( boardState[ 0 ] == 0 ) ) {
		myMove = 0;
	}

	else if ( ( boardState[ 6 ] == -1 ) && ( boardState[ 4 ] == -1 ) && ( boardState[ 2 ] == 0 ) ) {
		myMove = 2;
	}

	else if ( ( boardState[ 6 ] == -1 ) && ( boardState[ 2 ] == -1 ) && ( boardState[ 4 ] == 0 ) ) {
		myMove = 4;
	}

	else if ( ( boardState[ 4 ] == -1 ) && ( boardState[ 2 ] == -1 ) && ( boardState[ 6 ] == 0 ) ) {
		myMove = 6;
	}

	else if ( ( boardState[ 4 ] == -1 ) && //if corner fork is possible,
			( boardState[ 0 ] == 1 ) && //set it up
			( boardState[ 1 ] == 0 ) && ( boardState[ 2 ] == 0 ) && ( boardState[ 3 ] == 0 )
			&& ( boardState[ 5 ] == 0 ) && ( boardState[ 6 ] == 0 ) && ( boardState[ 7 ] == 0 )
			&& ( boardState[ 8 ] == 0 ) ) {
		myMove = 8;
	}

	else if ( ( boardState[ 4 ] == -1 ) && ( boardState[ 0 ] == 0 ) && ( boardState[ 1 ] == 0 )
			&& ( boardState[ 2 ] == 1 ) && ( boardState[ 3 ] == 0 ) && ( boardState[ 5 ] == 0 )
			&& ( boardState[ 6 ] == 0 ) && ( boardState[ 7 ] == 0 ) && ( boardState[ 8 ] == 0 ) ) {
		myMove = 6;
	}

	else if ( ( boardState[ 4 ] == -1 ) && ( boardState[ 0 ] == 0 ) && ( boardState[ 1 ] == 0 )
			&& ( boardState[ 2 ] == 0 ) && ( boardState[ 3 ] == 0 ) && ( boardState[ 5 ] == 0 )
			&& ( boardState[ 6 ] == 1 ) && ( boardState[ 7 ] == 0 ) && ( boardState[ 8 ] == 0 ) ) {
		myMove = 2;
	}

	else if ( ( boardState[ 4 ] == -1 ) && ( boardState[ 0 ] == 0 ) && ( boardState[ 1 ] == 0 )
			&& ( boardState[ 2 ] == 0 ) && ( boardState[ 3 ] == 0 ) && ( boardState[ 5 ] == 0 )
			&& ( boardState[ 6 ] == 0 ) && ( boardState[ 7 ] == 0 ) && ( boardState[ 8 ] == 1 ) ) {
		myMove = 0;
	}
	else if ( boardState[ 4 ] == 0 ) { //play in center if it is open
		myMove = 4;
	}

	/*
	 * else if ( (boardState[0] == 0) && (boardState[4] != 0) && ((boardState[1] == -1) ||
	 * (boardState[3] == -1)) ) { myMove = 0; }
	 * 
	 * else if ( (boardState[8] == 0) && (boardState[4] != 0) && ((boardState[5] == -1) ||
	 * (boardState[7] == -1)) ) { myMove = 8; }
	 */
	else if ( boardState[ 0 ] == 0 || boardState[ 2 ] == 0 || boardState[ 6 ] == 0
			|| boardState[ 8 ] == 0 ) {
		randMoveIdx = rand.nextInt( 4 );
		switch ( randMoveIdx ) {
			case 0:
				myMove = 0;
				break;
			case 1:
				myMove = 2;
				break;
			case 2:
				myMove = 6;
				break;
			case 3:
				myMove = 8;
				break;
		}
	}

	else {
		myMove = rand.nextInt( 9 );
	}

	return myMove;
}

/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties props ) throws Exception {
	Randomizer randomizer = (Randomizer) props.singletonObjectProperty( Randomizer.class );
	rand = randomizer.getRand();
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
