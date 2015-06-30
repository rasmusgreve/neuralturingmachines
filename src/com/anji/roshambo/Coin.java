package com.anji.roshambo;

import java.util.Random;

/**
 * http://www.cs.unimaas.nl/~donkers/games/roshambo03/
 */
public class Coin {

/**
 * flip biased coin: pr = probability of getting 1
 * @param pr probability of returning 1
 * @return 0 or 1
 */
public static int flip( double pr ) {
	double p = rand.nextDouble();
	if ( p <= pr )
		return 1;
	return 0;
}

/**
 * flip biased coin:
 * @param pr_rock = probability of getting ROCK
 * @param pr_paper = probability of getting PAPER
 * @return <code>RoshamboPlayer.ROCK</code>,<code>RoshamboPlayer.PAPER</code>, or
 * <code>RoshamboPlayer.SCISSORS</code>
 */
public static int flip( double pr_rock, double pr_paper ) {
	double p = rand.nextDouble();
	if ( p < pr_rock )
		return RoshamboPlayer.ROCK;
	if ( p < ( pr_rock + pr_paper ) )
		return RoshamboPlayer.PAPER;
	return RoshamboPlayer.SCISSORS;
}

/**
 * flip unbiased coin ROCK, PAPER or SCISSORS:
 * @return 0, 1, or 2
 */
public static int flip() {
	return rand.nextInt( 3 );
}

private static Random rand = new Random();

/**
 * @param aRand new random object
 */
public static void setRand( Random aRand ) {
	rand = aRand;
}
}
