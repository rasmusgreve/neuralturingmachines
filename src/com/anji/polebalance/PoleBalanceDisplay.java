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
 * created by Philip Tucker on Jul 6, 2004
 */

package com.anji.polebalance;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

/**
 * @author Philip Tucker
 */
public class PoleBalanceDisplay extends JFrame {

private final static String NAME = "Pole Balance Display";

private PoleBalanceCanvas poleBalanceCanvas;

private final static int CANVAS_WIDTH = 300;

private final static int CANVAS_HEIGHT = 75;

private final static int SPEED_CONTROL_MIDPOINT = 20;

private int maxSteps;

private JSlider speedControl;

private JProgressBar progressBar;

/**
 * ctor
 * 
 * @param aTrackLength
 * @param aPoleLengths
 * @param aMaxSteps
 */
public PoleBalanceDisplay( double aTrackLength, double[] aPoleLengths, int aMaxSteps ) {
	super( NAME );
	init( aTrackLength, aPoleLengths, aMaxSteps );
}

private void init( double aTrackLength, double[] aPoleLengths, int aMaxSteps ) {
	maxSteps = aMaxSteps;

	addWindowListener( new WindowAdapter() {

		public void windowClosing( WindowEvent e ) {
			setVisible( false );
			dispose();
		}
	} );

	// this frame has 3 sections - status, surface canvas, and eye canvas
	GridLayout mainLayout = new GridLayout( 2, 1 );
	mainLayout.setHgap( 10 );
	mainLayout.setVgap( 10 );
	getContentPane().setLayout( mainLayout );
	GridLayout topPanelLayout = new GridLayout( 2, 1 );
	topPanelLayout.setHgap( 10 );
	topPanelLayout.setVgap( 10 );
	Panel topPanel = new Panel( topPanelLayout );

	// 1 - status area
	speedControl = new JSlider( SwingConstants.HORIZONTAL, 0, SPEED_CONTROL_MIDPOINT * 2,
			SPEED_CONTROL_MIDPOINT );
	speedControl.setName( "Speed" );

	// 2 - progress bar
	progressBar = new JProgressBar( SwingConstants.HORIZONTAL, 0, maxSteps );
	progressBar.setName( "Steps" );
	progressBar.setValue( 0 );

	// 3 - surface canvas
	poleBalanceCanvas = new PoleBalanceCanvas( aTrackLength, aPoleLengths );
	poleBalanceCanvas.setSize( new Dimension( CANVAS_WIDTH * 2, CANVAS_HEIGHT ) );

	topPanel.add( speedControl );
	topPanel.add( progressBar );
	getContentPane().add( topPanel );
	getContentPane().add( poleBalanceCanvas );

	pack();
}

/**
 * Perform one step in processing.
 * @param currentStep
 * @param aCartPos
 * @param aPoleAngles
 */
public void step( int currentStep, double aCartPos, double[] aPoleAngles ) {
	int stepsToSleep = speedControl.getMaximum() - speedControl.getValue();

	// negative stepsToSkip means we sleep a millisecond for each step
	if ( stepsToSleep > 0 ) {
		try {
			Thread.sleep( stepsToSleep );
		}
		catch ( InterruptedException e ) {
			// ignore
		}
	}
	
	progressBar.setValue( currentStep );
	poleBalanceCanvas.step( aCartPos, aPoleAngles );
	poleBalanceCanvas.repaint();
}

/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	return NAME;
}
}
