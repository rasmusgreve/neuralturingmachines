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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import com.anji.integration.AnjiActivator;
import com.anji.nn.ActivationFunction;
import com.anji.nn.ActivationFunctionFactory;
import com.anji.nn.AnjiNet;
import com.anji.nn.CacheNeuronConnection;
import com.anji.nn.Neuron;
import com.anji.nn.NeuronConnection;

/**
 * @author Philip Tucker
 */
public class AnjiActivatorTest extends TestCase {

	private final static double[] EXPECTED_XOR_RESPONSE = { -0.495585415446536d,
			0.29070029721930535d, 0.29070029721930535d, -0.495585415446536d };

	private final static double[] EXPECTED_COMPLEX_RECURRENT_RESPONSE = { -0.0625, -0.3046875d,
			-0.4375d, -0.4774169921875d, -0.4734954833984375d, -0.4595603942871094d,
			-0.4494335651397705d };

	private final static double[] EXPECTED_SIMPLE_RECURRENT_RESPONSE = { 0.0d, 3.0d,
			0.0d, 3.0d, 0.0d, 3.0d, 0.0d, 3.0d, 0.0d, 3.0d, 0.0d, 3.0d, 0.0d, 3.0d, 0.0d, 3.0d, 0.0d,
			3.0d, 0.0d, 3.0d };

	/**
	 * ctor
	 */
	public AnjiActivatorTest() {
		this( AnjiActivatorTest.class.toString() );
	}

	/**
	 * ctor
	 * 
	 * @param arg0
	 */
	public AnjiActivatorTest( String arg0 ) {
		super( arg0 );
	}

	/**
	 * test soilve xor
	 * 
	 * @throws Exception
	 */
	public void testSolveXor() throws Exception {
		AnjiActivator uut = newSolveXorNetwork( 1 );
		double[][] inputs = { { -1, -1, 1 }, { -1, 1, 1 }, { 1, -1, 1 }, { 1, 1, 1 } };

		// xor
		for ( int i = 0; i < inputs.length; ++i )
			assertEquals( "wrong xor response " + i, EXPECTED_XOR_RESPONSE[ i ], uut
					.next( inputs[ i ] )[ 0 ], 0.0d );

		// different order
		assertEquals( "wrong xor response 2", EXPECTED_XOR_RESPONSE[ 0 ],
				uut.next( inputs[ 0 ] )[ 0 ], 0.0d );
		assertEquals( "wrong xor response 1", EXPECTED_XOR_RESPONSE[ 1 ],
				uut.next( inputs[ 1 ] )[ 0 ], 0.0d );
		assertEquals( "wrong xor response 4", EXPECTED_XOR_RESPONSE[ 2 ],
				uut.next( inputs[ 2 ] )[ 0 ], 0.0d );
		assertEquals( "wrong xor response 3", EXPECTED_XOR_RESPONSE[ 3 ],
				uut.next( inputs[ 3 ] )[ 0 ], 0.0d );

		// with reset
		for ( int i = 0; i < inputs.length; ++i ) {
			assertEquals( "wrong xor response " + i, EXPECTED_XOR_RESPONSE[ i ], uut
					.next( inputs[ i ] )[ 0 ], 0.0d );
			uut.reset();
		}
	}

	/**
	 * test
	 * 
	 * @throws Exception
	 */
	public void testComplexRecurrent() throws Exception {
		AnjiActivator uut = newComplexNetwork( true, 1 );
		double[] inputs = { -1, -1, 1 };

		for ( int i = 0; i < 10; ++i ) {
			for ( int j = 1; j < EXPECTED_COMPLEX_RECURRENT_RESPONSE.length; ++j )
				assertEquals( "wrong response " + i + ", " + j,
						EXPECTED_COMPLEX_RECURRENT_RESPONSE[ j ], uut.next( inputs )[ 0 ], 0.0d );
			uut.reset();
		}
	}

	/**
	 * test
	 * 
	 * @throws Exception
	 */
	public void testSimpleRecurrent() throws Exception {
		// with only backward connections cached
		AnjiActivator uut = newSimpleRecurrentNetwork( 1, false );
		double[] inputs = { 1, 1 };
		for ( int i = 0; i < 10; ++i ) {
			for ( int j = 0; j < EXPECTED_SIMPLE_RECURRENT_RESPONSE.length; ++j )
				assertEquals( "wrong response " + i + ", " + j,
						EXPECTED_SIMPLE_RECURRENT_RESPONSE[ j ], uut.next( inputs )[ 0 ], 0.0d );
			uut.reset();
		}

		// with all connections cached - 0 initial activation, then 2 steps for every 1 before
		uut = newSimpleRecurrentNetwork( 1, true );
		for ( int i = 0; i < 10; ++i ) {
			assertEquals( "wrong initial response", 3.0, uut.next( inputs )[ 0 ], 0.0d );
			for ( int j = 0; j < EXPECTED_SIMPLE_RECURRENT_RESPONSE.length * 2; ++j )
				assertEquals( "wrong response " + i + ", " + j,
						EXPECTED_SIMPLE_RECURRENT_RESPONSE[ j / 2 ], uut.next( inputs )[ 0 ], 0.0d );
			uut.reset();
		}
	}

	/**
	 * test
	 * 
	 * @throws Exception
	 */
	public void testComplexNonRecurrent() throws Exception {
		AnjiActivator uut = newComplexNetwork( false, 1 );
		double[] inputs = { -1, -1, 1 };

		for ( int i = 0; i < 10; ++i ) {
			for ( int j = 0; j < 10; ++j )
				assertEquals( "wrong response " + i + ", " + j,
						EXPECTED_COMPLEX_RECURRENT_RESPONSE[ 0 ], uut.next( inputs )[ 0 ], 0.0d );
			uut.reset();
		}
	}

	//private static void printArray( double[] in ) {
	//	if ( in.length > 0 ) {
	//		System.out.print( in[0] );
	//		for ( int i = 1; i < in.length; ++i )
	//			System.out.print( ", " + in[i]);
	//		System.out.println();
	//	}
	//}

	/**
	 * test
	 * 
	 * @param numCycles
	 * @return activator
	 */
	public static AnjiActivator newSolveXorNetwork( int numCycles ) {
		ActivationFunction linear = ActivationFunctionFactory.getInstance().getLinear();
		ActivationFunction tanh = ActivationFunctionFactory.getInstance().getTanh();

		// 3 inputs
		Neuron in1 = new Neuron( linear );
		Neuron in2 = new Neuron( linear );
		Neuron in3 = new Neuron( linear );

		// 3 hidden
		Neuron hid1 = new Neuron( tanh );
		Neuron hid2 = new Neuron( tanh );
		Neuron hid3 = new Neuron( tanh );

		// 1 output
		Neuron out1 = new Neuron( tanh );

		// input -> hidden
		NeuronConnection in1hid1 = new NeuronConnection( in1 );
		hid1.addIncomingConnection( in1hid1 );
		in1hid1.setWeight( 1.0d );

		NeuronConnection in1hid2 = new NeuronConnection( in1 );
		hid2.addIncomingConnection( in1hid2 );
		in1hid2.setWeight( -1.0d );

		NeuronConnection in2hid1 = new NeuronConnection( in2 );
		hid1.addIncomingConnection( in2hid1 );
		in2hid1.setWeight( -1.0d );

		NeuronConnection in2hid2 = new NeuronConnection( in2 );
		hid2.addIncomingConnection( in2hid2 );
		in2hid2.setWeight( 1.0d );

		NeuronConnection in3hid1 = new NeuronConnection( in3 );
		hid1.addIncomingConnection( in3hid1 );
		in3hid1.setWeight( -0.5d );

		NeuronConnection in3hid2 = new NeuronConnection( in3 );
		hid2.addIncomingConnection( in3hid2 );
		in3hid2.setWeight( -0.5d );

		NeuronConnection in3hid3 = new NeuronConnection( in3 );
		hid3.addIncomingConnection( in3hid3 );
		in3hid3.setWeight( 1.0d );

		// hidden -> output
		NeuronConnection hid1out1 = new NeuronConnection( hid1 );
		out1.addIncomingConnection( hid1out1 );
		hid1out1.setWeight( 1.0d );

		NeuronConnection hid2out1 = new NeuronConnection( hid2 );
		out1.addIncomingConnection( hid2out1 );
		hid2out1.setWeight( 1.0d );

		NeuronConnection hid3out1 = new NeuronConnection( hid3 );
		out1.addIncomingConnection( hid3out1 );
		hid3out1.setWeight( 0.5d );

		// node collections
		List nodes = new ArrayList( 17 );
		nodes.add( in1 );
		nodes.add( in2 );
		nodes.add( in3 );
		nodes.add( hid1 );
		nodes.add( hid2 );
		nodes.add( hid3 );
		nodes.add( out1 );
		List outNodes = new ArrayList( 1 );
		outNodes.add( out1 );
		List inNodes = new ArrayList( 3 );
		inNodes.add( in1 );
		inNodes.add( in2 );
		inNodes.add( in3 );

		// build network
		Collections.shuffle( nodes ); // make sure there's no order on dependency
		AnjiNet net = new AnjiNet( nodes, inNodes, outNodes, new ArrayList(), "test" );
		AnjiActivator result = new AnjiActivator( net, numCycles );
		return result;
	}

	/**
	 * @param recurrent
	 * @param numCycles
	 * @return activator
	 */
	public static AnjiActivator newComplexNetwork( boolean recurrent, int numCycles ) {
		ActivationFunction linear = ActivationFunctionFactory.getInstance().getLinear();

		// 3 inputs
		Neuron in1 = new Neuron( linear );
		Neuron in2 = new Neuron( linear );
		Neuron in3 = new Neuron( linear );

		// 3 hidden1
		Neuron hid1_1 = new Neuron( linear );
		Neuron hid1_2 = new Neuron( linear );
		Neuron hid1_3 = new Neuron( linear );

		// 2 hidden2
		Neuron hid2_1 = new Neuron( linear );
		Neuron hid2_2 = new Neuron( linear );

		// 1 output
		Neuron out1 = new Neuron( linear );

		// input -> hidden1
		NeuronConnection in1hid1_1 = new NeuronConnection( in1 );
		hid1_1.addIncomingConnection( in1hid1_1 );
		in1hid1_1.setWeight( 1.0d );

		NeuronConnection in1hid1_2 = new NeuronConnection( in1 );
		hid1_2.addIncomingConnection( in1hid1_2 );
		in1hid1_2.setWeight( -1.0d );

		NeuronConnection in2hid1_1 = new NeuronConnection( in2 );
		hid1_1.addIncomingConnection( in2hid1_1 );
		in2hid1_1.setWeight( -1.0d );

		NeuronConnection in2hid1_2 = new NeuronConnection( in2 );
		hid1_2.addIncomingConnection( in2hid1_2 );
		in2hid1_2.setWeight( 1.0d );

		NeuronConnection in3hid1_1 = new NeuronConnection( in3 );
		hid1_1.addIncomingConnection( in3hid1_1 );
		in3hid1_1.setWeight( -0.5d );

		NeuronConnection in3hid1_2 = new NeuronConnection( in3 );
		hid1_2.addIncomingConnection( in3hid1_2 );
		in3hid1_2.setWeight( -0.5d );

		NeuronConnection in3hid1_3 = new NeuronConnection( in3 );
		hid1_3.addIncomingConnection( in3hid1_3 );
		in3hid1_3.setWeight( 1.0d );

		// input -> hidden2
		NeuronConnection in1hid2_1 = new NeuronConnection( in1 );
		hid2_1.addIncomingConnection( in1hid2_1 );
		in1hid2_1.setWeight( 0.75d );

		NeuronConnection in1hid2_2 = new NeuronConnection( in1 );
		hid2_2.addIncomingConnection( in1hid2_2 );
		in1hid2_2.setWeight( 0.25d );

		// input -> output
		NeuronConnection in2out1 = new NeuronConnection( in2 );
		out1.addIncomingConnection( in2out1 );
		in2out1.setWeight( -0.50d );

		// hidden1 -> hidden2
		NeuronConnection hid1_1hid2_1 = new NeuronConnection( hid1_1 );
		hid2_1.addIncomingConnection( hid1_1hid2_1 );
		hid1_1hid2_1.setWeight( -1.0d );

		// hidden1 -> output
		NeuronConnection hid1_1out1 = new NeuronConnection( hid1_1 );
		out1.addIncomingConnection( hid1_1out1 );
		hid1_1out1.setWeight( 1.0d );

		NeuronConnection hid1_2out1 = new NeuronConnection( hid1_2 );
		out1.addIncomingConnection( hid1_2out1 );
		hid1_2out1.setWeight( 1.0d );

		NeuronConnection hid1_3out1 = new NeuronConnection( hid1_3 );
		out1.addIncomingConnection( hid1_3out1 );
		hid1_3out1.setWeight( 0.5d );

		NeuronConnection hid2_2out1 = new NeuronConnection( hid2_2 );
		out1.addIncomingConnection( hid2_2out1 );
		hid2_2out1.setWeight( 0.25d );

		// recurrent
		Collection recurrentConns = new ArrayList();
		if ( recurrent ) {
			// output -> output
			NeuronConnection out1out1 = new CacheNeuronConnection( out1 );
			out1.addIncomingConnection( out1out1 );
			out1out1.setWeight( 0.25d );
			recurrentConns.add( out1out1 );

			// output -> hidden2
			NeuronConnection out1hid2_1 = new CacheNeuronConnection( out1 );
			hid2_1.addIncomingConnection( out1hid2_1 );
			out1hid2_1.setWeight( 0.25d );
			recurrentConns.add( out1hid2_1 );

			// output -> hidden1
			NeuronConnection out1hid1_2 = new CacheNeuronConnection( out1 );
			hid1_2.addIncomingConnection( out1hid1_2 );
			out1hid1_2.setWeight( 0.25d );
			recurrentConns.add( out1hid1_2 );

			// output -> input
			NeuronConnection out1in3 = new CacheNeuronConnection( out1 );
			in3.addIncomingConnection( out1in3 );
			out1in3.setWeight( 0.25d );
			recurrentConns.add( out1in3 );

			// hidden2 -> hidden2
			NeuronConnection hid2_2hid2_1 = new CacheNeuronConnection( hid2_2 );
			hid2_1.addIncomingConnection( hid2_2hid2_1 );
			hid2_2hid2_1.setWeight( 0.25d );
			recurrentConns.add( hid2_2hid2_1 );

			// hidden2 -> hidden1
			NeuronConnection hid2_2hid1_2 = new CacheNeuronConnection( hid2_2 );
			hid1_2.addIncomingConnection( hid2_2hid1_2 );
			hid2_2hid1_2.setWeight( 0.25d );
			recurrentConns.add( hid2_2hid1_2 );

			// hidden2 -> input
			NeuronConnection hid2_2in2 = new CacheNeuronConnection( hid2_2 );
			in2.addIncomingConnection( hid2_2in2 );
			hid2_2in2.setWeight( 0.25d );
			recurrentConns.add( hid2_2in2 );

			// hidden1 -> input
			NeuronConnection hid1_2in2 = new CacheNeuronConnection( hid1_2 );
			in2.addIncomingConnection( hid1_2in2 );
			hid1_2in2.setWeight( 0.25d );
			recurrentConns.add( hid1_2in2 );

			// hidden1 -> hidden1
			NeuronConnection hid1_1hid1_1 = new CacheNeuronConnection( hid1_1 );
			hid1_1.addIncomingConnection( hid1_1hid1_1 );
			hid1_1hid1_1.setWeight( 0.25d );
			recurrentConns.add( hid1_1hid1_1 );

			// input -> input
			NeuronConnection in3in2 = new CacheNeuronConnection( in3 );
			in2.addIncomingConnection( in3in2 );
			in3in2.setWeight( 0.25d );
			recurrentConns.add( in3in2 );
		}

		// node collections
		List nodes = new ArrayList( 17 );
		nodes.add( in1 );
		nodes.add( in2 );
		nodes.add( in3 );
		nodes.add( hid1_1 );
		nodes.add( hid1_2 );
		nodes.add( hid1_3 );
		nodes.add( hid2_1 );
		nodes.add( hid2_2 );
		nodes.add( out1 );
		List outNodes = new ArrayList( 1 );
		outNodes.add( out1 );
		List inNodes = new ArrayList( 3 );
		inNodes.add( in1 );
		inNodes.add( in2 );
		inNodes.add( in3 );

		// build network
		Collections.shuffle( nodes ); // make sure there's no order on dependency
		AnjiNet net = new AnjiNet( nodes, inNodes, outNodes, recurrentConns, "test" );
		AnjiActivator result = new AnjiActivator( net, numCycles );
		return result;
	}

	/**
	 * @param numCycles
	 * @param allRecurrent
	 * @return activator
	 */
	public static AnjiActivator newSimpleRecurrentNetwork( int numCycles, boolean allRecurrent ) {
		ActivationFunction linear = ActivationFunctionFactory.getInstance().getLinear();

		//
		// neurons
		//

		// 2 inputs
		Neuron in1 = new Neuron( linear );
		Neuron in2 = new Neuron( linear );

		// 1 output
		Neuron out1 = new Neuron( linear );

		// 
		// connections
		//
		Collection recurrentConns = new ArrayList();

		// input -> output
		NeuronConnection in1out1 = allRecurrent ? new CacheNeuronConnection( in1 )
				: new NeuronConnection( in1 );
		if ( allRecurrent )
			recurrentConns.add( in1out1 );
		out1.addIncomingConnection( in1out1 );
		in1out1.setWeight( 1.0d );

		NeuronConnection in2out1 = allRecurrent ? new CacheNeuronConnection( in2 )
				: new NeuronConnection( in2 );
		if ( allRecurrent )
			recurrentConns.add( in2out1 );
		out1.addIncomingConnection( in2out1 );
		in2out1.setWeight( 2.0d );

		// output -> input
		NeuronConnection out1in1 = new CacheNeuronConnection( out1 );
		recurrentConns.add( out1in1 );
		in1.addIncomingConnection( out1in1 );
		out1in1.setWeight( 3.0d );

		NeuronConnection out1in2 = new CacheNeuronConnection( out1 );
		recurrentConns.add( out1in2 );
		in2.addIncomingConnection( out1in2 );
		out1in2.setWeight( -2.0d );

		// node collections
		List nodes = new ArrayList( 17 );
		nodes.add( in1 );
		nodes.add( in2 );
		nodes.add( out1 );
		List outNodes = new ArrayList( 1 );
		outNodes.add( out1 );
		List inNodes = new ArrayList( 3 );
		inNodes.add( in1 );
		inNodes.add( in2 );

		// build network
		Collections.shuffle( nodes ); // make sure there's no order on dependency
		AnjiNet net = new AnjiNet( nodes, inNodes, outNodes, recurrentConns, "test" );
		AnjiActivator result = new AnjiActivator( net, numCycles );
		return result;
	}

}
