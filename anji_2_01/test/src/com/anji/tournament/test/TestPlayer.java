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
 * Created on Jul 19, 2005 by Philip Tucker
 */
package com.anji.tournament.test;

import com.anji.tournament.Player;

/**
 * @author Philip Tucker
 */
public class TestPlayer implements Player {

private int resetCount;

private String name;

private int playCount;

/**
 * play game
 */
public void play() {
	++playCount;
}

/**
 * ctor
 * @param aName
 */
public TestPlayer( String aName ) {
	super();
	name = aName;
}

/**
 * @see com.anji.tournament.Player#getPlayerId()
 */
public String getPlayerId() {
	return name;
}

/**
 * @see com.anji.tournament.Player#reset()
 */
public void reset() {
	++resetCount;
	playCount = 0;
}

/**
 * reset count to 0
 */
public void resetResetCount() {
	resetCount = 0;
}

/**
 * @return reset count
 */
public int getResetCount() {
	return resetCount;
}

/**
 * @return # games played since last reset
 */
public int getPlayCount() {
	return playCount;
}
}
