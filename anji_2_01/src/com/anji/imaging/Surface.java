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
 * Created on Sep 18, 2004 by Philip Tucker
 */
package com.anji.imaging;

import java.io.File;
import java.io.IOException;

import com.anji.util.Configurable;

/**
 * A Surface is a 2d array of int values. It is an abstraction of an image or game board
 * allowing the floating eye to access it without knowing details. Surface objects implementing
 * this interface should not be used until <code>setImage()</code> has been called.
 * <code>setImage()</code> should reset the image pixels, but not the size of the image, so
 * the same object can be used for multiple images. Size of surface is set via properties - if
 * size is not set via properties, defaults to size of first image set via
 * <code>setImage()</code>.
 */
public interface Surface extends Configurable {

/**
 * class to perform image transformation; subclass of BufferedImageTransformer
 */
public final static String TRANSFORMER_CLASS_KEY = "surface.transformer";

/**
 * final size of surface to which images will be scaled
 */
public final static String SURFACE_WIDTH_KEY = "surface.width";

/**
 * final size of surface to which images will be scaled
 */
public final static String SURFACE_HEIGHT_KEY = "surface.height";

/**
 * Set source data of image. The image file is scaled (using area averaging) to surface size.
 * 
 * @param file
 * @throws IOException
 */
public void setImage( File file ) throws IOException;

/**
 * transform image via translate, rotate, scale, crop
 * 
 * @param parms
 * @return transformed image flattened into 1D array
 */
public int[] transform( TransformParameters parms );

/**
 * @return source image
 */
public int[] getData();

/**
 * @return width of source image
 */
public int getWidth();

/**
 * @return height of source image
 */
public int getHeight();

/**
 * @param x
 * @param y
 * @return value of pixel at location x,y
 */
public int getValue( int x, int y );
}
