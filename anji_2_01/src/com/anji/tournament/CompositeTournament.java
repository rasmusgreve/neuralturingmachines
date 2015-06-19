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
 * Created on May 30, 2005 by Philip Tucker
 */
package com.anji.tournament;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.anji.util.Configurable;
import com.anji.util.Properties;

/**
 * @author Philip Tucker
 */
public class CompositeTournament implements Tournament, Configurable {

private static final String SUB_TOURNAMENTS_KEY = "composite.subtournaments";

private List subTourneys = new ArrayList();

private int minScore = 0;

private int maxScore = 0;

/**
 * default ctor; <code>init()</code> should be called before using this object
 */
public CompositeTournament() {
	super();
}

/**
 * ctor
 * @param aSubTourneys
 */
public CompositeTournament( List aSubTourneys ) {
	this();
	init( aSubTourneys );
}

/**
 * @see com.anji.tournament.Tournament#clearContestants()
 */
public void clearContestants() {
	Iterator it = subTourneys.iterator();
	while ( it.hasNext() ) {
		Tournament t = (Tournament) it.next();
		t.clearContestants();
	}
}

/**
 * @see com.anji.tournament.Tournament#addContestant(com.anji.tournament.Player)
 */
public void addContestant( Player aContestant ) {
	Iterator it = subTourneys.iterator();
	while ( it.hasNext() ) {
		Tournament t = (Tournament) it.next();
		t.addContestant( aContestant );
	}
}

/**
 * @see com.anji.tournament.Tournament#playTournament()
 */
public List playTournament() {
	Map playerResults = new HashMap();
	Iterator tourneyIter = subTourneys.iterator();

	// get results for first tournament
	if ( tourneyIter.hasNext() ) {
		Tournament t = (Tournament) tourneyIter.next();
		List results = t.playTournament();
		Iterator resultsIter = results.iterator();
		while ( resultsIter.hasNext() ) {
			TournamentPlayerResults firstResults = (TournamentPlayerResults) resultsIter.next();
			playerResults.put( firstResults.getPlayer().getPlayerId(), firstResults );
		}
	}

	// add results from remaining tournaments
	while ( tourneyIter.hasNext() ) {
		Tournament t = (Tournament) tourneyIter.next();
		List results = t.playTournament();
		Iterator resultsIter = results.iterator();
		while ( resultsIter.hasNext() ) {
			TournamentPlayerResults newResults = (TournamentPlayerResults) resultsIter.next();
			TournamentPlayerResults previousResults = (TournamentPlayerResults) playerResults
					.get( newResults.getPlayer().getPlayerId() );
			previousResults.getResults().increment( newResults.getResults() );
		}
	}

	// gather and sort results
	List tournamentResults = new ArrayList( playerResults.values() );
	Collections.sort( tournamentResults, PlayerResultsScoreComparator
			.getDescendingTournamentInstance() );
	return tournamentResults;
}

/**
 * @see com.anji.tournament.Tournament#getMaxScore()
 */
public int getMaxScore() {
	return maxScore;
}

/**
 * @see com.anji.tournament.Tournament#getMinScore()
 */
public int getMinScore() {
	return minScore;
}

/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties props ) throws Exception {
	init( props.newObjectListProperty( SUB_TOURNAMENTS_KEY ) );
}

private void init( List aSubTourneys ) {
	subTourneys = aSubTourneys;
	minScore = 0;
	maxScore = 0;
	Iterator it = subTourneys.iterator();
	while ( it.hasNext() ) {
		Tournament t = (Tournament) it.next();
		minScore += t.getMinScore();
		maxScore += t.getMaxScore();
	}
}

/**
 * return all sub-tournaments
 * @return <code>List</code> contains <code>Tournament</code> objects
 */
public List getSubTournaments() {
	return subTourneys;
}

}
