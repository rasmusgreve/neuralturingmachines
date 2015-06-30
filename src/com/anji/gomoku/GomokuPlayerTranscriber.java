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
 * Created on Jul 23, 2005 by Philip Tucker
 */
package com.anji.gomoku;

import org.jgap.Chromosome;

import com.anji.integration.Activator;
import com.anji.integration.ActivatorTranscriber;
import com.anji.integration.TranscriberException;
import com.anji.tournament.Player;
import com.anji.tournament.PlayerTranscriber;
import com.anji.ttt.BoardPlayer;
import com.anji.util.Configurable;
import com.anji.util.Properties;

/**
 * @author Philip Tucker
 */
public class GomokuPlayerTranscriber implements PlayerTranscriber, Configurable {

private final static String TYPE_KEY = "gomoku.player.type";

private final static String TYPE_DEFAULT = "default";

private final static String TYPE_SCANNING = "scannnig";

private ActivatorTranscriber activatorTranscriber;

private String type = TYPE_DEFAULT;

/**
 * default ctor
 */
public GomokuPlayerTranscriber() {
	super();
}

/**
 * @see com.anji.integration.Transcriber#transcribe(org.jgap.Chromosome)
 */
public Object transcribe( Chromosome c ) throws TranscriberException {
	return newPlayer( c );
}

/**
 * @see com.anji.tournament.PlayerTranscriber#newPlayer(org.jgap.Chromosome)
 */
public Player newPlayer( Chromosome c ) throws TranscriberException {
	return newBoardPlayer( c );
}

/**
 * @param c
 * @return new tic-tac-toe player
 * @throws TranscriberException
 */
public BoardPlayer newBoardPlayer( Chromosome c ) throws TranscriberException {
	Activator activator = activatorTranscriber.newActivator( c );

	if ( TYPE_SCANNING.equals( type ) )
		return new GomokuScanningNeuralNetPlayer( activator );

	return new GomokuNeuralNetPlayer( activator );
}

/**
 * @see com.anji.integration.Transcriber#getPhenotypeClass()
 */
public Class getPhenotypeClass() {
	return BoardPlayer.class;
}

/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties props ) throws Exception {
	activatorTranscriber = (ActivatorTranscriber) props
			.singletonObjectProperty( ActivatorTranscriber.class );
	type = props.getProperty( TYPE_KEY, TYPE_DEFAULT );
}

}
