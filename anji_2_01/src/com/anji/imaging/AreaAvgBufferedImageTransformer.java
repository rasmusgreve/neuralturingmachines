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

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * DefaultBufferedImageTransformer
 */
public class AreaAvgBufferedImageTransformer implements BufferedImageTransformer {

	private BufferedImage data;

	/**
	 * @see com.anji.imaging.BufferedImageTransformer#setImage(java.awt.image.BufferedImage)
	 */
	public void setImage( BufferedImage anImg ) {
		data = anImg;
	}

	/**
	 * @see com.anji.imaging.BufferedImageTransformer#transform(com.anji.imaging.TransformParameters)
	 */
	public Image transform( TransformParameters parms ) {
		// translate
		AffineTransform xform = AffineTransform.getTranslateInstance( parms.getTranslateX(), parms
				.getTranslateY() );

		// rotate
		int width = data.getWidth();
		int height = data.getHeight();
		double centerX = width / 2.0d;
		double centerY = height / 2.0d;
		if ( parms.getRotate() != 0.0d )
			xform.preConcatenate( AffineTransform.getRotateInstance( parms.getRotate(), centerX,
					centerY ) );

		// flip
		if ( parms.isFlipHorizontal() )
			xform.preConcatenate( new AffineTransform( -1.0, 0.0, 0.0, 1.0, width, 0.0 ) );

		// transform
		AffineTransformOp xformOp = new AffineTransformOp( xform,
				AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
		BufferedImage postXform = xformOp.filter( data, null );

		// crop
		int cropX = parms.getCropWidth();
		int cropY = parms.getCropHeight();
		int visibleSurfaceWidth = (int) ( ( cropX / parms.getScaleX() ) + 0.5d );
		int visibleSurfaceHeight = (int) ( ( cropY / parms.getScaleY() ) + 0.5d );
		BufferedImage postCrop = postXform.getSubimage(
				( ( width - visibleSurfaceWidth ) + 1 ) / 2,
				( ( height - visibleSurfaceHeight ) + 1 ) / 2, visibleSurfaceWidth,
				visibleSurfaceHeight );

		// scale and return
		if ( ( cropX == postCrop.getWidth() ) && ( cropY == postCrop.getHeight() ) )
			return postCrop;
		return postCrop.getScaledInstance( cropX, cropY, Image.SCALE_AREA_AVERAGING );
	}
}
