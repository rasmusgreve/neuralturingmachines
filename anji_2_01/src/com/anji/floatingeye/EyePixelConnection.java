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
 * created by Philip Tucker on Jun 10, 2004
 */

package com.anji.floatingeye;

import com.anji.nn.Connection;

/**
 * @author Philip Tucker
 */
public class EyePixelConnection implements Connection {

	private int x;

	private int y;

	private FloatingEye eye;

	/**
	 * @param anX
	 * @param aY
	 * @param anEye floating eye object from which to get location data
	 */
	public EyePixelConnection( int anX, int aY, FloatingEye anEye ) {
		if ( anX < 0 || aY < 0 || anX >= anEye.getWidth() || aY >= anEye.getHeight() )
			throw new IllegalArgumentException( "invalid coordinate for eye pixel: (" + anX + ", "
					+ aY + ")" );
		x = anX;
		y = aY;
		eye = anEye;
	}

	/**
	 * TODO - could make this more efficient if eye pixels were an array 
	 * TODO - only works with grayscale now
	 * @see com.anji.nn.Connection#read()
	 */
	public double read() {
		int rawPixel = eye.getEyePixel( x, y );

		// subtract 255 from saturation byte to make off-canvas pixels negative
		int byte1 = ( ( rawPixel & 0xFF000000 ) >>> 24 ) - 255;

		// use only byte2 for color since it's grayscale
		// subtract byte2 from 255 to flip black and
		// white - since white is background we want it closer to transparent
		int byte2 = 255 - ( ( rawPixel & 0x00FF0000 ) >>> 16 );

		// if saturation is 255, return color; otherwise return saturation
		int adjustedPixel = ( byte1 == 0 ) ? byte2 : byte1;
		double result = adjustedPixel / 255.0d;
		return result;
	}

	/**
	 * @see com.anji.nn.Connection#toXml()
	 */
	public String toXml() {
		StringBuffer result = new StringBuffer();
		result.append( "<" ).append( Connection.XML_TAG );
		result.append( "\" from-eye-x=\"" ).append( x );
		result.append( "\" from-eye-y=\"" ).append( y ).append( "\" />" );

		return result.toString();
	}

	/**
	 * @see com.anji.nn.Connection#cost()
	 */
	public long cost() {
		return 821;
	}
}
