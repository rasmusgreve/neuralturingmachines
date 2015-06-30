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
 * created by Philip Tucker on Dec 11, 2004
 */
package com.anji.fingerprint;

import java.util.Iterator;

/**
 * ConfusionMatrix
 */
public class ConfusionMatrix {

	private int[][] values = new int[ Classification.getClassifications().size() ][ Classification
			.getClassifications().size() ];

	/**
	 *  
	 */
	public ConfusionMatrix() {
		super();
	}

	/**
	 * set cell in confusion matrix
	 * 
	 * @param actual
	 * @param machine
	 * @param count
	 */
	public void setCount( Classification actual, Classification machine, int count ) {
		values[ actual.getConfusionMatrixIndex() ][ machine.getConfusionMatrixIndex() ] = count;
	}

	/**
	 * get cell in confusion matrix
	 * 
	 * @param actual
	 * @param machine
	 * @return count
	 */
	public int getCount( Classification actual, Classification machine ) {
		return values[ actual.getConfusionMatrixIndex() ][ machine.getConfusionMatrixIndex() ];
	}

	/**
	 * increment cell in confusion matrix
	 * 
	 * @param actual
	 * @param machine
	 */
	public void increment( Classification actual, Classification machine ) {
		++( values[ actual.getConfusionMatrixIndex() ][ machine.getConfusionMatrixIndex() ] );
	}

	/**
	 * @return values
	 */
	public int[][] getValues() {
		return values;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return toString( "\t" );
	}

	/**
	 * record separator is "\n"
	 * 
	 * @param sep field separator
	 * @return tabular representation of confusion matrix
	 */
	public String toString( String sep ) {
		StringBuffer result = new StringBuffer();

		// column headers
		Iterator classificationIter = Classification.getClassifications().iterator();
		while ( classificationIter.hasNext() ) {
			Classification cl = (Classification) classificationIter.next();
			result.append( sep ).append( cl.toString() );
		}
		result.append( sep ).append( "Rjct %" );
		result.append( sep ).append( "TP %" );
		result.append( "\n" );

		// rows
		int totalCorrect = 0;
		int totalRejected = 0;
		int totalIncorrect = 0;
		classificationIter = Classification.getClassifications().iterator();
		while ( classificationIter.hasNext() ) {
			Classification cl = (Classification) classificationIter.next();
			result.append( cl.toString() );

			int idx = cl.getConfusionMatrixIndex();
			int rowCorrect = 0;
			int rowRejected = 0;
			int rowIncorrect = 0;
			int[] row = values[ idx ];
			for ( int i = 0; i < row.length; ++i ) {
				result.append( sep ).append( row[ i ] );
				if ( i == Classification.NONE.getConfusionMatrixIndex() )
					rowRejected += row[ i ];
				else if ( i == idx )
					rowCorrect += row[ i ];
				else
					rowIncorrect += row[ i ];
			}
			result.append( sep ).append(
					(float) ( rowRejected * 100 ) / ( rowRejected + rowCorrect + rowIncorrect ) );
			result.append( sep ).append( (float) ( rowCorrect * 100 ) / ( rowCorrect + rowIncorrect ) );
			result.append( "\n" );
			totalCorrect += rowCorrect;
			totalRejected += rowRejected;
			totalIncorrect += rowIncorrect;
		}
		result.append( "Rjct %" ).append( sep ).append(
				(float) ( totalRejected * 100 ) / ( totalRejected + totalCorrect + totalIncorrect ) ).append(
				"\n" );
		result.append( "Accur:" ).append( sep ).append(
				(float) ( totalCorrect * 100 ) / ( totalCorrect + totalIncorrect ) );

		return result.toString();
	}
}
