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
 * created by Philip Tucker on Dec 6, 2004
 */

package com.anji.floatingeye;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.anji.imaging.DoubleLocation2D;
import com.anji.imaging.Identifier;
import com.anji.imaging.Java2DSurface;
import com.anji.imaging.Surface;
import com.anji.nn.AnjiNet;
import com.anji.nn.StepHourglassConnection;

/**
 * The purpose of this class is to use an <code>AnjiNet</code> and <code>FloatingEye</code>
 * to identify an image. Use <code>AnjiNetFloatingEyeIdentifierFactory</code> to construct this.
 * @see com.anji.floatingeye.AnjiNetFloatingEyeIdentifierFactory
 * @see com.anji.imaging.Identifier
 * 
 * @author Philip Tucker
 */
class AnjiNetFloatingEyeImageIdentifier implements Identifier {

private static Logger logger = Logger.getLogger( AnjiNetFloatingEyeImageIdentifier.class );

private AnjiNetFloatingEye eye;

private WeightedAffinityListener weightedAffinityListener = new WeightedAffinityListener();

private FloatingEyeDisplay display;

private StepHourglassConnection hourglassConnection = new StepHourglassConnection();

private long stepSleepMillis;

private Surface surface;

private int numSteps;

/**
 * ctor
 * 
 * @param aNet
 * @param aSurface
 * @param anEyeDim
 * @param aMovementParms
 * @param doDisplay
 * @param aNumSteps number of steps allowed for each evaluation
 */
AnjiNetFloatingEyeImageIdentifier( AnjiNet aNet, Surface aSurface, int anEyeDim,
		EyeMovementParms aMovementParms, boolean doDisplay, int aNumSteps ) {
	super();
	surface = aSurface;
	CompositeAffinityListener compositeListener = new CompositeAffinityListener();
	compositeListener.add( weightedAffinityListener );
	List additionalInputConns = new ArrayList( 1 );
	additionalInputConns.add( hourglassConnection );
	eye = new AnjiNetFloatingEye( aNet, surface, anEyeDim, aMovementParms, compositeListener,
			additionalInputConns );
	if ( doDisplay ) {
		display = new FloatingEyeDisplay( (Java2DSurface) aSurface, eye.getFloatingEye() );
		display.setVisible( true );
		compositeListener.add( display );
	}
	stepSleepMillis = 0;
	numSteps = aNumSteps;
}

/**
 * @param imgFile
 * @return confidence between 0 (certain it is not a match) and 1 (certain it is a match)
 * @throws IOException
 * @see Identifier#identify(File)
 */
public float identify( File imgFile ) throws IOException {
	surface.setImage( imgFile );
	weightedAffinityListener.reset( numSteps );
	hourglassConnection.reset( numSteps );
	eye.reset();

	for ( int i = 0; i < numSteps; ++i ) {
		eye.step();

		if ( display != null ) {
			StringBuffer status = new StringBuffer();
			status.append( "cost=" ).append( eye.cost() ).append( "\t# steps=" ).append(
					eye.getStepNum() ).append( "/" ).append( numSteps ).append( "\timg=" ).append(
					imgFile.getName() ).append( "\n" ).append( eye.toString() ).append(
					"\nweighted affinity=" ).append(
					DoubleLocation2D.TO_STRING_FORMAT.format( weightedAffinityListener
							.getWeightedAffinity() ) );
			String statusStr = status.toString();

			display.setStatus( statusStr );
			display.repaint();
			try {
				Thread.sleep( stepSleepMillis );
			}
			catch ( InterruptedException e ) {
				logger.warn( "sleep interrupted", e );
			}
		}
		
		hourglassConnection.step();
	}

	return (float) weightedAffinityListener.getWeightedAffinity();
}

/**
 * dispose of any remaining resources; identify must not be called again after dispose
 * @see Identifier#dispose()
 */
public void dispose() {
	// display destroy
	if ( display != null ) {
		display.setVisible( false );
		display.dispose();
	}
}

/**
 * @return number of steps used by previous call to <code>identify(File)</code>
 * @see AnjiNetFloatingEye#getStepNum()
 * @see AnjiNetFloatingEyeImageIdentifier#identify(File)
 * @see Identifier#getStepNum()
 */
public int getStepNum() {
	return eye.getStepNum();
}

/**
 * @see com.anji.imaging.Identifier#cost()
 */
public long cost() {
	return eye.cost();
}

/**
 * set number of milliseconds to sleep between steps
 * @param aStepSleepMillis
 */
public void setStepSleepMillis( long aStepSleepMillis ) {
	this.stepSleepMillis = aStepSleepMillis;
}
}
