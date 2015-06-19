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
 * Created on Jul 23, 2005 by Philip Tucker
 */
package com.anji.tournament;

/**
 * @author Philip Tucker
 */
public class GameResults {

/**
 * results for contestant, or player 1
 */
private PlayerStats player1Stats = new PlayerStats();

/**
 * results for opponent, or player 2
 */
private PlayerStats player2Stats = new PlayerStats();

/**
 * default ctor
 */
public GameResults() {
	super();
}

/**
 * @param count # wins to add to player 1, and losses for player 2
 */
public void incrementPlayer1Wins( int count ) {
	player1Stats.incrementWins( count );
	player2Stats.incrementLosses( count );
}

/**
 * @param count # losses to add to player 1, and wins for player 2
 */
public void incrementPlayer1Losses( int count ) {
	player1Stats.incrementLosses( count );
	player2Stats.incrementWins( count );
}

/**
 * @param count # ties to add to player 1 and player 2
 */
public void incrementTies( int count ) {
	player1Stats.incrementTies( count );
	player2Stats.incrementTies( count );
}

/**
 * @param player1Points new points for player 1
 * @param player2Points new points for player 2
 */
public void incrementRawScore( int player1Points, int player2Points ) {
	player1Stats.incrementRawScore( player1Points );
	player2Stats.incrementRawScore( player2Points );
}

/**
 * increment player stats based on new results
 * @param newResults
 */
public void increment( GameResults newResults ) {
	player1Stats.increment( newResults.player1Stats );
	player2Stats.increment( newResults.player2Stats );
}

/**
 * increment player stats based on new results; swap player one and player two stats
 * @param newResults
 */
public void incrementSwapped( GameResults newResults ) {
	player1Stats.increment( newResults.player2Stats );
	player2Stats.increment( newResults.player1Stats );
}

/**
 * @return player 1 stats
 */
public PlayerStats getPlayer1Stats() {
	return player1Stats;
}

/**
 * @return player 2 stats
 */
public PlayerStats getPlayer2Stats() {
	return player2Stats;
}

/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	return player1Stats.toString() + " vs " + player2Stats.getRawScore();
}
}
