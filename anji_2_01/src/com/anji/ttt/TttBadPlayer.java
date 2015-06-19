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
 * "Bad" tic-tac-toe subject. See <code>move()</code> for details.
 * 
 * @author Philip Tucker
 */
public class TttBadPlayer implements BoardPlayer, Configurable {

private final static String playerId = "TTT Bad Player";

private Random rand;

/**
 * @see Player#getPlayerId()
 */
public String getPlayerId() {
	return playerId;
}

/**
 * Plays randomly in first open side space. If no sides are open, plays randomly in first open
 * corner. If no corners are open, plays in center.
 * 
 * @see BoardPlayer#move(int[])
 */
public int move( int[] boardState ) {
	int myMove = 0;
	int randMoveIdx = 0;

	if ( ( boardState[ 1 ] == 0 ) || //if sides are open
			( boardState[ 3 ] == 0 ) || //play randomly in open side
			( boardState[ 5 ] == 0 ) || ( boardState[ 7 ] == 0 ) ) {
		randMoveIdx = rand.nextInt( 4 );
		switch ( randMoveIdx ) {
			case 0:
				myMove = 1;
				break;
			case 1:
				myMove = 3;
				break;
			case 2:
				myMove = 5;
				break;
			case 3:
				myMove = 7;
				break;
		}
	}
	else if ( ( boardState[ 0 ] == 0 ) || //if corners are open
			( boardState[ 2 ] == 0 ) || //play randomly in open corner
			( boardState[ 6 ] == 0 ) || ( boardState[ 8 ] == 0 ) ) {
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
	else
		myMove = rand.nextInt( 9 );

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
