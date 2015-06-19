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
public class SingleEliminationTournament implements Tournament, Configurable {

// private final static Logger logger = Logger.getLogger( SingleEliminationTournament.class );

private List contestants = new ArrayList();

private ScoringWeights scoringWeights;

private Random rand;

private Game game;

/**
 * default ctor; <code>init()</code> should be called before using this object
 */
public SingleEliminationTournament() {
	super();
}

/**
 * ctor Note: this object overrides <code>aMatch</code> countOpponentResults to
 * <code>true</code>
 * @param aGame
 * @param aWeights
 * @param aRand
 */
public SingleEliminationTournament( Game aGame, ScoringWeights aWeights, Random aRand ) {
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
	Bracket bracket = new Bracket( "single", rand, contestants, game );
	List tourneyResults = new ArrayList();
	int rank = contestants.size();
	int roundScore = 0;

	while ( bracket.size() > 1 ) {
		List losers = bracket.playBracket();
		Collections.sort( losers, PlayerResultsScoreComparator.getAscendingInstance() );

		// this variable keeps track of the tie breaker bonus given each contestant
		int tieBreakerBonus = 0;

		Iterator it = losers.iterator();
		while ( it.hasNext() ) {
			PlayerResults pr = (PlayerResults) it.next();
			tourneyResults.add( 0, new TournamentPlayerResults( pr, rank--, roundScore
					+ ( tieBreakerBonus++ ) ) );
		}

		// next round worth twice as many points
		roundScore = ( roundScore == 0 ) ? contestants.size() : ( roundScore * 2 );
	}

	// add champ
	PlayerResults pr = (PlayerResults) bracket.getContestants().get( 0 );
	tourneyResults.add( 0, new TournamentPlayerResults( pr, rank--, roundScore ) );

	return tourneyResults;
}

/**
 * This isn't precise, but should be greater than or equal to the number of rounds.
 * @return number of rounds in tournament; the extra game played at the end if the winners
 * bracket champ loses to the losers baracket champ is not considered a separate round
 */
public int getMaxRounds() {
	int size = contestants.size();
	if ( size < 2 )
		return 0;

	// log base 2 of population size
	double d = Math.log( size ) / Math.log( 2.0 );
	return (int) Math.ceil( d );
}

/**
 * Sum of round scores, which start at <code>contestants.size()</code> and double each round
 * @see com.anji.tournament.Tournament#getMaxScore()
 */
public int getMaxScore() {
	int size = contestants.size();
	if ( size < 2 )
		return 0;

	return ( (int) ( Math.pow( 2, getMaxRounds() - 1 ) ) ) * size;
}

/**
 * @see com.anji.tournament.Tournament#getMinScore()
 */
public int getMinScore() {
	// player who loses first round and has lowest score
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
