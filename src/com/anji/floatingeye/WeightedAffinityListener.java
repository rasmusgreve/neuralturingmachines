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
 * created by Derek James on Aug 14, 2004
 */

package com.anji.floatingeye;

import java.util.Arrays;

import com.anji.imaging.IntLocation2D;

/**
 * @author Derek James
 */
public class WeightedAffinityListener implements AffinityListener {

private int stepNumber = 0;

private double[] affinities = new double[0];

/**
 * number of steps to average for weighted sum; this also resets weighted affinity listener
 * @param numSteps
 */
public synchronized void reset( int numSteps ) {
	affinities = new double[ numSteps ];
	reset();
}

/**
 * @see com.anji.floatingeye.AffinityListener#updateAffinity(com.anji.imaging.IntLocation2D,
 * double)
 */
public synchronized void updateAffinity( IntLocation2D pos, double value ) {
	if ( stepNumber >= affinities.length )
		throw new IllegalArgumentException( "stepped past end of weighted affinities array" );
	affinities[ stepNumber ] = value;
	stepNumber++;
}

/**
 * @return max affinity.
 */
public synchronized double getWeightedAffinity() {
	double runningTotal = 0;
	int runningMax = 0;
	for ( int i = 0; i < stepNumber; i++ ) {
		runningTotal += ( affinities[ i ] * i * i );
		runningMax += ( i * i );
	}
	return ( runningTotal / runningMax );
}

/**
 * @see com.anji.floatingeye.AffinityListener#reset()
 */
public synchronized void reset() {
	stepNumber = 0;
	Arrays.fill( affinities, 0 );
}

/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	return getWeightedAffinity() + " [weighted]";
}
}
