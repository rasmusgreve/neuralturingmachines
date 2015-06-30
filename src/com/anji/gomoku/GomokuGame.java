/*
 * Copyright (C) 2004 Derek James and Philip Tucker This file is part of ANJI (Another NEAT Java
 * Implementation). ANJI is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA created by Derek James
 */
package com.anji.gomoku;

import java.util.Calendar;

import org.apache.log4j.Logger;

import com.anji.tournament.Game;
import com.anji.tournament.GameConfiguration;
import com.anji.tournament.GameResults;
import com.anji.tournament.PlayerResults;
import com.anji.tournament.ScoringWeights;
import com.anji.ttt.Board;
import com.anji.ttt.BoardPlayer;
import com.anji.util.Configurable;

/**
 * @author Derek James
 */
public class GomokuGame implements Game, Configurable {

private static Logger logger = Logger.getLogger( GomokuGame.class );

private final static int DEFAULT_BOARD_SIZE = 7;

private int boardSize = DEFAULT_BOARD_SIZE;

private GameConfiguration gameConfig = GameConfiguration.DEFAULT;

private String name = "Gomoku";

/**
 * @see com.anji.tournament.Game#play(com.anji.tournament.PlayerResults,
 * com.anji.tournament.PlayerResults)
 */
public GameResults play( PlayerResults contestantResults, PlayerResults opponentResults ) {
	GameResults results = new GameResults();

	// players
	BoardPlayer playerOne = (BoardPlayer) contestantResults.getPlayer();
	BoardPlayer playerTwo = (BoardPlayer) opponentResults.getPlayer();
	if ( gameConfig.doResetPlayers() ) {
		playerOne.reset();
		playerTwo.reset();
	}

	boolean gameOver = false;
	int newMove = 0;
	boolean legalMove = false;
	int numMoves = 0;
	StringBuffer gameOneXml = new StringBuffer();
	String playerOneId = playerOne.getPlayerId();
	String playerTwoId = playerTwo.getPlayerId();
	String XML_GAME_TAG = "Gomoku";
	String XML_TYPE_TAG = "GomokuGame";
	String XML_INFORMATION_TAG = "Information";
	String XML_BOARDSIZE_TAG = "BoardSize";
	String XML_COMMENT_TAG = "Comment";
	String MOVE_COLUMN = null;
	int MOVE_ROW = 0;
	int moveColumn = 0;

	Board board = new GomokuBoard( boardSize );
	board.initializeBoard();
	gameOver = false;

	gameOneXml.append( "<" ).append( XML_GAME_TAG ).append( ">\n" );
	gameOneXml.append( "<" ).append( XML_TYPE_TAG ).append( ">\n" );
	gameOneXml.append( "<" ).append( XML_INFORMATION_TAG ).append( ">\n" );
	gameOneXml.append( "<" ).append( XML_BOARDSIZE_TAG ).append( ">" );
	gameOneXml.append( boardSize );
	gameOneXml.append( "</" ).append( XML_BOARDSIZE_TAG ).append( ">\n" );
	gameOneXml.append( "</" ).append( XML_INFORMATION_TAG ).append( ">\n" );
	gameOneXml.append( "<" ).append( XML_COMMENT_TAG ).append( ">" );
	gameOneXml.append( playerOneId ).append( " vs. " );
	gameOneXml.append( playerTwoId ).append( "  \n" );
	gameOneXml.append( Calendar.getInstance().getTime() );
	gameOneXml.append( "</" ).append( XML_COMMENT_TAG ).append( ">\n" );

	while ( gameOver == false ) {

		legalMove = false;
		MOVE_COLUMN = null;
		MOVE_ROW = 0;
		moveColumn = 0;

		while ( legalMove == false ) {
			newMove = playerOne.move( board.getBoardState() );
			legalMove = board.checkLegalMove( newMove );
		}

		moveColumn = ( newMove % boardSize );
		switch ( moveColumn ) {
			case 0:
				MOVE_COLUMN = "A";
				break;
			case 1:
				MOVE_COLUMN = "B";
				break;
			case 2:
				MOVE_COLUMN = "C";
				break;
			case 3:
				MOVE_COLUMN = "D";
				break;
			case 4:
				MOVE_COLUMN = "E";
				break;
			case 5:
				MOVE_COLUMN = "F";
				break;
			case 6:
				MOVE_COLUMN = "G";
				break;
			case 7:
				MOVE_COLUMN = "H";
				break;
			case 8:
				MOVE_COLUMN = "J";
				break;
			case 9:
				MOVE_COLUMN = "K";
				break;
			case 10:
				MOVE_COLUMN = "L";
				break;
			case 11:
				MOVE_COLUMN = "M";
				break;
			case 12:
				MOVE_COLUMN = "N";
				break;
			case 13:
				MOVE_COLUMN = "O";
				break;
			case 14:
				MOVE_COLUMN = "P";
				break;
		}
		MOVE_ROW = ( ( newMove / boardSize ) + 1 );
		gameOneXml.append( "<Black at=\"" ).append( MOVE_COLUMN ).append( MOVE_ROW ).append(
				"\" />\n" );
		numMoves++;
		board.updateBoard( newMove );

		logger.debug( "Player 1:\n" + board.displayBoard() ); //display
		// board

		if ( board.checkForWin() ) {
			logger.debug( "Player 1 Wins!\n\n" );
			results.incrementPlayer1Wins( 1 );
			gameOver = true;
		}
		else if ( board.checkForTie() ) {
			logger.debug( "Tie Game!\n\n" );
			results.incrementTies( 1 );
			gameOver = true;
		}

		board.swap(); //change signs of board state

		if ( gameOver == false ) {

			legalMove = false;
			MOVE_COLUMN = null;
			MOVE_ROW = 0;
			moveColumn = 0;

			while ( legalMove == false ) {
				newMove = playerTwo.move( board.getBoardState() );
				legalMove = board.checkLegalMove( newMove );
			}

			moveColumn = ( newMove % boardSize );
			switch ( moveColumn ) {
				case 0:
					MOVE_COLUMN = "A";
					break;
				case 1:
					MOVE_COLUMN = "B";
					break;
				case 2:
					MOVE_COLUMN = "C";
					break;
				case 3:
					MOVE_COLUMN = "D";
					break;
				case 4:
					MOVE_COLUMN = "E";
					break;
				case 5:
					MOVE_COLUMN = "F";
					break;
				case 6:
					MOVE_COLUMN = "G";
					break;
				case 7:
					MOVE_COLUMN = "H";
					break;
				case 8:
					MOVE_COLUMN = "J";
					break;
				case 9:
					MOVE_COLUMN = "K";
					break;
				case 10:
					MOVE_COLUMN = "L";
					break;
				case 11:
					MOVE_COLUMN = "M";
					break;
				case 12:
					MOVE_COLUMN = "N";
					break;
				case 13:
					MOVE_COLUMN = "O";
					break;
				case 14:
					MOVE_COLUMN = "P";
					break;
			}
			MOVE_ROW = ( ( newMove / boardSize ) + 1 );
			gameOneXml.append( "<White at=\"" ).append( MOVE_COLUMN ).append( MOVE_ROW ).append(
					"\" />\n" );

			numMoves++;
			board.updateBoard( newMove );

			logger.debug( "Player 2:\n" + board.displayBoard() ); //display
			// board

			if ( board.checkForWin() ) {
				logger.debug( "Player 2 Wins!\n\n" );
				results.incrementPlayer1Losses( 1 );
				gameOver = true;
			}
			else if ( board.checkForTie() ) {
				logger.debug( "Tie Game!\n\n" );
				results.incrementTies( 1 );
				gameOver = true;
			}

			board.swap(); //change signs of board state
		}
	}

	gameOneXml.append( "</" ).append( XML_TYPE_TAG ).append( ">\n" );
	gameOneXml.append( "</" ).append( XML_GAME_TAG ).append( ">\n" );
	logger.debug( gameOneXml );

	logger.debug( results.toString() );

	// results
	if ( gameConfig.doLogResults() )
		logger.info( new StringBuffer( name ).append( ": " ).append( contestantResults.getPlayer() )
				.append( " vs " ).append( opponentResults.getPlayer() ).append( ": " ).append( results )
				.toString() );
	contestantResults.getResults().increment( results.getPlayer1Stats() );
	opponentResults.getResults().increment( results.getPlayer2Stats() );
	return results;
}

/**
 * @see com.anji.tournament.Game#requiredPlayerClass()
 */
public Class requiredPlayerClass() {
	return BoardPlayer.class;
}

/**
 * @see com.anji.tournament.Game#getMaxScore(com.anji.tournament.ScoringWeights)
 */
public int getMaxScore( ScoringWeights aWeights ) {
	return aWeights.getWinValue();
}

/**
 * @see com.anji.tournament.Game#getMinScore(com.anji.tournament.ScoringWeights)
 */
public int getMinScore( ScoringWeights aWeights ) {
	return aWeights.getLossValue();
}

/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( com.anji.util.Properties props ) throws Exception {
	gameConfig = (GameConfiguration) props.singletonObjectProperty( GameConfiguration.class );
	name += ( " " + props.getName() );
}

}
