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
 * Created on May 31, 2005 by Philip Tucker
 */
package com.anji.tournament;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.anji.util.Configurable;
import com.anji.util.Properties;
import com.anji.util.Randomizer;

/**
 * Standard double elimination tournament, except players are re-seeded after every round. Also,
 * in final round if losers bracket champ beats winners bracket champ, another game is played to
 * determine champ.
 * @author Philip Tucker
 */
public class DoubleEliminationTournament implements Tournament, Configurable {

// private final static Logger logger = Logger.getLogger( DoubleEliminationTournament.class );

private List contestants = new ArrayList();

private ScoringWeights scoringWeights;

private Random rand;

private Game game;

/**
 * default ctor; <code>init()</code> should be called before using this object
 */
public DoubleEliminationTournament() {
	super();
}

/**
 * ctor Note: this object overrides <code>aMatch</code> countOpponentResults to
 * <code>true</code>
 * @param aGame
 * @param aWeights
 * @param aRand
 */
public DoubleEliminationTournament( Game aGame, ScoringWeights aWeights, Random aRand ) {
	init( aGame, aWeights, aRand );
}

private void init( Game aGame, ScoringWeights aWeights, Random aRand ) {
	if ( aWeights.getLossValue() > 0 ) {
		throw new IllegalArgumentException(
				"can not have positive loss value for tournaments with variable numbers of games" );
	}

	game = aGame;
	scoringWeights = aWeights;
	rand = aRand;
}

/**
 * @see com.anji.tournament.Tournament#clearContestants()
 */
public void clearContestants() {
	contestants.clear();
}

/**
 * @see com.anji.tournament.Tournament#addContestant(com.anji.tournament.Player)
 */
public void addContestant( Player aContestant ) {
	contestants.add( new PlayerResults( aContestant, scoringWeights ) );
}

/**
 * @see com.anji.tournament.Tournament#playTournament()
 */
public List playTournament() {
	// lists all contain PlayerResult objects
	Bracket winnersBracket = new Bracket( "winners", rand, contestants, game );
	Bracket losersBracket = new Bracket( "losers", rand, null, game );
	List tourneyResults = new ArrayList();

	// these variables keep track of the rank and score earned by each contestant each round
	int roundScore = 0;
	int rank = contestants.size();

	// loop while we still have >1 contestant
	while ( ( winnersBracket.size() + losersBracket.size() ) > 1 ) {
		if ( ( winnersBracket.size() == 1 ) && ( losersBracket.size() == 1 ) )
			playChampionshipRound( winnersBracket, losersBracket, rank, roundScore, tourneyResults );
		else {
			// losers bracket
			if ( losersBracket.size() > 1 ) {
				// sort losers in ascending order of score
				List losers = losersBracket.playBracket();
				Collections.sort( losers, PlayerResultsScoreComparator.getAscendingInstance() );

				// this variable keeps track of the tie breaker bonus given each contestant
				int tieBreakerBonus = 0;

				// iterate through losers placing them in results
				Iterator it = losers.iterator();
				while ( it.hasNext() ) {
					PlayerResults pr = (PlayerResults) it.next();
					tourneyResults.add( 0, new TournamentPlayerResults( pr, rank--, roundScore
							+ ( tieBreakerBonus++ ) ) );
				}

				// next round worth twice as many points
				roundScore = ( roundScore == 0 ) ? contestants.size() : ( roundScore * 2 );
			}

			// winners bracket
			if ( winnersBracket.size() > 1 ) {
				List losers = winnersBracket.playBracket();
				// losers go to losers bracket
				losersBracket.addContestants( losers );
			}
		}
	}

	return tourneyResults;
}

private void playChampionshipRound( Bracket winnersBracket, Bracket losersBracket, int rank,
		int roundScore, List tourneyResults ) {
	// championship round
	PlayerResults player1Results = (PlayerResults) winnersBracket.getContestants().get( 0 );
	PlayerResults player2Results = (PlayerResults) losersBracket.getContestants().get( 0 );
	GameResults results = game.play( player1Results, player2Results );
	if ( results.getPlayer1Stats().isWin( rand ) ) {
		// undefeated champ
		TournamentPlayerResults runnerUp = new TournamentPlayerResults( player2Results, rank--,
				roundScore );
		TournamentPlayerResults champ = new TournamentPlayerResults( player1Results, rank--,
				roundScore * 2 );
		tourneyResults.add( 0, runnerUp );
		tourneyResults.add( 0, champ );
	}
	else {
		// both players now have one loss, we play one more game to determine champ
		results = game.play( player1Results, player2Results );
		if ( results.getPlayer1Stats().isWin( rand ) ) {
			TournamentPlayerResults runnerUp = new TournamentPlayerResults( player2Results, rank--,
					roundScore );
			TournamentPlayerResults champ = new TournamentPlayerResults( player1Results, rank--,
					roundScore * 2 );
			tourneyResults.add( 0, runnerUp );
			tourneyResults.add( 0, champ );
		}
		else {
			TournamentPlayerResults runnerUp = new TournamentPlayerResults( player1Results, rank--,
					roundScore );
			TournamentPlayerResults champ = new TournamentPlayerResults( player2Results, rank--,
					roundScore * 2 );
			tourneyResults.add( 0, runnerUp );
			tourneyResults.add( 0, champ );
		}
	}

	winnersBracket.clearContestants();
	losersBracket.clearContestants();
}

/**
 * This isn't precise, but should be greater than or equal to the number of rounds.
 * @return number of rounds in tournament; the extra game played at the end if the winners
 * bracket champ loses to the losers bracket champ is not considered a separate round
 */
public int getMaxRounds() {
	int result = 0;
	int wbCount = contestants.size();
	int lbCount = 0;
	while ( wbCount + lbCount > 1 ) {
		// final round
		if ( ( wbCount == 1 ) && ( lbCount == 1 ) )
			lbCount = 0;
		else {
			// losers bracket
			if ( lbCount > 1 ) {
				int loserCount = ( lbCount / 2 );
				lbCount -= loserCount;
			}
			// winners bracket
			if ( wbCount > 1 ) {
				int loserCount = ( wbCount / 2 );
				wbCount -= loserCount;
				lbCount += loserCount;
			}
		}
		++result;
	}
	return result;
}

/**
 * This isn't precise, but should be greater than or equal to the number of rounds in which
 * players are eliminated from the lsoers bracket.
 * @return number of elimination rounds in tournament
 */
public int getMaxEliminationRounds() {
	int result = 0;
	int wbCount = contestants.size();
	int lbCount = 0;
	while ( wbCount + lbCount > 1 ) {
		// final round
		if ( ( wbCount == 1 ) && ( lbCount == 1 ) ) {
			lbCount = 0;
			++result;
		}
		else {
			// losers bracket
			if ( lbCount > 1 ) {
				int loserCount = ( lbCount / 2 );
				lbCount -= loserCount;
				++result;
			}
			// winners bracket
			if ( wbCount > 1 ) {
				int loserCount = ( wbCount / 2 );
				wbCount -= loserCount;
				lbCount += loserCount;
			}
		}
	}
	return result;
}

/**
 * @see com.anji.tournament.Tournament#getMaxScore()
 */
public int getMaxScore() {
	int size = contestants.size();
	if ( size < 2 )
		return 0;

	return ( (int) ( Math.pow( 2, getMaxEliminationRounds() - 1 ) ) ) * size;
}

/**
 * @see com.anji.tournament.Tournament#getMinScore()
 */
public int getMinScore() {
	return 0;
}

/**
 * Note: this object overrides <code>aMatch</code> countOpponentResults to <code>true</code>
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties props ) throws Exception {
	Randomizer randomizer = (Randomizer) props.singletonObjectProperty( Randomizer.class );
	String componentName = props.getProperty( SimpleTournament.COMPONENT_GAME_KEY );
	Game aGame = (Game) props.newObjectProperty( componentName );
	init( aGame, (ScoringWeights) props.singletonObjectProperty( ScoringWeights.class ),
			randomizer.getRand() );
}

}
