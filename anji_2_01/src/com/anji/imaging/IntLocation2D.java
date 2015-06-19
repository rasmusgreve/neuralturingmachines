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
 * created by Philip Tucker on Jul 6, 2004
 */

package com.anji.imaging;

/**
 * @author Philip Tucker
 */
public class IntLocation2D {
	
	/**
	 * default ctor
	 */
	public IntLocation2D() {
		super();
	}

	/**
	 * @param anX
	 * @param aY
	 */
	public IntLocation2D( int anX, int aY ) {
		x = anX;
		y = aY;
	}
	
	/**
	 * x-axis value
	 */
	public int x;

	/**
	 * y-axis value
	 */
	public int y;

	/**
	 * @see Object#equals(java.lang.Object)
	 */
	public boolean equals( Object o ) {
		IntLocation2D other = (IntLocation2D) o;
		return ( this.x == other.x ) && ( this.y == other.y );
	}
	
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "x = " + x + ", y = " + y;
	}
}

