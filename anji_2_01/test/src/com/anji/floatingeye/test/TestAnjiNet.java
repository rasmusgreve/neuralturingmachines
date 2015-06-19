/*
 * Copyright (C) 2004  Derek James and Philip Tucker
 *
 * This file is part of ANJI (Another NEAT Java Implementation).
 *
 * ANJI is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * created by Philip Tucker on Jul 2, 2004
 */

package com.anji.floatingeye.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.anji.nn.AnjiNet;


/**
 * @author Philip Tucker
 */
public class TestAnjiNet extends AnjiNet {

	/**
	 * @param inDim
	 * @param outDim
	 */
	public TestAnjiNet( int inDim, int outDim ) {
		super();
		List testInNeurons = new ArrayList();
		List testOutNeurons = new ArrayList();
		List testAllNeurons = new ArrayList();
		for ( int i = 0; i < inDim; ++i ) {
			TestNeuron n = new TestNeuron();
			testInNeurons.add( n );
			testAllNeurons.add( n );
		}
		for ( int i = 0; i < outDim; ++i ) {
			TestNeuron n = new TestNeuron();
			testOutNeurons.add( n );
			testAllNeurons.add( n );
		}
		
		// make sure there's no dependencey on order
		Collections.shuffle( testAllNeurons );

		init( testAllNeurons, testInNeurons, testOutNeurons, new ArrayList(), "testAnjiNet" );
	}


	/**
	 * @param idx
	 * @return input <code>TestNeuron</code>.
	 */
	public synchronized TestNeuron getInNeuron( int idx ) {
		return (TestNeuron) super.getInputNeuron( idx );
	}

	/**
	 * @param idx
	 * @return output <code>TestNeuron</code>.
	 */
	public synchronized TestNeuron getOutNeuron( int idx ) {
		return (TestNeuron) super.getOutputNeuron( idx );
	}
}

