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
 * Created on Sep 18, 2004 by Philip Tucker
 */
package com.anji.imaging;

/**
 * ImageTransformer
 */
public interface ImageTransformer {

	/**
	 * Transforms the underlying image given the translate, rotate, and scale parameters and
	 * returns an int array of ipxel data.
	 * 
	 * @param translateX
	 * @param translateY
	 * @param rotate
	 * @param scaleX
	 * @param scaleY
	 * @return transformed pixels
	 */
	public int[] transform( int translateX, int translateY, double rotate, double scaleX,
			double scaleY );
}
