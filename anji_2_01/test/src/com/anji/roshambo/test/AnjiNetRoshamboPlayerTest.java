/*
 * Copyright (C) 2004 Derek James and Philip Tucker
 * 
 * This file is part of ANJI (Another NEAT Java Implementation).
 * 
 * ANJI is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 * 
 * Created on May 17, 2005 by Philip Tucker
 */
package com.anji.roshambo.test;

import junit.framework.TestCase;

import org.jgap.Chromosome;

import com.anji.integration.ActivatorTranscriber;
import com.anji.integration.TranscriberException;
import com.anji.persistence.FilePersistence;
import com.anji.roshambo.AnjiNetRoshamboPlayer;
import com.anji.roshambo.RoshamboPlayer;
import com.anji.roshambo.RoshamboPlayerTranscriber;
import com.anji.tournament.Player;
import com.anji.util.DummyConfiguration;
import com.anji.util.Properties;

/**
 * @author Philip Tucker
 */
public class AnjiNetRoshamboPlayerTest extends TestCase {

private final static String ROSHAMBO_CHROMOSOME_XML = "<chromosome id=\"143\"><neuron id=\"111\" type=\"in\" activation=\"linear\"/><neuron id=\"112\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"113\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"114\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"115\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"116\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"117\" type=\"out\" activation=\"sigmoid\"/>"
		+ "<connection id=\"118\" src-id=\"111\" dest-id=\"117\" weight=\"0.5903035402297974\"/>"
		+ "<connection id=\"119\" src-id=\"112\" dest-id=\"117\" weight=\"-0.09244883060455322\"/>"
		+ "<connection id=\"120\" src-id=\"113\" dest-id=\"117\" weight=\"0.6609854698181152\"/>"
		+ "<connection id=\"121\" src-id=\"114\" dest-id=\"117\" weight=\"-0.13911688327789307\"/>"
		+ "<connection id=\"122\" src-id=\"115\" dest-id=\"117\" weight=\"0.7574683427810669\"/>"
		+ "<connection id=\"123\" src-id=\"116\" dest-id=\"117\" weight=\"-0.6534011363983154\"/>"
		+ "<neuron id=\"124\" type=\"out\" activation=\"sigmoid\"/>"
		+ "<connection id=\"125\" src-id=\"111\" dest-id=\"124\" weight=\"0.2654029130935669\"/>"
		+ "<connection id=\"126\" src-id=\"112\" dest-id=\"124\" weight=\"-0.15117192268371582\"/>"
		+ "<connection id=\"127\" src-id=\"113\" dest-id=\"124\" weight=\"-0.6020402908325195\"/>"
		+ "<connection id=\"128\" src-id=\"114\" dest-id=\"124\" weight=\"-0.2927100658416748\"/>"
		+ "<connection id=\"129\" src-id=\"115\" dest-id=\"124\" weight=\"0.9218858480453491\"/>"
		+ "<connection id=\"130\" src-id=\"116\" dest-id=\"124\" weight=\"0.5147671699523926\"/>"
		+ "<neuron id=\"131\" type=\"out\" activation=\"sigmoid\"/>"
		+ "<connection id=\"132\" src-id=\"111\" dest-id=\"131\" weight=\"0.4996345043182373\"/>"
		+ "<connection id=\"133\" src-id=\"112\" dest-id=\"131\" weight=\"0.6317259073257446\"/>"
		+ "<connection id=\"134\" src-id=\"113\" dest-id=\"131\" weight=\"0.4816843271255493\"/>"
		+ "<connection id=\"135\" src-id=\"114\" dest-id=\"131\" weight=\"0.5605806112289429\"/>"
		+ "<connection id=\"136\" src-id=\"115\" dest-id=\"131\" weight=\"-0.34408628940582275\"/>"
		+ "<connection id=\"137\" src-id=\"116\" dest-id=\"131\" weight=\"-0.1930234432220459\"/>"
		+ "</chromosome>";

private final static String NOT_ROSHAMBO_CHROMOSOME_XML = "<chromosome id=\"125101\" primary-parent-id=\"123777\"><neuron id=\"0\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"1\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"2\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"54\" type=\"out\" activation=\"tanh\"/>"
		+ "<connection id=\"122377\" src-id=\"1\" dest-id=\"54\" weight=\"21.49284506351882\"/>"
		+ "<connection id=\"122377\" src-id=\"2\" dest-id=\"54\" weight=\"-21.49284506351882\"/>"
		+ "</chromosome>";

private final static String HISTORY_ROSHAMBO_CHROMOSOME_XML = ""
		+ "<chromosome id=\"5708\" primary-parent-id=\"5066\"><neuron id=\"0\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"1\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"2\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"3\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"4\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"5\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"6\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"7\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"8\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"9\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"10\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"11\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"12\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"13\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"14\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"15\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"16\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"17\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"18\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"19\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"20\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"21\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"22\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"23\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"24\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"25\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"26\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"27\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"28\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"29\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"30\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"31\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"32\" type=\"in\" activation=\"linear\"/>"
		+ "<neuron id=\"33\" type=\"out\" activation=\"sigmoid\"/>"
		+ "<connection id=\"34\" src-id=\"0\" dest-id=\"33\" weight=\"-2.546317156508128\"/>"
		+ "<connection id=\"35\" src-id=\"1\" dest-id=\"33\" weight=\"-9.961883112556713\"/>"
		+ "<connection id=\"36\" src-id=\"2\" dest-id=\"33\" weight=\"7.748141230183566\"/>"
		+ "<connection id=\"37\" src-id=\"3\" dest-id=\"33\" weight=\"3.114282427125726\"/>"
		+ "<connection id=\"38\" src-id=\"4\" dest-id=\"33\" weight=\"11.065406401421747\"/>"
		+ "<connection id=\"39\" src-id=\"5\" dest-id=\"33\" weight=\"2.399735651813307\"/>"
		+ "<connection id=\"41\" src-id=\"7\" dest-id=\"33\" weight=\"1.3761180359950909\"/>"
		+ "<connection id=\"42\" src-id=\"8\" dest-id=\"33\" weight=\"-5.464668077344317\"/>"
		+ "<connection id=\"43\" src-id=\"9\" dest-id=\"33\" weight=\"-0.3228435336148819\"/>"
		+ "<connection id=\"44\" src-id=\"10\" dest-id=\"33\" weight=\"-5.783216696229878\"/>"
		+ "<connection id=\"45\" src-id=\"11\" dest-id=\"33\" weight=\"-0.9137699452946217\"/>"
		+ "<connection id=\"46\" src-id=\"12\" dest-id=\"33\" weight=\"-0.20407040959705947\"/>"
		+ "<connection id=\"47\" src-id=\"13\" dest-id=\"33\" weight=\"-8.09897695179063\"/>"
		+ "<connection id=\"48\" src-id=\"14\" dest-id=\"33\" weight=\"2.452349000804067\"/>"
		+ "<connection id=\"49\" src-id=\"15\" dest-id=\"33\" weight=\"-6.802147252457753\"/>"
		+ "<connection id=\"51\" src-id=\"17\" dest-id=\"33\" weight=\"-7.515294370129009\"/>"
		+ "<connection id=\"52\" src-id=\"18\" dest-id=\"33\" weight=\"6.491920881868819\"/>"
		+ "<connection id=\"53\" src-id=\"19\" dest-id=\"33\" weight=\"0.5637206003318822\"/>"
		+ "<connection id=\"54\" src-id=\"20\" dest-id=\"33\" weight=\"-1.2600750679774197\"/>"
		+ "<connection id=\"55\" src-id=\"21\" dest-id=\"33\" weight=\"-4.142029843912064\"/>"
		+ "<connection id=\"56\" src-id=\"22\" dest-id=\"33\" weight=\"0.17459582712899713\"/>"
		+ "<connection id=\"57\" src-id=\"23\" dest-id=\"33\" weight=\"-1.6947040572571832\"/>"
		+ "<connection id=\"58\" src-id=\"24\" dest-id=\"33\" weight=\"1.4495376803818207\"/>"
		+ "<connection id=\"59\" src-id=\"25\" dest-id=\"33\" weight=\"2.1453069718650144\"/>"
		+ "<connection id=\"60\" src-id=\"26\" dest-id=\"33\" weight=\"-0.6628911199289149\"/>"
		+ "<connection id=\"61\" src-id=\"27\" dest-id=\"33\" weight=\"-1.2122218720752391\"/>"
		+ "<connection id=\"62\" src-id=\"28\" dest-id=\"33\" weight=\"-1.45470521926117\"/>"
		+ "<connection id=\"63\" src-id=\"29\" dest-id=\"33\" weight=\"5.816876018586452\"/>"
		+ "<connection id=\"64\" src-id=\"30\" dest-id=\"33\" weight=\"-7.222219067785111\"/>"
		+ "<connection id=\"65\" src-id=\"31\" dest-id=\"33\" weight=\"5.143301232439974\"/>"
		+ "<connection id=\"66\" src-id=\"32\" dest-id=\"33\" weight=\"-2.001091751776851\"/>"
		+ "<neuron id=\"67\" type=\"out\" activation=\"sigmoid\"/>"
		+ "<connection id=\"68\" src-id=\"0\" dest-id=\"67\" weight=\"-1.2277891715242568\"/>"
		+ "<connection id=\"69\" src-id=\"1\" dest-id=\"67\" weight=\"11.175679564505089\"/>"
		+ "<connection id=\"70\" src-id=\"2\" dest-id=\"67\" weight=\"-0.9678513877347753\"/>"
		+ "<connection id=\"71\" src-id=\"3\" dest-id=\"67\" weight=\"-0.7170830574704294\"/>"
		+ "<connection id=\"72\" src-id=\"4\" dest-id=\"67\" weight=\"1.5143297073974864\"/>"
		+ "<connection id=\"73\" src-id=\"5\" dest-id=\"67\" weight=\"0.8701228581347917\"/>"
		+ "<connection id=\"75\" src-id=\"7\" dest-id=\"67\" weight=\"1.3247291183303576\"/>"
		+ "<connection id=\"76\" src-id=\"8\" dest-id=\"67\" weight=\"1.6677232623368545\"/>"
		+ "<connection id=\"79\" src-id=\"11\" dest-id=\"67\" weight=\"-7.98339895781729\"/>"
		+ "<connection id=\"80\" src-id=\"12\" dest-id=\"67\" weight=\"2.8665541225506455\"/>"
		+ "<connection id=\"81\" src-id=\"13\" dest-id=\"67\" weight=\"-1.6130625324156806\"/>"
		+ "<connection id=\"82\" src-id=\"14\" dest-id=\"67\" weight=\"2.831962082666819\"/>"
		+ "<connection id=\"83\" src-id=\"15\" dest-id=\"67\" weight=\"-4.076046009065459\"/>"
		+ "<connection id=\"86\" src-id=\"18\" dest-id=\"67\" weight=\"4.185595283970741\"/>"
		+ "<connection id=\"87\" src-id=\"19\" dest-id=\"67\" weight=\"-0.2985951373269197\"/>"
		+ "<connection id=\"88\" src-id=\"20\" dest-id=\"67\" weight=\"-4.3419543903315905\"/>"
		+ "<connection id=\"90\" src-id=\"22\" dest-id=\"67\" weight=\"0.2036754502255651\"/>"
		+ "<connection id=\"91\" src-id=\"23\" dest-id=\"67\" weight=\"-8.475263342629543\"/>"
		+ "<connection id=\"92\" src-id=\"24\" dest-id=\"67\" weight=\"-1.9472210594648267\"/>"
		+ "<connection id=\"93\" src-id=\"25\" dest-id=\"67\" weight=\"4.0345785808466665\"/>"
		+ "<connection id=\"94\" src-id=\"26\" dest-id=\"67\" weight=\"-3.275069026225335\"/>"
		+ "<connection id=\"95\" src-id=\"27\" dest-id=\"67\" weight=\"-2.2582884847403064\"/>"
		+ "<connection id=\"96\" src-id=\"28\" dest-id=\"67\" weight=\"7.037975138584281\"/>"
		+ "<connection id=\"97\" src-id=\"29\" dest-id=\"67\" weight=\"3.1571571386181443\"/>"
		+ "<connection id=\"98\" src-id=\"30\" dest-id=\"67\" weight=\"-3.1275467734127282\"/>"
		+ "<connection id=\"99\" src-id=\"31\" dest-id=\"67\" weight=\"-0.5034492957884393\"/>"
		+ "<connection id=\"100\" src-id=\"32\" dest-id=\"67\" weight=\"-0.8671444870682672\"/>"
		+ "<neuron id=\"101\" type=\"out\" activation=\"sigmoid\"/>"
		+ "<connection id=\"102\" src-id=\"0\" dest-id=\"101\" weight=\"1.1683935526676879\"/>"
		+ "<connection id=\"103\" src-id=\"1\" dest-id=\"101\" weight=\"-6.921634370199646\"/>"
		+ "<connection id=\"106\" src-id=\"4\" dest-id=\"101\" weight=\"-6.391430970196158\"/>"
		+ "<connection id=\"107\" src-id=\"5\" dest-id=\"101\" weight=\"1.6633805961224972\"/>"
		+ "<connection id=\"109\" src-id=\"7\" dest-id=\"101\" weight=\"-4.7739700954989726\"/>"
		+ "<connection id=\"110\" src-id=\"8\" dest-id=\"101\" weight=\"4.851061289523416\"/>"
		+ "<connection id=\"112\" src-id=\"10\" dest-id=\"101\" weight=\"-0.8239172659258995\"/>"
		+ "<connection id=\"114\" src-id=\"12\" dest-id=\"101\" weight=\"-8.344155959624171\"/>"
		+ "<connection id=\"115\" src-id=\"13\" dest-id=\"101\" weight=\"-4.108677548871388\"/>"
		+ "<connection id=\"116\" src-id=\"14\" dest-id=\"101\" weight=\"-2.078609136537576\"/>"
		+ "<connection id=\"117\" src-id=\"15\" dest-id=\"101\" weight=\"2.641121719399514\"/>"
		+ "<connection id=\"118\" src-id=\"16\" dest-id=\"101\" weight=\"-6.947108079632201\"/>"
		+ "<connection id=\"119\" src-id=\"17\" dest-id=\"101\" weight=\"9.02646744372681\"/>"
		+ "<connection id=\"120\" src-id=\"18\" dest-id=\"101\" weight=\"-4.14529873921712\"/>"
		+ "<connection id=\"121\" src-id=\"19\" dest-id=\"101\" weight=\"2.359889916981553\"/>"
		+ "<connection id=\"122\" src-id=\"20\" dest-id=\"101\" weight=\"6.571224298603195\"/>"
		+ "<connection id=\"123\" src-id=\"21\" dest-id=\"101\" weight=\"5.891886724375836\"/>"
		+ "<connection id=\"124\" src-id=\"22\" dest-id=\"101\" weight=\"2.7670142479379036\"/>"
		+ "<connection id=\"125\" src-id=\"23\" dest-id=\"101\" weight=\"0.9680016643500124\"/>"
		+ "<connection id=\"126\" src-id=\"24\" dest-id=\"101\" weight=\"-2.5879993297271486\"/>"
		+ "<connection id=\"127\" src-id=\"25\" dest-id=\"101\" weight=\"4.935230343309604\"/>"
		+ "<connection id=\"128\" src-id=\"26\" dest-id=\"101\" weight=\"3.263165126244557\"/>"
		+ "<connection id=\"129\" src-id=\"27\" dest-id=\"101\" weight=\"-2.2278149700391765\"/>"
		+ "<connection id=\"130\" src-id=\"28\" dest-id=\"101\" weight=\"9.406194173371764\"/>"
		+ "<connection id=\"132\" src-id=\"30\" dest-id=\"101\" weight=\"1.4026526858483215\"/>"
		+ "<connection id=\"133\" src-id=\"31\" dest-id=\"101\" weight=\"3.8044196348862047\"/>"
		+ "<connection id=\"134\" src-id=\"32\" dest-id=\"101\" weight=\"0.7049670434827788\"/>"
		+ "<neuron id=\"890\" type=\"hid\" activation=\"sigmoid\"/>"
		+ "<connection id=\"892\" src-id=\"890\" dest-id=\"101\" weight=\"-3.402556165914475\"/>"
		+ "<neuron id=\"929\" type=\"hid\" activation=\"sigmoid\"/>"
		+ "<connection id=\"930\" src-id=\"29\" dest-id=\"929\" weight=\"-1.1102625630494394\"/>"
		+ "<connection id=\"931\" src-id=\"929\" dest-id=\"101\" weight=\"1.5539324262410066\"/>"
		+ "<connection id=\"1353\" src-id=\"929\" dest-id=\"67\" weight=\"-3.790500724169468\"/>"
		+ "<neuron id=\"1376\" type=\"hid\" activation=\"sigmoid\"/>"
		+ "<connection id=\"1377\" src-id=\"16\" dest-id=\"1376\" weight=\"-7.69026921351425\"/>"
		+ "<connection id=\"1378\" src-id=\"1376\" dest-id=\"67\" weight=\"-2.8593690556383997\"/>"
		+ "<neuron id=\"1406\" type=\"hid\" activation=\"sigmoid\"/>"
		+ "<connection id=\"1407\" src-id=\"6\" dest-id=\"1406\" weight=\"0.5707861432676387\"/>"
		+ "<connection id=\"1408\" src-id=\"1406\" dest-id=\"33\" weight=\"1.1692960075182692\"/>"
		+ "<connection id=\"1554\" src-id=\"4\" dest-id=\"890\" weight=\"-10.958563671100665\"/>"
		+ "<connection id=\"1555\" src-id=\"22\" dest-id=\"890\" weight=\"-2.3300017881439974\"/>"
		+ "<connection id=\"1557\" src-id=\"11\" dest-id=\"929\" weight=\"0.9707571727386131\"/>"
		+ "<neuron id=\"1678\" type=\"hid\" activation=\"sigmoid\"/>"
		+ "<connection id=\"1679\" src-id=\"10\" dest-id=\"1678\" weight=\"8.480195926343047\"/>"
		+ "<connection id=\"1680\" src-id=\"1678\" dest-id=\"67\" weight=\"-2.004409273693191\"/>"
		+ "<connection id=\"1914\" src-id=\"32\" dest-id=\"929\" weight=\"-4.496088883333842\"/>"
		+ "<connection id=\"1916\" src-id=\"26\" dest-id=\"890\" weight=\"2.9594158084144087\"/>"
		+ "<connection id=\"1947\" src-id=\"11\" dest-id=\"1678\" weight=\"-6.483146962935393\"/>"
		+ "<neuron id=\"1957\" type=\"hid\" activation=\"sigmoid\"/>"
		+ "<connection id=\"1958\" src-id=\"11\" dest-id=\"1957\" weight=\"6.3790990813722726\"/>"
		+ "<connection id=\"1959\" src-id=\"1957\" dest-id=\"101\" weight=\"-4.830214911983357\"/>"
		+ "<connection id=\"2130\" src-id=\"1406\" dest-id=\"1957\" weight=\"-7.572100194396736\"/>"
		+ "<connection id=\"2138\" src-id=\"10\" dest-id=\"929\" weight=\"-3.229004055004339\"/>"
		+ "<connection id=\"2153\" src-id=\"21\" dest-id=\"1678\" weight=\"4.362350129427256\"/>"
		+ "<neuron id=\"2239\" type=\"hid\" activation=\"sigmoid\"/>"
		+ "<connection id=\"2240\" src-id=\"2\" dest-id=\"2239\" weight=\"4.361048650658501\"/>"
		+ "<connection id=\"2241\" src-id=\"2239\" dest-id=\"890\" weight=\"2.9117035720852185\"/>"
		+ "<neuron id=\"2254\" type=\"hid\" activation=\"sigmoid\"/>"
		+ "<connection id=\"2255\" src-id=\"6\" dest-id=\"2254\" weight=\"-0.8498434075196478\"/>"
		+ "<connection id=\"2256\" src-id=\"2254\" dest-id=\"101\" weight=\"-4.777989378352553\"/>"
		+ "<neuron id=\"2257\" type=\"hid\" activation=\"sigmoid\"/>"
		+ "<connection id=\"2258\" src-id=\"9\" dest-id=\"2257\" weight=\"-0.13003055772415625\"/>"
		+ "<connection id=\"2259\" src-id=\"2257\" dest-id=\"101\" weight=\"-9.984590092545377\"/>"
		+ "<connection id=\"2432\" src-id=\"28\" dest-id=\"890\" weight=\"3.047960603469628\"/>"
		+ "<connection id=\"2506\" src-id=\"28\" dest-id=\"2254\" weight=\"-2.4886579757934433\"/>"
		+ "<connection id=\"2508\" src-id=\"23\" dest-id=\"2239\" weight=\"1.008097544901172\"/>"
		+ "<connection id=\"2509\" src-id=\"101\" dest-id=\"2257\" weight=\"-0.3711308081258444\"/>"
		+ "<connection id=\"2510\" src-id=\"10\" dest-id=\"2239\" weight=\"-0.7454976839068603\"/>"
		+ "<connection id=\"2516\" src-id=\"22\" dest-id=\"2254\" weight=\"1.150293348162399\"/>"
		+ "<connection id=\"2669\" src-id=\"11\" dest-id=\"1406\" weight=\"0.25232229913338167\"/>"
		+ "<connection id=\"2756\" src-id=\"1\" dest-id=\"890\" weight=\"-0.8448285238660097\"/>"
		+ "<connection id=\"2831\" src-id=\"2239\" dest-id=\"929\" weight=\"4.815472459444248\"/>"
		+ "<connection id=\"2832\" src-id=\"3\" dest-id=\"2254\" weight=\"0.903722655212942\"/>"
		+ "<connection id=\"3098\" src-id=\"28\" dest-id=\"2239\" weight=\"-0.8824656972782965\"/>"
		+ "<connection id=\"3100\" src-id=\"33\" dest-id=\"2239\" weight=\"-2.05535386143062\"/>"
		+ "<connection id=\"3712\" src-id=\"17\" dest-id=\"2257\" weight=\"1.8785148925161275\"/>"
		+ "<connection id=\"4098\" src-id=\"0\" dest-id=\"1376\" weight=\"-0.2640045337193141\"/>"
		+ "<connection id=\"4562\" src-id=\"30\" dest-id=\"1957\" weight=\"-1.0065885088011595\"/>"
		+ "<connection id=\"4593\" src-id=\"26\" dest-id=\"1957\" weight=\"-0.18916048766343796\"/>"
		+ "<connection id=\"5474\" src-id=\"10\" dest-id=\"2254\" weight=\"-0.9381483229670913\"/>"
		+ "<connection id=\"5475\" src-id=\"2254\" dest-id=\"1957\" weight=\"2.2152620047219704\"/>"
		+ "</chromosome>";

// TODO
//private final static String SCANNING_ROSHAMBO_CHROMOSOME_XML = ""
//		+ "<chromosome id=\"5708\" primary-parent-id=\"5066\"><neuron id=\"0\" type=\"in\"
// activation=\"linear\"/>"
//		+ "<neuron id=\"1\" type=\"in\" activation=\"linear\"/>"
//		+ "<neuron id=\"2\" type=\"in\" activation=\"linear\"/>"
//		+ "<connection id=\"5475\" src-id=\"2254\" dest-id=\"1957\" weight=\"2.2152620047219704\"/>"
//		+ "</chromosome>";

private Chromosome historyRoshamboChromosome;

private Chromosome roshamboChromosome;

private Chromosome notRoshamboChromosome;

// TODO
// private Chromosome scanningRoshamboChromosome;

private final static int[] EXPECTED_MOVES = new int[] { RoshamboPlayer.SCISSORS,
		RoshamboPlayer.SCISSORS, RoshamboPlayer.SCISSORS, RoshamboPlayer.SCISSORS,
		RoshamboPlayer.SCISSORS, RoshamboPlayer.ROCK, RoshamboPlayer.SCISSORS,
		RoshamboPlayer.SCISSORS, RoshamboPlayer.SCISSORS, RoshamboPlayer.SCISSORS };

private final static int[] OPPONENT_MOVES = new int[] { RoshamboPlayer.PAPER,
		RoshamboPlayer.SCISSORS, RoshamboPlayer.SCISSORS, RoshamboPlayer.ROCK,
		RoshamboPlayer.SCISSORS, RoshamboPlayer.SCISSORS, RoshamboPlayer.ROCK, RoshamboPlayer.ROCK,
		RoshamboPlayer.SCISSORS, RoshamboPlayer.SCISSORS };

private final static int[] SCORES = new int[] { RoshamboPlayer.LOSS, RoshamboPlayer.DRAW,
		RoshamboPlayer.DRAW, RoshamboPlayer.LOSS, RoshamboPlayer.DRAW, RoshamboPlayer.WIN,
		RoshamboPlayer.LOSS, RoshamboPlayer.LOSS, RoshamboPlayer.DRAW, RoshamboPlayer.DRAW };

/**
 * ctor
 */
public AnjiNetRoshamboPlayerTest() {
	this( AnjiNetRoshamboPlayerTest.class.toString() );
}

/**
 * ctor
 * @param arg0
 */
public AnjiNetRoshamboPlayerTest( String arg0 ) {
	super( arg0 );
}

/**
 * @see junit.framework.TestCase#setUp()
 */
protected void setUp() throws Exception {
	DummyConfiguration config = new DummyConfiguration();
	roshamboChromosome = FilePersistence.chromosomeFromXml( config, ROSHAMBO_CHROMOSOME_XML );
	notRoshamboChromosome = FilePersistence.chromosomeFromXml( config,
			NOT_ROSHAMBO_CHROMOSOME_XML );
	historyRoshamboChromosome = FilePersistence.chromosomeFromXml( config,
			HISTORY_ROSHAMBO_CHROMOSOME_XML );
	// TODO
	//	scanningRoshamboChromosome = FilePersistence.chromosomeFromXml( config,
	//			SCANNING_ROSHAMBO_CHROMOSOME_XML );
}

/**
 * test deterministic transcriber, multi cycle
 * @throws Exception
 */
public void testTranscriberDeterministicMultiCycle() throws Exception {
	doTestTranscriber( true, 10 );
}

/**
 * test non-deterministic transcriber, multi cycle
 * @throws Exception
 */
public void testTranscriberNonDeterministicMultiCycle() throws Exception {
	doTestTranscriber( false, 10 );
}

/**
 * test deterministic transcriber
 * @throws Exception
 */
public void testTranscriberDeterministic() throws Exception {
	doTestTranscriber( true, 1 );
}

/**
 * test non-deterministic transcriber
 * @throws Exception
 */
public void testTranscriberNonDeterministic() throws Exception {
	doTestTranscriber( false, 1 );
}

/**
 * @throws Exception
 */
public void testHistoryPlayer() throws Exception {
	Properties props = new Properties();
	props.setProperty( ActivatorTranscriber.RECURRENT_CYCLES_KEY, Integer.toString( 1 ) );
	props.setProperty( RoshamboPlayerTranscriber.DETERMINISTIC_KEY, Boolean.toString( true ) );
	props.setProperty( RoshamboPlayerTranscriber.HISTORY_SIZE_KEY, Integer.toString( 10 ) );

	// test with default and explicitly set to not scanning
	doTestHistoryPlayer( props );
	props.setProperty( RoshamboPlayerTranscriber.SCANNING_WINDOW_SIZE_KEY, Integer.toString( 0 ) );
	doTestHistoryPlayer( props );
}

private void doTestHistoryPlayer( Properties props ) throws Exception {
	RoshamboPlayerTranscriber trans = (RoshamboPlayerTranscriber) props
			.singletonObjectProperty( RoshamboPlayerTranscriber.class );
	assertEquals( "wrong phenotype class", RoshamboPlayer.class, trans.getPhenotypeClass() );
	RoshamboPlayer roshamboPlayer = trans.newRoshamboPlayer( historyRoshamboChromosome );
	assertTrue( "player not AnjiNetRoshamboPlayer",
			roshamboPlayer instanceof AnjiNetRoshamboPlayer );
	assertEquals( "wrong author", "Derek James & Philip Tucker", roshamboPlayer.getAuthor() );
	assertEquals( "wrong player ID", historyRoshamboChromosome.getId().toString(), roshamboPlayer
			.getPlayerId() );
	assertEquals( "wrong toString()", historyRoshamboChromosome.getId().toString(),
			roshamboPlayer.toString() );
}

/**
 * @throws Exception
 */
// TODO
//public void testScanningPlayer() throws Exception {
//	Properties props = new Properties();
//	props.setProperty( RoshamboPlayerTranscriber.RECURRENT_CYCLES_KEY, Integer.toString( 1 ) );
//	props.setProperty( RoshamboPlayerTranscriber.DETERMINISTIC_KEY, Boolean.toString( true ) );
//	props.setProperty( RoshamboPlayerTranscriber.HISTORY_SIZE_KEY, Integer.toString( 10 ) );
//
//	try {
//		props.setProperty( RoshamboPlayerTranscriber.SCANNING_WINDOW_SIZE_KEY, Integer
//				.toString( 40 ) );
//		props.newObjectProperty( RoshamboPlayerTranscriber.class );
//		fail( "should have failed transcribing player with scanning window size greater than history
// size" );
//	}
//	catch ( IllegalArgumentException e ) {
//		// success
//	}
//
//	props.setProperty( RoshamboPlayerTranscriber.SCANNING_WINDOW_SIZE_KEY, Integer.toString( 4 )
// );
//	RoshamboPlayerTranscriber trans = (RoshamboPlayerTranscriber) props
//			.singletonObjectProperty( RoshamboPlayerTranscriber.class );
//	assertEquals( "wrong phenotype class", RoshamboPlayer.class, trans.getPhenotypeClass() );
//	RoshamboPlayer roshamboPlayer = trans.newRoshamboPlayer( scanningRoshamboChromosome );
//	assertTrue( "player not AnjiNetRoshamboPlayer",
//			roshamboPlayer instanceof AnjiNetScanningRoshamboPlayer );
//	assertEquals( "wrong author", "Derek James & Philip Tucker", roshamboPlayer.getAuthor() );
//	assertEquals( "wrong player ID", scanningRoshamboChromosome.getId().toString(),
//			roshamboPlayer.getPlayerId() );
//	assertEquals( "wrong toString()", scanningRoshamboChromosome.getId().toString(),
//			roshamboPlayer.toString() );
//}
private void doTestTranscriber( boolean isDeterministic, int recurrentCycles ) throws Exception {
	Properties props = new Properties();
	props.setProperty( ActivatorTranscriber.RECURRENT_CYCLES_KEY, Integer
			.toString( recurrentCycles ) );
	props.setProperty( RoshamboPlayerTranscriber.DETERMINISTIC_KEY, Boolean
			.toString( isDeterministic ) );

	RoshamboPlayerTranscriber trans = (RoshamboPlayerTranscriber) props
			.singletonObjectProperty( RoshamboPlayerTranscriber.class );
	assertEquals( "wrong phenotype class", RoshamboPlayer.class, trans.getPhenotypeClass() );

	// test 3 transcribe methods
	RoshamboPlayer roshamboPlayer = trans.newRoshamboPlayer( roshamboChromosome );
	Player player = trans.newPlayer( roshamboChromosome );
	Object phenotype = trans.transcribe( roshamboChromosome );
	assertRoshamboPlayerValid( roshamboPlayer, isDeterministic );
	assertRoshamboPlayerValid( (RoshamboPlayer) player, isDeterministic );
	assertRoshamboPlayerValid( (RoshamboPlayer) phenotype, isDeterministic );
}

/**
 * test bad chromosome
 */
public void testBadChromosome() {
	Properties props = new Properties();
	RoshamboPlayerTranscriber trans = (RoshamboPlayerTranscriber) props
			.singletonObjectProperty( RoshamboPlayerTranscriber.class );
	assertEquals( "wrong phenotype class", RoshamboPlayer.class, trans.getPhenotypeClass() );

	// invalid roshambo chromosome
	try {
		trans.newRoshamboPlayer( notRoshamboChromosome );
		fail( "should have failed on bad chromosome" );
	}
	catch ( TranscriberException e ) {
		// success
	}
	try {
		trans.newPlayer( notRoshamboChromosome );
		fail( "should have failed on bad chromosome" );
	}
	catch ( TranscriberException e ) {
		// success
	}
	try {
		trans.transcribe( notRoshamboChromosome );
		fail( "should have failed on bad chromosome" );
	}
	catch ( TranscriberException e ) {
		// success
	}
}

private void assertRoshamboPlayerValid( RoshamboPlayer player, boolean isDeterministic ) {
	assertEquals( "wrong author", "Derek James & Philip Tucker", player.getAuthor() );
	assertEquals( "wrong player ID", roshamboChromosome.getId().toString(), player.getPlayerId() );
	assertEquals( "wrong toString()", roshamboChromosome.getId().toString(), player.toString() );
	player.reset( EXPECTED_MOVES.length * 2 );
	player.reset( EXPECTED_MOVES.length );
	for ( int i = 0; i < EXPECTED_MOVES.length; ++i ) {
		if ( isDeterministic )
			assertEquals( "wrong move " + i, EXPECTED_MOVES[ i ], player.nextMove() );
		player.storeMove( OPPONENT_MOVES[ i ], SCORES[ i ] );
	}
}

}
