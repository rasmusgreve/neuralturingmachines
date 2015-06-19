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
 * created by Philip Tucker on Jun 9, 2004
 */

package com.anji.floatingeye.test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.jgap.test.DummyFitnessFunction;

import com.anji.floatingeye.EyeMovementParms;
import com.anji.floatingeye.FloatingEye;
import com.anji.floatingeye.FloatingLocation;
import com.anji.imaging.Java2DSurface;
import com.anji.neat.NeatConfiguration;
import com.anji.util.Properties;

/**
 * @author Philip Tucker
 */
public class FloatingEyeTest extends TestCase {

private Properties props = new Properties();

private NeatConfiguration config;

private Java2DSurface surface;

private final static String NAME = "test floating eye";

/**
 * ctor
 */
public FloatingEyeTest() {
	this( FloatingEyeTest.class.toString() );
}

/**
 * ctor
 * 
 * @param name
 */
public FloatingEyeTest( String name ) {
	super( name );
}

/**
 * initialization
 * @see junit.framework.TestCase#setUp()
 */
protected void setUp() throws Exception {
	props.loadFromResource( "test_eye.properties" );
	config = new NeatConfiguration( props );
	config.setFitnessFunction( new DummyFitnessFunction() );

	// create surface
	surface = new Java2DSurface();
	BufferedImage bi = new BufferedImage( 4, 4, BufferedImage.TYPE_INT_ARGB );
	bi.setRGB( 0, 0, 1 );
	bi.setRGB( 1, 1, 1 );
	bi.setRGB( 2, 2, 1 );
	bi.setRGB( 3, 3, 1 );
	surface.setImage( bi );
}

/**
 * test floating eye
 * 
 * @throws Exception
 */
public void testFloatingEyeLimited() throws Exception {
	TestNeuron aOutNeuron = new TestNeuron();
	TestNeuron bOutNeuron = new TestNeuron();
	TestNeuron cOutNeuron = new TestNeuron();
	List controlNeurons = new ArrayList();
	controlNeurons.add( aOutNeuron );
	controlNeurons.add( bOutNeuron );

	// eye params
	double minZoom = 0.5d;
	double startZoom = 0.6d;
	short eyeDim = 2;

	//
	// not enough neurons
	//

	// no x
	try {
		new FloatingEye( NAME, controlNeurons, surface, eyeDim, new EyeMovementParms( minZoom,
				startZoom, true, 0d, 1d, 1d, 1d ) );
		fail( "no x, should have failed with not enough control neurons" );
	}
	catch ( Exception e ) {
		// success
	}

	// no y
	try {
		new FloatingEye( NAME, controlNeurons, surface, eyeDim, new EyeMovementParms( minZoom,
				startZoom, true, 1d, 0d, 1d, 1d ) );
		fail( "no y, should have failed with not enough control neurons" );
	}
	catch ( Exception e ) {
		// success
	}

	// no z
	try {
		new FloatingEye( NAME, controlNeurons, surface, eyeDim, new EyeMovementParms( minZoom,
				startZoom, true, 1d, 1d, 0d, 1d ) );
		fail( "no z, should have failed with not enough control neurons" );
	}
	catch ( Exception e ) {
		// success
	}

	// no rotate
	try {
		new FloatingEye( NAME, controlNeurons, surface, eyeDim, new EyeMovementParms( minZoom,
				startZoom, true, 1d, 1d, 1d, 0d ) );
		fail( "no rotate, should have failed with not enough control neurons" );
	}
	catch ( Exception e ) {
		// success
	}

	controlNeurons.add( cOutNeuron );

	//
	// enough neurons
	//

	// no x
	new FloatingEye( NAME, controlNeurons, surface, eyeDim, new EyeMovementParms( minZoom,
			startZoom, true, 0d, 1d, 1d, 1d ) );

	// no y
	new FloatingEye( NAME, controlNeurons, surface, eyeDim, new EyeMovementParms( minZoom,
			startZoom, true, 1d, 0d, 1d, 1d ) );

	// no z
	new FloatingEye( NAME, controlNeurons, surface, eyeDim, new EyeMovementParms( minZoom,
			startZoom, true, 1d, 1d, 0d, 1d ) );

	// no rotate
	new FloatingEye( NAME, controlNeurons, surface, eyeDim, new EyeMovementParms( minZoom,
			startZoom, true, 1d, 1d, 1d, 0d ) );
}

/**
 * test floating eye
 * 
 * @throws Exception
 */
public void testFloatingEye() throws Exception {
	TestNeuron xOutNeuron = new TestNeuron();
	TestNeuron yOutNeuron = new TestNeuron();
	TestNeuron zOutNeuron = new TestNeuron();
	TestNeuron dirOutNeuron = new TestNeuron();
	List controlNeurons = new ArrayList();
	controlNeurons.add( xOutNeuron );
	controlNeurons.add( yOutNeuron );

	// eye params
	double minZoom = 0.5d;
	double startZoom = 0.6d;
	short eyeDim = 2;

	// not enough neurons
	try {
		new FloatingEye( NAME, controlNeurons, surface, eyeDim, new EyeMovementParms( minZoom,
				startZoom, true ) );
		fail( "should have failed with not enough control neurons" );
	}
	catch ( Exception e ) {
		// success
	}

	controlNeurons.add( zOutNeuron );
	controlNeurons.add( dirOutNeuron );

	// eye dimension too small
	try {
		new FloatingEye( NAME, controlNeurons, surface, 0, new EyeMovementParms( minZoom,
				startZoom, true ) );
		fail( "should have failed with too small eye" );
	}
	catch ( Exception e ) {
		// success
	}

	// min zoom too big
	try {
		new FloatingEye( NAME, controlNeurons, surface, eyeDim, new EyeMovementParms( 1.1d,
				startZoom, true ) );
		fail( "should have failed with too small eye" );
	}
	catch ( Exception e ) {
		// success
	}

	// min zoom too small
	try {
		new FloatingEye( NAME, controlNeurons, surface, eyeDim, new EyeMovementParms( -0.1d,
				startZoom, true ) );
		fail( "should have failed with too small eye" );
	}
	catch ( Exception e ) {
		// success
	}

	// start zoom too big
	double tooBigStartZoom = 1.1d;
	FloatingEye uut = new FloatingEye( NAME, controlNeurons, surface, eyeDim,
			new EyeMovementParms( minZoom, tooBigStartZoom, true ) );
	assertEquals( "wrong initial zoom w/ startZoom = " + tooBigStartZoom, 1.0d, uut.getZoom(),
			0.0d );

	// start zoom too small
	double tooSmallStartZoom = minZoom - 0.1d;
	uut = new FloatingEye( NAME, controlNeurons, surface, eyeDim, new EyeMovementParms( minZoom,
			tooSmallStartZoom, true ) );
	assertEquals( "wrong initial zoom w/ startZoom = " + tooSmallStartZoom, minZoom, uut
			.getZoom(), 0.0d );

	// valid eye
	double expectedZ = 1 - startZoom;
	uut = new FloatingEye( NAME, controlNeurons, surface, eyeDim, new EyeMovementParms( minZoom,
			startZoom, true, 0.1d, 0.1d, 0.1d, 0.1d ) );
	assertEquals( "wrong initial x location", new FloatingLocation( 0, 0, expectedZ ), uut
			.getEyeLocation() );
	assertEquals( "wrong initial direction", 0, uut.getEyeDirectionRadians(), 0.0d );
	assertEquals( "wrong step 0", 0, uut.getStepNum() );

	// move in x direction
	xOutNeuron.setValue( 1 );
	yOutNeuron.setValue( 0 );
	zOutNeuron.setValue( 0 );
	dirOutNeuron.setValue( 0 );
	uut.step();
	assertEquals( "wrong step 1", 1, uut.getStepNum() );
	assertEquals( "wrong location 1", new FloatingLocation( 0.1, 0.0d, expectedZ ), uut
			.getEyeLocation() );
	assertEquals( "wrong direction 1", 0, uut.getEyeDirectionRadians(), 0.0d );
	uut.step();
	assertEquals( "wrong step 2", 2, uut.getStepNum() );
	assertEquals( "wrong location 2", new FloatingLocation( 0.2, 0.0d, expectedZ ), uut
			.getEyeLocation() );
	assertEquals( "wrong direction 2", 0, uut.getEyeDirectionRadians(), 0.0d );

	// move in y direction
	xOutNeuron.setValue( 0 );
	yOutNeuron.setValue( 1 );
	zOutNeuron.setValue( 0 );
	dirOutNeuron.setValue( 0 );
	uut.step();
	assertEquals( "wrong step 3", 3, uut.getStepNum() );
	assertEquals( "wrong location 3", new FloatingLocation( 0.2d, 0.1d, expectedZ ), uut
			.getEyeLocation() );
	assertEquals( "wrong direction 3", 0, uut.getEyeDirectionRadians(), 0.0d );
	uut.step();
	assertEquals( "wrong step 4", 4, uut.getStepNum() );
	assertEquals( "wrong location 4", new FloatingLocation( 0.2d, 0.2d, expectedZ ), uut
			.getEyeLocation() );
	assertEquals( "wrong direction 4", 0, uut.getEyeDirectionRadians(), 0.0d );

	// move in z direction
	xOutNeuron.setValue( 0 );
	yOutNeuron.setValue( 0 );
	zOutNeuron.setValue( -1 );
	dirOutNeuron.setValue( 0 );
	uut.step();
	assertEquals( "wrong step 5", 5, uut.getStepNum() );
	assertEquals( "wrong location 5", new FloatingLocation( 0.2d, 0.2d, expectedZ - 0.1d ), uut
			.getEyeLocation() );
	assertEquals( "wrong direction 5", 0, uut.getEyeDirectionRadians(), 0.0d );
	uut.step();
	assertEquals( "wrong step 6", 6, uut.getStepNum() );
	assertEquals( "wrong location 6", new FloatingLocation( 0.2d, 0.2d,
			expectedZ - 0.19999999999999999d ), uut.getEyeLocation() );
	assertEquals( "wrong direction 6", 0, uut.getEyeDirectionRadians(), 0.0d );

	// spin
	xOutNeuron.setValue( 0 );
	yOutNeuron.setValue( 0 );
	zOutNeuron.setValue( 0 );
	dirOutNeuron.setValue( 1 );
	uut.step();
	assertEquals( "wrong step 7", 7, uut.getStepNum() );
	assertEquals( "wrong location 7", new FloatingLocation( 0.2d, 0.2d,
			expectedZ - 0.19999999999999999d ), uut.getEyeLocation() );
	assertEquals( "wrong direction 7", 0.1d * Math.PI, uut.getEyeDirectionRadians(),
			0.00000000001d );
	uut.step();
	assertEquals( "wrong step 8", 8, uut.getStepNum() );
	assertEquals( "wrong location 8", new FloatingLocation( 0.2d, 0.2d,
			expectedZ - 0.19999999999999999d ), uut.getEyeLocation() );
	assertEquals( "wrong direction 8", 0.2d * Math.PI, uut.getEyeDirectionRadians(),
			0.00000000001d );

	// flips disabled
	uut = new FloatingEye( NAME, controlNeurons, surface, eyeDim, new EyeMovementParms( minZoom,
			startZoom, false ) );
	assertEquals( "wrong initial location", new FloatingLocation( 0, 0, expectedZ ), uut
			.getEyeLocation() );
	assertEquals( "wrong initial direction", 0, uut.getEyeDirectionRadians(), 0.0d );
	assertEquals( "wrong step 0", 0, uut.getStepNum() );

	// move in z direction
	xOutNeuron.setValue( 0 );
	yOutNeuron.setValue( 0 );
	zOutNeuron.setValue( -1 );
	dirOutNeuron.setValue( 0 );
	uut.step();
	assertEquals( "wrong step 1", 1, uut.getStepNum() );
	assertEquals( "wrong location 1", new FloatingLocation( 0.0d, 0.0d, 0.0d ), uut
			.getEyeLocation() );
	assertEquals( "wrong direction 1", 0, uut.getEyeDirectionRadians(), 0.0d );
	uut.step();
	assertEquals( "wrong step 2", 2, uut.getStepNum() );
	assertEquals( "wrong location 2", new FloatingLocation( 0.0d, 0.0d, 0.0d ), uut
			.getEyeLocation() );
	assertEquals( "wrong direction 2", 0, uut.getEyeDirectionRadians(), 0.0d );
}
}
