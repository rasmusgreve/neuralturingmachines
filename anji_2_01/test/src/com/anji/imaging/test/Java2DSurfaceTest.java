/*
 * Created on Sep 19, 2004 by Philip Tucker
 */
package com.anji.imaging.test;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import junit.framework.TestCase;

import com.anji.imaging.Java2DSurface;
import com.anji.imaging.Surface;
import com.anji.util.Properties;

/**
 * Java2DSurfaceTest
 */
public class Java2DSurfaceTest extends TestCase {

private final static String PROP_PATH = "test_eye.properties";

private final static File SMALL_IMG_FILE = new File( "test/images/test_sm.jpg" );

private final static File MEDIUM_IMG_FILE = new File( "test/images/test.jpg" );

private final static File LARGE_IMG_FILE = new File( "test/images/test_lg.jpg" );

private final static BufferedImage SMALL_IMG = readImage( SMALL_IMG_FILE );

private final static BufferedImage MEDIUM_IMG = readImage( MEDIUM_IMG_FILE );

private final static BufferedImage LARGE_IMG = readImage( LARGE_IMG_FILE );

private static BufferedImage readImage( File f ) {
	try {
		return ImageIO.read( f );
	}
	catch ( IOException e ) {
		String msg = "error reading " + f.getAbsolutePath();
		System.err.println( msg );
		throw new IllegalArgumentException( msg );
	}
}

private Dimension surfaceDimension;

private Properties props = new Properties();

/**
 * ctor
 */
public Java2DSurfaceTest() {
	super( Java2DSurfaceTest.class.toString() );
}

/**
 * ctor
 * 
 * @param arg0
 */
public Java2DSurfaceTest( String arg0 ) {
	super( arg0 );
}

/**
 * @see junit.framework.TestCase#setUp()
 */
protected void setUp() throws Exception {
	props.loadFromResource( PROP_PATH );
	surfaceDimension = new Dimension( props.getIntProperty( Surface.SURFACE_WIDTH_KEY ), props
			.getIntProperty( Surface.SURFACE_HEIGHT_KEY ) );
	assertEquals( "wrong width for medium test image", (int) surfaceDimension.getWidth(),
			MEDIUM_IMG.getWidth() );
	assertEquals( "wrong height for medium test image", (int) surfaceDimension.getHeight(),
			MEDIUM_IMG.getHeight() );
}

private void assertSurfaceCorrect( String msgPrefix, Surface uut ) throws Exception {
	assertEquals( msgPrefix + "wrong width", (int) surfaceDimension.getWidth(), uut.getWidth() );
	assertEquals( msgPrefix + "wrong height", (int) surfaceDimension.getHeight(), uut.getHeight() );
	int[] pixels = uut.getData();
	assertEquals( msgPrefix + "wrong # pixels", (int) surfaceDimension.getWidth()
			* (int) surfaceDimension.getHeight(), pixels.length );
}

/**
 * tets with size set in properties
 * 
 * @throws Exception
 */
public void testSizeFromProps() throws Exception {
	Java2DSurface uut = new Java2DSurface();
	uut.init( props );
	doTestSurface( uut );
}

/**
 * tets with size set via first image
 * 
 * @throws Exception
 */
public void testSizeFromFirstImg() throws Exception {
	doTestSurface( new Java2DSurface() );
}

/**
 * 
 * @throws Exception
 */
private void doTestSurface( Java2DSurface uut ) throws Exception {
	// set from file
	uut.setImage( MEDIUM_IMG_FILE );
	assertSurfaceCorrect( "from file: ", uut );

	// set from image
	uut.setImage( MEDIUM_IMG );
	assertSurfaceCorrect( "from image: ", uut );

	// set from small image and file
	uut.setImage( SMALL_IMG_FILE );
	assertSurfaceCorrect( "from small file: ", uut );
	uut.setImage( SMALL_IMG );
	assertSurfaceCorrect( "from small image: ", uut );

	// set big from image and file
	uut.setImage( LARGE_IMG_FILE );
	assertSurfaceCorrect( "from large file: ", uut );
	uut.setImage( LARGE_IMG );
	assertSurfaceCorrect( "from large image: ", uut );
}
}
