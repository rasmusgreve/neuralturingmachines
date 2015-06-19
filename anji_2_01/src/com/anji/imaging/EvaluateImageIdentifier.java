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
 * created by Philip Tucker on Jul 23, 2004
 */

package com.anji.imaging;

import java.util.ArrayList;

import org.jgap.Chromosome;
import org.jgap.Configuration;

import com.anji.Copyright;
import com.anji.persistence.FilePersistence;
import com.anji.util.DummyConfiguration;
import com.anji.util.Properties;

/**
 * @author Philip Tucker
 */
public class EvaluateImageIdentifier {

/**
 * @param args
 * @throws Exception
 */
public static void main( String[] args ) throws Exception {
	System.out.println( Copyright.STRING );
	if ( args.length < 2 )
		System.err
				.println( "usage: <cmd> <properties-file> <chromosome1-id> [<chromosome2-id> <chromosome3-id> ...]" );

	Properties props = new Properties( args[ 0 ] );
	IdentifyImageFitnessFunction iiff = new IdentifyImageFitnessFunction();
	iiff.init( props );
	FilePersistence db = new FilePersistence();
	db.init( props );

	Configuration config = new DummyConfiguration();
	ArrayList chroms = new ArrayList();
	for ( int i = 1; i < args.length; ++i ) {
		Chromosome chrom = db.loadChromosome( args[ i ], config );
		chroms.add( chrom );
	}
	int fitness = ( chroms.size() > 1 ) ? iiff.evaluateEnsemble( chroms ) : iiff
			.evaluate( (Chromosome) chroms.get( 0 ) );
	System.out
			.println( "fitness = " + fitness + " / " + IdentifyImageFitnessFunction.MAX_FITNESS );
}
}
