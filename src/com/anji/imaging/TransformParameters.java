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

/**
 * TransformParameters Parameters for a full transform, as when transforming from surface to eye
 * image based on location, rotation, zoom factor, and size of eye.
 */
public class TransformParameters {

private int translateX;

private int translateY;

private double rotate;

private double scaleX;

private double scaleY;

private int cropWidth;

private int cropHeight;

private boolean flipHorizontal;

/**
 * @param aTranslateX
 * @param aTranslateY
 * @param aRotate
 * @param aScaleX
 * @param aScaleY
 * @param aCropX
 * @param aCropY
 * @see TransformParameters#TransformParameters(int, int, double, double, double, int, int,
 * boolean)
 */
public TransformParameters( int aTranslateX, int aTranslateY, double aRotate, double aScaleX,
		double aScaleY, int aCropX, int aCropY ) {
	this( aTranslateX, aTranslateY, aRotate, aScaleX, aScaleY, aCropX, aCropY, false );
}

/**
 * @param aTranslateX
 * @param aTranslateY
 * @param aCropX
 * @param aCropY
 * @return new parameters to perform only a translation and crop
 */
public final static TransformParameters newTranslateParameters( int aTranslateX,
		int aTranslateY, int aCropX, int aCropY ) {
	return new TransformParameters( aTranslateX, aTranslateY, 0d, 1d, 1d, aCropX, aCropY );
}

/**
 * @param aRotate
 * @param aCropX
 * @param aCropY
 * @return new parameters to perform only a rotation and crop
 */
public final static TransformParameters newRotateParameters( double aRotate, int aCropX,
		int aCropY ) {
	return new TransformParameters( 0, 0, aRotate, 1d, 1d, aCropX, aCropY );
}

/**
 * @param aScaleX
 * @param aScaleY
 * @param aCropX
 * @param aCropY
 * @return new parameters to perform only a rotation and crop
 */
public final static TransformParameters newScaleParameters( double aScaleX, double aScaleY,
		int aCropX, int aCropY ) {
	return new TransformParameters( 0, 0, 0d, aScaleX, aScaleY, aCropX, aCropY );
}

/**
 * @param aTranslateX
 * @param aTranslateY
 * @param aRotate in radians
 * @param aScaleX
 * @param aScaleY
 * @param aCropX
 * @param aCropY
 * @param aFlipHorizontal
 */
public TransformParameters( int aTranslateX, int aTranslateY, double aRotate, double aScaleX,
		double aScaleY, int aCropX, int aCropY, boolean aFlipHorizontal ) {
	translateX = aTranslateX;
	translateY = aTranslateY;
	rotate = aRotate;
	scaleX = aScaleX;
	scaleY = aScaleY;
	cropWidth = aCropX;
	cropHeight = aCropY;
	flipHorizontal = aFlipHorizontal;
}

/**
 * @return returns cropWidth.
 */
public int getCropWidth() {
	return cropWidth;
}

/**
 * @return returns cropHeight.
 */
public int getCropHeight() {
	return cropHeight;
}

/**
 * @return returns rotate.
 */
public double getRotate() {
	return rotate;
}

/**
 * @return returns scaleX.
 */
public double getScaleX() {
	return scaleX;
}

/**
 * @return returns scaleY.
 */
public double getScaleY() {
	return scaleY;
}

/**
 * @return returns translateX.
 */
public int getTranslateX() {
	return translateX;
}

/**
 * @return returns translateY.
 */
public int getTranslateY() {
	return translateY;
}

/**
 * @return returns flipHorizontal.
 */
public boolean isFlipHorizontal() {
	return flipHorizontal;
}

/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	// TODO - JDK 1.5.0
	//		Formatter fmt = new Formatter();
	//		fmt.format( "move/scal/turn/flip/crop=%d,%d/%1.5f,%1.5f/%1.5f/%b/%d,%d", new Object[] {
	//				new Integer( translateX ), new Integer( translateY ), new Double( scaleX ),
	//				new Double( scaleY ), new Double( rotate ), new Boolean( flipHorizontal ),
	//				new Integer( cropWidth ), new Integer( cropHeight ) } );
	//		return fmt.toString();
	StringBuffer result = new StringBuffer();
	result.append( "move/scal/turn/flip/crop=" );
	result.append( translateX ).append( "," ).append( translateY ).append( "/" );
	double displayScaleX = ( (int) ( scaleX * 100000 ) ) / 100000d;
	double displayScaleY = ( (int) ( scaleY * 100000 ) ) / 100000d;
	result.append( displayScaleX ).append( "," ).append( displayScaleY ).append( "/" );
	double displayRotate = ( (int) ( rotate * 100000 ) ) / 100000d;
	result.append( displayRotate ).append( "/" );
	result.append( flipHorizontal ).append( "/" );
	result.append( cropHeight ).append( "," ).append( cropWidth );

	return result.toString();
}
}
