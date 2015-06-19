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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import junit.framework.TestCase;

import org.jgap.Allele;
import org.jgap.Chromosome;
import org.jgap.ChromosomeMaterial;
import org.jgap.test.DummyFitnessFunction;

import com.anji.integration.AnjiActivator;
import com.anji.integration.AnjiNetTranscriber;
import com.anji.neat.ConnectionAllele;
import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.NeuronAllele;
import com.anji.nn.ActivationFunctionType;
import com.anji.nn.RecurrencyPolicy;
import com.anji.nn.TanhActivationFunction;
import com.anji.util.Properties;

/**
 * @author Philip Tucker
 */
public class AnjiTranscriberTest extends TestCase {

private final static String PROP_FILE_NAME = "test.properties";

private final static String nonLinearLayerType = TanhActivationFunction.NAME;

private static NeatConfiguration buildConfig( short dimStimuli, short dimResponse,
		ActivationFunctionType act ) throws Exception {
	Properties props = new Properties( PROP_FILE_NAME );
	props.setProperty( NeatConfiguration.STIMULUS_SIZE_KEY, "" + dimStimuli );
	props.setProperty( NeatConfiguration.RESPONSE_SIZE_KEY, "" + dimResponse );
	props.setProperty( NeatConfiguration.INITIAL_TOPOLOGY_ACTIVATION_KEY, act.toString() );

	// config
	NeatConfiguration config = new NeatConfiguration( props );
	config.getRandomGenerator().setSeed( 0 );
	config.setFitnessFunction( new DummyFitnessFunction( config.getRandomGenerator() ) );
	config.setPopulationSize( 100 );
	config.lockSettings();
	config.load();

	return config;
}

/**
 * ctor
 */
public AnjiTranscriberTest() {
	this( AnjiTranscriberTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public AnjiTranscriberTest( String name ) {
	super( name );
}

/**
 * test
 * @throws Exception
 */
public void testRecurrent() throws Exception {
	doTestComplex( true );
}

/**
 * test
 * @throws Exception
 */
public void testNonrecurrent() throws Exception {
	doTestComplex( false );
}

/**
 * test
 * @throws Exception
 */
public void testSimpleNonrecurrent() throws Exception {
	RecurrencyPolicy recurrencyPolicy = RecurrencyPolicy.DISALLOWED;

	short dimStimuli = 3;
	short dimResponse = 1;
	ActivationFunctionType act = ActivationFunctionType.valueOf( nonLinearLayerType );
	NeatConfiguration config = buildConfig( dimStimuli, dimResponse, act );

	ChromosomeMaterial material = NeatChromosomeUtility.newSampleChromosomeMaterial( dimStimuli,
			(short) 0, dimResponse, config, true );
	Chromosome genotype = new Chromosome( material, config.nextChromosomeId() );

	// genotype
	AnjiNetTranscriber trans = new AnjiNetTranscriber( recurrencyPolicy );

	// phenotype
	AnjiActivator nnet = new AnjiActivator( trans.newAnjiNet( genotype ), 1 );

	assertNeuralNetValid( "testSimple", dimStimuli, dimResponse, nnet );

	double[] expectedOutput = { 0.27289794574314996d };
	double[] output = verifyActivation( nnet, dimStimuli, dimResponse );
	for ( int i = 0; i < dimResponse; ++i ) {
		assertEquals( "wrong output " + i, expectedOutput[ i ], output[ i ], 0.0d );
	}

	// genotype.cleanup();
}

/**
 * test
 * @throws Exception
 */
public void testMediumRecurrent() throws Exception {
	RecurrencyPolicy recurrencyPolicy = RecurrencyPolicy.BEST_GUESS;

	short dimStimuli = 9;
	short dimResponse = 3;
	ActivationFunctionType act = ActivationFunctionType.valueOf( nonLinearLayerType );
	NeatConfiguration config = buildConfig( dimStimuli, dimResponse, act );

	ChromosomeMaterial material = NeatChromosomeUtility.newSampleChromosomeMaterial( dimStimuli,
			(short) 0, dimResponse, config, true );
	Chromosome genotype = new Chromosome( material, config.nextChromosomeId() );

	// genotype
	AnjiNetTranscriber trans = new AnjiNetTranscriber( recurrencyPolicy );

	// phenotype
	AnjiActivator nnet = new AnjiActivator( trans.newAnjiNet( genotype ), 1 );

	assertNeuralNetValid( "testSimple", dimStimuli, dimResponse, nnet );

	double[] expectedOutput = { 0.11113760755739621d, 0.5914805112917818d, -0.8655808827388736d };
	double[] output = verifyActivation( nnet, dimStimuli, dimResponse );
	for ( int i = 0; i < dimResponse; ++i ) {
		assertEquals( "wrong output " + i, expectedOutput[ i ], output[ i ], 0.0d );
	}

	// genotype.cleanup();
}

private void assertNeuralNetValid( String assertPrefix, short dimStimuli, short dimResponse,
		AnjiActivator nnet ) {
	assertEquals( assertPrefix + ": bad in rows", dimStimuli, nnet.getInputDimension() );
	assertEquals( assertPrefix + ": bad out rows", dimResponse, nnet.getOutputDimension() );
}

/**
 * test
 * @throws Exception
 */
public void testBadSrcConnection() throws Exception {
	doTestBadConnection( true );
}

/**
 * test
 * @throws Exception
 */
public void testBadDestConnection() throws Exception {
	doTestBadConnection( false );
}

private void doTestBadConnection( boolean badSrc ) throws Exception {
	short dimStimuli = 12;
	short dimResponse = 5;
	ActivationFunctionType act = ActivationFunctionType.valueOf( nonLinearLayerType );
	NeatConfiguration config = buildConfig( dimStimuli, dimResponse, act );

	ChromosomeMaterial material = NeatChromosomeUtility.newSampleChromosomeMaterial( dimStimuli,
			(short) 0, dimResponse, config, true );
	Chromosome genotype = new Chromosome( material, config.nextChromosomeId() );

	// add bad connection
	SortedSet oldAlleles = genotype.getAlleles();
	List newAlleles = new ArrayList( oldAlleles.size() + 1 );
	Long sampleNeuronId = null;
	Iterator iter = oldAlleles.iterator();
	while ( iter.hasNext() ) {
		Allele allele = (Allele) iter.next();
		newAlleles.add( allele );
		if ( allele instanceof NeuronAllele ) {
			NeuronAllele neuronAllele = (NeuronAllele) allele;
			sampleNeuronId = neuronAllele.getInnovationId();
		}
	}
	ConnectionAllele connAllele = null;
	if ( badSrc )
		connAllele = config.newConnectionAllele( config.nextInnovationId(), sampleNeuronId );
	else
		connAllele = config.newConnectionAllele( sampleNeuronId, config.nextInnovationId() );
	newAlleles.add( connAllele );
	// genotype.cleanup();
	//	genotype = new Chromosome( new ChromosomeMaterial( newAlleles ), config.nextChromosomeId()
	// );
	//
	//	AnjiNetTranscriber trans = new AnjiNetTranscriber( RecurrencyPolicy.BEST_GUESS );
	//
	//	try {
	//		trans.transcribe( genotype );
	//		fail( "should have failed on connection with bad " + ( badSrc ? "source" : "destination" )
	// );
	//	}
	//	catch ( TranscriberException e ) {
	//		// success
	//	}
	// genotype.cleanup();
}

private void doTestComplex( boolean isRecurrent ) throws Exception {
	// note: each layer must have at least 2 neurons
	short dimStimuli = 9;
	short dimHidden1 = 12;
	short dimHidden2 = 6;
	short dimResponse = 4;
	NeatConfiguration config = buildConfig( dimStimuli, dimResponse,
			ActivationFunctionType.SIGMOID );
	TestChromosomeFactory factory = new TestChromosomeFactory( config );

	List alleles = factory.newAlleles( dimStimuli, dimResponse, dimHidden1, dimHidden2,
			isRecurrent );
	Chromosome genotype = new Chromosome( new ChromosomeMaterial( alleles ), config
			.nextChromosomeId() );

	// legal transcription
	verifyComplexTranscription( genotype, dimStimuli, dimResponse, isRecurrent );

	// genotype.cleanup();
}

private double[] verifyComplexTranscription( Chromosome genotype, short dimStimuli,
		short dimResponse, boolean isRecurrent ) throws Exception {
	RecurrencyPolicy recurrencyPolicy = RecurrencyPolicy.BEST_GUESS;
	AnjiNetTranscriber trans = new AnjiNetTranscriber( recurrencyPolicy );

	AnjiActivator nnet = new AnjiActivator( trans.newAnjiNet( genotype ), 1 );

	if ( isRecurrent )
		assertEquals( "recurrency wrong", true, nnet.isRecurrent() );
	assertNeuralNetValid( "doTestComplex", dimStimuli, dimResponse, nnet );
	double[] pattern = verifyActivation( nnet, dimStimuli, dimResponse );
	return pattern;
}

// TODO - do multiple inputs to check for impact of recurrent connections
private static double[] verifyActivation( AnjiActivator nnet, short dimStimuli,
		short dimResponse ) {
	// activation flow
	double[] inVal = new double[ dimStimuli ];
	Arrays.fill( inVal, 0.5d );

	// Read the next pattern and print it
	double[] pattern = nnet.next( inVal );
	assertEquals( "wrong dimension for output pattern", dimResponse, pattern.length );
	System.out.print( "Output Pattern == " );
	for ( int i = 0; i < pattern.length; ++i )
		System.out.print( pattern[ i ] + " " );
	System.out.println();

	return pattern;
}

}
