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
import java.awt.Graphics;
import java.awt.Image;

/**
 * @author Philip Tucker
 */
public class EyeCanvas extends Canvas {

	private FloatingEye canvasEye;

	/**
	 * @param anEye
	 * @param canvasWidth
	 * @param canvasHeight
	 */
	public EyeCanvas( FloatingEye anEye, int canvasWidth, int canvasHeight ) {
		canvasEye = anEye;
		setSize( canvasWidth, canvasHeight );
	}

	//	private static void print( String label, BufferedImage img ) {
	//		System.out.println( label );
	//		for ( int x = 0; x < img.getWidth(); ++x ) {
	//			for ( int y = 0; y < img.getHeight(); ++y ) {
	//				double val =img.getRGB( x, y );
	//				System.out.print( (int) ( ( val / -65794.0d ) * 2 ) );
	//			}
	//			System.out.println();
	//		}
	//	}

	/**
	 * @param g
	 */
	public void paint( Graphics g ) {
		g.drawImage( canvasEye.getEyeImage().getScaledInstance( getWidth(), getHeight(),
				Image.SCALE_FAST ), 0, 0, null );
	}
}
