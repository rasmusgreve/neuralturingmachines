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
public class EvSailSigmoidActivationFunctionTest extends TestCase {

/**
 * ctor
 */
public EvSailSigmoidActivationFunctionTest() {
	this( EvSailSigmoidActivationFunctionTest.class.toString() );
}

/**
 * ctor
 * @param arg0
 */
public EvSailSigmoidActivationFunctionTest( String arg0 ) {
	super( arg0 );
}

/**
 * test activation function
 * @throws Exception
 */
public void testEvSailSigmoid() throws Exception {
	ActivationFunction uut = ActivationFunctionFactory.getInstance().getEvSailSigmoid();
	assertEquals( "wrong value 1", 0.0d, uut.apply( -5.0d ), 0.0d );
	assertEquals( "wrong value 2", 0.0, uut.apply( -1.0d ), 0.0d );
	assertEquals( "wrong value 3", 4.0500000000E-01d, uut.apply( -0.1d ), 0.0d );
	assertEquals( "wrong value 4", 0.5, uut.apply( 0 ), 0.0d );
	assertEquals( "wrong value 5", 1.0d - 4.0500000000E-01d, uut.apply( 0.1d ), 0.0d );
	assertEquals( "wrong value 6", 1.0d, uut.apply( 1.0d ), 0.0d );
	assertEquals( "wrong value 7", 1.0d, uut.apply( 5.0d ), 0.0d );
}

/**
 * verify it approximates sigmoid
 * @throws Exception
 */
public void testCompareToSigmoid() throws Exception {
	ActivationFunction uut = ActivationFunctionFactory.getInstance().getEvSailSigmoid();
	ActivationFunction sigmoid = ActivationFunctionFactory.getInstance().getSigmoid();
	for ( double x = -100.0d; x <= 100.0d; x += 0.1 ) {
		assertEquals( "too different from sigmoid", sigmoid.apply( x ), uut.apply( x ), 0.075 );
	}
}

/**
 * test performance
 * @throws Exception
 */
public void testPerformance() throws Exception {
	ActivationFunction uut = ActivationFunctionFactory.getInstance().getEvSailSigmoid();
	ActivationFunction sigmoid = ActivationFunctionFactory.getInstance().getSigmoid();

	long start = System.currentTimeMillis();
	for ( double x = -100000.0d; x <= 100000.0d; x += 0.1 ) {
		sigmoid.apply( x );
		// System.out.print( ( (long) ( sigmoid.apply( x ) * 1000 ) ) + ", " );
	}
	System.out.println( "sigmoid took " + ( System.currentTimeMillis() - start ) + " millis" );

	start = System.currentTimeMillis();
	for ( double x = -100000.0d; x <= 100000.0d; x += 0.1 ) {
		uut.apply( x );
		// System.out.print( ( (long) ( uut.apply( x ) * 1000 ) ) + ", " );
	}
	System.out.println( "evsail took " + ( System.currentTimeMillis() - start ) + " millis" );
}

}
