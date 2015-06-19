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
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import junit.framework.TestCase;

import org.jgap.Allele;
import org.jgap.Chromosome;
import org.jgap.ChromosomeMaterial;
import org.jgap.test.DummyFitnessFunction;

import com.anji.integration.Activator;
import com.anji.integration.AnjiActivator;
import com.anji.integration.AnjiNetTranscriber;
import com.anji.integration.Transcriber;
import com.anji.neat.ConnectionAllele;
import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.NeuronAllele;
import com.anji.nn.ActivationFunctionType;
import com.anji.nn.AnjiNet;
import com.anji.nn.RecurrencyPolicy;
import com.anji.nn.TanhActivationFunction;
import com.anji.util.Properties;

/**
 * @author Philip Tucker
 */
public class TranscriberTest extends TestCase {

private final static String PROP_FILE_NAME = "test.properties";

private final static String activationType = TanhActivationFunction.NAME;

private static NeatConfiguration buildConfig( short dimStimuli, short dimResponse,
		ActivationFunctionType act ) throws Exception {
	Properties props = new Properties( PROP_FILE_NAME );
	props.setProperty( NeatConfiguration.STIMULUS_SIZE_KEY, "" + dimStimuli );
	props.setProperty( NeatConfiguration.RESPONSE_SIZE_KEY, "" + dimResponse );
	props.setProperty( NeatConfiguration.INITIAL_TOPOLOGY_ACTIVATION_KEY, act.toString() );

	//config
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
public TranscriberTest() {
	this( TranscriberTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public TranscriberTest( String name ) {
	super( name );
}

//public void testRecurrentJoone() throws Exception {
//	doTestComplex(true);
//}
//
//public void testComplexJoone() throws Exception {
//	doTestComplex(false);
//}

//public void testSimpleJoone() throws Exception {
//	doTestSimple( true );
//}

/**
 * test
 * @throws Exception
 */
public void testSimpleAnji() throws Exception {
	doTestSimple( false );
}

/**
 * 
 * @param joone
 * @throws Exception
 */
public void doTestSimple( boolean joone ) throws Exception {
	short dimStimuli = 22;
	short dimHidden = 3;
	short dimResponse = 9;
	ActivationFunctionType act = ActivationFunctionType.valueOf( activationType );
	NeatConfiguration config = buildConfig( dimStimuli, dimResponse, act );

	ChromosomeMaterial material = NeatChromosomeUtility.newSampleChromosomeMaterial( dimStimuli,
			dimHidden, dimResponse, config, true );
	Chromosome genotype = new Chromosome( material, config.nextChromosomeId() );

	// genotype
	Transcriber trans = ( joone ?
	//		(Transcriber) TranscriberFactory.getInstance().newJooneTranscriber( genotype ) :
			null : (Transcriber) new AnjiNetTranscriber( RecurrencyPolicy.BEST_GUESS ) );

	// phenotype
	Activator nnet = new AnjiActivator( (AnjiNet) trans.transcribe( genotype ), 1 );
	//	assertNeuralNetValid( "testSimple", dimStimuli, dimResponse, (short) 2, nnet,
	// SigmoidLayer.class );
	verifyActivation( nnet, dimStimuli, dimResponse );
}

//protected static void assertNeuralNetValid(String assertPrefix, short dimStimuli,
//	short dimResponse, short numLayers, SimpleNeuralNet nnet, Class aLayerClass)
//{
//	assertEquals(assertPrefix + ": bad in rows", dimStimuli, nnet.getInputLayer().getRows());
//	assertEquals(assertPrefix + ": bad out rows", dimResponse,
// nnet.getOutputLayer().getRows());
//	assertEquals(assertPrefix + ": bad # hidden layers", numLayers, nnet.getLayers().size());
//	Iterator it = nnet.getLayers().iterator();
//	while (it.hasNext()) {
//		Layer layer = (Layer) it.next();
//		assertEquals(assertPrefix + ": wrong layer type", aLayerClass, layer.getClass());
//	}
//}

/**
 * test
 * @throws Exception
 */
public void testBadSrcConnectionAnji() throws Exception {
	doTestBadConnection( true, false );
}

/**
 * test
 * @throws Exception
 */
public void testBadDestConnectionAnji() throws Exception {
	doTestBadConnection( false, false );
}

//public void testBadSrcConnectionJoone() throws Exception {
//	doTestBadConnection( true, true );
//}
//
//public void testBadDestConnectionJoone() throws Exception {
//	doTestBadConnection( false, true );
//}

private void doTestBadConnection( boolean badSrc, boolean joone ) throws Exception {
	short dimStimuli = 12;
	short dimHidden = 0;
	short dimResponse = 5;
	ActivationFunctionType act = ActivationFunctionType.valueOf( activationType );
	NeatConfiguration config = buildConfig( dimStimuli, dimResponse, act );

	ChromosomeMaterial material = NeatChromosomeUtility.newSampleChromosomeMaterial( dimStimuli,
			dimHidden, dimResponse, config, true );
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
			NeuronAllele nAllele = (NeuronAllele) allele;
			sampleNeuronId = nAllele.getInnovationId();
		}
	}
	ConnectionAllele cAllele = null;
	if ( badSrc )
		cAllele = config.newConnectionAllele( config.nextInnovationId(), sampleNeuronId );
	else
		cAllele = config.newConnectionAllele( sampleNeuronId, config.nextInnovationId() );
	newAlleles.add( cAllele );
	genotype = new Chromosome( new ChromosomeMaterial( newAlleles ), config.nextChromosomeId() );

	//	Transcriber trans = ( joone ?
	//	// (Transcriber) TranscriberFactory.getInstance().newJooneTranscriber( genotype ) :
	//			null : (Transcriber) new AnjiNetTranscriber( RecurrencyPolicy.BEST_GUESS ) );
	//
	//	try {
	//		trans.transcribe( genotype );
	//		fail( "should have failed on connection with bad " + ( badSrc ? "source" : "destination" )
	// );
	//	}
	//	catch ( TranscriberException e ) {
	//		// success
	//	}
}

// TODO - do multiple inputs to check for impact of recurrent connections
private static double[] verifyActivation( Activator nnet, short dimStimuli, short dimResponse ) {
	// activation flow
	double[] inVal = new double[ dimStimuli ];
	for ( int i = 0; i < dimStimuli; ++i )
		inVal[ i ] = 0.5d;

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
