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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import org.jgap.Chromosome;
import org.jgap.ChromosomeMaterial;
import org.jgap.test.DummyFitnessFunction;
import org.jgap.test.MutationOperatorTest;

import com.anji.integration.AnjiNetTranscriber;
import com.anji.integration.TranscriberException;
import com.anji.neat.AddConnectionMutationOperator;
import com.anji.neat.ConnectionAllele;
import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.NeuronAllele;
import com.anji.nn.ActivationFunctionType;
import com.anji.nn.RecurrencyPolicy;
import com.anji.util.Properties;
import com.anji.util.Reset;

/**
 * @author Philip Tucker
 */
public class AddConnectionMutationOperatorTest extends MutationOperatorTest {

private final static String PROP_FILE_NAME = "test.properties";

private final static int POPULATION_SIZE = 100;

private final static int PRE_MUTANTS_SIZE = 60;

private final static short DIM_INPUTS = 4;

private final static short DIM_OUTPUTS = 2;

private final static float MUTATION_RATE = 0.10f;

private RecurrencyPolicy recurrencyPolicy;

private Properties props = new Properties();

private boolean linearInputs = false;

/**
 * ctor
 */
public AddConnectionMutationOperatorTest() {
	this( AddConnectionMutationOperatorTest.class.toString() );
}

/**
 * ctor
 * 
 * @param name
 */
public AddConnectionMutationOperatorTest( String name ) {
	super( name );
}

/**
 * @see org.jgap.test.MutationOperatorTest#initUut()
 */
protected void initUut() throws Exception {
	uut = new AddConnectionMutationOperator( MUTATION_RATE, recurrencyPolicy );
	assertEquals( "wrong mutation rate", MUTATION_RATE, uut.getMutationRate(), 0.0f );
}

/**
 * @see org.jgap.test.MutationOperatorTest#initConfig()
 */
protected void initConfig() throws Exception {
	props.loadFromResource( PROP_FILE_NAME );
	props.setProperty( NeatConfiguration.STIMULUS_SIZE_KEY, "" + DIM_INPUTS );
	props.setProperty( NeatConfiguration.RESPONSE_SIZE_KEY, "" + DIM_OUTPUTS );
	props.setProperty( NeatConfiguration.INITIAL_TOPOLOGY_FULLY_CONNECTED_KEY, "false" );
	recurrencyPolicy = RecurrencyPolicy.load( props );
	if ( linearInputs )
		props.setProperty( NeatConfiguration.INITIAL_TOPOLOGY_ACTIVATION_INPUT_KEY,
				ActivationFunctionType.LINEAR.toString() );

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
	int totalNewConns = 0;
	for ( int i = 0; i < preMutants.size(); ++i ) {
		ChromosomeMaterial preMutant = (ChromosomeMaterial) preMutants.get( i );
		ChromosomeMaterial mutant = (ChromosomeMaterial) mutants.get( i );
		NeatChromosomeUtilityTest.validate( mutant.getAlleles() );

		// neurons unchanged
		SortedMap preMutantNeurons = NeatChromosomeUtility.getNeuronMap( preMutant.getAlleles() );
		SortedMap mutantNeurons = NeatChromosomeUtility.getNeuronMap( mutant.getAlleles() );
		assertEquals( "modified neurons", preMutantNeurons, mutantNeurons );

		// connections only grow
		SortedMap preMutantConns = NeatChromosomeUtility.getConnectionMap( preMutant.getAlleles() );
		SortedMap mutantConns = NeatChromosomeUtility.getConnectionMap( mutant.getAlleles() );
		assertTrue( "lost connections", mutantConns.values().containsAll( preMutantConns.values() ) );

		// new connections
		Set newConnIds = new HashSet();
		newConnIds.addAll( mutantConns.keySet() );
		newConnIds.removeAll( preMutantConns.keySet() );
		totalNewConns += newConnIds.size();

		// recurrency
		if ( RecurrencyPolicy.DISALLOWED.equals( recurrencyPolicy ) ) {
			Chromosome c = new Chromosome( mutant, config.nextChromosomeId() );
			addChromosome( c );
			AnjiNetTranscriber tr = new AnjiNetTranscriber( recurrencyPolicy );
			try {
				tr.transcribe( c );
			}
			catch ( TranscriberException e ) {
				fail( "mutation should not have created recurrent connection" );
			}
			try {
				tr.newAnjiNet( c );
			}
			catch ( TranscriberException e ) {
				fail( "mutation should not have created recurrent connection" );
			}
		}

		// linear inputs
		if ( linearInputs ) {
			Iterator it = newConnIds.iterator();
			while ( it.hasNext() ) {
				Long connId = (Long) it.next();
				ConnectionAllele conn = (ConnectionAllele) mutantConns.get( connId );
				Long destNeuronId = conn.getDestNeuronId();
				NeuronAllele destNeuron = (NeuronAllele) mutantNeurons.get( destNeuronId );
				assertFalse( "connection to linear neuron", ActivationFunctionType.LINEAR
						.equals( destNeuron.getActivationType() ) );
			}
		}

		// TODO - expected mutations
	}

	assertTrue( "no mutations", totalNewConns > 0 );
}

/**
 * test
 * 
 * @throws Exception
 */
public void testNoRecurrency() throws Exception {
	recurrencyPolicy = RecurrencyPolicy.DISALLOWED;
	initUut();
	super.testMutationOperator();
}

/**
 * test
 * 
 * @throws Exception
 */
public void testLinearInput() throws Exception {
	linearInputs = true;
	recurrencyPolicy = RecurrencyPolicy.BEST_GUESS;
	initUut();
	initPreMutants();
	super.testMutationOperator();
}

/**
 * test
 */
public void testDefaults() {
	AddConnectionMutationOperator oper = new AddConnectionMutationOperator();
	assertEquals( "wrong default mutation rate",
			AddConnectionMutationOperator.DEFAULT_MUTATE_RATE, oper.getMutationRate(), 0.0f );
}

}
