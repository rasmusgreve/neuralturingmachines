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
 * Created on Sep 18, 2004 by Philip Tucker
 */
package com.anji.imaging;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import com.anji.util.Configurable;
import com.anji.util.Properties;

/**
 * DefaultBufferedImageTransformer
 */
public class DefaultBufferedImageTransformer implements BufferedImageTransformer, Configurable {

/**
 * property to set interpolation type
 * 
 * @see AffineTransformOp
 */
public final static String INTERPOLATION_TYPE_KEY = "image.transform.interpolation.type";

private final static int DEFAULT_INTERPOLATION_TYPE = AffineTransformOp.TYPE_BILINEAR;

private BufferedImage data;

private int centerX;

private int centerY;

private int interpolationType = DEFAULT_INTERPOLATION_TYPE;

/**
 * @see com.anji.imaging.BufferedImageTransformer#setImage(java.awt.image.BufferedImage)
 */
public void setImage( BufferedImage anImg ) {
	data = anImg;
	centerX = data.getWidth() / 2;
	centerY = data.getHeight() / 2;
}

/**
 * Pre-crop image down to smallest size we need so we are not rotating, flipping, and scaling
 * entire image. Crop around center of what will be post-transformed image, allowing enough room
 * for diagonals to rotate, and allowing for scaling to decrease size of image.
 * 
 * @param parms transform parameters
 * @return cropped image
 */
private BufferedImage preCrop( TransformParameters parms ) {
	// center of cropped image
	int cropCenterX = centerX - parms.getTranslateX();
	int cropCenterY = centerY - parms.getTranslateY();

	// cropped width allows for rotation of diagonals (factor) and extends far enough to
	// allow for scale to decrease image size
	double invScaleX = 1d / parms.getScaleX();
	double invScaleY = 1d / parms.getScaleY();
	double factor = Math.abs( Math.cos( parms.getRotate() ) )
			+ Math.abs( Math.sin( parms.getRotate() ) );
	int croppedWidth = (int) ( ( parms.getCropWidth() * factor * invScaleX ) + 0.5 );
	int croppedHeight = (int) ( ( parms.getCropHeight() * factor * invScaleY ) + 0.5 );

	// upper left corner of cropped image
	int cropStartX = cropCenterX - ( croppedWidth / 2 );
	int cropStartY = cropCenterY - ( croppedHeight / 2 );
	return data.getSubimage( cropStartX, cropStartY, croppedWidth, croppedHeight );
}

/**
 * @see com.anji.imaging.BufferedImageTransformer#transform(com.anji.imaging.TransformParameters)
 */
public Image transform( TransformParameters parms ) {
	BufferedImage preCropped = preCrop( parms );
	//	try {
	//		ImageIO.write( preCropped, "TIF", new File( "c:/temp/test/precropped.tif" ) );
	//	}
	//	catch ( IOException e ) {
	//		throw new RuntimeException( e );
	//	}

	// rotate
	AffineTransform xform = new AffineTransform();
	double rotate = parms.getRotate();
	int preCroppedWidth = preCropped.getWidth();
	int preCroppedHeight = preCropped.getHeight();
	double preCroppedCenterX = preCroppedWidth / 2d;
	double preCroppedCenterY = preCroppedHeight / 2d;
	if ( rotate != 0.0d )
		xform.preConcatenate( AffineTransform.getRotateInstance( rotate, preCroppedCenterX,
				preCroppedCenterY ) );

	// scale
	double scaleX = parms.getScaleX();
	double scaleY = parms.getScaleY();
	if ( ( scaleX != 1.0d ) || ( scaleY != 1.0d ) )
		xform.preConcatenate( AffineTransform.getScaleInstance( scaleX, scaleY ) );

	// flip
	if ( parms.isFlipHorizontal() )
		xform.preConcatenate( new AffineTransform( -1.0, 0.0, 0.0, 1.0, preCroppedWidth * scaleX,
				0.0 ) );

	// transform and crop
	AffineTransformOp xformOp = new AffineTransformOp( xform, interpolationType );
	BufferedImage postXform = xformOp.filter( preCropped, null );
	//	try {
	//		ImageIO.write( postXform, "TIF", new File( "c:/temp/test/postxform.tif" ) );
	//	}
	//	catch ( IOException e ) {
	//		throw new RuntimeException( e );
	//	}
	int resultWidth = parms.getCropWidth();
	int resultHeight = parms.getCropHeight();
	int startX = (int) ( ( ( ( preCroppedWidth * scaleX ) - resultWidth ) + 1d ) / 2d );
	int startY = (int) ( ( ( ( preCroppedHeight * scaleY ) - resultHeight ) + 1d ) / 2d );
		return postXform.getSubimage( startX, startY, resultWidth, resultHeight );
}

/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties props ) throws Exception {
	interpolationType = props.getIntProperty( INTERPOLATION_TYPE_KEY, DEFAULT_INTERPOLATION_TYPE );
}
}
