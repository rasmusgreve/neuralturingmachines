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
 * created by Derek James
 */
package com.anji.ttt;

/**
 * Abstracts the common elements of the playing area of a game, such as Tic-Tac-Toe or Checkers.
 * The board is "squashed" into a 1-dimensional array of integers, referenced as the board
 * state. Positions containing friendly pieces have value 1, opponent pieces -1, and empty
 * spaces 0.
 * @author Derek James
 */
public interface Board {

/**
 * @return number of spaces in a single row or column
 */
public int getBoardSize();

/**
 * set up board to initial state before game has started
 */
public void initializeBoard();

/**
 * @param newMove position in which to place next move
 * @return false if <code>newMove</code> would create an invalid board state, true otherwise
 */
public boolean checkLegalMove( int newMove );

/**
 * updates board state such that position <code>newMove</code> will now equal 1
 * @param newMove position in which to place next move; must be between >= 0 and < size of board
 */
public void updateBoard( int newMove );

/**
 * @return boolean true if state of board represents a win for friendly pieces (those with value
 * 1), false otherwise
 */
public boolean checkForWin();

/**
 * @return true if state of board represents a tie, false otherwise
 */
public boolean checkForTie();

/**
 * @return string representation of board
 */
public String displayBoard();

/**
 * @return int[] representation of board; friendly pieces are 1, opponent -1, empty spaces 0
 */
public int[] getBoardState();

/**
 * swap all friendly pieces for opponent and vice versa
 */
public void swap();
}
