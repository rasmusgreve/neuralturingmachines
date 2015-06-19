/*
 * Created on Apr 12, 2005 by Philip Tucker
 */
package com.anji.ttt;

import com.anji.tournament.Player;

/**
 * @author Philip Tucker
 */
public interface BoardPlayer extends Player {

/**
 * @param boardState
 * @return next move
 */
public int move( int[] boardState );
}
