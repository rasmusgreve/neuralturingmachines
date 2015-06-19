/**
 * ----------------------------------------------------------------------------| Created on Mar
 * 12, 2003
 */

package org.jgap.test;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.jgap.impl.test.CloneReproductionOperatorTest;
import org.jgap.impl.test.IntegerAlleleTest;
import org.jgap.impl.test.WeightedRouletteSelectorTest;

/**
 * @author Philip Tucker
 */
public class Master extends TestCase {

/**
 * @return suite of tests
 */
public static TestSuite suite() {
	TestSuite suite = new TestSuite();
	suite.addTest( new TestSuite( ChromosomeTest.class ) );
	suite.addTest( new TestSuite( ConfigurationTest.class ) );
	suite.addTest( new TestSuite( IntegerAlleleTest.class ) );
	suite.addTest( new TestSuite( GenotypeTest.class ) );
	suite.addTest( new TestSuite( SpecieTest.class ) );
	suite.addTest( new TestSuite( CloneReproductionOperatorTest.class ) );
	suite.addTest( new TestSuite( WeightedRouletteSelectorTest.class ) );
	suite.addTest( new TestSuite( GenotypeTest.class ) );
	return suite;
}

/**
 * main method
 * @param args
 */
public static void main( String[] args ) {
	TestRunner.run( suite() );
}

}
