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
 * Created on Jun 4, 2005 by Philip Tucker
 */
package com.anji.tournament.test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.anji.tournament.Game;
import com.anji.tournament.GameConfiguration;
import com.anji.tournament.GameResults;
import com.anji.tournament.Player;
import com.anji.tournament.PlayerResults;
import com.anji.tournament.ScoringWeights;

/**
 * @author Philip Tucker
 */
public class TestGame implements Game {

private Class theRequiredPlayerClass = Player.class;

private int gamesPlayed = 0;

private boolean allowTies = false;

private Set players = new HashSet();

private GameConfiguration config = GameConfiguration.DEFAULT;

/**
 * ctor
 * @param anAllowTies
 */
public TestGame( boolean anAllowTies ) {
	allowTies = anAllowTies;
}

/**
 * @return number game splayed with this object
 */
public int getGamesPlayed() {
	return gamesPlayed;
}

private void play( Player p ) {
	if ( config.doResetPlayers() )
		p.reset();
	if ( p instanceof TestPlayer ) {
		TestPlayer tp = (TestPlayer) p;
		tp.play();
	}
}

/**
 * @see com.anji.tournament.Game#play(com.anji.tournament.PlayerResults,
 * com.anji.tournament.PlayerResults)
 */
public GameResults play( PlayerResults contestant, PlayerResults opponent ) {
	++gamesPlayed;

	// validate players and play game
	Player p1 = contestant.getPlayer();
	Player p2 = opponent.getPlayer();
	if ( ( ( theRequiredPlayerClass.isAssignableFrom( p1.getClass() ) ) && ( theRequiredPlayerClass
			.isAssignableFrom( p2.getClass() ) ) ) == false )
		throw new IllegalArgumentException( "requires players to be " + theRequiredPlayerClass );
	play( p1 );
	play( p2 );

	// random result
	Random rand = new Random();
	int randInt = 0;
	if ( allowTies )
		randInt = ( rand.nextInt( 3 ) - 1 );
	else
		randInt = rand.nextBoolean() ? 1 : -1;

	// increment results
	GameResults gameResults = new GameResults();
	switch ( randInt ) {
		case -1:
			gameResults.incrementPlayer1Losses( 1 );
			break;
		case 0:
			gameResults.incrementTies( 1 );
			break;
		case 1:
			gameResults.incrementPlayer1Wins( 1 );
			break;
		default:
			throw new IllegalStateException( "invalid result: " + randInt );
	}
	contestant.getResults().increment( gameResults.getPlayer1Stats() );
	opponent.getResults().increment( gameResults.getPlayer2Stats() );

	return gameResults;
}

/**
 * @see com.anji.tournament.Game#requiredPlayerClass()
 */
public Class requiredPlayerClass() {
	return theRequiredPlayerClass;
}

/**
 * @see com.anji.tournament.Game#getMaxScore(com.anji.tournament.ScoringWeights)
 */
public int getMaxScore( ScoringWeights aWeights ) {
	return aWeights.getWinValue();
}

/**
 * @see com.anji.tournament.Game#getMinScore(com.anji.tournament.ScoringWeights)
 */
public int getMinScore( ScoringWeights aWeights ) {
	return aWeights.getLossValue();
}

/**
 * reset games played and players
 */
public void resetGamesPlayed() {
	gamesPlayed = 0;
}

/**
 * @param aTheRequiredPlayerClass
 */
public void setRequiredPlayerClass( Class aTheRequiredPlayerClass ) {
	theRequiredPlayerClass = aTheRequiredPlayerClass;
}

/**
 * @param aConfig
 */
public void setConfig( GameConfiguration aConfig ) {
	config = aConfig;
}
}
