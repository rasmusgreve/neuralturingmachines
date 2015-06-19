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
 * Created on Jul 16, 2005 by Philip Tucker
 */
package com.anji.tournament;

import org.apache.log4j.Logger;

import com.anji.util.Configurable;
import com.anji.util.Properties;

/**
 * This is a game composed of iterating over a component game. This is an abstract object that
 * can represent, for example, a match, set, or game of multiple trials. Every other component
 * game swaps the order of the contestant and opponent (or, player one and player two) to give
 * both players a chance to move first in games where initiative matters.
 * 
 * @author Philip Tucker
 */
public class IteratedGame implements Game, Configurable {

private final static Logger logger = Logger.getLogger( IteratedGame.class );

private final static String COMPONENT_COUNT_KEY = "component.count";

private final static String COMPONENT_KEY = "component";

private GameConfiguration config = GameConfiguration.DEFAULT;

private Game componentGame;

private int iterationCount = 0;

private String name = "iterated-game";

/**
 * default ctor; should call <code>init()</code> after this
 */
public IteratedGame() {
	super();
}

/**
 * @param aComponentGame
 * @param anIterationCount
 */
public IteratedGame( Game aComponentGame, int anIterationCount ) {
	this( aComponentGame, anIterationCount, GameConfiguration.DEFAULT );
}

/**
 * @param aComponentGame
 * @param anIterationCount
 * @param aConfig
 */
public IteratedGame( Game aComponentGame, int anIterationCount, GameConfiguration aConfig ) {
	componentGame = aComponentGame;
	iterationCount = anIterationCount;
	config = aConfig;
}

private void reset( Player p ) {
	if ( p instanceof IteratedPlayer ) {
		IteratedPlayer ip = (IteratedPlayer) p;
		ip.reset( iterationCount );
	}
	else
		p.reset();
}

/**
 * @see com.anji.tournament.Game#play(com.anji.tournament.PlayerResults,
 * com.anji.tournament.PlayerResults)
 */
public GameResults play( PlayerResults contestantResults, PlayerResults opponentResults ) {
	long startMillis = System.currentTimeMillis();
	if ( config.doResetPlayers() ) {
		reset( contestantResults.getPlayer() );
		reset( opponentResults.getPlayer() );
	}

	GameResults results = new GameResults();
	for ( int i = 0; i < iterationCount; ++i ) {
		if ( i % 2 == 0 ) {
			GameResults iterationResults = componentGame.play( contestantResults, opponentResults );
			results.increment( iterationResults );
		}
		else {
			GameResults iterationResults = componentGame.play( opponentResults, contestantResults );
			results.incrementSwapped( iterationResults );
		}
	}

	if ( config.doLogResults() )
		logger.info( new StringBuffer( name ).append( " " ).append( contestantResults.getPlayer() )
				.append( " vs " ).append( opponentResults.getPlayer() ).append( ": " ).append( results )
				.append( ": " ).append( System.currentTimeMillis() - startMillis ).append( " ms" )
				.toString() );

	return results;
}

/**
 * @see com.anji.tournament.Game#requiredPlayerClass()
 */
public Class requiredPlayerClass() {
	return componentGame.requiredPlayerClass();
}

/**
 * @see com.anji.tournament.Game#getMaxScore(com.anji.tournament.ScoringWeights)
 */
public int getMaxScore( ScoringWeights aWeights ) {
	return iterationCount * componentGame.getMaxScore( aWeights );
}

/**
 * @see com.anji.tournament.Game#getMinScore(com.anji.tournament.ScoringWeights)
 */
public int getMinScore( ScoringWeights aWeights ) {
	return iterationCount * componentGame.getMinScore( aWeights );
}

/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties props ) throws Exception {
	iterationCount = props.getIntProperty( COMPONENT_COUNT_KEY );
	String componentName = props.getProperty( COMPONENT_KEY );
	componentGame = (Game) props.newObjectProperty( componentName );
	config = (GameConfiguration) props.singletonObjectProperty( GameConfiguration.class );
	name = props.getName();
}

}
