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
 * created by Philip Tucker on Apr 10, 2003
 */
package com.anji.integration.test;

import junit.framework.TestCase;

import org.jgap.Chromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.test.DummyFitnessFunction;

import com.anji.integration.Activator;
import com.anji.integration.ActivatorTranscriber;
import com.anji.integration.TargetFitnessFunction;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.test.TestChromosomeFactory;
import com.anji.nn.RecurrencyPolicy;
import com.anji.util.Properties;

/**
 * @author Philip Tucker
 */
public class ActivatorTest extends TestCase {

private final static double[][] EXPECTED_RESPONSE_TANH = { { -0.3848934355707019d },
		{ 0.17773988640608862d }, { 0.17773988640608862d }, { -0.3848934355707019d } };

/*
 * private final static double[][] EXPECTED_RESPONSE_RECURRENT_TANH = { { -0.026519725534001726 }, {
 * 0.0434887992264803 }, { 0.04348879959623497 }, { -0.035741994680771993 } };
 */

private Properties propsTanh;

private double[][] stimuliTanh = null;

private TestChromosomeFactory chromFactory = null;

/**
 * ctor
 */
public ActivatorTest() {
	this( ActivatorTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public ActivatorTest( String name ) {
	super( name );
}

private NeatConfiguration buildConfig( short dimStimuli, short dimResponse )
		throws InvalidConfigurationException {
	Properties props = new Properties( propsTanh );
	props.setProperty( NeatConfiguration.STIMULUS_SIZE_KEY, "" + dimStimuli );
	props.setProperty( NeatConfiguration.RESPONSE_SIZE_KEY, "" + dimResponse );
	NeatConfiguration config = new NeatConfiguration( props );
	config.getRandomGenerator().setSeed( 0 );

	config.setFitnessFunction( new DummyFitnessFunction( config.getRandomGenerator() ) );
	config.setPopulationSize( 100 );
	config.lockSettings();

	config.load();

	return config;
}

/**
 * initialization
 * @see junit.framework.TestCase#setUp()
 */
public void setUp() throws Exception {
	propsTanh = new Properties( "test_tanh.properties" );
	stimuliTanh = Properties.loadArrayFromFile( propsTanh
			.getResourceProperty( TargetFitnessFunction.STIMULI_FILE_NAME_KEY ) );
	chromFactory = new TestChromosomeFactory( buildConfig( (short) 3, (short) 1 ) );
}

private void assertEquals( String msg, double[][] expected, double[][] actual ) {
	assertEquals( msg + ": wrong # rows", expected.length, actual.length );
	for ( int i = 0; i < expected.length; ++i ) {
		assertEquals( msg + ": wrong # cols in row " + i, expected[ i ].length, actual[ i ].length );
		for ( int j = 0; j < expected[ i ].length; ++j ) {
			assertEquals( msg + ": wrong value at [" + i + "," + j + "]", expected[ i ][ j ],
					actual[ i ][ j ], 0.0d );
		}
	}
}

//public void testJooneXorTanh() throws Exception {
//	Properties props = new Properties();
//	props.setProperty( RecurrencyPolicy.KEY, RecurrencyPolicy.BEST_GUESS.toString() );
//	props.setProperty( ActivatorTranscriber.RECURRENT_CYCLES_KEY, "1" );
//	ActivatorTranscriber.getInstance().init( props );
//	
//	SimpleNeuralNet nnet = SimpleNeuralNetTest.newNeuralNet( false, true );
//
//	Activator uut = ActivatorTranscriber.getInstance().newActivator( nnet );
//	double[][] response = uut.next( stimuliTanh );
//	assertEquals( "xor response", EXPECTED_RESPONSE_TANH, response );
//}
//
//public void testJooneXorMultiCyclesTanh() throws Exception {
//	Properties props = new Properties();
//	props.setProperty( ActivatorTranscriber.RECURRENT_CYCLES_KEY,
//		"" + SimpleNeuralNetTest.NUM_RECURRENT_CYCLES );
//	ActivatorTranscriber.getInstance().init( props );
//
//	SimpleNeuralNet nnet = SimpleNeuralNetTest.newNeuralNet( false, true );
//
//	Activator uut = ActivatorTranscriber.getInstance().newActivator( nnet );
//	double[][] response = uut.next( stimuliTanh );
//	assertEquals( "xor response, multi cycle", EXPECTED_RESPONSE_TANH, response );
//}
//
//public void testJooneXorRecurrentMultiCyclesTanh() throws Exception {
//	Properties props = new Properties();
//	props.setProperty( ActivatorTranscriber.RECURRENT_CYCLES_KEY, "10" );
//	ActivatorTranscriber.getInstance().init( props );
//
//	SimpleNeuralNet nnet = SimpleNeuralNetTest.newNeuralNet( true, true );
//
//	Activator uut = ActivatorTranscriber.getInstance().newActivator( nnet );
//	double[][] response = uut.next( stimuliTanh );
//	assertEquals( "xor response, recurrent", EXPECTED_RESPONSE_RECURRENT_TANH, response );
//}

/**
 * test
 * @throws Exception
 */
public void testAnjiXorTanhRecurrent() throws Exception {
	doTestAnjiXorTanh( true );
}

/**
 * test
 * @throws Exception
 */
public void testAnjiXorTanhNonrecurrent() throws Exception {
	doTestAnjiXorTanh( false );
}

private void doTestAnjiXorTanh( boolean recurrent ) throws Exception {
	// best guess
	Properties props = new Properties();
	if ( recurrent ) {
		props.setProperty( RecurrencyPolicy.KEY, RecurrencyPolicy.BEST_GUESS.toString() );
		props.setProperty( ActivatorTranscriber.RECURRENT_CYCLES_KEY, "3" );
	}
	else {
		props.setProperty( RecurrencyPolicy.KEY, RecurrencyPolicy.DISALLOWED.toString() );
		props.setProperty( ActivatorTranscriber.RECURRENT_CYCLES_KEY, "1" );
	}
	props.setProperty( ActivatorTranscriber.TYPE_KEY, ActivatorTranscriber.ANJI_TYPE );

	Chromosome c = chromFactory.newSolveXorChromosome();
	ActivatorTranscriber activatorFactory = (ActivatorTranscriber) props
			.newObjectProperty( ActivatorTranscriber.class );
	Activator uut = activatorFactory.newActivator( c );
	double[][] response = uut.next( stimuliTanh );
	assertEquals( "xor response, multi cycle", EXPECTED_RESPONSE_TANH, response );

	// disallowed
	props.setProperty( RecurrencyPolicy.KEY, RecurrencyPolicy.DISALLOWED.toString() );
	activatorFactory = (ActivatorTranscriber) props.newObjectProperty( ActivatorTranscriber.class );

	uut = activatorFactory.newActivator( c );
	response = uut.next( stimuliTanh );
	assertEquals( "xor response, multi cycle", EXPECTED_RESPONSE_TANH, response );

	// lazy
	props.setProperty( RecurrencyPolicy.KEY, RecurrencyPolicy.LAZY.toString() );
	props.setProperty( ActivatorTranscriber.RECURRENT_CYCLES_KEY, "3" );
	activatorFactory = (ActivatorTranscriber) props.newObjectProperty( ActivatorTranscriber.class );

	uut = activatorFactory.newActivator( c );
	response = uut.next( stimuliTanh );
	assertEquals( "xor response, multi cycle", EXPECTED_RESPONSE_TANH, response );
}

}
