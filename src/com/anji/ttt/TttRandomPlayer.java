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

import java.util.Random;

import com.anji.tournament.Player;
import com.anji.util.Configurable;
import com.anji.util.Properties;
import com.anji.util.Randomizer;

/**
 * "Random" tic-tac-toe subject. See <code>move()</code> for details.
 * @author Philip Tucker
 */
public class TttRandomPlayer implements BoardPlayer, Configurable {

private static final String playerId = "TTT Random Player";

private Random rand;

/**
 * @see Player#getPlayerId()
 */
public String getPlayerId() {
	return playerId;
}

/**
 * @see com.anji.ttt.BoardPlayer#move(int[])
 */
public int move( int[] boardState ) {
	return rand.nextInt( 9 );
}

/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties props ) throws Exception {
	Randomizer randomizer = (Randomizer) props.singletonObjectProperty( Randomizer.class );
	rand = randomizer.getRand();
}

/**
 * @see com.anji.tournament.Player#reset()
 */
public void reset() {
	// no-op
}


/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	return playerId;
}

}
