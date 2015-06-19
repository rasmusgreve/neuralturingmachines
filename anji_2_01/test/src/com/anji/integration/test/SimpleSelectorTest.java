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
 * created by Philip Tucker on Jan 22, 2004
 */
package com.anji.integration.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.jgap.Chromosome;

import com.anji.integration.SimpleSelector;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.NeatTargetFitnessFunction;
import com.anji.neat.test.TestChromosomeFactory;
import com.anji.util.Properties;

/**
 * @author Philip Tucker
 */
public class SimpleSelectorTest extends TestCase {

private final static String PROP_FILE_NAME = "test.properties";

/**
 * ctor
 */
public SimpleSelectorTest() {
	this( SimpleSelectorTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public SimpleSelectorTest( String name ) {
	super( name );
}

/**
 * test selector
 * @throws Exception
 */
public void testSimpleSelector() throws Exception {
	SimpleSelector uut = new SimpleSelector();
	uut.setSurvivalRate( 0.667f );

	Properties props = new Properties( PROP_FILE_NAME );
	props.setProperty( NeatConfiguration.STIMULUS_SIZE_KEY, "" + 3 );
	props.setProperty( NeatConfiguration.RESPONSE_SIZE_KEY, "" + 3 );
	NeatConfiguration config = new NeatConfiguration( props );
	config.load();
	config.setBulkFitnessFunction( new NeatTargetFitnessFunction() );
	TestChromosomeFactory factory = new TestChromosomeFactory( config );

	List chroms = new ArrayList();
	Chromosome c1 = factory.newChromosome( false );
	Chromosome c2 = factory.newChromosome( false );
	Chromosome c3 = factory.newChromosome( false );
	c1.setFitnessValue( 100 );
	c2.setFitnessValue( 200 );
	c3.setFitnessValue( 300 );
	chroms.add( c1 );
	chroms.add( c2 );
	chroms.add( c3 );
	uut.add( config, chroms );

	List survivors = uut.select( config );
	assertEquals( "wrong # survivors", (int) ( chroms.size() * uut.getSurvivalRate() ), survivors
			.size() );
	assertTrue( "c3 didn't survive", survivors.contains( c3 ) );
	assertTrue( "c2 didn't survive", survivors.contains( c2 ) );
	uut.empty();
	survivors = uut.select( config );
	assertTrue( "selector not empty", survivors.isEmpty() );

	//		c1.cleanup();
	//		c2.cleanup();
	//		c3.cleanup();
}

}
