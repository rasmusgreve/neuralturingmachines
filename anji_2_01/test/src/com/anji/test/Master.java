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
 * created by Philip Tucker on Mar 12, 2003
 */
package com.anji.test;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.anji.Copyright;
import com.anji.fingerprint.test.ClassifierResultTest;
import com.anji.floatingeye.test.AnjiNetFloatingEyeTest;
import com.anji.floatingeye.test.EyePixelConnectionTest;
import com.anji.floatingeye.test.FloatingEyeTest;
import com.anji.imaging.test.CoordinateTranslatorTest;
import com.anji.imaging.test.ImageRandomizerTest;
import com.anji.imaging.test.Java2DSurfaceTest;
import com.anji.integration.test.ActivatorTest;
import com.anji.integration.test.ConsoleLogEventListenerTest;
import com.anji.integration.test.PersistenceEventListenerTest;
import com.anji.integration.test.SimpleSelectorTest;
import com.anji.neat.test.AddConnectionMutationOperatorTest;
import com.anji.neat.test.AddNeuronMutationOperatorTest;
import com.anji.neat.test.AnjiTranscriberTest;
import com.anji.neat.test.ConnectionGeneTest;
import com.anji.neat.test.EvolverTest;
import com.anji.neat.test.NeatChromosomeUtilityTest;
import com.anji.neat.test.NeatConfigurationTest;
import com.anji.neat.test.NeatCrossoverReproductionOperatorTest;
import com.anji.neat.test.NeuronGeneTest;
import com.anji.neat.test.PruneMutationOperatorTest;
import com.anji.neat.test.RemoveConnectionMutationOperatorAllTest;
import com.anji.neat.test.RemoveConnectionMutationOperatorSkewedTest;
import com.anji.neat.test.RemoveConnectionMutationOperatorSmallTest;
import com.anji.neat.test.TargetFitnessFunctionTest;
import com.anji.neat.test.TranscriberTest;
import com.anji.neat.test.WeightMutationOperatorTest;
import com.anji.nn.test.AnjiActivatorTest;
import com.anji.nn.test.AnjiNetTest;
import com.anji.nn.test.InverseAbsActivationFunctionTest;
import com.anji.nn.test.LinearActivationFunctionTest;
import com.anji.nn.test.NeuronConnectionTest;
import com.anji.nn.test.NeuronTest;
import com.anji.nn.test.PatternTest;
import com.anji.nn.test.SigmoidActivationFunctionTest;
import com.anji.nn.test.StepHourglassConnectionTest;
import com.anji.nn.test.TanhActivationFunctionTest;
import com.anji.nn.test.TanhCubicActivationFunctionTest;
import com.anji.persistence.test.FilePersistenceTest;
import com.anji.roshambo.test.AnjiNetRoshamboPlayerTest;
import com.anji.roshambo.test.RoshamboPlayerTest;
import com.anji.tournament.test.CompositeTournamentTest;
import com.anji.tournament.test.DirectTournamentTest;
import com.anji.tournament.test.DoubleEliminationTournamentTest;
import com.anji.tournament.test.GameResultsTest;
import com.anji.tournament.test.IteratedGameTest;
import com.anji.tournament.test.KRandomOppsTournamentTest;
import com.anji.tournament.test.PlayerResultsScoreComparatorTest;
import com.anji.tournament.test.SingleEliminationTournamentTest;
import com.anji.util.test.PropertiesTest;

/**
 * @author Philip Tucker
 */
public class Master extends TestCase {

/**
 * @return new suite
 */
public static TestSuite suite() {
	TestSuite suite = new TestSuite();

	// fingerprint
	suite.addTest( new TestSuite( ClassifierResultTest.class ) );

	// floating eye
	suite.addTest( new TestSuite( AnjiNetFloatingEyeTest.class ) );
	suite.addTest( new TestSuite( EyePixelConnectionTest.class ) );
	suite.addTest( new TestSuite( FloatingEyeTest.class ) );

	// imaging
	suite.addTest( new TestSuite( CoordinateTranslatorTest.class ) );
	suite.addTest( new TestSuite( ImageRandomizerTest.class ) );
	suite.addTest( new TestSuite( Java2DSurfaceTest.class ) );

	// integration
	suite.addTest( new TestSuite( ActivatorTest.class ) );
	suite.addTest( new TestSuite( ConsoleLogEventListenerTest.class ) );
	suite.addTest( new TestSuite( PersistenceEventListenerTest.class ) );
	suite.addTest( new TestSuite( SimpleSelectorTest.class ) );

	// neat
	suite.addTest( new TestSuite( AddConnectionMutationOperatorTest.class ) );
	suite.addTest( new TestSuite( AddNeuronMutationOperatorTest.class ) );
	suite.addTest( new TestSuite( AnjiTranscriberTest.class ) );
	suite.addTest( new TestSuite( ConnectionGeneTest.class ) );
	suite.addTest( new TestSuite( NeatChromosomeUtilityTest.class ) );
	suite.addTest( new TestSuite( NeatConfigurationTest.class ) );
	suite.addTest( new TestSuite( NeatCrossoverReproductionOperatorTest.class ) );
	suite.addTest( new TestSuite( NeuronGeneTest.class ) );
	suite.addTest( new TestSuite( PruneMutationOperatorTest.class ) );
	suite.addTest( new TestSuite( RemoveConnectionMutationOperatorSmallTest.class ) );
	suite.addTest( new TestSuite( RemoveConnectionMutationOperatorAllTest.class ) );
	suite.addTest( new TestSuite( RemoveConnectionMutationOperatorSkewedTest.class ) );
	suite.addTest( new TestSuite( TargetFitnessFunctionTest.class ) );
	suite.addTest( new TestSuite( TranscriberTest.class ) );
	suite.addTest( new TestSuite( WeightMutationOperatorTest.class ) );

	// nn
	suite.addTest( new TestSuite( AnjiActivatorTest.class ) );
	suite.addTest( new TestSuite( AnjiNetTest.class ) );
	//	suite.addTest(new TestSuite(EvSailSigmoidActivationFunctionTest.class));
	suite.addTest( new TestSuite( InverseAbsActivationFunctionTest.class ) );
	suite.addTest( new TestSuite( LinearActivationFunctionTest.class ) );
	// suite.addTest( new TestSuite( MeasureNetTimingTest.class ) );
	suite.addTest( new TestSuite( NeuronConnectionTest.class ) );
	suite.addTest( new TestSuite( NeuronTest.class ) );
	suite.addTest( new TestSuite( PatternTest.class ) );
	suite.addTest( new TestSuite( SigmoidActivationFunctionTest.class ) );
	suite.addTest( new TestSuite( StepHourglassConnectionTest.class ) );
	suite.addTest( new TestSuite( TanhActivationFunctionTest.class ) );
	suite.addTest( new TestSuite( TanhCubicActivationFunctionTest.class ) );

	// persistence
	suite.addTest( new TestSuite( FilePersistenceTest.class ) );

	// roshambo
	suite.addTest( new TestSuite( AnjiNetRoshamboPlayerTest.class ) );
	suite.addTest( new TestSuite( RoshamboPlayerTest.class ) );

	// tournament
	suite.addTest( new TestSuite( CompositeTournamentTest.class ) );
	suite.addTest( new TestSuite( DirectTournamentTest.class ) );
	suite.addTest( new TestSuite( DoubleEliminationTournamentTest.class ) );
	suite.addTest( new TestSuite( GameResultsTest.class ) );
	suite.addTest( new TestSuite( IteratedGameTest.class ) );
	suite.addTest( new TestSuite( KRandomOppsTournamentTest.class ) );
	suite.addTest( new TestSuite( PlayerResultsScoreComparatorTest.class ) );
	suite.addTest( new TestSuite( SingleEliminationTournamentTest.class ) );

	// util
	suite.addTest( new TestSuite( PropertiesTest.class ) );

	// run this one last since it takes so long
	suite.addTest( new TestSuite( EvolverTest.class ) );

	return suite;
}

/**
 * @param args
 */
public static void main( String[] args ) {
	System.out.println( Copyright.STRING );
	TestRunner.run( suite() );
}

}
