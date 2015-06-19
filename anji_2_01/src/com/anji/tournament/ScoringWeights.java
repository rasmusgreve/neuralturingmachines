/*
 * Created on Apr 10, 2005 by Philip Tucker
 */
package com.anji.tournament;

import com.anji.util.Configurable;
import com.anji.util.Properties;

/**
 * @author Philip Tucker
 */
public class ScoringWeights implements Configurable {

private static final String WIN_VALUE_KEY = "win.value";

private static final String LOSS_VALUE_KEY = "loss.value";

private static final String TIE_VALUE_KEY = "tie.value";

private static final String RAWSCORE_VALUE_KEY = "rawscore.value";

private final static int DEFAULT_WIN_VALUE = 5;

private final static int DEFAULT_LOSS_VALUE = 0;

private final static int DEFAULT_TIE_VALUE = 2;

private final static int DEFAULT_RAWSCORE_VALUE = 0;

private int winValue = DEFAULT_WIN_VALUE;

private int lossValue = DEFAULT_LOSS_VALUE;

private int tieValue = DEFAULT_TIE_VALUE;

private float rawScoreValue = DEFAULT_RAWSCORE_VALUE;

/**
 * @param aWinValue must be >= 0, >=<code>aLossValue</code>, and >=<code>aTieValue</code>
 * @param aLossValue must be <=<code>aTieValue</code> and <=<code>aWinValue</code>
 * @param aTieValue must be <=<code>aWinValue</code> and >=<code>aLossValue</code>
 * @param aRawScoreValue
 */
public ScoringWeights( int aWinValue, int aLossValue, int aTieValue, float aRawScoreValue ) {
	if ( ( ( aLossValue > aTieValue ) || ( aTieValue > aWinValue ) ) || ( aWinValue < 0 ) )
		throw new IllegalArgumentException( "illegal W-L-T weights: " + aWinValue + "-"
				+ aLossValue + "-" + aTieValue );
	winValue = aWinValue;
	lossValue = aLossValue;
	tieValue = aTieValue;
	rawScoreValue = aRawScoreValue;
}

/**
 * default constructor
 */
public ScoringWeights() {
	this( DEFAULT_WIN_VALUE, DEFAULT_LOSS_VALUE, DEFAULT_TIE_VALUE, DEFAULT_RAWSCORE_VALUE );
}

/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties props ) {
	winValue = props.getIntProperty( WIN_VALUE_KEY, DEFAULT_WIN_VALUE );
	lossValue = props.getIntProperty( LOSS_VALUE_KEY, DEFAULT_LOSS_VALUE );
	tieValue = props.getIntProperty( TIE_VALUE_KEY, DEFAULT_TIE_VALUE );
	rawScoreValue = props.getFloatProperty( RAWSCORE_VALUE_KEY, DEFAULT_RAWSCORE_VALUE );
}

/**
 * @return loss weight
 */
public int getLossValue() {
	return lossValue;
}

/**
 * @return tie weight
 */
public int getTieValue() {
	return tieValue;
}

/**
 * @return win weight
 */
public int getWinValue() {
	return winValue;
}

/**
 * @param results
 * @return weighted score
 */
public float calculateTotalScore( PlayerStats results ) {
	return ( results.getWins() * winValue ) + ( results.getLosses() * lossValue )
			+ ( results.getTies() * tieValue ) + ( results.getRawScore() * rawScoreValue );
}

/**
 * @param results
 * @return average weighted score per game
 */
public float calculateAverageScore( PlayerStats results ) {
	return calculateTotalScore( results )
			/ ( results.getWins() + results.getLosses() + results.getTies() );
}

/**
 * @return raw score weight
 */
public float getRawScoreValue() {
	return rawScoreValue;
}

/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	StringBuffer result = new StringBuffer();
	result.append( winValue ).append( "/" ).append( lossValue ).append( "/" ).append( tieValue )
			.append( "/" ).append( rawScoreValue );
	return result.toString();
}

}
