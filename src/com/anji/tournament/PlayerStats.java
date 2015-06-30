/*
 * Copyright (C) 2004 Derek James and Philip Tucker This file is part of ANJI (Another NEAT Java
 * Implementation). ANJI is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA Created on Mar 16, 2004 by Philip Tucker
 */
package com.anji.tournament;

import java.util.Random;

import org.apache.log4j.Logger;

import com.anji.util.XmlPersistable;

/**
 * Collates win-loss-tie results.
 * 
 * @author Philip Tucker
 */
public class PlayerStats implements XmlPersistable {

private final static Logger logger = Logger.getLogger( PlayerStats.class );

private final static String XML_TAG = "game-result";

private int wins;

private int losses;

private int ties;

private int rawScore = 0;

/**
 * New object with 0 wins, losses, or ties.
 */
public PlayerStats() {
	this( 0, 0, 0 );
}

/**
 * New object with specified wins, losses, or ties.
 * 
 * @param someWins
 * @param someLosses
 * @param someTies
 */
public PlayerStats( int someWins, int someLosses, int someTies ) {
	super();
	wins = someWins;
	losses = someLosses;
	ties = someTies;
}

///**
// * Returns new <code>PlayerStats</code> object based on opponent's results; i.e., wins become
// * losses and losses become wins.
// *
// * @param anOpponentResults
// * @return PlayerStats results for opponent of opponentResults
// */
//public static PlayerStats fromOpponentResults( PlayerStats anOpponentResults ) {
//	return new PlayerStats( anOpponentResults.getLosses(), anOpponentResults.getWins(),
//			anOpponentResults.getTies() );
//}

/**
 * @return int number of losses
 */
public int getLosses() {
	return losses;
}

/**
 * @return int number of ties
 */
public int getTies() {
	return ties;
}

/**
 * @return int number of wins
 */
public int getWins() {
	return wins;
}

/**
 * @return int raw score
 */
public int getRawScore() {
	return rawScore;
}

/**
 * adds <code>newLosses</code> to losses
 * 
 * @param newLosses
 */
public void incrementLosses( int newLosses ) {
	losses += newLosses;
}

/**
 * adds <code>newTies</code> to ties
 * 
 * @param newTies
 */
public void incrementTies( int newTies ) {
	ties += newTies;
}

/**
 * adds <code>newWins</code> to wins
 * 
 * @param newWins
 */
public void incrementWins( int newWins ) {
	wins += newWins;
}

/**
 * adds <code>newRawScore</code> to rawScore
 * 
 * @param newRawScore
 */
public void incrementRawScore( int newRawScore ) {
	rawScore += newRawScore;
}

/**
 * @return String String repesentation of results
 */
public String toString() {
	StringBuffer result = new StringBuffer();
	result.append( wins ).append( "-" ).append( losses ).append( "-" ).append( ties ).append(
			": " ).append( rawScore );
	return result.toString();
}

/**
 * @return String XML repesentation of results
 */
public String toXml() {
	StringBuffer result = new StringBuffer( "<" ).append( XML_TAG );
	result.append( " wins=\"" ).append( wins ).append( "\" losses=\"" ).append( losses );
	result.append( "\" ties=\"" ).append( ties ).append( "\" />" );
	return result.toString();
}

/**
 * Increment wins, losses, and ties by counts in <code>newResults</code>.
 * 
 * @param newResults
 */
public void increment( PlayerStats newResults ) {
	incrementWins( newResults.wins );
	incrementTies( newResults.ties );
	incrementLosses( newResults.losses );
	incrementRawScore( newResults.rawScore );
}

/**
 * @see java.lang.Object#equals(java.lang.Object)
 */
public boolean equals( Object o ) {
	PlayerStats other = (PlayerStats) o;
	return ( ( wins == other.wins ) && ( losses == other.losses ) && ( ties == other.ties ) && ( rawScore == other.rawScore ) );
}

/**
 * set all values
 * @param aWins
 * @param aLosses
 * @param aTies
 * @param aRawScore
 */
public void set( int aWins, int aLosses, int aTies, int aRawScore ) {
	wins = aWins;
	losses = aLosses;
	ties = aTies;
	rawScore = aRawScore;
}

/**
 * convert this object to a boolean result; note that this is based on wins, losses, and raw
 * score, not scoring weights
 * @param rand
 * @return true if wins > losses, false if losses > wins, coin flip otherwise
 */
public boolean isWin( Random rand ) {
	boolean result = false;
	if ( wins > losses )
		result = true;
	else if ( wins < losses )
		result = false;
	else {
		// wins == losses
		if ( rawScore > 0 )
			result = true;
		else if ( rawScore < 0 )
			result = false;
		else {
			logger.info( "breaking tie with coin flip" );
			result = rand.nextBoolean();
		}
	}
	return result;
}

/**
 * @see com.anji.util.XmlPersistable#getXmlRootTag()
 */
public String getXmlRootTag() {
	return XML_TAG;
}

/**
 * @see com.anji.util.XmlPersistable#getXmld()
 */
public String getXmld() {
	return Integer.toString( hashCode() );
}

}
