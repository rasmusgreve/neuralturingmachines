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
 * created by Philip Tucker on Sep 30, 2004
 */
package com.anji.nn.test;

import java.util.Random;

import com.anji.nn.StepHourglassConnection;

import junit.framework.TestCase;

/**
 * StepHourglassConnectionTest
 */
public class StepHourglassConnectionTest extends TestCase {

/**
 * ctor
 */
public StepHourglassConnectionTest() {
	this( StepHourglassConnectionTest.class.toString() );
}

/**
 * ctor
 * 
 * @param arg0
 */
public StepHourglassConnectionTest( String arg0 ) {
	super( arg0 );
}

/**
 * @throws Exception
 */
public void testConnection() throws Exception {
	StepHourglassConnection uut = new StepHourglassConnection();
	assertEquals( "wrong cost", 74, uut.cost() );
	try {
		uut.reset( 0 );
		fail( "should have thrown exception for 0 max steps" );
	}
	catch ( Exception e ) {
		// success
	}

	for ( int i = 0; i < 10; ++i ) {
		int numSteps = ( new Random().nextInt( 1000 ) ) + 1;
		uut.reset( numSteps );
		for ( int stepsRemaining = numSteps; stepsRemaining > -3; --stepsRemaining ) {
			// make sure value remains the same for multiple reads on same step
			for ( int j = 0; j < 5; ++j ) {
				double expected = ( stepsRemaining <= 0 ) ? 0d : (double) stepsRemaining / numSteps;
				assertEquals( "wrong value: " + stepsRemaining, expected, uut.read(), 0.0d );
			}
			uut.step();
		}

		// make sure hourglass remains at 0
		for ( int j = 0; j < 5; ++j ) {
			assertEquals( "wrong value: " + j, 0.0d, uut.read(), 0.0d );
			uut.step();
		}
	}
}

}
