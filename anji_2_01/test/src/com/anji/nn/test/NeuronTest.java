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

import com.anji.nn.ActivationFunctionFactory;
import com.anji.nn.Neuron;
import com.anji.nn.Pattern;

/**
 * @author Philip Tucker
 */
public class NeuronTest extends TestCase {

	/**
	 * ctor
	 */
	public NeuronTest() {
		this( NeuronTest.class.toString() );
	}

	/**
	 * ctor
	 * @param arg0
	 */
	public NeuronTest( String arg0 ) {
		super( arg0 );
	}

	/**
	 * test
	 * @throws Exception
	 */
	public void testNode() throws Exception {
		// null input
		try {
			new Neuron( null );
			fail( "should have thrown exception" );
		}
		catch ( IllegalArgumentException e ) {
			// success
		}

		// empty node
		Neuron uut = new Neuron( ActivationFunctionFactory.getInstance().getLinear() );
		assertEquals( "wrong initial value for current", 0.0d, uut.getValue(), 0.0d );
		for ( int i = 0; i < 100; ++i ) {
			uut.step();
			assertEquals( "wrong initial value for current", 0.0d, uut.getValue(), 0.0d );
		}

		// node with 1 input
		Pattern in = new Pattern( 3 );
		uut.addIncomingConnection( in.getConnection( 0 ) );
		in.setValue( 0, 1.0d );
		uut.step();
		assertEquals( "wrong initial value for current", 0.0d, uut.getValue(), 1.0d );
		for ( int i = 0; i < 100; ++i ) {
			uut.step();
			assertEquals( "wrong initial value for current", 0.0d, uut.getValue(), 1.0d );
		}

		// node with many inputs
		uut.addIncomingConnection( in.getConnection( 1 ) );
		uut.addIncomingConnection( in.getConnection( 2 ) );
		in.setValue( 1, 11.0d );
		in.setValue( 2, 111.0d );
		uut.step();
		assertEquals( "wrong initial value for current", 123.0d, uut.getValue(), 0.0d );
		for ( int i = 0; i < 100; ++i ) {
			uut.step();
			assertEquals( "wrong initial value for current", 123.0d, uut.getValue(), 0.0d );
		}

		// reset
		uut.reset();
		assertEquals( "wrong initial value for current", 123.0d, uut.getValue(), 0.0d );
		uut.step();
		assertEquals( "wrong initial value for current", 123.0d, uut.getValue(), 0.0d );
		for ( int i = 0; i < 100; ++i ) {
			uut.step();
			assertEquals( "wrong initial value for current", 123.0d, uut.getValue(), 0.0d );
		}

	}

}
