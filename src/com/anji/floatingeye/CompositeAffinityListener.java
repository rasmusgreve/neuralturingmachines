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
 * created by Philip Tucker on Nov 7, 2004
 */
package com.anji.floatingeye;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.anji.imaging.IntLocation2D;


/**
 * CompositeAffinityListener
 */
public class CompositeAffinityListener implements AffinityListener {

	private List listeners = new ArrayList();

	/**
	 * add new listener
	 * @param listener
	 */
	public void add( AffinityListener listener ) {
		listeners.add( listener );
	}

	/**
	 * @see com.anji.floatingeye.AffinityListener#updateAffinity(com.anji.imaging.IntLocation2D, double)
	 */
	public void updateAffinity( IntLocation2D aPos, double aValue ) {
		Iterator it = listeners.iterator();
		while ( it.hasNext() ) {
			AffinityListener listener = (AffinityListener) it.next();
			listener.updateAffinity( aPos, aValue );
		}
	}

	/**
	 * @see com.anji.floatingeye.AffinityListener#reset()
	 */
	public void reset() {
		Iterator it = listeners.iterator();
		while ( it.hasNext() ) {
			AffinityListener listener = (AffinityListener) it.next();
			listener.reset();
		}
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer result = new StringBuffer();
		Iterator it = listeners.iterator();
		while ( it.hasNext() ) {
			AffinityListener listener = (AffinityListener) it.next();
			result.append( listener.toString() ).append( ":" );
		}
		return result.toString();
	}
}

