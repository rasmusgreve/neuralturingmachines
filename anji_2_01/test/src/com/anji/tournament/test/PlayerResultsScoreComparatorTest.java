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
 * Created on Jun 3, 2005 by Philip Tucker
 */
package com.anji.tournament.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import com.anji.roshambo.CopyingPlayer;
import com.anji.tournament.Player;
import com.anji.tournament.PlayerResults;
import com.anji.tournament.PlayerResultsScoreComparator;
import com.anji.tournament.ScoringWeights;

/**
 * @author Philip Tucker
 */
public class PlayerResultsScoreComparatorTest extends TestCase {

private final static ScoringWeights SCORING_WEIGHTS = new ScoringWeights( 4, 0, 1, 0 );

/**
 * ctor
 */
public PlayerResultsScoreComparatorTest() {
	this( PlayerResultsScoreComparatorTest.class.toString() );
}

/**
 * ctor
 * @param arg0
 */
public PlayerResultsScoreComparatorTest( String arg0 ) {
	super( arg0 );
}

/**
 * tets comparator
 * @throws Exception
 */
public void testIt() throws Exception {
	Player p1 = new CopyingPlayer();
	PlayerResults pr1 = new PlayerResults( p1, SCORING_WEIGHTS );
	Player p2 = new CopyingPlayer();
	PlayerResults pr2 = new PlayerResults( p2, SCORING_WEIGHTS );
	Player p3 = new CopyingPlayer();
	PlayerResults pr3 = new PlayerResults( p3, SCORING_WEIGHTS );
	Player p4 = new CopyingPlayer();
	PlayerResults pr4 = new PlayerResults( p4, SCORING_WEIGHTS );

	pr1.getResults().set( 3, 1, 2, 0 );
	pr2.getResults().set( 1, 2, 3, 0 );
	pr3.getResults().set( 1, 0, 1, 0 );
	pr4.getResults().set( 0, 0, 3, 0 );

	List results = new ArrayList();
	results.add( pr1 );
	results.add( pr2 );
	results.add( pr3 );
	results.add( pr4 );
	Collections.shuffle( results );

	// ascending
	Collections.sort( results, PlayerResultsScoreComparator.getAscendingInstance() );
	assertTrue( "wrong #1 ascending", pr4 == results.get( 0 ) );
	assertTrue( "wrong #2 ascending", pr3 == results.get( 1 ) );
	assertTrue( "wrong #3 ascending", pr2 == results.get( 2 ) );
	assertTrue( "wrong #4 ascending", pr1 == results.get( 3 ) );

	// descending
	Collections.sort( results, PlayerResultsScoreComparator.getDescendingInstance() );
	assertTrue( "wrong #1 descending", pr1 == results.get( 0 ) );
	assertTrue( "wrong #2 descending", pr2 == results.get( 1 ) );
	assertTrue( "wrong #3 descending", pr3 == results.get( 2 ) );
	assertTrue( "wrong #4 descending", pr4 == results.get( 3 ) );
}

}

