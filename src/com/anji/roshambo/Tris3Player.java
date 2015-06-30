package com.anji.roshambo;

/**
 * http://www.cs.unimaas.nl/~donkers/games/roshambo03/
 */
public class Tris3Player implements RoshamboPlayer {

private static final int HISTORY_LENGTH = 10;

// set to something between 0 and 7ish maybe

private int myLastMoveBar1;

private int hisLastMoveBar1;

int myLastMove;

private int hisLastMove;

private int cTrial;

private HistoryState root;

private History hist;

/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	return getPlayerId();
}

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
	cTrial = 0;
	hisLastMove = 0;
	hisLastMoveBar1 = 0;
	hist = new History();
	myLastMove = 0;
	myLastMoveBar1 = 0;
	root = null;
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#nextMove()
 */
public int nextMove() {

	if ( root == null ) {
		myLastMove = Coin.flip();
	}
	else {
		myLastMove = root.getBestMove( hist.getCHistory() );
	}
	return myLastMove;
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#storeMove(int, int)
 */
public void storeMove( int move, int score ) {
	hisLastMove = move;

	if ( cTrial <= HISTORY_LENGTH ) {
		//There isn't any history to store yet
	}
	else {

		if ( root == null ) {
			root = new HistoryState( hist.getCHistory(), hisLastMove );
		}
		else {
			root.addState( hist.getCHistory(), hisLastMove );
		}
	}

	hist.addPairToHistory( myLastMove, hisLastMove );

	cTrial++; // Another trial is now in the database

}

/**
 * @see com.anji.tournament.Player#getPlayerId()
 */
public String getPlayerId() {
	return "Tris3 - the super history bot";
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#getAuthor()
 */
public String getAuthor() {
	return "Tristan Allwood";
}

class History {

int[][] cHistory = new int[ HISTORY_LENGTH ][ 2 ];

int[][] tmpHistory;

void addPairToHistory( int mLM, int hLM ) {
	tmpHistory = new int[ HISTORY_LENGTH ][ 2 ];

	for ( int i = 0; i < cHistory.length - 1; i++ ) {
		tmpHistory[ i ] = cHistory[ i + 1 ];
	}

	tmpHistory[ tmpHistory.length - 1 ] = new int[] { mLM, hLM };
	cHistory = tmpHistory;

}

int[][] getCHistory() {
	return cHistory;
}

}

class HistoryState {

int[][] history;

int future;

int freq;

HistoryState nextNode;

/**
 * ctor
 * @param inHistory
 * @param inFuture
 */
public HistoryState( int[][] inHistory, int inFuture ) {
	history = inHistory;
	future = inFuture;
	freq = 1;
}

void addState( int[][] inHistory, int inFuture ) {
	HistoryState cHS = this;
	boolean matches;
	while ( true ) {
		matches = true;

		for ( int i = 0; i < inHistory.length; i++ ) {
			if ( ( inHistory[ 0 ] != cHS.history[ 0 ] ) || ( inHistory[ 1 ] != cHS.history[ 1 ] ) ) {
				matches = false;
			}
		}

		if ( inFuture != cHS.future ) {
			matches = false;
		}

		if ( matches ) {
			cHS.freq++;
			return;
		}
		else if ( cHS.nextNode == null ) {
			cHS.nextNode = new HistoryState( inHistory, inFuture );
			return;
		}
		else {
			cHS = cHS.nextNode;
		}
	}
}

int getBestMove( int[][] cHistory ) {
	double cMax;
	int cSame;
	int cPos;

	HistoryState cSH = this;

	long[] scores = new long[ 3 ];
	double[] dscores = new double[ 3 ];
	long[] tmp;
	long[] divs = new long[ 3 ];

	while ( cSH != null ) {
		tmp = cSH.getScores( cHistory );
		scores[ (int) tmp[ 0 ] ] += tmp[ 1 ];
		divs[ (int) tmp[ 0 ] ] += tmp[ 2 ];
		cSH = cSH.nextNode;
	}

	//Copied from Tris2 - sorting code
	for ( int i = 0; i < 3; i++ ) {
		dscores[ i ] = scores[ i ];
	}

	cMax = dscores[ 0 ];

	cPos = 0;
	cSame = 0;
	for ( int i = 1; i < 3; i++ ) {
		if ( cMax < dscores[ i ] ) {
			cMax = dscores[ i ];
			cPos = i;
			cSame = 0;
		}
		else if ( cMax == dscores[ i ] ) {
			cSame++;
		}
	}
	if ( cSame == 1 ) {
		//    the annoying case
		if ( dscores[ 0 ] == dscores[ 1 ] ) {
			myLastMove = Coin.flip( 0.5, 0.5 );
		}
		else if ( dscores[ 1 ] == dscores[ 2 ] ) {
			myLastMove = Coin.flip( 0, 0.5 );
		}
		else {
			myLastMove = Coin.flip( 0.5, 0 );
		}
	}
	else if ( cSame == 2 ) {
		//    theya re all the same
		myLastMove = Coin.flip();
	}
	else {
		myLastMove = cPos;
	}

	//My last move hols what i think they will do

	myLastMove++;
	myLastMove = myLastMove % 3;
	return myLastMove;

}

long[] getScores( int[][] cHistory ) {
	//return[0] = rps
	//return[1] = score for it
	//return[2] = freq
	long[] output = new long[ 3 ];
	output[ 0 ] = future;
	output[ 1 ] = 0;
	output[ 2 ] = freq;
	for ( int i = 0; i < cHistory.length; i++ ) {
		if ( ( cHistory[ i ][ 0 ] == history[ i ][ 0 ] )
				&& ( cHistory[ i ][ 1 ] == history[ i ][ 1 ] ) ) {
			output[ 1 ] += ( ( i + 1 ) * ( i + 1 ) );
		}
	}

	output[ 1 ] *= ( freq * freq );
	return output;
}
}

/**
 * @see java.lang.Object#hashCode()
 */
public int hashCode() {
	return getPlayerId().hashCode();
}

}
