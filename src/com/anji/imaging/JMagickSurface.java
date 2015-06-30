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
 * Created on Sep 21, 2004 by Philip Tucker
 */
package com.anji.imaging;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;

import org.apache.log4j.Logger;

import com.anji.util.Properties;

/**
 * JMagickSurface
 */
public class JMagickSurface implements Surface {

	private static final Logger logger = Logger.getLogger( JMagickSurface.class );

	private MagickImage data;

	private Dimension surfaceDimension;

	private int[] pixels;

	private MagickImageTransformer xformer = new MagickImageTransformer();

	// R = red, G = green, B = blue, A = alpha, C = cyan, Y = yellow, M = magenta, K = black, or I
	// = intensity (for grayscale)
	private final static String PIXEL_MAP = "I";

	/**
	 * default ctor
	 */
	public JMagickSurface() {
		super();
	}

	/**
	 * @see com.anji.imaging.Surface#setImage(java.io.File)
	 */
	public synchronized void setImage( File aFile ) throws IOException {
		String fullPath = aFile.getAbsolutePath();
		try {
			ImageInfo info = new ImageInfo( fullPath );
			data = new MagickImage( info );
			
			// parse image dimensions
			String sizeStr = info.getSize();
			int xIdx = sizeStr.indexOf( 'x' );
			String widthStr = sizeStr.substring( 0, xIdx );
			String heightStr = sizeStr.substring( xIdx + 1 );
			int width = Integer.parseInt( widthStr );
			int height = Integer.parseInt( heightStr );

			// set or validate image dimensions
			if ( surfaceDimension == null )
				surfaceDimension = new Dimension( width, height );
			else if ( ( width != surfaceDimension.getWidth() )
					|| ( height != surfaceDimension.getHeight() ) )
				throw new IllegalArgumentException( "JMagickSurface resize not yet implemented" );

			pixels = null;
			xformer.setImage( data );
		}
		catch ( MagickException e ) {
			String msg = "error creating MagickImage from file " + fullPath;
			logger.error( msg );
			throw new IOException( msg );
		}
	}

	/**
	 * @see com.anji.imaging.Surface#transform(com.anji.imaging.TransformParameters)
	 */
	public synchronized int[] transform( TransformParameters parms ) {
		int width = parms.getCropWidth();
		int height = parms.getCropHeight();
		int[] result = new int[ width * height ];
		try {
			xformer.transform( parms ).dispatchImage( 0, 0, width, height, PIXEL_MAP, result );
		}
		catch ( MagickException e ) {
			String msg = "error transforming image";
			logger.error( msg, e );
			throw new IllegalStateException( msg + ": " +  e );
		}
		return result;
	}

	/**
	 * @see com.anji.imaging.Surface#getData()
	 */
	public synchronized int[] getData() {
		if ( pixels == null ) {
			try {
				Dimension dim = data.getDimension();
				data.dispatchImage( 0, 0, dim.width, dim.height, PIXEL_MAP, pixels );
			}
			catch ( MagickException e ) {
				String msg = "error dispatching pixels";
				logger.error( msg, e );
				throw new IllegalStateException( msg + ": " +  e );
			}
		}
		return pixels;
	}

	/**
	 * @see com.anji.imaging.Surface#getWidth()
	 */
	public synchronized int getWidth() {
		try {
			return data.getDimension().width;
		}
		catch ( MagickException e ) {
			String msg = "error getting image width";
			logger.error( msg, e );
			throw new IllegalStateException( msg + ": " +  e );
		}
	}

	/**
	 * @see com.anji.imaging.Surface#getHeight()
	 */
	public synchronized int getHeight() {
		try {
			return data.getDimension().height;
		}
		catch ( MagickException e ) {
			String msg = "error getting image height";
			logger.error( msg, e );
			throw new IllegalStateException( msg + ": " +  e );
		}
	}

	/**
	 * @see com.anji.imaging.Surface#getValue(int, int)
	 */
	public synchronized int getValue( int x, int y ) {
		return getData()[ x + ( y * getWidth() ) ];
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

		xformer = (MagickImageTransformer) props.newObjectProperty( TRANSFORMER_CLASS_KEY );
	}
}
