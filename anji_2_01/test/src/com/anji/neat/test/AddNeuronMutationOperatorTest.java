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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import org.jgap.ChromosomeMaterial;
import org.jgap.test.DummyFitnessFunction;
import org.jgap.test.MutationOperatorTest;

import com.anji.neat.AddNeuronMutationOperator;
import com.anji.neat.ConnectionAllele;
import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.NeuronAllele;
import com.anji.neat.NeuronType;
import com.anji.util.Properties;
import com.anji.util.Reset;

/**
 * @author Philip Tucker
 */
public class AddNeuronMutationOperatorTest extends MutationOperatorTest {

private final static String PROP_FILE_NAME = "test.properties";

private final static int POPULATION_SIZE = 100;

private final static int PRE_MUTANTS_SIZE = 60;

private final static short DIM_INPUTS = 4;

private final static short DIM_OUTPUTS = 2;

private final static float MUTATION_RATE = 0.10f;

/**
 *  
 */
public AddNeuronMutationOperatorTest() {
	this( AddNeuronMutationOperatorTest.class.toString() );
}

/**
 * @param name
 */
public AddNeuronMutationOperatorTest( String name ) {
	super( name );
}

/**
 * @see junit.framework.TestCase#setUp()
 */
public void setUp() throws Exception {
	super.setUp();
}

/**
 * @see org.jgap.test.MutationOperatorTest#initUut()
 */
protected void initUut() throws Exception {
	uut = new AddNeuronMutationOperator( MUTATION_RATE );
	assertEquals( "wrong mutation rate", MUTATION_RATE, uut.getMutationRate(), 0.0f );
}

/**
 * @see org.jgap.test.MutationOperatorTest#initConfig()
 */
protected void initConfig() throws Exception {
	Properties props = new Properties( PROP_FILE_NAME );
	props.setProperty( NeatConfiguration.STIMULUS_SIZE_KEY, "" + DIM_INPUTS );
	props.setProperty( NeatConfiguration.RESPONSE_SIZE_KEY, "" + DIM_OUTPUTS );

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
	for ( int i = 0; i < preMutants.size(); ++i ) {
		ChromosomeMaterial preMutant = (ChromosomeMaterial) preMutants.get( i );
		ChromosomeMaterial mutant = (ChromosomeMaterial) mutants.get( i );
		NeatChromosomeUtilityTest.validate( mutant.getAlleles() );

		// input neurons unchanged
		SortedMap preMutantInNeurons = NeatChromosomeUtility.getNeuronMap( preMutant.getAlleles(),
				NeuronType.INPUT );
		SortedMap mutantInNeurons = NeatChromosomeUtility.getNeuronMap( mutant.getAlleles(),
				NeuronType.INPUT );
		assertEquals( "modified input neurons", preMutantInNeurons, mutantInNeurons );

		// output neurons unchanged
		SortedMap preMutantOutNeurons = NeatChromosomeUtility.getNeuronMap( preMutant.getAlleles(),
				NeuronType.OUTPUT );
		SortedMap mutantOutNeurons = NeatChromosomeUtility.getNeuronMap( mutant.getAlleles(),
				NeuronType.OUTPUT );
		assertEquals( "modified output neurons", preMutantOutNeurons, mutantOutNeurons );

		// hidden neurons only grow
		SortedMap preMutantHidNeurons = NeatChromosomeUtility.getNeuronMap( preMutant.getAlleles(),
				NeuronType.HIDDEN );
		SortedMap mutantHidNeurons = NeatChromosomeUtility.getNeuronMap( mutant.getAlleles(),
				NeuronType.HIDDEN );
		assertTrue( "modified hidden neurons", mutantHidNeurons.values().containsAll(
				preMutantHidNeurons.values() ) );
		Set newHidNeurons = new HashSet();
		newHidNeurons.addAll( mutantHidNeurons.values() );
		newHidNeurons.removeAll( preMutantHidNeurons.values() );
		int numNewNeurons = mutantHidNeurons.size() - preMutantHidNeurons.size();

		// connections only grow in size
		SortedMap preMutantConns = NeatChromosomeUtility.getConnectionMap( preMutant.getAlleles() );
		SortedMap mutantConns = NeatChromosomeUtility.getConnectionMap( mutant.getAlleles() );
		assertTrue( "lost connections", mutantConns.size() >= preMutantConns.size() );
		Set newConns = new HashSet();
		newConns.addAll( mutantConns.values() );
		newConns.removeAll( preMutantConns.values() );
		int numNewConns = mutantConns.size() - preMutantConns.size();

		// new conns for every new neuron
		assertEquals( "should be new connection for every new neuron", numNewNeurons, numNewConns );
		Iterator it = newHidNeurons.iterator();
		while ( it.hasNext() ) {
			NeuronAllele neuronAllele = (NeuronAllele) it.next();

			// incoming connection
			Set inConnAlleles = findConnectionsWithDest( mutantConns.values(), neuronAllele
					.getInnovationId() );
			assertEquals( "new neuron should have exactly one in conection", 1, inConnAlleles.size() );
			ConnectionAllele inConnAllele = (ConnectionAllele) inConnAlleles.iterator().next();

			// outgoing connection
			Set outConnAlleles = findConnectionsWithSrc( mutantConns.values(), neuronAllele
					.getInnovationId() );
			assertEquals( "new neuron should have exactly one out conection", 1, inConnAlleles.size() );
			ConnectionAllele outConnAllele = (ConnectionAllele) outConnAlleles.iterator().next();

			// replaced connection
			ConnectionAllele expectedReplacedConnAllele = ( (NeatConfiguration) config )
					.newConnectionAllele( inConnAllele.getSrcNeuronId(), outConnAllele.getDestNeuronId() );

			assertTrue( "replaced connection not found", preMutantConns.values().contains(
					expectedReplacedConnAllele ) );
			ConnectionAllele replacedConnAllele = null;
			Iterator it2 = preMutantConns.values().iterator();
			while ( it2.hasNext() && ( replacedConnAllele == null ) ) {
				ConnectionAllele current = (ConnectionAllele) it2.next();
				if ( current.equals( expectedReplacedConnAllele ) )
					replacedConnAllele = current;
			}
			assertEquals( "incoming weight wrong", 1.0d, inConnAllele.getWeight(), 0.0d );
			assertEquals( "outgoing weight wrong", replacedConnAllele.getWeight(), outConnAllele
					.getWeight(), 0.0d );
		}

		// TODO - expected mutations
	}
}

/**
 * @param conns
 * @param id
 * @return <code>Set</code> contains <code>ConnectionAllele</code> objects
 */
protected static Set findConnectionsWithDest( Collection conns, Long id ) {
	// TODO - make map of connection alleles indexed on src/dest ids
	Set result = new HashSet();
	Iterator it = conns.iterator();
	while ( it.hasNext() ) {
		ConnectionAllele connAllele = (ConnectionAllele) it.next();
		if ( connAllele.getDestNeuronId() == id )
			result.add( connAllele );
	}
	return result;
}

/**
 * @param conns
 * @param id
 * @return <code>Set</code> contains <code>ConnectionAllele</code> objects
 */
protected static Set findConnectionsWithSrc( Collection conns, Long id ) {
	Set result = new HashSet();
	Iterator it = conns.iterator();
	while ( it.hasNext() ) {
		ConnectionAllele connAllele = (ConnectionAllele) it.next();
		if ( connAllele.getSrcNeuronId() == id )
			result.add( connAllele );
	}
	return result;
}

/**
 * test
 */
public void testDefaults() {
	AddNeuronMutationOperator oper = new AddNeuronMutationOperator();
	assertEquals( "wrong default mutation rate", AddNeuronMutationOperator.DEFAULT_MUTATE_RATE,
			oper.getMutationRate(), 0.0f );
}

}
