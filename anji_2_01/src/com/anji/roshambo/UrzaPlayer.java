package com.anji.roshambo;

/**
 * Urza roshambo bot by Martijn Muurman and Enno Peters 6-2002
 * @author Martijn Muurman and Enno Peters
 */
public class UrzaPlayer implements RoshamboPlayer {

private final static String NAME = "Urza";

/**
 * Interne structuur benodigd voor het opbouwen van een 'historie-boom' Elke van deze nodes is
 * een vertakking in deze boom en heeft maximaal 9 vertakkingen naar andere nodes
 */
private class SuperNode {

SuperNode leftleft;

SuperNode leftmiddle;

SuperNode leftright;

SuperNode middleleft;

SuperNode middlemiddle;

SuperNode middleright;

SuperNode rightleft;

SuperNode rightmiddle;

SuperNode rightright;

int oppr, opps, oppp, mypr, myps, mypp;

/* Initialisatie */
SuperNode() {
	oppr = 0;
	opps = 0;
	oppp = 0;
	mypr = 0;
	mypp = 0;
	myps = 0;
}
}

/* constantes staande voor zelf en opponent */
private static final int me = 0;

private static final int op = 1;

/* zoekdiepte in historie boom */
private static final int depth = 7;

/* Huidig aantal gevoerde stratiegien */
private static final int strategies = 28;

/* Rootnode historieboom */
SuperNode root;

/* huidige beurt */
int cur_move;

/* historie lijsten */
int my_hist[];

int op_hist[];

/* de ideaal gespeelde historie voor score berekening */
int ideal_hist[];

/* het slecht mogelijk gespeelde eveneens voor score berekening */
int worst_hist[];

/* de verschillende predicties van de strategien */
int laststratpred[];

/* histories van alle strategien */
int strathist[][];

/* hoe vaak ik r p en s heb gespeeld */
int myrcount;

int mypcount;

int myscount;

/* hoe vaak de opponent r p en s heeft gespeeld */
int opprcount;

int opppcount;

int oppscount;

/**
 * initialisatie ..arrays maken alles op 0 etc
 * @param trials
 */
public void reset( int trials ) {
	my_hist = new int[ trials ];
	op_hist = new int[ trials ];
	ideal_hist = new int[ trials ];
	worst_hist = new int[ trials ];
	laststratpred = new int[ strategies ];
	strathist = new int[ strategies ][ trials ];
	myrcount = 0;
	mypcount = 0;
	myscount = 0;
	opprcount = 0;
	opppcount = 0;
	oppscount = 0;
	cur_move = 0;
	pi_index = 0;
	root = new SuperNode();
}

/**
 * @see com.anji.tournament.Player#getPlayerId()
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
 * @see com.anji.roshambo.RoshamboPlayer#getAuthor()
 */
public String getAuthor() {
	return "Martijn Muurman & Enno Peters";
}

/**
 * laatst gespeelde move,ideale historie en slechte historie opslaan
 * @param move
 * @param score
 */
public void storeMove( int move, int score ) {
	op_hist[ cur_move ] = move;
	ideal_hist[ cur_move ] = ( op_hist[ cur_move ] + 1 ) % 3;
	worst_hist[ cur_move ] = ( op_hist[ cur_move ] + 2 ) % 3;
	if ( move == ROCK )
		opprcount++;
	else if ( move == PAPER )
		opppcount++;
	else
		oppscount++;
	cur_move++;
}

/*
 * geef de score van strategie 'index' over een zeker periode door de strategie te vergelijken
 * met de beste en slechtse strategie die gespeeld had kunnen worden.
 */
private int calcStratScore( int index, int period ) {
	int value = 0;
	for ( int i = cur_move - period; i < cur_move; i++ ) {
		if ( strathist[ index ][ i ] == ideal_hist[ i ] )
			value++;
		if ( strathist[ index ][ i ] == worst_hist[ i ] )
			value--;
	}
	return value;
}

/*
 * bepaald de te spelen strategie door scores van strategien te berekenen voor de laatse
 * 1,3,5,10,15,50,100,250 en 500 beurten. Hierdoor is de prestatie van de laatste tijd
 * belangrijker dan die van 700 beurten geleden. De functie geeft het nummer door van de
 * strategie met de hoogste score
 */

private int MetaStrategie() {
	int beststrat = 12; // 12 = random
	int maxscore = -9999;
	int curscore = -10000;
	for ( int i = 0; i < strategies; i++ ) {
		if ( cur_move > 0 )
			curscore = calcStratScore( i, 1 );
		if ( cur_move > 2 )
			curscore += calcStratScore( i, 3 );
		if ( cur_move > 4 )
			curscore += calcStratScore( i, 5 );
		if ( cur_move > 9 )
			curscore += calcStratScore( i, 10 );
		if ( cur_move > 24 )
			curscore += calcStratScore( i, 25 );
		if ( cur_move > 49 )
			curscore += calcStratScore( i, 50 );
		if ( cur_move > 99 )
			curscore += calcStratScore( i, 100 );
		if ( cur_move > 249 )
			curscore += calcStratScore( i, 250 );
		if ( cur_move > 499 )
			curscore += calcStratScore( i, 500 );

		if ( curscore > maxscore ) {
			maxscore = curscore;
			beststrat = i;
		}
	}
	return beststrat; // 0 de eerste keer
}

/*
 * zoekt in de historie van beide spelers naar de langste strings van rps die gelijk zijn aan de
 * huidige situatie. Geeft dan vervolgens aan de hand 'mode' terug wat wij of de tegenstander
 * toen gespeeld hebben.
 */
private int history_match_both( int mode ) {
	int seq_size = 0;
	int seq_idx = -1;
	if ( cur_move > 1 ) {
		for ( int i = cur_move - 2; i >= 0; i-- ) {
			if ( ( i < seq_size ) || ( seq_size > 50 ) )
				break;
			int j = 0;
			while ( ( j <= i ) && ( my_hist[ i - j ] == my_hist[ cur_move - 1 - j ] )
					&& ( op_hist[ i - j ] == op_hist[ cur_move - 1 - j ] ) ) {
				j++;
				if ( j > seq_size ) {
					seq_size = j;
					seq_idx = i;
				}
			}
		}
	}

	int result = 0;
	if ( seq_idx == -1 )
		result = Coin.flip();
	else {
		if ( mode == op )
			result = ( op_hist[ seq_idx ] + 1 ) % 3;
		else
			result = ( my_hist[ seq_idx ] + 1 ) % 3;
	}
	return result;
}

/* PI tabel maakt het mogelijk simpele pi bots te voorspellen */
int pi_index;

int pi_table[] = { 3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5, 8, 9, 7, 9, 3, 2, 3, 8, 4, 6, 2, 6, 4, 3,
		3, 8, 3, 2, 7, 9, 5, 0, 2, 8, 8, 4, 1, 9, 7, 1, 6, 9, 3, 9, 9, 3, 7, 5, 1, 0, 5, 8, 2, 0,
		9, 7, 4, 9, 4, 4, 5, 9, 2, 3, 0, 7, 8, 1, 6, 4, 0, 6, 2, 8, 6, 2, 0, 8, 9, 9, 8, 6, 2, 8,
		0, 3, 4, 8, 2, 5, 3, 4, 2, 1, 1, 7, 0, 6, 7, 9, 8, 2, 1, 4, 8, 0, 8, 6, 5, 1, 3, 2, 8, 2,
		3, 0, 6, 6, 4, 7, 0, 9, 3, 8, 4, 4, 6, 0, 9, 5, 5, 0, 5, 8, 2, 2, 3, 1, 7, 2, 5, 3, 5, 9,
		4, 0, 8, 1, 2, 8, 4, 8, 1, 1, 1, 7, 4, 5, 0, 2, 8, 4, 1, 0, 2, 7, 0, 1, 9, 3, 8, 5, 2, 1,
		1, 0, 5, 5, 5, 9, 6, 4, 4, 6, 2, 2, 9, 4, 8, 9, 5, 4, 9, 3, 0, 3, 8, 1, 9, 6, 4, 4, 2, 8,
		8, 1, 0, 9, 7, 5, 6, 6, 5, 9, 3, 3, 4, 4, 6, 1, 2, 8, 4, 7, 5, 6, 4, 8, 2, 3, 3, 7, 8, 6,
		7, 8, 3, 1, 6, 5, 2, 7, 1, 2, 0, 1, 9, 0, 9, 1, 4, 5, 6, 4, 8, 5, 6, 6, 9, 2, 3, 4, 6, 0,
		3, 4, 8, 6, 1, 0, 4, 5, 4, 3, 2, 6, 6, 4, 8, 2, 1, 3, 3, 9, 3, 6, 0, 7, 2, 6, 0, 2, 4, 9,
		1, 4, 1, 2, 7, 3, 7, 2, 4, 5, 8, 7, 0, 0, 6, 6, 0, 6, 3, 1, 5, 5, 8, 8, 1, 7, 4, 8, 8, 1,
		5, 2, 0, 9, 2, 0, 9, 6, 2, 8, 2, 9, 2, 5, 4, 0, 9, 1, 7, 1, 5, 3, 6, 4, 3, 6, 7, 8, 9, 2,
		5, 9, 0, 3, 6, 0, 0, 1, 1, 3, 3, 0, 5, 3, 0, 5, 4, 8, 8, 2, 0, 4, 6, 6, 5, 2, 1, 3, 8, 4,
		1, 4, 6, 9, 5, 1, 9, 4, 1, 5, 1, 1, 6, 0, 9, 4, 3, 3, 0, 5, 7, 2, 7, 0, 3, 6, 5, 7, 5, 9,
		5, 9, 1, 9, 5, 3, 0, 9, 2, 1, 8, 6, 1, 1, 7, 3, 8, 1, 9, 3, 2, 6, 1, 1, 7, 9, 3, 1, 0, 5,
		1, 1, 8, 5, 4, 8, 0, 7, 4, 4, 6, 2, 3, 7, 9, 9, 6, 2, 7, 4, 9, 5, 6, 7, 3, 5, 1, 8, 8, 5,
		7, 5, 2, 7, 2, 4, 8, 9, 1, 2, 2, 7, 9, 3, 8, 1, 8, 3, 0, 1, 1, 9, 4, 9, 1, 2, 9, 8, 3, 3,
		6, 7, 3, 3, 6, 2, 4, 4, 0, 6, 5, 6, 6, 4, 3, 0, 8, 6, 0, 2, 1, 3, 9, 4, 9, 4, 6, 3, 9, 5,
		2, 2, 4, 7, 3, 7, 1, 9, 0, 7, 0, 2, 1, 7, 9, 8, 6, 0, 9, 4, 3, 7, 0, 2, 7, 7, 0, 5, 3, 9,
		2, 1, 7, 1, 7, 6, 2, 9, 3, 1, 7, 6, 7, 5, 2, 3, 8, 4, 6, 7, 4, 8, 1, 8, 4, 6, 7, 6, 6, 9,
		4, 0, 5, 1, 3, 2, 0, 0, 0, 5, 6, 8, 1, 2, 7, 1, 4, 5, 2, 6, 3, 5, 6, 0, 8, 2, 7, 7, 8, 5,
		7, 7, 1, 3, 4, 2, 7, 5, 7, 7, 8, 9, 6, 0, 9, 1, 7, 3, 6, 3, 7, 1, 7, 8, 7, 2, 1, 4, 6, 8,
		4, 4, 0, 9, 0, 1, 2, 2, 4, 9, 5, 3, 4, 3, 0, 1, 4, 6, 5, 4, 9, 5, 8, 5, 3, 7, 1, 0, 5, 0,
		7, 9, 2, 2, 7, 9, 6, 8, 9, 2, 5, 8, 9, 2, 3, 5, 4, 2, 0, 1, 9, 9, 5, 6, 1, 1, 2, 1, 2, 9,
		0, 2, 1, 9, 6, 0, 8, 6, 4, 0, 3, 4, 4, 1, 8, 1, 5, 9, 8, 1, 3, 6, 2, 9, 7, 7, 4, 7, 7, 1,
		3, 0, 9, 9, 6, 0, 5, 1, 8, 7, 0, 7, 2, 1, 1, 3, 4, 9, 9, 9, 9, 9, 9, 8, 3, 7, 2, 9, 7, 8,
		0, 4, 9, 9, 5, 1, 0, 5, 9, 7, 3, 1, 7, 3, 2, 8, 1, 6, 0, 9, 6, 3, 1, 8, 5, 9, 5, 0, 2, 4,
		4, 5, 9, 4, 5, 5, 3, 4, 6, 9, 0, 8, 3, 0, 2, 6, 4, 2, 5, 2, 2, 3, 0, 8, 2, 5, 3, 3, 4, 4,
		6, 8, 5, 0, 3, 5, 2, 6, 1, 9, 3, 1, 1, 8, 8, 1, 7, 1, 0, 1, 0, 0, 0, 3, 1, 3, 7, 8, 3, 8,
		7, 5, 2, 8, 8, 6, 5, 8, 7, 5, 3, 3, 2, 0, 8, 3, 8, 1, 4, 2, 0, 6, 1, 7, 1, 7, 7, 6, 6, 9,
		1, 4, 7, 3, 0, 3, 5, 9, 8, 2, 5, 3, 4, 9, 0, 4, 2, 8, 7, 5, 5, 4, 6, 8, 7, 3, 1, 1, 5, 9,
		5, 6, 2, 8, 6, 3, 8, 8, 2, 3, 5, 3, 7, 8, 7, 5, 9, 3, 7, 5, 1, 9, 5, 7, 7, 8, 1, 8, 5, 7,
		7, 8, 0, 5, 3, 2, 1, 7, 1, 2, 2, 6, 8, 0, 6, 6, 1, 3, 0, 0, 1, 9, 2, 7, 8, 7, 6, 6, 1, 1,
		1, 9, 5, 9, 0, 9, 2, 1, 6, 4, 2, 0, 1, 9, 8, 9, 3, 8, 0, 9, 5, 2, 5, 7, 2, 0, 1, 0, 6, 5,
		4, 8, 5, 8, 6, 3, 2, 7, 8, 8, 6, 5, 9, 3, 6, 1, 5, 3, 3, 8, 1, 8, 2, 7, 9, 6, 8, 2, 3, 0,
		3, 0, 1, 9, 5, 2, 0, 3, 5, 3, 0, 1, 8, 5, 2, 9, 6, 8, 9, 9, 5, 7, 7, 3, 6, 2, 2, 5, 9, 9,
		4, 1, 3, 8, 9, 1, 2, 4, 9, 7, 2, 1, 7, 7, 5, 2, 8, 3, 4, 7, 9, 1, 3, 1, 5, 1, 5, 5, 7, 4,
		8, 5, 7, 2, 4, 2, 4, 5, 4, 1, 5, 0, 6, 9, 5, 9, 5, 0, 8, 2, 9, 5, 3, 3, 1, 1, 6, 8, 6, 1,
		7, 2, 7, 8, 5, 5, 8, 8, 9, 0, 7, 5, 0, 9, 8, 3, 8, 1, 7, 5, 4, 6, 3, 7, 4, 6, 4, 9, 3, 9,
		3, 1, 9, 2, 5, 5, 0, 6, 0, 4, 0, 0, 9, 2, 7, 7, 0, 1, 6, 7, 1, 1, 3, 9, 0, 0, 9, 8, 4, 8,
		8, 2, 4, 0, 1 };

/* returned het volgende element uit de pi tabel */
private int beat_Pi() {
	int result = ( pi_table[ pi_index ] + 1 ) % 3;
	pi_index++;
	while ( pi_table[ pi_index ] == 0 )
		pi_index++;
	pi_index %= 1200;
	return ( result );
}

/* vergelijkbaar met PI maar nu voor een de bruijn sequence */
int db_table[] = /* De Bruijn sequence: */
{ 1, 0, 2, 0, 0, 2, 0, 2, 0, 1, 1, 0, 0, 2, 2, 1, 0, 0, 1, 1, 2, 2, 0, 0, 1, 2, 1, 0, 2, 2, 2,
		2, 0, 1, 2, 0, 2, 2, 0, 2, 1, 1, 2, 1, 1, 0, 1, 1, 1, 2, 0, 0, 0, 0, 2, 1, 0, 1, 0, 1, 2,
		2, 1, 2, 0, 1, 0, 0, 0, 1, 0, 2, 1, 2, 1, 2, 2, 2, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 1, 0, 2,
		2, 2, 0, 0, 2, 2, 0, 2, 0, 1, 0, 1, 1, 0, 2, 1, 1, 2, 2, 2, 2, 1, 1, 1, 2, 0, 1, 2, 2, 1,
		2, 0, 0, 0, 1, 0, 0, 0, 0, 2, 0, 2, 2, 1, 0, 0, 1, 2, 1, 2, 2, 0, 1, 1, 2, 1, 1, 0, 0, 2,
		1, 0, 1, 2, 0, 2, 1, 2, 1, 0, 2, 1, 1, 2, 0, 0, 1, 0, 1, 2, 2, 0, 1, 0, 0, 2, 0, 1, 2, 0,
		1, 1, 2, 1, 1, 1, 1, 0, 2, 0, 2, 1, 0, 2, 2, 0, 2, 2, 2, 2, 0, 0, 0, 1, 2, 1, 2, 2, 2, 1,
		1, 0, 1, 1, 0, 0, 0, 0, 2, 1, 2, 0, 2, 0, 0, 2, 2, 1, 0, 0, 1, 1, 1, 2, 2, 1, 2, 1, 0, 1,
		0, 2, 1, 0, 1, 0, 2, 0, 2, 0, 0, 1, 2, 2, 2, 0, 2, 1, 0, 0, 1, 1, 1, 2, 2, 1, 1, 0, 2, 2,
		0, 0, 0, 2, 2, 2, 2, 1, 2, 2, 0, 1, 2, 0, 0, 2, 0, 1, 1, 2, 1, 2, 1, 1, 1, 1, 0, 0, 2, 1,
		2, 0, 1, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 2, 1, 0, 2, 1, 1, 2, 0, 2, 2, 2, 2, 1, 1, 1, 1, 0,
		0, 2, 0, 2, 2, 2, 1, 2, 1, 0, 2, 1, 0, 0, 0, 0, 2, 1, 1, 2, 2, 1, 0, 1, 0, 0, 1, 1, 1, 2,
		1, 1, 0, 1, 2, 2, 2, 2, 0, 0, 1, 2, 0, 2, 0, 1, 2, 1, 2, 0, 1, 0, 1, 1, 2, 0, 0, 0, 1, 0,
		2, 2, 0, 2, 1, 2, 2, 0, 1, 1, 0, 2, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 2, 1, 1, 0, 0, 1, 1, 1,
		1, 0, 2, 0, 2, 1, 2, 0, 2, 2, 1, 2, 2, 2, 1, 1, 1, 2, 1, 2, 1, 0, 0, 2, 0, 1, 1, 0, 1, 0,
		2, 1, 0, 2, 2, 2, 2, 0, 2, 0, 0, 2, 2, 0, 0, 1, 2, 2, 1, 0, 1, 1, 2, 0, 1, 2, 1, 1, 2, 2,
		0, 1, 0, 1, 2, 2, 2, 0, 2, 0, 0, 2, 0, 2, 1, 2, 2, 2, 2, 0, 0, 0, 0, 2, 2, 1, 0, 0, 0, 1,
		2, 0, 1, 2, 1, 2, 0, 0, 1, 0, 2, 0, 1, 0, 0, 2, 1, 0, 1, 2, 2, 1, 1, 2, 0, 2, 2, 2, 1, 2,
		1, 0, 2, 2, 0, 1, 1, 0, 2, 1, 1, 0, 0, 1, 1, 2, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 2,
		1, 0, 0, 0, 0, 1, 1, 0, 2, 1, 2, 1, 2, 2, 2, 0, 0, 1, 2, 0, 1, 0, 1, 2, 1, 1, 2, 2, 0, 2,
		0, 2, 1, 1, 0, 0, 1, 0, 2, 0, 0, 2, 0, 1, 1, 2, 0, 2, 2, 1, 1, 1, 1, 0, 1, 0, 0, 2, 2, 2,
		2, 1, 2, 0, 0, 0, 2, 1, 0, 2, 2, 0, 1, 2, 2, 1, 0, 2, 1, 0, 1, 0, 1, 1, 1, 1, 2, 1, 1, 0,
		1, 2, 1, 2, 2, 2, 2, 1, 2, 0, 0, 0, 1, 1, 2, 0, 2, 0, 2, 1, 0, 0, 0, 0, 2, 0, 0, 1, 0, 0,
		2, 2, 2, 0, 0, 2, 1, 1, 2, 2, 0, 1, 2, 0, 1, 1, 0, 0, 1, 2, 2, 1, 1, 1, 0, 2, 0, 1, 0, 2,
		2, 0, 2, 2, 1, 0, 2, 1, 2, 2, 2, 1, 0, 1, 0, 2, 2, 1, 2, 0, 2, 1, 0, 2, 0, 0, 0, 0, 1, 2,
		1, 0, 0, 2, 0, 2, 2, 0, 1, 0, 1, 1, 2, 1, 1, 0, 0, 1, 0, 0, 0, 2, 1, 1, 2, 0, 0, 2, 2, 2,
		2, 0, 0, 1, 1, 1, 0, 2, 1, 2, 1, 2, 2, 1, 1, 1, 1, 2, 2, 0, 2, 0, 1, 2, 0, 1, 1, 0, 1, 1,
		0, 0, 1, 1, 0, 1, 2, 0, 1, 2, 1, 2, 2, 1, 0, 0, 2, 0, 2, 1, 0, 1, 0, 2, 2, 0, 1, 1, 2, 1,
		0, 2, 0, 0, 1, 0, 1, 1, 1, 2, 2, 2, 2, 1, 2, 0, 2, 2, 1, 1, 2, 0, 0, 2, 1, 2, 1, 1, 1, 1,
		0, 2, 1, 1, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 1, 2, 2, 0, 2, 0, 2, 2, 0, 2, 1, 1, 2, 0, 2, 0,
		0, 1, 1, 1, 0, 0, 1, 2, 1, 1, 0, 1, 1, 0, 2, 2, 0, 0, 2, 2, 1, 1, 1, 1, 2, 1, 2, 1, 0, 2,
		0, 2, 2, 2, 2, 1, 2, 0, 0, 0, 1, 0, 0, 2, 1, 0, 0, 0, 0, 2, 0, 1, 1, 2, 2, 2, 0, 1, 2, 2,
		1, 0, 1, 0, 1, 2, 0, 1, 0, 2, 1, 0, 2, 0, 2, 1, 1, 1, 0, 0, 2, 2, 2, 0, 1, 1, 2, 2, 1, 2,
		0, 0, 0, 1, 0, 1, 2, 1, 0 };

private int beat_deBruijn() {
	return ( ( db_table[ cur_move % 1000 ] + 1 ) % 3 );
}

private int beat_repetition() {
	int result = 0;
	if ( cur_move == 0 )
		result = Coin.flip();
	else
		result = ( ( op_hist[ cur_move - 1 ] + ( cur_move + 2 ) ) % 3 );
	return result;
}

/* returned mijn laatse op de opponents laatste move */
private int beat_simple( int mode ) {
	if ( cur_move == 0 )
		return ROCK;
	else if ( mode == me )
		return my_hist[ cur_move - 1 ];
	else
		return op_hist[ cur_move - 1 ];
}

/* geeft een voorspelling aan de hand van wat ik of de opp het vaakst hebben gespeeld */
private int beat_stat( int mode ) {
	int result = 0;
	if ( mode == me ) {
		if ( ( myrcount > mypcount ) && ( myrcount > myscount ) )
			result = PAPER;
		else if ( mypcount > myscount )
			result = SCISSORS;
		else
			result = ROCK;
	}
	else {
		if ( ( opprcount > opppcount ) && ( opprcount > oppscount ) )
			result = PAPER;
		else if ( opppcount > oppscount )
			result = SCISSORS;
		else
			result = ROCK;
	}
	return result;
}

/**
 * In deze functie worden de voorspellingen van de verschillende strategien in een array
 * gestopt. Vervolgens bepaald meta_strategie() welke strategie gespeeld word en wordt de
 * historie van al deze strategien bijgewerkt.
 * @return next move
 */
public int nextMove() {
	laststratpred[ 0 ] = history_match_both( me );
	laststratpred[ 1 ] = ( laststratpred[ 0 ] + 1 ) % 3;
	laststratpred[ 2 ] = ( laststratpred[ 0 ] + 2 ) % 3;
	laststratpred[ 3 ] = history_match_both( op );
	laststratpred[ 4 ] = ( laststratpred[ 3 ] + 1 ) % 3;
	laststratpred[ 5 ] = ( laststratpred[ 3 ] + 2 ) % 3;

	laststratpred[ 6 ] = Coin.flip();
	laststratpred[ 7 ] = beat_Pi();
	laststratpred[ 8 ] = beat_deBruijn();
	laststratpred[ 9 ] = beat_repetition();

	laststratpred[ 10 ] = beat_stat( me );
	laststratpred[ 11 ] = ( laststratpred[ 10 ] + 1 ) % 3;
	laststratpred[ 12 ] = ( laststratpred[ 10 ] + 2 ) % 3;
	laststratpred[ 13 ] = beat_stat( op );
	laststratpred[ 14 ] = ( laststratpred[ 13 ] + 1 ) % 3;
	laststratpred[ 15 ] = ( laststratpred[ 13 ] + 2 ) % 3;

	laststratpred[ 16 ] = UrzaSuperStats( root, op, 80, cur_move - 1 );
	laststratpred[ 17 ] = ( laststratpred[ 16 ] + 1 ) % 3;
	laststratpred[ 18 ] = ( laststratpred[ 16 ] + 2 ) % 3;

	laststratpred[ 19 ] = UrzaSuperStats( root, me, 80, cur_move - 1 );
	laststratpred[ 20 ] = ( laststratpred[ 19 ] + 1 ) % 3;
	laststratpred[ 21 ] = ( laststratpred[ 19 ] + 2 ) % 3;

	laststratpred[ 22 ] = beat_simple( me );
	laststratpred[ 23 ] = ( laststratpred[ 22 ] + 1 ) % 3;
	laststratpred[ 24 ] = ( laststratpred[ 22 ] + 2 ) % 3;
	laststratpred[ 25 ] = beat_simple( op );
	laststratpred[ 26 ] = ( laststratpred[ 25 ] + 1 ) % 3;
	laststratpred[ 27 ] = ( laststratpred[ 25 ] + 2 ) % 3;

	int stratidx = MetaStrategie();
	my_hist[ cur_move ] = laststratpred[ stratidx ];

	for ( int i = 0; i < strategies; i++ )
		strathist[ i ][ cur_move ] = laststratpred[ i ];

	int choice = my_hist[ cur_move ];
	if ( choice == ROCK )
		myrcount++;
	else if ( choice == PAPER )
		mypcount++;
	else
		myscount++;
	return choice;
}

/**
 * De nu volgende drie functies zorgen voor een boom die de hele historie van beide spelers
 * modelleerd. Elk beurt van het spel zorgt voor 9 mogelijkheden voor die beurt. Nu coderen we
 * de histories door aan de hand van beide spelers een kant op te gaan in die boom. Een en ander
 * wordt misschien duidelijker aan de hand van een voorbeeld: de historie van beide spelers voor
 * de laatse 3 beurten. ik : 0 1 2 2 op : 2 1 0 1 Dit zou alsvolgt in de boom komen.De laastse
 * beurt gaan we opslaan. Dit is scissors voor mij en paper voor de tegenstander. Dit slaan we
 * dus ook op in de root-node waar we beginnen (setreversedsupernode) en vervolgens gaan we
 * verder kijken. Hiervoor werd 2 0 gespeeld en dus gaan we 'rechts(2)links(0)' in de boom. Als
 * deze context(node) niet zou bestaan maken we deze en zijn we klaar. Anders gaan we vanaf daar
 * nog verder aan de boom werken. We werken van de huidige beurt naar achteren
 * (reversed).Zodoende krijgen we een boom met informatie over series. Willen we nu weten wat de
 * volgende move wordt van de tegenstander dan kunnen we door deze boom weer af te zoeken de
 * langst gespeelde string vinden(getreversedsupernode). De nodes in de boom geven als het ware
 * wat de tegenstander en ik hebben gespeeld in een bepaalde context.
 * @param node
 * @param curpos
 * @param lastpos
 * @param aDepth
 */
private void SetReversedSuperNode( SuperNode node, int curpos, int lastpos, int aDepth ) {
	/* Current node updaten */
	if ( curpos < 1 )
		return;
	switch ( op_hist[ lastpos ] ) {
		case 0:
			node.oppr++;
			break;
		case 1:
			node.oppp++;
			break;
		case 2:
			node.opps++;
			break;
	}
	switch ( my_hist[ lastpos ] ) {
		case 0:
			node.mypr++;
			break;
		case 1:
			node.mypp++;
			break;
		case 2:
			node.myps++;
			break;
	}
	/* maximale diepte voor de boom */
	if ( lastpos - curpos > aDepth ) {
		return;
	}

	/* dieper die boom in aan de hand van de histories */
	switch ( op_hist[ curpos - 1 ] ) {
		case 0:
			switch ( my_hist[ curpos - 1 ] ) {
				case 0:
					if ( node.leftleft == null )
						node.leftleft = new SuperNode();
					SetReversedSuperNode( node.leftleft, --curpos, lastpos, aDepth );
					break;
				case 1:
					if ( node.leftmiddle == null )
						node.leftmiddle = new SuperNode();
					SetReversedSuperNode( node.leftmiddle, --curpos, lastpos, aDepth );
					break;
				case 2:
					if ( node.leftright == null )
						node.leftright = new SuperNode();
					SetReversedSuperNode( node.leftright, --curpos, lastpos, aDepth );
					break;
			}
			break;
		case 1:
			switch ( my_hist[ curpos - 1 ] ) {
				case 0:
					if ( node.middleleft == null )
						node.middleleft = new SuperNode();
					SetReversedSuperNode( node.middleleft, --curpos, lastpos, aDepth );
					break;
				case 1:
					if ( node.middlemiddle == null )
						node.middlemiddle = new SuperNode();
					SetReversedSuperNode( node.middlemiddle, --curpos, lastpos, aDepth );
					break;
				case 2:
					if ( node.middleright == null )
						node.middleright = new SuperNode();
					SetReversedSuperNode( node.middleright, --curpos, lastpos, aDepth );
					break;
			}
			break;
		case 2:
			switch ( my_hist[ curpos - 1 ] ) {
				case 0:
					if ( node.rightleft == null )
						node.rightleft = new SuperNode();
					SetReversedSuperNode( node.rightleft, --curpos, lastpos, aDepth );
					break;
				case 1:
					if ( node.rightmiddle == null )
						node.rightmiddle = new SuperNode();
					SetReversedSuperNode( node.rightmiddle, --curpos, lastpos, aDepth );
					break;
				case 2:
					if ( node.rightright == null )
						node.rightright = new SuperNode();
					SetReversedSuperNode( node.rightright, --curpos, lastpos, aDepth );
					break;
			}
			break;
	}
}

private SuperNode getreversedsupernode( SuperNode node, SuperNode best, int curpos,
		int lastpos, int aDepth, int mode ) {
	// PDT - unused
	// int pr, pp, ps;
	int total;

	/* Inspecteer huidige node */
	if ( mode == op ) {
		total = ( node.oppr          ) + ( node.oppp          ) + ( node.opps          );
		// PDT - unused
		//		pr = node.oppr;
		//		pp = node.oppp;
		//		ps = node.opps;
	}
	else {
		total = ( node.mypr          ) + ( node.mypp          ) + ( node.myps          );
		// PDT - unused
		//		pr = node.mypr;
		//		pp = node.mypp;
		//		ps = node.myps;
	}
	/*
	 * als alle kansen samen nul zijn is de node ongebruikt (heeft geen info) en gebruiken we dus
	 * de node die 1 niveau minder diep ligt
	 */
	if ( total == 0 )
		return best;

	best = node;

	/* dieper die boom in aan de hand van de histories */
	SuperNode result = null;
	switch ( op_hist[ curpos ] ) {
		case 0:
			switch ( my_hist[ curpos ] ) {
				case 0:
					if ( node.leftleft == null ) {
						result = best;
					}
					else
						result = getreversedsupernode( node.leftleft, best, --curpos, lastpos, aDepth, mode );
				case 1:
					if ( node.leftmiddle == null ) {
						result = best;
					}
					else
						result = getreversedsupernode( node.leftmiddle, best, --curpos, lastpos, aDepth,
								mode );
				case 2:
					if ( node.leftright == null ) {
						result = best;
					}
					else
						result = getreversedsupernode( node.leftright, best, --curpos, lastpos, aDepth,
								mode );
			}
			break;
		case 1:
			switch ( my_hist[ curpos ] ) {
				case 0:
					if ( node.middleleft == null ) {
						result = best;
					}
					else
						result = getreversedsupernode( node.middleleft, best, --curpos, lastpos, aDepth,
								mode );
				case 1:
					if ( node.middlemiddle == null ) {
						result = best;
					}
					else
						result = getreversedsupernode( node.middlemiddle, best, --curpos, lastpos, aDepth,
								mode );
				case 2:
					if ( node.middleright == null ) {
						result = best;
					}
					else
						result = getreversedsupernode( node.middleright, best, --curpos, lastpos, aDepth,
								mode );
			}
			break;
		case 2:
			switch ( my_hist[ curpos ] ) {
				case 0:
					if ( node.rightleft == null ) {
						result = best;
					}
					else
						result = getreversedsupernode( node.rightleft, best, --curpos, lastpos, aDepth,
								mode );
				case 1:
					if ( node.rightmiddle == null ) {
						result = best;
					}
					else
						result = getreversedsupernode( node.rightmiddle, best, --curpos, lastpos, aDepth,
								mode );
				case 2:
					if ( node.rightright == null ) {
						result = best;
					}
					else
						result = getreversedsupernode( node.rightright, best, --curpos, lastpos, aDepth,
								mode );
			}
			break;
	}
	return result;

	//ahgi unreachable
	//	return node;
}

/**
 * Urzasuperstats werkt eerst de boom bij dmv setreversedsupernode en haalt vervolgens een
 * voorspelling dmv gereversedsupernode
 * @param aRoot
 * @param mode
 * @param aDepth
 * @param aCur_move
 */
private int UrzaSuperStats( SuperNode aRoot, int mode, int aDepth, int aCur_move ) {
	int result = 0;
	SuperNode node;
	SetReversedSuperNode( aRoot, aCur_move, aCur_move, aDepth );
	node = getreversedsupernode( aRoot, aRoot, aCur_move, aCur_move, aDepth, mode );
	/* Haal informatie over gespeelde rps en return de move die mij of de tegenstander verslaat */
	if ( mode == op ) {
		if ( ( node.oppr          ) > ( node.oppp          ) )
			if ( ( node.oppr          ) > ( node.opps          ) ) {
				result = PAPER;
			}
			else {
				result = ROCK;
			}
		else if ( ( node.oppp          ) > ( node.opps          ) ) {
			result = SCISSORS;
		}
		else {
			result = ROCK;
		}
	}
	else {
		if ( ( node.mypr           ) > ( node.mypp           ) )
			if ( ( node.mypr           ) > ( node.myps           ) ) {
				result = PAPER;
			}
			else {
				result = ROCK;
			}
		else if ( ( node.mypp           ) > ( node.myps           ) ) {
			result = SCISSORS;
		}
		else {
			result = ROCK;
		}
	}
	return result;
}

/**
 * @see com.anji.tournament.Player#reset()
 */
public void reset() {
	reset( 0 );
}


/**
 * @see java.lang.Object#hashCode()
 */
public int hashCode() {
	return getPlayerId().hashCode();
}

}
