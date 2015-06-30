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
 * created by Philip Tucker on Aug 13, 2004
 */

package com.anji.polebalance;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 * 
 * @author Philip Tucker
 */
public class PoleBalanceCanvas extends Canvas {

private double trackLength;

private double trackLengthHalf;

private double[] poleLengths;

private double cartPos = 0;

private double[] poleAngles;

private double maxPoleLength = 0;

private static final int DISPLAY_CART_WIDTH = 20;

private static final int DISPLAY_CART_HEIGHT = 5;

/**
 * @param g
 */
public void paint( Graphics g ) {
	Color orig = g.getColor();
	int displayTrackLength = (int) ( getWidth() * 0.80 );
	double scaleRatio = displayTrackLength / trackLength;

	// track
	g.setColor( Color.BLACK );
	int displayTrackYPos = (int) ( getHeight() * 0.90 );
	int displayTrackLeftXPos = ( getWidth() / 2 ) - ( displayTrackLength / 2 );
	g.drawLine( displayTrackLeftXPos, displayTrackYPos,
			( displayTrackLeftXPos + displayTrackLength ), displayTrackYPos );

	// cart
	g.setColor( Color.MAGENTA );
	int displayCartCenterXPos = displayTrackLeftXPos
			+ (int) ( displayTrackLength * ( ( cartPos + trackLengthHalf ) / trackLength ) );
	int displayCartLeftXPos = (int) ( displayCartCenterXPos - ( (double) DISPLAY_CART_WIDTH / 2 ) );
	g.fillRect( displayCartLeftXPos, displayTrackYPos - DISPLAY_CART_HEIGHT, DISPLAY_CART_WIDTH,
			DISPLAY_CART_HEIGHT );

	// poles
	ArrayList colors = new ArrayList();
	colors.add( Color.BLUE );
	colors.add( Color.CYAN );
	for ( int i = 0; i < poleAngles.length; ++i ) {
		g.setColor( (Color) colors.get( i ) );
		double displayPoleLength = poleLengths[ i ] * scaleRatio;
		double radians = poleAngles[ i ] * Math.PI;
		double x = Math.sin( radians ) * displayPoleLength;
		double y = Math.cos( radians ) * displayPoleLength;
		g.drawLine( displayCartCenterXPos, displayTrackYPos - DISPLAY_CART_HEIGHT,
				(int) ( displayCartCenterXPos + x ),
				(int) ( ( displayTrackYPos - DISPLAY_CART_HEIGHT ) - y ) );
	}

	g.setColor( orig );
}

/**
 * ctor
 * 
 * @param aTrackLength
 * @param aPoleLengths
 */
public PoleBalanceCanvas( double aTrackLength, double[] aPoleLengths ) {
	trackLength = aTrackLength;
	trackLengthHalf = trackLength / 2;
	poleLengths = aPoleLengths;
	poleAngles = new double[ poleLengths.length ];
	for ( int i = 0; i < poleLengths.length; ++i ) {
		if ( poleLengths[ i ] > maxPoleLength )
			maxPoleLength = poleLengths[ i ];
		poleAngles[ i ] = 0;
	}
}

/**
 * @param aCartPos
 * @param aPoleAngles
 */
public void step( double aCartPos, double[] aPoleAngles ) {
	if ( poleLengths.length != aPoleAngles.length )
		throw new IllegalArgumentException( "wrong # poles, expected " + poleLengths.length
				+ ", got " + aPoleAngles.length );
	if ( aCartPos < -trackLengthHalf || aCartPos > trackLengthHalf )
		throw new IllegalArgumentException( "wrong cart pos, expected abs < " + trackLengthHalf
				+ ", got " + aCartPos );
	cartPos = aCartPos;
	poleAngles = aPoleAngles;
}

}
