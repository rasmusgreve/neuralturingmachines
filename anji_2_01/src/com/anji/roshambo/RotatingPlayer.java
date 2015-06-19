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
 * Created on Apr 30, 2004 by Derek James
 */
package com.anji.roshambo;

import com.anji.util.Configurable;
import com.anji.util.Properties;

/**
 * This subject rotates Rock Paper Sissors. http://www.cs.unimaas.nl/~donkers/games/roshambo03/
 */
public class RotatingPlayer implements RoshamboPlayer, Configurable {

private final static String RAND_KEY = "rotatingplayer.start.random";

private int move = ROCK;

private boolean isStartRandom = false;

private final static double ONE_THIRD = 1d / 3d;

/**
 * default ctor
 */
public RotatingPlayer() {
	super();
}

/**
 * @param aStartRandom
 */
public RotatingPlayer( boolean aStartRandom ) {
	isStartRandom = aStartRandom;
}

/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	return getPlayerId();
}

/**
 * @see com.anji.tournament.Player#reset()
 */
public void reset() {
	if ( isStartRandom )
		move = Coin.flip( ONE_THIRD, ONE_THIRD );
	else
		move = ROCK;
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#reset(int)
 */
public void reset( int trials ) {
	reset();
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#storeMove(int, int)
 */
public void storeMove( int aMove, int score ) {
	// no-op
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#nextMove()
 */
public int nextMove() {
	move++;
	return move % 3;
}

/**
 * @see com.anji.tournament.Player#getPlayerId()
 */
public String getPlayerId() {
	return "Rotating Player";
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#getAuthor()
 */
public String getAuthor() {
	return "standard";
}

/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties props ) throws Exception {
	isStartRandom = props.getBooleanProperty( RAND_KEY );
}


/**
 * @see java.lang.Object#hashCode()
 */
public int hashCode() {
	return getPlayerId().hashCode();
}


}
