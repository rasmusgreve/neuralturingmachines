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
 * created by Philip Tucker on Jul 24, 2004
 */

package com.anji.nn.test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import com.anji.floatingeye.EyeMovementParms;
import com.anji.floatingeye.EyePixelConnection;
import com.anji.floatingeye.FloatingEye;
import com.anji.floatingeye.LocationXConnection;
import com.anji.floatingeye.LocationYConnection;
import com.anji.floatingeye.LocationZConnection;
import com.anji.floatingeye.ThetaConnection;
import com.anji.floatingeye.test.TestNeuron;
import com.anji.imaging.Java2DSurface;
import com.anji.nn.ActivationFunction;
import com.anji.nn.ActivationFunctionFactory;
import com.anji.nn.BiasConnection;
import com.anji.nn.CacheNeuronConnection;
import com.anji.nn.Connection;
import com.anji.nn.Neuron;
import com.anji.nn.NeuronConnection;
import com.anji.nn.Pattern;
import com.anji.nn.RandomConnection;
import com.anji.nn.StepHourglassConnection;

/**
 * @author Philip Tucker
 */
public class MeasureNetTimingTest extends TestCase {

private final static Random rand = new Random();

private static final int CYCLES = 100000000;

private class TestConnection implements Connection {

private double value;

/**
 * @param aValue
 */
public TestConnection( double aValue ) {
	value = aValue;
}

/**
 * @see com.anji.nn.Connection#read()
 */
public double read() {
	return value;
}

/**
 * @see com.anji.nn.Connection#toXml()
 */
public String toXml() {
	return "<TestConnection/>";
}

/**
 * @see com.anji.nn.Connection#cost()
 */
public long cost() {
	return 41;
}

}

/**
 *  
 */
public MeasureNetTimingTest() {
	super();
}

/**
 * @param arg0
 */
public MeasureNetTimingTest( String arg0 ) {
	super( arg0 );
}

/**
 * @param func
 * @return ratio of neuron (w/ 1 conn) time to conn time
 */
private double doTestNeuronTimes( ActivationFunction func ) {
	Neuron uut = new Neuron( func );

	// time for neuron w/ single connection
	TestConnection c = new TestConnection( rand.nextDouble() );
	uut.addIncomingConnection( c );

	// 315 for neuron, 115 for neuron cost per conn
	double neuronSingleConnTime = doTestNeuronTime( uut, 315 + 115 + c.cost() + func.cost() );
	System.out.println( "time for " + func.toString() + " neuron w/ single conn == "
			+ neuronSingleConnTime );

	// time added to neuron for each additional connection
	double totalTime = 0.0d;
	int totalExtraConns = 0;
	int maxConns = 100;
	for ( int numConns = 2; numConns < maxConns; ++numConns ) {
		c = new TestConnection( rand.nextDouble() );
		uut.addIncomingConnection( c );
		// 315 for neuron, 115 for neuron cost per conn
		totalTime += doTestNeuronTime( uut, 315 + ( numConns * ( 115 + c.cost() ) ) + func.cost() );
		totalExtraConns += ( numConns - 1 );
	}
	double neuronExtraConnTime = ( totalTime - neuronSingleConnTime ) / totalExtraConns;
	System.out.println( "time for each additonal " + func.toString() + " conn == "
			+ neuronExtraConnTime );

	TestNeuron testNeuron = new TestNeuron();
	testNeuron.setValue( rand.nextDouble() );
	Connection testConnection = new NeuronConnection( testNeuron );
	double connTime = doTestConnectionTime( testConnection, 57, false ) + neuronExtraConnTime;

	double result = neuronSingleConnTime / connTime;
	System.out.println( func.toString() + " ratio neuron:conn == " + result );
	return result;
}

private double doTestFuncTime( ActivationFunction func, long expectedCost ) {
	assertEquals( "wrong cost for " + func.toString(), expectedCost, func.cost() );

	double val = new Random().nextDouble();
	long startTime = System.currentTimeMillis();
	for ( int i = 0; i < CYCLES; ++i ) {
		func.apply( val );
	}
	double duration = (double) ( System.currentTimeMillis() - startTime ) / CYCLES;
	System.out.println( func.toString() + " took " + duration + " millis" );
	return duration;
}

/**
 *  
 */
public void testNeuronTimes() {
	doTestNeuronTimes( ActivationFunctionFactory.getInstance().getEvSailSigmoid() );
	doTestNeuronTimes( ActivationFunctionFactory.getInstance().getInverseAbs() );
	doTestNeuronTimes( ActivationFunctionFactory.getInstance().getLinear() );
	doTestNeuronTimes( ActivationFunctionFactory.getInstance().getSigmoid() );
	doTestNeuronTimes( ActivationFunctionFactory.getInstance().getTanh() );
	doTestNeuronTimes( ActivationFunctionFactory.getInstance().getTanhCubic() );
	doTestNeuronTimes( ActivationFunctionFactory.getInstance().getStep() );
	doTestNeuronTimes( ActivationFunctionFactory.getInstance().getSignedStep() );
	doTestNeuronTimes( ActivationFunctionFactory.getInstance().getClampedLinear() );
	doTestNeuronTimes( ActivationFunctionFactory.getInstance().getSignedClampedLinear() );
}

/**
 *  
 */
public void testFuncTimes() {
	doTestFuncTime( ActivationFunctionFactory.getInstance().getEvSailSigmoid(), 166 );
	doTestFuncTime( ActivationFunctionFactory.getInstance().getInverseAbs(), 75 );
	doTestFuncTime( ActivationFunctionFactory.getInstance().getLinear(), 42 );
	doTestFuncTime( ActivationFunctionFactory.getInstance().getTanh(), 385 );
	doTestFuncTime( ActivationFunctionFactory.getInstance().getTanhCubic(), 1231 );
	doTestFuncTime( ActivationFunctionFactory.getInstance().getSigmoid(), 497 );
	doTestFuncTime( ActivationFunctionFactory.getInstance().getStep(), 40 );
	doTestFuncTime( ActivationFunctionFactory.getInstance().getSignedStep(), 40 );
	doTestFuncTime( ActivationFunctionFactory.getInstance().getClampedLinear(), 42 );
	doTestFuncTime( ActivationFunctionFactory.getInstance().getSignedClampedLinear(), 42 );
}

/**
 * test connection costs and times
 */
public void testConnectionTimes() {
	doTestConnectionTime( BiasConnection.getInstance(), 41, true );

	List controlNeurons = new ArrayList();
	for ( int i = 0; i < 5; ++i )
		controlNeurons.add( new TestNeuron() );

	// create surface
	BufferedImage bi = new BufferedImage( 10, 10, BufferedImage.TYPE_INT_ARGB );
	Java2DSurface surface = new Java2DSurface();
	surface.setImage( bi );
	for ( int x = 0; x < surface.getWidth(); ++x )
		for ( int y = 0; y < surface.getHeight(); ++y )
			bi.setRGB( x, y, rand.nextInt() | ( 255 << 24 ) );
	surface.setImage( bi );
	FloatingEye eye = new FloatingEye( "test", controlNeurons, surface, 3, new EyeMovementParms(
			0.5d, 0.5d, true, 1.0d, 1.0d, 1.0d, 1.0d ) );

	doTestConnectionTime( new EyePixelConnection( 1, 1, eye ), 821, true );

	doTestConnectionTime( new LocationXConnection( eye ), 41, true );
	doTestConnectionTime( new LocationYConnection( eye ), 41, true );
	doTestConnectionTime( new LocationZConnection( eye ), 41, true );
	doTestConnectionTime( new ThetaConnection( eye ), 41, true );

	TestNeuron n = new TestNeuron();
	n.setValue( rand.nextDouble() );
	doTestConnectionTime( new NeuronConnection( n ), 57, true );

	Pattern p = new Pattern( 1 );
	p.setValue( 0, rand.nextDouble() );
	doTestConnectionTime( p.getConnection( 0 ), 41, true );

	doTestConnectionTime( RandomConnection.getInstance(), 222, true );

	StepHourglassConnection shc = new StepHourglassConnection();
	shc.reset( (int) ( CYCLES * 0.95 ) );
	doTestConnectionTime( shc, 74, true );

	doTestCacheNeuronConnectionTime();
}

private static double doTestConnectionTime( Connection c, long expectedCost, boolean doPrint ) {
	assertEquals( "wrong cost for " + c.getClass().toString(), expectedCost, c.cost() );

	long startTime = System.currentTimeMillis();
	for ( int i = 0; i < CYCLES; ++i )
		c.read();
	double duration = (double) ( System.currentTimeMillis() - startTime ) / CYCLES;
	if ( doPrint )
		System.out.println( c.getClass().toString() + " connection took " + duration + " millis" );
	return duration;
}

private static double doTestCacheNeuronConnectionTime() {
	TestNeuron n = new TestNeuron();
	n.setValue( rand.nextDouble() );
	CacheNeuronConnection c = new CacheNeuronConnection( n );

	assertEquals( "wrong cost for " + c.getClass().toString(), 159, c.cost() );

	long startTime = System.currentTimeMillis();
	for ( int i = 0; i < CYCLES; ++i ) {
		c.read();
		c.step();
	}
	double duration = (double) ( System.currentTimeMillis() - startTime ) / CYCLES;
	System.out.println( c.getClass().toString() + " connection took " + duration + " millis" );
	return duration;
}

private static double doTestNeuronTime( Neuron n, long expectedCost ) {
	assertEquals( "wrong cost for " + n.getClass().toString(), expectedCost, n.cost() );

	long startTime = System.currentTimeMillis();
	for ( int i = 0; i < CYCLES; ++i ) {
		n.getValue();
		n.step();
	}
	double duration = (double) ( System.currentTimeMillis() - startTime ) / CYCLES;
	return duration;
}
}
