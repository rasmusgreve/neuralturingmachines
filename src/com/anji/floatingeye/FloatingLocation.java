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
 * created by Philip Tucker on Jul 6, 2004
 */

package com.anji.floatingeye;

import com.anji.imaging.DoubleLocation2D;

/**
 * @author Philip Tucker
 */
public class FloatingLocation extends DoubleLocation2D {

/**
 * default ctor
 */
public FloatingLocation() {
	super();
}

/**
 * @param anX
 * @param aY
 * @param aZ
 */
public FloatingLocation( double anX, double aY, double aZ ) {
	super( anX, aY );
	z = aZ;
}

/**
 * z-axis (zoom) value
 */
public double z;

/**
 * @see Object#equals(java.lang.Object)
 */
public boolean equals( Object o ) {
	FloatingLocation other = (FloatingLocation) o;
	return ( super.equals( other ) ) && ( this.z == other.z );
}

/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	StringBuffer result = new StringBuffer();
	result.append( super.toString() ).append( ", z = " ).append( TO_STRING_FORMAT.format( z ) );
	return result.toString();
}
}
