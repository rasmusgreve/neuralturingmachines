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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import junit.framework.TestCase;

import org.jgap.Allele;
import org.jgap.Chromosome;
import org.jgap.ChromosomeMaterial;
import org.jgap.test.DummyFitnessFunction;

import com.anji.neat.ConnectionAllele;
import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.NeuronAllele;
import com.anji.neat.NeuronType;
import com.anji.nn.ActivationFunctionType;
import com.anji.util.Properties;
import com.anji.util.Reset;

/**
 * @author Philip Tucker
 */
public class NeatChromosomeUtilityTest extends TestCase {

private final static String PROP_FILE_NAME = "test.properties";

private final static short DIM_STIMULI = 22;

private final static short DIM_HIDDEN = 3;

private final static short DIM_RESPONSE = 10;

private NeatConfiguration config = null;

/**
 * ctor
 */
public NeatChromosomeUtilityTest() {
	this( NeatChromosomeUtilityTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public NeatChromosomeUtilityTest( String name ) {
	super( name );
}

/**
 * @see junit.framework.TestCase#setUp()
 */
public void setUp() throws Exception {
	Properties props = new Properties( PROP_FILE_NAME );
	props.setProperty( NeatConfiguration.STIMULUS_SIZE_KEY, "" + DIM_STIMULI );
	props.setProperty( NeatConfiguration.RESPONSE_SIZE_KEY, "" + DIM_RESPONSE );
	props.setProperty( NeatConfiguration.INITIAL_TOPOLOGY_ACTIVATION_KEY,
			ActivationFunctionType.SIGMOID.toString() );

	// clear previous stored configs and populations
	Reset reset = new Reset( props );
	reset.setUserInteraction( false );
	reset.reset();

	// config
	config = new NeatConfiguration( props );
	config.setFitnessFunction( new DummyFitnessFunction( config.getRandomGenerator() ) );
	config.setPopulationSize( 100 );
	config.getRandomGenerator().setSeed( 0 );
	config.lockSettings();
	config.load();
}

/**
 * validate NEAT chromosome in initial state
 * @param chrom
 * @param dimStimuli
 * @param dimHidden
 * @param dimResponse
 * @param fullyConnected
 */
public static void assertValidInitialNeatChromosome( Chromosome chrom, short dimStimuli,
		short dimHidden, short dimResponse, boolean fullyConnected ) {
	int expectedNumNeurons = dimStimuli + dimResponse;
	int expectedNumConnections = 0;
	if ( fullyConnected ) {
		expectedNumNeurons += dimHidden;
		expectedNumConnections = ( dimStimuli * dimHidden ) + ( dimHidden * dimResponse );
	}

	SortedMap neurons = NeatChromosomeUtility.getNeuronMap( chrom.getAlleles() );
	SortedMap inputs = NeatChromosomeUtility.getNeuronMap( chrom.getAlleles(), NeuronType.INPUT );
	SortedMap hidden = NeatChromosomeUtility.getNeuronMap( chrom.getAlleles(), NeuronType.HIDDEN );
	SortedMap outputs = NeatChromosomeUtility
			.getNeuronMap( chrom.getAlleles(), NeuronType.OUTPUT );
	SortedMap conns = NeatChromosomeUtility.getConnectionMap( chrom.getAlleles() );
	assertEquals( "wrong #neurons", expectedNumNeurons, neurons.size() );
	assertEquals( "wrong #stimuli", dimStimuli, inputs.size() );
	assertEquals( "wrong #response", dimResponse, outputs.size() );
	assertEquals( "wrong #connections", expectedNumConnections, conns.size() );
	assertEquals( "wrong sample chrom size", expectedNumNeurons + expectedNumConnections, chrom
			.size() );

	if ( fullyConnected ) {
		Iterator it = inputs.keySet().iterator();
		while ( it.hasNext() ) {
			List inNeuronIds = new ArrayList();
			inNeuronIds.add( it.next() );
			Collection outConns = NeatChromosomeUtility.extractConnectionAllelesForSrcNeurons( conns
					.values(), inNeuronIds );
			assertEquals( "wrong number output connections for input neuron",
					( dimHidden > 0 ) ? dimHidden : dimResponse, outConns.size() );
		}

		it = hidden.keySet().iterator();
		while ( it.hasNext() ) {
			List hidNeuronIds = new ArrayList();
			hidNeuronIds.add( it.next() );
			Collection outConns = NeatChromosomeUtility.extractConnectionAllelesForSrcNeurons( conns
					.values(), hidNeuronIds );
			assertEquals( "wrong number output connections for input neuron", dimResponse, outConns
					.size() );
			Collection inConns = NeatChromosomeUtility.extractConnectionAllelesForDestNeurons( conns
					.values(), hidNeuronIds );
			assertEquals( "wrong number output connections for input neuron", dimStimuli, inConns
					.size() );
		}

		it = outputs.keySet().iterator();
		while ( it.hasNext() ) {
			List outNeuronIds = new ArrayList();
			outNeuronIds.add( it.next() );
			Collection inConns = NeatChromosomeUtility.extractConnectionAllelesForDestNeurons( conns
					.values(), outNeuronIds );
			assertEquals( "wrong number output connections for input neuron",
					( dimHidden > 0 ) ? dimHidden : dimResponse, inConns.size() );
		}
	}

	Iterator iter = chrom.getAlleles().iterator();
	while ( iter.hasNext() ) {
		Allele allele = (Allele) iter.next();
		assertTrue( "unexpected allele type",
				( ( allele instanceof NeuronAllele ) || ( allele instanceof ConnectionAllele ) ) );
	}
}

/**
 * test not fully connected
 * @throws Exception
 */
public void testNewSampleChromosomeMaterial() throws Exception {
	doTestNewSampleChromosomeMaterial( false );
}

/**
 * test fully connected
 * @throws Exception
 */
public void testNewSampleChromosomeMaterialFullyConnected() throws Exception {
	doTestNewSampleChromosomeMaterial( true );
}

private void doTestNewSampleChromosomeMaterial( boolean fullyConnected ) throws Exception {
	ChromosomeMaterial material = NeatChromosomeUtility.newSampleChromosomeMaterial( DIM_STIMULI,
			DIM_HIDDEN, DIM_RESPONSE, config, fullyConnected );
	Chromosome chrom = new Chromosome( material, config.nextChromosomeId() );
	assertValidInitialNeatChromosome( chrom, DIM_STIMULI, DIM_HIDDEN, DIM_RESPONSE,
			fullyConnected );
}

/**
 * test valid netowrk connectivity
 * @throws Exception
 */
public void testIsValid() throws Exception {
	// good chromosome
	ChromosomeMaterial material = NeatChromosomeUtility.newSampleChromosomeMaterial( DIM_STIMULI,
			DIM_HIDDEN, DIM_RESPONSE, config, true );
	NeatChromosomeUtilityTest.validate( material.getAlleles() );

	// too many inputs, not enough connections
	ChromosomeMaterial newMaterial = material.clone( null );
	newMaterial.getAlleles().add( config.newNeuronAllele( NeuronType.HIDDEN ) );
	try {
		validate( newMaterial.getAlleles() );
		fail( "should not be valid, too many inputs, not enough connections" );
	}
	catch ( AssertionError e ) {
		// success
	}
}

/**
 * @param alleles
 * @throws AssertionError
 * @see NeatChromosomeUtilityTest#validate(Set, boolean)
 */
public static void validate( Set alleles ) throws AssertionError {
	validate( alleles, false );
}

/**
 * tests for valid genes to represent a NEAT chromosome
 * @param alleles <code>Set</code> contains <code>Gene</code> objects
 * @param allowStrandedNeurons
 * @throws AssertionError
 */
public static void validate( Set alleles, boolean allowStrandedNeurons ) throws AssertionError {
	SortedMap in = NeatChromosomeUtility.getNeuronMap( alleles, NeuronType.INPUT );
	SortedMap out = NeatChromosomeUtility.getNeuronMap( alleles, NeuronType.OUTPUT );
	if ( in.isEmpty() )
		throw new AssertionError( "no inputs" );
	if ( out.isEmpty() )
		throw new AssertionError( "no outputs" );

	SortedMap neurons = NeatChromosomeUtility.getNeuronMap( alleles );
	List conns = NeatChromosomeUtility.getConnectionList( alleles );
	Iterator iter = conns.iterator();
	while ( iter.hasNext() ) {
		ConnectionAllele cAllele = (ConnectionAllele) iter.next();
		if ( !( neurons.containsKey( cAllele.getSrcNeuronId() ) ) )
			throw new AssertionError( "connection src does not exist" );
		if ( !( neurons.containsKey( cAllele.getDestNeuronId() ) ) )
			throw new AssertionError( "connection dest does not exist" );
	}

	if ( !allowStrandedNeurons ) {
		List hiddenNeurons = NeatChromosomeUtility.getNeuronList( alleles, NeuronType.HIDDEN );
		iter = hiddenNeurons.iterator();
		while ( iter.hasNext() ) {
			NeuronAllele nAllele = (NeuronAllele) iter.next();
			Collection ids = new ArrayList();
			ids.add( nAllele.getInnovationId() );
			Collection inConns = NeatChromosomeUtility.extractConnectionAllelesForDestNeurons( conns,
					ids );
			Collection outConns = NeatChromosomeUtility.extractConnectionAllelesForSrcNeurons( conns,
					ids );
			if ( inConns.isEmpty() )
				throw new AssertionError( "no inputs for hidden neuron " + nAllele.toString() );
			if ( outConns.isEmpty() )
				throw new AssertionError( "no outputs for hidden neuron " + nAllele.toString() );
		}
	}
}

/**
 * @throws Exception
 */
public void testExtract() throws Exception {
	Collection expectedInputs = new ArrayList();
	for ( int i = 0; i < 20; ++i )
		expectedInputs.add( config.newNeuronAllele( NeuronType.INPUT ) );
	Collection expectedOutputs = new ArrayList();
	for ( int i = 0; i < 10; ++i )
		expectedOutputs.add( config.newNeuronAllele( NeuronType.OUTPUT ) );
	Collection expectedHidden = new ArrayList();
	for ( int i = 0; i < 20; ++i )
		expectedHidden.add( config.newNeuronAllele( NeuronType.HIDDEN ) );
	Collection expectedConns = new ArrayList();
	for ( int i = 0; i < 20; ++i )
		expectedConns.add( config.newConnectionAllele( new Long( i ), new Long( i * 10 ) ) );

	Collection expectedNeurons = new ArrayList();
	expectedNeurons.addAll( expectedInputs );
	expectedNeurons.addAll( expectedOutputs );
	expectedNeurons.addAll( expectedHidden );
	List allGenes = new ArrayList();
	allGenes.addAll( expectedNeurons );
	allGenes.addAll( expectedConns );
	ChromosomeMaterial material = new ChromosomeMaterial( allGenes );
	Chromosome chrom = new Chromosome( material, config.nextChromosomeId() );

	SortedMap inputMap = NeatChromosomeUtility
			.getNeuronMap( chrom.getAlleles(), NeuronType.INPUT );
	List inputList = NeatChromosomeUtility.getNeuronList( chrom.getAlleles(), NeuronType.INPUT );
	SortedMap outputMap = NeatChromosomeUtility.getNeuronMap( chrom.getAlleles(),
			NeuronType.OUTPUT );
	List outputList = NeatChromosomeUtility.getNeuronList( chrom.getAlleles(), NeuronType.OUTPUT );
	SortedMap hiddenMap = NeatChromosomeUtility.getNeuronMap( chrom.getAlleles(),
			NeuronType.HIDDEN );
	List hiddenList = NeatChromosomeUtility.getNeuronList( chrom.getAlleles(), NeuronType.HIDDEN );
	SortedMap neuronMap = NeatChromosomeUtility.getNeuronMap( chrom.getAlleles() );
	List neuronList = NeatChromosomeUtility.getNeuronList( chrom.getAlleles() );
	SortedMap connMap = NeatChromosomeUtility.getConnectionMap( chrom.getAlleles() );
	List connList = NeatChromosomeUtility.getConnectionList( chrom.getAlleles() );

	assertTrue( "map: inputs != expected", expectedInputs.containsAll( inputMap.values() )
			&& ( inputMap.values().containsAll( expectedInputs ) ) );
	assertTrue( "list: inputs != expected", expectedInputs.containsAll( inputList )
			&& ( inputList.containsAll( expectedInputs ) ) );
	assertTrue( "map: outputs != expected", expectedOutputs.containsAll( outputMap.values() )
			&& ( outputMap.values().containsAll( expectedOutputs ) ) );
	assertTrue( "list: outputs != expected", expectedOutputs.containsAll( outputList )
			&& ( outputList.containsAll( expectedOutputs ) ) );
	assertTrue( "map: hidden != expected", expectedHidden.containsAll( hiddenMap.values() )
			&& ( hiddenMap.values().containsAll( expectedHidden ) ) );
	assertTrue( "list: hidden != expected", expectedHidden.containsAll( hiddenList )
			&& ( hiddenList.containsAll( expectedHidden ) ) );
	assertTrue( "map: neurons != expected", expectedNeurons.containsAll( neuronMap.values() )
			&& ( neuronMap.values().containsAll( expectedNeurons ) ) );
	assertTrue( "list: neurons != expected", expectedNeurons.containsAll( neuronList )
			&& ( neuronList.containsAll( expectedNeurons ) ) );
	assertTrue( "map: conns != expected", expectedConns.containsAll( connMap.values() )
			&& ( connMap.values().containsAll( expectedConns ) ) );
	assertTrue( "list: conns != expected", expectedConns.containsAll( connList )
			&& ( connList.containsAll( expectedConns ) ) );

	// chrom.cleanup();
}

/**
 * test connectivity of neuron and connection genes
 * @throws Exception
 */
public void testNeuronsAreConnected() throws Exception {
	ConnectionAllele cg11 = config.newConnectionAllele( new Long( 1 ), new Long( 1 ) );
	ConnectionAllele cg12 = config.newConnectionAllele( new Long( 1 ), new Long( 2 ) );
	ConnectionAllele cg23 = config.newConnectionAllele( new Long( 2 ), new Long( 3 ) );
	ConnectionAllele cg33 = config.newConnectionAllele( new Long( 3 ), new Long( 3 ) );
	ConnectionAllele cg45 = config.newConnectionAllele( new Long( 4 ), new Long( 5 ) );
	ConnectionAllele cg56 = config.newConnectionAllele( new Long( 5 ), new Long( 6 ) );

	Set conns = new HashSet();
	assertTrue( "1-2 not connected in empty set", NeatChromosomeUtility.neuronsAreConnected(
			new Long( 1 ), new Long( 2 ), conns ) == false );
	assertTrue( "1-1 connected in empty set", NeatChromosomeUtility.neuronsAreConnected(
			new Long( 1 ), new Long( 1 ), conns ) );

	conns.add( cg12 );
	conns.add( cg33 );
	conns.add( cg23 );
	conns.add( cg45 );
	conns.add( cg56 );
	boolean connected = NeatChromosomeUtility.neuronsAreConnected( new Long( 1 ), new Long( 6 ),
			conns );
	assertTrue( "1-6 connected", !connected );
	connected = NeatChromosomeUtility.neuronsAreConnected( new Long( 2 ), new Long( 6 ), conns );
	assertTrue( "2-6 connected", !connected );
	connected = NeatChromosomeUtility.neuronsAreConnected( new Long( 1 ), new Long( 3 ), conns );
	assertTrue( "1-3 not connected", connected );
	connected = NeatChromosomeUtility.neuronsAreConnected( new Long( 4 ), new Long( 6 ), conns );
	assertTrue( "4-6 not connected", connected );

	conns.add( cg11 );
	connected = NeatChromosomeUtility.neuronsAreConnected( new Long( 1 ), new Long( 1 ), conns );
	assertTrue( "1-1 not connected", connected );
	connected = NeatChromosomeUtility.neuronsAreConnected( new Long( 2 ), new Long( 2 ), conns );
	assertTrue( "2-2 not connected", connected );
}

}
