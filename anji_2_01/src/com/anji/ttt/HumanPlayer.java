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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.anji.tournament.Player;

/**
 * <code>Player</code> that determines moves by input from stdin.
 * @author Philip Tucker
 */
public class HumanPlayer implements BoardPlayer {

private String playerId = "Human Player";

/**
 * @see Player#getPlayerId()
 */
public String getPlayerId() {
	return playerId;
}

/**
 * Prompts stdout and queries stdin for a move, which shuold be an integer betwene 0 and board
 * size - 1, inclusive.
 * @see BoardPlayer#move(int[])
 */
public int move( int[] boardState ) {
	int myMove;

	System.out.println( "Your move? " );
	BufferedReader console = new BufferedReader( new InputStreamReader( System.in ) );
	String consoleInput = null;
	try {
		consoleInput = console.readLine();
	}
	catch ( IOException e ) {
		consoleInput = "<" + e + ">";
	}
	myMove = Integer.parseInt( consoleInput );
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
