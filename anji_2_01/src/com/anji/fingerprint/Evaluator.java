/*
 * Copyright (C) 2004 Derek James and Philip Tucker This file is part of ANJI (Another NEAT Java
 * Implementation). ANJI is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA created by Philip Tucker on Dec 11, 2004
 */
package com.anji.fingerprint;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.jgap.Chromosome;

import com.anji.Copyright;
import com.anji.floatingeye.AnjiNetFloatingEyeIdentifierFactory;
import com.anji.imaging.IdentifyImageFitnessFunction;
import com.anji.imaging.ImageFileFilter;
import com.anji.imaging.Surface;
import com.anji.integration.AnjiNetTranscriber;
import com.anji.nn.AnjiNet;
import com.anji.persistence.Persistence;
import com.anji.util.Configurable;
import com.anji.util.DummyConfiguration;
import com.anji.util.Properties;

/**
 * Evaluator
 */
public class Evaluator implements Configurable {

private static Logger logger = Logger.getLogger( Evaluator.class );

private class TargetClassification {

Classification primary = Classification.NONE;

Classification secondary = Classification.NONE;

/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	return primary.toString() + secondary.toString();
}
}

private final static String EVAL_CHROM_PREFIX = "fingerprint.eval.chromosomes.";

private final static String EVAL_IMGS_KEY = "fingerprint.eval.images";

private Classifier classifier = new Classifier();

private File imgDir;

private AnjiNetFloatingEyeIdentifierFactory identifierFactory;

private AnjiNetTranscriber transcriber;

/**
 * init
 * 
 * @param props
 * @throws Exception
 */
public void init( Properties props ) throws Exception {
	transcriber = (AnjiNetTranscriber) props.singletonObjectProperty( AnjiNetTranscriber.class );
	identifierFactory = (AnjiNetFloatingEyeIdentifierFactory) props
			.singletonObjectProperty( AnjiNetFloatingEyeIdentifierFactory.class );
	Persistence db = (Persistence) props
			.singletonObjectProperty( Persistence.PERSISTENCE_CLASS_KEY );

	// get image dir and build surface from sample image file
	imgDir = props.getDirProperty( EVAL_IMGS_KEY );
	File[] imgFiles = imgDir.listFiles( ImageFileFilter.getInstance() );
	if ( imgFiles.length < 1 )
		throw new IllegalArgumentException( "no image files" );
	Surface surface = (Surface) props
			.newObjectProperty( IdentifyImageFitnessFunction.SURFACE_CLASS_KEY );
	surface.setImage( imgFiles[ 0 ] );

	// find chromosome properties
	Iterator it = props.keySet().iterator();
	while ( it.hasNext() ) {
		String key = (String) it.next();
		if ( key.startsWith( EVAL_CHROM_PREFIX ) ) {
			// get classification
			String classID = key.substring( EVAL_CHROM_PREFIX.length() );
			Classification classification = Classification.valueOf( classID );
			if ( classification == null )
				throw new IllegalArgumentException( "unknown classification: " + classID );

			// get chromosomes
			StringTokenizer tok = new StringTokenizer( props.getProperty( key ), "," );
			while ( tok.hasMoreTokens() ) {
				String chromID = tok.nextToken();
				Chromosome c = db.loadChromosome( chromID, new DummyConfiguration() );
				AnjiNet net = transcriber.newAnjiNet( c );
				classifier.addIdentifier( classification, identifierFactory
						.getIdentifier( net, surface ) );
			}
		}
	}
}

/**
 * assumes file format like F0001_01_W.JPG
 * 
 * @param f
 * @return classification
 */
private TargetClassification parseClassification( File f ) {
	String classID = f.getName().substring( 9 );
	classID = classID.substring( 0, classID.indexOf( '.' ) );
	if ( ( classID == null ) || ( classID.length() <= 0 ) )
		throw new IllegalArgumentException( "could not parse classification from "
				+ f.getAbsolutePath() );
	TargetClassification result = new TargetClassification();
	result.primary = Classification.valueOf( classID.substring( 0, 1 ) );
	if ( classID.length() > 1 )
		result.secondary = Classification.valueOf( classID.substring( 1, 1 ) );
	return result;
}

/**
 * Have the classifier process all images in directory, and report on how many are correct and
 * incorrect.
 * 
 * @return confusion matrix
 * @throws IOException
 */
public ConfusionMatrix evaluate() throws IOException {
	ConfusionMatrix result = new ConfusionMatrix();
	File[] imgFiles = imgDir.listFiles( ImageFileFilter.getInstance() );
	for ( int i = 0; i < imgFiles.length; ++i ) {
		File f = imgFiles[ i ];
		TargetClassification target = null;
		try {
			target = parseClassification( f );
		}
		catch ( Throwable th ) {
			logger.warn( "could not parse file " + f.getAbsolutePath() );
		}
		ClassifierResult cr = classifier.classify( f );
		result.increment( target.primary, cr.getPrimaryClassification() );
	}
	return result;
}

/**
 * @param args
 * @throws Exception
 */
public static void main( String[] args ) throws Exception {
	System.out.println( Copyright.STRING );
	if ( args.length < 1 )
		System.err.println( "usage: <cmd> <properties-file>" );

	Evaluator ev = new Evaluator();
	Properties props = new Properties( args[ 0 ] );
	ev.init( props );
	System.out.println( ev.evaluate().toString() );
}
}
