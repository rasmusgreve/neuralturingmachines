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
 * Created on Aug 21, 2005 by Philip Tucker
 */
package com.anji.imaging;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Philip Tucker
 */
public class ImageFileFilter implements FileFilter {

private ImageFileFilter() {
	super();
}

private static ImageFileFilter instance = null;

/**
 * @return singleton
 */
public static ImageFileFilter getInstance() {
	if ( instance == null )
		instance = new ImageFileFilter();
	return instance;
}

/**
 * @see java.io.FileFilter#accept(java.io.File)
 */
public boolean accept( File aPathname ) {
	return aPathname.isFile();
}

}
