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

package com.anji.floatingeye;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.anji.imaging.IntLocation2D;
import com.anji.imaging.Java2DSurface;

/**
 * @author Philip Tucker
 */
public class FloatingEyeDisplay extends JFrame implements AffinityListener {

private final static String NAME = "Floating Eye Display";

private TextArea statusArea;

private FloatingEye eye;

private Canvas surfaceCanvas;

private Canvas eyeCanvas;

private XYSeries affinitySeries = new XYSeries( "affinityValue" );

private final static int IMG_WIDTH = 150;

private final static int IMG_HEIGHT = 150;

/**
 * @see java.awt.Component#repaint()
 */
public void repaint() {
	super.repaint();
	statusArea.repaint();
	surfaceCanvas.repaint();
	eyeCanvas.repaint();
}

/**
 * ctor
 * 
 * @param aSurface
 * @param anEye
 */
public FloatingEyeDisplay( Java2DSurface aSurface, FloatingEye anEye ) {
	super( NAME );
	init( aSurface, anEye );
}

private void init( Java2DSurface surface, FloatingEye anEye ) {
	eye = anEye;

	// this frame has 3 sections - status, surface canvas, and eye canvas
	addWindowListener( new WindowAdapter() {

		public void windowClosing( WindowEvent e ) {
			setVisible( false );
			dispose();
		}
	} );
	GridLayout mainLayout = new GridLayout( 2, 1 );
	mainLayout.setHgap( 10 );
	mainLayout.setVgap( 10 );
	getContentPane().setLayout( mainLayout );
	GridLayout topPanelLayout = new GridLayout( 2, 1 );
	topPanelLayout.setHgap( 10 );
	topPanelLayout.setVgap( 10 );
	Panel topPanel = new Panel( topPanelLayout );
	GridLayout bottomPanelLayout = new GridLayout( 1, 2 );
	bottomPanelLayout.setHgap( 10 );
	bottomPanelLayout.setVgap( 10 );
	Panel bottomPanel = new Panel( bottomPanelLayout );

	// 1 - status area
	statusArea = new TextArea( "", 1, 1, TextArea.SCROLLBARS_VERTICAL_ONLY );
	statusArea.setEditable( false );
	statusArea.setFont( new Font( "Dialog", Font.PLAIN, 10 ) );
	statusArea.setSize( new Dimension( IMG_WIDTH * 2, IMG_HEIGHT / 2 ) );

	// 2 - affinity history chart
	XYSeriesCollection seriesCollection = new XYSeriesCollection( affinitySeries );
	JFreeChart chart = ChartFactory.createXYLineChart( "affinity history", "step", "affinity",
			seriesCollection, PlotOrientation.VERTICAL, false, false, false );
	ValueAxis rangeAxis = chart.getXYPlot().getRangeAxis();
	rangeAxis.setAutoRange( false );
	rangeAxis.setRange( 0d, 1d );
	ChartPanel chartPanel = new ChartPanel( chart );
	chartPanel.setPreferredSize( new Dimension( IMG_WIDTH * 2, IMG_HEIGHT / 2 ) );

	// 3 - surface canvas
	surfaceCanvas = new SurfaceCanvas( surface, eye, IMG_WIDTH, IMG_HEIGHT );
	surfaceCanvas.setBackground( Color.WHITE );

	// 4 - eye canvas
	eyeCanvas = new EyeCanvas( eye, IMG_WIDTH, IMG_HEIGHT );
	eyeCanvas.setBackground( Color.WHITE );

	topPanel.add( statusArea );
	topPanel.add( chartPanel );
	bottomPanel.add( surfaceCanvas );
	bottomPanel.add( eyeCanvas );
	getContentPane().add( topPanel );
	getContentPane().add( bottomPanel );

	pack();
}

/**
 * set display string
 * 
 * @param s
 */
public void setStatus( String s ) {
	statusArea.setText( s );
}

/**
 * @see com.anji.floatingeye.AffinityListener#updateAffinity(com.anji.imaging.IntLocation2D,
 * double)
 */
public void updateAffinity( IntLocation2D aPos, double aValue ) {
	affinitySeries.add( affinitySeries.getItemCount(), aValue );
}

/**
 * @see com.anji.floatingeye.AffinityListener#reset()
 */
public void reset() {
	affinitySeries.clear();
}

/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	return NAME;
}
}
