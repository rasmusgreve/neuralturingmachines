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
 * created by Philip Tucker on Jun 10, 2004
 */

package com.anji.floatingeye;

import com.anji.nn.Connection;


/**
 * @author Philip Tucker
 */
public class LocationXConnection implements Connection {

	private FloatingEye eye;
	
	/**
	 * @param anEye floating eye object from which to get location data
	 */
	public LocationXConnection( FloatingEye anEye ) {
		eye = anEye;
	}
	
	/**
	 * @see com.anji.nn.Connection#read()
	 */
	public double read() {
		return eye.eyeLocation.x;
	}

	/**
	 * @see com.anji.nn.Connection#toXml()
	 */
	public String toXml() {
		StringBuffer result = new StringBuffer();
		result.append( "<" ).append( Connection.XML_TAG );
		result.append( "\" from-location=\"x\" />" );

		return result.toString();
	}

	/**
	 * @see com.anji.nn.Connection#cost()
	 */
	public long cost() {
		return 41;
	}


}

