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
import com.anji.nn.NeuronConnection;
import com.anji.nn.Pattern;
import com.anji.nn.CacheNeuronConnection;

/**
 * @author Philip Tucker
 */
public class NeuronConnectionTest extends TestCase {

/**
 * ctor
 */
public NeuronConnectionTest() {
	this( NeuronConnectionTest.class.toString() );
}

/**
 * ctor
 * @param arg0
 */
public NeuronConnectionTest( String arg0 ) {
	super( arg0 );
}

/**
 * test feed forward connection
 */
public void testForwardConnection() {
	double weight = 0.5d;
	Pattern in = new Pattern( 1 );
	Neuron incoming = new Neuron( ActivationFunctionFactory.getInstance().getSigmoid() );
	incoming.addIncomingConnection( in.getConnection( 0 ) );

	NeuronConnection uut = new NeuronConnection( incoming );
	uut.setWeight( weight );

	// note: call step before in.setValue() to make sure step can collect input values from
	// previous cycles before
	//   processing current cycle
	incoming.step();
	in.setValue( 0, -5.0d );
	assertEquals( "wrong value 1", weight * 2.028042461056263E-11d, uut.read(), 0.0d );

	incoming.step();
	in.setValue( 0, -1.0d );
	assertEquals( "wrong value 2", weight * 0.007215565412965859, uut.read(), 0.0d );

	incoming.step();
	in.setValue( 0, -0.1d );
	assertEquals( "wrong value 3", weight * 0.37932192474098875, uut.read(), 0.0d );

	weight = -0.77;
	uut.setWeight( weight );

	incoming.step();
	in.setValue( 0, 0.0d );
	assertEquals( "wrong value 4", weight * 0.5, uut.read(), 0.0d );

	incoming.step();
	in.setValue( 0, 0.1d );
	assertEquals( "wrong value 5", weight * 0.6206780752590112, uut.read(), 0.0d );

	incoming.step();
	in.setValue( 0, 1.0d );
	assertEquals( "wrong value 6", weight * 0.9927844345870342, uut.read(), 0.0d );

	incoming.step();
	in.setValue( 0, 5.0d );
	assertEquals( "wrong value 7", weight * ( 1 - 2.028042461056263E-11d ), uut.read(), 0.0d );
}

/**
 * test recurrent connection
 */
public void testBackwardConnection() {
	double weight = 0.5d;
	Pattern in = new Pattern( 1 );
	Neuron incoming = new Neuron( ActivationFunctionFactory.getInstance().getLinear() );
	incoming.addIncomingConnection( in.getConnection( 0 ) );

	// recurrent
	CacheNeuronConnection uut = new CacheNeuronConnection( incoming );
	uut.setWeight( weight );

	// return 0 because connection returns previous step value
	in.setValue( 0, -5.0d );
	uut.step();
	incoming.step();
	assertEquals( "wrong value 0", weight * -5.0d, uut.read(), 0.0d );
	incoming.getValue(); // neuron == -5.0

	// step with same value
	uut.step(); // connection == -5.0
	incoming.step();
	assertEquals( "wrong value 1", weight * -5.0d, uut.read(), 0.0d );
	incoming.getValue(); // neuron == -5.0

	// step with new value
	in.setValue( 0, -0.1d );
	uut.step(); // connection = -5.0
	incoming.step();
	assertEquals( "wrong value 2", weight * -5.0d, uut.read(), 0.0d );
	incoming.getValue(); // neuron = -0.1

	// step with new value
	in.setValue( 0, 0.0d );
	uut.step(); // connection = -0.1
	incoming.step();
	assertEquals( "wrong value 3", weight * -0.1d, uut.read(), 0.0d );
	incoming.getValue(); // neuron = 0.0

	// step with new value
	in.setValue( 0, 2.0d );
	uut.step(); // connection = 0.0
	incoming.step();
	assertEquals( "wrong value 4", weight * 0.0d, uut.read(), 0.0d );
	incoming.getValue(); // neuron = 2.0

	weight = -0.77;
	uut.setWeight( weight );

	in.setValue( 0, 4.0d );
	uut.step(); // connection = 2.0
	incoming.step();
	assertEquals( "wrong value 5", weight * 2.0d, uut.read(), 0.0d );
	incoming.getValue(); // neuron = 4.0

	uut.step(); // connection = 4.0
	incoming.step();
	assertEquals( "wrong value 6", weight * 4.0d, uut.read(), 0.0d );
}

}
