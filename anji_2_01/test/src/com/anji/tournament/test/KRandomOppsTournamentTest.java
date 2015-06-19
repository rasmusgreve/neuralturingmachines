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
import com.anji.roshambo.RoshamboGame;
import com.anji.roshambo.RotatingPlayer;
import com.anji.roshambo.Tris3Player;
import com.anji.roshambo.WizardexpPlayer;
import com.anji.tournament.Game;
import com.anji.tournament.IteratedGame;
import com.anji.tournament.KRandomOppsTournament;
import com.anji.tournament.Player;
import com.anji.tournament.ScoringWeights;
import com.anji.tournament.Tournament;
import com.anji.tournament.TournamentPlayerResults;

/**
 * @author Philip Tucker
 */
public class KRandomOppsTournamentTest extends TestCase {

/**
 *  
 */
public KRandomOppsTournamentTest() {
	this( KRandomOppsTournamentTest.class.toString() );
}

/**
 * @param arg0
 */
public KRandomOppsTournamentTest( String arg0 ) {
	super( arg0 );
}

private static void addPlayer( Player p, Tournament t, Set s ) {
	t.addContestant( p );
	s.add( p );
}

/**
 * @throws Exception
 */
public void testIt() throws Exception {
	int trialsPerGame = 100;
	int gamesPerMatch = 5;
	int k = 4;
	Game roshambo = new IteratedGame( new RoshamboGame(), trialsPerGame );
	ScoringWeights weights = new ScoringWeights( 1, -3, 0, 0 );
	IteratedGame match = new IteratedGame( roshambo, gamesPerMatch );
	KRandomOppsTournament tourney = new KRandomOppsTournament( match, k, weights, new Random() );

	assertEquals( "wrong max score", weights.getWinValue() * trialsPerGame * gamesPerMatch * k,
			tourney.getMaxScore() );
	assertEquals( "wrong min score", weights.getLossValue() * trialsPerGame * gamesPerMatch * k,
			tourney.getMinScore() );

	HashSet players = new HashSet();
	addPlayer( new CopyingPlayer(), tourney, players );
	addPlayer( new DeanPlayer(), tourney, players );
	addPlayer( new EnigmaPlayer(), tourney, players );
	addPlayer( new GnobotPlayer(), tourney, players );
	addPlayer( new IocainePlayer(), tourney, players );
	addPlayer( new JustRockPlayer(), tourney, players );
	addPlayer( new MarshalbotPlayer(), tourney, players );
	addPlayer( new MohammedkaaschPlayer(), tourney, players );
	addPlayer( new Muto5Player(), tourney, players );
	addPlayer( new RandomPlayer(), tourney, players );
	addPlayer( new RotatingPlayer(), tourney, players );
	addPlayer( new Tris3Player(), tourney, players );
	addPlayer( new WizardexpPlayer(), tourney, players );

	// play tournament
	List tourneyContestantsAndResults = tourney.playTournament();
	assertEquals( "wrong # players", 13, tourneyContestantsAndResults.size() );

	// for every win there should be a loss
	int totalWins = 0;
	int totalLosses = 0;

	// keep track of players, make sure there is 1:1 mapping with players we added
	Collection tourneySubjects = new ArrayList();

	// make sure player results sorted in descending order of score
	float prevScore = Float.MAX_VALUE;

	// loop through results
	Iterator it = tourneyContestantsAndResults.iterator();
	while ( it.hasNext() ) {
		TournamentPlayerResults par = (TournamentPlayerResults) it.next();
		tourneySubjects.add( par.getPlayer() );
		assertTrue( "results not sorted by score", par.getScore() <= prevScore );
		assertTrue( "score > max", par.getScore() <= tourney.getMaxScore() );
		assertTrue( "score < min", par.getScore() >= tourney.getMinScore() );
		prevScore = par.getScore();
		assertEquals( "wrong score based on weights", ( weights.getWinValue() * par.getResults()
				.getWins() )
				+ ( weights.getLossValue() * par.getResults().getLosses() )
				+ ( weights.getTieValue() * par.getResults().getTies() ), par.getScore(), 0f );
		assertEquals( "wrong number games", trialsPerGame * gamesPerMatch * k, par.getResults()
				.getWins()
				+ par.getResults().getLosses() + par.getResults().getTies() );
		totalWins += par.getResults().getWins();
		totalLosses += par.getResults().getLosses();
	}

	// keep track of players, make sure there is 1:1 mapping with players we added
	assertTrue( "missing subject", tourneySubjects.containsAll( players ) );
	assertTrue( "extra subject", players.containsAll( tourneySubjects ) );
}

}
