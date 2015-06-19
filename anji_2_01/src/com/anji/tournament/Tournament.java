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

import java.util.List;

/**
 * Abstract class that manages a list of players, has them play each other in matches, and
 * returns the results. Also returns min and max score.
 * 
 * @author Philip Tucker
 */
public interface Tournament {

/**
 * remove all contestants added via <code>addContestant()</code>
 */
public void clearContestants();

/**
 * @param contestant <code>Player</code> to be added.
 */
public void addContestant( Player contestant );

/**
 * Play full tournament.
 * @return <code>List</code> contains <code>TournamentPlayerResults</code> objects, sorted
 * in descending order of score
 */
public List playTournament();

/**
 * @return maximum possible score a contestant can achieve in this tournament; this value may
 * change as contestants are added; refers to
 * <code>TournamentPlayerResults.getTournamentScore()</code> not
 * <code>TournamentPlayerResults.getScore()</code>
 */
public int getMaxScore();

/**
 * @return minimum possible score a contestant can achieve in this tournament; this value may
 * change as contestants are added; refers to
 * <code>TournamentPlayerResults.getTournamentScore()</code> not
 * <code>TournamentPlayerResults.getScore()</code>
 */
public int getMinScore();

}
