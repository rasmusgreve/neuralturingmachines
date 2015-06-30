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
 * created by Philip Tucker on Jun 12, 2004
 */

package com.anji.imaging;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.anji.util.Configurable;
import com.anji.util.Properties;

/**
 * A 2-d matrix of int values (this could be a graphical image or a game board) used by
 * <code>FloatingEye</code>. Also contains logic to transform surface image to eye image.
 * init() and one of tne of the setImage() functions must be called before this object is fully
 * initialized.
 * 
 * @author Philip Tucker
 */
public class Java2DSurface implements Surface, Configurable {

	private static final Logger logger = Logger.getLogger( Java2DSurface.class );

	private final static int IMAGE_TYPE = BufferedImage.TYPE_INT_ARGB;

	private BufferedImage data;

	private BufferedImage dataPlusGutter;

	private int[] pixels;

	private BufferedImageTransformer xformer = new DefaultBufferedImageTransformer();

	private Dimension surfaceDimension;

	/**
	 * default ctor
	 */
	public Java2DSurface() {
		super();
	}

	/**
	 * @see Surface#setImage(File)
	 * @see Java2DSurface#setImage(BufferedImage)
	 */
	public synchronized void setImage( File imageFile ) throws IOException {
		BufferedImage img = ImageIO.read( imageFile );
		if ( img == null )
			throw new IllegalArgumentException( "invalid image file: " + imageFile.getAbsolutePath() );
		setImage( img );
	}

	/**
	 * set values of 2-d rectangular surface <code>BufferedImage</code>; this image is larger
	 * than specified dimension so floating eye can go off the edge; these "off the edge" spaces
	 * are set to <code>aNonviewableSpaceValue</code>
	 * 
	 * @param newData
	 */
	public synchronized void setImage( BufferedImage newData ) {
		// scale image if necessary - if dimensions are not set, set them to the size of this image
		int width = newData.getWidth();
		int height = newData.getHeight();
		Image scaledData = newData;
		if ( surfaceDimension == null )
			surfaceDimension = new Dimension( width, height );
		else if ( ( width != surfaceDimension.getWidth() )
				|| ( height != surfaceDimension.getHeight() ) ) {
			width = (int) surfaceDimension.getWidth();
			height = (int) surfaceDimension.getHeight();
			scaledData = newData.getScaledInstance( width, height, Image.SCALE_AREA_AVERAGING );
		}

		// this is necessary to make sure we're in the proper color model
		data = new BufferedImage( width, height, IMAGE_TYPE );
		Graphics2D g = data.createGraphics();
		g.drawImage( scaledData, 0, 0, null );
		g.dispose();

		// maintain a "gutter" 75% of the longest dimension to give the floating eye space to wander
		// off the canvas
		int offset = Math.max( (int) ( ( surfaceDimension.getWidth() * 0.75 ) + 0.5 ),
				(int) ( ( surfaceDimension.getHeight() * 0.75 ) + 0.5 ) );
		dataPlusGutter = new BufferedImage( width + ( offset * 2 ), height + ( offset * 2 ),
				IMAGE_TYPE );
		g = dataPlusGutter.createGraphics();
		g.drawImage( newData, null, offset, offset );
		g.dispose();
		pixels = null;

		xformer.setImage( dataPlusGutter );
	}

	/**
	 * @return x dimension (aka width) of square 2-d surface
	 */
	public synchronized int getWidth() {
		return data.getWidth();
	}

	/**
	 * @return y dimension (aka height) of square 2-d surface
	 */
	public synchronized int getHeight() {
		return data.getHeight();
	}

	/**
	 * @param x
	 * @param y
	 * @return value for space at position <code>x</code>,<code>y</code>
	 */
	public synchronized int getValue( int x, int y ) {
		return data.getRGB( x, y );
	}

	/**
	 * @return <code>String</code> XML representation of object
	 */
	public synchronized String toXml() {
		StringBuffer result = new StringBuffer();
		result.append( "<surface>\n" );
		for ( int x = 0; x < getWidth(); ++x ) {
			result.append( "\t" );
			for ( int y = 0; y < getHeight(); ++y ) {
				result.append( "<space x=\"" ).append( x );
				result.append( "\" y=\"" ).append( y );
				result.append( "\" value=\"" ).append( getValue( x, y ) ).append( "\" />" );
			}
			result.append( "\n" );
		}
		result.append( "</surface>\n" );
		return result.toString();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		if ( data == null || dataPlusGutter == null )
			return "uninitialized";
		return "surface/fullcanvas=" + data.getWidth() + "," + data.getHeight() + "/"
				+ dataPlusGutter.getWidth() + "," + dataPlusGutter.getHeight();
	}

	/**
	 * @return viewable surface dataPlusGutter
	 * @see Surface#getData()
	 */
	public synchronized int[] getData() {
		if ( pixels == null )
			pixels = grabPixels( data, 0, 0, data.getWidth(), data.getHeight() );
		return pixels;
	}

	/**
	 * @return <code>BufferedImage</code> dataPlusGutter, not including "gutter"
	 */
	public synchronized BufferedImage getBufferedImage() {
		return data;
	}

	private static int[] grabPixels( Image img, int startX, int startY, int width, int height ) {
		PixelGrabber grabber = new PixelGrabber( img, startX, startY, width, height, false );
		try {
			if ( grabber.grabPixels() == false )
				throw new IllegalStateException( "error grabbing pixels" );
		}
		catch ( InterruptedException e ) {
			throw new IllegalStateException( "error grabbing pixels: " + e );
		}
		return (int[]) grabber.getPixels();
	}

	/**
	 * @see com.anji.imaging.Surface#transform(com.anji.imaging.TransformParameters)
	 */
	public synchronized int[] transform( TransformParameters parms ) {
		return grabPixels( xformer.transform( parms ), 0, 0, parms.getCropWidth(), parms
				.getCropHeight() );
	}

	/**
	 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
	 */
	public synchronized void init( Properties props ) throws Exception {
		try {
			surfaceDimension = new Dimension( props.getIntProperty( SURFACE_WIDTH_KEY ), props
					.getIntProperty( SURFACE_HEIGHT_KEY ) );
		}
		catch ( RuntimeException e ) {
			logger.warn( "surface dimensions not set, defaulting to size of first image" );
		}
		xformer = (BufferedImageTransformer) props.newObjectProperty( TRANSFORMER_CLASS_KEY );
	}
}
