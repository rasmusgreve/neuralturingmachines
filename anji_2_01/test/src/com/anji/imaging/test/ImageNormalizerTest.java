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
 * created by Philip Tucker on Nov 20, 2004
 */

package com.anji.imaging.test;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import junit.framework.TestCase;

import com.anji.imaging.ImageFileFilter;
import com.anji.imaging.ImageNormalizer;

/**
 * @author Philip Tucker
 */
public class ImageNormalizerTest extends TestCase {

private final static String ORIG_DIR = "test/images";

private final static String RESULT_DIR = "test/images/image-normalizer";

private File origDir;

private File resultDir;

private Set validImageFileNames = new HashSet();

/**
 * ctor
 */
public ImageNormalizerTest() {
	super( ImageNormalizerTest.class.toString() );
}

/**
 * ctor
 * 
 * @param arg0
 */
public ImageNormalizerTest( String arg0 ) {
	super( arg0 );
}

/**
 * @see junit.framework.TestCase#setUp()
 */
protected void setUp() throws Exception {
	// read origin directory and save valid files in origFiles
	origDir = new File( ORIG_DIR );
	if ( origDir.isDirectory() == false )
		throw new IllegalStateException( ORIG_DIR + " is not a directory" );
	File[] allFiles = origDir.listFiles( ImageFileFilter.getInstance() );
	for ( int i = 0; i < allFiles.length; ++i ) {
		File f = allFiles[ i ];
		try {
			if ( ImageIO.read( f ) != null )
				validImageFileNames.add( f.getName() );
		}
		catch ( Throwable t ) {
			// skip
		}
	}

	// create result dir
	resultDir = new File( RESULT_DIR );
	resultDir.mkdir();
	if ( resultDir.isDirectory() == false )
		throw new IllegalStateException( RESULT_DIR + " is not a directory" );
}

/**
 * @see junit.framework.TestCase#tearDown()
 */
protected void tearDown() throws Exception {
	resultDir.delete();
}

/**
 * @throws Exception
 */
public void testNormalizerSmallTall() throws Exception {
	doTestImageNormalizer( 50, 100 );
}

/**
 * @throws Exception
 */
public void testNormalizerSmallWide() throws Exception {
	doTestImageNormalizer( 100, 50 );
}

/**
 * @throws Exception
 */
public void testNormalizerSmall() throws Exception {
	doTestImageNormalizer( 100, 100 );
}

/**
 * @throws Exception
 */
public void testNormalizerBigTall() throws Exception {
	doTestImageNormalizer( 500, 1000 );
}

/**
 * @throws Exception
 */
public void testNormalizerBigWide() throws Exception {
	doTestImageNormalizer( 1000, 500 );
}

/**
 * @throws Exception
 */
public void testNormalizerBig() throws Exception {
	doTestImageNormalizer( 1000, 1000 );
}

/**
 * @param resultWidth
 * @param resultHeight
 * @throws Exception
 */
protected void doTestImageNormalizer( int resultWidth, int resultHeight ) throws Exception {
	// normailze images
	ImageNormalizer uut = new ImageNormalizer( origDir, resultDir );
	Dimension resultDim = new Dimension( resultWidth, resultHeight );
	uut.normalize( resultDim );

	// read result files
	File[] resultFiles = resultDir.listFiles( ImageFileFilter.getInstance() );
	for ( int i = 0; i < resultFiles.length; ++i ) {
		File resultFile = resultFiles[ i ];
		BufferedImage bi = ImageIO.read( resultFile );
		assertTrue( "where did this file come from: " + resultFile.getName(), validImageFileNames
				.contains( resultFile.getName() ) );
		assertEquals( "wrong width: " + resultFile.getName(), resultWidth, bi.getWidth() );
		assertEquals( "wrong height: " + resultFile.getName(), resultHeight, bi.getHeight() );
	}
}
}
