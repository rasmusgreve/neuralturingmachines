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
 * created by Philip Tucker on Dec 12, 2004
 */
package com.anji.imaging;

import java.io.File;
import java.io.IOException;

/**
 * Identifier. To use this class, create an instance via
 * <code>AnjiNetFloatingEyeIdentifierFactory</code>. Call <code>identify(File)</code> and
 * <code>getStepNum()</code> as often as necessary, and call <code>disopse()</code> when
 * finished.
 */
public interface Identifier {

/**
 * @param imgFile
 * @return confidence between 0 (certain it is not a match) and 1 (certain it is a match)
 * @throws IOException
 */
public float identify( File imgFile ) throws IOException;

/**
 * dispose of any remaining resources; identify must not be called again after dispose
 */
public void dispose();

/**
 * @return number of steps used by previous call to <code>identify(File)</code>
 */
public int getStepNum();

/**
 * @return indicates relative cost in resources of this identifier
 */
public long cost();
}
