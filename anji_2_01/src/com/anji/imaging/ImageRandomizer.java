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
 * created by Derek James on Jul 6, 2004
 */

package com.anji.imaging;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.anji.util.Configurable;
import com.anji.util.Properties;
import com.anji.util.Randomizer;

/**
 * TODO - bicubic with JDK 1.5
 * @author Philip Tucker
 */
public class ImageRandomizer implements Configurable {

private static final Logger logger = Logger.getLogger( ImageRandomizer.class );

/**
 * property to determine if images should be randomized
 */
public final static String IMG_RANDOMIZE_KEY = "image.randomize";

private final static String IMG_MATCH_ORIG_DIR_KEY = "image.randomize.matches.originals";

private final static String IMG_MISMATCH_ORIG_DIR_KEY = "image.randomize.mismatches.originals";

/**
 * number of image files to place in match directory
 */
public final static String IMG_MATCH_COUNT_KEY = "image.randomize.matches.count";

/**
 * number of image files to place in mismatch directory
 */
public final static String IMG_MISMATCH_COUNT_KEY = "image.randomize.mismatches.count";

private final static String IMG_TRANSLATEX_KEY = "image.randomize.move.x";

private final static String IMG_TRANSLATEY_KEY = "image.randomize.move.y";

private final static String IMG_STARTX_KEY = "image.randomize.start.x";

private final static String IMG_STARTY_KEY = "image.randomize.start.y";

private final static String IMG_TRANSLATEZ_KEY = "image.randomize.scale";

private final static String IMG_TRANSFORMTHETA_KEY = "image.randomize.rotate";

private final static String IMG_CROPSIZE_KEY = "image.randomize.crop.size";

private final static String IMG_SHEARX_KEY = "image.randomize.shear.x";

private final static String IMG_SHEARY_KEY = "image.randomize.shear.y";

private final static String IMG_BRIGHTNESS_KEY = "image.randomize.brightness";

private final static String IMG_TOGGLE_KEY = "image.randomize.toggle";

private double maxShearX;

private double maxShearY;

private int maxTranslateX;

private int maxTranslateY;

private double maxAdjustSaturation;

private double toggleRatio;

private int startX;

private int startY;

private double maxScale;

private double maxRotate;

private int cropSize;

private List origMatchImgs;

private List origMismatchImgs;

private int matchImageCount;

private int mismatchImageCount;

private Randomizer randomizer;

/**
 * @param props
 * @throws IOException
 */
public void init( Properties props ) throws IOException {
	randomizer = (Randomizer) props.singletonObjectProperty( Randomizer.class );
	maxTranslateX = props.getIntProperty( IMG_TRANSLATEX_KEY, 0 );
	maxTranslateY = props.getIntProperty( IMG_TRANSLATEY_KEY, 0 );
	startX = props.getIntProperty( IMG_STARTX_KEY, -1 );
	startY = props.getIntProperty( IMG_STARTY_KEY, -1 );
	maxScale = props.getDoubleProperty( IMG_TRANSLATEZ_KEY, 0 );
	maxRotate = props.getIntProperty( IMG_TRANSFORMTHETA_KEY, 0 );
	maxShearX = props.getDoubleProperty( IMG_SHEARX_KEY, 0 );
	maxShearY = props.getDoubleProperty( IMG_SHEARY_KEY, 0 );
	maxAdjustSaturation = props.getDoubleProperty( IMG_BRIGHTNESS_KEY, 0 );
	toggleRatio = props.getDoubleProperty( IMG_TOGGLE_KEY, 0 );
	cropSize = props.getIntProperty( IMG_CROPSIZE_KEY );
	matchImageCount = props.getIntProperty( IMG_MATCH_COUNT_KEY );
	mismatchImageCount = props.getIntProperty( IMG_MISMATCH_COUNT_KEY );
	origMatchImgs = readImages( props.getDirProperty( IMG_MATCH_ORIG_DIR_KEY ) );
	origMismatchImgs = readImages( props.getDirProperty( IMG_MISMATCH_ORIG_DIR_KEY ) );
	if ( origMatchImgs.size() + origMismatchImgs.size() <= 0 )
		throw new IllegalArgumentException( "no images loaded" );
}

/**
 * 
 * @param dir
 * @return <code>Collection</code> contains <code>BufferedImage</code> objects
 * @throws IOException
 */
private static List readImages( File dir ) throws IOException {
	List result = new ArrayList();
	File[] files = dir.listFiles(ImageFileFilter.getInstance());
	for ( int i = 0; i < files.length; ++i ) {
		File f = files[ i ];
		if ( f.isFile() ) {
			BufferedImage img = ImageIO.read( f );
			if ( img == null )
				logger.info( f.getAbsolutePath() + " is not an image file" );
			else
				result.add( img );
		}
		else
			logger.info( f.getAbsolutePath() + " is not a proper file" );
	}

	logger.info( result.size() + " images loaded from " + dir.getAbsolutePath() );
	return result;
}

private double nextDoubleWithinRange( double range ) {
	return range * ( ( randomizer.getRand().nextDouble() * 2 ) - 1 );
}

private static BufferedImage transformShear( BufferedImage image, double shearXFactor,
		double shearYFactor ) {
	double origCenterX = image.getWidth() / 2;
	double origCenterY = image.getHeight() / 2;

	// shear
	AffineTransform at = new AffineTransform();
	double newCenterX = origCenterX + ( shearXFactor * origCenterY );
	double newCenterY = origCenterY + ( shearYFactor * origCenterX );
	at.shear( shearXFactor, shearYFactor );
	at.translate( origCenterX - newCenterX, origCenterY - newCenterY );
	AffineTransformOp op = new AffineTransformOp( at, AffineTransformOp.TYPE_BILINEAR );
	return op.filter( image, null ).getSubimage( 0, 0, image.getWidth(), image.getHeight() );
}

private static int boundByteRange( int x ) {
	return Math.max( Math.min( x, 255 ), 0 );
}

private static BufferedImage transformSaturation( BufferedImage image, int delta, Random rand,
		double toggleRatio ) {
	BufferedImage result = new BufferedImage( image.getWidth(), image.getHeight(),
			BufferedImage.TYPE_INT_ARGB );

	for ( int x = 0; x < result.getWidth(); ++x ) {
		for ( int y = 0; y < result.getHeight(); ++y ) {
			int origRgb = ( rand.nextDouble() < toggleRatio ) ? rand.nextInt() : image.getRGB( x, y );

			int origSaturation = ( ( origRgb & 0xFF000000 ) >>> 24 );
			int origR = ( ( origRgb & 0x00FF0000 ) >>> 16 );
			int origG = ( ( origRgb & 0x0000FF00 ) >>> 8 );
			int origB = origRgb & 0x000000FF;

			int newSaturation = boundByteRange( origSaturation + delta );
			int newR = boundByteRange( origR + delta );
			int newG = boundByteRange( origG + delta );
			int newB = boundByteRange( origB + delta );

			int newRgb = ( newSaturation << 24 ) + ( newR << 16 ) + ( newG << 8 ) + newB;
			result.setRGB( x, y, newRgb );
		}
	}
	return result;
}

//private static void writeTempImage( BufferedImage img, String name ) {
//	try {
//		File outFile = new File( new File( "c:/temp/" ), name + ".tif" );
//		ImageIO.write( img, "tif", outFile );
//	}
//	catch ( IOException e ) {
//		logger.error( "error writing file " + name, e );
//	}
//}

private static BufferedImage transformRotate( BufferedImage image, double rotate ) {
	AffineTransform at = new AffineTransform();
	at.rotate( Math.toRadians( rotate ), image.getWidth() / 2, image.getHeight() / 2 );
	AffineTransformOp op = new AffineTransformOp( at, AffineTransformOp.TYPE_BILINEAR );
	BufferedImage postRotate = op.filter( image, null );
	return postRotate.getSubimage( 0, 0, image.getWidth(), image.getHeight() );
}

private static BufferedImage transformScale( BufferedImage image, double scaleFactor ) {
	AffineTransform at = new AffineTransform();
	at.scale( scaleFactor, scaleFactor );
	AffineTransformOp op = new AffineTransformOp( at, AffineTransformOp.TYPE_BILINEAR );
	return op.filter( image, null );
}

private BufferedImage transform( BufferedImage image ) {
	// saturation
	int saturationDelta = (int) ( nextDoubleWithinRange( maxAdjustSaturation ) * 255 );
	BufferedImage postAdjustSaturation = transformSaturation( image, saturationDelta, randomizer
			.getRand(), toggleRatio );
	//writeTempImage( postAdjustSaturation, "postAdjustSaturation" );

	// shear
	double shearXFactor = nextDoubleWithinRange( maxShearX );
	double shearYFactor = nextDoubleWithinRange( maxShearY );
	BufferedImage postShear = transformShear( postAdjustSaturation, shearXFactor, shearYFactor );
	//writeTempImage( postShear, "postShear" );

	// rotate
	double rotate = nextDoubleWithinRange( maxRotate );
	BufferedImage postRotate = transformRotate( postShear, rotate );
	//writeTempImage( postRotate, "postRotate" );

	// scale
	double scaleFactor = 1 + nextDoubleWithinRange( maxScale );
	BufferedImage postScale = transformScale( postRotate, scaleFactor );
	//writeTempImage( postScale, "postScale" );

	// crop & translate
	double deltaX = nextDoubleWithinRange( maxTranslateX );
	int xCropStartPoint = ( startX > -1 ) ? startX : ( postScale.getWidth() - cropSize ) / 2;
	xCropStartPoint -= deltaX;
	xCropStartPoint = Math.max( xCropStartPoint, 0 );
	double deltaY = nextDoubleWithinRange( maxTranslateY );
	int yCropStartPoint = ( startY > -1 ) ? startY : ( postScale.getHeight() - cropSize ) / 2;
	yCropStartPoint -= deltaY;
	yCropStartPoint = Math.max( yCropStartPoint, 0 );
	int xCropSize = Math.min( cropSize, postScale.getWidth() - xCropStartPoint );
	int yCropSize = Math.min( cropSize, postScale.getHeight() - yCropStartPoint );
	BufferedImage postCrop = postScale.getSubimage( xCropStartPoint, yCropStartPoint, xCropSize,
			yCropSize );
	//writeTempImage( postCrop, "postCrop" );

	// expand back to correct size
	if ( ( postCrop.getWidth() != cropSize ) || ( postCrop.getHeight() != cropSize ) ) {
		double xScaleFactor = (double) cropSize / postCrop.getWidth();
		double yScaleFactor = (double) cropSize / postCrop.getHeight();
		double rescaleFactor = Math.max( xScaleFactor, yScaleFactor );
		AffineTransform at = new AffineTransform();
		at.scale( rescaleFactor, rescaleFactor );
		AffineTransformOp op = new AffineTransformOp( at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
		postCrop = op.filter( postCrop, null );
		//writeTempImage( postCrop, "postRescale" );
		postCrop = postCrop.getSubimage( 0, 0, cropSize, cropSize );
		//writeTempImage( postCrop, "postRecrop" );
	}

	return postCrop;
}

private void clear( File dir ) {
	if ( dir.isDirectory() ) {
		File[] files = dir.listFiles( ImageFileFilter.getInstance() );
		for ( int i = 0; i < files.length; ++i )
			files[ i ].delete();
	}
	else
		throw new IllegalArgumentException( dir.getAbsolutePath() + " is not a directory" );
}

/**
 * clears destination dirs then populates them with new transformed images
 * 
 * @param destMatchDIr directory to which transformed match image files are written
 * @param destMismatchDir directory to which transformed mismatch image files are written
 * @throws IOException
 */
public void transformFiles( File destMatchDIr, File destMismatchDir ) throws IOException {
	clear( destMatchDIr );
	clear( destMismatchDir );

	for ( int i = 0; i < matchImageCount; ++i ) {
		int idx = randomizer.getRand().nextInt( origMatchImgs.size() );
		BufferedImage origImg = (BufferedImage) origMatchImgs.get( idx );
		File outFile = new File( destMatchDIr, "img" + i + ".tif" );
		ImageIO.write( transform( origImg ), "tif", outFile );
	}

	for ( int i = 0; i < mismatchImageCount; ++i ) {
		int idx = randomizer.getRand().nextInt( origMismatchImgs.size() );
		BufferedImage origImg = (BufferedImage) origMismatchImgs.get( idx );
		File outFile = new File( destMismatchDir, "img" + i + ".tif" );
		ImageIO.write( transform( origImg ), "tif", outFile );
	}
}
}
