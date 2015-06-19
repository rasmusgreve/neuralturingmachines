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
 * Created on Jul 16, 2005 by Philip Tucker
 */
package com.anji.tournament;

import com.anji.util.Configurable;
import com.anji.util.Properties;

/**
 * @author Philip Tucker
 */
public class GameConfiguration implements Configurable {

/**
 * default configuration
 */
public final static GameConfiguration DEFAULT = new GameConfiguration();

private final static String LOG_KEY = "log";

private final static String RESET_KEY = "players.reset";

private boolean resetPlayers = true;

private boolean logResults = false;

/**
 * default ctor; call <code>init()</code> to initialize
 */
public GameConfiguration() {
	super();
}

/**
 * @param aResetPlayers
 * @param aLogResults
 */
public GameConfiguration( boolean aResetPlayers, boolean aLogResults ) {
	resetPlayers = aResetPlayers;
	logResults = aLogResults;
}

/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties props ) throws Exception {
	resetPlayers = props.getBooleanProperty( RESET_KEY, true );
	logResults = props.getBooleanProperty( LOG_KEY, false );
}

/**
 * @return reset players
 */
public boolean doResetPlayers() {
	return resetPlayers;
}

/**
 * @return log results
 */
public boolean doLogResults() {
	return logResults;
}
}
