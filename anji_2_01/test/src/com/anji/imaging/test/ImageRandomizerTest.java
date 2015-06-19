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
 * Created on Sep 9, 2004 by Philip Tucker
 */
package com.anji.imaging.test;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.anji.imaging.IdentifyImageFitnessFunction;
import com.anji.imaging.ImageFileFilter;
import com.anji.imaging.ImageRandomizer;
import com.anji.util.Properties;

/**
 * @author default
 */
public class ImageRandomizerTest extends TestCase {

private final static Logger logger = Logger.getLogger( ImageRandomizerTest.class );

private Properties props = new Properties();

private File matchDir;

private File mismatchDir;

private int matchImageCount;

private int mismatchImageCount;

/**
 * ctor
 */
public ImageRandomizerTest() {
	this( ImageRandomizerTest.class.toString() );
}

/**
 * ctor
 * 
 * @param name
 */
public ImageRandomizerTest( String name ) {
	super( name );
}

/**
 * @see junit.framework.TestCase#setUp()
 */
protected void setUp() throws Exception {
	props.loadFromResource( "test_eye.properties" );
	matchDir = props.getDirProperty( IdentifyImageFitnessFunction.IMG_MATCH_DIR_KEY );
	mismatchDir = props.getDirProperty( IdentifyImageFitnessFunction.IMG_MISMATCH_DIR_KEY );
	matchImageCount = props.getIntProperty( ImageRandomizer.IMG_MATCH_COUNT_KEY );
	mismatchImageCount = props.getIntProperty( ImageRandomizer.IMG_MISMATCH_COUNT_KEY );
}

/**
 * test ImageRandomizer
 * @throws Exception
 */
public void testIt() throws Exception {
	for ( int step = 0; step < 1; ++step ) {
		ImageRandomizer uut = new ImageRandomizer();
		uut.init( props );
		uut.transformFiles( matchDir, mismatchDir );

		// match files
		File[] files = matchDir.listFiles( ImageFileFilter.getInstance() );
		assertEquals( "wrong # match files", matchImageCount, files.length );
		int width = 0;
		int height = 0;
		for ( int i = 0; i < files.length; ++i ) {
			BufferedImage bi = ImageIO.read( files[ i ] );
			if ( i == 0 ) {
				width = bi.getWidth();
				height = bi.getHeight();
			}
			else {
				assertEquals( "wrong width", width, bi.getWidth() );
				assertEquals( "wrong height", height, bi.getHeight() );
			}
		}

		// mismatch files
		files = mismatchDir.listFiles( ImageFileFilter.getInstance() );
		assertEquals( "wrong # mismatch files", mismatchImageCount, files.length );
		for ( int i = 0; i < files.length; ++i ) {
			BufferedImage bi = ImageIO.read( files[ i ] );
			if ( i == 0 ) {
				width = bi.getWidth();
				height = bi.getHeight();
			}
			else {
				assertEquals( "wrong width", width, bi.getWidth() );
				assertEquals( "wrong height", height, bi.getHeight() );
			}
		}

		logger.info( "completed step " + step );
	}
}
}
