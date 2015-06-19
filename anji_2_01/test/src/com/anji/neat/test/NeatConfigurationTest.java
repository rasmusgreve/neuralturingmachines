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
 * created by Philip Tucker on Mar 14, 2003
 */
package com.anji.neat.test;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
import org.jgap.Allele;
import org.jgap.Chromosome;
import org.jgap.ChromosomeMaterial;
import org.jgap.IdFactory;
import org.jgap.InvalidConfigurationException;
import org.jgap.MutationOperator;
import org.jgap.ReproductionOperator;
import org.jgap.impl.CloneReproductionOperator;
import org.jgap.test.ConfigurationTest;

import com.anji.integration.SimpleSelector;
import com.anji.neat.AddConnectionMutationOperator;
import com.anji.neat.AddNeuronMutationOperator;
import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.NeatCrossoverReproductionOperator;
import com.anji.neat.NeatIdMap;
import com.anji.neat.NeuronAllele;
import com.anji.neat.NeuronType;
import com.anji.neat.PruneMutationOperator;
import com.anji.neat.RemoveConnectionMutationOperator;
import com.anji.neat.WeightMutationOperator;
import com.anji.nn.ActivationFunctionType;
import com.anji.nn.ClampedLinearActivationFunction;
import com.anji.nn.LinearActivationFunction;
import com.anji.nn.SigmoidActivationFunction;
import com.anji.util.Properties;
import com.anji.util.Reset;

/**
 * @author Philip Tucker
 */
public class NeatConfigurationTest extends ConfigurationTest {

private static final short DIM_STIMULI = 21;

private static final short DIM_HIDDEN = 3;

private static final short DIM_RESPONSE = 10;

private static final float SURVIVAL_RATE = 0.20f;

private static final long BASE_ID = IdFactory.DEFAULT_BASE_ID;

private static final double WEIGHT_MAX = 2.0;

private static final double WEIGHT_MIN = -2.0;

private final static Properties TEST_PROPERTIES = new Properties();
static {
	String propFileName = "skeleton.properties";
	try {
		TEST_PROPERTIES.loadFromResource( propFileName );
	}
	catch ( IOException e ) {
		throw new IllegalArgumentException( "error loading " + propFileName );
	}
	TEST_PROPERTIES.setProperty( NeatConfiguration.STIMULUS_SIZE_KEY, Short
			.toString( DIM_STIMULI ) );
	TEST_PROPERTIES.setProperty( NeatConfiguration.RESPONSE_SIZE_KEY, Short
			.toString( DIM_RESPONSE ) );
	TEST_PROPERTIES.setProperty( NeatConfiguration.INITIAL_TOPOLOGY_NUM_HIDDEN_NEURONS_KEY, Short
			.toString( DIM_HIDDEN ) );
	TEST_PROPERTIES.setProperty( NeatConfiguration.INITIAL_TOPOLOGY_FULLY_CONNECTED_KEY,
			Boolean.TRUE.toString() );
	TEST_PROPERTIES.setProperty( NeatConfiguration.SURVIVAL_RATE_KEY, Float
			.toString( SURVIVAL_RATE ) );
	TEST_PROPERTIES.setProperty( NeatConfiguration.WEIGHT_MAX_KEY, Double.toString( WEIGHT_MAX ) );
	TEST_PROPERTIES.setProperty( NeatConfiguration.WEIGHT_MIN_KEY, Double.toString( WEIGHT_MIN ) );
	TEST_PROPERTIES.setProperty( NeatIdMap.NEAT_ID_MAP_FILE_KEY, "./test/db/neatid.xml" );
	TEST_PROPERTIES.setProperty( NeatConfiguration.ID_FACTORY_KEY, "./test/db/id.xml" );
	TEST_PROPERTIES.setProperty( NeatConfiguration.INITIAL_TOPOLOGY_ACTIVATION_KEY,
			SigmoidActivationFunction.NAME );
	TEST_PROPERTIES.setProperty( NeatConfiguration.INITIAL_TOPOLOGY_ACTIVATION_INPUT_KEY,
			LinearActivationFunction.NAME );
	TEST_PROPERTIES.setProperty( NeatConfiguration.INITIAL_TOPOLOGY_ACTIVATION_OUTPUT_KEY,
			ClampedLinearActivationFunction.NAME );
}

/**
 * ctor
 */
public NeatConfigurationTest() {
	this( NeatConfigurationTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public NeatConfigurationTest( String name ) {
	super( name );
}

/**
 * @return configuration object
 */
protected NeatConfiguration getNeatConfigUut() {
	return (NeatConfiguration) uut;
}

/**
 * @see junit.framework.TestCase#setUp()
 */
protected void setUp() throws Exception {
	java.util.Properties p = new java.util.Properties();
	p.putAll( TEST_PROPERTIES );
	PropertyConfigurator.configure( p );
	super.setUp();
}

/**
 * @see org.jgap.test.ConfigurationTest#initUut()
 */
protected void initUut() throws Exception {
	// clear previous stored configs and populations
	Reset reset = new Reset( TEST_PROPERTIES );
	reset.setUserInteraction( false );
	reset.reset();

	uut = new NeatConfiguration( TEST_PROPERTIES );
	( (NeatConfiguration) uut ).load();
}

/**
 * @see org.jgap.test.ConfigurationTest#doTestVirginConfiguration()
 */
protected void doTestVirginConfiguration() {
	assertValidConfig( (NeatConfiguration) uut, SURVIVAL_RATE, WEIGHT_MIN, WEIGHT_MAX );
}

/**
 * assert <code>config</code> object is valid and contains specified values
 * @param config
 * @param survivalRate
 * @param weightMin
 * @param weightMax
 */
protected static void assertValidConfig( NeatConfiguration config, float survivalRate,
		double weightMin, double weightMax ) {
	// values
	assertEquals( "wrong initial survival rate", survivalRate, config.getNaturalSelector()
			.getSurvivalRate(), 0.0f );
	assertEquals( "wrong initial weight min", weightMin, config.getMinConnectionWeight(), 0.0f );
	assertEquals( "wrong initial weight max", weightMax, config.getMaxConnectionWeight(), 0.0f );

	// reproduction ops
	assertEquals( "wrong # reproduction operators", 2, config.getReproductionOperators().size() );
	assertNotNull( "no crossover operator", config.getCrossoverOperator() );
	assertNotNull( "no clone operator", config.getCloneOperator() );
	Iterator it = config.getReproductionOperators().iterator();
	boolean hasClone = false;
	boolean hasCrossover = false;
	while ( it.hasNext() ) {
		ReproductionOperator repro = (ReproductionOperator) it.next();
		if ( repro instanceof NeatCrossoverReproductionOperator )
			hasCrossover = true;
		else if ( repro instanceof CloneReproductionOperator )
			hasClone = true;
		else
			fail( "unexpected reproduction operator: " + repro );
	}
	assertTrue( "missing crossover operator", hasCrossover );
	assertTrue( "missing clone operator", hasClone );

	// mutation ops
	assertEquals( "wrong # mutation operators", 5, config.getMutationOperators().size() );
	it = config.getMutationOperators().iterator();
	boolean hasAddConn = false;
	boolean hasRemoveConn = false;
	boolean hasPrune = false;
	boolean hasAddNeuron = false;
	boolean hasChangeWeight = false;
	while ( it.hasNext() ) {
		MutationOperator mutat = (MutationOperator) it.next();
		if ( mutat instanceof AddConnectionMutationOperator ) {
			float rate = ( (AddConnectionMutationOperator) mutat ).getMutationRate();
			float expected = AddConnectionMutationOperator.DEFAULT_MUTATE_RATE;
			assertEquals( "wrong add connection mutation rate", expected, rate, 0.0f );
			hasAddConn = true;
		}
		else if ( mutat instanceof RemoveConnectionMutationOperator ) {
			float rate = ( (RemoveConnectionMutationOperator) mutat ).getMutationRate();
			float expected = RemoveConnectionMutationOperator.DEFAULT_MUTATE_RATE;
			assertEquals( "wrong remove connection mutation rate", expected, rate, 0.0f );
			hasRemoveConn = true;
		}
		else if ( mutat instanceof AddNeuronMutationOperator ) {
			float rate = ( (AddNeuronMutationOperator) mutat ).getMutationRate();
			float expected = AddNeuronMutationOperator.DEFAULT_MUTATE_RATE;
			assertEquals( "wrong add neuron mutation rate", expected, rate, 0.0f );
			hasAddNeuron = true;
		}
		else if ( mutat instanceof WeightMutationOperator ) {
			float rate = ( (WeightMutationOperator) mutat ).getMutationRate();
			float expected = WeightMutationOperator.DEFAULT_MUTATE_RATE;
			assertEquals( "wrong weight mutation rate", expected, rate, 0.0f );
			hasChangeWeight = true;
		}
		else if ( mutat instanceof PruneMutationOperator ) {
			float rate = ( (PruneMutationOperator) mutat ).getMutationRate();
			float expected = PruneMutationOperator.DEFAULT_MUTATE_RATE;
			assertEquals( "wrong prune mutation rate", expected, rate, 0.0f );
			hasPrune = true;
		}
		else
			fail( "unexpected mutation operator: " + mutat );
	}
	assertTrue( "missing add connect operator", hasAddConn );
	assertTrue( "missing remove connect operator", hasRemoveConn );
	assertTrue( "missing add neuron operator", hasAddNeuron );
	assertTrue( "missing modify weight operator", hasChangeWeight );
	assertTrue( "missing prune operator", hasPrune );

	// fitness func
	assertNull( "should not have fitness function", config.getFitnessFunction() );

	// selector
	assertTrue( "wrong inner selector", config.getNaturalSelector() instanceof SimpleSelector );

	// event mgr
	assertNotNull( "no event mgr", config.getEventManager() );

	assertEquals( "pop size should be zero", NeatConfiguration.DEFAULT_POPUL_SIZE, config
			.getPopulationSize() );

	assertNotNull( "null id factory", config.getIdFactory() );
}

/**
 * assert <code>config</code> valid and locked with specified values
 * @param config
 * @param dimStimuli
 * @param dimHidden
 * @param dimResponse
 * @param baseId
 * @throws Exception
 */
protected static void assertValidLockedConfig( NeatConfiguration config, short dimStimuli,
		short dimHidden, short dimResponse, long baseId ) throws Exception {
	int expectedNumNeurons = dimStimuli + dimResponse;

	// id generation
	Long id1 = config.nextInnovationId();
	assertTrue( "bad innovation id", ( baseId + expectedNumNeurons ) <= id1.longValue() );
	Long id2 = config.nextInnovationId();
	assertTrue( "bad chrom id", id2.longValue() > id1.longValue() );
	assertEquals( "wrong innovation id", id2.longValue() + 1, config.nextInnovationId()
			.longValue() );
	assertEquals( "wrong innovation id", id2.longValue() + 2, config.nextInnovationId()
			.longValue() );

	// sample chrom
	assertNotNull( "should have sample chrom", config.getSampleChromosomeMaterial() );
	ChromosomeMaterial material = config.getSampleChromosomeMaterial();
	Chromosome c = new Chromosome( material, config.nextChromosomeId() );
	NeatChromosomeUtilityTest.assertValidInitialNeatChromosome( c, dimStimuli, dimHidden,
			dimResponse, true );
}

/**
 * @see org.jgap.test.ConfigurationTest#doTestLockedConfig()
 */
protected void doTestLockedConfig() throws Exception {
	assertValidLockedConfig( getNeatConfigUut(), DIM_STIMULI, DIM_HIDDEN, DIM_RESPONSE, BASE_ID );
	super.doTestLockedConfig();
}

/**
 * @throws InvalidConfigurationException
 * @see org.jgap.test.ConfigurationTest#doTestSampleChromosome(boolean)
 */
protected void doTestSampleChromosome( boolean shouldAllowLock )
		throws InvalidConfigurationException {
	NeatChromosomeUtilityTest.validate( uut.getSampleChromosomeMaterial().getAlleles() );
	doTestLockSettings( shouldAllowLock );
}

/**
 * test default values
 * @throws Exception
 */
public void testDefaults() throws Exception {
	NeatConfiguration config = new NeatConfiguration( TEST_PROPERTIES );
	assertValidConfig( config, NeatConfiguration.DEFAULT_SURVIVAL_RATE, WEIGHT_MIN, WEIGHT_MAX );
}

/**
 * test configuration with invalid survival rate set
 * @throws Exception
 */
public void testBadSurvivalRate() throws Exception {
	try {
		Properties props = (Properties) TEST_PROPERTIES.clone();
		props.setProperty( NeatConfiguration.SURVIVAL_RATE_KEY, "0.60" );
		uut = new NeatConfiguration( props );
		fail( "should have failed" );
	}
	catch ( InvalidConfigurationException e ) {
		// success
	}
	catch ( Throwable t ) {
		fail( "unexpected exception: " + t );
	}
}

/**
 * test innovation IDs
 * @throws Exception
 */
public void testIds() throws Exception {
	// neuron id
	Long neuronId1 = getNeatConfigUut().newNeuronAllele( new Long( 1 ) ).getInnovationId();
	Long neuronId2 = getNeatConfigUut().newNeuronAllele( new Long( 1 ) ).getInnovationId();
	assertEquals( "ids should be same", neuronId1, neuronId2 );
	Long neuronId3 = getNeatConfigUut().newNeuronAllele( new Long( 3 ) ).getInnovationId();
	assertTrue( "ids should be different 1", neuronId2.longValue() != neuronId3.longValue() );
	Long neuronId4 = getNeatConfigUut().newNeuronAllele( new Long( 4 ) ).getInnovationId();
	assertTrue( "ids should be different 2", ( neuronId2.longValue() != neuronId4.longValue() )
			&& ( neuronId3.longValue() != neuronId4.longValue() ) );
	Long neuronId5 = getNeatConfigUut().newNeuronAllele( new Long( 2 ) ).getInnovationId();
	assertTrue( "ids should be different 3", ( neuronId2 != neuronId5 )
			&& ( neuronId3 != neuronId5 ) && ( neuronId4 != neuronId5 ) );

	// connection id
	Long connId1 = getNeatConfigUut().newConnectionAllele( new Long( 1 ), new Long( 2 ) )
			.getInnovationId();
	Long connId2 = getNeatConfigUut().newConnectionAllele( new Long( 1 ), new Long( 2 ) )
			.getInnovationId();
	assertEquals( "ids should be same", connId1, connId2 );
	Long connId3 = getNeatConfigUut().newConnectionAllele( new Long( 1 ), new Long( 3 ) )
			.getInnovationId();
	assertTrue( "ids should be different 1", connId2.longValue() != connId3.longValue() );
	Long connId4 = getNeatConfigUut().newConnectionAllele( new Long( 4 ), new Long( 2 ) )
			.getInnovationId();
	assertTrue( "ids should be different 2", ( connId2.longValue() != connId4.longValue() )
			&& ( connId3.longValue() != connId4.longValue() ) );
	Long connId5 = getNeatConfigUut().newConnectionAllele( new Long( 2 ), new Long( 4 ) )
			.getInnovationId();
	assertTrue( "ids should be different 3", ( connId2.longValue() != connId5.longValue() )
			&& ( connId3.longValue() != connId5.longValue() )
			&& ( connId4.longValue() != connId5.longValue() ) );

	// save and restore
	getNeatConfigUut().store();
	NeatConfiguration loadedConfig = new NeatConfiguration( TEST_PROPERTIES );
	loadedConfig.load();
	neuronId2 = loadedConfig.newNeuronAllele( new Long( 1 ) ).getInnovationId();
	assertEquals( "ids should be same", neuronId1, neuronId2 );
	connId2 = loadedConfig.newConnectionAllele( new Long( 1 ), new Long( 2 ) ).getInnovationId();
	assertEquals( "ids should be same", connId1, connId2 );
}

/**
 * test gene and chromosome operators
 * 
 * @throws Exception
 */
public void testGenes() throws Exception {
	// new allele
	NeuronAllele neuronAllele = getNeatConfigUut().newNeuronAllele( NeuronType.INPUT );
	assertEquals( "new gene: wrong input activation function", ActivationFunctionType.LINEAR,
			neuronAllele.getActivationType() );
	neuronAllele = getNeatConfigUut().newNeuronAllele( NeuronType.HIDDEN );
	assertEquals( "new gene: wrong hidden activation function", ActivationFunctionType.SIGMOID,
			neuronAllele.getActivationType() );
	neuronAllele = getNeatConfigUut().newNeuronAllele( NeuronType.OUTPUT );
	assertEquals( "new gene: wrong output activation function",
			ActivationFunctionType.CLAMPED_LINEAR, neuronAllele.getActivationType() );

	// sample chromosome material
	Set alleles = getNeatConfigUut().getSampleChromosomeMaterial().getAlleles();
	assertEquals( "sample chrom: wrong number neuron genes", DIM_STIMULI + DIM_HIDDEN
			+ DIM_RESPONSE, NeatChromosomeUtility.getNeuronList( alleles ).size() );
	assertEquals( "sample chrom: wrong number input neuron genes", DIM_STIMULI,
			NeatChromosomeUtility.getNeuronList( alleles, NeuronType.INPUT ).size() );
	assertEquals( "sample chrom: wrong number hidden neuron genes", DIM_HIDDEN,
			NeatChromosomeUtility.getNeuronList( alleles, NeuronType.HIDDEN ).size() );
	assertEquals( "sample chrom: wrong number output neuron genes", DIM_RESPONSE,
			NeatChromosomeUtility.getNeuronList( alleles, NeuronType.OUTPUT ).size() );
	assertEquals( "sample chrom: wrong number connection genes", ( DIM_STIMULI * DIM_HIDDEN )
			+ ( DIM_HIDDEN * DIM_RESPONSE ), NeatChromosomeUtility.getConnectionList( alleles )
			.size() );
	Iterator it = alleles.iterator();
	while ( it.hasNext() ) {
		Allele allele = (Allele) it.next();
		if ( allele instanceof NeuronAllele ) {
			neuronAllele = (NeuronAllele) allele;
			if ( neuronAllele.isType( NeuronType.INPUT ) )
				assertEquals( "wrong input activation function", ActivationFunctionType.LINEAR,
						neuronAllele.getActivationType() );
			else if ( neuronAllele.isType( NeuronType.OUTPUT ) )
				assertEquals( "wrong input activation function", ActivationFunctionType.CLAMPED_LINEAR,
						neuronAllele.getActivationType() );
			else
				assertEquals( "wrong hidden activation function", ActivationFunctionType.SIGMOID,
						neuronAllele.getActivationType() );
		}
	}
}
}
