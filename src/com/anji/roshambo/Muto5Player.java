package com.anji.roshambo;

import java.util.TreeMap;

/**
 * http://www.cs.unimaas.nl/~donkers/games/roshambo03/
 * TODO - reset fields
 */
public class Muto5Player implements RoshamboPlayer {

int myHistory[], oppHistory[];

int lastMove;

MyMethod[] methods;

int trials;

int score = 0;

int lossStreak = 0;

int lastMethod = 0;

int currentMethod = 0;

static final boolean verbose = false;

static final int NUM_METHODS = 4;

static final int LOSS_STREAK_MAX = 3;

static final int LOSS_STREAK_PENALTY = 5;

/**
 * @see com.anji.tournament.Player#getPlayerId()
 */
public String getPlayerId() {
	return "MUTO";
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#getAuthor()
 */
public String getAuthor() {
	return "Andrew Wheat/Tristan Allwood";
}

/**
 * @see com.anji.tournament.Player#reset()
 */
public void reset() {
	reset( trials );
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#reset(int)
 */
public void reset( int aTrials ) {
	this.trials = aTrials;

	myHistory = new int[ aTrials + 1 ];
	oppHistory = new int[ aTrials + 1 ];
	myHistory[ 0 ] = oppHistory[ 0 ] = 0;

	// iniciate the various methods
	methods = new MyMethod[ NUM_METHODS ];
	methods[ 3 ] = new MyMethod( new NotInLast( aTrials ), 1 );
	methods[ 2 ] = new MyMethod( new Tris2( aTrials ), 2 );
	methods[ 1 ] = new MyMethod( new Tris3( aTrials ), 3 );
	methods[ 0 ] = new MyMethod( new MyRandom( aTrials ), 4 );

}

/**
 * @see com.anji.roshambo.RoshamboPlayer#storeMove(int, int)
 */
public void storeMove( int move, int aScore ) {
	oppHistory[ ++oppHistory[ 0 ] ] = move;
	myHistory[ ++myHistory[ 0 ] ] = lastMove;

	this.score += aScore;

	if ( aScore < 0 ) {
		lossStreak++;
	}
	else {
		lossStreak = 0;
	}

	// call the storeMove s of the inner classes
	for ( int i = 0; i < NUM_METHODS; i++ ) {
		methods[ i ].thisMethod.storeMove( move, aScore );
		if ( move == methods[ i ].myLastMove ) {
			// this would have drawn
			methods[ i ].myQueue.add( 0 );
			//				  methods[i].score += 1;
		}
		else if ( move == ( methods[ i ].myLastMove + 1 ) % 3 ) {
			// this would have lost
			methods[ i ].myQueue.add( -1 );
			//				  methods[i].score -= 1;
		}
		else {
			// the only other option! this would have won
			methods[ i ].myQueue.add( 1 );
			//				  methods[i].score += 2;
		}
		methods[ i ].updateScore();
	}
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
	// find the best method, and use that
	int maxScore = methods[ 0 ].myScore;
	int maxIndex = 0;
	int secondIndex = 0;
	for ( int i = 1; i < NUM_METHODS; i++ ) {
		if ( methods[ i ].myScore > maxScore ) {
			secondIndex = maxIndex;
			maxIndex = i;
			maxScore = methods[ i ].myScore;
		}
		else if ( methods[ i ].myScore == maxScore ) {
			if ( methods[ i ].weighting > methods[ maxIndex ].weighting ) {
				secondIndex = maxIndex;
				maxIndex = i;
			}
			else {
				// the max index is unchanged
			}
		}
		methods[ i ].myLastMove = methods[ i ].thisMethod.nextMove();
	}
	currentMethod = maxIndex;

	if ( lastMethod != currentMethod ) {
		swap();
	}

	if ( lossStreak >= LOSS_STREAK_MAX ) {
		// switch algorithm
		lastMethod = currentMethod;
		currentMethod = secondIndex;
		methods[ maxIndex ].myScore = methods[ secondIndex ].myScore - LOSS_STREAK_PENALTY;
		lastMove = methods[ secondIndex ].myLastMove;
		swap();
	}
	else {
		lastMove = methods[ currentMethod ].myLastMove;
	}
	lastMethod = currentMethod;

	return lastMove;
}

/*-----[]----------------------------------------------------------------------*/
private void swap() {
	lossStreak = 0;
}

/*-----[]------------------------------------------------------------------------------*/
/*-------------------------------------------------------------------------------------*/
class MyMethod {

RoshamboPlayer thisMethod;

Queue myQueue = new Queue( 10 );

int myScore = myQueue.getSum();

int myLastMove = 0;

int weighting;

String name;

/**
 * ctor
 * @param p
 * @param weight
 */
public MyMethod( RoshamboPlayer p, int weight ) {
	thisMethod = p;
	weighting = weight;
	name = ( thisMethod.getPlayerId() );// + " (" + thisMethod.getAuthor() + ")");
}

/*-----[]----------------------------------------------------------------------*/
void updateScore() {
	myScore = myQueue.getSum();
}
}

/*-----[]------------------------------------------------------------------------------*/
/*-------------------------------------------------------------------------------------*/
class Queue {

TreeMap theData = new TreeMap();

int first = 0;

int last = 0;

int sum = 0;

int size = 0;

/*-----[]----------------------------------------------------------------------*/
Queue( int s ) {
	size = s;
}

/*-----[]----------------------------------------------------------------------*/
int getNext() {
	return 1;
}

/*-----[]----------------------------------------------------------------------*/
void add( int a ) {
	theData.put( new Integer( last++ ), new Integer( a ) );

	sum += a;

	if ( ( last - first ) > 10 ) {
		sum -= ( (Integer) ( theData.get( new Integer( first ) ) ) ).intValue();
		theData.remove( new Integer( first ) );
		first++;
	}
}

/*-----[]----------------------------------------------------------------------*/
int getSum() {
	return sum;
}
/*-----[]----------------------------------------------------------------------*/
}

/*-----[]------------------------------------------------------------------------------*/
/*-------------------------------------------------------------------------------------*/
class MyRandom implements RoshamboPlayer {

// random class

/**
 * ctor
 * @param aTrials
 */
public MyRandom( int aTrials ) {
	reset( aTrials );
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#nextMove()
 */
public int nextMove() {
	return Coin.flip();
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#storeMove(int, int)
 */
public void storeMove( int move, int aScore ) {
	// no-op
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
public void reset( int aTrials ) {
	// no-op
}

/**
 * @see com.anji.tournament.Player#getPlayerId()
 */
public String getPlayerId() {
	return "MUTO [MyRandom]";
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#getAuthor()
 */
public String getAuthor() {
	return "Andrew Wheat";
}
/*-----[]----------------------------------------------------------------------*/
}

/*-----[]------------------------------------------------------------------------------*/
/*-------------------------------------------------------------------------------------*/
class NotInLast implements RoshamboPlayer {

private int myLastMove = Coin.flip();

private int theirLastMove = Coin.flip();

/**
 * ctor
 * @param aTrials
 */
public NotInLast( int aTrials ) {
	reset( aTrials );
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
public void reset( int aTrials ) {
	// no-op
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#storeMove(int, int)
 */
public void storeMove( int move, int aScore ) {
	theirLastMove = move;
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#nextMove()
 */
public int nextMove() {
	if ( myLastMove == theirLastMove ) {
		myLastMove = Coin.flip();
	}
	else {
		myLastMove = 3 - ( myLastMove | theirLastMove );
	}

	return myLastMove;
}

/**
 * @see com.anji.tournament.Player#getPlayerId()
 */
public String getPlayerId() {
	return "MUTO [NotInLast]";
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#getAuthor()
 */
public String getAuthor() {
	return "Tristran Allwood/Andrew Wheat";
}
/*-----[]----------------------------------------------------------------------*/
}

/*-----[]------------------------------------------------------------------------------*/
/*-------------------------------------------------------------------------------------*/
/**
 * http://www.cs.unimaas.nl/~donkers/games/roshambo03/
 */
public class Tris2 implements RoshamboPlayer {

int[][][] matrix = new int[ 3 ][ 3 ][ 3 ];

int hisLastMove = Coin.flip();

int myLastMove = Coin.flip();

int myLastMoveBar1 = Coin.flip();

int[] pMoves;

int cMax;

int cSame;

int cPos;

/**
 * ctor
 * @param aTrials
 */
public Tris2( int aTrials ) {
	reset( aTrials );
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
public void reset( int aTrials ) {
	for ( int i = 0; i < 3; i++ ) {
		for ( int j = 0; j < 3; j++ ) {
			for ( int k = 0; k < 3; k++ ) {
				matrix[ k ][ j ][ i ] = 0;
			}
		}
	}

}

/**
 * @see com.anji.roshambo.RoshamboPlayer#storeMove(int, int)
 */
public void storeMove( int move, int aScore ) {
	matrix[ myLastMoveBar1 ][ hisLastMove ][ move ]++;
	hisLastMove = move;
	myLastMoveBar1 = myLastMove;
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#nextMove()
 */
public int nextMove() {
	pMoves = matrix[ myLastMove ][ hisLastMove ];

	cMax = pMoves[ 0 ];
	cPos = 0;
	cSame = 0;
	for ( int i = 1; i < 3; i++ ) {
		if ( cMax < pMoves[ i ] ) {
			cMax = pMoves[ i ];
			cPos = i;
			cSame = 0;
		}
		else if ( cMax == pMoves[ i ] ) {
			cSame++;
		}
	}
	if ( cSame == 1 ) {
		//the annoying case
		if ( pMoves[ 0 ] == pMoves[ 1 ] ) {
			myLastMove = Coin.flip( 0.5, 0.5 );
		}
		else if ( pMoves[ 1 ] == pMoves[ 2 ] ) {
			myLastMove = Coin.flip( 0, 0.5 );
		}
		else {
			myLastMove = Coin.flip( 0.5, 0 );
		}
	}
	else if ( cSame == 2 ) {
		//theya re all the same
		myLastMove = Coin.flip();
	}
	else {
		myLastMove = cPos;
	}
	//myLastMove = what i think they will do
	myLastMove++;
	myLastMove = myLastMove % 3;
	//      myLastMove++;
	//      myLastMove = myLastMove % 3;
	//myLastMove = what i wanna do
	return myLastMove;
}

/**
 * @see com.anji.tournament.Player#getPlayerId()
 */
public String getPlayerId() {
	return "MUTO [Tris2]";
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#getAuthor()
 */
public String getAuthor() {
	return "Tristan Allwood";
}
/*-----[]----------------------------------------------------------------------*/
}

/*-----[]------------------------------------------------------------------------------*/
/*-------------------------------------------------------------------------------------*/
/**
 * http://www.cs.unimaas.nl/~donkers/games/roshambo03/
 */
public class Tris3 implements RoshamboPlayer {

static final int HISTORY_LENGTH = 10;

// set to something between 0 and 7ish maybe

int myLastMoveBar1;

int hisLastMoveBar1;

int myLastMove;

int hisLastMove;

int cTrial;

HistoryState root;

History hist;

/**
 * ctor
 * @param t
 */
public Tris3( int t ) {
	reset( t );
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
public void reset( int aTrials ) {
	cTrial = 0;
	hist = new History();
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
public void storeMove( int move, int aScore ) {
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
	return "MUTO [Tris3]";
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#getAuthor()
 */
public String getAuthor() {
	return "Tristan Allwood";
}

/*-----[]------------------------------------------------------------------------------*/
/*-------------------------------------------------------------------------------------*/
class History {

int[][] cHistory = new int[ HISTORY_LENGTH ][ 2 ];

int[][] tmpHistory;

/*-----[]----------------------------------------------------------------------*/
void addPairToHistory( int mLM, int hLM ) {
	tmpHistory = new int[ HISTORY_LENGTH ][ 2 ];

	for ( int i = 0; i < cHistory.length - 1; i++ ) {
		tmpHistory[ i ] = cHistory[ i + 1 ];
	}

	tmpHistory[ tmpHistory.length - 1 ] = new int[] { mLM, hLM };
	cHistory = tmpHistory;
}

/*-----[]----------------------------------------------------------------------*/
int[][] getCHistory() {
	return cHistory;
}

}

/*-----[]------------------------------------------------------------------------------*/
/*-------------------------------------------------------------------------------------*/
class HistoryState {

int[][] history;

int future;

int freq;

HistoryState nextNode;

/**
 * @param inHistory
 * @param inFuture
 */
public HistoryState( int[][] inHistory, int inFuture ) {
	history = inHistory;
	future = inFuture;
	freq = 1;
}

/*-----[]----------------------------------------------------------------------*/
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

/*-----[]----------------------------------------------------------------------*/
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

/*-----[]----------------------------------------------------------------------*/
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
/*-----[]----------------------------------------------------------------------*/
}
}
/*-----[]------------------------------------------------------------------------------*/
/*-------------------------------------------------------------------------------------*/


/**
 * @see java.lang.Object#hashCode()
 */
public int hashCode() {
	return getPlayerId().hashCode();
}

}

