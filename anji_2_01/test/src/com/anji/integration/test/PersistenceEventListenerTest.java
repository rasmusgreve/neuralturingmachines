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
 * Created on May 14, 2005 by Philip Tucker
 */
package com.anji.integration.test;

import java.io.File;

import junit.framework.TestCase;

import org.jgap.Chromosome;
import org.jgap.Genotype;
import org.jgap.event.GeneticEvent;

import com.anji.integration.PersistenceEventListener;
import com.anji.integration.XmlPersistableRun;
import com.anji.integration.XmlPersistableChromosome;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.NeatIdMap;
import com.anji.neat.test.TestNeatConfigurationFactory;
import com.anji.persistence.FilePersistence;
import com.anji.persistence.Persistence;
import com.anji.run.Run;
import com.anji.util.Properties;
import com.anji.util.Reset;

/**
 * @author Philip Tucker
 */
public class PersistenceEventListenerTest extends TestCase {

private Properties props = new Properties();

private File chromDir = null;

private File runDir = null;

private File neatIdFile = null;

private File idFile = null;

private NeatConfiguration config;

private Genotype genotype;

private Run run;

/**
 * ctor
 */
public PersistenceEventListenerTest() {
	super();
}

/**
 * ctor
 * @param name
 */
public PersistenceEventListenerTest( String name ) {
	super( name );
}

/**
 * @see junit.framework.TestCase#setUp()
 */
protected void setUp() throws Exception {
	props.loadFromResource( "test.properties" );

	Reset reset = new Reset( props );
	reset.setUserInteraction( false );
	reset.reset();

	// assume FilePersistence
	File dbBaseDir = props.getDirProperty( Persistence.PERSISTENCE_CLASS_KEY + "."
			+ FilePersistence.BASE_DIR_KEY );
	File[] dbFiles = dbBaseDir.listFiles();
	for ( int i = 0; i < dbFiles.length; ++i ) {
		File f = dbFiles[ i ];
		if ( f.getName().equalsIgnoreCase( XmlPersistableChromosome.XML_CHROMOSOME_TAG ) )
			chromDir = f;
		else if ( f.getName().equalsIgnoreCase( XmlPersistableRun.RUN_TAG ) )
			runDir = f;
	}

	// ID files
	neatIdFile = new File( props.getProperty( NeatIdMap.NEAT_ID_MAP_FILE_KEY ) );
	idFile = new File( props.getProperty( NeatConfiguration.ID_FACTORY_KEY ) );
	assertFalse( "neat ID file exists", neatIdFile.exists() );
	assertFalse( "ID file exists", idFile.exists() );

	// genotype
	config = TestNeatConfigurationFactory.getInstance().buildConfig( props );
	genotype = Genotype.randomInitialGenotype( config );

	// assert initial state
	assertNotNull( "no chromosome directory", chromDir );
	assertNotNull( "no run directory", runDir );
	assertFileCount( chromDir, 0 );
	assertFileCount( runDir, 0 );
	assertTrue( "neat ID file does not exist", neatIdFile.exists() );
	assertTrue( "ID file does not exist", idFile.exists() );

	run = new Run( "testrun" );
}

private void assertFileCount( File dir, int count ) {
	assertTrue( dir.getAbsolutePath() + ": not a directory", dir.isDirectory() );
	assertEquals( dir.getAbsolutePath() + ": wrong number files", count, dir.listFiles().length );
}

/**
 * test persist all chromosomes
 * @throws Exception
 */
public void testPersistAll_1() throws Exception {
	doTestIt( true, false, false );
}

/**
 * test persist all chromosomes
 * @throws Exception
 */
public void testPersistAll_2() throws Exception {
	doTestIt( true, false, true );
}

/**
 * test persist all chromosomes
 * @throws Exception
 */
public void testPersistAll_3() throws Exception {
	doTestIt( true, true, false );
}

/**
 * test persist all chromosomes
 * @throws Exception
 */
public void testPersistAll_4() throws Exception {
	doTestIt( true, true, true );
}

/**
 * test persist all chromosomes
 * @throws Exception
 */
public void testPersistSome_1() throws Exception {
	doTestIt( false, false, false );
}

/**
 * test persist all chromosomes
 * @throws Exception
 */
public void testPersistSome_2() throws Exception {
	doTestIt( false, false, true );
}

/**
 * test persist all chromosomes
 * @throws Exception
 */
public void testPersistSome_3() throws Exception {
	doTestIt( false, true, false );
}

/**
 * test persist all chromosomes
 * @throws Exception
 */
public void testPersistSome_4() throws Exception {
	doTestIt( false, true, true );
}

private void doTestIt( boolean persistAll, boolean persistChamps, boolean persistLast )
		throws Exception {
	// iniitalize persistence event listener
	props.setProperty( PersistenceEventListener.PERSIST_ALL_CHROMOSOMES_KEY, Boolean
			.toString( persistAll ) );
	props.setProperty( PersistenceEventListener.PERSIST_CHAMPIONS_KEY, Boolean
			.toString( persistChamps ) );
	props.setProperty( PersistenceEventListener.PERSIST_LAST_GEN_KEY, Boolean
			.toString( persistLast ) );
	PersistenceEventListener uut = new PersistenceEventListener( config, run );
	uut.init( props );

	// genetic operators start event
	GeneticEvent event = new GeneticEvent( GeneticEvent.GENOTYPE_START_GENETIC_OPERATORS_EVENT,
			genotype );
	uut.geneticEventFired( event );
	assertTrue( "neat ID file does not exist", neatIdFile.exists() );
	assertTrue( "ID file does not exist", idFile.exists() );
	assertFileCount( chromDir, 0 );
	assertFileCount( runDir, 0 );

	// genetic operators finish event
	assertTrue( "error deleting neat id file", neatIdFile.delete() );
	assertTrue( "error deleting id file", idFile.delete() );
	event = new GeneticEvent( GeneticEvent.GENOTYPE_FINISH_GENETIC_OPERATORS_EVENT, genotype );
	uut.geneticEventFired( event );
	assertTrue( "neat ID file does not exist", neatIdFile.exists() );
	assertTrue( "ID file does not exist", idFile.exists() );
	assertFileCount( chromDir, 0 );
	assertFileCount( runDir, 0 );

	// evaluated event - generation 1
	event = new GeneticEvent( GeneticEvent.GENOTYPE_EVALUATED_EVENT, genotype );
	uut.geneticEventFired( event );
	assertTrue( "neat ID file does not exist", neatIdFile.exists() );
	assertTrue( "ID file does not exist", idFile.exists() );
	int expectedChromosomeCount = ( persistAll || persistLast ) ? genotype.getChromosomes()
			.size() : ( persistChamps ? 1 : 0 );
	assertFileCount( chromDir, expectedChromosomeCount );
	assertFileCount( runDir, 1 );

	// mimic new generation
	Chromosome champ = genotype.getFittestChromosome();
	genotype = Genotype.randomInitialGenotype( config );
	champ.setFitnessValue( Integer.MAX_VALUE );
	genotype.getChromosomes().set( 0, champ );

	// evaluated event - generation 2
	event = new GeneticEvent( GeneticEvent.GENOTYPE_EVALUATED_EVENT, genotype );
	uut.geneticEventFired( event );
	assertTrue( "neat ID file does not exist", neatIdFile.exists() );
	assertTrue( "ID file does not exist", idFile.exists() );
	expectedChromosomeCount = persistAll ? ( ( genotype.getChromosomes().size() * 2 ) - 1 )
			: ( persistLast ? genotype.getChromosomes().size() : persistChamps ? 1 : 0 );
	assertFileCount( chromDir, expectedChromosomeCount );
	assertFileCount( runDir, 1 );

	// mimic new generation
	genotype = Genotype.randomInitialGenotype( config );

	// evaluated event - generation 3
	event = new GeneticEvent( GeneticEvent.GENOTYPE_EVALUATED_EVENT, genotype );
	uut.geneticEventFired( event );
	assertTrue( "neat ID file does not exist", neatIdFile.exists() );
	assertTrue( "ID file does not exist", idFile.exists() );
	if ( persistAll )
		expectedChromosomeCount = ( genotype.getChromosomes().size() * 3 ) - 1;
	else {
		if ( persistLast ) {
			if ( persistChamps )
				expectedChromosomeCount = genotype.getChromosomes().size() + 1;
			else
				expectedChromosomeCount = genotype.getChromosomes().size();
		}
		else {
			if ( persistChamps )
				expectedChromosomeCount = 2;
			else
				expectedChromosomeCount = 0;
		}
	}
	assertFileCount( chromDir, expectedChromosomeCount );
	assertFileCount( runDir, 1 );
}

}
