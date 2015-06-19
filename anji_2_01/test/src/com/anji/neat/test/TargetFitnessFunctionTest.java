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
package com.anji.neat.test;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.jgap.Chromosome;
import org.jgap.ChromosomeMaterial;
import org.jgap.InvalidConfigurationException;

import com.anji.integration.ActivatorTranscriber;
import com.anji.integration.ExponentialTargetFitnessFunction;
import com.anji.integration.TargetFitnessFunction;
import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.NeatTargetFitnessFunction;
import com.anji.nn.RecurrencyPolicy;
import com.anji.util.Properties;

/**
 * @author Philip Tucker
 */
public class TargetFitnessFunctionTest extends TestCase {

private final static short RECURRENT_CYCLES = 10;

private final static String PROP_FILE_NAME = "test_tanh.properties";

private Properties props;

/**
 * ctor
 */
public TargetFitnessFunctionTest() {
	this( TargetFitnessFunctionTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public TargetFitnessFunctionTest( String name ) {
	super( name );
}

/**
 * initialization
 * @see junit.framework.TestCase#setUp()
 */
public void setUp() throws IOException {
	props = new Properties( PROP_FILE_NAME );
	props.setProperty( NeatConfiguration.STIMULUS_SIZE_KEY, "" + 3 );
	props.setProperty( NeatConfiguration.RESPONSE_SIZE_KEY, "" + 1 );
}

/**
 * test
 * @throws InvalidConfigurationException
 */
public void testXorExponentialFitness() throws InvalidConfigurationException {
	doTestXor( true, (short) 1 );
}

/**
 * test
 * @throws InvalidConfigurationException
 */
public void testXorNeatStyleFitness() throws InvalidConfigurationException {
	doTestXor( false, (short) 1 );
}

/**
 * test
 * @throws InvalidConfigurationException
 */
public void testXorRecurrentExponentialFitness() throws InvalidConfigurationException {
	doTestXor( true, RECURRENT_CYCLES );
}

/**
 * test
 * @throws InvalidConfigurationException
 */
public void testXorRecurrentNeatStyleFitness() throws InvalidConfigurationException {
	doTestXor( false, RECURRENT_CYCLES );
}

private void doTestXor( boolean exponentialFitness, short recurrentCycles )
		throws InvalidConfigurationException {
	if ( recurrentCycles > 1 ) {
		props.setProperty( ActivatorTranscriber.RECURRENT_CYCLES_KEY, "" + recurrentCycles );
		props.setProperty( RecurrencyPolicy.KEY, RecurrencyPolicy.BEST_GUESS.toString() );
	}
	else {
		props.setProperty( ActivatorTranscriber.RECURRENT_CYCLES_KEY, "1" );
		props.setProperty( RecurrencyPolicy.KEY, RecurrencyPolicy.DISALLOWED.toString() );
	}
	NeatConfiguration config = TestNeatConfigurationFactory.getInstance().buildConfig( props );

	TargetFitnessFunction uut = null;
	if ( exponentialFitness )
		uut = new ExponentialTargetFitnessFunction();
	else
		uut = new NeatTargetFitnessFunction();
	uut.init( props );

	TestChromosomeFactory factory = new TestChromosomeFactory( config );

	ArrayList chroms = new ArrayList( 3 );

	// sample chromosome
	ChromosomeMaterial material = NeatChromosomeUtility.newSampleChromosomeMaterial( (short) 3,
			(short) 0, (short) 1, config, true );
	chroms.add( new Chromosome( material, config.nextChromosomeId() ) );

	// test chromosome
	chroms.add( factory.newChromosome( recurrentCycles > 1, 3, 1 ) );

	// successful chromosome
	chroms.add( factory.newSolveXorChromosome() );

	uut.evaluate( chroms );

	Chromosome c = (Chromosome) chroms.get( 0 );
	assertEquals( "bad fitness 1", exponentialFitness ? 46428297 : 76968379, c.getFitnessValue() );

	c = (Chromosome) chroms.get( 1 );
	int expected = 0;
	if ( recurrentCycles > 1 )
		expected = exponentialFitness ? 1436650 : 68981671;
	else
		expected = exponentialFitness ? 456526 : 62948578;
	assertEquals( "bad fitness 2", expected, c.getFitnessValue() );

	c = (Chromosome) chroms.get( 2 );
	assertEquals( "bad fitness 3",
			exponentialFitness ? TestChromosomeFactory.EXPONENTIAL_XOR_FITNESS_RECURRENT
					: TestChromosomeFactory.NEAT_XOR_FITNESS_RECURRENT, c.getFitnessValue() );
}

}
