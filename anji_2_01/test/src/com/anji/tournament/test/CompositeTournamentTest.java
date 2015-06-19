/*
 * Created on Apr 11, 2005 by Philip Tucker
 */
package com.anji.tournament.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import com.anji.roshambo.CopyingPlayer;
import com.anji.roshambo.DeanPlayer;
import com.anji.roshambo.EnigmaPlayer;
import com.anji.roshambo.GnobotPlayer;
import com.anji.roshambo.IocainePlayer;
import com.anji.roshambo.OneTrackMindPlayer;
import com.anji.roshambo.RoshamboGame;
import com.anji.roshambo.RoshamboPlayer;
import com.anji.tournament.CompositeTournament;
import com.anji.tournament.DirectTournament;
import com.anji.tournament.GameConfiguration;
import com.anji.tournament.IteratedGame;
import com.anji.tournament.KRandomOppsTournament;
import com.anji.tournament.Player;
import com.anji.tournament.PlayerResults;
import com.anji.tournament.ScoringWeights;
import com.anji.tournament.Tournament;
import com.anji.tournament.TournamentPlayerResults;
import com.anji.util.Properties;

/**
 * @author Philip Tucker
 */
public class CompositeTournamentTest extends TestCase {

/**
 * ctor
 */
public CompositeTournamentTest() {
	this( CompositeTournamentTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public CompositeTournamentTest( String name ) {
	super( name );
}

/**
 * test initialization
 * @throws Exception
 */
public void testInit() throws Exception {
	int aWinValue = 3;
	int aLossValue = -1;
	int bWinValue = 6;
	int bLossValue = 1;
	int gamesPerMatch = 3;
	int k = 5;

	// props
	Properties props = new Properties();
	props.put( "composite.subtournaments", "A,B" );
	String matchName = "match";
	props.put( "component", matchName );
	props.put( matchName + Properties.CLASS_SUFFIX, IteratedGame.class.toString().substring(
			"class ".length() ) );
	String gameName = "game";
	props.put( matchName + ".component", gameName );
	props.put( matchName + ".component.count", Integer.toString( gamesPerMatch ) );
	props.put( gameName + Properties.CLASS_SUFFIX, RoshamboGame.class.toString().substring(
			"class ".length() ) );

	// direct sub-tournament props
	props.put( "A.class", DirectTournament.class.toString().substring(
			"class ".length() ) );
	props.put( "A.direct.opponents", "A,B,C" );
	props.put( "A.A.class", CopyingPlayer.class.toString().substring(
			"class ".length() ) );
	props.put( "A.B.class", DeanPlayer.class.toString().substring(
			"class ".length() ) );
	props.put( "A.C.class", OneTrackMindPlayer.class.toString().substring(
			"class ".length() ) );
	props.put( "A.C.onetrackmindplayer.onetrack", "scissors" );
	props.put( "A.win.value", Integer.toString( aWinValue ) );
	props.put( "A.loss.value", Integer.toString( aLossValue ) );
	props.put( "A.tie.value", Integer.toString( 1 ) );

	// k-random opps sub-tournament props
	props.put( "B.class", KRandomOppsTournament.class.toString().substring( "class ".length() ) );
	props.put( "B.krandomopps.k", Integer.toString( k ) );
	props.put( "B.win.value", Integer.toString( bWinValue ) );
	props.put( "B.loss.value", Integer.toString( bLossValue ) );
	props.put( "B.tie.value", Integer.toString( 4 ) );

	// construct tournament
	CompositeTournament uut = (CompositeTournament) props
			.singletonObjectProperty( CompositeTournament.class  );

	// get sub-tournaments
	List subTourneys = uut.getSubTournaments();
	assertEquals( "wrong # sub-tournaments", 2, subTourneys.size() );
	boolean foundTourneyA = false;
	boolean foundTourneyB = false;
	Iterator subTourneyIter = subTourneys.iterator();
	while ( subTourneyIter.hasNext() ) {
		Tournament subTourney = (Tournament) subTourneyIter.next();
		if ( subTourney instanceof DirectTournament ) {
			foundTourneyA = true;
			DirectTournament directSubTourney = (DirectTournament) subTourney;

			// scoring weight for win/loss * games per match * # opponents
			assertEquals( "wrong max score", aWinValue * gamesPerMatch * 3, directSubTourney
					.getMaxScore() );
			assertEquals( "wrong min score", aLossValue * gamesPerMatch * 3, directSubTourney
					.getMinScore() );

			List opponentsAndResults = directSubTourney.getOpponentsAndResults();
			assertEquals( "wrong # opponents", 3, opponentsAndResults.size() );
			boolean foundOpponentA = false;
			boolean foundOpponentB = false;
			boolean foundOpponentC = false;
			Iterator it = opponentsAndResults.iterator();
			while ( it.hasNext() ) {
				PlayerResults par = (PlayerResults) it.next();
				Player p = par.getPlayer();
				if ( p instanceof CopyingPlayer ) {
					assertFalse( "found player A twice", foundOpponentA );
					foundOpponentA = true;
				}
				else if ( p instanceof DeanPlayer ) {
					assertFalse( "found player B twice", foundOpponentB );
					foundOpponentB = true;
				}
				else if ( p instanceof OneTrackMindPlayer ) {
					assertFalse( "found player C twice", foundOpponentC );
					foundOpponentC = true;
					OneTrackMindPlayer otmp = (OneTrackMindPlayer) p;
					assertEquals( "wrong one track", RoshamboPlayer.SCISSORS, otmp.getOneTrack() );
				}
			}
			assertTrue( "no opponent A", foundOpponentA );
			assertTrue( "no opponent B", foundOpponentB );
			assertTrue( "no opponent C", foundOpponentC );
		}
		else if ( subTourney instanceof KRandomOppsTournament ) {
			foundTourneyB = true;
			KRandomOppsTournament krandSubTourney = (KRandomOppsTournament) subTourney;

			// scoring weight for win/loss * games per match * k
			assertEquals( "wrong max score", bWinValue * gamesPerMatch * k, krandSubTourney
					.getMaxScore() );
			assertEquals( "wrong min score", bLossValue * gamesPerMatch * k, krandSubTourney
					.getMinScore() );
		}
		else
			fail( "unexpected sub-tournament: " + subTourney );
	}
	assertTrue( "missing tournament A", foundTourneyA );
	assertTrue( "missing tournament B", foundTourneyB );
}

/**
 * @throws Exception
 */
public void testIt() throws Exception {
	int gamesPerMatch = 5;
	int trialsPerGame = 100;
	ScoringWeights weights = new ScoringWeights( 3, -1, 0, 0 );
	GameConfiguration config = new GameConfiguration( true, false );
	IteratedGame match = new IteratedGame( new IteratedGame( new RoshamboGame( config ),
			trialsPerGame, config ), gamesPerMatch, config );
	List subTourneys = new ArrayList();

	// tourney A
	DirectTournament tourneyA = new DirectTournament( match, weights, new Random() );
	tourneyA.addOpponent( new CopyingPlayer() );
	tourneyA.addOpponent( new DeanPlayer() );
	subTourneys.add( tourneyA );

	// tourney B
	int k = 2;
	KRandomOppsTournament tourneyB = new KRandomOppsTournament( match, k, weights, new Random() );
	subTourneys.add( tourneyB );

	// composite
	CompositeTournament uut = new CompositeTournament( subTourneys );
	assertEquals( "wrong max score", tourneyA.getMaxScore() + tourneyB.getMaxScore(), uut
			.getMaxScore() );
	assertEquals( "wrong min score", tourneyA.getMinScore() + tourneyB.getMinScore(), uut
			.getMinScore() );
	uut.addContestant( new CopyingPlayer() );
	uut.addContestant( new EnigmaPlayer() );
	uut.addContestant( new GnobotPlayer() );
	uut.addContestant( new IocainePlayer() );

	// get sub-tournaments
	subTourneys = uut.getSubTournaments();
	assertEquals( "wrong # sub-tournaments", 2, subTourneys.size() );
	boolean foundTourneyA = false;
	boolean foundTourneyB = false;
	Iterator subTourneyIter = subTourneys.iterator();
	while ( subTourneyIter.hasNext() ) {
		Tournament subTourney = (Tournament) subTourneyIter.next();
		if ( subTourney instanceof DirectTournament ) {
			foundTourneyA = true;
			DirectTournament directSubTourney = (DirectTournament) subTourney;
			assertTrue( "wrong direct tourney", directSubTourney == tourneyA );
		}
		else if ( subTourney instanceof KRandomOppsTournament ) {
			foundTourneyB = true;
			KRandomOppsTournament krandSubTourney = (KRandomOppsTournament) subTourney;
			assertTrue( "wrong k-rand tourney", krandSubTourney == tourneyB );
		}
		else
			fail( "unexpected sub-tournament: " + subTourney );
	}
	assertTrue( "missing tournament A", foundTourneyA );
	assertTrue( "missing tournament B", foundTourneyB );

	// play tournament
	List tourneyContestantResults = uut.playTournament();
	assertEquals( "wrong # players", 4, tourneyContestantResults.size() );

	// track W/L, and make sure player results sorted in descending order of score
	float prevTourneyScore = Float.MAX_VALUE;

	// loop through results
	Iterator it = tourneyContestantResults.iterator();
	while ( it.hasNext() ) {
		TournamentPlayerResults contestantResults = (TournamentPlayerResults) it.next();
		assertTrue( "results not sorted by score",
				contestantResults.getTournamentScore() <= prevTourneyScore );
		assertTrue( "score > max", contestantResults.getScore() <= uut.getMaxScore() );
		assertTrue( "score < min", contestantResults.getScore() >= uut.getMinScore() );
		assertEquals( "wrong score based on weights", ( weights.getWinValue() * contestantResults
				.getResults().getWins() )
				+ ( weights.getLossValue() * contestantResults.getResults().getLosses() )
				+ ( weights.getTieValue() * contestantResults.getResults().getTies() ),
				contestantResults.getScore(), 0f );
		prevTourneyScore = contestantResults.getTournamentScore();

		// trials per game * games per match * ( # direct opponents + k )
		assertEquals( "wrong number games", trialsPerGame * gamesPerMatch * ( 2 + k ),
				contestantResults.getResults().getWins() + contestantResults.getResults().getLosses()
						+ contestantResults.getResults().getTies() );
	}
}
}
