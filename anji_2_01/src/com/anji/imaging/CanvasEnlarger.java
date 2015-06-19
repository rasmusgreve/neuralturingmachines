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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.anji.Copyright;

/**
 * class CanvasEnlarger
 */
public class CanvasEnlarger {

/**
 * @param args
 * @throws Exception
 */
public static void main( String[] args ) throws Exception {
	System.out.println( Copyright.STRING );
	CanvasEnlarger ce = new CanvasEnlarger();
	ce.transformFiles();
}

/**
 * @throws Exception
 */
public void transformFiles() throws Exception {
	// Read from a file
	File dir = new File( "images/original_matches/" );

	String s[] = dir.list();
	for ( int i = 0; i < s.length; i++ ) {
		Image image = null;
		Image bg = null;
		try {
			StringBuffer sb1 = new StringBuffer( s[ i ].length() );
			sb1.append( "images/original_matches/" );
			sb1.append( s[ i ] );
			System.out.println( "transforming: " + sb1.toString() );
			StringBuffer sb2 = new StringBuffer( s[ i ].length() );
			sb2.append( "images/matches/" );
			sb2.append( s[ i ] );
			String bgwhite = new String( "images/bgwhite.tif" );

			File file = new File( sb1.toString() );
			image = ImageIO.read( file );
			File file2 = new File( bgwhite );
			bg = ImageIO.read( file2 );
			BufferedImage bi = new BufferedImage( ( image.getWidth( null ) * 2 ), ( image
					.getHeight( null ) * 2 ), BufferedImage.TYPE_INT_RGB );

			Graphics g = bi.createGraphics();
			g.drawImage( bg, 0, 0, null );
			g
					.drawImage( image, ( image.getWidth( null ) / 2 ), ( image.getHeight( null ) / 2 ),
							null );
			g.dispose();

			File fileOut = new File( sb2.toString() );

			ImageIO.write( bi, "tif", fileOut );
		}
		catch ( IOException e ) {
			throw e;
		}
	}

}
}
