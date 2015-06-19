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
 * created by Philip Tucker on Sep 10, 2004
 */

package com.anji.floatingeye.test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.anji.floatingeye.EyeMovementParms;
import com.anji.floatingeye.EyePixelConnection;
import com.anji.floatingeye.FloatingEye;
import com.anji.imaging.Java2DSurface;

/**
 * @author Philip Tucker
 */
public class EyePixelConnectionTest extends TestCase {

private final static int TRANSPARENT = 0x00000000;

private final static int BLACK_TRANS = 0x80000000;

private final static int WHITE_TRANS = 0x80FEFEFE;

private final static int BLACK = 0xFF000000;

private final static int GREY = 0xFF808080;

private final static int WHITE = 0xFFFEFEFE;

/**
 * ctor
 */
public EyePixelConnectionTest() {
	super();
}

/**
 * ctor
 * 
 * @param arg0
 */
public EyePixelConnectionTest( String arg0 ) {
	super( arg0 );
}

/**
 * test eye pixel connection
 * 
 * @throws Exception
 */
public void testEyePixelConnection() throws Exception {
	testEye( BLACK, 1.0d );
	testEye( GREY, 0.4980392156862745d );
	testEye( WHITE, 0.00392156862745098d );
	testEye( BLACK_TRANS, -0.4980392156862745d );
	testEye( WHITE_TRANS, -0.4980392156862745d );
	testEye( TRANSPARENT, -1.0d );
}

private void testEye( int surfacePixel, double expectedResponse ) throws Exception {
	List controlNeurons = new ArrayList( 5 );
	for ( int i = 0; i < 5; ++i )
		controlNeurons.add( new TestNeuron() );

	// create surface
	Java2DSurface surface = new Java2DSurface();
	BufferedImage bi = new BufferedImage( 100, 100, BufferedImage.TYPE_INT_ARGB );
	for ( int x = 0; x < bi.getWidth(); ++x )
		for ( int y = 0; y < bi.getHeight(); ++y )
			bi.setRGB( x, y, surfacePixel );
	surface.setImage( bi );

	FloatingEye eye = new FloatingEye( "test eye", controlNeurons, surface, 1,
			new EyeMovementParms( 1d ) );
	eye.step();
	EyePixelConnection uut = new EyePixelConnection( 0, 0, eye );
	assertEquals( "wrong response", expectedResponse, uut.read(), 0.0d );
}
}
