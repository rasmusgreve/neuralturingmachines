package com.anji.roshambo;

/**
 * This subject playes randomly with equal probabilities
 * http://www.cs.unimaas.nl/~donkers/games/roshambo03/
 */
public class RandomPlayer implements RoshamboPlayer {

/**
 * @see com.anji.tournament.Player#reset()
 */
public void reset() {
	reset( 0 );
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#reset(int)
 */
public void reset( int trials ) {
	// no-op
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#storeMove(int, int)
 */
public void storeMove( int move, int score ) {
	// no-op
}

/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	return getPlayerId();
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#nextMove()
 */
public int nextMove() {
	return Coin.flip();
}

/**
 * @see com.anji.tournament.Player#getPlayerId()
 */
public String getPlayerId() {
	return "Random Player";
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#getAuthor()
 */
public String getAuthor() {
	return "standard";
}


/**
 * @see java.lang.Object#hashCode()
 */
public int hashCode() {
	return getPlayerId().hashCode();
}

}

