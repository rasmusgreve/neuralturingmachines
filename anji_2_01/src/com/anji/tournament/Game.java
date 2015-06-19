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
 * Created on Apr 10, 2005 by Philip Tucker
 */
package com.anji.tournament;

/**
 * Game does not carry state of a specific contest between two players. Each call to
 * <code>play(Player, Player)</code> is a specific contest, creating whatever game state is
 * needs. Note: A game should be "fair" between the two players. Any game for which there is a
 * significant advantage for moving first should play 2 games, one with each subject going
 * first.
 * 
 * @author Philip Tucker
 */
public interface Game {

/**
 * @param contestantResults
 * @param opponentResults
 * @return <code>GameResults</code> for this game only; these totals are also added to each
 * players results
 */
public GameResults play( PlayerResults contestantResults, PlayerResults opponentResults );

/**
 * @return class of which all players must be subclass (or implementor, if interface); must be
 * implementor of <code>Player</code>
 */
public Class requiredPlayerClass();

/**
 * @param weights
 * @return maximum possible score for a single game, given these scoring weights
 */
public int getMaxScore( ScoringWeights weights );

/**
 * @param weights
 * @return minimum possible score for a single game game, given these scoring weights
 */
public int getMinScore( ScoringWeights weights );
}
