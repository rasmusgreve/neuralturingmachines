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

import java.util.Arrays;
import java.util.Random;

import org.apache.log4j.Logger;

import com.anji.nn.AnjiNet;
import com.anji.nn.BiasConnection;
import com.anji.nn.Pattern;
import com.anji.nn.StepHourglassConnection;

/**
 * @author Philip Tucker
 */
public class AnjiNetRoshamboPlayer implements RoshamboPlayer {

private final static Logger logger = Logger.getLogger( AnjiNetRoshamboPlayer.class );

private AnjiNet net;

private StepHourglassConnection hourglassConnection = new StepHourglassConnection();

private Random rand;

private int activationCycles = 1;

private double[] history;

private double[] previousResult = new double[ 1 ];

/**
 * Must have at least 6 inputs. Inputs 0-2 correspond to rock, scissors, paper. Each move the
 * network is activated with one of rock scissors or paer at 1.0 and the rest at 0.0. Input 3
 * corresponds to previous move results (<code>WIN</code>,<code>LOSS</code>,
 * <code>DRAW</code>). Input 4 is hourglass, and input 5 is bias. Must have at least 3
 * outputs. Outputs 0-2 are rock, scissors, paper, values between 0 and 1 (inclusive) indicating
 * affinity for each.
 * @param aNet
 * @param aRand if null, player deterministically chooses each move as the maximum output of the
 * net; otherwise, each move is chosen as a weighted probability based on each output
 * @param anActivationCycles number of cycles network is activated for each move
 * @param aHistorySize number of previous moves to input to net each cycle
 */
public AnjiNetRoshamboPlayer( AnjiNet aNet, Random aRand, int anActivationCycles,
		int aHistorySize ) {
	super();

	// validate nnet dimensions: 3 inputs (x historySize) for rock, scissors, paper 1 input for
	// previous result (-1 == loss, 0 == tie, 1 == win), 1 for hourglass, 1 for bias
	int historyInputCount = ( aHistorySize * 3 );
	int expectedInputDimension = historyInputCount + 3;
	if ( aNet.getInputDimension() < expectedInputDimension )
		throw new IllegalArgumentException( "Roshambo requires " + expectedInputDimension
				+ "  inputs" );
	else if ( aNet.getInputDimension() > expectedInputDimension )
		logger.warn( "more inputs (" + aNet.getInputDimension() + ") than expected ("
				+ expectedInputDimension + ")" );

	// output dimension = 3 (for each potential move)
	if ( aNet.getOutputDimension() < 3 )
		throw new IllegalArgumentException( "Roshambo scanner requires 3 outputs" );
	else if ( aNet.getOutputDimension() > 3 )
		logger.warn( "more outputs (" + aNet.getOutputDimension() + ") than expected (3)" );

	// set attributes
	net = aNet;
	rand = aRand;
	activationCycles = anActivationCycles;

	// input pattern and indexes
	history = new double[ historyInputCount ];
	Pattern historyPattern = new Pattern( history );
	Pattern prevResultPattern = new Pattern( previousResult );

	// connect inputs to net
	int i;
	for ( i = 0; i < historyPattern.getDimension(); ++i )
		net.getInputNeuron( i ).addIncomingConnection( historyPattern.getConnection( i ) );
	net.getInputNeuron( i++ ).addIncomingConnection( prevResultPattern.getConnection( 0 ) );
	net.getInputNeuron( i++ ).addIncomingConnection( hourglassConnection );
	net.getInputNeuron( i++ ).addIncomingConnection( BiasConnection.getInstance() );
}

/**
 * @param aNet
 * @see AnjiNetRoshamboPlayer#AnjiNetRoshamboPlayer(AnjiNet, int)
 */
public AnjiNetRoshamboPlayer( AnjiNet aNet ) {
	this( aNet, 1 );
}

/**
 * @param aNet
 * @param aRand
 * @see AnjiNetRoshamboPlayer#AnjiNetRoshamboPlayer(AnjiNet, Random, int, int)
 */
public AnjiNetRoshamboPlayer( AnjiNet aNet, Random aRand ) {
	this( aNet, aRand, 1, 1 );
}

/**
 * @param aNet
 * @param anActivationCycles
 * @see AnjiNetRoshamboPlayer#AnjiNetRoshamboPlayer(AnjiNet, Random, int, int)
 */
public AnjiNetRoshamboPlayer( AnjiNet aNet, int anActivationCycles ) {
	this( aNet, (Random) null, anActivationCycles, 1 );
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#reset(int)
 */
public void reset( int aTrials ) {
	net.reset();
	hourglassConnection.reset( aTrials );
	Arrays.fill( history, 0 );
	previousResult[ 0 ] = 0;
}

/**
 * @see com.anji.tournament.Player#reset()
 */
public void reset() {
	net.reset();
	hourglassConnection.reset();
	Arrays.fill( history, 0 );
	previousResult[ 0 ] = 0;
}

/**
 * factored out of <code>storeMove()</code> to be reused by
 * <code>AnjiNetScanningRoshamboPlayer</code>
 */
static void updateHistory( double[] aHistory, int aMove ) {
	// shift history
	for ( int i = 3; i < ( aHistory.length - 1 ); ++i )
		aHistory[ i - 3 ] = aHistory[ i ];

	// add new move to historyAndPrevResult
	aHistory[ aHistory.length - 3 ] = ( aMove == ROCK ) ? 1.0d : 0.0d;
	aHistory[ aHistory.length - 2 ] = ( aMove == PAPER ) ? 1.0d : 0.0d;
	aHistory[ aHistory.length - 1 ] = ( aMove == SCISSORS ) ? 1.0d : 0.0d;
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#storeMove(int, int)
 */
public void storeMove( int aMove, int aScore ) {
	updateHistory( history, aMove );
	previousResult[ 0 ] = aScore;
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#nextMove()
 */
public int nextMove() {
	// activate net
	for ( int i = 0; i < activationCycles; ++i ) {
		net.step();
		net.fullyActivate();
	}
	hourglassConnection.step();

	// get response and calculate next move
	return calculateMove( net.getOutputNeuron( ROCK ).getValue(), net.getOutputNeuron( PAPER )
			.getValue(), net.getOutputNeuron( SCISSORS ).getValue(), rand );
}

/**
 * factored out of <code>nextMove()</code> to be reused by
 * <code>AnjiNetScanningRoshamboPlayer</code>
 * @param rockAffinity
 * @param paperAffinity
 * @param scissorsAffinity
 * @param rand
 * @return <code>ROCK</code>,<code>PAPER</code>, or <code>SCISSORS</code>
 */
static int calculateMove( double rockAffinity, double paperAffinity, double scissorsAffinity,
		Random rand ) {
	// calculate result
	int result = SCISSORS;
	if ( rand == null ) {
		// deterministic player
		if ( rockAffinity > scissorsAffinity ) {
			if ( rockAffinity > paperAffinity )
				result = ROCK;
		}
		else if ( paperAffinity > scissorsAffinity )
			result = PAPER;
	}
	else {
		// probablistic player - ignore responses below 0 (assume no responses above 1)
		double totalAffinity = Math.max( 0d, rockAffinity ) + Math.max( 0d, paperAffinity )
				+ Math.max( 0d, scissorsAffinity );
		double prob = rand.nextDouble() * totalAffinity;
		if ( prob < rockAffinity )
			result = ROCK;
		else if ( prob < ( rockAffinity + paperAffinity ) )
			result = PAPER;
	}

	return result;
}

/**
 * @see com.anji.tournament.Player#getPlayerId()
 */
public String getPlayerId() {
	return net.getName();
}

/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	return net.getName();
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#getAuthor()
 */
public String getAuthor() {
	return "Derek James & Philip Tucker";
}

}
