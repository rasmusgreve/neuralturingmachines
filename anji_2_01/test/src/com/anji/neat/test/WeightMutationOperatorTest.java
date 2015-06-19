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

import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;

import org.jgap.ChromosomeMaterial;
import org.jgap.test.DummyFitnessFunction;
import org.jgap.test.MutationOperatorTest;

import com.anji.neat.ConnectionAllele;
import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.WeightMutationOperator;
import com.anji.util.Properties;
import com.anji.util.Reset;

/**
 * @author Philip Tucker
 */
public class WeightMutationOperatorTest extends MutationOperatorTest {

private final static String PROP_FILE_NAME = "test.properties";

private final static int POPULATION_SIZE = 100;

private final static int PRE_MUTANTS_SIZE = 60;

private final static short DIM_INPUTS = 4;

private final static short DIM_OUTPUTS = 2;

private final static float MUTATION_RATE = 0.10f;

private final static float STD_DEV = 0.20f;

/**
 * ctor
 */
public WeightMutationOperatorTest() {
	this( WeightMutationOperatorTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public WeightMutationOperatorTest( String name ) {
	super( name );
}

/**
 * @see org.jgap.test.MutationOperatorTest#initUut()
 */
protected void initUut() throws Exception {
	uut = new WeightMutationOperator( MUTATION_RATE, STD_DEV );
	assertEquals( "wrong mutation rate", MUTATION_RATE, uut.getMutationRate(), 0.0f );
}

/**
 * @see org.jgap.test.MutationOperatorTest#initConfig()
 */
protected void initConfig() throws Exception {
	Properties props = new Properties( PROP_FILE_NAME );
	props.setProperty( NeatConfiguration.STIMULUS_SIZE_KEY, "" + DIM_INPUTS );
	props.setProperty( NeatConfiguration.RESPONSE_SIZE_KEY, "" + DIM_OUTPUTS );
	props.setProperty( NeatConfiguration.INITIAL_TOPOLOGY_FULLY_CONNECTED_KEY, "true" );

	// clear previous stored configs and populations
	Reset reset = new Reset( props );
	reset.setUserInteraction( false );
	reset.reset();

	// config
	config = new NeatConfiguration( props );
	config.setPopulationSize( POPULATION_SIZE );
	config.getRandomGenerator().setSeed( 0 );
	config.setFitnessFunction( new DummyFitnessFunction( config.getRandomGenerator() ) );
	( (NeatConfiguration) config ).load();
}

/**
 * @see org.jgap.test.MutationOperatorTest#initPreMutants()
 */
protected void initPreMutants() throws Exception {
	preMutants.clear();
	for ( int i = 0; i < PRE_MUTANTS_SIZE; ++i ) {
		ChromosomeMaterial material = ChromosomeMaterial.randomInitialChromosomeMaterial( config );
		preMutants.add( material );
	}
}

/**
 * @see org.jgap.test.MutationOperatorTest#doTestAfterMutate(java.util.List)
 */
protected void doTestAfterMutate( List mutants ) throws Exception {
	int totalChanged = 0;
	for ( int i = 0; i < preMutants.size(); ++i ) {
		ChromosomeMaterial preMutant = (ChromosomeMaterial) preMutants.get( i );
		ChromosomeMaterial mutant = (ChromosomeMaterial) mutants.get( i );
		NeatChromosomeUtilityTest.validate( mutant.getAlleles() );

		// neurons unchanged
		SortedMap preMutantNeurons = NeatChromosomeUtility.getNeuronMap( preMutant.getAlleles() );
		SortedMap mutantNeurons = NeatChromosomeUtility.getNeuronMap( mutant.getAlleles() );
		assertEquals( "modified neurons", preMutantNeurons, mutantNeurons );

		// connection only changed weight
		SortedMap preMutantConns = NeatChromosomeUtility.getConnectionMap( preMutant.getAlleles() );
		SortedMap mutantConns = NeatChromosomeUtility.getConnectionMap( mutant.getAlleles() );
		assertTrue( "added connections", preMutantConns.entrySet().containsAll(
				mutantConns.entrySet() ) );
		assertTrue( "lost connections", mutantConns.entrySet().containsAll(
				preMutantConns.entrySet() ) );

		int totalChangedPerChromosome = 0;
		Iterator preIter = preMutantConns.values().iterator();
		Iterator postIter = mutantConns.values().iterator();
		while ( preIter.hasNext() ) {
			assertTrue( "different # connections", postIter.hasNext() );
			ConnectionAllele preConn = (ConnectionAllele) preIter.next();
			ConnectionAllele postConn = (ConnectionAllele) postIter.next();
			assertEquals( "different innovation id", preConn.getInnovationId(), postConn
					.getInnovationId() );
			double delta = Math.abs( preConn.getWeight() - postConn.getWeight() );
			assertTrue( "delta too big", delta < ( STD_DEV * 4 ) );
			if ( delta > 0 )
				totalChangedPerChromosome++;
		}

		// TODO - expected mutations
		assertTrue( "too many mutations: " + totalChangedPerChromosome,
				totalChangedPerChromosome <= 5 );
		totalChanged += totalChangedPerChromosome;
	}
	assertTrue( "too few mutations: " + totalChanged, totalChanged >= 10 );
}

/**
 * test default values
 */
public void testDefaults() {
	WeightMutationOperator oper = new WeightMutationOperator();
	assertEquals( "wrong default mutation rate", WeightMutationOperator.DEFAULT_MUTATE_RATE, oper
			.getMutationRate(), 0.0f );
	assertEquals( "wrong default std dev", WeightMutationOperator.DEFAULT_STD_DEV, oper
			.getStdDev(), 0.0f );
}

}
