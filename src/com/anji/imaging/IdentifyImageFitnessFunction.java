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
 * created by Philip Tucker on Jul 12, 2004
 */

package com.anji.imaging;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;

import com.anji.floatingeye.AnjiNetFloatingEyeIdentifierFactory;
import com.anji.integration.AnjiNetTranscriber;
import com.anji.integration.ErrorFunction;
import com.anji.integration.ErrorRateCounter;
import com.anji.integration.TranscriberException;
import com.anji.nn.AnjiNet;
import com.anji.util.Configurable;
import com.anji.util.Properties;
import com.anji.util.Randomizer;

/**
 * Fitness is determined by the number of images identified correctly. A <i>frink </i> is
 * roughly the amount of JVM processing power a Dell Latitude C600 850MHz w/ 256 MB RAM can
 * perform in one millisecond. Parameter <code>EVAL_MILLIS_KEY</code> determines the number of
 * frinks each net is allotted per image.
 * 
 * @author Philip Tucker
 */
public class IdentifyImageFitnessFunction implements BulkFitnessFunction, Configurable {

private class ImageInfo {

/**
 * file from which image was loaded
 */
public File file;

/**
 * true if we want to match this image
 */
public boolean isMatch;

/**
 * @param anIsMatch
 * @param aFile
 */
public ImageInfo( boolean anIsMatch, File aFile ) {
	isMatch = anIsMatch;
	file = aFile;
}
}

private File matchDir;

private File mismatchDir;

private static Logger logger = Logger.getLogger( IdentifyImageFitnessFunction.class );

/**
 * directory containing images to match
 */
public final static String IMG_MATCH_DIR_KEY = "image.matches";

/**
 * directory containing images not to match
 */
public final static String IMG_MISMATCH_DIR_KEY = "image.mismatches";

/**
 * implements Surface; object to handle image manipulation
 */
public final static String SURFACE_CLASS_KEY = "eye.surface";

private final static boolean SUM_OF_SQUARES = false;

/**
 * max fitness value
 */
public final static int MAX_FITNESS = 1000000;

private List imgInfos = new ArrayList();

private ImageRandomizer imgRandomizer;

private Surface surface;

private Randomizer randomizer;

private AnjiNetFloatingEyeIdentifierFactory identifierFactory;

private AnjiNetTranscriber transcriber;

/**
 * default ctor
 */
public IdentifyImageFitnessFunction() {
	super();
}

/**
 * @see org.jgap.BulkFitnessFunction#evaluate(java.util.List)
 * @see IdentifyImageFitnessFunction#evaluate(Chromosome)
 */
public void evaluate( List subjects ) {

	// randomize rotation and position of image samples
	if ( imgRandomizer != null ) {
		try {
			imgRandomizer.transformFiles( matchDir, mismatchDir );
		}
		catch ( IOException e ) {
			logger.error( "error randomizing image files", e );
			throw new RuntimeException( "error randomizing image files", e );
		}
		loadImages();
	}

	// evaulate each image
	Iterator it = subjects.iterator();
	while ( it.hasNext() ) {
		Chromosome c = (Chromosome) it.next();
		try {
			c.setFitnessValue( evaluate( c ) );
		}
		catch ( Throwable e ) {
			logger.warn( "error evaluating chromosome " + c.toString(), e );
			c.setFitnessValue( 0 );
		}
	}
}

/**
 * Returns int between 0 and <code>MAX_FITNESS</code> relative to # images ANN (transcribed
 * from <code>c</code>) is able to identify correctly.
 * 
 * @param c
 * @return fitness value for <code>c</code>
 * @throws TranscriberException
 * @throws IOException
 */
public int evaluate( Chromosome c ) throws TranscriberException, IOException {
	double[][] targets = new double[ imgInfos.size() ][ 1 ];
	double[][] responses = new double[ imgInfos.size() ][ 1 ];
	int idx = 0;

	// transcribe
	AnjiNet net = transcriber.newAnjiNet( c );
	long cost = net.cost();

	// shuffle images and iterate through them
	Collections.shuffle( imgInfos, randomizer.getRand() );
	Iterator it = imgInfos.iterator();

	// get first image
	ImageInfo imgInfo = (ImageInfo) it.next();
	surface.setImage( imgInfo.file );
	int imgWidth = surface.getWidth();
	int imgHeight = surface.getHeight();

	Identifier identifier = identifierFactory.getIdentifier( net, surface );
	logger.debug( toCostString( cost, identifier.getStepNum() ) );

	// responses and errors
	double maxError = ErrorFunction.getInstance().getMaxError( imgInfos.size(), 1.0,
			SUM_OF_SQUARES );
	double maxRawFitnessValue = Math.pow( maxError, 2 );

	// match first image
	targets[ idx ][ 0 ] = imgInfo.isMatch ? 1.0d : 0.0d;
	double result = identifier.identify( imgInfo.file );
	logger.debug( toCategorizationString( imgInfo, new double[] { result } ) );
	responses[ idx++ ][ 0 ] = result;

	// evaluate remaining images
	while ( it.hasNext() ) {
		imgInfo = (ImageInfo) it.next();
		targets[ idx ][ 0 ] = imgInfo.isMatch ? 1.0d : 0.0d;
		if ( ( imgWidth != surface.getWidth() ) || ( imgHeight != surface.getHeight() ) )
			throw new IllegalArgumentException( "images must all be same dimension: " + imgWidth
					+ ", " + imgHeight + " vs " + surface.getWidth() + ", " + surface.getHeight() );
		result = identifier.identify( imgInfo.file );
		logger.debug( toCategorizationString( imgInfo, new double[] { result } ) );
		responses[ idx++ ][ 0 ] = result;
	}

	identifier.dispose();

	// log match information
	ErrorRateCounter.getInstance().countErrors( "chromosome " + c.getId().toString(), targets, responses );

	// calculate fitness
	double error = ErrorFunction.getInstance().calculateError( targets, responses, false );
	if ( error > maxError )
		throw new IllegalStateException( "error " + error + " > max error " + maxError );
	double rawFitnessValue = Math.pow( maxError - error, 2 );
	double skewedFitness = ( rawFitnessValue / maxRawFitnessValue ) * MAX_FITNESS;
	return (int) skewedFitness;

}

/**
 * Returns int between 0 and <code>MAX_FITNESS</code> relative to # images ANNs (transcribed
 * from <code>ensemble</code>) are able to identify correctly. Identification is based on
 * averaging outputs of all ANNs.
 * 
 * @param ensemble <code>Collection</code> contains <code>Chromosome</code> objects
 * @return fitness value for <code>ensemble</code>
 * @throws TranscriberException
 * @throws IOException
 */
public int evaluateEnsemble( Collection ensemble ) throws TranscriberException, IOException {
	double[][] targets = new double[ imgInfos.size() ][ 1 ];
	double[][] responses = new double[ imgInfos.size() ][ 1 ];
	int idx = 0;

	// shuffle images and iterate through them
	Collections.shuffle( imgInfos, randomizer.getRand() );
	Iterator imgInfoIter = imgInfos.iterator();

	// get first image
	ImageInfo imgInfo = (ImageInfo) imgInfoIter.next();
	surface.setImage( imgInfo.file );
	int imgWidth = surface.getWidth();
	int imgHeight = surface.getHeight();

	// transcribe
	ArrayList identifiers = new ArrayList();
	Iterator ensembleIter = ensemble.iterator();
	while ( ensembleIter.hasNext() ) {
		Chromosome c = (Chromosome) ensembleIter.next();
		AnjiNet net = transcriber.newAnjiNet( c );
		Identifier identifier = identifierFactory.getIdentifier( net, surface );
		identifiers.add( identifier );
	}

	// responses and errors
	double maxError = ErrorFunction.getInstance().getMaxError( imgInfos.size(), 1.0,
			SUM_OF_SQUARES );
	double maxRawFitnessValue = Math.pow( maxError, 2 );

	// match first image
	targets[ idx ][ 0 ] = imgInfo.isMatch ? 1.0d : 0.0d;
	double resultTotal = 0d;
	double[] results = new double[ identifiers.size() ];
	for ( int i = 0; i < identifiers.size(); ++i ) {
		Identifier identifier = (Identifier) identifiers.get( i );
		logger.debug( toCostString( identifier.cost(), identifier.getStepNum() ) );
		float result = identifier.identify( imgInfo.file );
		resultTotal += result;
		results[ i ] = result;
	}
	logger.debug( toCategorizationString( imgInfo, results ) );
	double[] ensembleResponse = { resultTotal / identifiers.size() };
	responses[ idx++ ][ 0 ] = ensembleResponse[ 0 ];
	logger.debug( toCategorizationString( imgInfo, ensembleResponse ) );

	// evaluate remaining images
	while ( imgInfoIter.hasNext() ) {
		imgInfo = (ImageInfo) imgInfoIter.next();
		targets[ idx ][ 0 ] = imgInfo.isMatch ? 1.0d : 0.0d;
		if ( ( imgWidth != surface.getWidth() ) || ( imgHeight != surface.getHeight() ) )
			throw new IllegalArgumentException( "images must all be same dimension: " + imgWidth
					+ ", " + imgHeight + " vs " + surface.getWidth() + ", " + surface.getHeight() );
		resultTotal = 0d;
		for ( int i = 0; i < identifiers.size(); ++i ) {
			Identifier identifier = (Identifier) identifiers.get( i );
			logger.debug( toCostString( identifier.cost(), identifier.getStepNum() ) );
			float result = identifier.identify( imgInfo.file );
			resultTotal += result;
			results[ i ] = result;
		}
		logger.debug( toCategorizationString( imgInfo, results ) );
		ensembleResponse[ 0 ] = resultTotal / identifiers.size();
		responses[ idx++ ][ 0 ] = ensembleResponse[ 0 ];
		logger.debug( toCategorizationString( imgInfo, ensembleResponse ) );
	}

	// cleanup
	Iterator identifierIter = identifiers.iterator();
	while ( identifierIter.hasNext() ) {
		Identifier identifier = (Identifier) identifierIter.next();
		identifier.dispose();
	}

	//print match information
	ErrorRateCounter.getInstance().countErrors( targets, responses );

	// calculate fitness
	double error = ErrorFunction.getInstance().calculateError( targets, responses, false );
	if ( error > maxError )
		throw new IllegalStateException( "error " + error + " > max error " + maxError );
	double rawFitnessValue = Math.pow( maxError - error, 2 );
	double skewedFitness = ( rawFitnessValue / maxRawFitnessValue ) * MAX_FITNESS;
	return (int) skewedFitness;

}

private static String toResultString( ImageInfo imgInfo, double affinity ) {
	StringBuffer result = new StringBuffer();
	result.append( imgInfo.isMatch ? ( ( affinity > 0.5 ) ? "TRUE-POS" : "FALSE-NEG" )
			: ( ( affinity <= 0.5 ) ? "TRUE-NEG" : "FALSE-POS" ) );
	result.append( "[" ).append( affinity ).append( "]" );
	return result.toString();
}

private static String toCategorizationString( ImageInfo imgInfo, double[] affinities ) {
	StringBuffer result = new StringBuffer();
	if ( affinities.length > 0 )
		result.append( toResultString( imgInfo, affinities[ 0 ] ) );
	for ( int i = 1; i < affinities.length; ++i )
		result.append( "/" ).append( toResultString( imgInfo, affinities[ i ] ) );
	result.append( ", " ).append( imgInfo.file.getAbsolutePath() );
	return result.toString();
}

private static String toCostString( long cost, int stepCount ) {
	StringBuffer result = new StringBuffer();
	result.append( "cost=" ).append( cost ).append( ", steps=" ).append( stepCount );
	return result.toString();
}

/**
 * @see org.jgap.BulkFitnessFunction#getMaxFitnessValue()
 */
public int getMaxFitnessValue() {
	return MAX_FITNESS;
}

/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties props ) throws Exception {
	transcriber = (AnjiNetTranscriber) props.singletonObjectProperty( AnjiNetTranscriber.class );
	randomizer = (Randomizer) props.singletonObjectProperty( Randomizer.class );
	identifierFactory = (AnjiNetFloatingEyeIdentifierFactory) props
			.singletonObjectProperty( AnjiNetFloatingEyeIdentifierFactory.class );

	matchDir = props.getDirProperty( IMG_MATCH_DIR_KEY );
	mismatchDir = props.getDirProperty( IMG_MISMATCH_DIR_KEY );

	boolean doRandomizeImgs = props.getBooleanProperty( ImageRandomizer.IMG_RANDOMIZE_KEY, false );
	if ( doRandomizeImgs ) {
		imgRandomizer = new ImageRandomizer();
		imgRandomizer.init( props );
		try {
			imgRandomizer.transformFiles( matchDir, mismatchDir );
		}
		catch ( IOException e ) {
			logger.error( "error randomizing image files", e );
			throw new RuntimeException( "error randomizing image files", e );
		}
	}
	loadImages();

	ErrorFunction.getInstance().init( props );

	surface = (Surface) props.newObjectProperty( SURFACE_CLASS_KEY );
}

private void loadImages() {
	imgInfos.clear();

	// load match images
	File[] matchFiles = matchDir.listFiles( ImageFileFilter.getInstance() );
	for ( int i = 0; i < matchFiles.length; ++i ) {
		imgInfos.add( new ImageInfo( true, matchFiles[ i ] ) );
	}

	// load mismatch images
	File[] mismatchFiles = mismatchDir.listFiles( ImageFileFilter.getInstance() );
	for ( int i = 0; i < mismatchFiles.length; ++i ) {
		imgInfos.add( new ImageInfo( false, mismatchFiles[ i ] ) );
	}

	if ( imgInfos.isEmpty() )
		throw new IllegalArgumentException( "must have at least one image" );
}
}
