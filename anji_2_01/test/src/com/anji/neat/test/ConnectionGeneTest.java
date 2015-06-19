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

import java.io.ByteArrayInputStream;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;

import com.anji.integration.XmlPersistableAllele;
import com.anji.neat.ConnectionAllele;
import com.anji.neat.ConnectionGene;

/**
 * @author Philip Tucker
 */
public class ConnectionGeneTest extends TestCase {

/**
 * exposes constructor for testing
 * @author Philip Tucker
 */
protected class TestConnectionAllele extends ConnectionAllele {

/**
 * ctor
 * @param newInnovationId
 * @param newSrcNeuronId
 * @param newDestNeuronId
 */
public TestConnectionAllele( Long newInnovationId, Long newSrcNeuronId, Long newDestNeuronId ) {
	super( new ConnectionGene( newInnovationId, newSrcNeuronId, newDestNeuronId ) );
}
}

/**
 * ctor
 */
public ConnectionGeneTest() {
	this( ConnectionGeneTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public ConnectionGeneTest( String name ) {
	super( name );
}

private void assertConnectionAlleleEquals( ConnectionAllele connAllele, Long id, Long srcId,
		Long destId, double weight ) {
	assertEquals( "wrong innovation id", id, connAllele.getInnovationId() );
	assertEquals( "wrong src id", srcId, connAllele.getSrcNeuronId() );
	assertEquals( "wrong dest id", destId, connAllele.getDestNeuronId() );
	assertEquals( "wrong weight", weight, connAllele.getWeight(), 0.0d );
}

/**
 * test constructor
 * @throws Exception
 */
public void testConstructor() throws Exception {
	Long id = new Long( 50 );
	Long srcId = new Long( 100 );
	Long destId = new Long( 200 );
	ConnectionAllele allele = new TestConnectionAllele( id, srcId, destId );
	assertConnectionAlleleEquals( allele, id, srcId, destId, ConnectionAllele.DEFAULT_WEIGHT );
}

/**
 * test accessor methods
 * @throws Exception
 */
public void testAccessors() throws Exception {
	Long id = new Long( 50 );
	Long srcId = new Long( 100 );
	Long destId = new Long( 200 );
	double weight = 0.5d;

	ConnectionAllele allele = new TestConnectionAllele( id, srcId, destId );
	allele.setWeight( weight );
	assertConnectionAlleleEquals( allele, id, srcId, destId, weight );
}

/**
 * test <code>cloneGene()</code> method
 * @throws Exception
 */
public void testCloneAllele() throws Exception {
	Long id = new Long( 50 );
	Long srcId = new Long( 100 );
	Long destId = new Long( 200 );
	double weight = 0.5d;
	ConnectionAllele orig = new TestConnectionAllele( id, srcId, destId );
	orig.setWeight( weight );

	ConnectionAllele copy = (ConnectionAllele) orig.cloneAllele();
	assertConnectionAlleleEquals( copy, id, srcId, destId, weight );
	assertEquals( "should be equal", orig, copy );
}

/**
 * test <code>compareTo()</code> and <code>equals()</code> methods
 */
public void testCompare() {
	Long id = new Long( 50 );
	Long srcId = new Long( 100 );
	Long destId = new Long( 200 );

	ConnectionAllele allele1 = new TestConnectionAllele( id, srcId, destId );
	ConnectionAllele allele2 = new TestConnectionAllele( id, srcId, destId );
	assertEquals( "bad equals", allele1, allele2 );
	assertEquals( "bad compareTo", 0, allele1.compareTo( allele2 ) );
	assertEquals( "bad compareTo", 0, allele1.compareTo( allele2 ) );

	allele1.setWeight( 0.5d );
	assertEquals( "bad equals diff weight", allele1, allele2 );
	assertEquals( "bad compareTo w/ diff weight", 0, allele1.compareTo( allele2 ) );

	ConnectionAllele allele3 = new TestConnectionAllele( new Long( id.longValue() + 1 ), srcId,
			destId );
	assertTrue( "bad equals w/ diff src", allele1.equals( allele3 ) == false );
	assertTrue( "bad comapreTo w/ diff src", allele1.compareTo( allele3 ) != 0 );
	assertEquals( "compareTo not symmetric", allele3.compareTo( allele1 ), allele1
			.compareTo( allele3 )
			* -1 );
}

/**
 * test persistence representation is unsupported
 * @throws Exception
 */
public void testPersistence() throws Exception {
	Long id = new Long( 50 );
	ConnectionAllele allele = new TestConnectionAllele( id, new Long( 1 ), new Long( 2 ) );

	// xml
	DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	XmlPersistableAllele xmlGene = new XmlPersistableAllele( allele );
	Document doc = builder.parse( new ByteArrayInputStream( xmlGene.toXml().getBytes() ) );
	ConnectionAllele allele2 = XmlPersistableAllele.connectionFromXml( doc.getFirstChild() );
	assertConnectionAlleleEquals( allele2, allele.getInnovationId(), allele.getSrcNeuronId(),
			allele.getDestNeuronId(), allele.getWeight() );
}

/**
 * test
 */
public void testRandom() {
	Long id = new Long( 50 );
	Long srcId = new Long( 100 );
	Long destId = new Long( 200 );
	ConnectionAllele allele = new TestConnectionAllele( id, srcId, destId );

	allele.setToRandomValue( new Random() );
	assertEquals( "wrong src id", srcId, allele.getSrcNeuronId() );
	assertEquals( "wrong dest id", destId, allele.getDestNeuronId() );
	assertTrue( "wrong weight", ConnectionAllele.DEFAULT_WEIGHT != allele.getWeight() );
}

}
