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
 * created by Philip Tucker on Dec 6, 2004
 */
package com.anji.fingerprint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enumerated type representing fingerprint classifications: whorl, right loop, left loop, arch,
 * and tented arch. Values returned in <code>toString()</code> are single-character
 * representations of classification.
 * 
 * @author Philip Tucker
 */
public class Classification {

	private String name;

	private static Map classificationMap;

	private static List classificationList;

	private int confusionMatrixIndex;

	/**
	 * whorl
	 */
	public final static Classification WHORL = new Classification( "W", 0 );

	/**
	 * right loop
	 */
	public final static Classification RIGHT_LOOP = new Classification( "R", 1 );

	/**
	 * left loop
	 */
	public final static Classification LEFT_LOOP = new Classification( "L", 2 );

	/**
	 * arch
	 */
	public final static Classification ARCH = new Classification( "A", 3 );

	/**
	 * tented arch
	 */
	public final static Classification TENTED_ARCH = new Classification( "T", 4 );

	/**
	 * none / undefined / reject
	 */
	public final static Classification NONE = new Classification( "X", 5 );

	/**
	 * @param aName id of type
	 * @param aConfusionIndex placement in confusion matrix
	 */
	private Classification( String aName, int aConfusionIndex ) {
		name = aName;
		confusionMatrixIndex = aConfusionIndex;
	}

	/**
	 * @param name id of type
	 * @return <code>NeuronType</code> enumerated type corresponding to <code>name</code>
	 */
	public static Classification valueOf( String name ) {
		if ( classificationMap == null ) {
			classificationMap = new HashMap();
			classificationMap.put( Classification.WHORL.toString(), Classification.WHORL );
			classificationMap.put( Classification.RIGHT_LOOP.toString(), Classification.RIGHT_LOOP );
			classificationMap.put( Classification.LEFT_LOOP.toString(), Classification.LEFT_LOOP );
			classificationMap.put( Classification.ARCH.toString(), Classification.ARCH );
			classificationMap.put( Classification.TENTED_ARCH.toString(), Classification.TENTED_ARCH );
			classificationMap.put( Classification.NONE.toString(), Classification.NONE );
		}
		Classification result = (Classification) classificationMap.get( name.toUpperCase() );
		return result;
	}

	/**
	 * @return all classifications in order of confusion matrix index
	 */
	public static List getClassifications() {
		if ( classificationList == null ) {
			classificationList = new ArrayList();
			classificationList.add( Classification.WHORL );
			classificationList.add( Classification.RIGHT_LOOP );
			classificationList.add( Classification.LEFT_LOOP );
			classificationList.add( Classification.ARCH );
			classificationList.add( Classification.TENTED_ARCH );
			classificationList.add( Classification.NONE );
		}
		return classificationList;
	}

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

	/**
	 * @return index in confusion matrix
	 */
	public int getConfusionMatrixIndex() {
		return confusionMatrixIndex;
	}
}
