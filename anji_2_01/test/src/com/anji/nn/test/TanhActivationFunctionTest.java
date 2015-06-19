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
public class TanhActivationFunctionTest extends TestCase {

/**
 * ctor
 */
public TanhActivationFunctionTest() {
	this( TanhActivationFunctionTest.class.toString() );
}

/**
 * ctor
 * @param arg0
 */
public TanhActivationFunctionTest( String arg0 ) {
	super( arg0 );
}

/**
 * test activation function
 * @throws Exception
 */
public void testTanh() throws Exception {
	ActivationFunction uut = ActivationFunctionFactory.getInstance().getTanh();
	assertEquals( "wrong value", -0.9999092042625951, uut.apply( -5.0d ), 0.0d );
	assertEquals( "wrong value", -0.7615941559557649, uut.apply( -1.0d ), 0.0d );
	assertEquals( "wrong value", -0.09966799462495568, uut.apply( -0.1d ), 0.0d );
	assertEquals( "wrong value", 0.0, uut.apply( 0 ), 0.0d );
	assertEquals( "wrong value", 0.0996679946249559, uut.apply( 0.1d ), 0.0d );
	assertEquals( "wrong value", 0.7615941559557646, uut.apply( 1.0d ), 0.0d );
	assertEquals( "wrong value", 0.9999092042625952, uut.apply( 5.0d ), 0.0d );
}

}
