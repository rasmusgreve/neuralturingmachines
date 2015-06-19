/*
 * Created on Apr 11, 2005 by Philip Tucker
 */
package com.anji.tournament.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;

import com.anji.roshambo.CopyingPlayer;
import com.anji.roshambo.DeanPlayer;
import com.anji.roshambo.EnigmaPlayer;
import com.anji.roshambo.GnobotPlayer;
import com.anji.roshambo.IocainePlayer;
import com.anji.roshambo.JustRockPlayer;
import com.anji.roshambo.MarshalbotPlayer;
import com.anji.roshambo.MohammedkaaschPlayer;
import com.anji.roshambo.Muto5Player;
import com.anji.roshambo.RandomPlayer;
import com.anji.roshambo.RoshamboPlayer;
import com.anji.roshambo.RotatingPlayer;
import com.anji.roshambo.Tris3Player;
import com.anji.roshambo.WizardexpPlayer;
import com.anji.tournament.Game;
import com.anji.tournament.IteratedGame;
import com.anji.tournament.Player;
import com.anji.tournament.ScoringWeights;
import com.anji.tournament.SingleEliminationTournament;
import com.anji.tournament.Tournament;
import com.anji.tournament.TournamentPlayerResults;
import com.anji.util.Properties;

/**
 * @author Philip Tucker
 */
public class SingleEliminationTournamentTest extends TestCase {

private final static ScoringWeights SCORING_WEIGHTS = new ScoringWeights( 3, 0, 1, 0 );

/**
 * ctor
 */
public SingleEliminationTournamentTest() {
	this( SingleEliminationTournamentTest.class.toString() );
}

/**
 * ctor
 * @param arg0
 */
public SingleEliminationTournamentTest( String arg0 ) {
	super( arg0 );
}

/**
 * @see junit.framework.TestCase#setUp()
 */
protected void setUp() throws Exception {
	new Properties( "test.properties" );
}

private static SingleEliminationTournament newTournament( int gamesPerMatch, Game g ) {
	IteratedGame m = new IteratedGame( g, gamesPerMatch );
	return new SingleEliminationTournament( m, SCORING_WEIGHTS, new Random() );
}

private static void addPlayer( Player p, Tournament t, Set s ) {
	t.addContestant( p );
	s.add( p );
}

private void assertTourneyMinMax( String assertPrefix, SingleEliminationTournament tourney,
		int expectedMaxRounds, int expectedMaxScore ) {
	assertEquals( assertPrefix + "wrong max rounds", expectedMaxRounds, tourney.getMaxRounds() );
	assertEquals( assertPrefix + "wrong min score", 0, tourney.getMinScore() );
	assertEquals( assertPrefix + "wrong max score", expectedMaxScore, tourney.getMaxScore() );
}

/**
 * test max rounds calculation
 */
public void testMinsAndMaxes() {
	int gamesPerMatch = 5;
	TestGame game = new TestGame( true );
	game.setRequiredPlayerClass( RoshamboPlayer.class );
	SingleEliminationTournament uut = newTournament( gamesPerMatch, game );

	// 0 contestants
	assertTourneyMinMax( "0 contestants: ", uut, 0, 0 );

	// 1 contestants
	uut.addContestant( new CopyingPlayer() );
	assertTourneyMinMax( "1 contestants: ", uut, 0, 0 );

	// 2 contestants
	uut.addContestant( new DeanPlayer() );
	assertTourneyMinMax( "2 contestants: ", uut, 1, 2 );

	// 3 contestants
	uut.addContestant( new EnigmaPlayer() );
	assertTourneyMinMax( "3 contestants: ", uut, 2, 6 );

	// 4 contestants
	uut.addContestant( new GnobotPlayer() );
	assertTourneyMinMax( "4 contestants: ", uut, 2, 8 );

	// 5 contestants
	uut.addContestant( new IocainePlayer() );
	assertTourneyMinMax( "5 contestants: ", uut, 3, 20 );

	// 6 contestants
	uut.addContestant( new JustRockPlayer() );
	assertTourneyMinMax( "6 contestants: ", uut, 3, 24 );

	// 7 contestants
	uut.addContestant( new MarshalbotPlayer() );
	assertTourneyMinMax( "7 contestants: ", uut, 3, 28 );

	// 8 contestants
	uut.addContestant( new MohammedkaaschPlayer() );
	assertTourneyMinMax( "8 contestants: ", uut, 3, 32 );

	// 9 contestants
	uut.addContestant( new Muto5Player() );
	assertTourneyMinMax( "9 contestants: ", uut, 4, 72 );

	// 10 contestants
	uut.addContestant( new RandomPlayer() );
	assertTourneyMinMax( "10 contestants: ", uut, 4, 80 );

	// 11 contestants
	uut.addContestant( new RotatingPlayer() );
	assertTourneyMinMax( "11 contestants: ", uut, 4, 88 );

	// 12 contestants
	uut.addContestant( new Tris3Player() );
	assertTourneyMinMax( "12 contestants: ", uut, 4, 96 );

	// 13 contestants
	uut.addContestant( new WizardexpPlayer() );
	assertTourneyMinMax( "13 contestants: ", uut, 4, 104 );
}

/**
 * basic test
 * @throws Exception
 */
public void testTourney() throws Exception {
	doTestTourney( false );
}

/**
 * test w/ single game per match
 * @throws Exception
 */
public void testTourneySingleGamePerMatch() throws Exception {
	doTestTourney( true );
}

private void doTestTourney( boolean singleGamePerMatch ) throws Exception {
	int gamesPerMatch = ( singleGamePerMatch ) ? 1 : 5;
	TestGame game = new TestGame( !singleGamePerMatch );
	game.setRequiredPlayerClass( RoshamboPlayer.class );
	SingleEliminationTournament uut = newTournament( gamesPerMatch, game );

	assertEquals( "wrong max score", 0, uut.getMaxScore() );
	assertEquals( "wrong min score", 0, uut.getMinScore() );

	HashSet players = new HashSet();
	addPlayer( new CopyingPlayer(), uut, players );
	addPlayer( new DeanPlayer(), uut, players );
	addPlayer( new EnigmaPlayer(), uut, players );
	addPlayer( new GnobotPlayer(), uut, players );
	addPlayer( new IocainePlayer(), uut, players );
	addPlayer( new JustRockPlayer(), uut, players );
	addPlayer( new MarshalbotPlayer(), uut, players );
	addPlayer( new MohammedkaaschPlayer(), uut, players );
	addPlayer( new Muto5Player(), uut, players );
	addPlayer( new RandomPlayer(), uut, players );
	addPlayer( new RotatingPlayer(), uut, players );
	addPlayer( new Tris3Player(), uut, players );
	addPlayer( new WizardexpPlayer(), uut, players );
	assertTourneyMinMax( "13 contestants: ", uut, 4, 104 );

	// play tournament
	List tourneyContestantsAndResults = uut.playTournament();
	assertEquals( "wrong # players", 13, tourneyContestantsAndResults.size() );

	int totalWins = 0;
	int totalLosses = 0;
	int totalTies = 0;

	// keep track of players, make sure there is 1:1 mapping with players we added
	Collection tourneySubjects = new ArrayList();

	// loop through results
	int champCount = 0;
	TournamentPlayerResults champ = null;
	TournamentPlayerResults absoluteLoser = null;
	int rank = 1;
	int prevTourneyScore = Integer.MAX_VALUE;
	Iterator it = tourneyContestantsAndResults.iterator();
	while ( it.hasNext() ) {
		TournamentPlayerResults tpr = (TournamentPlayerResults) it.next();
		tourneySubjects.add( tpr.getPlayer() );

		// save champ and absoulte loser
		if ( rank == 1 )
			champ = tpr;
		else if ( rank == tourneyContestantsAndResults.size() )
			absoluteLoser = tpr;

		// check sort order of rank and tourney score
		assertEquals( "wrong rank", rank++, tpr.getRank() );
		assertTrue( "results not sorted according to tourney score",
				tpr.getTournamentScore() < prevTourneyScore );

		// bounds check scores
		assertTrue( "tourney score " + tpr.getTournamentScore() + " > max " + uut.getMaxScore(),
				tpr.getTournamentScore() <= uut.getMaxScore() );
		assertTrue( "tourney score " + tpr.getTournamentScore() + " < min " + uut.getMinScore(),
				tpr.getTournamentScore() >= uut.getMinScore() );
		int max = ( game.getMaxScore( SCORING_WEIGHTS ) * gamesPerMatch * uut.getMaxRounds() );
		assertTrue( "score " + tpr.getScore() + " > max " + max, tpr.getScore() <= max );
		int min = ( game.getMinScore( SCORING_WEIGHTS ) * gamesPerMatch );
		assertTrue( "score " + tpr.getScore() + " < min " + min, tpr.getScore() >= min );
		assertEquals( "wrong score based on weights", ( SCORING_WEIGHTS.getWinValue() * tpr
				.getResults().getWins() )
				+ ( SCORING_WEIGHTS.getLossValue() * tpr.getResults().getLosses() )
				+ ( SCORING_WEIGHTS.getTieValue() * tpr.getResults().getTies() ), tpr.getScore(), 0f );
		if ( singleGamePerMatch ) {
			assertTrue( "more than 1 loss", tpr.getResults().getLosses() <= 1 );
			if ( tpr.getResults().getLosses() < 1 )
				++champCount;
		}

		prevTourneyScore = tpr.getTournamentScore();
		totalWins += tpr.getResults().getWins();
		totalLosses += tpr.getResults().getLosses();
		totalTies += tpr.getResults().getTies();
	}

	// check # games played, champs, and absolute loser
	assertEquals( "games played out of sync with results", totalWins + totalTies + totalLosses,
			game.getGamesPlayed() * 2 );
	assertEquals( "wrong # games played", 12 * gamesPerMatch, game.getGamesPlayed() );
	if ( singleGamePerMatch ) {
		assertTrue( "champ had a loss", champ.getResults().getLosses() == 0 );
		assertEquals( "too many champs (players w/ < 1 loss)", 1, champCount );
	}
	assertEquals( "champ rank wrong", 1, champ.getRank() );
	assertEquals( "champ score wrong", uut.getMaxScore(), champ.getTournamentScore() );
	assertEquals( "absolute loser rank wrong", tourneyContestantsAndResults.size(), absoluteLoser
			.getRank() );
	assertEquals( "absolute loser score wrong", uut.getMinScore(), absoluteLoser
			.getTournamentScore() );

	// keep track of players, make sure there is 1:1 mapping with players we added
	assertTrue( "missing contestant", tourneySubjects.containsAll( players ) );
	assertTrue( "extra contestant", players.containsAll( tourneySubjects ) );

	// wins == losses
	assertEquals( "wins != losses", totalWins, totalLosses );
}
}
