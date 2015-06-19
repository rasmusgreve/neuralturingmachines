/*
 * Created on Dec 5, 2003
 */
package org.jgap.impl.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.jgap.Chromosome;
import org.jgap.ChromosomeMaterial;
import org.jgap.Configuration;
import org.jgap.Specie;
import org.jgap.impl.CloneReproductionOperator;
import org.jgap.impl.IntegerAllele;

import com.anji.util.DummyConfiguration;

/**
 * @author Philip Tucker
 */
public class CloneReproductionOperatorTest extends TestCase {

/**
 * ctor
 */
public CloneReproductionOperatorTest() {
	this( CloneReproductionOperatorTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public CloneReproductionOperatorTest( String name ) {
	super( name );
}

/**
 * test operator
 * @throws Exception
 */
public void testCloning() throws Exception {
	Configuration config = new DummyConfiguration();
	config.setPopulationSize( 100 );

	CloneReproductionOperator uut = new CloneReproductionOperator();
	uut.setSlice( 0.5f );
	assertEquals( "wrong slice", 0.5f, uut.getSlice(), 0.0f );

	// build parents
	List species = new ArrayList( 3 );
	List parents = new ArrayList( 9 );
	List allChroms = new ArrayList();
	for ( int h = 0; h < 3; ++h ) {
		List chroms = new ArrayList( 3 );
		for ( int i = 0; i < 3; ++i ) {
			Collection genes = new ArrayList( 3 );
			for ( int j = 0; j < 3; ++j ) {
				IntegerAllele gene = new IntegerAllele( config );
				gene.setValue( new Integer( j * 10 ) );
				genes.add( gene );
			}
			ChromosomeMaterial cMat = new ChromosomeMaterial( genes );
			Chromosome c = new Chromosome( cMat, config.nextChromosomeId() );
			chroms.add( c );
			parents.add( c );
		}
		Specie s = new Specie( config.getSpeciationParms(), (Chromosome) chroms.get( 0 ) );
		s.add( (Chromosome) chroms.get( 1 ) );
		s.add( (Chromosome) chroms.get( 2 ) );
		species.add( s );
		allChroms.addAll( chroms );
	}

	// reproduce
	List offspring = new ArrayList();
	try {
		uut.reproduce( config, species, offspring );
	}
	catch ( IllegalStateException e ) {
		// success - fitness has not been set
	}
	config.getBulkFitnessFunction().evaluate( allChroms );
	uut.reproduce( config, species, offspring );
	assertEquals( "wrong # offspring", 50, offspring.size() );

	// each offspring must match a parent exactly
	Iterator offspringIter = offspring.iterator();
	while ( offspringIter.hasNext() ) {
		ChromosomeMaterial childMat = (ChromosomeMaterial) offspringIter.next();
		Chromosome child = new Chromosome( childMat, config.nextChromosomeId() );
		boolean match = false;
		Iterator parentsIter = parents.iterator();
		while ( parentsIter.hasNext() && !match ) {
			Chromosome parent = (Chromosome) parentsIter.next();
			match = ( parent.distance( child, config.getSpeciationParms() ) == 0.0d );
		}
		assertTrue( "child matched no parent", match );
		// child.cleanup();
	}
}

}
