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
 * created by Philip Tucker on Jul 18, 2004
 */

package com.anji.floatingeye.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.anji.floatingeye.AnjiNetFloatingEye;
import com.anji.floatingeye.EyeMovementParms;
import com.anji.floatingeye.FloatingLocation;
import com.anji.floatingeye.LastAffinityListener;
import com.anji.imaging.Java2DSurface;
import com.anji.nn.BiasConnection;
import com.anji.nn.Connection;
import com.anji.nn.RandomConnection;

/**
 * @author Philip Tucker
 */
public class AnjiNetFloatingEyeTest extends TestCase {

private final static String TEST_IMAGE_FILE_PATH = "test/images/test.tif";

/**
 * ctor
 */
public AnjiNetFloatingEyeTest() {
	this( AnjiNetFloatingEyeTest.class.toString() );
}

/**
 * ctor
 * 
 * @param name
 */
public AnjiNetFloatingEyeTest( String name ) {
	super( name );
}

/**
 * @throws Exception
 */
public void testAnjiNetFloatingEye() throws Exception {
	// params
	Java2DSurface surface = new Java2DSurface();
	surface.setImage( new File( TEST_IMAGE_FILE_PATH ) );
	LastAffinityListener affinityListener = new LastAffinityListener();
	int eyeDim = 3;

	// wrong sized net w/ missing x
	try {
		TestAnjiNet net = new TestAnjiNet( 5 + ( eyeDim * eyeDim ) - 2, 5 );
		new AnjiNetFloatingEye( net, surface, 3, new EyeMovementParms( 0.6, 0.75, true, 0.0d, 0.1d,
				0.1d, 0.1d ), affinityListener, new ArrayList() );
		fail( "no x, should have thrown exception, too few inputs" );
	}
	catch ( IllegalArgumentException e ) {
		// success
	}

	// wrong sized net w/ missing y
	try {
		TestAnjiNet net = new TestAnjiNet( 5 + ( eyeDim * eyeDim ) - 2, 5 );
		new AnjiNetFloatingEye( net, surface, 3, new EyeMovementParms( 0.6, 0.75, true, 0.1d, 0.0d,
				0.1d, 0.1d ), affinityListener, new ArrayList() );
		fail( "no y, should have thrown exception, too few inputs" );
	}
	catch ( IllegalArgumentException e ) {
		// success
	}

	// wrong sized net w/ missing z
	try {
		TestAnjiNet net = new TestAnjiNet( 5 + ( eyeDim * eyeDim ) - 2, 5 );
		new AnjiNetFloatingEye( net, surface, 3, new EyeMovementParms( 0.6, 0.75, true, 0.1d, 0.1d,
				0.0d, 0.1d ), affinityListener, new ArrayList() );
		fail( "no z, should have thrown exception, too few inputs" );
	}
	catch ( IllegalArgumentException e ) {
		// success
	}

	// wrong sized net w/ missing theta
	try {
		TestAnjiNet net = new TestAnjiNet( 5 + ( eyeDim * eyeDim ) - 2, 5 );
		new AnjiNetFloatingEye( net, surface, 3, new EyeMovementParms( 0.6, 0.75, true, 0.1d, 0.1d,
				0.1d, 0.0d ), affinityListener, new ArrayList() );
		fail( "no theta, should have thrown exception, too few inputs" );
	}
	catch ( IllegalArgumentException e ) {
		// success
	}

	// wrong sized net
	try {
		TestAnjiNet net = new TestAnjiNet( 5 + ( eyeDim * eyeDim ) - 1, 5 );
		new AnjiNetFloatingEye( net, surface, 3, new EyeMovementParms( 0.6, 0.75, true, 0.1d, 0.1d,
				0.1d, 0.1d ), affinityListener, new ArrayList() );
		fail( "should have thrown exception, too few inputs" );
	}
	catch ( IllegalArgumentException e ) {
		// success
	}

	TestAnjiNet net = new TestAnjiNet( 5 + ( eyeDim * eyeDim ) + 2, 5 );
	TestNeuron xOutNeuron = net.getOutNeuron( 0 );
	TestNeuron yOutNeuron = net.getOutNeuron( 1 );
	TestNeuron zOutNeuron = net.getOutNeuron( 2 );
	TestNeuron thetaOutNeuron = net.getOutNeuron( 3 );
	TestNeuron affinityOutNeuron = net.getOutNeuron( 4 );
	List additionalInputConns = new ArrayList();
	additionalInputConns.add( BiasConnection.getInstance() );
	additionalInputConns.add( RandomConnection.getInstance() );

	AnjiNetFloatingEye uut = new AnjiNetFloatingEye( net, surface, eyeDim, new EyeMovementParms(
			0.6, 0.75, true, 0.1d, 0.1d, 0.1d, 0.1d ), affinityListener, additionalInputConns );

	// initial
	assertEyeEquals( uut, new FloatingLocation( 0.0, 0.0, 0.25 ), 0.0d, net, 0 );
	assertEquals( "affinity 0", 0.0d, affinityListener.getLastAffinity(), 0.5d );

	// after 1st step
	xOutNeuron.setValue( 0.5d );
	uut.step();
	assertEyeEquals( uut, new FloatingLocation( 0.05, 0.0, 0.25 ), 0.0d, net, 1 );
	assertEquals( "affinity 1", 0.0d, affinityListener.getLastAffinity(), 0.5d );

	// after 2nd step
	xOutNeuron.setValue( 0.0d );
	yOutNeuron.setValue( 1.0d );
	uut.step();
	assertEyeEquals( uut, new FloatingLocation( 0.05, 0.1, 0.25 ), 0.0d, net, 2 );
	assertEquals( "affinity 2", 0.0d, affinityListener.getLastAffinity(), 0.5d );

	// after 3rd step
	xOutNeuron.setValue( 0.0d );
	yOutNeuron.setValue( 0.0d );
	zOutNeuron.setValue( -0.5d );
	uut.step();
	assertEyeEquals( uut, new FloatingLocation( 0.05, 0.1, 0.20 ), 0.0d, net, 3 );
	assertEquals( "affinity 3", 0.0d, affinityListener.getLastAffinity(), 0.5d );

	// after 4th step
	xOutNeuron.setValue( 0.0d );
	yOutNeuron.setValue( 0.0d );
	zOutNeuron.setValue( 0.0d );
	thetaOutNeuron.setValue( 0.5d );
	uut.step();
	assertEyeEquals( uut, new FloatingLocation( 0.05, 0.1, 0.2 ), 0.05, net, 4 );
	assertEquals( "affinity 4", 0.0d, affinityListener.getLastAffinity(), 0.5d );

	// after 5th step
	xOutNeuron.setValue( 0.0d );
	yOutNeuron.setValue( 0.0d );
	zOutNeuron.setValue( 0.0d );
	thetaOutNeuron.setValue( 0.0d );
	affinityOutNeuron.setValue( 1.0d );
	uut.step();
	assertEyeEquals( uut, new FloatingLocation( 0.05, 0.1, 0.2 ), 0.05, net, 5 );
	assertEquals( "affinity 5", 1.0d, affinityListener.getLastAffinity(), 1.0d );

	// after reset
	uut.reset();
	assertEyeEquals( uut, new FloatingLocation( 0.0, 0.0, 0.25 ), 0.0d, net, 0 );
	assertEquals( "affinity 6", 0.0d, affinityListener.getLastAffinity(), 0.5d );
}

private void assertEyeEquals( AnjiNetFloatingEye eye, FloatingLocation expectedLocation,
		double expectedDirectionInput, TestAnjiNet net, int expectedStep ) {
	TestNeuron xInNeuron = net.getInNeuron( 0 );
	TestNeuron yInNeuron = net.getInNeuron( 1 );
	TestNeuron zInNeuron = net.getInNeuron( 2 );
	TestNeuron thetaInNeuron = net.getInNeuron( 3 );
	TestNeuron biasInNeuron = net.getInNeuron( 4 );

	assertEquals( "wrong step # " + expectedStep, expectedStep, eye.getStepNum() );
	assertEquals( "wrong location " + expectedStep, expectedLocation, eye.getFloatingEye()
			.getEyeLocation() );
	assertEquals( "wrong theta input value ", expectedDirectionInput * Math.PI, eye
			.getFloatingEye().getEyeDirectionRadians(), 0.00000000001d );
	assertEquals( "wrong input to x connection " + expectedStep, expectedLocation.x,
			( (Connection) xInNeuron.getIncomingConns().iterator().next() ).read(), 0.0d );
	assertEquals( "wrong input to y connection " + expectedStep, expectedLocation.y,
			( (Connection) yInNeuron.getIncomingConns().iterator().next() ).read(), 0.0d );
	assertEquals( "wrong input to z connection " + expectedStep, expectedLocation.z,
			( (Connection) zInNeuron.getIncomingConns().iterator().next() ).read(), 0.0d );
	assertEquals( "wrong input to theta connection " + expectedStep, expectedDirectionInput,
			( (Connection) thetaInNeuron.getIncomingConns().iterator().next() ).read(), 0.0d );
	assertEquals( "wrong input to bias connection " + expectedStep, 1.0,
			( (Connection) biasInNeuron.getIncomingConns().iterator().next() ).read(), 0.0d );
}
}
