/*
 * Copyright (C) 2005 Derek James and Philip Tucker
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
 * Created on May 16, 2005 by Philip Tucker
 */
package com.anji.roshambo;

import java.util.Random;

import org.jgap.Chromosome;

import com.anji.integration.ActivatorTranscriber;
import com.anji.integration.AnjiNetTranscriber;
import com.anji.integration.TranscriberException;
import com.anji.nn.AnjiNet;
import com.anji.tournament.Player;
import com.anji.tournament.PlayerTranscriber;
import com.anji.util.Configurable;
import com.anji.util.Properties;
import com.anji.util.Randomizer;

/**
 * Constructs <code>RoshamboPlayer</code> objects from <code>AniNet</code> objects
 * transcribed from <code>Chromosome</code> objects.
 * @author Philip Tucker
 */
public class RoshamboPlayerTranscriber implements PlayerTranscriber, Configurable {

/**
 * public visibility for test
 */
public final static String DETERMINISTIC_KEY = "roshambo.anji.deterministic";

/**
 * public visibility for test
 */
public final static String HISTORY_SIZE_KEY = "roshambo.anji.history.size";

/**
 * public visibility for test
 */
public final static String SCANNING_WINDOW_SIZE_KEY = "roshambo.anji.scanning.window.size";

private int historySize = 1;

private AnjiNetTranscriber anjiNetTranscriber;

private int recurrentCycles = 1;

private boolean isDeterministic = false;

private Randomizer randomizer;

private int scanningWindowSize = 0;

/**
 * default constructor - should be followed by call to <code>init()</code>
 */
public RoshamboPlayerTranscriber() {
	super();
}

/**
 * convenience method to return specific type
 * @param genotype
 * @return new <code>RoshamboPlayer</code> object
 * @throws TranscriberException
 */
public RoshamboPlayer newRoshamboPlayer( Chromosome genotype ) throws TranscriberException {
	AnjiNet net = anjiNetTranscriber.newAnjiNet( genotype );
	try {
		Random rand = isDeterministic ? null : randomizer.getRand();
		RoshamboPlayer result;
		if ( scanningWindowSize > 0 )
			result = new AnjiNetScanningRoshamboPlayer( net, rand, recurrentCycles, historySize,
					scanningWindowSize );
		else
			result = new AnjiNetRoshamboPlayer( net, rand, recurrentCycles, historySize );
		return result;
	}
	catch ( IllegalArgumentException e ) {
		throw new TranscriberException( "error transcribing " + genotype.getId(), e );
	}
}

/**
 * @see com.anji.tournament.PlayerTranscriber#newPlayer(org.jgap.Chromosome)
 * @see RoshamboPlayerTranscriber#newRoshamboPlayer(Chromosome)
 */
public Player newPlayer( Chromosome genotype ) throws TranscriberException {
	return newRoshamboPlayer( genotype );
}

/**
 * @see com.anji.integration.Transcriber#transcribe(org.jgap.Chromosome)
 * @see RoshamboPlayerTranscriber#newRoshamboPlayer(Chromosome)
 */
public Object transcribe( Chromosome genotype ) throws TranscriberException {
	return newRoshamboPlayer( genotype );
}

/**
 * @see com.anji.integration.Transcriber#getPhenotypeClass()
 */
public Class getPhenotypeClass() {
	return RoshamboPlayer.class;
}

/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties aProps ) throws Exception {
	anjiNetTranscriber = (AnjiNetTranscriber) aProps
			.singletonObjectProperty( AnjiNetTranscriber.class );
	isDeterministic = aProps.getBooleanProperty( DETERMINISTIC_KEY, false );
	randomizer = (Randomizer) aProps.singletonObjectProperty( Randomizer.class );
	recurrentCycles = aProps.getIntProperty( ActivatorTranscriber.RECURRENT_CYCLES_KEY, 1 );
	historySize = aProps.getIntProperty( HISTORY_SIZE_KEY, 1 );
	scanningWindowSize = aProps.getIntProperty( SCANNING_WINDOW_SIZE_KEY, 0 );
	if ( scanningWindowSize > historySize )
		throw new IllegalArgumentException( "scanningWindowSize " + scanningWindowSize
				+ " > history size " + historySize );
}

}
