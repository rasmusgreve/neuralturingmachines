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
 * created by Philip Tucker on Jun 13, 2004
 */

package com.anji.imaging.test;

import com.anji.imaging.CardinalDirection;
import com.anji.imaging.CoordinateTranslator;
import com.anji.imaging.CoordinateTranslatorFactory;
import com.anji.imaging.IntLocation2D;

import junit.framework.TestCase;

/**
 * @author Philip Tucker
 */
public class CoordinateTranslatorTest extends TestCase {

/**
 * ctor
 */
public CoordinateTranslatorTest() {
	this( CoordinateTranslatorTest.class.toString() );
}

/**
 * ctor
 * 
 * @param name
 */
public CoordinateTranslatorTest( String name ) {
	super( name );
}

private void doTestTranslator( CoordinateTranslator uut, IntLocation2D coords, int max,
		IntLocation2D expected ) {
	uut.transform( coords, max );
	assertEquals( uut.toString(), expected, coords );
}

/**
 * test translators
 */
public void testTranslators() {
	int max = 5;

	// north
	CoordinateTranslator uut = CoordinateTranslatorFactory.getInstance().getTransformer( false,
			CardinalDirection.NORTH );
	doTestTranslator( uut, new IntLocation2D( 0, 0 ), max, new IntLocation2D( 0, 0 ) );
	doTestTranslator( uut, new IntLocation2D( 3, 3 ), max, new IntLocation2D( 3, 3 ) );
	doTestTranslator( uut, new IntLocation2D( 3, 1 ), max, new IntLocation2D( 3, 1 ) );
	doTestTranslator( uut, new IntLocation2D( 1, 3 ), max, new IntLocation2D( 1, 3 ) );
	doTestTranslator( uut, new IntLocation2D( 1, 1 ), max, new IntLocation2D( 1, 1 ) );

	// east
	uut = CoordinateTranslatorFactory.getInstance()
			.getTransformer( false, CardinalDirection.EAST );
	doTestTranslator( uut, new IntLocation2D( 0, 0 ), max, new IntLocation2D( 5, 0 ) );
	doTestTranslator( uut, new IntLocation2D( 3, 3 ), max, new IntLocation2D( 2, 3 ) );
	doTestTranslator( uut, new IntLocation2D( 3, 1 ), max, new IntLocation2D( 4, 3 ) );
	doTestTranslator( uut, new IntLocation2D( 1, 3 ), max, new IntLocation2D( 2, 1 ) );
	doTestTranslator( uut, new IntLocation2D( 1, 1 ), max, new IntLocation2D( 4, 1 ) );

	// south
	uut = CoordinateTranslatorFactory.getInstance().getTransformer( false,
			CardinalDirection.SOUTH );
	doTestTranslator( uut, new IntLocation2D( 0, 0 ), max, new IntLocation2D( 5, 5 ) );
	doTestTranslator( uut, new IntLocation2D( 3, 3 ), max, new IntLocation2D( 2, 2 ) );
	doTestTranslator( uut, new IntLocation2D( 3, 1 ), max, new IntLocation2D( 2, 4 ) );
	doTestTranslator( uut, new IntLocation2D( 1, 3 ), max, new IntLocation2D( 4, 2 ) );
	doTestTranslator( uut, new IntLocation2D( 1, 1 ), max, new IntLocation2D( 4, 4 ) );

	// west
	uut = CoordinateTranslatorFactory.getInstance()
			.getTransformer( false, CardinalDirection.WEST );
	doTestTranslator( uut, new IntLocation2D( 0, 0 ), max, new IntLocation2D( 0, 5 ) );
	doTestTranslator( uut, new IntLocation2D( 3, 3 ), max, new IntLocation2D( 3, 2 ) );
	doTestTranslator( uut, new IntLocation2D( 3, 1 ), max, new IntLocation2D( 1, 2 ) );
	doTestTranslator( uut, new IntLocation2D( 1, 3 ), max, new IntLocation2D( 3, 4 ) );
	doTestTranslator( uut, new IntLocation2D( 1, 1 ), max, new IntLocation2D( 1, 4 ) );

	// north flipped
	uut = CoordinateTranslatorFactory.getInstance()
			.getTransformer( true, CardinalDirection.NORTH );
	doTestTranslator( uut, new IntLocation2D( 0, 0 ), max, new IntLocation2D( 5, 0 ) );
	doTestTranslator( uut, new IntLocation2D( 3, 3 ), max, new IntLocation2D( 2, 3 ) );
	doTestTranslator( uut, new IntLocation2D( 3, 1 ), max, new IntLocation2D( 2, 1 ) );
	doTestTranslator( uut, new IntLocation2D( 1, 3 ), max, new IntLocation2D( 4, 3 ) );
	doTestTranslator( uut, new IntLocation2D( 1, 1 ), max, new IntLocation2D( 4, 1 ) );

	// east flipped
	uut = CoordinateTranslatorFactory.getInstance().getTransformer( true, CardinalDirection.EAST );
	doTestTranslator( uut, new IntLocation2D( 0, 0 ), max, new IntLocation2D( 0, 0 ) );
	doTestTranslator( uut, new IntLocation2D( 3, 3 ), max, new IntLocation2D( 3, 3 ) );
	doTestTranslator( uut, new IntLocation2D( 3, 1 ), max, new IntLocation2D( 1, 3 ) );
	doTestTranslator( uut, new IntLocation2D( 1, 3 ), max, new IntLocation2D( 3, 1 ) );
	doTestTranslator( uut, new IntLocation2D( 1, 1 ), max, new IntLocation2D( 1, 1 ) );

	// south flipped
	uut = CoordinateTranslatorFactory.getInstance()
			.getTransformer( true, CardinalDirection.SOUTH );
	doTestTranslator( uut, new IntLocation2D( 0, 0 ), max, new IntLocation2D( 0, 5 ) );
	doTestTranslator( uut, new IntLocation2D( 3, 3 ), max, new IntLocation2D( 3, 2 ) );
	doTestTranslator( uut, new IntLocation2D( 3, 1 ), max, new IntLocation2D( 3, 4 ) );
	doTestTranslator( uut, new IntLocation2D( 1, 3 ), max, new IntLocation2D( 1, 2 ) );
	doTestTranslator( uut, new IntLocation2D( 1, 1 ), max, new IntLocation2D( 1, 4 ) );

	// west flipped
	uut = CoordinateTranslatorFactory.getInstance().getTransformer( true, CardinalDirection.WEST );
	doTestTranslator( uut, new IntLocation2D( 0, 0 ), max, new IntLocation2D( 5, 5 ) );
	doTestTranslator( uut, new IntLocation2D( 3, 3 ), max, new IntLocation2D( 2, 2 ) );
	doTestTranslator( uut, new IntLocation2D( 3, 1 ), max, new IntLocation2D( 4, 2 ) );
	doTestTranslator( uut, new IntLocation2D( 1, 3 ), max, new IntLocation2D( 2, 4 ) );
	doTestTranslator( uut, new IntLocation2D( 1, 1 ), max, new IntLocation2D( 4, 4 ) );
}

}
