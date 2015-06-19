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
 * created by Philip Tucker on Jun 9, 2004
 */

package com.anji.floatingeye;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.List;

import org.apache.log4j.Logger;

import com.anji.imaging.DoubleLocation2D;
import com.anji.imaging.IntLocation2D;
import com.anji.imaging.RangeTranslator;
import com.anji.imaging.RangeTranslatorFactory;
import com.anji.imaging.Surface;
import com.anji.imaging.TransformParameters;
import com.anji.nn.Neuron;
import com.anji.util.XmlPersistable;

/**
 * TODO - non square surface & non square eyes
 * 
 * Implements a floating eye that integrates with an <code>AnjiNet</code> ANN. Foating eye
 * manages a 2-d matrix of short values known as a "surface" (this could be a graphical image or
 * a game board), the loaction of the eye in x,y,z coordinates. x,y are the location on the
 * surface, z is the location above or below the surface. An "eye image" is managed - a 2-d
 * matrix of short values corresponding to pixels in the eye. The zoom factor of the eye is
 * determined by the z value. At z=0, each pixel of the eye corresponds to a single space on the
 * surface. At z=1 each eye pixel can see a maxBlurFactor by maxBlurFactor square. The eye image
 * is centered on the x,y coordinates (off center by 1/2 pixel if the eye is at a zoom factor
 * where the visual field spans an even number spaces).
 * 
 * @author Philip Tucker
 */
public class FloatingEye implements XmlPersistable {

private static Logger logger = Logger.getLogger( FloatingEye.class );

private final static String XML_TAG = "eye";

private int stepNum;

private String name;

private EyeMovementParms movementParms;

//
// representation begin
//

// eye location in x,y,z coordinates, real values between -1 (west, south, zoomed out
// inverted) and 1 (east, north, zoomed out)
// together eyeLocation and eyeRotation comprise all the data necessary to transform
// surface into eyeImage
// package visibility because Location[X, Y, Z]Connection acces it
FloatingLocation eyeLocation = new FloatingLocation();

// value between -1 ... 1, translated to radians between 0 and 2*PI
// package visibility because ThetaConnection accesses it
double eyeRotation;

boolean eyeLocationXDirty;

boolean eyeLocationYDirty;

boolean eyeLocationZDirty;

boolean flipDirty;

private boolean eyeRotationDirty;

private Surface surface;

private boolean surfaceDirty;

//
// representation end
//

// dimensions eye_width x eye_height
// package visibility because EyePixelConnection accesses it
int[] eyePixels;

private int eyeWidth;

private int eyeHeight;

private Neuron eyeLocationXControlNeuron;

private Neuron eyeLocationYControlNeuron;

private Neuron eyeLocationZControlNeuron;

private Neuron eyeRotationControlNeuron;

// translates from control neuron output to representaiton values (-1...1)
private RangeTranslator eyeLocationXControlTranslator;

private RangeTranslator eyeLocationYControlTranslator;

private RangeTranslator eyeLocationZControlTranslator;

// translates from control neuron output to representaiton values (-1...1)
private RangeTranslator eyeRotationControlTranslator;

private RangeTranslator eyeRotationInputToRadiansTranslator;

/**
 * A floating eye is a 2-d window of inputs to a neural net that can zoom in and out relative to
 * a 2-d surface. Assumes first 3 inputs to net are x,y,z coordinates, and first 5 outputs are
 * move X, move Y, move Z, rotate, and affinity (i.e., how much the network "likes" the current
 * position over which it is centered). All other inputs correspond to the inputs to the eye, a
 * 2-d metrix squashed into a 1-d array. <code>AnjiNet</code> is not thread safe, so the
 * caller should not access AnjiNet after passing it to <code>FloatingEye</code>.
 * 
 * @param aName identifying string
 * @param controlNeurons list of neurons, must be at least 4 (x, y, z, rotate) in general, but
 * any of them for which the corresponding maxMovePerStep parameter is 0 are not required
 * @param aSurface 2-d matrix of ints
 * @param anEyeDim
 * @param someMovementParms paramaters controlling movement of eye
 */
public FloatingEye( String aName, List controlNeurons, Surface aSurface, int anEyeDim,
		EyeMovementParms someMovementParms ) {
	if ( anEyeDim < 1 )
		throw new IllegalArgumentException( "eye dimensions must be >= 1: " + anEyeDim );

	// 4 control neurons for x,y,z, theta; only require the ones for which maxMovePerStep > 0
	int controlNeuronCount = 0;
	movementParms = someMovementParms;
	if ( movementParms.getMaxXMovePerStep() > 0d )
		++controlNeuronCount;
	if ( movementParms.getMaxYMovePerStep() > 0d )
		++controlNeuronCount;
	if ( movementParms.getMaxZMovePerStep() > 0d )
		++controlNeuronCount;
	if ( movementParms.getMaxThetaMovePerStep() > 0d )
		++controlNeuronCount;
	if ( controlNeurons.size() < controlNeuronCount )
		throw new IllegalArgumentException( "# control neurons must be >= 4: "
				+ controlNeurons.size() );

	// eyeImage = new BufferedImage( anEyeDim, anEyeDim, BufferedImage.TYPE_INT_ARGB );
	eyeWidth = anEyeDim;
	eyeHeight = anEyeDim;
	eyePixels = new int[ eyeWidth * eyeHeight ];
	name = aName;
	surface = aSurface;
	surfaceDirty = true;

	// transform operators
	eyeRotationInputToRadiansTranslator = RangeTranslatorFactory.getInstance().getTranslator( -1,
			1, -Math.PI, Math.PI );

	connectToControlNeurons( controlNeurons );
	reset();
}

private boolean isFlipped() {
	return movementParms.isFlipEnabled() && ( eyeLocation.z < 0.0d );
}

/**
 * @return <code>String</code> XML representation of object
 */
public synchronized String toXml() {
	StringBuffer result = new StringBuffer();
	result.append( "<" ).append( XML_TAG ).append( ">\n\t<position x=\"" );
	synchronized ( this ) {
		result.append( eyeLocation.x );
		result.append( "\" y=\"" ).append( eyeLocation.y );
		result.append( "\" z=\"" ).append( eyeLocation.z );
		result.append( "\" />\n\t<eye-image>\n" );
		for ( int x = 0; x < eyeWidth; ++x ) {
			for ( int y = 0; y < eyeHeight; ++y ) {
				result.append( "\t\t<pixel x=\"" ).append( x );
				result.append( "\" y=\"" ).append( y ).append( "\">" );
				result.append( "</pixel>\n" );
			}
		}
	}
	result.append( "\t</eye-image>\n</" ).append( XML_TAG ).append( ">\n" );
	return result.toString();
}

/**
 * @see Object#toString()
 */
public String toString() {
	StringBuffer result = new StringBuffer();
	result.append( name ).append( ": " ).append( eyeLocation.toString() ).append( ": theta=" )
			.append( DoubleLocation2D.TO_STRING_FORMAT.format( getEyeDirectionRadians() ) );
	return result.toString();
}

/**
 * reset neural net and state of eye
 */
public synchronized void reset() {
	//		updatePosition();
	eyeLocation.x = 0.0d;
	eyeLocation.y = 0.0d;
	eyeLocation.z = 1.0d - movementParms.getStartZoom();
	eyeRotation = 0.0d;

	eyeLocationXDirty = true;
	eyeLocationYDirty = true;
	eyeLocationZDirty = true;
	flipDirty = true;
	eyeRotationDirty = true;

	stepNum = 0;
}

private void connectToControlNeurons( List controlNeurons ) {
	// range translator for output neurons
	RangeTranslatorFactory factory = RangeTranslatorFactory.getInstance();

	// output neurons and eyeRotation parameters
	int outputIdx = 0;
	if ( movementParms.getMaxXMovePerStep() > 0d ) {
		eyeLocationXControlNeuron = (Neuron) controlNeurons.get( outputIdx++ );
		eyeLocationXControlTranslator = factory.getTranslator( eyeLocationXControlNeuron.getFunc()
				.getMinValue(), eyeLocationXControlNeuron.getFunc().getMaxValue(), -1, 1 );
	}
	if ( movementParms.getMaxYMovePerStep() > 0d ) {
		eyeLocationYControlNeuron = (Neuron) controlNeurons.get( outputIdx++ );
		eyeLocationYControlTranslator = factory.getTranslator( eyeLocationYControlNeuron.getFunc()
				.getMinValue(), eyeLocationYControlNeuron.getFunc().getMaxValue(), -1, 1 );
	}
	if ( movementParms.getMaxZMovePerStep() > 0d ) {
		eyeLocationZControlNeuron = (Neuron) controlNeurons.get( outputIdx++ );
		eyeLocationZControlTranslator = factory.getTranslator( eyeLocationZControlNeuron.getFunc()
				.getMinValue(), eyeLocationZControlNeuron.getFunc().getMaxValue(), -1, 1 );
	}
	if ( movementParms.getMaxThetaMovePerStep() > 0d ) {
		eyeRotationControlNeuron = (Neuron) controlNeurons.get( outputIdx++ );
		eyeRotationControlTranslator = factory.getTranslator( eyeRotationControlNeuron.getFunc()
				.getMinValue(), eyeRotationControlNeuron.getFunc().getMaxValue(), -1, 1 );
	}

	if ( controlNeurons.size() != outputIdx )
		logger.warn( name + ": FloatingEye did not use all outputs" );
}

/**
 * @return zoom factor between 0 (exclusive) and 1 (inclusive)
 */
public synchronized double getZoom() {
	return Math.max( 1 - Math.abs( eyeLocation.z ), movementParms.getMinZoom() );
}

/**
 * @return width of eye in pixels
 */
public int getWidth() {
	return eyeWidth;
}

/**
 * @return height of eye in pixels
 */
public int getHeight() {
	return eyeHeight;
}

/**
 * translates eye location values from representation range (-1.0 ... 1.0 double) to surface
 * range (0 ... width/length int)
 * 
 * @return surface location of eye center in x,y coordinates
 */
public synchronized IntLocation2D getSurfaceLocation() {
	int width = surface.getWidth();
	int height = surface.getHeight();
	IntLocation2D result = new IntLocation2D();
	result.x = Math.min( (int) ( ( ( eyeLocation.x + 1.0d ) / 2.0d ) * width ), width - 1 );
	result.y = Math.min( (int) ( ( ( eyeLocation.y + 1.0d ) / 2.0d ) * height ), height - 1 );
	return result;
}

private synchronized boolean isEyeImageDirty() {
	return ( surfaceDirty || eyeLocationXDirty || eyeLocationYDirty || eyeLocationZDirty || eyeRotationDirty );
}

private synchronized void setEyeImageClean() {
	surfaceDirty = false;
	eyeLocationXDirty = false;
	eyeLocationYDirty = false;
	eyeLocationZDirty = false;
	eyeRotationDirty = false;
	flipDirty = false;
}

private synchronized void loadEyeImage() {
	if ( isEyeImageDirty() ) {
		try {
			IntLocation2D surfaceLocation = getSurfaceLocation();
			double zoom = getZoom();
			int xCenter = surface.getWidth() / 2;
			int yCenter = surface.getHeight() / 2;
			TransformParameters parms = new TransformParameters( xCenter - surfaceLocation.x, yCenter
					- surfaceLocation.y, -eyeRotationInputToRadiansTranslator.translate( eyeRotation ),
					zoom, zoom, eyeWidth, eyeHeight, isFlipped() );
			eyePixels = surface.transform( parms );

			setEyeImageClean();
		}
		catch ( RasterFormatException e ) {
			logger.error( "error transforming eye: " + toString(), e );
			throw e;
		}
	}
}

//	private void debugPrint( BufferedImage img ) {
//		BufferedImage fullSurface = surface.getAbsoluteData();
//		System.out.println( "Full Java2DSurface [" + fullSurface.getWidth() + ","
//				+ fullSurface.getHeight() + "]" );
//		for ( int y = 0; y < fullSurface.getHeight(); ++y ) {
//			for ( int x = 0; x < fullSurface.getWidth(); ++x )
//				System.out.print( Format.sprintf( "%8x ", new Object[] { new Integer(
// fullSurface
//						.getRGB( x, y ) ) } ) );
//			System.out.println();
//		}
//
//		System.out.println( "Java2DSurface [" + surface.getWidth() + "," +
// surface.getHeight() + "]" );
//		for ( int y = 0; y < surface.getHeight(); ++y ) {
//			for ( int x = 0; x < surface.getWidth(); ++x )
//				System.out.print( Format.sprintf( "%8x ", new Object[] { new Integer(
// surface.getValue(
//						x, y ) ) } ) );
//			System.out.println();
//		}
//
//		System.out.println( "Image [" + img.getWidth() + "," + img.getHeight() +
// "]" );
//		for ( int y = 0; y < img.getHeight(); ++y ) {
//			for ( int x = 0; x < img.getWidth(); ++x )
//				System.out.print( Format.sprintf( "%8x ",
//						new Object[] { new Integer( img.getRGB( x, y ) ) } ) );
//			System.out.println();
//		}
//
//		System.out.println( "Eye [" + eyeImage.getWidth() + "," +
// eyeImage.getHeight() + "]" );
//		for ( int y = 0; y < eyeImage.getHeight(); ++y ) {
//			for ( int x = 0; x < eyeImage.getWidth(); ++x ) {
//				int rgb = eyeImage.getRGB( x, y );
//				System.out.print( Format.sprintf( "%8x ", new Object[] { new Integer( rgb )
// } ) );
//			}
//			System.out.println();
//		}
//	}

private double nextLocation( double currentLocation, double rawControlValue,
		RangeTranslator rangeXlator, double maxMovePerStep ) {
	// translate from activation function value to -1 .. 1
	double controlValue = rangeXlator.translate( rawControlValue );

	// determine movement amount relative to maxMovePerStep
	double newLocation = currentLocation + ( controlValue * maxMovePerStep );

	// clip at -1 and 1
	return Math.min( Math.max( newLocation, -1.0d ), 1.0d );
}

/**
 * TODO - set dirty only if positions are different enough to make a difference
 * 
 * "position" is combination of <code>eyeLocation</code> and <code>eyeRotation</code>
 */
private synchronized void updatePosition() {
	double lastX = eyeLocation.x;
	double lastY = eyeLocation.y;
	double lastZ = eyeLocation.z;
	double lastDirection = eyeRotation;

	if ( movementParms.getMaxXMovePerStep() > 0d )
		eyeLocation.x = nextLocation( eyeLocation.x, eyeLocationXControlNeuron.getValue(),
				eyeLocationXControlTranslator, movementParms.getMaxXMovePerStep() );
	if ( movementParms.getMaxYMovePerStep() > 0d )
		eyeLocation.y = nextLocation( eyeLocation.y, eyeLocationYControlNeuron.getValue(),
				eyeLocationYControlTranslator, movementParms.getMaxYMovePerStep() );
	if ( movementParms.getMaxZMovePerStep() > 0d )
		eyeLocation.z = nextLocation( eyeLocation.z, eyeLocationZControlNeuron.getValue(),
				eyeLocationZControlTranslator, movementParms.getMaxZMovePerStep() );
	if ( !movementParms.isFlipEnabled() )
		// eyeLocation.z = Math.abs( eyeLocation.z );
		eyeLocation.z = Math.max( eyeLocation.z, 0.0d );

	if ( movementParms.getMaxThetaMovePerStep() > 0d )
		eyeRotation = nextLocation( eyeRotation, eyeRotationControlNeuron.getValue(),
				eyeRotationControlTranslator, movementParms.getMaxThetaMovePerStep() );

	eyeLocationZDirty = ( eyeLocation.z != lastZ );
	eyeLocationXDirty = eyeLocationZDirty || ( eyeLocation.x != lastX );
	eyeLocationYDirty = eyeLocationZDirty || ( eyeLocation.y != lastY );
	eyeRotationDirty = ( eyeRotation != lastDirection );
	flipDirty = ( ( eyeLocation.z >= 0 && lastZ < 0 ) || ( eyeLocation.z < 0 && lastZ >= 0 ) );
}

/**
 * @param count number of times to step
 * @see FloatingEye#step()
 */
public void step( int count ) {
	for ( int i = 0; i < count; ++i )
		step();
}

/**
 * load eye, step network, and update position from network output
 */
public void step() {
	++stepNum;
	loadEyeImage();
	updatePosition();
}

/**
 * package visibility because <code>FloatingEyeSurfaceListener</code> accesses it
 * 
 * @param aSurfaceDirty
 */
synchronized void setSurfaceDirty( boolean aSurfaceDirty ) {
	surfaceDirty = aSurfaceDirty;
}

/**
 * @return eye location values between -1.0d and 1.0d
 */
public synchronized FloatingLocation getEyeLocation() {
	return eyeLocation;
}

/**
 * @return eye direction between -1 and 1
 */
public synchronized double getEyeDirectionRadians() {
	return eyeRotationInputToRadiansTranslator.translate( eyeRotation );
}

/**
 * @return eye image
 */
public synchronized Image getEyeImage() {
	BufferedImage img = new BufferedImage( eyeWidth, eyeHeight, BufferedImage.TYPE_INT_ARGB );
	for ( int x = 0; x < eyeWidth; ++x )
		for ( int y = 0; y < eyeHeight; ++y )
			img.setRGB( x, y, getEyePixel( x, y ) );
	return img;
}

/**
 * @return number of steps this eye has performed
 */
public synchronized int getStepNum() {
	return stepNum;
}

/**
 * @param x
 * @param y
 * @return pixel value at x,y
 */
public synchronized int getEyePixel( int x, int y ) {
	return eyePixels[ x + ( y * eyeWidth ) ];
}

/**
 * @see com.anji.util.XmlPersistable#getXmlRootTag()
 */
public String getXmlRootTag() {
	return XML_TAG;
}

/**
 * @see com.anji.util.XmlPersistable#getXmld()
 */
public String getXmld() {
	return name;
}

//	/**
//	 * set running flag to start
//	 */
//	protected synchronized void startRunning() {
//		running = true;
//	}
//	
//	/**
//	 * set running flag to stop
//	 */
//	public synchronized void stopRunning() {
//		running = false;
//	}
//
//	/**
//	 * @return true iff thread is actively running
//	 */
//	protected synchronized boolean isRunning() {
//		return running;
//	}
}
