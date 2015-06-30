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
 * Created on Mar 16, 2004 by Philip Tucker
 */
package com.anji.tournament;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;

import com.anji.tournament.Player;
import com.anji.tournament.Tournament;
import com.anji.util.Configurable;
import com.anji.util.Properties;

/**
 * Fitness function in which chromosomes are evaluated by their phenotypes playing a set of
 * matches. Specific tournaments decide which opponents and how many matches and games are
 * played.
 * 
 * @author Philip Tucker
 */
public class TournamentFitnessFunction implements BulkFitnessFunction, Configurable {

private Logger logger = Logger.getLogger( TournamentFitnessFunction.class );

private final static String TOURNAMENT_CLASS_KEY = "tournament";

private final static String TRANSCRIBER_CLASS_KEY = "tournament.transcriber";

private final static String MAX_FITNESS_KEY = "tournament.fitness.max";

private static final String NONCHROMOSOME_CONTESTANTS_KEY = "tournament.nonchromosome.contestants";

private Map playerToChromosomeMap = new HashMap();

private Tournament tournament;

private PlayerTranscriber transcriber;

private int maxFitness = Integer.MAX_VALUE;

private List nonChromosomeContestants = new ArrayList();

/**
 * ctor - must call <code>init(Properties)</code> after this
 */
public TournamentFitnessFunction() {
	// no-op
}

/**
 * ctor
 * @param aTournament
 * @param aTranscriber
 */
public TournamentFitnessFunction( Tournament aTournament, PlayerTranscriber aTranscriber ) {
	tournament = aTournament;
	transcriber = aTranscriber;
}

/**
 * See <a href=" {@docRoot}/params.htm" target="anji_params">Parameter Details </a> for
 * specific property settings.
 * 
 * @param props
 */
public void init( Properties props ) {
	tournament = (Tournament) props.newObjectProperty( TOURNAMENT_CLASS_KEY );
	transcriber = (PlayerTranscriber) props.newObjectProperty( TRANSCRIBER_CLASS_KEY );
	maxFitness = props.getIntProperty( MAX_FITNESS_KEY, Integer.MAX_VALUE );
	nonChromosomeContestants = props.newObjectListProperty( NONCHROMOSOME_CONTESTANTS_KEY,
			new ArrayList() );
}

/**
 * @see org.jgap.BulkFitnessFunction#evaluate(java.util.List)
 */
public void evaluate( List chroms ) {
	playerToChromosomeMap.clear();
	tournament.clearContestants();

	// add non-chromosome players
	Iterator it = nonChromosomeContestants.iterator();
	while ( it.hasNext() ) {
		Player contestant = (Player) it.next();
		tournament.addContestant( contestant );
	}

	// add chromosome players
	it = chroms.iterator();
	while ( it.hasNext() ) {
		Chromosome chrom = (Chromosome) it.next();
		try {
			Player contestant = transcriber.newPlayer( chrom );
			playerToChromosomeMap.put( contestant, chrom );
			tournament.addContestant( contestant );
		}
		catch ( Throwable th ) {
			logger.warn( "error with chromosome " + chrom, th );
		}
	}

	// tournament
	List tourneyResults = tournament.playTournament();

	// get results and assign fitness
	it = tourneyResults.iterator();
	while ( it.hasNext() ) {
		TournamentPlayerResults tourneyPlayerResults = (TournamentPlayerResults) it.next();

		// adjust results from [minValue ... maxValue] to [0 ... maxFitness]
		float adjustedScore = tourneyPlayerResults.getTournamentScore() - tournament.getMinScore();
		float adjustedMaxScore = tournament.getMaxScore() - tournament.getMinScore();
		int normalizedScore = (int) ( ( adjustedScore / adjustedMaxScore ) * maxFitness );

		// set fitness
		Chromosome c = (Chromosome) playerToChromosomeMap.get( tourneyPlayerResults.getPlayer() );
		if ( c == null )
			// non-chromosome player
			logger.info( tourneyPlayerResults );
		else
			c.setFitnessValue( normalizedScore );
	}
}

/**
 * @see org.jgap.BulkFitnessFunction#getMaxFitnessValue()
 */
public int getMaxFitnessValue() {
	return maxFitness;
}
}
