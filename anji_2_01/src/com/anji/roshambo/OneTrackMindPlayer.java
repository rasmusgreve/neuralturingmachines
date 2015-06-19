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
 * Created on Apr 30, 2004 by Philip Tucker
 */
package com.anji.roshambo;

import com.anji.util.Configurable;
import com.anji.util.Properties;

/**
 * @author Philip Tucker
 */
public class OneTrackMindPlayer implements RoshamboPlayer, Configurable {

private final static String ONE_TRACK_KEY = "onetrackmindplayer.onetrack";

private final static String RAND_KEY = "onetrackmindplayer.start.random";

private int oneTrack = ROCK;

private boolean isRandStart = false;

private final static double ONE_THIRD = 1d / 3d;

/**
 * default ctor
 */
public OneTrackMindPlayer() {
	super();
}

/**
 * @param aTrack
 */
public OneTrackMindPlayer( int aTrack ) {
	if ( aTrack != ROCK && aTrack != SCISSORS && aTrack != PAPER )
		throw new IllegalArgumentException( "invalid R/S/P parameter " + aTrack );
	oneTrack = aTrack;
}

/**
 * @param aRandStart
 */
public OneTrackMindPlayer( boolean aRandStart ) {
	isRandStart = aRandStart;
}

/**
 * @see com.anji.tournament.Player#reset()
 */
public void reset() {
	if ( isRandStart )
		oneTrack = Coin.flip( ONE_THIRD, ONE_THIRD );
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
public void storeMove( int move, int score ) {
	// no-op
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#nextMove()
 */
public int nextMove() {
	return oneTrack;
}

/**
 * @see com.anji.tournament.Player#getPlayerId()
 */
public String getPlayerId() {
	return ( isRandStart ? "Rand" : toChoiceString( oneTrack ) ) + " 1 Track Player";
}

/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	return getPlayerId();
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#getAuthor()
 */
public String getAuthor() {
	return "Philip Tucker";
}

/**
 * @see Configurable#init(Properties)
 */
public void init( Properties props ) throws Exception {
	isRandStart = props.getBooleanProperty( RAND_KEY, false );
	String oneTrackVal = props.getProperty( ONE_TRACK_KEY, "rock" ).trim().toLowerCase();
	if ( oneTrackVal.equals( "rock" ) )
		oneTrack = ROCK;
	else if ( oneTrackVal.equals( "scissors" ) )
		oneTrack = SCISSORS;
	else if ( oneTrackVal.equals( "paper" ) )
		oneTrack = PAPER;
	else
		throw new IllegalArgumentException( "invalid rock/scissors/paper value: " + oneTrackVal );
}

/**
 * @return <code>ROCK</code>,<code>SCISSORS</code>, or <code>PAPER</code>
 */
public int getOneTrack() {
	return oneTrack;
}

private static String toChoiceString( int choice ) {
	return ( choice == ROCK ) ? "Rock" : ( ( choice == SCISSORS ) ? "Scissors" : "Paper" );
}

/**
 * @see java.lang.Object#hashCode()
 */
public int hashCode() {
	return getPlayerId().hashCode();
}

}
