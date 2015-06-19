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
 * Created on Feb 26, 2004 by Philip Tucker
 */
package com.anji.nn.test;

import junit.framework.TestCase;

import com.anji.nn.ActivationFunction;
import com.anji.nn.ActivationFunctionFactory;

/**
 * @author Philip Tucker
 */
public class InverseAbsActivationFunctionTest extends TestCase {

/**
 * ctor
 */
public InverseAbsActivationFunctionTest() {
	this( InverseAbsActivationFunctionTest.class.toString() );
}

/**
 * ctor
 * @param arg0
 */
public InverseAbsActivationFunctionTest( String arg0 ) {
	super( arg0 );
}

/**
 * test activation function
 * @throws Exception
 */
public void testInverseAbs() throws Exception {
	ActivationFunction uut = ActivationFunctionFactory.getInstance().getInverseAbs();
	assertEquals( "wrong value", -0.94340d, uut.apply( -5.0d ), 0.00001d );
	assertEquals( "wrong value", -0.76923d, uut.apply( -1.0d ), 0.00001d );
	assertEquals( "wrong value", -0.25000d, uut.apply( -0.1d ), 0.00001d );
	assertEquals( "wrong value", 0.0, uut.apply( 0 ), 0.00001d );
	assertEquals( "wrong value", 0.25000d, uut.apply( 0.1d ), 0.00001d );
	assertEquals( "wrong value", 0.76923d, uut.apply( 1.0d ), 0.00001d );
	assertEquals( "wrong value", 0.94340d, uut.apply( 5.0d ), 0.00001d );
}

/**
 * verify it approximates tanh
 * @throws Exception
 */
public void testCompare() throws Exception {
	ActivationFunction uut = ActivationFunctionFactory.getInstance().getInverseAbs();
	ActivationFunction tanh = ActivationFunctionFactory.getInstance().getTanh();
	for ( double x = -100.0d; x <= 100.0d; x += 0.1 ) {
		assertEquals( "too different from sigmoid", tanh.apply( x ), uut.apply( x ), 0.25 );
	}
}

/**
 * test performance
 * @throws Exception
 */
public void testPerformance() throws Exception {
	ActivationFunction uut = ActivationFunctionFactory.getInstance().getInverseAbs();
	ActivationFunction tanh = ActivationFunctionFactory.getInstance().getTanh();

	long start = System.currentTimeMillis();
	for ( double x = -100000.0d; x <= 100000.0d; x += 0.1 ) {
		tanh.apply( x );
		// System.out.print( ( (long) ( tanh.apply( x ) * 1000 ) ) + ", " );
	}
	System.out.println( "tanh took " + ( System.currentTimeMillis() - start ) + " millis" );

	start = System.currentTimeMillis();
	for ( double x = -100000.0d; x <= 100000.0d; x += 0.1 ) {
		uut.apply( x );
		// System.out.print( ( (long) ( uut.apply( x ) * 1000 ) ) + ", " );
	}
	System.out.println( "inv-abs took " + ( System.currentTimeMillis() - start ) + " millis" );
}

}
