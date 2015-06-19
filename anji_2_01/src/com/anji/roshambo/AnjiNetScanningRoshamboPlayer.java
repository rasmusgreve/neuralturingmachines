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

import com.anji.nn.ActivationFunction;
import com.anji.nn.AnjiNet;
import com.anji.nn.BiasConnection;
import com.anji.nn.Pattern;
import com.anji.nn.StepHourglassConnection;

/**
 * @author Philip Tucker
 */
public class AnjiNetScanningRoshamboPlayer implements RoshamboPlayer {

private final static Logger logger = Logger.getLogger( AnjiNetScanningRoshamboPlayer.class );

private final static int MAX_SCANNING_WINDOW_MOVE_PER_STEP = 3;

private final static int SCANNING_WINDOW_MOVE_OUTPUT_IDX = 3;

private double scanningWindowMovementOutputMid = 0d;

private double scanningWindowMovementOutputMaxAbs = 1d;

private AnjiNet net;

private StepHourglassConnection hourglassConnection = new StepHourglassConnection();

private Random rand;

private int activationCycles = 1;

private double[] history;

private double[] scanningWindow;

private double[] previousResult = new double[ 1 ];

private int scanningWindowPos;

private int scanningWindowPosMax;

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
 * @param aHistorySize number of previous moves to store
 * @param aScanningWindowSize number of moves (from move history) input to net each cycle
 */
public AnjiNetScanningRoshamboPlayer( AnjiNet aNet, Random aRand, int anActivationCycles,
		int aHistorySize, int aScanningWindowSize ) {
	super();

	// validate nnet dimensions: 3 inputs (x scanningWindowSize) for rock, scissors, paper 1 input
	// for previous result (-1 == loss, 0 == tie, 1 == win), 1 for hourglass, 1 for bias
	int scanningWindowInputCount = ( aScanningWindowSize * 3 );
	int expectedInputDimension = scanningWindowInputCount + 3;
	if ( aNet.getInputDimension() < expectedInputDimension )
		throw new IllegalArgumentException( "Roshambo requires " + expectedInputDimension
				+ "  inputs" );
	else if ( aNet.getInputDimension() > expectedInputDimension )
		logger.warn( "more inputs (" + aNet.getInputDimension() + ") than expected ("
				+ expectedInputDimension + ")" );

	// output dimension = 3 (for each potential move) + 1 for scanning window move
	if ( aNet.getOutputDimension() < 4 )
		throw new IllegalArgumentException( "Roshambo scanner requires 4 outputs" );
	else if ( aNet.getOutputDimension() > 4 )
		logger.warn( "more outputs (" + aNet.getOutputDimension() + ") than expected (4)" );

	// validate relative sizes of scanning window and history
	if ( aScanningWindowSize >= aHistorySize )
		throw new IllegalArgumentException( "scanning window size " + aScanningWindowSize
				+ " must be smaller than history size " + aHistorySize );
	ActivationFunction movementOutputFunction = aNet.getOutputNeuron(
			SCANNING_WINDOW_MOVE_OUTPUT_IDX ).getFunc();
	scanningWindowMovementOutputMid = ( movementOutputFunction.getMaxValue() + movementOutputFunction
			.getMinValue() ) / 2;
	scanningWindowMovementOutputMaxAbs = movementOutputFunction.getMaxValue()
			- scanningWindowMovementOutputMid;

	// set attributes
	net = aNet;
	rand = aRand;
	activationCycles = anActivationCycles;

	// input pattern and indexes
	history = new double[ aHistorySize * 3 ];
	previousResult[ 0 ] = 0;
	Pattern prevResultPattern = new Pattern( previousResult );
	scanningWindow = new double[ scanningWindowInputCount ];
	Pattern scanningWindowPattern = new Pattern( scanningWindow );
	scanningWindowPosMax = history.length - scanningWindow.length;
	scanningWindowPos = scanningWindowPosMax;

	// connect inputs to net
	int i;
	for ( i = 0; i < scanningWindowPattern.getDimension(); ++i )
		net.getInputNeuron( i ).addIncomingConnection( scanningWindowPattern.getConnection( i ) );
	net.getInputNeuron( i++ ).addIncomingConnection( prevResultPattern.getConnection( 0 ) );
	net.getInputNeuron( i++ ).addIncomingConnection( hourglassConnection );
	net.getInputNeuron( i++ ).addIncomingConnection( BiasConnection.getInstance() );
}

/**
 * @param aNet
 * @see AnjiNetScanningRoshamboPlayer#AnjiNetScanningRoshamboPlayer(AnjiNet, int)
 */
public AnjiNetScanningRoshamboPlayer( AnjiNet aNet ) {
	this( aNet, 1 );
}

/**
 * @param aNet
 * @param aRand
 * @see AnjiNetScanningRoshamboPlayer#AnjiNetScanningRoshamboPlayer(AnjiNet, Random, int, int,
 * int)
 */
public AnjiNetScanningRoshamboPlayer( AnjiNet aNet, Random aRand ) {
	this( aNet, aRand, 1, 1, 0 );
}

/**
 * @param aNet
 * @param anActivationCycles
 * @see AnjiNetScanningRoshamboPlayer#AnjiNetScanningRoshamboPlayer(AnjiNet, Random, int, int,
 * int)
 */
public AnjiNetScanningRoshamboPlayer( AnjiNet aNet, int anActivationCycles ) {
	this( aNet, (Random) null, anActivationCycles, 1, 0 );
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
 * @see com.anji.roshambo.RoshamboPlayer#storeMove(int, int)
 */
public void storeMove( int aMove, int aScore ) {
	AnjiNetRoshamboPlayer.updateHistory( history, aMove );

	updateScanningWindow();

	previousResult[ 0 ] = aScore;
}

private void updateScanningWindow() {
	// update scanning window position
	double value = net.getOutputNeuron( SCANNING_WINDOW_MOVE_OUTPUT_IDX ).getValue();
	double relativeMovementDelta = ( value - scanningWindowMovementOutputMid )
			/ scanningWindowMovementOutputMaxAbs;
	int absMovementDelta = (int) Math.round( relativeMovementDelta
			* MAX_SCANNING_WINDOW_MOVE_PER_STEP );
	scanningWindowPos += ( absMovementDelta * 3 );

	// clamp between 0 and scanningWindowPosMax, which is history.length - scanningWindow.length
	scanningWindowPos = Math.max( scanningWindowPos, 0 );
	scanningWindowPos = Math.min( scanningWindowPos, scanningWindowPosMax );

	// fill scanning window from history
	for ( int i = 0; i < scanningWindow.length; ++i )
		scanningWindow[ i ] = history[ i + scanningWindowPos ];
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#nextMove()
 */
public int nextMove() {
	// activate net and get response
	for ( int i = 0; i < activationCycles; ++i ) {
		net.step();
		net.fullyActivate();
		updateScanningWindow();
	}
	hourglassConnection.step();

	// get response and calculate next move
	return AnjiNetRoshamboPlayer.calculateMove( net.getOutputNeuron( ROCK ).getValue(), net
			.getOutputNeuron( PAPER ).getValue(), net.getOutputNeuron( SCISSORS ).getValue(), rand );
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
