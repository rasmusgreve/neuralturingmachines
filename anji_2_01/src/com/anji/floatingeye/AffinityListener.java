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
public interface AffinityListener {

	/**
	 * current affinity while eye is at position <code>pos</code> is <code>value</code>
	 * affinity should be between 0.0 and 1.0 inclusive
	 * @param pos
	 * @param value
	 */
	public void updateAffinity( IntLocation2D pos, double value );
	
	/**
	 * reset affinity history
	 */
	public void reset();
}

