package com.anji.roshambo;

import com.anji.util.Configurable;
import com.anji.util.Properties;

/**
 * This subject copies the opponent's move. http://www.cs.unimaas.nl/~donkers/games/roshambo03/
 */
public class CopyingPlayer implements RoshamboPlayer, Configurable {

private final static String RAND_KEY = "copyingplayer.start.random";

private int lastmove = ROCK;

private boolean isStartRandom = false;

private final static double ONE_THIRD = 1d / 3d;

/**
 * default ctor
 */
public CopyingPlayer() {
	super();
}

/**
 * @param aStartRandom
 */
public CopyingPlayer( boolean aStartRandom ) {
	isStartRandom = aStartRandom;
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#reset(int)
 */
public void reset( int trials ) {
	reset();
}

/**
 * @see com.anji.tournament.Player#reset()
 */
public void reset() {
	if ( isStartRandom )
		lastmove = Coin.flip( ONE_THIRD, ONE_THIRD );
	else
		lastmove = ROCK;
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#storeMove(int, int)
 */
public void storeMove( int move, int score ) {
	lastmove = move;
}

/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	return getPlayerId();
}

/**
 * Copies opponent's last move.
 * @see com.anji.roshambo.RoshamboPlayer#nextMove()
 */
public int nextMove() {
	return lastmove;
}

/**
 * @see com.anji.tournament.Player#getPlayerId()
 */
public String getPlayerId() {
	return "Copy Player";
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#getAuthor()
 */
public String getAuthor() {
	return "standard";
}

/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties props ) throws Exception {
	isStartRandom = props.getBooleanProperty( RAND_KEY, false );
	if ( isStartRandom )
		reset( 0 );
}

/**
 * @see java.lang.Object#hashCode()
 */
public int hashCode() {
	return getPlayerId().hashCode();
}

}
