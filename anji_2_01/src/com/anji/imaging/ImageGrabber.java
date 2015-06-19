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

package com.anji.imaging;

/**
 * @author Derek James
 */
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.anji.Copyright;

/**
 * ImageGrabber
 */
class ImageGrabber {

/**
 * @param args
 * @throws Exception
 */
public static void main( String[] args ) throws Exception {
	System.out.println( Copyright.STRING );

	Image image = null;
	try {
		// Read from a file
		File file = new File( "images/ocr/A_1.tif" );
		image = ImageIO.read( file );
		BufferedImage bi = (BufferedImage) image;

		// new BufferedImage(image.getWidth(null), image.getHeight(null),
		// BufferedImage.TYPE_INT_RGB);

		// Copy image to buffered image
		//            Graphics g = bi.createGraphics();
		//        
		//            // Paint the image onto the buffered image
		//            g.drawImage(image, 0, 0, null);
		//            g.dispose();

		AffineTransform at = new AffineTransform();
		at.rotate( Math.toRadians( 135 ), bi.getWidth() / 2, bi.getHeight() / 2 );
		AffineTransformOp op = new AffineTransformOp( at, AffineTransformOp.TYPE_BILINEAR );
		bi = op.filter( bi, null );
		File fileOut = new File( "images/ocr/output.tif" );
		ImageIO.write( bi, "tif", fileOut );

	}
	catch ( IOException e ) {
		throw e;
	}

	//        int[] pixels = new int[ 50*50 ];
	//        PixelGrabber pixelGrabber=new PixelGrabber(image, 0, 0, 50, 50, pixels, 0, 50 );
	//    
	//        // GRAB
	//        try
	//        {
	//          pixelGrabber.grabPixels();
	//        }
	//        catch (Exception e)
	//        {
	//          System.out.println("PixelGrabber exception");
	//        }

	BufferedImage bi = (BufferedImage) image;
	for ( int x = 0; x < 50; ++x ) {
		for ( int y = 0; y < 50; ++y )
			System.out.print( bi.getRGB( x, y ) + " " );
		System.out.println();
	}

	for ( int x = 0; x < 50; ++x ) {
		for ( int y = 0; y < 50; ++y )
			System.out.print( ( (double) ( bi.getRGB( x, y ) / ( 255 * 255 ) + 1 ) / -257 ) + " " );
		System.out.println();
	}

	//        for (int i = 0; i < 2500; i++) {
	//          System.out.print( pixels[i] + " ");
	//          if ( i % 100 == 99 )
	//          	System.out.println();
	//        }
	//
	//        for (int i = 0; i < 2500; i++) {
	//                System.out.print(((double)(pixels[i]/(255*255) + 1)/-257) + " ");
	//                if ( i % 100 == 99 )
	//                	System.out.println();
	//        }

}

}
