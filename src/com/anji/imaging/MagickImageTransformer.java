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
 * Created on Sep 18, 2004 by Philip Tucker
 */
package com.anji.imaging;

import java.awt.Dimension;
import java.awt.Rectangle;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;

import org.apache.log4j.Logger;

/**
 * BufferedImageTransformer setImage must be called before object is initialized.
 */
public class MagickImageTransformer {

	private static final Logger logger = Logger.getLogger( MagickImageTransformer.class );

	private MagickImage data;

	/**
	 * @param anImg
	 */
	public void setImage( MagickImage anImg ) {
		data = anImg;
	}

	/**
	 * scales, rotates, translates, crops image
	 * 
	 * @param parms
	 * @return transformed image
	 */
	public MagickImage transform( TransformParameters parms ) {
		try {
			// translate
			MagickImage result = data.rollImage( parms.getTranslateX(), parms.getTranslateY() );
result.setFileName("c:/temp/test/1-translate.tif");
result.writeImage( new ImageInfo("c:/temp/test/1-translate.tif") );
			
			// scale
			double scaleX = parms.getScaleX();
			double scaleY = parms.getScaleY();
			if ( ( scaleX != 1.0d ) || ( scaleY != 1.0d ) ) {
				Dimension d = result.getDimension();
				int newCols = (int) ( ( d.getWidth() * scaleX ) + 0.5d );
				int newRows = (int) ( ( d.getHeight() * scaleY ) + 0.5d );
				result = result.scaleImage( newCols, newRows );
result.setFileName("c:/temp/test/2-scale.tif");
result.writeImage( new ImageInfo( "c:/temp/test/2-scale.tif" ) );
			}

			// rotate
			double rotate = ( parms.getRotate() * 360 ) / Math.PI;
			if ( rotate != 0.0d ) {
				result = result.rotateImage( rotate );
result.setFileName("c:/temp/test/3-rotate.tif");
result.writeImage( new ImageInfo( "c:/temp/test/3-rotate.tif" ) );
			}

			// filp
			if ( parms.isFlipHorizontal() ) {
				result = result.flopImage();
result.setFileName("c:/temp/test/4-flip.tif");
result.writeImage( new ImageInfo( "c:/temp/test/4-flip.tif" ) );
			}
			
			// crop
			int cropX = parms.getCropWidth();
			int cropY = parms.getCropHeight();
			Dimension d = result.getDimension();
			if ( ( cropX != d.width ) || ( cropY != d.height ) ) {
				int startX = (int) ( ( ( d.width - cropX ) + 1d ) / 2d );
				int startY = (int) ( ( ( d.height - cropY ) + 1d ) / 2d );
				result = result.chopImage( new Rectangle( startX, startY, cropX, cropY ) );
result.setFileName("c:/temp/test/5-crop.tif");
result.writeImage( new ImageInfo( "c:/temp/test/5-crop.tif" ) );
			}

			return result;
		}
		catch ( MagickException e ) {
			String msg = "error transforming MagickImage";
			logger.error( msg, e );
			throw new IllegalStateException( msg + ": " +  e );
		}
	}
}
