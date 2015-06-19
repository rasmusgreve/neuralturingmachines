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
 * Created on Jul 31, 2005 by Philip Tucker
 */
package com.anji.roshambo.test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import junit.framework.TestCase;

import com.anji.roshambo.CopyingPlayer;
import com.anji.roshambo.DeanPlayer;
import com.anji.roshambo.EnigmaPlayer;
import com.anji.roshambo.GnobotPlayer;
import com.anji.roshambo.IocainePlayer;
import com.anji.roshambo.JustRockPlayer;
import com.anji.roshambo.MarshalbotPlayer;
import com.anji.roshambo.MohammedkaaschPlayer;
import com.anji.roshambo.Muto5Player;
import com.anji.roshambo.OneTrackMindPlayer;
import com.anji.roshambo.RandomPlayer;
import com.anji.roshambo.RoshamboPlayer;
import com.anji.roshambo.RotatingPlayer;
import com.anji.roshambo.Tris3Player;
import com.anji.roshambo.UrzaPlayer;
import com.anji.roshambo.WizardexpPlayer;

/**
 * @author Philip Tucker
 */
public class RoshamboPlayerTest extends TestCase {

private final static CopyingPlayer COPYING_PLAYER = new CopyingPlayer( false );

private final static CopyingPlayer RANDSTART_COPYING_PLAYER = new CopyingPlayer( true );

private final static DeanPlayer DEAN_PLAYER = new DeanPlayer();

private final static EnigmaPlayer ENIGMA_PLAYER = new EnigmaPlayer();

private final static GnobotPlayer GNOBOT_PLAYER = new GnobotPlayer();

private final static IocainePlayer IOCAINE_PLAYER = new IocainePlayer();

private final static JustRockPlayer JUST_ROCK_PLAYER = new JustRockPlayer();

private final static MarshalbotPlayer MARSHALBOT_PLAYER = new MarshalbotPlayer();

private final static MohammedkaaschPlayer MOHAMMEDKAASCH_PLAYER = new MohammedkaaschPlayer();

private final static Muto5Player MUTO5_PLAYER = new Muto5Player();

private final static OneTrackMindPlayer ROCK_1TRACK_PLAYER = new OneTrackMindPlayer(
		RoshamboPlayer.ROCK );

private final static OneTrackMindPlayer SCISSORS_1TRACK_PLAYER = new OneTrackMindPlayer(
		RoshamboPlayer.SCISSORS );

private final static OneTrackMindPlayer PAPER_1TRACK_PLAYER = new OneTrackMindPlayer(
		RoshamboPlayer.PAPER );

private final static OneTrackMindPlayer RANDSTART_1TRACK_PLAYER = new OneTrackMindPlayer( true );

private final static RandomPlayer RANDOM_PLAYER = new RandomPlayer();

private final static RotatingPlayer ROTATING_PLAYER = new RotatingPlayer( false );

private final static RotatingPlayer RANDSTART_ROTATING_PLAYER = new RotatingPlayer( true );

private final static Tris3Player TRIS3_PLAYER = new Tris3Player();

private final static UrzaPlayer URZA_PLAYER = new UrzaPlayer();

private final static WizardexpPlayer WIZARDEXP_PLAYER = new WizardexpPlayer();

private final static HashSet PLAYERS = new HashSet();
static {
	PLAYERS.add( JUST_ROCK_PLAYER );
	PLAYERS.add( ROCK_1TRACK_PLAYER );
	PLAYERS.add( PAPER_1TRACK_PLAYER );
	PLAYERS.add( RANDSTART_1TRACK_PLAYER );
	PLAYERS.add( SCISSORS_1TRACK_PLAYER );
	PLAYERS.add( COPYING_PLAYER );
	PLAYERS.add( RANDSTART_COPYING_PLAYER );
	PLAYERS.add( ROTATING_PLAYER );
	PLAYERS.add( RANDSTART_ROTATING_PLAYER );
	PLAYERS.add( DEAN_PLAYER );
	PLAYERS.add( ENIGMA_PLAYER );
	PLAYERS.add( GNOBOT_PLAYER );
	PLAYERS.add( IOCAINE_PLAYER );
	PLAYERS.add( MARSHALBOT_PLAYER );
	PLAYERS.add( MOHAMMEDKAASCH_PLAYER );
	PLAYERS.add( MUTO5_PLAYER );
	PLAYERS.add( RANDOM_PLAYER );
	PLAYERS.add( TRIS3_PLAYER );
	PLAYERS.add( URZA_PLAYER );
	PLAYERS.add( WIZARDEXP_PLAYER );
}

private final static Random RAND = new Random();

private void assertValidMove( int move ) {
	if ( move != RoshamboPlayer.ROCK && move != RoshamboPlayer.SCISSORS
			&& move != RoshamboPlayer.PAPER )
		fail( "invalid move value " + move );
}

/**
 * @throws Exception
 */
public void testAll() throws Exception {
	Iterator it = PLAYERS.iterator();
	while ( it.hasNext() ) {
		RoshamboPlayer uut = (RoshamboPlayer) it.next();
		assertNotNull( "null playerId " + uut, uut.getPlayerId() );
		assertEquals( "playerId and toString differ for " + uut.getPlayerId(), uut.getPlayerId(),
				uut.toString() );
		assertEquals( "playerId and hashcode differ for " + uut.getPlayerId(), uut.getPlayerId()
				.hashCode(), uut.hashCode() );

		long startMillis = System.currentTimeMillis();
		int gameCount = 4;
		int trialCount = 1000;
		for ( int gameIdx = 0; gameIdx < gameCount; ++gameIdx ) {
			uut.reset( 1000 );
			for ( int trialIdx = 0; trialIdx < trialCount; ++trialIdx ) {
				int move = uut.nextMove();
				assertValidMove( move );
				uut.storeMove( RAND.nextInt( 3 ), RAND.nextInt( 3 ) - 1 );
			}
		}
		System.out.println( uut.toString() + " took " + ( System.currentTimeMillis() - startMillis )
				+ " ms for 4 games of 1000 trials" );
	}
}

/**
 * test copying player
 */
public void testCopyingPlayers() {
	doTestCopyingPlayer( COPYING_PLAYER, false );
	doTestCopyingPlayer( RANDSTART_COPYING_PLAYER, true );
}

private void doTestCopyingPlayer( RoshamboPlayer uut, boolean isStartRand ) {
	int gameCount = 4;
	int trialCount = 1000;
	for ( int gameIdx = 0; gameIdx < gameCount; ++gameIdx ) {
		uut.reset( trialCount );
		int lastMove = RoshamboPlayer.ROCK;
		for ( int trialIdx = 0; trialIdx < trialCount; ++trialIdx ) {
			int move = uut.nextMove();
			if ( !isStartRand || ( trialIdx > 0 ) )
				assertEquals( "copying player move different from last opponent move", lastMove, move );
			lastMove = RAND.nextInt( 3 );
			uut.storeMove( lastMove, RAND.nextInt( 3 ) - 1 );
		}
	}
}

/**
 * test rotating player
 */
public void testRotatingPlayers() {
	doTestRotatingPlayer( ROTATING_PLAYER, false );
	doTestRotatingPlayer( RANDSTART_ROTATING_PLAYER, true );
}

private void doTestRotatingPlayer( RoshamboPlayer uut, boolean isStartRand ) {
	int gameCount = 4;
	int trialCount = 1000;
	for ( int gameIdx = 0; gameIdx < gameCount; ++gameIdx ) {
		uut.reset( trialCount );
		int moveCount = 1;
		for ( int trialIdx = 0; trialIdx < trialCount; ++trialIdx ) {
			int move = uut.nextMove();
			if ( isStartRand && trialIdx == 0 )
				moveCount = move;
			else
				assertEquals( "wrong rotating player move", moveCount % 3, move );
			++moveCount;
			uut.storeMove( RAND.nextInt( 3 ), RAND.nextInt( 3 ) - 1 );
		}
	}
}

/**
 * test one track players
 */
public void testOneTrackPlayers() {
	assertEquals( ROCK_1TRACK_PLAYER.getOneTrack(), RoshamboPlayer.ROCK );
	assertEquals( SCISSORS_1TRACK_PLAYER.getOneTrack(), RoshamboPlayer.SCISSORS );
	assertEquals( PAPER_1TRACK_PLAYER.getOneTrack(), RoshamboPlayer.PAPER );
	doTestOneTrack( JUST_ROCK_PLAYER, RoshamboPlayer.ROCK );
	doTestOneTrack( ROCK_1TRACK_PLAYER, RoshamboPlayer.ROCK );
	doTestOneTrack( SCISSORS_1TRACK_PLAYER, RoshamboPlayer.SCISSORS );
	doTestOneTrack( PAPER_1TRACK_PLAYER, RoshamboPlayer.PAPER );
}

private void doTestOneTrack( RoshamboPlayer uut, int oneTrack ) {
	int gameCount = 4;
	int trialCount = 1000;
	for ( int gameIdx = 0; gameIdx < gameCount; ++gameIdx ) {
		uut.reset( trialCount );
		for ( int trialIdx = 0; trialIdx < trialCount; ++trialIdx ) {
			assertEquals( "wrong one track player move: " + uut, oneTrack, uut.nextMove() );
			uut.storeMove( RAND.nextInt( 3 ), RAND.nextInt( 3 ) - 1 );
		}
	}
}

/**
 * test random start one track player
 */
public void testRandStartOneTrackPlayer() {
	int gameCount = 4;
	int trialCount = 1000;
	int oneTrack = RoshamboPlayer.ROCK;
	for ( int gameIdx = 0; gameIdx < gameCount; ++gameIdx ) {
		RANDSTART_1TRACK_PLAYER.reset( trialCount );
		for ( int trialIdx = 0; trialIdx < trialCount; ++trialIdx ) {
			int move = RANDSTART_1TRACK_PLAYER.nextMove();
			if ( trialIdx == 0 )
				oneTrack = move;
			else
				assertEquals( "wrong one track player move: " + RANDSTART_1TRACK_PLAYER, oneTrack, move );
			RANDSTART_1TRACK_PLAYER.storeMove( RAND.nextInt( 3 ), RAND.nextInt( 3 ) - 1 );
		}
	}
}

}
