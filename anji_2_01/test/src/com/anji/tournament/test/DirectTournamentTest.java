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
import com.anji.roshambo.OneTrackMindPlayer;
import com.anji.roshambo.RandomPlayer;
import com.anji.roshambo.RoshamboGame;
import com.anji.roshambo.RoshamboPlayer;
import com.anji.roshambo.RotatingPlayer;
import com.anji.roshambo.Tris3Player;
import com.anji.roshambo.WizardexpPlayer;
import com.anji.tournament.DirectTournament;
import com.anji.tournament.GameConfiguration;
import com.anji.tournament.PlayerStats;
import com.anji.tournament.IteratedGame;
import com.anji.tournament.Player;
import com.anji.tournament.PlayerResults;
import com.anji.tournament.ScoringWeights;
import com.anji.tournament.TournamentPlayerResults;
import com.anji.util.Properties;

/**
 * @author Philip Tucker
 */
public class DirectTournamentTest extends TestCase {

/**
 *  
 */
public DirectTournamentTest() {
	this( DirectTournamentTest.class.toString() );
}

/**
 * @param arg0
 */
public DirectTournamentTest( String arg0 ) {
	super( arg0 );
}

private static void addContestant( Player p, DirectTournament t, Set s ) {
	t.addContestant( p );
	s.add( p );
}

private static void addOpponent( Player p, DirectTournament t, Set s ) {
	t.addOpponent( p );
	s.add( p );
}

/**
 * test initialization
 * @throws Exception
 */
public void testInit() throws Exception {
	Properties props = new Properties();
	props.put( "direct.opponents", "A,B,C" );
	props.put( "A.class", CopyingPlayer.class.toString().substring( "class ".length() ) );
	props.put( "B.class", DeanPlayer.class.toString().substring( "class ".length() ) );
	props.put( "C.class", OneTrackMindPlayer.class.toString().substring( "class ".length() ) );
	props.put( "C.onetrackmindplayer.onetrack", "scissors" );
	String gameName = "game";
	props.put( "component", gameName );
	props.put( gameName + Properties.CLASS_SUFFIX, RoshamboGame.class.toString().substring(
			"class ".length() ) );
	DirectTournament uut = (DirectTournament) props.newObjectProperty( DirectTournament.class );
	List opponentsAndResults = uut.getOpponentsAndResults();
	assertEquals( "wrong # opponents", 3, opponentsAndResults.size() );
	boolean foundA = false;
	boolean foundB = false;
	boolean foundC = false;
	Iterator it = opponentsAndResults.iterator();
	while ( it.hasNext() ) {
		PlayerResults par = (PlayerResults) it.next();
		Player p = par.getPlayer();
		if ( p instanceof CopyingPlayer ) {
			assertFalse( "found player A twice", foundA );
			foundA = true;
		}
		else if ( p instanceof DeanPlayer ) {
			assertFalse( "found player B twice", foundB );
			foundB = true;
		}
		else if ( p instanceof OneTrackMindPlayer ) {
			assertFalse( "found player C twice", foundC );
			foundC = true;
			OneTrackMindPlayer otmp = (OneTrackMindPlayer) p;
			assertEquals( "wrong one track", RoshamboPlayer.SCISSORS, otmp.getOneTrack() );
		}
	}
	assertTrue( "no player A", foundA );
	assertTrue( "no player B", foundB );
	assertTrue( "no player C", foundC );
}

/**
 * @throws Exception
 */
public void testIt() throws Exception {
	int gamesPerMatch = 5;
	int trialsPerGame = 100;
	ScoringWeights weights = new ScoringWeights( 3, -1, 0, 0 );
	IteratedGame match = new IteratedGame( new IteratedGame( new RoshamboGame(), trialsPerGame,
			new GameConfiguration( false, false ) ), gamesPerMatch, new GameConfiguration() );
	DirectTournament tourney = new DirectTournament( match, weights, new Random() );
	assertEquals( "wrong max score", 0, tourney.getMaxScore() );
	assertEquals( "wrong min score", 0, tourney.getMinScore() );
	assertEquals( "wrong # opponents", 0, tourney.getOpponentsAndResults().size() );

	// add opponents
	HashSet opponents = new HashSet();
	addOpponent( new CopyingPlayer(), tourney, opponents );
	addOpponent( new DeanPlayer(), tourney, opponents );
	assertEquals( "wrong max score", trialsPerGame * gamesPerMatch * 2 * weights.getWinValue(),
			tourney.getMaxScore() );
	assertEquals( "wrong min score", trialsPerGame * gamesPerMatch * 2 * weights.getLossValue(),
			tourney.getMinScore() );
	assertEquals( "wrong # opponents", 2, tourney.getOpponentsAndResults().size() );

	// opponents collection
	Collection tourneyOpponents = new ArrayList();
	Iterator it = tourney.getOpponentsAndResults().iterator();
	while ( it.hasNext() ) {
		PlayerResults par = (PlayerResults) it.next();
		tourneyOpponents.add( par.getPlayer() );
	}
	assertTrue( "missing opponent", tourneyOpponents.containsAll( opponents ) );
	assertTrue( "extra opponent", opponents.containsAll( tourneyOpponents ) );

	// add contestants
	HashSet players = new HashSet();
	addContestant( new CopyingPlayer(), tourney, players );
	addContestant( new DeanPlayer(), tourney, players );
	addContestant( new EnigmaPlayer(), tourney, players );
	addContestant( new GnobotPlayer(), tourney, players );
	addContestant( new IocainePlayer(), tourney, players );
	addContestant( new JustRockPlayer(), tourney, players );
	addContestant( new MarshalbotPlayer(), tourney, players );
	addContestant( new MohammedkaaschPlayer(), tourney, players );
	addContestant( new Muto5Player(), tourney, players );
	addContestant( new RandomPlayer(), tourney, players );
	addContestant( new RotatingPlayer(), tourney, players );
	addContestant( new Tris3Player(), tourney, players );
	addContestant( new WizardexpPlayer(), tourney, players );

	// play tournament
	List tourneyContestantResults = tourney.playTournament();
	assertEquals( "wrong # players", 13, tourneyContestantResults.size() );

	// for every win there should be a loss
	int totalWins = 0;
	int totalLosses = 0;

	// keep track of players, make sure there is 1:1 mapping with players we added
	Collection tourneyContestants = new ArrayList();

	// make sure player results sorted in descending order of score
	float prevScore = Float.MAX_VALUE;

	// loop through results
	it = tourneyContestantResults.iterator();
	while ( it.hasNext() ) {
		TournamentPlayerResults par = (TournamentPlayerResults) it.next();
		tourneyContestants.add( par.getPlayer() );
		assertTrue( "results not sorted by score", par.getScore() <= prevScore );
		assertTrue( "score > max", par.getScore() <= tourney.getMaxScore() );
		assertTrue( "score < min", par.getScore() >= tourney.getMinScore() );
		assertEquals( "wrong score based on weights", ( weights.getWinValue() * par.getResults()
				.getWins() )
				+ ( weights.getLossValue() * par.getResults().getLosses() )
				+ ( weights.getTieValue() * par.getResults().getTies() ), par.getScore(), 0f );
		prevScore = par.getScore();

		// games per match * # opponents
		assertEquals( "wrong number games", trialsPerGame * gamesPerMatch * 2, par.getResults()
				.getWins()
				+ par.getResults().getLosses() + par.getResults().getTies() );

		totalWins += par.getResults().getWins();
		totalLosses += par.getResults().getLosses();
	}

	// keep track of players, make sure there is 1:1 mapping with players we added
	assertTrue( "missing subject", tourneyContestants.containsAll( players ) );
	assertTrue( "extra subject", players.containsAll( tourneyContestants ) );

	// opponents
	it = tourney.getOpponentsAndResults().iterator();
	while ( it.hasNext() ) {
		PlayerResults par = (PlayerResults) it.next();
		assertTrue( "extra opponent", opponents.contains( par.getPlayer() ) );
		PlayerStats results = par.getResults();
		assertEquals( "wrong number games", trialsPerGame * gamesPerMatch * players.size(), results
				.getWins()
				+ results.getLosses() + results.getTies() );
		totalWins += results.getWins();
		totalLosses += results.getLosses();
	}

	// for every win there should be a loss
	assertEquals( "losses != wins", totalWins, totalLosses );
}
}
