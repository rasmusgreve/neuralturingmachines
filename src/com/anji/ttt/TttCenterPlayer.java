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

/**
 * "Bad" tic-tac-toe subject with a preference for playing in the center. See
 * <code>move()</code> for details.
 * @author Philip Tucker
 */
public class TttCenterPlayer implements BoardPlayer {

private static final String playerId = "TTT Center Player";

private static TttCenterPlayer instance = null;

/**
 * singleton; public for creation via reflection
 */
public TttCenterPlayer() {
	// noop
}

/**
 * @return singleton instance
 */
public static TttCenterPlayer getInstance() {
	if ( instance == null )
		instance = new TttCenterPlayer();
	return instance;
}

/**
 * @see Player#getPlayerId()
 */
public String getPlayerId() {
	return playerId;
}

/**
 * Plays in center if open, otherwise plays randomly.
 * @see BoardPlayer#move(int[])
 */
public int move( int[] boardState ) {
	int myMove;

	if ( boardState[ 4 ] == 0 ) { //play in center if it is open
		myMove = 4;
	}
	else {
		myMove = (int) ( Math.random() * 9 );
	}
	return myMove;
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
