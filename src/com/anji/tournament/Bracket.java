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
 * Created on Jun 4, 2005 by Philip Tucker
 */
package com.anji.tournament;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

/**
 * @author Philip Tucker
 */
public class Bracket {

private final static Logger logger = Logger.getLogger( Bracket.class );

private Random rand;

private List contestants = new ArrayList();

private Game game;

private String name;

/**
 * Bracket has a list of <code>contestants</code> and a destination for losers. Each round a
 * set of losers is eliminated they are added to <code>loserDestination</code> in reverse
 * order of when they were eliminated. Those eliminated the same round are added in descending
 * order of score.
 * @param aName
 * @param aRand
 * @param aContestants <code>List</code> contains <code>PlayerResult</code> objects; makes a
 * copy of this list internally; this parameter may be null if no contestants are to be added
 * @param aGame
 */
public Bracket( String aName, Random aRand, List aContestants, Game aGame ) {
	super();
	name = aName;
	rand = aRand;
	if ( aContestants != null )
		contestants.addAll( aContestants );
	game = aGame;
}

/**
 * @return number of remainnig contestants yet to be eliminated
 */
public int size() {
	return contestants.size();
}

/**
 * @return unmodifiable list of remaming contestants; <code>List</code> contains
 * <code>PlayerResult</code> objects
 */
public List getContestants() {
	return Collections.unmodifiableList( contestants );
}

/**
 * clear contestants
 */
public void clearContestants() {
	contestants.clear();
}

/**
 * add new contestants to beginning of bracket
 * @param newContestants
 */
public void addContestants( List newContestants ) {
	contestants.addAll( 0, newContestants );
}

/**
 * Playe matches between contestants in bracket designated by <code>contestants</code>.
 * Losers are removed from this bracket and returned.
 * @return losers; <code>List</code> contains <code>PlayerResults</code> objects, sorted in
 * descending order of score
 */
public List playBracket() {
	// seed according to score
	int startingSize = contestants.size();
	Collections.shuffle( contestants, rand );
	Collections.sort( contestants, PlayerResultsScoreComparator.getAscendingInstance() );

	// iterate through players, pairing them up in matches and moving loser to loserDestination
	List losers = new ArrayList();
	Iterator it = contestants.iterator();
	while ( it.hasNext() ) {
		PlayerResults player1Results = (PlayerResults) it.next();
		if ( it.hasNext() ) {
			PlayerResults player2Results = (PlayerResults) it.next();
			GameResults results = game.play( player1Results, player2Results );

			// move loser to loserDestination
			if ( results.getPlayer1Stats().isWin( rand ) )
				losers.add( player2Results );
			else
				losers.add( player1Results );
		}
		else
			logger.warn( "odd number of players in winners bracket, one player gets bye: "
					+ player1Results.getPlayer().getPlayerId() );
	}

	// remove losers from currentBracket, sort them according to score, and add them to
	// loserDestination we insert them at the beginning of loserDestination so they will be seeded
	// ahead of those who have lost in earlier rounds
	contestants.removeAll( losers );
	Collections.sort( losers, PlayerResultsScoreComparator.getDescendingInstance() );
	int endingSize = contestants.size();
	logger.info( name + " bracket: " + startingSize + "->" + endingSize );
	return losers;
}

/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	return contestants.toString();
}

}
