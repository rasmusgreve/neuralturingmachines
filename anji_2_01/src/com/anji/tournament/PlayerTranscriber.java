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
 * Created on April 30, 2004 by Philip Tucker
 */
package com.anji.tournament;

import org.jgap.Chromosome;

import com.anji.integration.Transcriber;
import com.anji.integration.TranscriberException;

/**
 * @author Philip Tucker
 */
public interface PlayerTranscriber extends Transcriber {

/**
 * @param c chromosome to transcribe
 * @return <code>Player</code> phenotype
 * @throws TranscriberException
 */
public Player newPlayer( Chromosome c ) throws TranscriberException;
}
