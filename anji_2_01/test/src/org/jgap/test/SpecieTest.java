/**
 * ------------------------------------------------------------------------------------------------------------------------------------------------------------|
 * Created on Sep 21, 2003
 */
package org.jgap.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.jgap.Chromosome;
import org.jgap.ChromosomeMaterial;
import org.jgap.Configuration;
import org.jgap.SpeciationParms;
import org.jgap.Specie;
import org.jgap.impl.IntegerAllele;

import com.anji.util.DummyConfiguration;

/**
 * @author philip
 */
public class SpecieTest extends TestCase {

private static int NUM_SPECIES = 3;

private static int NUM_GENES = 3;

private List reps = new ArrayList( NUM_SPECIES );

private List species = new ArrayList( NUM_SPECIES );

private Configuration config = new DummyConfiguration();

/**
 * ctor
 */
public SpecieTest() {
	this( SpecieTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public SpecieTest( String name ) {
	super( name );
}

/**
 * @see junit.framework.TestCase#setUp()
 */
protected void setUp() throws Exception {
	SpeciationParms speciationParms = config.getSpeciationParms();
	speciationParms.setSpecieCompatCommonCoeff( 0.04d );
	speciationParms.setSpeciationThreshold( 0.03d );

	// build representative chromosomes and species
	for ( int i = 0; i < NUM_SPECIES; ++i ) {
		Collection alleles = new ArrayList( NUM_GENES );
		for ( int j = 0; j < NUM_GENES; ++j ) {
			IntegerAllele intAllele = new IntegerAllele( config, 0, 100 );
			intAllele.setValue( new Integer( 100 + ( i * 10 ) + j ) );
			alleles.add( intAllele );
		}
		Chromosome rep = new Chromosome( new ChromosomeMaterial( alleles ), config.nextChromosomeId() );
		rep.setFitnessValue( Math.abs( config.getRandomGenerator().nextInt( 1000 ) ) );
		reps.add( rep );

		Specie s = new Specie( speciationParms, rep );
		species.add( s );
	}
}

/**
 * add chromosomes to species
 * @throws Exception
 */
public void testChroms() throws Exception {

	for ( int i = 0; i < NUM_SPECIES; ++i ) {
		// representative
		Chromosome rep = (Chromosome) reps.get( i );
		Specie uut = (Specie) species.get( i );
		assertEquals( "specie ID wrong", rep.getId(), uut.getRepresentativeId() );
		assertFalse( "specie empty", uut.isEmpty() );
		assertEquals( "specie size != 1", 1, uut.getChromosomes().size() );
		assertTrue( "specie does not contain representative", uut.getChromosomes().contains( rep ) );
		assertEquals( "specie fitness != representative fitness", rep.getFitnessValue(), uut
				.getFitnessValue(), 0.0d );
		assertEquals( "wrong specie-specific fitness value for representative", rep
				.getFitnessValue(), uut.getChromosomeFitnessValue( rep ), 0.0d );
		assertTrue( "representative does not match", uut.match( rep ) );
		assertFalse( "representative does not match", uut.add( rep ) );

		// clone
		Chromosome clone = new Chromosome( rep.cloneMaterial(), config.nextChromosomeId() );
		clone.setFitnessValue( rep.getFitnessValue() );
		assertFalse( "specie contains representative clone", uut.getChromosomes().contains( clone ) );
		assertTrue( "clone does not match", uut.match( clone ) );
		assertTrue( "clone not added", uut.add( clone ) );
		assertEquals( "specie size != 2", 2, uut.getChromosomes().size() );
		assertEquals( "specie fitness != clone fitness", clone.getFitnessValue(), uut
				.getFitnessValue(), 0.0d );
		assertTrue( "specie does not contain clone", uut.getChromosomes().contains( clone ) );
		assertEquals( "wrong specie-specific fitness value for clone", ( (double) clone
				.getFitnessValue() ) / 2, uut.getChromosomeFitnessValue( rep ), 0.0d );

		// new chromosome
		ChromosomeMaterial material = rep.cloneMaterial();
		IntegerAllele intAllele = (IntegerAllele) material.getAlleles().iterator().next();
		Integer val = intAllele.getValue();
		val = new Integer( val.intValue() + 1 );
		intAllele.setValue( val );
		Chromosome c = new Chromosome( material, config.nextChromosomeId() );
		c.setFitnessValue( 1500 );
		assertTrue( "specie contains new chromosome", uut.getChromosomes().contains( c ) == false );
		assertTrue( "new chromosome does not match", uut.match( c ) );
		assertTrue( "new chromosome not added", uut.add( c ) );
		assertEquals( "specie size != 3", 3, uut.getChromosomes().size() );
		double expectedFitness = ( rep.getFitnessValue() + clone.getFitnessValue() + c
				.getFitnessValue() ) / 3.0d;
		assertEquals( "specie fitness wrong", expectedFitness, uut.getFitnessValue(), 0.0d );
		assertTrue( "specie does not contain new chromosome", uut.getChromosomes().contains( c ) );
		assertEquals( "wrong specie-specific fitness value for new chromosome", ( (double) c
				.getFitnessValue() ) / 3, uut.getChromosomeFitnessValue( c ), 0.0d );
		assertEquals( "wrong fittest", c, uut.getFittest() );

		// new different chromosome
		ArrayList alleles = new ArrayList( 1 );
		intAllele = new IntegerAllele( config, 0, 100 );
		intAllele.setValue( new Integer( 200 ) );
		alleles.add( intAllele );
		material = new ChromosomeMaterial( alleles );
		c = new Chromosome( material, config.nextChromosomeId() );
		c.setFitnessValue( 75 );
		assertTrue( "specie contains new chromosome", uut.getChromosomes().contains( c ) == false );
		assertFalse( "new chromosome matches", uut.match( c ) );
		try {
			uut.add( c );
			fail( "added unmatching chromosome" );
		}
		catch ( IllegalArgumentException e ) {
			// success
		}
		assertEquals( "specie size != 3", 3, uut.getChromosomes().size() );
		assertFalse( "specie does not contain new chromosome", uut.getChromosomes().contains( c ) );
		try {
			uut.getChromosomeFitnessValue( c );
			fail( "got fitness value for unmatched chromosome" );
		}
		catch ( IllegalArgumentException e ) {
			// success
		}
	}
}

/**
 * test <code>Specie.cull()</code>
 */
public void testCull() {
	Specie uut = (Specie) species.get( 0 );
	Chromosome rep = (Chromosome) reps.get( 0 );

	// test chromosomes
	Chromosome c1 = new Chromosome( rep.cloneMaterial(), config.nextChromosomeId() );
	Chromosome c2 = new Chromosome( rep.cloneMaterial(), config.nextChromosomeId() );
	Chromosome c3 = new Chromosome( rep.cloneMaterial(), config.nextChromosomeId() );
	Chromosome c4 = new Chromosome( rep.cloneMaterial(), config.nextChromosomeId() );

	// add to specie
	uut.add( c1 );
	uut.add( c2 );
	uut.add( c3 );
	uut.add( c4 );

	// cull all but keepers
	Collection keepers = new ArrayList( 2 );
	keepers.add( c1 );
	keepers.add( c3 );
	uut.cull( keepers );

	// assert
	assertTrue( "c1 removed", uut.getChromosomes().contains( c1 ) );
	assertFalse( "c2 not removed", uut.getChromosomes().contains( c2 ) );
	assertTrue( "c3 removed", uut.getChromosomes().contains( c3 ) );
	assertFalse( "c4 not removed", uut.getChromosomes().contains( c4 ) );
	//	
	//	c1.cleanup();
	//	c2.cleanup();
	//	c3.cleanup();
	//	c4.cleanup();
}

}
