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
 * created by Philip Tucker on Dec 7, 2004
 */

package com.anji.floatingeye;

import org.apache.log4j.Logger;

/**
 * Data holder for FloatingEye movement parameters.
 * 
 * @author Philip Tucker
 */
public class EyeMovementParms {

	private static Logger logger = Logger.getLogger( EyeMovementParms.class );

	private double startZoom;

	private double minZoom;

	private double maxXMovePerStep;

	private double maxYMovePerStep;

	private double maxZMovePerStep;

	private double maxThetaMovePerStep;

	private boolean flipEnabled;
	
	/**
	 * ctor
	 * 
	 * @param aMinZoom min zoom ratio
	 */
	public EyeMovementParms( double aMinZoom ) {
		this( aMinZoom, aMinZoom, true );
	}

	/**
	 * ctor
	 * 
	 * @param aMinZoom min zoom ratio
	 * @param aStartZoom
	 * @param aFlipEnabled
	 */
	public EyeMovementParms( double aMinZoom, double aStartZoom, boolean aFlipEnabled ) {
		this( aMinZoom, aStartZoom, aFlipEnabled, 1d, 1d, 1d, 1d );
	}

	/**
	 * ctor
	 * 
	 * @param aMinZoom min zoom ratio
	 * @param aStartZoom
	 * @param aFlipEnabled
	 * @param aMaxXMovePerStep b/w 0.0d and 1.0d, that is the max the eye can move in the x
	 * direction in one step (where -1.0 and 1.0 are the edges of the board)
	 * @param aMaxYMovePerStep b/w 0.0d and 1.0d, that is the max the eye can move in the y
	 * direction in one step (where -1.0 and 1.0 are the edges of the board)
	 * @param aMaxZMovePerStep b/w 0.0d and 1.0d, that is the max the eye can move in the z
	 * direction in one step (where -1.0 and 1.0 are the edges of the board)
	 * @param aMaxThetaMovePerStep b/w 0.0d and 1.0d, that is the max the eye can rotate in one
	 * step (where -1.0 and 1.0 are the rotation bounds)
	 */
	public EyeMovementParms( double aMinZoom, double aStartZoom, boolean aFlipEnabled,
			double aMaxXMovePerStep, double aMaxYMovePerStep, double aMaxZMovePerStep,
			double aMaxThetaMovePerStep ) {
		super();
		if ( aMinZoom <= 0.0d || aMinZoom > 1.0d )
			throw new IllegalArgumentException( "zoom factor must be > 0 and <= 1: " + aMinZoom );
		minZoom = aMinZoom;
		startZoom = aStartZoom;
		if ( startZoom < minZoom ) {
			logger.warn( "start zoom " + startZoom + " < min zoom " + minZoom
					+ ": changing start zoom to " + minZoom );
			startZoom = minZoom;
		}
		else if ( startZoom > 1.0d ) {
			logger.warn( "start zoom > 1.0: changing start zoom to 1.0" );
			startZoom = 1.0d;
		}
		flipEnabled = aFlipEnabled;
		maxXMovePerStep = aMaxXMovePerStep;
		maxYMovePerStep = aMaxYMovePerStep;
		maxZMovePerStep = aMaxZMovePerStep;
		maxThetaMovePerStep = aMaxThetaMovePerStep;
	}

	/**
	 * @return gets flipEnabled.
	 */
	public synchronized boolean isFlipEnabled() {
		return flipEnabled;
	}

	/**
	 * @return gets maxThetaMovePerStep.
	 */
	public synchronized double getMaxThetaMovePerStep() {
		return maxThetaMovePerStep;
	}

	/**
	 * @return gets maxXMovePerStep.
	 */
	public synchronized double getMaxXMovePerStep() {
		return maxXMovePerStep;
	}

	/**
	 * @return gets maxYMovePerStep.
	 */
	public synchronized double getMaxYMovePerStep() {
		return maxYMovePerStep;
	}

	/**
	 * @return gets maxZMovePerStep.
	 */
	public synchronized double getMaxZMovePerStep() {
		return maxZMovePerStep;
	}

	/**
	 * @return gets minZoom.
	 */
	public synchronized double getMinZoom() {
		return minZoom;
	}

	/**
	 * @return gets startZoom.
	 */
	public synchronized double getStartZoom() {
		return startZoom;
	}
}
