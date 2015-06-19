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
 * created by Derek James on Jul 6, 2004
 */

package com.anji.imaging;

/**
 * @author Derek James
 */

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author Philip Tucker
 */
public class ImageNormalizer {

private File origDir;

private File resultDir;

/**
 * @param anOrigDir
 * @param aResultDir
 */
public ImageNormalizer( File anOrigDir, File aResultDir ) {
	origDir = anOrigDir;
	resultDir = aResultDir;
}

/**
 * @param resultDim
 * @return int number of image files normalized
 * @throws IOException
 */
public int normalize( Dimension resultDim ) throws IOException {
	int result = 0;
	DefaultBufferedImageTransformer xformer = new DefaultBufferedImageTransformer();

	// loop through files from origin directory
	File[] files = origDir.listFiles( ImageFileFilter.getInstance() );
	for ( int i = 0; i < files.length; ++i ) {
		File f = files[ i ];
		boolean isValidImgFIle = false;
		BufferedImage origImg = null;

		// make sure image file is valid
		if ( f.isFile() ) {
			try {
				origImg = ImageIO.read( f );
				System.out.println( "read " + f.getAbsolutePath() );
				isValidImgFIle = ( origImg != null );
			}
			catch ( Throwable t ) {
				System.err.println( "error reading file: " + f.getAbsolutePath() + ": " + t.toString() );
			}
		}

		if ( isValidImgFIle ) {
			// read image into transformer
			xformer.setImage( origImg );

			// determine scale factor - scale minimumum of height and width so that it fits in
			// result rectangle
			double scaleX = resultDim.getWidth() / origImg.getWidth();
			double scaleY = resultDim.getHeight() / origImg.getHeight();
			double scaleFactor = Math.max( scaleX, scaleY );

			// scale image
			TransformParameters parms = new TransformParameters( 0, 0, 0d, scaleFactor, scaleFactor,
					(int) resultDim.getWidth(), (int) resultDim.getHeight() );
			Image scaledImg = xformer.transform( parms );

			// write scaled image into result
			BufferedImage resultImg = new BufferedImage( (int) resultDim.getWidth(), (int) resultDim
					.getHeight(), BufferedImage.TYPE_INT_ARGB );
			Graphics2D g = resultImg.createGraphics();
			g.drawImage( scaledImg, new AffineTransform(), null );
			g.dispose();

			// write result file to result directory with same file name as original
			ImageIO.write( resultImg, "TIF", new File( resultDir.getAbsolutePath()
					+ File.separatorChar + f.getName() ) );
			++result;
		}
		else
			System.err.println( "not an image file: " + f.getAbsolutePath() );
	}

	return result;
}
}
