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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.anji.util.Configurable;
import com.anji.util.Properties;
import com.anji.util.Randomizer;

/**
 * Each contestant plays 1 game against each opponent.
 * @author Philip Tucker
 */
public class DirectTournament extends SimpleTournament implements Configurable {

private Logger logger = Logger.getLogger( DirectTournament.class );

private static final String OPPONENTS_KEY = "direct.opponents";

private List opponentsResults = new ArrayList();

private PlayerPair currentPlayerPair;

private Iterator playerIter;

private Iterator opponentResultsIter;

private Random rand;

/**
 * @param aGame
 * @param aWeights
 * @param aRand used to shuffle order of opponents
 */
public DirectTournament( Game aGame, ScoringWeights aWeights, Random aRand ) {
	super( aGame, aWeights );
	rand = aRand;
}

/**
 * default ctor; should be followed by <code>init()</code>
 */
public DirectTournament() {
	// no-op
}

/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties props ) throws Exception {
	super.init( props );
	Randomizer randomizer = (Randomizer) props.singletonObjectProperty( Randomizer.class );
	rand = randomizer.getRand();
	addOpponents( props.newObjectListProperty( OPPONENTS_KEY ) );
}

/**
 * add multiple opponents
 * @param opponents <code>Collection</code> contains <code>Player</code> objects
 */
public void addOpponents( Collection opponents ) {
	Iterator it = opponents.iterator();
	while ( it.hasNext() )
		addOpponent( (Player) it.next() );
}

/**
 * @param opponent
 */
public void addOpponent( Player opponent ) {
	opponentsResults.add( new PlayerResults( opponent, getScoringWeights() ) );
}

/**
 * @see com.anji.tournament.SimpleTournament#hasNextPlayerPair()
 */
protected boolean hasNextPlayerPair() {
	return ( currentPlayerPair != null )
			&& ( playerIter.hasNext() || opponentResultsIter.hasNext() );
}

/**
 * @see com.anji.tournament.SimpleTournament#nextPlayerPair()
 */
protected PlayerPair nextPlayerPair() {
	if ( currentPlayerPair != null ) {

		// get next subject if we've hit last opponent
		if ( opponentResultsIter.hasNext() == false ) {
			if ( playerIter.hasNext() ) {
				currentPlayerPair.contestant = (PlayerResults) playerIter.next();
				currentPlayerPair.opponent = null;
				
				// shuffle so that we don't play in the same order every time
				Collections.shuffle( opponentsResults, rand );
				opponentResultsIter = opponentsResults.iterator();
			}
			else
				currentPlayerPair = null;
		}

		if ( currentPlayerPair != null ) {
			// get next opponent for current subject
			currentPlayerPair.opponent = (PlayerResults) opponentResultsIter.next();
		}
	}

	return currentPlayerPair;
}

/**
 * @see com.anji.tournament.SimpleTournament#startTournament()
 */
protected void startTournament() {
	playerIter = getResults().iterator();
	Collections.shuffle( opponentsResults, rand );
	opponentResultsIter = opponentsResults.iterator();
	currentPlayerPair = new PlayerPair();

	if ( playerIter.hasNext() )
		currentPlayerPair.contestant = (PlayerResults) playerIter.next();
}

/**
 * @see com.anji.tournament.SimpleTournament#getMaxScore()
 */
public int getMaxScore() {
	return opponentsResults.size() * getGame().getMaxScore( getScoringWeights() );
}

/**
 * @return <code>List</code> contains opponent <code>PlayerResults</code> objects
 */
public List getOpponentsAndResults() {
	return opponentsResults;
}

/**
 * 
 * @see com.anji.tournament.Tournament#getMinScore()
 */
public int getMinScore() {
	return opponentsResults.size() * getGame().getMinScore( getScoringWeights() );
}

/**
 * @see com.anji.tournament.SimpleTournament#endTournament()
 */
protected void endTournament() {
	Iterator it = opponentsResults.iterator();
	int idx = 0;
	while ( it.hasNext() ) {
		PlayerResults results = (PlayerResults) it.next();
		logger.info( "opponent " + ++idx + ": " + results.toString() );
	}
}

}
