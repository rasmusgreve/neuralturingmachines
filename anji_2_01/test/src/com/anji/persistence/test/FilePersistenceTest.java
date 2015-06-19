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
 * created by Philip Tucker on Jun 4, 2003
 */
package com.anji.persistence.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.jgap.Allele;
import org.jgap.Chromosome;
import org.jgap.test.DummyFitnessFunction;
import org.w3c.dom.Document;

import com.anji.integration.XmlPersistableChromosome;
import com.anji.neat.ConnectionAllele;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.NeuronAllele;
import com.anji.neat.NeuronType;
import com.anji.persistence.FilePersistence;
import com.anji.persistence.Persistence;
import com.anji.util.Properties;
import com.anji.util.Reset;
import com.anji.util.XmlPersistable;

/**
 * @author Philip Tucker
 */
public class FilePersistenceTest extends TestCase {

private final static String PROP_FILE_NAME = "test.properties";

private static class TestXmlPersistable implements XmlPersistable {

final static String TEST_TYPE = "testtype";

final static String TEST_KEY = "testkey";

final static String TEST_DATA = "<tag>This is my data.</tag>";

/**
 * @see com.anji.util.XmlPersistable#toXml()
 */
public String toXml() {
	return TEST_DATA;
}

/**
 * @see com.anji.util.XmlPersistable#getXmlRootTag()
 */
public String getXmlRootTag() {
	return TEST_TYPE;
}

/**
 * @see com.anji.util.XmlPersistable#getXmld()
 */
public String getXmld() {
	return TEST_KEY;
}

}

private FilePersistence uut;

private String testBaseDir = null;

private NeatConfiguration config = null;

private final static short DIM_STIMULI = 22;

private final static short DIM_RESPONSE = 10;

/**
 * ctor
 */
public FilePersistenceTest() {
	this( FilePersistenceTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public FilePersistenceTest( String name ) {
	super( name );
}

private void deleteTestDir() {
	File file = new File( testBaseDir + "/" + TestXmlPersistable.TEST_TYPE );
	file.delete();
}

/**
 * @see junit.framework.TestCase#setUp()
 */
public void setUp() throws Exception {
	Properties props = new Properties( PROP_FILE_NAME );
	props.setProperty( NeatConfiguration.STIMULUS_SIZE_KEY, "" + DIM_STIMULI );
	props.setProperty( NeatConfiguration.RESPONSE_SIZE_KEY, "" + DIM_RESPONSE );

	uut = (FilePersistence) props.newObjectProperty( Persistence.PERSISTENCE_CLASS_KEY );

	testBaseDir = props.getProperty( Persistence.PERSISTENCE_CLASS_KEY + "."
			+ FilePersistence.BASE_DIR_KEY );
	deleteTestDir();

	// clear previous stored configs and populations
	Reset reset = new Reset( props );
	reset.setUserInteraction( false );
	reset.reset();

	// config
	config = new NeatConfiguration( props );
	config.setFitnessFunction( new DummyFitnessFunction( config.getRandomGenerator() ) );
	config.setPopulationSize( 100 );
	config.getRandomGenerator().setSeed( 0 );
	config.lockSettings();
	config.load();
}

/**
 * @see junit.framework.TestCase#tearDown()
 */
public void tearDown() {
	deleteTestDir();
}

/**
 * test file persistence
 * @throws Exception
 */
public void testIt() throws Exception {
	// TODO
	fail( "TODO" );
}

/**
 * XML contains no chromosome ID
 * @throws Exception
 */
public void testFromXmlWithoutId() throws Exception {
	doTestFromXml( null );
}

/**
 * XML contains chromosome ID
 * @throws Exception
 */
public void testFromXmlWithId() throws Exception {
	doTestFromXml( new Long( 123 ) );
}

private void doTestFromXml( Long id ) throws Exception {
	Map expectedAlleles = new HashMap();
	NeuronAllele nGene1 = config.newNeuronAllele( NeuronType.INPUT );
	expectedAlleles.put( nGene1, nGene1 );
	NeuronAllele nGene2 = config.newNeuronAllele( NeuronType.INPUT );
	expectedAlleles.put( nGene2, nGene2 );
	NeuronAllele nGene3 = config.newNeuronAllele( NeuronType.INPUT );
	expectedAlleles.put( nGene3, nGene3 );
	NeuronAllele nGene4 = config.newNeuronAllele( NeuronType.OUTPUT );
	expectedAlleles.put( nGene4, nGene4 );
	NeuronAllele nGene5 = config.newNeuronAllele( NeuronType.HIDDEN );
	expectedAlleles.put( nGene5, nGene5 );
	ConnectionAllele cgene1 = config.newConnectionAllele( new Long( 0 ), new Long( 2 ) );
	cgene1.setWeight( -1.0d );
	expectedAlleles.put( cgene1, cgene1 );
	ConnectionAllele cgene2 = config.newConnectionAllele( new Long( 1 ), new Long( 2 ) );
	cgene2.setWeight( 0.8150344685135649d );
	expectedAlleles.put( cgene2, cgene2 );
	ConnectionAllele cgene3 = config.newConnectionAllele( new Long( 2 ), new Long( 2 ) );
	cgene3.setWeight( -1.0d );
	expectedAlleles.put( cgene3, cgene3 );
	ConnectionAllele cgene4 = config.newConnectionAllele( new Long( 0 ), new Long( 1 ) );
	cgene4.setWeight( -1.0d );
	expectedAlleles.put( cgene4, cgene4 );
	ConnectionAllele cgene5 = config.newConnectionAllele( new Long( 0 ), new Long( 0 ) );
	cgene5.setWeight( 1.0d );
	expectedAlleles.put( cgene5, cgene5 );
	ConnectionAllele cgene6 = config.newConnectionAllele( new Long( 3 ), new Long( 1 ) );
	cgene6.setWeight( -0.4941929578781128d );
	expectedAlleles.put( cgene6, cgene6 );
	ConnectionAllele cgene7 = config.newConnectionAllele( new Long( 1 ), new Long( 4 ) );
	cgene7.setWeight( 1.0d );
	expectedAlleles.put( cgene7, cgene7 );
	ConnectionAllele cgene8 = config.newConnectionAllele( new Long( 4 ), new Long( 2 ) );
	cgene8.setWeight( -0.4941525578781128d );
	expectedAlleles.put( cgene8, cgene8 );

	String xml = "<chromosome";
	if ( id != null )
		xml = xml + " id=\"" + id + "\"";
	xml = xml + ">" + "<neuron id=\"" + nGene1.getInnovationId() + "\" type=\"in\" activation=\""
			+ nGene1.getActivationType().toString() + "\"/>" + "<neuron id=\""
			+ nGene2.getInnovationId() + "\" type=\"in\" activation=\""
			+ nGene2.getActivationType().toString() + "\"/>" + "<neuron id=\""
			+ nGene3.getInnovationId() + "\" type=\"in\" activation=\""
			+ nGene3.getActivationType().toString() + "\"/>" + "<neuron id=\""
			+ nGene4.getInnovationId() + "\" type=\"out\" activation=\""
			+ nGene4.getActivationType().toString() + "\"/>" + "<connection id=\""
			+ cgene1.getInnovationId()
			+ "\" src-id=\"0\" dest-id=\"3\" weight=\"-1.0\" recurrent=\"false\"/>"
			+ "<connection id=\"" + cgene2.getInnovationId()
			+ "\" src-id=\"1\" dest-id=\"3\" weight=\"0.8150344685135649\" recurrent=\"false\"/>"
			+ "<connection id=\"" + cgene3.getInnovationId()
			+ "\" src-id=\"2\" dest-id=\"3\" weight=\"-1.0\" recurrent=\"true\"/>"
			+ "<connection id=\"" + cgene4.getInnovationId()
			+ "\" src-id=\"0\" dest-id=\"1\" weight=\"-1.0\" recurrent=\"false\"/>"
			+ "<connection id=\"" + cgene5.getInnovationId()
			+ "\" src-id=\"0\" dest-id=\"0\" weight=\"1.0\" recurrent=\"true\"/>"
			+ "<connection id=\"" + cgene6.getInnovationId()
			+ "\" src-id=\"3\" dest-id=\"1\" weight=\"-0.4941929578781128\" recurrent=\"false\"/>"
			+ "<neuron id=\"" + nGene5.getInnovationId() + "\" type=\"hid\" activation=\""
			+ nGene5.getActivationType().toString() + "\"/>" + "<connection id=\""
			+ cgene7.getInnovationId()
			+ "\" src-id=\"1\" dest-id=\"4\" weight=\"1.0\" recurrent=\"false\"/>"
			+ "<connection id=\"" + cgene8.getInnovationId()
			+ "\" src-id=\"4\" dest-id=\"2\" weight=\"-0.4941525578781128\" recurrent=\"false\"/>"
			+ "</chromosome>";
	DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	Document doc = builder.parse( new ByteArrayInputStream( xml.getBytes() ) );
	Chromosome chrom = FilePersistence.chromosomeFromXml( config, doc.getFirstChild() );
	doTestToXml( chrom );

	Long expectedId = ( id == null ) ? new Long( config.nextChromosomeId().longValue() - 1 ) : id;
	assertEquals( "wrong chromosome id", expectedId, chrom.getId() );

	SortedSet alleles = chrom.getAlleles();
	assertEquals( "wrong # genes", expectedAlleles.size(), alleles.size() );
	Iterator iter = alleles.iterator();
	while ( iter.hasNext() ) {
		Allele allele = (Allele) iter.next();
		assertTrue( "unexpected gene found", expectedAlleles.keySet().contains( allele ) );
		Allele expectedAllele = (Allele) expectedAlleles.get( allele );
		if ( expectedAllele instanceof ConnectionAllele ) {
			ConnectionAllele cAllele = (ConnectionAllele) allele;
			ConnectionAllele expectedCAllele = (ConnectionAllele) expectedAllele;
			assertEquals( "wrong weight", expectedCAllele.getWeight(), cAllele.getWeight(), 0.0d );
			assertEquals( "wrong innovation id", expectedCAllele.getInnovationId(), cAllele
					.getInnovationId() );
		}
		else if ( expectedAllele instanceof NeuronAllele ) {
			NeuronAllele ngene = (NeuronAllele) allele;
			NeuronAllele expectedNgene = (NeuronAllele) expectedAllele;
			assertEquals( "wrong activation", expectedNgene.getActivationType(), ngene
					.getActivationType() );
			assertEquals( "wrong innovation id", expectedNgene.getInnovationId(), ngene
					.getInnovationId() );
			assertEquals( "wrong neuron type", expectedNgene.getType(), ngene.getType() );
		}
		else
			fail( "unexpected gene type: " + allele.getClass().toString() );
	}
}

private static void doTestToXml( Chromosome orig ) throws Exception {
	XmlPersistableChromosome c = new XmlPersistableChromosome( orig );
	String xml = c.toXml();
	NeatConfiguration config = new NeatConfiguration( new Properties() );
	config.setFitnessFunction( new DummyFitnessFunction() );
	DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	Document doc = builder.parse( new ByteArrayInputStream( xml.getBytes() ) );
	Chromosome copy = FilePersistence.chromosomeFromXml( config, doc.getFirstChild() );
	assertEquals( "ids different", orig.getId(), copy.getId() );
	SortedSet origAlleles = orig.getAlleles();
	SortedSet copyAlleles = copy.getAlleles();
	assertTrue( "lost some genes", copyAlleles.containsAll( origAlleles ) );
	assertTrue( "gained some genes", origAlleles.containsAll( copyAlleles ) );
}
}
