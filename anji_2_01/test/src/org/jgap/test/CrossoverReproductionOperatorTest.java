/**
 * -------------------------------------------------------------------------------------------------------------------------------------------------------------|
 * Created on Mar 15, 2003
 */
package org.jgap.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.CrossoverReproductionOperator;
import org.jgap.Specie;

import com.anji.util.DummyConfiguration;

/**
 * @author Philip Tucker
 */
public abstract class CrossoverReproductionOperatorTest extends TestCase {

/**
 * unit under test
 */
protected CrossoverReproductionOperator uut = null;

/**
 * configuration object
 */
protected Configuration config = null;

/**
 * parent species
 */
protected List parentSpecies = null;

/**
 * single parent species
 */
protected Specie oneParentSpecie = null;

/**
 * parent IDs; <code>List</code> contains <code>Long</code> objects
 */
protected List parentIds = new ArrayList();

/**
 * ctor
 */
public CrossoverReproductionOperatorTest() {
	this( CrossoverReproductionOperatorTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public CrossoverReproductionOperatorTest( String name ) {
	super( name );
}

/**
 * @see junit.framework.TestCase#setUp()
 */
protected void setUp() throws Exception {
	initConfig();
	config.lockSettings();
	initUut();
	initParentSpecies();
	Specie parentSpecie = (Specie) parentSpecies.get( 0 );
	Chromosome orig = (Chromosome) parentSpecie.getChromosomes().get( 0 );
	Chromosome rep = new Chromosome( orig.cloneMaterial(), config.nextChromosomeId() );
	oneParentSpecie = new Specie( new DummyConfiguration().getSpeciationParms(), rep );
	parentSpecies.add( oneParentSpecie );
	parentIds.add( oneParentSpecie.getRepresentativeId() );
}

/**
 * initialize unit under test
 * @throws Exception
 */
protected abstract void initUut() throws Exception;

/**
 * initialize configuration object
 * @throws Exception
 */
protected abstract void initConfig() throws Exception;

/**
 * initialize parent species
 * @throws Exception
 */
protected abstract void initParentSpecies() throws Exception;

/**
 * @param offspring <code>List</code> contains ChromosomeMaterial objects
 * @throws Exception
 */
protected abstract void doTestAfterReproduce( List offspring ) throws Exception;

/**
 * @param species <code>Collection</code> contains <code>Specie</code> objects
 */
protected void evaluate( Collection species ) {
	List chroms = new ArrayList();
	Iterator it = species.iterator();
	while ( it.hasNext() ) {
		Specie s = (Specie) it.next();
		chroms.addAll( s.getChromosomes() );
	}
	config.getBulkFitnessFunction().evaluate( chroms );
}

/**
 * test crossover operator
 * @throws Exception
 */
public void testCrossover() throws Exception {
	// without fitness set
	try {
		uut.reproduce( config, parentSpecies, new ArrayList() );
		fail( "should have thrown exception" );
	}
	catch ( IllegalStateException e ) {
		// success - fitness not yet set
	}
	evaluate( parentSpecies );

	Iterator speciesIter = parentSpecies.iterator();
	while ( speciesIter.hasNext() ) {
		Specie parentSpecie = (Specie) speciesIter.next();
		int origParentsSize = parentSpecie.getChromosomes().size();
		int expectedOffspringSize = (int) ( config.getPopulationSize() * uut.getSlice() );
		List origParents = new ArrayList( origParentsSize );
		Iterator iter = parentSpecie.getChromosomes().iterator();
		while ( iter.hasNext() ) {
			Chromosome c = (Chromosome) iter.next();
			origParents.add( new Chromosome( c.cloneMaterial(), config.nextChromosomeId() ) );
		}

		// reproduce
		List offspring = new ArrayList();
		uut.reproduce( config, parentSpecies, offspring );
		assertEquals( "parents size modified", origParentsSize, parentSpecie.getChromosomes()
				.size() );
		for ( int i = 0; i < origParentsSize; ++i ) {
			Chromosome origC = (Chromosome) origParents.get( i );
			Chromosome c = (Chromosome) parentSpecie.getChromosomes().get( i );
			assertEquals( "parents were modified", origC.getAlleles(), c.getAlleles() );
		}
		assertEquals( "wrong # offspring", expectedOffspringSize, offspring.size() );
		doTestAfterReproduce( offspring );

		//		iter = origParents.iterator();
		//		while ( iter.hasNext() ) {
		//			Chromosome c = (Chromosome) iter.next();
		//			c.cleanup();
		//		}
	}
}

}
