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
 * Created on Jul 13, 2005 by Philip Tucker
 */
package com.anji.neat.test;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;

import com.anji.neat.ConnectionAllele;
import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.RemoveConnectionMutationOperator;

/**
 * @author Philip Tucker
 */
public class RemoveConnectionMutationOperatorSmallTest extends
		RemoveConnectionMutationOperatorTest {

/**
 *  
 */
public RemoveConnectionMutationOperatorSmallTest() {
	super();
}

/**
 * @param aName
 */
public RemoveConnectionMutationOperatorSmallTest( String aName ) {
	super( aName );
}

/**
 * @see org.jgap.test.MutationOperatorTest#initUut()
 */
protected void initUut() throws Exception {
	uut = new RemoveConnectionMutationOperator( MUTATION_RATE, MAX_WEIGHT_REMOVED,
			RemoveConnectionMutationOperator.Strategy.SMALL );
	assertEquals( "wrong mutation rate", MUTATION_RATE, uut.getMutationRate(), 0.0f );
}

/**
 * @see com.anji.neat.test.RemoveConnectionMutationOperatorTest#assertStrategy(java.util.Collection,
 * java.util.SortedMap)
 */
protected void assertStrategy( Collection removedConns, SortedMap remainingConns ) {
	// get largest removed conneciton weight
	double largestRemovedWeight = 0;
	Iterator it = removedConns.iterator();
	while ( it.hasNext() ) {
		ConnectionAllele connAllele = (ConnectionAllele) it.next();
		double absWeight = Math.abs( connAllele.getWeight() );
		if ( absWeight > largestRemovedWeight )
			largestRemovedWeight = absWeight;
	}

	// make sure no smaller weights exist in remaining connections
	it = remainingConns.values().iterator();
	while ( it.hasNext() ) {
		ConnectionAllele connAllele = (ConnectionAllele) it.next();
		double absWeight = Math.abs( connAllele.getWeight() );
		assertTrue( "connection with weight " + largestRemovedWeight
				+ " removed, but connection remained with weight " + absWeight,
				absWeight >= largestRemovedWeight );
	}

	// candidate connections
	int totalCandidateConnections = 0;
	List candidateConns = NeatChromosomeUtility.getConnectionList( remainingConns.values() );
	it = candidateConns.iterator();
	while ( it.hasNext() ) {
		ConnectionAllele connAllele = (ConnectionAllele) it.next();
		if ( Math.abs( connAllele.getWeight() ) <= MAX_WEIGHT_REMOVED )
			++totalCandidateConnections;
	}

	if ( totalCandidateConnections > ( 1 / MUTATION_RATE ) )
		assertTrue( "no mutations", removedConns.size() > 0 );
}
}
