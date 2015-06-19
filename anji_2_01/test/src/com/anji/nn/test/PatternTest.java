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

import com.anji.nn.Connection;
import com.anji.nn.Pattern;

import junit.framework.TestCase;

/**
 * @author Philip Tucker
 */
public class PatternTest extends TestCase {

/**
 * ctor
 */
public PatternTest() {
	this( PatternTest.class.toString() );
}

/**
 * ctor
 * @param arg0
 */
public PatternTest( String arg0 ) {
	super( arg0 );
}

/**
 * test pattern object
 */
public void testIt() {
	Pattern uut = new Pattern( 3 );

	try {
		uut.getConnection( -1 );
		fail( "should have thrown exception" );
	}
	catch ( IllegalArgumentException e ) {
		// success
	}

	try {
		uut.getConnection( 3 );
		fail( "should have thrown exception" );
	}
	catch ( IllegalArgumentException e ) {
		// success
	}

	double[] values = { 1.0d, -11.0d, 111.0d };
	uut.setValues( values );
	Connection c0 = uut.getConnection( 0 );
	Connection c1 = uut.getConnection( 1 );
	Connection c2 = uut.getConnection( 2 );

	assertNotNull( "conenction 0", c0 );
	assertNotNull( "conenction 1", c1 );
	assertNotNull( "conenction 2", c2 );

	for ( int i = 0; i < 100; ++i ) {
		assertEquals( "wrong value connection 0", 1.0d, c0.read(), 0.0d );
		assertEquals( "wrong value connection 1", -11.0d, c1.read(), 0.0d );
		assertEquals( "wrong value connection 2", 111.0d, c2.read(), 0.0d );
	}

	uut.setValue( 0, -2.0d );
	uut.setValue( 2, -222.0d );

	for ( int i = 0; i < 100; ++i ) {
		assertEquals( "wrong value connection 0", -2.0d, c0.read(), 0.0d );
		assertEquals( "wrong value connection 1", -11.0d, c1.read(), 0.0d );
		assertEquals( "wrong value connection 2", -222.0d, c2.read(), 0.0d );
	}
}

}
