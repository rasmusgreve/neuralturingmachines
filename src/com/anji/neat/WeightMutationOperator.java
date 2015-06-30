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
 * created by Philip Tucker on Feb 16, 2003
 */
package com.anji.neat;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgap.ChromosomeMaterial;
import org.jgap.Configuration;
import org.jgap.MutationOperator;

import com.anji.integration.AnjiRequiredException;
import com.anji.util.Configurable;
import com.anji.util.Properties;

/**
 * Implements NEAT perturb connection weight mutation according to <a
 * href="http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf"> Evolving Neural Networks
 * through Augmenting Topologies </a>.
 * 
 * @author Philip Tucker
 */
public class WeightMutationOperator extends MutationOperator implements Configurable {

/**
 * properties key, perturb weight mutation rate
 */
private static final String WEIGHT_MUTATE_RATE_KEY = "weight.mutation.rate";

/**
 * properties key, standard deviation of perturb weight mutation
 */
private static final String WEIGHT_MUTATE_STD_DEV_KEY = "weight.mutation.std.dev";

/**
 * default mutation rate
 */
public static final float DEFAULT_MUTATE_RATE = 0.10f;

/**
 * default standard deviation for weight delta
 */
public final static float DEFAULT_STD_DEV = 1.0f;

private float stdDev = DEFAULT_STD_DEV;

/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties props ) throws Exception {
	setMutationRate( props.getFloatProperty( WEIGHT_MUTATE_RATE_KEY, DEFAULT_MUTATE_RATE ) );
	stdDev = props.getFloatProperty( WEIGHT_MUTATE_STD_DEV_KEY, DEFAULT_STD_DEV );
}

/**
 * @see MutationOperator#MutationOperator(float)
 */
public WeightMutationOperator() {
	super( DEFAULT_MUTATE_RATE );
}

/**
 * @param newMutationRate
 * @see WeightMutationOperator#WeightMutationOperator(float, float)
 */
public WeightMutationOperator( float newMutationRate ) {
	this( newMutationRate, DEFAULT_STD_DEV );
}

/**
 * @param newMutationRate
 * @param newStdDev
 * @see MutationOperator#MutationOperator(float)
 */
public WeightMutationOperator( float newMutationRate, float newStdDev ) {
	super( newMutationRate );
	stdDev = newStdDev;
}

/**
 * Removes from <code>genesToAdd</code> and adds to <code>genesToRemove</code> all
 * connection genes that are modified.
 * 
 * @param jgapConfig The current active genetic configuration.
 * @param target chromosome material to mutate
 * @param genesToAdd <code>Set</code> contains <code>Gene</code> objects
 * @param genesToRemove <code>Set</code> contains <code>Gene</code> objects
 */
protected void mutate( Configuration jgapConfig, final ChromosomeMaterial target,
		Set genesToAdd, Set genesToRemove ) {
	if ( ( jgapConfig instanceof NeatConfiguration ) == false )
		throw new AnjiRequiredException( NeatConfiguration.class.toString() );
	NeatConfiguration config = (NeatConfiguration) jgapConfig;

	List conns = NeatChromosomeUtility.getConnectionList( target.getAlleles() );
	Collections.shuffle( conns, config.getRandomGenerator() );

	int numMutations = numMutations( config.getRandomGenerator(), conns.size() );
	Iterator iter = conns.iterator();
	int i = 0;
	while ( ( i++ < numMutations ) && iter.hasNext() ) {
		ConnectionAllele origAllele = (ConnectionAllele) iter.next();
		double nextWeight = origAllele.getWeight()
				+ ( config.getRandomGenerator().nextGaussian() * getStdDev() );
		if ( nextWeight > config.getMaxConnectionWeight() )
			nextWeight = config.getMaxConnectionWeight();
		else if ( nextWeight < config.getMinConnectionWeight() )
			nextWeight = config.getMinConnectionWeight();

		ConnectionAllele newAllele = (ConnectionAllele) origAllele.cloneAllele();
		newAllele.setWeight( nextWeight );

		genesToRemove.add( origAllele );
		genesToAdd.add( newAllele );
	}
}

/**
 * @return standard deviation for weight delta
 */
public float getStdDev() {
	return stdDev;
}

}
