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
 * created by Philip Tucker on Dec 6, 2004
 */

package com.anji.fingerprint;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.anji.imaging.Identifier;

/**
 * @author Philip Tucker
 */
public class Classifier {

	private static Logger logger = Logger.getLogger( Classifier.class );

	private Map identifiers = new HashMap();

	/**
	 *  
	 */
	public Classifier() {
		super();
	}

	/**
	 * add identifier for this classification
	 * 
	 * @param classification
	 * @param identifier
	 */
	public void addIdentifier( Classification classification, Identifier identifier ) {
		Collection classIdentifiers = (Collection) identifiers.get( classification );
		if ( classIdentifiers == null ) {
			classIdentifiers = new ArrayList();
			identifiers.put( classification, classIdentifiers );
		}
		classIdentifiers.add( identifier );
	}

	/**
	 * @param imgFile
	 * @return result of classifcation
	 * @throws IOException
	 */
	public ClassifierResult classify( File imgFile ) throws IOException {
		long startTime = System.currentTimeMillis();

		ClassifierResult result = new ClassifierResult();

		// loop through all classifications
		Iterator classificationIterator = identifiers.keySet().iterator();
		while ( classificationIterator.hasNext() ) {
			// for each classification, loop through all identifiers and compute average
			float total = 0f;
			Classification classification = (Classification) classificationIterator.next();
			Collection classIdentifiers = (Collection) identifiers.get( classification );
			if ( classIdentifiers != null ) {
				Iterator identifierIterator = classIdentifiers.iterator();
				while ( identifierIterator.hasNext() ) {
					Identifier identifier = (Identifier) identifierIterator.next();
					total += identifier.identify( imgFile );
				}
				result.setConfidence( classification, total / classIdentifiers.size() );
			}
		}

		logger.info( "classification took " + ( System.currentTimeMillis() - startTime ) + " ms" );
		return result;
	}
}
