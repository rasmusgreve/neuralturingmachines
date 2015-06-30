/*
 * Copyright (C) 2004 Derek James and Philip Tucker This file is part of ANJI (Another NEAT Java
 * Implementation). ANJI is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA created by Derek James
 */
package com.anji.tournament;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import com.anji.util.Configurable;
import com.anji.util.Properties;
import com.anji.util.Randomizer;

/**
 * Each subject plays at least <i>k </i> games against a random set of opponents. Does not count
 * opponent results (i.e., the k opponents chosen for each contestant each round).
 * 
 * @author Philip Tucker
 */
public class KRandomOppsTournament extends SimpleTournament implements Configurable {

private final static String K_KEY = "krandomopps.k";

private static final int DEFAULT_K = 5;

private int k = DEFAULT_K;

private Set currentPlayerOpponents = new HashSet();

private PlayerPair currentPlayerPair;

private Iterator playerAndResultsIterator;

private Random rand = new Random();

/**
 * default ctor; object must be initialized with <code>init()</code>
 */
public KRandomOppsTournament() {
	// no-op
}

/**
 * Note: this object overrides <code>aMatch</code> countOpponentResults to <code>false</code>
 * @param aGame
 * @param aWeights
 * @param aK number of opponents for each subject
 * @param aRand random number generator
 */
public KRandomOppsTournament( Game aGame, int aK, ScoringWeights aWeights, Random aRand ) {
	super( aGame, aWeights );
	k = aK;
	rand = aRand;
}

/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties props ) throws Exception {
	super.init( props );
	Randomizer randomizer = (Randomizer) props.singletonObjectProperty( Randomizer.class );
	k = props.getIntProperty( K_KEY, DEFAULT_K );
	rand = randomizer.getRand();
}

/**
 * @see com.anji.tournament.SimpleTournament#hasNextPlayerPair()
 */
protected boolean hasNextPlayerPair() {
	return ( currentPlayerPair != null )
			&& ( ( currentPlayerOpponents.size() < k ) || ( playerAndResultsIterator.hasNext() ) );
}

/**
 * @see com.anji.tournament.SimpleTournament#nextPlayerPair()
 */
protected PlayerPair nextPlayerPair() {
	if ( currentPlayerPair != null ) {

		// get next subject if we've hit k opponents
		if ( currentPlayerOpponents.size() >= k ) {
			if ( playerAndResultsIterator.hasNext() ) {
				currentPlayerPair.contestant = (PlayerResults) playerAndResultsIterator.next();
				currentPlayerPair.opponent = null;
				currentPlayerOpponents.clear();
			}
			else
				currentPlayerPair = null;
		}

		if ( currentPlayerPair != null ) {
			// get next random opponent for current subject
			do {
				// we use a clone of the player results so that opponent results are not counted
				PlayerResults playerResults = (PlayerResults) getResults().get(
						rand.nextInt( getResults().size() ) );
				currentPlayerPair.opponent = new PlayerResults( playerResults.getPlayer() );
			} while ( currentPlayerPair.opponent.equals( currentPlayerPair.contestant )
					|| currentPlayerOpponents.contains( currentPlayerPair.opponent ) );
			currentPlayerOpponents.add( currentPlayerPair.opponent );
		}
	}

	return currentPlayerPair;
}

/**
 * @see com.anji.tournament.SimpleTournament#startTournament()
 */
protected void startTournament() {
	int numPlayers = getResults().size();
	if ( k > ( numPlayers - 1 ) )
		throw new IllegalStateException( "not enough players (" + numPlayers
				+ ") for k opponents (" + k + ")" );

	currentPlayerOpponents.clear();
	playerAndResultsIterator = getResults().iterator();
	currentPlayerPair = new PlayerPair();

	if ( playerAndResultsIterator.hasNext() )
		currentPlayerPair.contestant = (PlayerResults) playerAndResultsIterator.next();
}

/**
 * @see com.anji.tournament.SimpleTournament#getMaxScore()
 */
public int getMaxScore() {
	return k * getGame().getMaxScore( getScoringWeights() );
}

/**
 * @see com.anji.tournament.Tournament#getMinScore()
 */
public int getMinScore() {
	return k * getGame().getMinScore( getScoringWeights() );
}

/**
 * @see com.anji.tournament.SimpleTournament#endTournament()
 */
protected void endTournament() {
	// no-op
}

}
