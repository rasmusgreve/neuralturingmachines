/**
 * ----------------------------------------------------------------------------| Created on Mar
 * 12, 2003
 */
package org.jgap.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import org.jgap.ChromosomeMaterial;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.InvalidConfigurationException;
import org.jgap.MutationOperator;
import org.jgap.NaturalSelector;
import org.jgap.ReproductionOperator;
import org.jgap.event.EventManager;
import org.jgap.impl.CloneReproductionOperator;
import org.jgap.impl.IntegerAllele;
import org.jgap.impl.WeightedRouletteSelector;

import com.anji.util.DummyReproductionOperator;

/**
 * @author Philip Tucker
 */
public class ConfigurationTest extends TestCase {

private final static List TEST_ALLELES = new ArrayList();
static {
	TEST_ALLELES.add( new IntegerAllele( 1, 2 ) );
}

/**
 * unit under test
 */
protected Configuration uut = null;

/**
 * ctor
 */
public ConfigurationTest() {
	this( ConfigurationTest.class.getName() );
}

/**
 * ctor
 * @param arg0
 */
public ConfigurationTest( String arg0 ) {
	super( arg0 );
}

/**
 * @see junit.framework.TestCase#setUp()
 */
protected void setUp() throws Exception {
	initUut();
}

/**
 * init configuration object (unit under test)
 * @throws Exception
 */
protected void initUut() throws Exception {
	uut = new Configuration();
}

/**
 * test <code>Configuration.lockSettings()</code>
 * @param shouldAllowLock
 * @throws InvalidConfigurationException
 */
protected void doTestLockSettings( boolean shouldAllowLock )
		throws InvalidConfigurationException {
	try {
		uut.lockSettings();
		if ( !shouldAllowLock )
			fail( "config should not allow lock" );
	}
	catch ( InvalidConfigurationException e ) {
		if ( shouldAllowLock )
			throw e;
	}
}

private void doTestReproductionOperators( boolean shouldAllowLock )
		throws InvalidConfigurationException {
	// reproduction operators
	try {
		uut.addReproductionOperator( null );
		fail( "expected exception" );
	}
	catch ( InvalidConfigurationException e ) {
		// success
	}
	int numOps = uut.getReproductionOperators().size();
	for ( int i = numOps; i < numOps + 2; ++i ) {
		ReproductionOperator repro = new DummyReproductionOperator();
		uut.addReproductionOperator( repro );
		assertTrue( "missing reproduction operator", uut.getReproductionOperators()
				.contains( repro ) );
		assertEquals( "unexpected reproduction operator", i + 1, uut.getReproductionOperators()
				.size() );
	}
	doTestLockSettings( shouldAllowLock );
}

private void doTestMutationOperators( boolean shouldAllowLock )
		throws InvalidConfigurationException {
	// mutation operators
	try {
		uut.addMutationOperator( null );
		fail( "expected exception" );
	}
	catch ( InvalidConfigurationException e ) {
		// success
	}
	int numOps = uut.getMutationOperators().size();
	for ( int i = numOps; i < numOps + 2; ++i ) {
		MutationOperator mutat = new DummyMutationOperator();
		uut.addMutationOperator( mutat );
		assertTrue( "missing reproduction operator", uut.getMutationOperators().contains( mutat ) );
		assertEquals( "unexpected reproduction operator", i + 1, uut.getMutationOperators().size() );
	}
	doTestLockSettings( shouldAllowLock );
}

/**
 * test sample chromosome
 * @param shouldAllowLock
 * @throws InvalidConfigurationException
 */
protected void doTestSampleChromosome( boolean shouldAllowLock )
		throws InvalidConfigurationException {
	// sample chromosome
	ChromosomeMaterial material = new ChromosomeMaterial( TEST_ALLELES );
	uut.setSampleChromosomeMaterial( material );
	assertEquals( "wrong chrom", material, uut.getSampleChromosomeMaterial() );
	doTestLockSettings( shouldAllowLock );
}

private void doTestNaturalSelector( boolean shouldAllowLock )
		throws InvalidConfigurationException {
	// natural selector
	NaturalSelector selector = new WeightedRouletteSelector();
	uut.setNaturalSelector( selector );
	assertEquals( "wrong selector", selector, uut.getNaturalSelector() );
	doTestLockSettings( shouldAllowLock );
}

private void doTestRandomGenerator( boolean shouldAllowLock )
		throws InvalidConfigurationException {
	// rand
	Random rand = new Random();
	uut.setRandomGenerator( rand );
	assertEquals( "wrong rand", rand, uut.getRandomGenerator() );
	doTestLockSettings( shouldAllowLock );
}

private void doTestEventManager( boolean shouldAllowLock ) throws InvalidConfigurationException {
	// event mgr
	EventManager eventMgr = new EventManager();
	uut.setEventManager( eventMgr );
	assertEquals( "wrong event mgr", eventMgr, uut.getEventManager() );
	doTestLockSettings( shouldAllowLock );
}

private void doTestPopulationSlices( boolean shouldAllowLock )
		throws InvalidConfigurationException {
	// pop slices
	uut.getNaturalSelector().setSurvivalRate( 0.10f );
	for ( int i = 0; i < uut.getReproductionOperators().size(); ++i ) {
		ReproductionOperator repro = (ReproductionOperator) uut.getReproductionOperators().get( i );
		repro.setSlice( 0.90f / uut.getReproductionOperators().size() );
	}

	// pop size
	uut.setPopulationSize( 110 );
	assertEquals( "wrong pop size", 110, uut.getPopulationSize() );

	doTestLockSettings( shouldAllowLock );
}

private void doTestFitnessFunction( boolean shouldAllowLock )
		throws InvalidConfigurationException {
	// fitness function
	FitnessFunction ff = new DummyFitnessFunction();
	uut.setFitnessFunction( ff );
	assertEquals( "wrong fitness function", ff, uut.getFitnessFunction() );
	doTestLockSettings( shouldAllowLock );
}

/**
 * test locked configuration
 * @throws Exception
 */
protected void doTestLockedConfig() throws Exception {
	doTestLockSettings( true );

	try {
		uut.addReproductionOperator( new CloneReproductionOperator() );
		fail( "expected lock exception for reproduction op" );
	}
	catch ( InvalidConfigurationException e ) {
		// success
	}

	try {
		uut.addMutationOperator( new DummyMutationOperator() );
		fail( "expected lock exception for mutation op" );
	}
	catch ( InvalidConfigurationException e ) {
		// success
	}

	try {
		uut.setFitnessFunction( new DummyFitnessFunction() );
		fail( "expected lock exception for fitness func" );
	}
	catch ( InvalidConfigurationException e ) {
		// success
	}

	try {
		uut.setSampleChromosomeMaterial( new ChromosomeMaterial( TEST_ALLELES ) );
		fail( "expected lock exception for sample chrom" );
	}
	catch ( InvalidConfigurationException e ) {
		// success
	}

	try {
		uut.setNaturalSelector( new WeightedRouletteSelector() );
		fail( "expected lock exception for selector" );
	}
	catch ( InvalidConfigurationException e ) {
		// success
	}

	try {
		uut.setRandomGenerator( new Random() );
		fail( "expected lock exception for rand" );
	}
	catch ( InvalidConfigurationException e ) {
		// success
	}

	try {
		uut.setEventManager( new EventManager() );
		fail( "expected lock exception for event mgr" );
	}
	catch ( InvalidConfigurationException e ) {
		// success
	}

	try {
		uut.setPopulationSize( 1 );
		fail( "expected lock exception for pop size" );
	}
	catch ( InvalidConfigurationException e ) {
		// success
	}
}

/**
 * test newly created config
 */
protected void doTestVirginConfiguration() {
	assertTrue( "unexpected reproduction operator", uut.getReproductionOperators().isEmpty() );
	assertTrue( "unexpected mutation operator", uut.getMutationOperators().isEmpty() );
	assertNull( "should not have fitness function", uut.getFitnessFunction() );
	assertNull( "should not have sample chrom", uut.getSampleChromosomeMaterial() );
	assertEquals( "should not have selector", null, uut.getNaturalSelector() );
	assertNull( "should not have rand", uut.getRandomGenerator() );
	assertNull( "should not have event mgr", uut.getEventManager() );
	assertEquals( "pop size should be zero", 0, uut.getPopulationSize() );
}

private void doTestConfiguration() throws Exception {
	doTestVirginConfiguration();
	doTestReproductionOperators( false );
	doTestMutationOperators( false );
	doTestSampleChromosome( false );
	doTestNaturalSelector( false );
	doTestRandomGenerator( false );
	doTestEventManager( false );
	doTestPopulationSlices( false );
	doTestFitnessFunction( true );
	doTestLockedConfig();
}

/**
 * test configuration object
 * @throws Exception
 */
public void testConfiguration() throws Exception {
	doTestConfiguration();
}

}
