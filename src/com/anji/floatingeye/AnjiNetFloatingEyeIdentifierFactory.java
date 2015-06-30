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
 * created by Philip Tucker on Dec 11, 2004
 */
package com.anji.floatingeye;

import com.anji.imaging.IdentifyImageFitnessFunction;
import com.anji.imaging.Java2DSurface;
import com.anji.imaging.Surface;
import com.anji.nn.AnjiNet;
import com.anji.util.Configurable;
import com.anji.util.Properties;

/**
 * AnjiNetFloatingEyeIdentifierFactory
 */
public class AnjiNetFloatingEyeIdentifierFactory implements Configurable {

/**
 * max movement per step as percentage of surface width
 */
public final static String EYE_MAX_X_KEY = "eye.max-per-step.x";

/**
 * max movement per step as percentage of surface height
 */
public final static String EYE_MAX_Y_KEY = "eye.max-per-step.y";

/**
 * max zoom per step as percentage of zoom range
 */
public final static String EYE_MAX_Z_KEY = "eye.max-per-step.z";

/**
 * max rotate per step as percentage of 2*pi radians
 */
public final static String EYE_MAX_THETA_KEY = "eye.max-per-step.theta";

private final static String EYE_DIM_KEY = "eye.dimension";

private final static String EVAL_FRINKS_KEY = "eye.eval.frinks";

private final static String EVAL_STEPS_KEY = "eye.eval.steps";

private final static String FLIP_ENABLED_KEY = "eye.flip.enabled";

private final static String EYE_DISPLAY_KEY = "eye.display";

private final static String EYE_STEP_SLEEP_MILLIS_KEY = "eye.step.sleep.millis";

private final static String EYE_ZOOM_START_KEY = "eye.zoom.start";

private int evalFactor;

private int evalSteps;

private boolean flipEnabled;

private int eyeDim;

private double maxXPerStep;

private double maxYPerStep;

private double maxZPerStep;

private double maxThetaPerStep;

private long stepSleepMillis;

private double eyeStartZoom;

private boolean doDisplay;

/**
 * init
 * 
 * @param props
 */
public void init( Properties props ) {
	maxXPerStep = props.getDoubleProperty( EYE_MAX_X_KEY, 1.0d );
	maxYPerStep = props.getDoubleProperty( EYE_MAX_Y_KEY, 1.0d );
	maxZPerStep = props.getDoubleProperty( EYE_MAX_Z_KEY, 1.0d );
	maxThetaPerStep = props.getDoubleProperty( EYE_MAX_THETA_KEY, 1.0d );
	eyeDim = props.getIntProperty( EYE_DIM_KEY );

	// strategy to determine # of steps for each evaluation by the eye
	evalSteps = props.getIntProperty( EVAL_STEPS_KEY, -1 );
	int evalFrinks = props.getIntProperty( EVAL_FRINKS_KEY, -1 );
	if ( ( evalSteps <= 0 ) && ( evalFrinks <= 0 ) )
		throw new IllegalArgumentException( "must specify positive value for either "
				+ EVAL_FRINKS_KEY + " or " + EVAL_STEPS_KEY );
	if ( ( evalSteps > 0 ) && ( evalFrinks > 0 ) )
		throw new IllegalArgumentException( "can not specify positive values for both "
				+ EVAL_FRINKS_KEY + " and " + EVAL_STEPS_KEY );
	evalFactor = ( evalFrinks > 0 ) ? ( evalFrinks * 1000000 ) : -1;

	flipEnabled = props.getBooleanProperty( FLIP_ENABLED_KEY, true );

	stepSleepMillis = props.getLongProperty( EYE_STEP_SLEEP_MILLIS_KEY, 0 );

	eyeStartZoom = props.getDoubleProperty( EYE_ZOOM_START_KEY, 1.0d );
	doDisplay = props.getBooleanProperty( EYE_DISPLAY_KEY, false );

	// a bit klugy - make sure surface can be displayed in Swing component
	Class surfaceClass = props.getClassProperty( IdentifyImageFitnessFunction.SURFACE_CLASS_KEY
			+ Properties.CLASS_SUFFIX );
	if ( doDisplay && !( Java2DSurface.class.isAssignableFrom( surfaceClass ) ) )
		throw new IllegalArgumentException( "eye display requires Java2DSurface" );
}

/**
 * @param net
 * @param surface
 * @return new identifier
 */
public AnjiNetFloatingEyeImageIdentifier getIdentifier( AnjiNet net, Surface surface ) {
	int imgWidth = surface.getWidth();
	int imgHeight = surface.getHeight();

	// create eye
	double minZoom = (double) eyeDim / Math.min( imgWidth, imgHeight );
	int numSteps = ( evalFactor > 0 ) ? (int) Math.round( (double) evalFactor / net.cost() )
			: evalSteps;
	AnjiNetFloatingEyeImageIdentifier result = new AnjiNetFloatingEyeImageIdentifier( net,
			surface, eyeDim, new EyeMovementParms( minZoom, eyeStartZoom, flipEnabled, maxXPerStep,
					maxYPerStep, maxZPerStep, maxThetaPerStep ), doDisplay, numSteps );
	result.setStepSleepMillis( stepSleepMillis );
	return result;
}

}
