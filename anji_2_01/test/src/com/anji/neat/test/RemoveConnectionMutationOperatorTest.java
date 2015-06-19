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
 * created by Philip Tucker on Mar 31, 2003
 */
package com.anji.neat.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;

import org.jgap.ChromosomeMaterial;
import org.jgap.test.DummyFitnessFunction;
import org.jgap.test.MutationOperatorTest;

import com.anji.neat.ConnectionAllele;
import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.RemoveConnectionMutationOperator;
import com.anji.util.Properties;
import com.anji.util.Reset;

/**
 * @author Philip Tucker
 */
public abstract class RemoveConnectionMutationOperatorTest extends MutationOperatorTest {

private final static String PROP_FILE_NAME = "test.properties";

private final static int POPULATION_SIZE = 100;

private final static int PRE_MUTANTS_SIZE = 60;

private final static short DIM_INPUTS = 4;

private final static short DIM_OUTPUTS = 2;

/**
 * mutation rate
 */
protected final static float MUTATION_RATE = 0.10f;

/**
 * max weight removed
 */
protected final static float MAX_WEIGHT_REMOVED = 0.10f;

private Properties props = new Properties();

/**
 * ctor
 */
public RemoveConnectionMutationOperatorTest() {
	this( RemoveConnectionMutationOperatorTest.class.toString() );
}

/**
 * @param name
 */
public RemoveConnectionMutationOperatorTest( String name ) {
	super( name );
}

/**
 * @see junit.framework.TestCase#setUp()
 */
public void setUp() throws Exception {
	props.loadFromResource( PROP_FILE_NAME );
	props.setProperty( NeatConfiguration.STIMULUS_SIZE_KEY, "" + DIM_INPUTS );
	props.setProperty( NeatConfiguration.RESPONSE_SIZE_KEY, "" + DIM_OUTPUTS );
	super.setUp();
}

/**
 * @see org.jgap.test.MutationOperatorTest#initConfig()
 */
protected void initConfig() throws Exception {
	// clear previous stored configs and populations
	Reset reset = new Reset( props );
	reset.setUserInteraction( false );
	reset.reset();

	// config
	config = new NeatConfiguration( props );
	config.getRandomGenerator().setSeed( 0 );
	config.setFitnessFunction( new DummyFitnessFunction( config.getRandomGenerator() ) );
	config.setPopulationSize( POPULATION_SIZE );
	( (NeatConfiguration) config ).load();
}

/**
 * @see org.jgap.test.MutationOperatorTest#initPreMutants()
 */
protected void initPreMutants() throws Exception {
	preMutants.clear();
	NeatConfiguration neatConfig = (NeatConfiguration) config;
	for ( int i = 0; i < PRE_MUTANTS_SIZE; ++i ) {
		ChromosomeMaterial m = TestChromosomeFactory.newMatureChromosomeMaterial( neatConfig );
		preMutants.add( m );
	}
}

/**
 * @see org.jgap.test.MutationOperatorTest#doTestAfterMutate(java.util.List)
 */
protected void doTestAfterMutate( List mutants ) throws Exception {
	for ( int i = 0; i < preMutants.size(); ++i ) {
		ChromosomeMaterial preMutant = (ChromosomeMaterial) preMutants.get( i );
		ChromosomeMaterial mutant = (ChromosomeMaterial) mutants.get( i );
		NeatChromosomeUtilityTest.validate( mutant.getAlleles(), true /* allowStrandedNeurons */);

		// neurons unchanged
		SortedMap preMutantNeurons = NeatChromosomeUtility.getNeuronMap( preMutant.getAlleles() );
		SortedMap mutantNeurons = NeatChromosomeUtility.getNeuronMap( mutant.getAlleles() );
		assertTrue( "added connections", preMutantNeurons.values().containsAll(
				mutantNeurons.values() ) );
		assertTrue( "removed connections", mutantNeurons.values().containsAll(
				preMutantNeurons.values() ) );

		// connections only shrink
		SortedMap preMutantConns = NeatChromosomeUtility.getConnectionMap( preMutant.getAlleles() );
		SortedMap mutantConns = NeatChromosomeUtility.getConnectionMap( mutant.getAlleles() );
		assertTrue( "added connections", preMutantConns.values().containsAll( mutantConns.values() ) );
		Collection removedConns = new ArrayList( preMutantConns.values() );
		removedConns.removeAll( mutantConns.values() );
		Iterator it = removedConns.iterator();
		while ( it.hasNext() ) {
			ConnectionAllele connAllele = (ConnectionAllele) it.next();
			double absWeight = Math.abs( connAllele.getWeight() );
			assertTrue( "removed weight too large", absWeight <= MAX_WEIGHT_REMOVED );
		}

		// mutations for specific strategy
		assertStrategy( removedConns, mutantConns );
	}
}

/**
 * Based on specific strategy, check connections removed against those not removed.
 * @param removedConns
 * @param remainingConns
 */
protected abstract void assertStrategy( Collection removedConns, SortedMap remainingConns );

/**
 * test default values
 */
public void testDefaults() {
	RemoveConnectionMutationOperator oper = new RemoveConnectionMutationOperator();
	assertEquals( "wrong default mutation rate",
			RemoveConnectionMutationOperator.DEFAULT_MUTATE_RATE, oper.getMutationRate(), 0.0f );
	assertEquals( "wrong default max weight",
			RemoveConnectionMutationOperator.DEFAULT_MAX_WEIGHT_REMOVED, oper.getMaxWeightRemoved(),
			0.0f );
}

}
