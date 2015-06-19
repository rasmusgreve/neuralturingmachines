/**
 * ------------------------------------------------------------------------------------------------------------------------------------------------------------|
 * Created on Sep 21, 2003
 */
package org.jgap.impl.test;

import junit.framework.TestCase;

import org.jgap.Allele;
import org.jgap.impl.IntegerAllele;

import com.anji.util.DummyConfiguration;

/**
 * @author philip
 */
public class IntegerAlleleTest extends TestCase {

private DummyConfiguration config = null;

/**
 * ctor
 */
public IntegerAlleleTest() {
	super( IntegerAlleleTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public IntegerAlleleTest( String name ) {
	super( name );
}

/**
 * @see junit.framework.TestCase#setUp()
 */
protected void setUp() throws Exception {
	config = new DummyConfiguration();
}

/**
 * test innovation IDs
 * @throws Exception
 */
public void testInnovationId() throws Exception {
	Long expectedId = new Long( config.nextInnovationId().longValue() + 1 );
	IntegerAllele uut = new IntegerAllele( config );
	assertEquals( "wrong id 1", expectedId, uut.getInnovationId() );

	expectedId = new Long( config.nextInnovationId().longValue() + 1 );
	uut = new IntegerAllele( config, 10, 20 );
	assertEquals( "wrong id 2", expectedId, uut.getInnovationId() );

	uut = new IntegerAllele( 10, 20 );
	assertEquals( "wrong id 3", new Long( 0 ), uut.getInnovationId() );
}

/**
 * test speciation distance
 * @throws Exception
 */
public void testDistance() throws Exception {
	IntegerAllele uut = new IntegerAllele( config );
	uut.setValue( new Integer( 2 ) );
	IntegerAllele differentAllele = new IntegerAllele( config );
	differentAllele.setValue( new Integer( 2 ) );
	Allele sameGene = uut.cloneAllele();

	assertEquals( "distance should be different", Double.MAX_VALUE,
			uut.distance( differentAllele ), 0.0d );
	assertEquals( "distance should be same", 0, uut.distance( sameGene ), 0.0d );

	assertFalse( "equals should be different", uut.equals( differentAllele ) );
	assertTrue( "equals should be same", uut.equals( sameGene ) );

	assertEquals( "compareTo should be different", uut.getInnovationId().longValue()
			- differentAllele.getInnovationId().longValue(), uut.compareTo( differentAllele ) );
	assertEquals( "compareTo should be same", 0, uut.compareTo( sameGene ) );
}

}
