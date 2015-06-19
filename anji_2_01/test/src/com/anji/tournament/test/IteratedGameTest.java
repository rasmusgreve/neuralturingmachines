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
 * Created on Jul 19, 2005 by Philip Tucker
 */
package com.anji.tournament.test;

import junit.framework.TestCase;

import com.anji.tournament.GameConfiguration;
import com.anji.tournament.IteratedGame;
import com.anji.tournament.PlayerResults;
import com.anji.tournament.ScoringWeights;

/**
 * @author Philip Tucker
 */
public class IteratedGameTest extends TestCase {

/**
 * ctor
 */
public IteratedGameTest() {
	this( IteratedGameTest.class.toString() );
}

/**
 * ctor
 * @param arg0
 */
public IteratedGameTest( String arg0 ) {
	super( arg0 );
}

/**
 * @throws Exception
 */
public void testReset() throws Exception {
	doTestIt( true, false );
}

/**
 * @throws Exception
 */
public void testNoReset() throws Exception {
	doTestIt( false, false );
}

/**
 * @throws Exception
 */
public void testResetIterated() throws Exception {
	doTestIt( true, true );
}

/**
 * @throws Exception
 */
public void testNoResetIterated() throws Exception {
	doTestIt( false, true );
}

private void doTestIt( boolean doReset, boolean isIteratedPlayers ) throws Exception {
	int iterationCount = 100;

	// set up component game
	TestGame g = new TestGame( true );
	g.setConfig( new GameConfiguration( false, false ) );
	g.setRequiredPlayerClass( TestPlayer.class );
	assertEquals( "wrong required player class", TestPlayer.class, g.requiredPlayerClass() );

	// set up iterated game
	IteratedGame uut = new IteratedGame( g, iterationCount,
			new GameConfiguration( doReset, false ) );
	assertEquals( "wrong required player class", TestPlayer.class, uut.requiredPlayerClass() );

	// set up players and results
	ScoringWeights weights = new ScoringWeights( 4, 0, 1, 0.5f );
	TestIteratedPlayer iteratedContestant = null;
	TestIteratedPlayer iteratedOpponent = null;
	TestPlayer contestant = new TestPlayer( "testPlayer1" );
	TestPlayer opponent = new TestPlayer( "testPlayer2" );
	if ( isIteratedPlayers ) {
		iteratedContestant = new TestIteratedPlayer( "testIteratedPlayer1" );
		contestant = iteratedContestant;
		iteratedOpponent = new TestIteratedPlayer( "testIteratedPlayer2" );
		opponent = iteratedOpponent;
	}
	PlayerResults contestantResults = new PlayerResults( contestant, weights );
	PlayerResults opponentResults = new PlayerResults( opponent, weights );

	// min/max scores
	assertEquals( "wrong max score", iterationCount * weights.getWinValue(), uut
			.getMaxScore( weights ) );
	assertEquals( "wrong min score", iterationCount * weights.getLossValue(), uut
			.getMinScore( weights ) );

	// play games
	uut.play( contestantResults, opponentResults );
	assertEquals( "wrong # component games played", iterationCount, g.getGamesPlayed() );
	assertEquals( "results 1 don't add up to total", iterationCount, contestantResults
			.getResults().getWins()
			+ contestantResults.getResults().getLosses() + contestantResults.getResults().getTies() );
	int expectedOpponentGamesPlayed = iterationCount;
	assertEquals( "results 2 don't add up to total", expectedOpponentGamesPlayed, opponentResults
			.getResults().getWins()
			+ opponentResults.getResults().getLosses() + opponentResults.getResults().getTies() );
	assertEquals( "wins 2 != losses 1", opponentResults.getResults().getWins(), contestantResults
			.getResults().getLosses() );
	assertEquals( "wins 1 != losses 2", contestantResults.getResults().getWins(), opponentResults
			.getResults().getLosses() );
	int expectedResetCount = doReset ? 1 : 0;
	assertEquals( "wrong reset count 1", expectedResetCount, contestant.getResetCount() );
	assertEquals( "wrong reset count 2", expectedResetCount, opponent.getResetCount() );
	assertEquals( "wrong # games played 1", iterationCount, contestant.getPlayCount() );
	assertEquals( "wrong # games played 2", iterationCount, opponent.getPlayCount() );
	if ( isIteratedPlayers ) {
		int expectedIterationsRemaining = doReset ? 0 : -iterationCount;
		assertEquals( "iterated player 1 has iterations remaining", expectedIterationsRemaining,
				iteratedContestant.getIterationsRemaining() );
		assertEquals( "iterated player 2 has iterations remaining", expectedIterationsRemaining,
				iteratedContestant.getIterationsRemaining() );
	}
}

}
