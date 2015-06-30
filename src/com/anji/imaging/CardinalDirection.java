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
 * created by Philip Tucker on Jun 9, 2004
 */

package com.anji.imaging;

//import java.util.HashMap;
//import java.util.Map;


/**
 * Enumerated type representing 4 cardinal directions, north, south, east , west;
 * 
 * @author Philip Tucker
 */
public class CardinalDirection {
	
	private String name;

//	private static Map directions;

	/**
	 * north
	 */
	public final static CardinalDirection NORTH = new CardinalDirection( "north" );

	/**
	 * south
	 */
	public final static CardinalDirection SOUTH = new CardinalDirection( "south" );

	/**
	 * east
	 */
	public final static CardinalDirection EAST = new CardinalDirection( "east" );

	/**
	 * west
	 */
	public final static CardinalDirection WEST = new CardinalDirection( "west" );

/**
	 * @param newName id of direction
	 */
	private CardinalDirection( String newName ) {
		name = newName;
	}

//	/**
//	 * @param name id of direction
//	 * @return <code>CardinalDirection</code> enumerated type corresponding to <code>name</code>
//	 */
//	public static CardinalDirection valueOf( String name ) {
//		if ( directions == null ) {
//			directions = new HashMap();
//			directions.put( CardinalDirection.NORTH.toString(), CardinalDirection.NORTH );
//			directions.put( CardinalDirection.SOUTH.toString(), CardinalDirection.SOUTH );
//			directions.put( CardinalDirection.EAST.toString(), CardinalDirection.EAST );
//			directions.put( CardinalDirection.WEST.toString(), CardinalDirection.WEST );
//		}
//		return (CardinalDirection) directions.get( name );
//	}

	/**
	 * @see Object#equals(java.lang.Object)
	 */
	public boolean equals( Object o ) {
		return ( this == o );
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return name;
	}

	/**
	 * define this so objects may be used in hash tables
	 * 
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return name.hashCode();
	}

}

