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
 * created by Philip Tucker on May 15, 2003
 */
package com.anji.integration.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Genotype;
import org.jgap.event.GeneticEvent;

import com.anji.integration.ConsoleLogEventListener;
import com.anji.util.DummyConfiguration;

/**
 * @author Philip Tucker
 */
public class ConsoleLogEventListenerTest extends TestCase {

private ByteArrayOutputStream outBytes = new ByteArrayOutputStream();

private PrintStream out = new PrintStream( outBytes );

private ByteArrayOutputStream errBytes = new ByteArrayOutputStream();

private ConsoleLogEventListener uut = null;

/**
 * ctor
 */
public ConsoleLogEventListenerTest() {
	this( ConsoleLogEventListenerTest.class.getName() );
}

/**
 * ctor
 * @param name
 */
public ConsoleLogEventListenerTest( String name ) {
	super( name );
}

/**
 * test evolved event
 * @throws Exception
 */
public void testGenotypeEvolvedEvent() throws Exception {
	Configuration config = new DummyConfiguration();
	config.setPopulationSize( 100 );
	Genotype genotype = Genotype.randomInitialGenotype( config );
	GeneticEvent event = new GeneticEvent( GeneticEvent.GENOTYPE_EVOLVED_EVENT, genotype );
	uut = new ConsoleLogEventListener( config, out );
	uut.geneticEventFired( event );

	Chromosome fittest = genotype.getFittestChromosome();
	int maxFitnessValue = ( config.getBulkFitnessFunction() != null ) ? config
			.getBulkFitnessFunction().getMaxFitnessValue() : config.getFitnessFunction()
			.getMaxFitnessValue();
	String outExpected = "species count: " + genotype.getSpecies().size() + "\r\n"
			+ "fittest chromosome: " + fittest.getId() + ", score == "
			+ ( (double) fittest.getFitnessValue() / maxFitnessValue ) + " and # genes == "
			+ fittest.getAlleles().size() + "\r\n";

	assertEquals( "wrong info written to stdout", outExpected, outBytes.toString() );
	assertEquals( "wrong info written to stderr", 0, errBytes.size() );
}

}
