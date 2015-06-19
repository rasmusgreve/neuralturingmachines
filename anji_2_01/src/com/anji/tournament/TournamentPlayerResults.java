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
 * Created on Jul 27, 2005 by Philip Tucker
 */
package com.anji.tournament;

/**
 * @author Philip Tucker
 */
public class TournamentPlayerResults {

private int rank = Integer.MAX_VALUE;

private int tournamentScore = 0;

private PlayerResults playerResults;

///**
// * @param aPlayer
// * @param aWeights
// */
//public TournamentPlayerResults( Player aPlayer, ScoringWeights aWeights ) {
//	this( new PlayerResults( aPlayer, aWeights ) );
//}

/**
 * @param aPlayerResults
 * @param aRank
 * @param aTournamentScore
 */
public TournamentPlayerResults( PlayerResults aPlayerResults, int aRank, int aTournamentScore ) {
	playerResults = aPlayerResults;
	rank = aRank;
	tournamentScore = aTournamentScore;
}

///**
// * @param aPlayer
// */
//public TournamentPlayerResults( Player aPlayer ) {
//	playerResults = new PlayerResults( aPlayer );
//}

/**
 * @return rank
 */
public int getRank() {
	return rank;
}

/**
 * @return tournament score
 */
public int getTournamentScore() {
	return tournamentScore;
}

/**
 * @return subject
 */
public Player getPlayer() {
	return playerResults.getPlayer();
}

/**
 * @return results
 */
public PlayerStats getResults() {
	return playerResults.getResults();
}

/**
 * @return score
 */
public float getScore() {
	return playerResults.getScore();
}

/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	return new StringBuffer().append( playerResults.toString() ).append( ": tourney " ).append(
			getRank() ).append( " [" ).append( getTournamentScore() ).append("]").toString();
}

/**
 * @see java.lang.Object#hashCode()
 */
public int hashCode() {
	return playerResults.hashCode();
}

/**
 * @see java.lang.Object#equals(java.lang.Object)
 */
public boolean equals( Object o ) {
	TournamentPlayerResults other = (TournamentPlayerResults) o;
	return getPlayer().equals( other.getPlayer() );
}

}
