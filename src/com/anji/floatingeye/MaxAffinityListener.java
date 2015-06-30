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
 * created by Philip Tucker on Jul 15, 2004
 */

package com.anji.floatingeye;

import com.anji.imaging.IntLocation2D;


/**
 * @author Philip Tucker
 */
public class MaxAffinityListener implements AffinityListener {

	private double maxAffinity = -Double.MAX_VALUE;
	
	/**
	 * @see com.anji.floatingeye.AffinityListener#updateAffinity(com.anji.imaging.IntLocation2D, double)
	 */
	public synchronized void updateAffinity( IntLocation2D pos, double value ) {
		if ( value > maxAffinity )
			maxAffinity = value;
	}

	
	/**
	 * @return max affinity.
	 */
	public synchronized double getMaxAffinity() {
		return maxAffinity;
	}


	/**
	 * @see com.anji.floatingeye.AffinityListener#reset()
	 */
	public void reset() {
		maxAffinity = -Double.MAX_VALUE;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return maxAffinity + " [max]";
	}
}

