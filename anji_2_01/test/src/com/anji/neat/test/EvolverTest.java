/*
 * Copyright (C) 2004 Derek James and Philip Tucker This file is part of ANJI (Another NEAT Java
 * Implementation). ANJI is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA Created on Feb 5, 2004 by Philip Tucker
 */
package com.anji.neat.test;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.jgap.Chromosome;

import com.anji.neat.Evolver;
import com.anji.util.Properties;

/**
 * @author Philip Tucker
 */
public class EvolverTest extends TestCase {

private final static Logger logger = Logger.getLogger( EvolverTest.class );

private final static String PROP_FILE = "test_xor.properties";

/**
 * ctor
 */
public EvolverTest() {
	this( EvolverTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public EvolverTest( String name ) {
	super( name );
}

/**
 * main test
 * @throws Exception
 */
public void testEvolver() throws Exception {
	int failCount = 0;
	StringBuffer logText = new StringBuffer();
	for ( int i = 0; i < 100; ++i ) {
		Evolver uut = new Evolver();
		uut.init( new Properties( PROP_FILE ) );
		logger.info( "EvolverTest: RUN " + i );
		Chromosome champ = uut.getChamp();
		assertNull( "not null initial champ", champ );

		uut.run();

		champ = uut.getChamp();
		if ( champ == null ) {
			logText.append( i ).append( ": no champ\n" );
			++failCount;
		}
		else if ( uut.getChampAdjustedFitness() < uut.getThresholdFitness() ) {
			logText.append( i ).append( ": fitness < threshold: " ).append(
					uut.getChampAdjustedFitness() ).append( "\n" );
			++failCount;
		}
		else if ( uut.getChampAdjustedFitness() < uut.getTargetFitness() ) {
			logText.append( i ).append( ": fitness < target: " ).append(
					uut.getChampAdjustedFitness() ).append( "\n" );
		}
	}

	logger.info( logText.toString() );
	assertEquals( failCount + " failures", 0, failCount );
}

}
