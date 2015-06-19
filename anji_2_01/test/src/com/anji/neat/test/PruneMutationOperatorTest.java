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

import java.util.List;
import java.util.SortedMap;

import org.jgap.ChromosomeMaterial;
import org.jgap.test.DummyFitnessFunction;
import org.jgap.test.MutationOperatorTest;

import com.anji.neat.ConnectionAllele;
import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.NeuronAllele;
import com.anji.neat.NeuronType;
import com.anji.neat.PruneMutationOperator;
import com.anji.neat.RemoveConnectionMutationOperator;
import com.anji.util.Properties;
import com.anji.util.Reset;

/**
 * @author Philip Tucker
 */
public class PruneMutationOperatorTest extends MutationOperatorTest {

private final static String PROP_FILE_NAME = "test.properties";

private final static int POPULATION_SIZE = 100;

private final static int PRE_MUTANTS_SIZE = 60;

private final static short DIM_INPUTS = 4;

private final static short DIM_OUTPUTS = 2;

private final static float MUTATION_RATE = 1.00f;

private Properties props = new Properties();

/**
 * ctor
 */
public PruneMutationOperatorTest() {
	this( PruneMutationOperatorTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public PruneMutationOperatorTest( String name ) {
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
 * @see org.jgap.test.MutationOperatorTest#initUut()
 */
protected void initUut() throws Exception {
	uut = new PruneMutationOperator( MUTATION_RATE );
	assertEquals( "wrong mutation rate", MUTATION_RATE, uut.getMutationRate(), 0.0f );
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

	// add several that have a few connections and neurons removed
	for ( int i = 0; i < PRE_MUTANTS_SIZE - 1; ++i ) {
		ChromosomeMaterial m = TestChromosomeFactory.newMatureChromosomeMaterial( neatConfig );

		// remove random connections up to 1/10 of total
		List conns = NeatChromosomeUtility.getConnectionList( m.getAlleles() );
		for ( int j = 0; j < config.getRandomGenerator().nextInt( ( conns.size() / 10 ) + 1 ); ++j ) {
			int rand = config.getRandomGenerator().nextInt( conns.size() );
			m.getAlleles().remove( conns.get( rand ) );
		}

		// remove random neurons up to 1/10 of total
		List neurons = NeatChromosomeUtility.getNeuronList( m.getAlleles(), NeuronType.HIDDEN );
		for ( int j = 0; j < config.getRandomGenerator().nextInt( ( neurons.size() / 10 ) + 1 ); ++j ) {
			int rand = config.getRandomGenerator().nextInt( neurons.size() );
			m.getAlleles().remove( neurons.get( rand ) );
		}

		preMutants.add( m );
	}

	// specifically add one with a stranded sub-network
	ChromosomeMaterial m = TestChromosomeFactory.newMatureChromosomeMaterial( neatConfig );
	NeuronAllele neuronAllele1 = neatConfig.newNeuronAllele( NeuronType.HIDDEN );
	NeuronAllele neuronAllele2 = neatConfig.newNeuronAllele( NeuronType.HIDDEN );
	ConnectionAllele connAllele1 = neatConfig.newConnectionAllele( neuronAllele1
			.getInnovationId(), neuronAllele2.getInnovationId() );
	ConnectionAllele connAlele2 = neatConfig.newConnectionAllele(
			neuronAllele2.getInnovationId(), neuronAllele1.getInnovationId() );
	m.getAlleles().add( neuronAllele1 );
	m.getAlleles().add( neuronAllele2 );
	m.getAlleles().add( connAllele1 );
	m.getAlleles().add( connAlele2 );
	preMutants.add( m );
}

/**
 * @see org.jgap.test.MutationOperatorTest#doTestAfterMutate(java.util.List)
 */
protected void doTestAfterMutate( List mutants ) throws Exception {
	for ( int i = 0; i < preMutants.size(); ++i ) {
		ChromosomeMaterial preMutant = (ChromosomeMaterial) preMutants.get( i );
		ChromosomeMaterial mutant = (ChromosomeMaterial) mutants.get( i );
		NeatChromosomeUtilityTest.validate( mutant.getAlleles(), false /* allowStrandedNeurons */);

		// neurons only shrink
		SortedMap preMutantNeurons = NeatChromosomeUtility.getNeuronMap( preMutant.getAlleles() );
		SortedMap mutantNeurons = NeatChromosomeUtility.getNeuronMap( mutant.getAlleles() );
		assertTrue( "added connections", preMutantNeurons.values().containsAll(
				mutantNeurons.values() ) );
		int numRemovedNeurons = ( preMutantNeurons.size() - mutantNeurons.size() );

		// connections only shrink
		SortedMap preMutantConns = NeatChromosomeUtility.getConnectionMap( preMutant.getAlleles() );
		SortedMap mutantConns = NeatChromosomeUtility.getConnectionMap( mutant.getAlleles() );
		assertTrue( "added connections", preMutantConns.values().containsAll( mutantConns.values() ) );

		try {
			NeatChromosomeUtilityTest
					.validate( preMutant.getAlleles(), false /* allowStrandedNeurons */);
		}
		catch ( AssertionError e ) {
			// premutant had stranded neuron, so we should have removed some alleles
			assertTrue( "did not prune to fix stranded neuron", numRemovedNeurons > 0 );
		}

		// TODO - expected mutations
	}
}

/**
 * test default values
 */
public void testDefaults() {
	RemoveConnectionMutationOperator oper = new RemoveConnectionMutationOperator();
	assertEquals( "wrong default mutation rate",
			RemoveConnectionMutationOperator.DEFAULT_MUTATE_RATE, oper.getMutationRate(), 0.0f );
	assertEquals( "wrong default mutation rate",
			RemoveConnectionMutationOperator.DEFAULT_MAX_WEIGHT_REMOVED, oper.getMaxWeightRemoved(),
			0.0f );
}

}
