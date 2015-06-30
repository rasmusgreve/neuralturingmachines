/*
 * Copyright (C) 2004 Derek James and Philip Tucker This file is part of ANJI (Another NEAT Java
 * Implementation). ANJI is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA Created on Mar 16, 2004 by Philip Tucker
 */
package com.anji.tournament;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.anji.util.Configurable;
import com.anji.util.Properties;

/**
 * Abstract class that manages a list of <code>Player</code> contestants, has them play each
 * other in matches, and packages the results.
 * 
 * @author Philip Tucker
 */
public abstract class SimpleTournament implements Tournament, Configurable {

/**
 * component game
 */
public final static String COMPONENT_GAME_KEY = "component";

private Game game;

/**
 * Pair of <code>PlayerAndResults</code> for each game.
 * 
 * @author Philip Tucker
 */
protected class PlayerPair {

/**
 * main contestant
 */
public PlayerResults contestant;

/**
 * opponent
 */
public PlayerResults opponent;

/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	return new StringBuffer().append( contestant.getPlayer().getPlayerId() ).append( " vs " )
			.append( opponent.getPlayer().getPlayerId() ).toString();
}
}

private List contestantResults = new ArrayList();

private ScoringWeights scoringWeights;

/**
 * @param aGame
 * @param aWeights
 */
protected SimpleTournament( Game aGame, ScoringWeights aWeights ) {
	game = aGame;
	scoringWeights = aWeights;
}

/**
 * default ctor; should be followwed by <code>init()</code>
 */
public SimpleTournament() {
	// no-op
}

/**
 * @param props
 * @throws Exception
 */
public void init( Properties props ) throws Exception {
	scoringWeights = (ScoringWeights) props.singletonObjectProperty( ScoringWeights.class );
	String componentName = props.getProperty( COMPONENT_GAME_KEY );
	game = (Game) props.newObjectProperty( componentName );
}

/**
 * @see com.anji.tournament.Tournament#clearContestants()
 */
public void clearContestants() {
	contestantResults.clear();
}

/**
 * @param player <code>Player</code> to be added.
 */
public void addContestant( Player player ) {
	contestantResults.add( new PlayerResults( player, scoringWeights ) );
}

/**
 * Returns all players added via <code>addContestant()</code> thus far and associated results.
 * @return <code>List</code> contains <code>PlayerAndResults</code> objects
 */
protected List getResults() {
	return contestantResults;
}

/**
 * @see Tournament#playTournament()
 */
public List playTournament() {
	startTournament();
	while ( hasNextPlayerPair() )
		playMatch( nextPlayerPair() );
	endTournament();

	return newTournamentResults();
}

private List newTournamentResults() {
	List tournamentResults = new ArrayList();
	Collections.sort( contestantResults, PlayerResultsScoreComparator.getDescendingInstance() );
	Iterator it = contestantResults.iterator();
	int rank = 1;
	while ( it.hasNext() ) {
		PlayerResults pr = (PlayerResults) it.next();
		tournamentResults
				.add( new TournamentPlayerResults( pr, rank++, Math.round( pr.getScore() ) ) );
	}
	return tournamentResults;
}

/**
 * @param pair
 */
protected void playMatch( PlayerPair pair ) {
	game.play( pair.contestant, pair.opponent );
}

/**
 * restart tournament
 */
protected abstract void startTournament();

/**
 * end tournament
 */
protected abstract void endTournament();

/**
 * @return next pair of players
 */
protected abstract PlayerPair nextPlayerPair();

/**
 * @return boolean true if player pairs are stil available
 */
protected abstract boolean hasNextPlayerPair();

/**
 * @return scoring weights
 */
protected ScoringWeights getScoringWeights() {
	return scoringWeights;
}

/**
 * @return game object
 */
protected Game getGame() {
	return game;
}
}
