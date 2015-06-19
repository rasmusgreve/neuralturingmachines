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

package com.anji.floatingeye.test;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.anji.Copyright;
import com.anji.floatingeye.AnjiNetFloatingEyeIdentifierFactory;
import com.anji.floatingeye.EyeCanvas;
import com.anji.floatingeye.EyeMovementParms;
import com.anji.floatingeye.FloatingEye;
import com.anji.floatingeye.SurfaceCanvas;
import com.anji.imaging.IdentifyImageFitnessFunction;
import com.anji.imaging.Java2DSurface;
import com.anji.util.Properties;

/**
 * @author Philip Tucker
 */
public class FloatingEyeTestDriver extends JFrame {

private final static String NAME = "Floating Eye Test Driver";

private final static int sliderFactor = 100000;

private final static double MIN_ZOOM = 1d / 3d;

private JSlider xSlider = new JSlider( SwingConstants.HORIZONTAL, -sliderFactor, sliderFactor,
		0 );

private JSlider ySlider = new JSlider( SwingConstants.HORIZONTAL, -sliderFactor, sliderFactor,
		0 );

private JSlider zSlider = new JSlider( SwingConstants.HORIZONTAL, -sliderFactor, sliderFactor,
		0 );

private JSlider thetaSlider = new JSlider( SwingConstants.HORIZONTAL, -sliderFactor,
		sliderFactor, 0 );

private FloatingEye eye;

/**
 * <code>protected</code> visibility increases performance of
 * <code>ChangeListener.stateChanged()</code> for <code>xSlider</code>
 */
protected TestNeuron xNeuron = new TestNeuron();

/**
 * <code>protected</code> visibility increases performance of
 * <code>ChangeListener.stateChanged()</code> for <code>ySlider</code>
 */
protected TestNeuron yNeuron = new TestNeuron();

/**
 * <code>protected</code> visibility increases performance of
 * <code>ChangeListener.stateChanged()</code> for <code>zSlider</code>
 */
protected TestNeuron zNeuron = new TestNeuron();

/**
 * <code>protected</code> visibility increases performance of
 * <code>ChangeListener.stateChanged()</code> for <code>thetaSlider</code>
 */
protected TestNeuron thetaNeuron = new TestNeuron();

private Canvas surfaceCanvas;

private Canvas eyeCanvas;

private double maxXPerStep;

private double maxYPerStep;

private double maxZPerStep;

private double maxThetaPerStep;

private void connectGuiToEye() {
	xSlider.addChangeListener( new ChangeListener() {

		public void stateChanged( ChangeEvent arg0 ) {
			JSlider src = (JSlider) arg0.getSource();
			xNeuron.setValue( (double) src.getValue() / sliderFactor );
		}
	} );
	ySlider.addChangeListener( new ChangeListener() {

		public void stateChanged( ChangeEvent arg0 ) {
			JSlider src = (JSlider) arg0.getSource();
			yNeuron.setValue( (double) src.getValue() / sliderFactor );
		}
	} );
	zSlider.addChangeListener( new ChangeListener() {

		public void stateChanged( ChangeEvent arg0 ) {
			JSlider src = (JSlider) arg0.getSource();
			zNeuron.setValue( (double) src.getValue() / sliderFactor );
		}
	} );
	thetaSlider.addChangeListener( new ChangeListener() {

		public void stateChanged( ChangeEvent arg0 ) {
			JSlider src = (JSlider) arg0.getSource();
			thetaNeuron.setValue( (double) src.getValue() / sliderFactor );
		}
	} );
}

// private final static int WHITE = 0;

// private final static int GREY = ( 128 << 24 );

// private final static int RED = GREY + ( 128 << 16 );

// private final static int GREEN = GREY + ( 128 << 8 );

// private final static int BLUE = GREY + 128;

// private final static int PURPLE = ( RED + BLUE ) - GREY;

//	/**
//	 * ctor
//	 */
//	public FloatingEyeTestDriver() {
//		super( NAME );
//
//		//
//		// create surface
//		//
//		int imageSize = 300;
//		Surface surface = new Surface( imageSize, 0, GREY );
//		for ( int x = (int) ( imageSize * 0.1 ); x < imageSize * 0.4; ++x )
//			for ( int y = (int) ( imageSize * 0.1 ); y < (int) ( imageSize * 0.4 ); ++y )
//				surface.setValue( x, y, RED );
//		for ( int x = (int) ( imageSize * 0.15 ); x < imageSize * 0.35; ++x )
//			for ( int y = (int) ( imageSize * 0.15 ); y < (int) ( imageSize * 0.35 ); ++y )
//				surface.setValue( x, y, WHITE );
//		for ( int x = (int) ( imageSize * 0.6 ); x < (int) ( imageSize * 0.9 ); ++x )
//			for ( int y = (int) ( imageSize * 0.1 ); y < (int) ( imageSize * 0.4 ); ++y )
//				surface.setValue( x, y, GREEN );
//		for ( int x = (int) ( imageSize * 0.1 ); x < (int) ( imageSize * 0.4 ); ++x )
//			for ( int y = (int) ( imageSize * 0.6 ); y < (int) ( imageSize * 0.9 ); ++y )
//				surface.setValue( x, y, BLUE );
//		for ( int x = (int) ( imageSize * 0.6 ); x < (int) ( imageSize * 0.9 ); ++x )
//			for ( int y = (int) ( imageSize * 0.6 ); y < (int) ( imageSize * 0.9 ); ++y )
//				surface.setValue( x, y, PURPLE );
//
//		init( surface );
//	}

/**
 * ctor
 * 
 * @param surface
 * @param aMaxXPerStep
 * @param aMaxYPerStep
 * @param aMaxZPerStep
 * @param aMaxThetaPerStep
 */
public FloatingEyeTestDriver( Java2DSurface surface, double aMaxXPerStep, double aMaxYPerStep,
		double aMaxZPerStep, double aMaxThetaPerStep ) {
	super( NAME );
	maxXPerStep = aMaxXPerStep;
	maxYPerStep = aMaxYPerStep;
	maxZPerStep = aMaxZPerStep;
	maxThetaPerStep = aMaxThetaPerStep;
	init( surface );
}

private void init( Java2DSurface surface ) {
	List controlNeurons = new ArrayList( 4 );
	xNeuron.setValue( (double) xSlider.getValue() / sliderFactor );
	if ( maxXPerStep != 0d )
		controlNeurons.add( xNeuron );
	yNeuron.setValue( (double) ySlider.getValue() / sliderFactor );
	if ( maxYPerStep != 0d )
		controlNeurons.add( yNeuron );
	zNeuron.setValue( (double) zSlider.getValue() / sliderFactor );
	if ( maxZPerStep != 0d )
		controlNeurons.add( zNeuron );
	thetaNeuron.setValue( (double) thetaSlider.getValue() / sliderFactor );
	if ( maxThetaPerStep != 0d )
		controlNeurons.add( thetaNeuron );
	int eyeSize = (int) ( Math.min( surface.getWidth(), surface.getHeight() ) * MIN_ZOOM );
	eye = new FloatingEye( "test drive floating eye", controlNeurons, surface, eyeSize,
			new EyeMovementParms( MIN_ZOOM, MIN_ZOOM, true, maxXPerStep, maxYPerStep, maxZPerStep,
					maxThetaPerStep ) );

	//
	// init GUI
	//
	addWindowListener( new WindowAdapter() {

		public void windowClosing( WindowEvent e ) {
			System.exit( 0 );
		}
	} );
	GridLayout mainLayout = new GridLayout( 1, 3 );
	mainLayout.setHgap( 10 );
	mainLayout.setVgap( 10 );
	getContentPane().setLayout( mainLayout );

	Panel controlPanel = new Panel( new GridLayout( 4, 1 ) );

	Panel xPanel = new Panel( new FlowLayout() );
	xPanel.add( new JLabel( "        X" ) );
	xPanel.add( xSlider );
	controlPanel.add( xPanel );

	Panel yPanel = new Panel( new FlowLayout() );
	yPanel.add( new JLabel( "        Y" ) );
	yPanel.add( ySlider );
	controlPanel.add( yPanel );

	Panel zPanel = new Panel( new FlowLayout() );
	zPanel.add( new JLabel( "        Z" ) );
	zPanel.add( zSlider );
	controlPanel.add( zPanel );

	Panel thetaPanel = new Panel( new FlowLayout() );
	thetaPanel.add( new JLabel( "theta" ) );
	thetaPanel.add( thetaSlider );
	controlPanel.add( thetaPanel );

	surfaceCanvas = new SurfaceCanvas( surface, eye, surface.getWidth(), surface.getHeight() );
	surfaceCanvas.setSize( surface.getWidth(), surface.getHeight() );
	surfaceCanvas.setBackground( Color.WHITE );

	eyeCanvas = new EyeCanvas( eye, surface.getWidth(), surface.getHeight() );
	eyeCanvas.setSize( surface.getWidth(), surface.getHeight() );
	eyeCanvas.setBackground( Color.WHITE );

	getContentPane().add( controlPanel );
	getContentPane().add( surfaceCanvas );
	getContentPane().add( eyeCanvas );

	pack();

	connectGuiToEye();
}

/**
 *  
 */
public void run() {
	while ( true ) {
		eye.step();
		surfaceCanvas.repaint();
		eyeCanvas.repaint();
	}
}

/**
 * @param args
 * @throws Exception
 */
public static void main( String[] args ) throws Exception {
	System.out.println( Copyright.STRING );
	if ( args.length < 1 ) {
		System.err.println( "usage: <cmd> <image-file>" );
		System.exit( -1 );
	}

	Properties props = new Properties( "test_eye.properties" );
	Java2DSurface surface = (Java2DSurface) props
			.newObjectProperty( IdentifyImageFitnessFunction.SURFACE_CLASS_KEY );
	surface.setImage( new File( args[ 0 ] ) );

	FloatingEyeTestDriver fetd = new FloatingEyeTestDriver( surface, props.getDoubleProperty(
			AnjiNetFloatingEyeIdentifierFactory.EYE_MAX_X_KEY, 1.0d ), props.getDoubleProperty(
			AnjiNetFloatingEyeIdentifierFactory.EYE_MAX_Y_KEY, 1.0d ), props.getDoubleProperty(
			AnjiNetFloatingEyeIdentifierFactory.EYE_MAX_Z_KEY, 1.0d ), props.getDoubleProperty(
			AnjiNetFloatingEyeIdentifierFactory.EYE_MAX_THETA_KEY, 1.0d ) );
	fetd.setVisible( true );
	fetd.run();
}
}
