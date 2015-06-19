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
 * Created on Feb 25, 2004 by Philip Tucker
 */
package org.jgap.test;

import java.io.FileWriter;

import junit.framework.TestCase;

import org.jgap.IdFactory;

/**
 * @author Philip Tucker
 */
public class IdFactoryTest extends TestCase {

private final static String FILE_NAME = "c:/temp/id.xml";

private final static long TEST_NEXT_ID = 0;

private final static long TEST_ID_COUNT = 1000;

/**
 * ctor
 */
public IdFactoryTest() {
	this( IdFactoryTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public IdFactoryTest( String name ) {
	super( name );
}

/**
 * @see junit.framework.TestCase#setUp()
 */
protected void setUp() throws Exception {
	FileWriter out = new FileWriter( FILE_NAME );
	out.write( ( "<id>" + TEST_NEXT_ID + "</id>" ) );
	out.flush();
	out.close();
}

/**
 * test with no file
 * @throws Exception
 */
public void testIdFactoryNoFile() throws Exception {
	doTestIdFactory( null, IdFactory.DEFAULT_BASE_ID );
}

/**
 * test with file
 * @throws Exception
 */
public void testIdFactoryWithFile() throws Exception {
	doTestIdFactory( FILE_NAME, TEST_NEXT_ID );

}

private void doTestIdFactory( String fileName, long nextId ) throws Exception {
	IdFactory uut = ( ( fileName == null ) ? new IdFactory() : new IdFactory( fileName ) );
	for ( int i = 0; i < TEST_ID_COUNT; ++i ) {
		assertEquals( "wrong id", nextId + i, uut.next() );
	}
	uut.store();

	if ( fileName != null ) {
		nextId += TEST_ID_COUNT;
		uut = new IdFactory( fileName );
		for ( int i = 0; i < TEST_ID_COUNT; ++i ) {
			assertEquals( "wrong id", nextId + i, uut.next() );
		}
		uut.store();

		nextId += TEST_ID_COUNT;
		uut = new IdFactory( fileName );
		for ( int i = 0; i < TEST_ID_COUNT; ++i ) {
			assertEquals( "wrong id", nextId + i, uut.next() );
		}
	}
}

}
