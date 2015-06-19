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
 * created by Philip Tucker on Jul 25, 2004
 */

package com.anji.nn.test;

import junit.framework.TestCase;

import org.jgap.Chromosome;
import org.jgap.ChromosomeMaterial;
import org.jgap.test.DummyFitnessFunction;

import com.anji.integration.AnjiNetTranscriber;
import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeatConfiguration;
import com.anji.nn.ActivationFunctionType;
import com.anji.nn.AnjiNet;
import com.anji.nn.RecurrencyPolicy;
import com.anji.util.Properties;
import com.anji.util.Reset;

/**
 * @author Philip Tucker
 */
public class AnjiNetTest extends TestCase {

private static String PROP_FILE = "test.properties";

private NeatConfiguration config;

private ActivationFunctionType act;

/**
 * ctor
 */
public AnjiNetTest() {
	this( AnjiNetTest.class.toString() );
}

/**
 * ctor
 * @param arg0
 */
public AnjiNetTest( String arg0 ) {
	super( arg0 );
}

/**
 * initialization
 * @see junit.framework.TestCase#setUp()
 */
protected void setUp() throws Exception {
	Properties props = new Properties( PROP_FILE );
	props.setProperty( NeatConfiguration.STIMULUS_SIZE_KEY, "" + 1 );
	props.setProperty( NeatConfiguration.RESPONSE_SIZE_KEY, "" + 1 );

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
 * @throws Exception
 */
public void testCalculateCost() throws Exception {
	ChromosomeMaterial cMat = NeatChromosomeUtility.newSampleChromosomeMaterial( (short) 5,
			(short) 0, (short) 5, config, false );
	Chromosome c = new Chromosome( cMat, config.nextChromosomeId() );
	AnjiNetTranscriber t = new AnjiNetTranscriber( RecurrencyPolicy.BEST_GUESS );
	AnjiNet net = t.newAnjiNet( c );

	// 497 == sigmoid cost, 315 == neuron cost
	assertEquals( "wrong cost 1", 10 * ( 497 + 315 ), net.cost() );

	cMat = NeatChromosomeUtility.newSampleChromosomeMaterial( (short) 10, (short) 10, (short) 10,
			config, true );
	c = new Chromosome( cMat, config.nextChromosomeId() );
	t = new AnjiNetTranscriber( RecurrencyPolicy.BEST_GUESS );
	net = t.newAnjiNet( c );

	assertEquals( "wrong cost 2", 58760, net.cost() );
}
}
