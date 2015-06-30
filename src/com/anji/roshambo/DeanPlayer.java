package com.anji.roshambo;

/**
 * http://www.cs.unimaas.nl/~donkers/games/roshambo03/
 */
public class DeanPlayer implements RoshamboPlayer {

private final static String NAME = "Dean";

private static final int TT = 21;

private Ladder in = new Ladder(), ex = new Ladder();

private int mymv;

/**
 * @see com.anji.roshambo.RoshamboPlayer#getAuthor()
 */
public String getAuthor() {
	return "Mathijs de Boer";
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#reset(int)
 */
public void reset( int trials ) {
	in = new Ladder();
	ex = new Ladder();
	mymv = Coin.flip();
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#storeMove(int, int)
 */
public void storeMove( int move, int score ) {
	if ( in.walk( mymv, move, move ) > ex.walk( move, mymv, move ) )
		mymv = ( in.top + 1 ) % 3;
	else
		mymv = ( ex.top + 1 ) % 3;
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#nextMove()
 */
public int nextMove() {
	return mymv;
}

private class STnode {

private int p[] = new int[ 6 ];

int f[] = new int[ 6 ], t1, t2;

private double s[] = new double[ 6 ];

STnode node[] = new STnode[ 3 ];
}

private class Ladder {

private STnode shoots[] = new STnode[ TT ];

private int p[] = new int[ TT * 7 ], s1[] = new int[ TT * 7 ], s1i, s2i, s1s, s2s;

int top;

private float s2[] = new float[ TT * 7 ];

private Ladder() {
	shoots[ 0 ] = new STnode();
}

float walk( int m1, int m2, int t ) {
	if ( p[ s1i ] == t )
		++s1s;
	if ( p[ s2i ] == t )
		++s2s;
	for ( int x = 0; x < TT * 7; x++ ) {
		if ( p[ x ] > -1 ) {
			s1[ x ] += ( ( p[ x ] == t ) ? 1 : ( ( p[ x ] + 2 ) % 3 == t ) ? -1 : 0 );
			s2[ x ] = (float) ( ( s2[ x ] * 0.8 ) + ( ( p[ x ] == t ) ? 0.2
					: ( ( p[ x ] + 2 ) % 3 == t ) ? -0.2 : 0 ) );
			p[ x ] = -1;
		}
	}
	int i = 1;
	STnode n = shoots[ 0 ];
	while ( n != null && i < TT ) {
		if ( n.f[ n.t1 ] < ++n.f[ m1 ] )
			n.t1 = m1;
		if ( n.f[ n.t2 + 3 ] < ++n.f[ m2 + 3 ] )
			n.t2 = m2;
		if ( n.node[ m1 ] == null )
			n.node[ m1 ] = new STnode();
		STnode m = n.node[ m1 ];
		n = shoots[ i ];
		shoots[ i++ ] = m;
	}
	i = 0;
	while ( shoots[ i / 3 ].f[ shoots[ i / 3 ].t1 ] > 0 ) {
		p[ i ] = ( shoots[ i / 3 ].t1 + i ) % 3;
		p[ i * 2 ] = ( shoots[ i / 3 ].t2 + i++ ) % 3;
	}
	for ( int x = TT * 6; x < TT * 7; x++ )
		p[ x ] = Coin.flip();
	s1i = s2i = 0;
	for ( int x = 0; x < TT * 7; x++ ) {
		if ( p[ x ] > -1 ) {
			if ( s1[ x ] > s1[ s1i ] )
				s1i = x;
			if ( s2[ x ] > s2[ s2i ] )
				s2i = x;
		}
	}

	if ( s1s > s2s ) {
		top = p[ s1i ];
		return s1s;
	}

	top = p[ s2i ];
	return s2s;

}
}

/**
 * @see com.anji.roshambo.RoshamboPlayer#getPlayerId()
 */
public String getPlayerId() {
	return NAME;
}

/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	return NAME;
}

/**
 * @see com.anji.tournament.Player#reset()
 */
public void reset() {
	reset( 1 );
}

/**
 * @see java.lang.Object#hashCode()
 */
public int hashCode() {
	return getPlayerId().hashCode();
}

}
