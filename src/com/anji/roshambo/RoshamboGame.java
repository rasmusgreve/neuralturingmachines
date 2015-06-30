package com.anji.roshambo;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.anji.Copyright;
import com.anji.tournament.Game;
import com.anji.tournament.GameConfiguration;
import com.anji.tournament.GameResults;
import com.anji.tournament.PlayerResults;
import com.anji.tournament.ScoringWeights;
import com.anji.util.Configurable;
import com.anji.util.Randomizer;

/**
 * http://www.cs.unimaas.nl/~donkers/games/roshambo03/ Modified by Philip Tucker to implement
 * <code>Game</code> interface. Renamed from RoshamboGame.
 */
public class RoshamboGame implements Game, Configurable {

private final static Logger logger = Logger.getLogger( RoshamboGame.class );

private GameConfiguration gameConfig = GameConfiguration.DEFAULT;

/**
 * ctor - should call <code>init(Properties)</code> after this ctor
 */
public RoshamboGame() {
	super();
}

/**
 * @param aConfig
 */
public RoshamboGame( GameConfiguration aConfig ) {
	gameConfig = aConfig;
}

/**
 * @see com.anji.tournament.Game#play(com.anji.tournament.PlayerResults,
 * com.anji.tournament.PlayerResults)
 */
public GameResults play( PlayerResults playerOneResults, PlayerResults playerTwoResults ) {
	long startMillis = System.currentTimeMillis();
	GameResults gameResults = new GameResults();
	RoshamboPlayer p1 = (RoshamboPlayer) playerOneResults.getPlayer();
	RoshamboPlayer p2 = (RoshamboPlayer) playerTwoResults.getPlayer();

	// single trial; score is -1, 0, or 1; we do not update score, since W-L-T fully captures
	// results
	int playerOneScore = play( p1, p2, 1 );
	switch ( playerOneScore ) {
		case -1:
			gameResults.incrementPlayer1Losses( 1 );
			break;
		case 0:
			gameResults.incrementTies( 1 );
			break;
		case 1:
			gameResults.incrementPlayer1Wins( 1 );
			break;
		default:
			throw new IllegalStateException( "invalid result for single trial: " + playerOneScore );
	}
	playerOneResults.getResults().increment( gameResults.getPlayer1Stats() );
	playerTwoResults.getResults().increment( gameResults.getPlayer2Stats() );

	if ( gameConfig.doLogResults() )
		logger.info( new StringBuffer( name ).append( ": " ).append( p1 ).append( " vs " ).append(
				p2 ).append( ": " ).append( playerOneScore ).append(
				System.currentTimeMillis() - startMillis ).append( " ms" ).toString() );

	return gameResults;
}

/**
 * @param contestant
 * @param opponent
 * @param trials
 * @return total wins - losses of subject p
 */
private int play( RoshamboPlayer contestant, RoshamboPlayer opponent, int trials ) {
	int totscore = 0;

	if ( gameConfig.doResetPlayers() ) {
		contestant.reset( trials );
		opponent.reset( trials );
	}
	for ( int i = 0; i < trials; i++ ) {
		int pm = contestant.nextMove();
		int qm = opponent.nextMove();
		logger.debug( contestant.toString() + ": roshambo move=" + pm );
		logger.debug( opponent.toString() + ": roshambo move=" + qm );
		int score = 0;
		if ( pm != qm ) {
			if ( ( pm == RoshamboPlayer.PAPER && qm == RoshamboPlayer.ROCK )
					|| ( pm == RoshamboPlayer.ROCK && qm == RoshamboPlayer.SCISSORS )
					|| ( pm == RoshamboPlayer.SCISSORS && qm == RoshamboPlayer.PAPER ) )
				score = 1;
			else
				score = -1;
		}
		contestant.storeMove( qm, score );
		opponent.storeMove( pm, -score );
		totscore += score;
	}
	return totscore;
}

// test suite ....
// usage: java RoshamboGame player1 player2 trials

/**
 * @param args
 * @throws Exception
 */
public static void main( String args[] ) throws Exception {
	java.util.Properties p = new java.util.Properties();
	p.load( ClassLoader.getSystemResourceAsStream( "roshambo.properties" ) );
	PropertyConfigurator.configure( p );

	System.out.println( Copyright.STRING );
	if ( args.length < 2 )
		System.err
				.println( "usage: <cmd> <roshambo-player1-class> <roshambo-player2-class> <trials>" );

	RoshamboGame m = new RoshamboGame( args[ 0 ], args[ 1 ] );
	if ( !m.isLoaded() ) {
		System.out.println( m.getError() );
		return;
	}
	System.out.println( m.describe() );
	int score = m.match( Integer.parseInt( args[ 2 ] ) );
	System.out.println( "Score: " + score );
}

/**
 * 
 * @param player1
 * @param player2
 */
public RoshamboGame( String player1, String player2 ) {

	loaded = false;
	loaderror = "";

	p = loadPlayer( player1 );
	if ( p == null )
		return;

	q = loadPlayer( player2 );
	if ( q == null )
		return;

	loaded = true;
	loaderror = "";
}

/**
 * @return description
 */
public String describe() {
	if ( !loaded )
		return "Players not loaded.";
	return p.getPlayerId() + " (" + p.getAuthor() + ") vs. " + q.getPlayerId() + " ("
			+ q.getAuthor() + ")";
}

/**
 * @param trials
 * @return subject one score
 */
public int match( int trials ) {
	if ( !loaded )
		return -1;

	return play( p, q, trials );
}

/**
 * @return true if players loaded
 */
public boolean isLoaded() {
	return loaded;
}

/**
 * 
 * @return error string
 */
public String getError() {
	return loaderror;
}

private RoshamboPlayer loadPlayer( String playerName ) {
	loaderror = "";
	RoshamboPlayer player;
	try {
		player = (RoshamboPlayer) Class.forName( playerName ).newInstance();
	}
	catch ( ClassNotFoundException e ) {
		loaderror = "Cannot find class " + playerName;
		return null;
	}
	catch ( Exception e ) {
		loaderror = "Error loading class " + playerName + " (" + e + ")";
		return null;
	}

	return player;
}

private RoshamboPlayer p, q;

private String loaderror;

private boolean loaded;

private String name = "roshambo";

/**
 * @see com.anji.tournament.Game#requiredPlayerClass()
 */
public Class requiredPlayerClass() {
	return RoshamboPlayer.class;
}

/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	return name;
}

/**
 * @see com.anji.tournament.Game#getMaxScore(com.anji.tournament.ScoringWeights)
 */
public int getMaxScore( ScoringWeights aWeights ) {
	return aWeights.getWinValue();
}

/**
 * @see com.anji.tournament.Game#getMinScore(com.anji.tournament.ScoringWeights)
 */
public int getMinScore( ScoringWeights aWeights ) {
	return aWeights.getLossValue();
}

/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( com.anji.util.Properties props ) throws Exception {
	gameConfig = (GameConfiguration) props.singletonObjectProperty( GameConfiguration.class );
	name += ( " " + props.getName() );
	Randomizer randomizer = (Randomizer) props.singletonObjectProperty( Randomizer.class );
	Coin.setRand( randomizer.getRand() );
}

}
