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
 * created by Philip Tucker on Aug 13, 2004
 */

package com.anji.floatingeye;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import com.anji.imaging.IntLocation2D;
import com.anji.imaging.Java2DSurface;

/**
 * 
 * @author Philip Tucker
 */
public class SurfaceCanvas extends Canvas {

private Java2DSurface canvasSurface;

private FloatingEye eye;

private double surfaceToCanvasScaleFactorX;

private double surfaceToCanvasScaleFactorY;

private AffineTransformOp surfaceXformOp;

/**
 * ctor
 * @param aSurface
 * @param anEye
 * @param canvasWidth
 * @param canvasHeight
 */
public SurfaceCanvas( Java2DSurface aSurface, FloatingEye anEye, int canvasWidth,
		int canvasHeight ) {
	canvasSurface = aSurface;
	eye = anEye;
	surfaceToCanvasScaleFactorX = (double) canvasWidth / canvasSurface.getWidth();
	surfaceToCanvasScaleFactorY = (double) canvasHeight / canvasSurface.getHeight();
	AffineTransform xform = AffineTransform.getScaleInstance( surfaceToCanvasScaleFactorX,
			surfaceToCanvasScaleFactorY );
	surfaceXformOp = new AffineTransformOp( xform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
	setSize( canvasWidth, canvasHeight );
}

private IntLocation2D calculatePointOnCircle( IntLocation2D center, double radius, double theta ) {
	// get point on circle centered at origin
	double x = radius * Math.cos( theta );
	double y = radius * Math.sin( theta );

	// adjust for center of circle
	x += center.x;
	y += center.y;

	// adjust for surface to canvas factor
	x *= surfaceToCanvasScaleFactorX;
	y *= surfaceToCanvasScaleFactorY;

	return new IntLocation2D( (int) Math.round( x ), (int) Math.round( y ) );
}

/**
 * draw surface, then draw outline of eye's viewable area
 * @see java.awt.Component#paint(java.awt.Graphics)
 */
public void paint( Graphics g ) {
	Color origColor = g.getColor();

	// draw surface
	BufferedImage img = surfaceXformOp.filter( canvasSurface.getBufferedImage(), null );
	g.drawImage( img, 0, 0, null );

	// get location of corner 1 before rotation, and length of half diagonal
	int halfHeight = (int) Math.round( ( eye.getWidth() / eye.getZoom() ) / 2d );
	int halfWidth = (int) Math.round( ( eye.getHeight() / eye.getZoom() ) / 2d );
	double corner1thetaStart = Math.atan( halfWidth / halfHeight );
	double halfDiagonal = Math.sqrt( ( halfWidth * halfWidth )
			+ ( halfHeight * halfHeight ) );

	double corner1theta = corner1thetaStart + eye.getEyeDirectionRadians();
	double corner2theta = corner1theta + ( Math.PI / 2 );
	double corner3theta = corner2theta + ( Math.PI / 2 );
	double corner4theta = corner3theta + ( Math.PI / 2 );
	double centerTopTheta = corner3theta + ( Math.PI / 4 );
	IntLocation2D eyeLocation = eye.getSurfaceLocation();
	IntLocation2D corner1 = calculatePointOnCircle( eyeLocation, halfDiagonal, corner1theta );
	IntLocation2D corner2 = calculatePointOnCircle( eyeLocation, halfDiagonal, corner2theta );
	IntLocation2D corner3 = calculatePointOnCircle( eyeLocation, halfDiagonal, corner3theta );
	IntLocation2D corner4 = calculatePointOnCircle( eyeLocation, halfDiagonal, corner4theta );
	IntLocation2D center = calculatePointOnCircle( eyeLocation, 0, centerTopTheta );
	IntLocation2D centerTop = calculatePointOnCircle( eyeLocation, halfHeight, centerTopTheta );

	// draw blue rectangle with top center line
	g.setColor( Color.BLUE );
	int[] xPoints = new int[] { corner4.x, corner1.x, corner2.x, corner3.x };
	int[] yPoints = new int[] { corner4.y, corner1.y, corner2.y, corner3.y };
	g.drawPolygon( xPoints, yPoints, 4 );
	xPoints = new int[] { center.x, centerTop.x };
	yPoints = new int[] { center.y, centerTop.y };
	g.drawPolyline( xPoints, yPoints, 2 );

	g.setColor( origColor );
}
}
