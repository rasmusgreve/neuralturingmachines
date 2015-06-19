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

import java.util.Collection;

import com.anji.nn.ActivationFunctionFactory;
import com.anji.nn.Neuron;


/**
 * @author Philip Tucker
 */
public class TestNeuron extends Neuron {

	/**
	 * ctor
	 */
	public TestNeuron() {
		super( ActivationFunctionFactory.getInstance().getTanh() );
	}

	/**
	 * @return activation value
	 */
	public synchronized double getValue() {
		return super.value;
	}

	/**
	 * @param aValue activation value
	 */
	public synchronized void setValue( double aValue ) {
		super.value = aValue;
	}
	
	/**
	 * @see com.anji.nn.Neuron#getIncomingConns()
	 */
	public Collection getIncomingConns() {
		return super.getIncomingConns();
	}

}

