package com.anji.roshambo;

import java.util.LinkedList;

/**
 * http://www.cs.unimaas.nl/~donkers/games/roshambo03/
 */
public class WizardexpPlayer implements RoshamboPlayer {

private final static int[] LENGTHS = new int[] { 1, 5, 10, 25, 50, 100, 250, 500 };

/**
 * @see com.anji.tournament.Player#reset()
 */
public void reset() {
	reset( idealSequence_.length );
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#reset(int)
 */
public void reset( int trials ) {
	myMove_ = 0;
	selfPredictor_ = new HistoryPredictor( trials );
	oppPredictor_ = new HistoryPredictor( trials );
	idealSequence_ = new int[ trials ];
	worstSequence_ = new int[ trials ];
	n_ = 0;

	predictorHistory_ = new int[ 7 ][ trials ];
	selectorChoice_ = new int[ LENGTHS.length ];
	selectorScore_ = new int[ LENGTHS.length ];
}

/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	return getPlayerId();
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#storeMove(int, int)
 */
public void storeMove( int move, int score ) {
	oppPredictor_.storeMove( myMove_, move );
	selfPredictor_.storeMove( move, myMove_ );
	idealSequence_[ n_ ] = beat( move );
	worstSequence_[ n_ ] = lose( move );

	for ( int i = 0; i < LENGTHS.length; ++i ) {
		int play = predictorHistory_[ selectorChoice_[ i ] ][ n_ ];
		if ( play == beat( move ) ) {
			++selectorScore_[ i ];
		}
		else if ( play == lose( move ) ) {
			--selectorScore_[ i ];
		}
	}

	++n_;
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#nextMove()
 */
public int nextMove() {
	int move;

	int myMove = selfPredictor_.nextMove();
	int opMove = oppPredictor_.nextMove();

	predictorHistory_[ 0 ][ n_ ] = myMove;
	predictorHistory_[ 1 ][ n_ ] = beat( myMove );
	predictorHistory_[ 2 ][ n_ ] = beat( beat( myMove ) );
	predictorHistory_[ 3 ][ n_ ] = opMove;
	predictorHistory_[ 4 ][ n_ ] = beat( opMove );
	predictorHistory_[ 5 ][ n_ ] = beat( beat( opMove ) );
	predictorHistory_[ 6 ][ n_ ] = Coin.flip();

	for ( int i = 0; i < LENGTHS.length; ++i ) {
		selectorChoice_[ i ] = selectPredictor( LENGTHS[ i ] );
	}
	int bestSelector = maxIndex( selectorScore_ );

	move = predictorHistory_[ selectorChoice_[ bestSelector ] ][ n_ ];
	myMove_ = move;
	return move;
}

/**
 * @see com.anji.tournament.Player#getPlayerId()
 */
public String getPlayerId() {
	return "WizardExp";
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#getAuthor()
 */
public String getAuthor() {
	return "Ian Glover";
}

private int beat( int play ) {
	return ( play + 1 ) % 3;
}

private int lose( int play ) {
	return ( play + 2 ) % 3;
}

private int selectPredictor( int length ) {
	int best = 0;
	int bestScore = -10000;

	for ( int i = 0; i < predictorHistory_.length; ++i ) {
		int score = 0;
		for ( int m = n_ - 1; m > n_ - length && m >= 0; --m ) {
			if ( predictorHistory_[ i ][ m ] == idealSequence_[ m ] ) {
				++score;
			}
			if ( predictorHistory_[ i ][ m ] == worstSequence_[ m ] ) {
				--score;
			}
		}
		if ( score > bestScore ) {
			bestScore = score;
			best = i;
		}
	}

	return best;
}

private class Node {

/**
 * Philip Tucker - public visibility for better performance
 */
public Node[][] children_;

/**
 * Philip Tucker - public visibility for better performance
 */
public int[] count_;

Node() {
	count_ = new int[] { 0, 0, 0 };
	children_ = new Node[ 3 ][ 3 ];
}
}

private class HistoryPredictor {

static final int MAX_DEPTH = 80;

/**
 * ctor
 * @param trials
 */
public HistoryPredictor( int trials ) {
	root_ = new Node();
	myHist_ = new int[ trials ];
	opHist_ = new int[ trials ];
}

Node root_;

int[] myHist_;

int[] opHist_;

/**
 * @return predicted next move
 */
public int nextMove() {
	Node node = find( root_, root_, n_ - 1 );
	return maxIndex( node.count_ );
}

/**
 * store previous move and result
 * @param myMove
 * @param opMove
 */
public void storeMove( int myMove, int opMove ) {
	myHist_[ n_ ] = myMove;
	opHist_[ n_ ] = opMove;
	add( root_, n_, n_ );
}

private void add( Node node, int current, int end ) {
	if ( current < 1 ) {
		return;
	}

	node.count_[ opHist_[ end ] ]++;

	if ( end - current > MAX_DEPTH ) {
		return;
	}

	int next = current - 1;
	if ( node.children_[ myHist_[ next ] ][ opHist_[ next ] ] == null ) {
		node.children_[ myHist_[ next ] ][ opHist_[ next ] ] = new Node();
	}

	add( node.children_[ myHist_[ next ] ][ opHist_[ next ] ], next, end );
}

private Node find( Node node, Node best, int current ) {
	int total = 0;
	for ( int i = 0; i < 3; ++i ) {
		total += node.count_[ i ];
	}

	if ( 0 == total ) {
		return best;
	}

	if ( node.children_[ myHist_[ current ] ][ opHist_[ current ] ] == null ) {
		return node;
	}
	return find( node.children_[ myHist_[ current ] ][ opHist_[ current ] ], node, current - 1 );
}
}

/**
 * Philip Tucker - protected visibility for better performance
 * @param array
 * @return result
 */
protected int maxIndex( int[] array ) {
	LinkedList candidates = new LinkedList();
	int max = array[ 0 ];
	for ( int i = 0; i < array.length; ++i ) {
		if ( array[ i ] == max ) {
			candidates.add( new Integer( i ) );
		}
		else if ( array[ i ] > max ) {
			candidates.clear();
			candidates.add( new Integer( i ) );
			max = array[ i ];
		}
	}
	int len = candidates.size();
	int i = 0;
	for ( i = 0; i < len - 1; ++i ) {
		double p = 1.0 / ( len - i );
		if ( Coin.flip( p ) == 0 ) {
			break;
		}
	}

	int index = ( ( (Integer) candidates.get( i ) ).intValue() );
	return index;
}

int myMove_;

HistoryPredictor oppPredictor_;

HistoryPredictor selfPredictor_;

int[] idealSequence_ = new int[ 0 ];

int[] worstSequence_;

int n_;

int[][] predictorHistory_;

int[] selectorChoice_;

int[] selectorScore_;

/**
 * @see java.lang.Object#hashCode()
 */
public int hashCode() {
	return getPlayerId().hashCode();
}
}
