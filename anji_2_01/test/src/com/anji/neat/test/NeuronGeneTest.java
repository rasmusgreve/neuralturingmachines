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
import com.anji.neat.NeuronAllele;
import com.anji.neat.NeuronGene;
import com.anji.neat.NeuronType;
import com.anji.nn.ActivationFunctionType;

/**
 * @author Philip Tucker
 */
public class NeuronGeneTest extends TestCase {

/**
 * 
 * @author Philip Tucker
 */
protected class TestNeuronAllele extends NeuronAllele {

/**
 * Exposes constructor for testing so we don't need to use configuration object.
 * @param newType
 * @param newInnovationId
 * @param actType
 */
public TestNeuronAllele( NeuronType newType, Long newInnovationId,
		ActivationFunctionType actType ) {
	super( new NeuronGene( newType, newInnovationId, actType ) );
}

}

/**
 * ctor
 */
public NeuronGeneTest() {
	this( NeuronGeneTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public NeuronGeneTest( String name ) {
	super( name );
}

private void assertNeuronAlleleEquals( NeuronAllele neuronAllele, Long id, NeuronType type,
		ActivationFunctionType anAct ) {
	assertEquals( "wrong id", id, neuronAllele.getInnovationId() );
	assertEquals( "wrong type", type, neuronAllele.getType() );
	assertEquals( "wrong activation function", anAct, neuronAllele.getActivationType() );
}

/**
 * @throws Exception
 */
public void testNeuronGene() throws Exception {
	// ctor and rand
	Long id = new Long( 100 );
	NeuronType type = NeuronType.INPUT;
	NeuronAllele neuronAllele1 = new TestNeuronAllele( type, id, ActivationFunctionType.SIGMOID );
	assertNeuronAlleleEquals( neuronAllele1, id, type, ActivationFunctionType.SIGMOID );

	// newAllele, equals, toString
	NeuronAllele neuronAllele2 = (NeuronAllele) neuronAllele1.cloneAllele();
	assertNeuronAlleleEquals( neuronAllele2, id, type, ActivationFunctionType.SIGMOID );
	assertEquals( "should be equal", neuronAllele2, neuronAllele1 );
	assertEquals( "toString() should be equal", neuronAllele2.toString(), neuronAllele1
			.toString() );

	// getValue, ctor, equals
	NeuronAllele neuronAllele3 = new TestNeuronAllele( NeuronType.HIDDEN, new Long( id
			.longValue() + 10 ), ActivationFunctionType.LINEAR );
	assertTrue( "should not be equal", neuronAllele2.equals( neuronAllele3 ) == false );
	assertTrue( "should not be equal",
			neuronAllele2.toString().equals( neuronAllele3.toString() ) == false );

	// compareTo
	assertEquals( "objects should be equal", 0, neuronAllele1.compareTo( neuronAllele2 ) );
	assertTrue( "objects should not be equal", neuronAllele1.compareTo( neuronAllele3 ) != 0 );
	assertTrue( "objects should not be equal", neuronAllele3.compareTo( neuronAllele1 ) != 0 );
	assertEquals( "compareTo no symmetric", neuronAllele3.compareTo( neuronAllele1 ),
			neuronAllele1.compareTo( neuronAllele3 ) * -1 );

	// rand
	neuronAllele1.setToRandomValue( new Random() );
	assertNeuronAlleleEquals( neuronAllele1, id, type, ActivationFunctionType.SIGMOID );
}

/**
 * 
 * @throws Exception
 */
public void testPersistence() throws Exception {
	Long id = new Long( 50 );
	NeuronAllele allele = new TestNeuronAllele( NeuronType.HIDDEN, id,
			ActivationFunctionType.LINEAR );

	// xml
	DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	XmlPersistableAllele xmlGene = new XmlPersistableAllele( allele );
	Document doc = builder.parse( new ByteArrayInputStream( xmlGene.toXml().getBytes() ) );
	NeuronAllele allele2 = XmlPersistableAllele.neuronFromXml( doc.getFirstChild() );
	assertNeuronAlleleEquals( allele2, allele.getInnovationId(), allele.getType(),
			ActivationFunctionType.LINEAR );
}

}
