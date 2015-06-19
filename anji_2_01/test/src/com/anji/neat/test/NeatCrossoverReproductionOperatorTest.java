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
 * created by Philip Tucker on Mar 15, 2003
 */
package com.anji.neat.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;

import org.jgap.Chromosome;
import org.jgap.ChromosomeMaterial;
import org.jgap.Specie;
import org.jgap.test.CrossoverReproductionOperatorTest;

import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.NeatCrossoverReproductionOperator;
import com.anji.neat.NeuronType;
import com.anji.util.DummyBulkFitnessFunction;
import com.anji.util.Properties;
import com.anji.util.Reset;

/**
 * @author Philip Tucker
 */
public class NeatCrossoverReproductionOperatorTest extends CrossoverReproductionOperatorTest {

private final static String PROP_FILE_NAME = "test.properties";

private final static int POPULATION_SIZE = 100;

private final static int PARENTS_NUM_SPECIES = 5;

private final static int PARENTS_NUM_CHROMOSOMES = 50;

private final static float SLICE = 0.25f;

private final static short DIM_INPUTS = 4;

private final static short DIM_OUTPUTS = 2;

/**
 * ctor
 */
public NeatCrossoverReproductionOperatorTest() {
	this( NeatCrossoverReproductionOperatorTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public NeatCrossoverReproductionOperatorTest( String name ) {
	super( name );
}

/**
 * @see org.jgap.test.CrossoverReproductionOperatorTest#initUut()
 */
protected void initUut() throws Exception {
	// uut
	uut = new NeatCrossoverReproductionOperator();
	uut.setSlice( SLICE );
	assertEquals( "setSlice/getSlice failed", SLICE, uut.getSlice(), 0.0f );
}

/**
 * @see org.jgap.test.CrossoverReproductionOperatorTest#initConfig()
 */
protected void initConfig() throws Exception {
	Properties props = new Properties( PROP_FILE_NAME );
	props.setProperty( NeatConfiguration.STIMULUS_SIZE_KEY, "" + DIM_INPUTS );
	props.setProperty( NeatConfiguration.RESPONSE_SIZE_KEY, "" + DIM_OUTPUTS );

	// clear previous stored configs and populations
	Reset reset = new Reset( props );
	reset.setUserInteraction( false );
	reset.reset();

	// config
	config = new NeatConfiguration( props );
	config.getRandomGenerator().setSeed( 0 );
	config.setPopulationSize( POPULATION_SIZE );
	config.getSpeciationParms().setSpeciationThreshold( 0.8d );
	config.setBulkFitnessFunction( new DummyBulkFitnessFunction( config.getRandomGenerator() ) );
	( (NeatConfiguration) config ).load();
}

/**
 * @see org.jgap.test.CrossoverReproductionOperatorTest#initParentSpecies()
 */
protected void initParentSpecies() throws Exception {
	parentSpecies = new ArrayList();
	for ( int i = 0; i < PARENTS_NUM_SPECIES; ++i ) {
		Specie specie = null;
		for ( int j = 0; j < PARENTS_NUM_CHROMOSOMES; ++j ) {
			ChromosomeMaterial material = ChromosomeMaterial.randomInitialChromosomeMaterial( config );
			Chromosome chrom = new Chromosome( material, config.nextChromosomeId() );
			parentIds.add( chrom.getId() );
			if ( j == 0 )
				specie = new Specie( config.getSpeciationParms(), chrom );
			else
				specie.add( chrom );
			chrom.setIsSelectedForNextGeneration( true );
		}
		parentSpecies.add( specie );
	}
}

/**
 * @see org.jgap.test.CrossoverReproductionOperatorTest#doTestAfterReproduce(java.util.List)
 */
protected void doTestAfterReproduce( List offspring ) throws Exception {
	Iterator it = offspring.iterator();
	while ( it.hasNext() ) {
		ChromosomeMaterial child = (ChromosomeMaterial) it.next();
		SortedMap inputs = NeatChromosomeUtility.getNeuronMap( child.getAlleles(), NeuronType.INPUT );
		SortedMap outputs = NeatChromosomeUtility
				.getNeuronMap( child.getAlleles(), NeuronType.OUTPUT );
		assertEquals( "wrong # ins", DIM_INPUTS, inputs.size() );
		assertEquals( "wrong # outs", DIM_OUTPUTS, outputs.size() );

		assertNotNull( "null parent1 id", child.getPrimaryParentId() );
		assertTrue( "both parents same", child.getPrimaryParentId().equals(
				child.getSecondaryParentId() ) == false );
		if ( child.getPrimaryParentId().equals( oneParentSpecie.getRepresentativeId() ) )
			assertNull( "not null parent2 id", child.getSecondaryParentId() );
		else {
			assertNotNull( "null parent2 id", child.getSecondaryParentId() );
			assertTrue( "invalid parent2 ", parentIds.contains( child.getSecondaryParentId() ) );
		}
		assertTrue( "invalid parent1 ", parentIds.contains( child.getPrimaryParentId() ) );

		NeatChromosomeUtilityTest.validate( child.getAlleles() );

		// TODO - add expected values
	}
}

}
