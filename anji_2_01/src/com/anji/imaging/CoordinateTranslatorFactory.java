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
 * created by Philip Tucker on Jun 9, 2004
 */

package com.anji.imaging;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Philip Tucker
 */
public class CoordinateTranslatorFactory {

	private class NorthTransformer implements CoordinateTranslator {

		/**
		 * @see CoordinateTranslator#transform(IntLocation2D, int)
		 */
		public void transform( IntLocation2D coords, int max ) {
			// no-op
		}
	}

	private class SouthTransformer implements CoordinateTranslator {

		/**
		 * @see Object#toString()
		 */
		public String toString() {
			return "SouthTransformer";
		}

		/**
		 * @see CoordinateTranslator#transform(IntLocation2D, int)
		 */
		public void transform( IntLocation2D coords, int max ) {
			coords.x = max - coords.x;
			coords.y = max - coords.y;
		}
	}

	private class EastTransformer implements CoordinateTranslator {

		/**
		 * @see Object#toString()
		 */
		public String toString() {
			return "EastTransformer";
		}

		/**
		 * @see CoordinateTranslator#transform(IntLocation2D, int)
		 */
		public void transform( IntLocation2D coords, int max ) {
			int x = coords.x;
			coords.x = max - coords.y;
			coords.y = x;
		}
	}

	private class WestTransformer implements CoordinateTranslator {

		/**
		 * @see Object#toString()
		 */
		public String toString() {
			return "WestTransformer";
		}

		/**
		 * @see CoordinateTranslator#transform(IntLocation2D, int)
		 */
		public void transform( IntLocation2D coords, int max ) {
			int x = coords.x;
			coords.x = coords.y;
			coords.y = max - x;
		}
	}

	private class FlippedNorthTransformer implements CoordinateTranslator {

		/**
		 * @see Object#toString()
		 */
		public String toString() {
			return "FlippedNorthTransformer";
		}

		/**
		 * @see CoordinateTranslator#transform(IntLocation2D, int)
		 */
		public void transform( IntLocation2D coords, int max ) {
			coords.x = max - coords.x;
		}
	}

	private class FlippedSouthTransformer implements CoordinateTranslator {

		/**
		 * @see Object#toString()
		 */
		public String toString() {
			return "FlippedSouthTransformer";
		}

		/**
		 * @see CoordinateTranslator#transform(IntLocation2D, int)
		 */
		public void transform( IntLocation2D coords, int max ) {
			coords.y = max - coords.y;
		}
	}

	private class FlippedEastTransformer implements CoordinateTranslator {

		/**
		 * @see Object#toString()
		 */
		public String toString() {
			return "FlippedEastTransformer";
		}

		/**
		 * @see CoordinateTranslator#transform(IntLocation2D, int)
		 */
		public void transform( IntLocation2D coords, int max ) {
			int x = coords.x;
			coords.x = coords.y;
			coords.y = x;
		}
	}

	private class FlippedWestTransformer implements CoordinateTranslator {

		/**
		 * @see Object#toString()
		 */
		public String toString() {
			return "FlippedWestTransformer";
		}

		/**
		 * @see CoordinateTranslator#transform(IntLocation2D, int)
		 */
		public void transform( IntLocation2D coords, int max ) {
			int x = coords.x;
			coords.x = max - coords.y;
			coords.y = max - x;
		}
	}

	private static CoordinateTranslatorFactory instance;

	private Map unflippedTransformers = new HashMap();

	private Map flippedTransformers = new HashMap();

	/**
	 * singleton
	 */
	private CoordinateTranslatorFactory() {
		unflippedTransformers.put( CardinalDirection.NORTH, new NorthTransformer() );
		unflippedTransformers.put( CardinalDirection.SOUTH, new SouthTransformer() );
		unflippedTransformers.put( CardinalDirection.EAST, new EastTransformer() );
		unflippedTransformers.put( CardinalDirection.WEST, new WestTransformer() );
		flippedTransformers.put( CardinalDirection.NORTH, new FlippedNorthTransformer() );
		flippedTransformers.put( CardinalDirection.SOUTH, new FlippedSouthTransformer() );
		flippedTransformers.put( CardinalDirection.EAST, new FlippedEastTransformer() );
		flippedTransformers.put( CardinalDirection.WEST, new FlippedWestTransformer() );
	}

	/**
	 * @return singleton instance
	 */
	public static CoordinateTranslatorFactory getInstance() {
		if ( instance == null )
			instance = new CoordinateTranslatorFactory();
		return instance;
	}

	/**
	 * @param flipped
	 * @param direction
	 * @return transformer to transform coordinates based on whether the board is flipped, and the
	 * direction one is facing
	 */
	public CoordinateTranslator getTransformer( boolean flipped, CardinalDirection direction ) {
		return flipped ? (CoordinateTranslator) flippedTransformers.get( direction )
				: (CoordinateTranslator) unflippedTransformers.get( direction );
	}
}
