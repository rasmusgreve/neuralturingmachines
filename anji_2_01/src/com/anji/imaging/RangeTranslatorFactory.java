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
 * created by Philip Tucker on Jun 12, 2004
 */

package com.anji.imaging;

/**
 * @author Philip Tucker
 */
public class RangeTranslatorFactory {

	private class NullRangeTranslator implements RangeTranslator {

		/**
		 * @see com.anji.imaging.RangeTranslator#translate(double)
		 */
		public double translate( double value ) {
			return value;
		}
	}

	private class DefaultRangeTranslator implements RangeTranslator {

		private double srcMin;

		private double destMin;

		private double factor;

		/**
		 * @param aSrcMin
		 * @param aSrcMax
		 * @param aDestMin
		 * @param aDestMax
		 */
		public DefaultRangeTranslator( double aSrcMin, double aSrcMax, double aDestMin,
				double aDestMax ) {
			if ( ( aSrcMax < aSrcMin ) || ( aDestMax < aDestMin ) )
				throw new IllegalArgumentException( "min < max" );
			srcMin = aSrcMin;
			destMin = aDestMin;
			factor = ( aDestMax - aDestMin ) / ( aSrcMax - aSrcMin );
		}

		/**
		 * @see com.anji.imaging.RangeTranslator#translate(double)
		 */
		public double translate( double value ) {
			return ( ( value - srcMin ) * factor ) + destMin;
		}
	}

	private RangeTranslator nullTranslator = new NullRangeTranslator();

	private static RangeTranslatorFactory instance = null;

	private RangeTranslatorFactory() {
		// no-op
	}

	/**
	 * @return singleton instance
	 */
	public static RangeTranslatorFactory getInstance() {
		if ( instance == null )
			instance = new RangeTranslatorFactory();
		return instance;
	}

	/**
	 * @param aSrcMin
	 * @param aSrcMax
	 * @param aDestMin
	 * @param aDestMax
	 * @return translator for given ranges
	 */
	public RangeTranslator getTranslator( double aSrcMin, double aSrcMax, double aDestMin,
			double aDestMax ) {
		return ( ( aSrcMin == aDestMin ) && ( aSrcMax == aDestMax ) ) ? nullTranslator
				: new DefaultRangeTranslator( aSrcMin, aSrcMax, aDestMin, aDestMax );
	}

}
