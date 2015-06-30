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

package com.anji.floatingeye;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.anji.imaging.DoubleLocation2D;
import com.anji.imaging.RangeTranslator;
import com.anji.imaging.RangeTranslatorFactory;
import com.anji.imaging.Surface;
import com.anji.nn.AnjiNet;
import com.anji.nn.BiasConnection;
import com.anji.nn.Connection;
import com.anji.nn.Neuron;
import com.anji.util.XmlPersistable;

/**
 * TODO - non square surface & non square eyes
 * 
 * Implements a floating eye that integrates with an <code>AnjiNet</code> ANN.
 * @author Philip Tucker
 */
public class AnjiNetFloatingEye implements XmlPersistable {

private static Logger logger = Logger.getLogger( AnjiNetFloatingEye.class );

private final static String XML_TAG = "AnjiNetFloatingEye";

private AnjiNet net;

private Neuron affinityOutputNeuron;

private RangeTranslator affinityTranslator;

private FloatingEye floatingEye;

private AffinityListener affinityListener;

/**
 * Connects and <code>AnjiNet</code> to a <code>FloatingEye</code>.
 * 
 * @param aNet in general, input dimensions must be at least 5 (x, y, z, theta, bias) + 2*eyeDim
 * (1 input for each coordinate in eye) + number of connections in additionalInputConnections,
 * and output dimension must be at least 5 (x, y, z, rotate, affinity); but, any input for which
 * the corresponding maxMovePerStep parameter is 0 is omitted
 * @param aSurface 2-d matrix of ints
 * @param anEyeDim
 * @param movementParms paramaters controlling movement of eye
 * @param anAffinityListener updated when affinity is updated
 * @param additionalInputConnections input connections in addition to x, y, z, theta, and bias
 */
public AnjiNetFloatingEye( AnjiNet aNet, Surface aSurface, int anEyeDim,
		EyeMovementParms movementParms, AffinityListener anAffinityListener,
		List additionalInputConnections ) {
	if ( anEyeDim < 1 )
		throw new IllegalArgumentException( "eye dimensions must be >= 1" );

	// input includes bias + additional + eye pixels
	int inputCount = 1 + additionalInputConnections.size() + ( anEyeDim * anEyeDim );

	// output includes affinity
	int outputCount = 1;

	// 4 input and output neurons for x,y,z, theta; only require the ones for which
	// maxMovePerStep > 0
	if ( movementParms.getMaxXMovePerStep() > 0d ) {
		++inputCount;
		++outputCount;
	}
	if ( movementParms.getMaxYMovePerStep() > 0d ) {
		++inputCount;
		++outputCount;
	}
	if ( movementParms.getMaxZMovePerStep() > 0d ) {
		++inputCount;
		++outputCount;
	}
	if ( movementParms.getMaxThetaMovePerStep() > 0d ) {
		++inputCount;
		++outputCount;
	}

	if ( aNet.getInputDimension() < inputCount )
		throw new IllegalArgumentException( "network input dimensions must be >= " + inputCount );

	// 1 each for x, y, z, theta, affinity
	if ( aNet.getOutputDimension() < outputCount )
		throw new IllegalArgumentException( "network output dimensions must be >= " + outputCount );

	floatingEye = new FloatingEye( aNet.getName(), aNet.getOutputNeurons( 0, outputCount - 1 ),
			aSurface, anEyeDim, movementParms );

	net = aNet;
	affinityOutputNeuron = aNet.getOutputNeuron( outputCount - 1 );
	affinityListener = anAffinityListener;
	affinityTranslator = RangeTranslatorFactory.getInstance().getTranslator(
			affinityOutputNeuron.getFunc().getMinValue(),
			affinityOutputNeuron.getFunc().getMaxValue(), 0, 1 );

	connectToInputNeurons( additionalInputConnections, movementParms.getMaxXMovePerStep() > 0d,
			movementParms.getMaxYMovePerStep() > 0d, movementParms.getMaxZMovePerStep() > 0d,
			movementParms.getMaxThetaMovePerStep() > 0d );
	reset();
}

/**
 * @return <code>String</code> XML representation of object
 */
public synchronized String toXml() {
	StringBuffer result = new StringBuffer();
	result.append( "<" ).append( XML_TAG ).append( ">" );
	result.append( net.toXml() );
	result.append( floatingEye.toXml() );
	result.append( "</" ).append( XML_TAG ).append( ">" );
	return result.toString();
}

/**
 * @see Object#toString()
 */
public String toString() {
	return floatingEye.toString()
			+ ": affinity="
			+ DoubleLocation2D.TO_STRING_FORMAT.format( affinityTranslator
					.translate( affinityOutputNeuron.getValue() ) );
}

/**
 * reset neural net and state of eye
 */
public synchronized void reset() {
	net.reset();
	floatingEye.reset();
	affinityListener.reset();
}

/**
 * @param additionalInputConnections <code>List</code> contains <code>Connection</code>
 * objects
 * @param includeX
 * @param includeY
 * @param includeZ
 * @param includeTheta
 */
private synchronized void connectToInputNeurons( List additionalInputConnections,
		boolean includeX, boolean includeY, boolean includeZ, boolean includeTheta ) {
	int inputIdx = 0;

	// x connection
	if ( includeX ) {
		Neuron xInputNeuron = net.getInputNeuron( inputIdx++ );
		xInputNeuron.addIncomingConnection( new LocationXConnection( floatingEye ) );
	}

	// y connection
	if ( includeY ) {
		Neuron yInputNeuron = net.getInputNeuron( inputIdx++ );
		yInputNeuron.addIncomingConnection( new LocationYConnection( floatingEye ) );
	}

	// z connection
	if ( includeZ ) {
		Neuron zInputNeuron = net.getInputNeuron( inputIdx++ );
		zInputNeuron.addIncomingConnection( new LocationZConnection( floatingEye ) );
	}

	// theta connection
	if ( includeTheta ) {
		Neuron thetaInputNeuron = net.getInputNeuron( inputIdx++ );
		thetaInputNeuron.addIncomingConnection( new ThetaConnection( floatingEye ) );
	}

	// bias
	Neuron biasNeuron = net.getInputNeuron( inputIdx++ );
	biasNeuron.addIncomingConnection( BiasConnection.getInstance() );

	// additional input connections
	Iterator it = additionalInputConnections.iterator();
	while ( it.hasNext() ) {
		Neuron n = net.getInputNeuron( inputIdx++ );
		n.addIncomingConnection( (Connection) it.next() );
	}

	// surface connections
	// this would need to be synchronized, but it's only called from the constructor
	for ( int x = 0; x < floatingEye.getWidth(); ++x ) {
		for ( int y = 0; y < floatingEye.getHeight(); ++y ) {
			EyePixelConnection pixConn = new EyePixelConnection( x, y, floatingEye );
			Neuron pixNeuron = net.getInputNeuron( inputIdx++ );
			pixNeuron.addIncomingConnection( pixConn );
		}
	}

	if ( net.getInputDimension() != inputIdx )
		logger.warn( "AnjiNetFloatingEye did not use all inputs from " + net.toString() );
}

/**
 * @param count number of times to step
 * @see AnjiNetFloatingEye#step()
 */
public void step( int count ) {
	for ( int i = 0; i < count; ++i )
		step();
}

/**
 * load eye, step network, and update position from network output
 */
public synchronized void step() {
	floatingEye.step();
	affinityListener.updateAffinity( floatingEye.getSurfaceLocation(), affinityTranslator
			.translate( affinityOutputNeuron.getValue() ) );
	net.step();
}

/**
 * @return number of steps eye has performed
 */
public synchronized int getStepNum() {
	return floatingEye.getStepNum();
}

/**
 * @return cost
 * @see AnjiNet#cost()
 */
public long cost() {
	return net.cost();
}

/**
 * @return floating eye
 */
public FloatingEye getFloatingEye() {
	return floatingEye;
}

/**
 * @see com.anji.util.XmlPersistable#getXmlRootTag()
 */
public String getXmlRootTag() {
	return null;
}

/**
 * @see com.anji.util.XmlPersistable#getXmld()
 */
public String getXmld() {
	return null;
}
}
