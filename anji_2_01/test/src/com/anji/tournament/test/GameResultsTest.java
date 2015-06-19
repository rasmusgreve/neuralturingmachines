/*
 * Created on Apr 10, 2005 by Philip Tucker
 */
package com.anji.tournament.test;

import java.util.Random;

import com.anji.tournament.PlayerStats;
import com.anji.tournament.ScoringWeights;

import junit.framework.TestCase;

/**
 * @author Philip Tucker
 */
public class GameResultsTest extends TestCase {

private static final int TEST_W = 5;

private static final int TEST_L = 3;

private static final int TEST_T = 1;

private static final float TEST_RAW = 0.01f;

private final static ScoringWeights TEST_SCORING_WEIGHTS = new ScoringWeights( 10, 0, 4,
		TEST_RAW );

/**
 * test ctors
 * 
 * @throws Exception
 */
public void testCtors() throws Exception {
	PlayerStats uut = new PlayerStats( TEST_W, TEST_L, TEST_T );
	assertEquals( "wrong # wins", TEST_W, uut.getWins() );
	assertEquals( "wrong # losses", TEST_L, uut.getLosses() );
	assertEquals( "wrong # ties", TEST_T, uut.getTies() );
	assertIsWin( uut );

	uut = new PlayerStats();
	assertEquals( "wrong # wins", 0, uut.getWins() );
	assertEquals( "wrong # losses", 0, uut.getLosses() );
	assertEquals( "wrong # ties", 0, uut.getTies() );
	assertIsWin( uut );
}

/**
 * test scores
 */
public void testScores() {
	PlayerStats uut = new PlayerStats( TEST_W, TEST_L, TEST_T );
	assertEquals( "wrong average score", 54f / 9f, TEST_SCORING_WEIGHTS
			.calculateAverageScore( uut ), 0.0f );
	assertEquals( "wrong total score", 54f, TEST_SCORING_WEIGHTS.calculateTotalScore( uut ), 0f );
	assertIsWin( uut );
}

/**
 * test mutators
 */
public void testMutators() {
	PlayerStats uut = new PlayerStats( TEST_W, TEST_L, TEST_T );
	assertIsWin( uut );

	uut.incrementWins( 2 );
	uut.incrementLosses( 4 );
	uut.incrementTies( 1 );
	assertEquals( "wrong # wins", TEST_W + 2, uut.getWins() );
	assertEquals( "wrong # losses", TEST_L + 4, uut.getLosses() );
	assertEquals( "wrong # ties", TEST_T + 1, uut.getTies() );
	assertIsWin( uut );

	uut.increment( new PlayerStats( 3, 2, 4 ) );
	assertEquals( "wrong # wins", TEST_W + 2 + 3, uut.getWins() );
	assertEquals( "wrong # losses", TEST_L + 4 + 2, uut.getLosses() );
	assertEquals( "wrong # ties", TEST_T + 1 + 4, uut.getTies() );
	assertIsWin( uut );
}

private static void assertIsWin( PlayerStats gr ) {
	if ( gr.getWins() > gr.getLosses() )
		assertTrue( "isWin() wrong", gr.isWin( new Random() ) );
	else if ( gr.getWins() < gr.getLosses() )
		assertFalse( "isWin() wrong", gr.isWin( new Random() ) );
}

}
