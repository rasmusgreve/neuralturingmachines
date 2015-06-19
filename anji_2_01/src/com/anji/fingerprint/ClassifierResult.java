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
 * created by Philip Tucker on Dec 9, 2004
 */
package com.anji.fingerprint;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassifierResult
 */
public class ClassifierResult {

	/**
	 * all confidences default to 50/50
	 */
	public static final float DEFAULT_CONFIDENCE = 0.5f;

	private Map confidences = new HashMap();

	private Classification primaryClassification = Classification.NONE;

	private Classification secondaryClassification = Classification.NONE;

	private float primaryConfidence = DEFAULT_CONFIDENCE;

	private float secondaryConfidence = DEFAULT_CONFIDENCE;

	/**
	 * @param aClassification
	 * @param confidence
	 */
	public void setConfidence( Classification aClassification, float confidence ) {
		if ( confidences.get( aClassification ) != null )
			throw new IllegalArgumentException( "confidence already set for " + aClassification );

		confidences.put( aClassification, new Float( confidence ) );
		if ( confidence > primaryConfidence ) {
			secondaryConfidence = primaryConfidence;
			secondaryClassification = primaryClassification;
			primaryConfidence = confidence;
			primaryClassification = aClassification;
		}
		else if ( confidence > secondaryConfidence ) {
			secondaryConfidence = confidence;
			secondaryClassification = aClassification;
		}
	}

	/**
	 * @return primary classification
	 */
	public Classification getPrimaryClassification() {
		return primaryClassification;
	}

	/**
	 * @return secondary classification
	 */
	public Classification getSecondaryClassification() {
		return secondaryClassification;
	}

	/**
	 * @param aClassification
	 * @return confidence in classification
	 */
	public float getConfidence( Classification aClassification ) {
		Float confidence = (Float) confidences.get( aClassification );
		return ( confidence == null ) ? DEFAULT_CONFIDENCE : confidence.floatValue();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return confidences.toString();
	}
}
