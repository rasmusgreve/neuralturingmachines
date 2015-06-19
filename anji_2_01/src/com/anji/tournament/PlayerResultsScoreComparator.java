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
 * Created on May 30, 2005 by Philip Tucker
 */
package com.anji.tournament;

import java.util.Comparator;

/**
 * Sorts <code>PlayerResult</code> objects in descending order of score.
 * @author Philip Tucker
 */
public class PlayerResultsScoreComparator implements Comparator {

private static PlayerResultsScoreComparator ascInstance = null;

private static PlayerResultsScoreComparator descInstance = null;

private static PlayerResultsScoreComparator ascTourneyInstance = null;

private static PlayerResultsScoreComparator descTourneyInstance = null;

private boolean isAscending = true;

private boolean isTournament = false;

private PlayerResultsScoreComparator( boolean anIsAscending, boolean anIsTournament ) {
	isAscending = anIsAscending;
	isTournament = anIsTournament;
}

/**
 * sorts in ascending order of score
 * @return ascending comparator
 */
public static PlayerResultsScoreComparator getAscendingInstance() {
	if ( ascInstance == null )
		ascInstance = new PlayerResultsScoreComparator( true, false );
	return ascInstance;
}

/**
 * sorts in descending order of score
 * @return descending comparator
 */
public static PlayerResultsScoreComparator getDescendingInstance() {
	if ( descInstance == null )
		descInstance = new PlayerResultsScoreComparator( false, false );
	return descInstance;
}

/**
 * sorts in ascending order of tournament score
 * @return ascending comparator
 */
public static PlayerResultsScoreComparator getAscendingTournamentInstance() {
	if ( ascTourneyInstance == null )
		ascTourneyInstance = new PlayerResultsScoreComparator( true, true );
	return ascTourneyInstance;
}

/**
 * sorts in descending order of tournament score
 * @return descending comparator
 */
public static PlayerResultsScoreComparator getDescendingTournamentInstance() {
	if ( descTourneyInstance == null )
		descTourneyInstance = new PlayerResultsScoreComparator( false, true );
	return descTourneyInstance;
}

/**
 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
 */
public int compare( Object arg0, Object arg1 ) {
	float diff;
	if ( isTournament ) {
		TournamentPlayerResults par0 = (TournamentPlayerResults) arg0;
		TournamentPlayerResults par1 = (TournamentPlayerResults) arg1;
		diff = par0.getTournamentScore() - par1.getTournamentScore();
	}
	else {
		PlayerResults par0 = (PlayerResults) arg0;
		PlayerResults par1 = (PlayerResults) arg1;
		diff = par0.getScore() - par1.getScore();
	}
	int result = ( diff > 0 ) ? -1 : ( ( diff < 0 ) ? 1 : 0 );
	return ( isAscending ) ? -result : result;
}
}
