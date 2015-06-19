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
 * created by Philip Tucker on Mar 9, 2003
 */
package com.anji.integration;

import org.jgap.Chromosome;

/**
 * To "transcribe" is to construct a phenotype from a genotype.
 * 
 * @author Philip Tucker
 */
public interface Transcriber {

/**
 * Sub-classes must implement this method to convert the genotype to a phenotype.
 * @param c chromosome to transcribe
 * @return phenotype
 * @throws TranscriberException
 */
public Object transcribe( Chromosome c ) throws TranscriberException;

/**
 * @return class of phenotype returned by <code>transcribe()</code>
 */
public Class getPhenotypeClass();
}
