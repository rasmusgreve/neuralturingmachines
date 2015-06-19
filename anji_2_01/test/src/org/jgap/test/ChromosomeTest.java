/**
 * ----------------------------------------------------------------------------| Created on Jun
 * 6, 2003
 */
package org.jgap.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.jgap.Allele;
import org.jgap.Chromosome;
import org.jgap.ChromosomeMaterial;
import org.jgap.Configuration;
import org.jgap.impl.IntegerAllele;

import com.anji.util.DummyConfiguration;

/**
 * @author Philip Tucker
 */
public class ChromosomeTest extends TestCase {

private ArrayList testAlleles = new ArrayList();

private Configuration config = new DummyConfiguration();

/**
 * ctor
 */
public ChromosomeTest() {
	this( ChromosomeTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public ChromosomeTest( String name ) {
	super( name );
}

/**
 * @see junit.framework.TestCase#setUp()
 */
protected void setUp() throws Exception {
	for ( int i = 0; i < 6; ++i ) {
		IntegerAllele intAllele = new IntegerAllele( config );
		intAllele.setValue( new Integer( i ) );
		testAlleles.add( intAllele );
	}
}

/**
 * test fitnes
 * @throws Exception
 */
public void testFitness() throws Exception {
	List genes = new ArrayList( 3 );
	for ( int i = 0; i < 3; ++i )
		genes.add( new IntegerAllele( config ) );
	ChromosomeMaterial material = new ChromosomeMaterial( genes );
	Chromosome chrom = new Chromosome( material, new Long( 1 ) );

	assertTrue( "initial fitness value invalid", chrom.getFitnessValue() == -1 );

	chrom.setFitnessValue( 100 );
	assertEquals( "value wrong after set fitness", 100, chrom.getFitnessValue() );
}

/**
 * test speciation distance
 * @throws Exception
 */
public void testDistance() throws Exception {
	// chromosome 1
	List alleles = new ArrayList( 3 );
	for ( int i = 0; i < 3; ++i ) {
		Allele allele = (Allele) testAlleles.get( i );
		alleles.add( allele.cloneAllele() );
	}
	ChromosomeMaterial material = new ChromosomeMaterial( alleles );
	Chromosome chrom1 = new Chromosome( material, config.nextChromosomeId() );

	// chromosome 2
	Chromosome chrom2 = new Chromosome( chrom1.cloneMaterial(), config.nextChromosomeId() );

	// chromosome 3
	Chromosome chrom3 = new Chromosome( chrom1.cloneMaterial(), config.nextChromosomeId() );
	Iterator iter = chrom3.getAlleles().iterator();
	while ( iter.hasNext() ) {
		IntegerAllele allele = (IntegerAllele) iter.next();
		allele.setValue( new Integer( 2 ) );
	}

	// chromosome 4
	alleles = new ArrayList( 3 );
	for ( int i = 3; i < 6; ++i ) {
		Allele allele = (Allele) testAlleles.get( i );
		alleles.add( allele.cloneAllele() );
	}
	material = new ChromosomeMaterial( alleles );
	Chromosome chrom4 = new Chromosome( material, config.nextChromosomeId() );

	// chromosome 5
	alleles = new ArrayList( 3 );
	for ( int i = 0; i < 6; i += 2 ) {
		IntegerAllele intAllele = (IntegerAllele) testAlleles.get( i );
		IntegerAllele newAllele = (IntegerAllele) intAllele.cloneAllele();
		if ( i == 0 )
			newAllele.setValue( new Integer( 4 ) );
		alleles.add( newAllele );
	}
	material = new ChromosomeMaterial( alleles );
	Chromosome chrom5 = new Chromosome( material, config.nextChromosomeId() );

	// same genes, same values
	assertEquals( "wrong distance", 0.0d, chrom1.distance( chrom2, config.getSpeciationParms() ),
			0.0d );
	assertEquals( "wrong distance", 0.0d, chrom2.distance( chrom1, config.getSpeciationParms() ),
			0.0d );

	// same genes, different values
	assertEquals( "wrong distance", 0.4d, chrom1.distance( chrom3, config.getSpeciationParms() ),
			0.0d );
	assertEquals( "wrong distance", 0.4d, chrom3.distance( chrom1, config.getSpeciationParms() ),
			0.0d );

	// completely disjoint genes
	assertEquals( "wrong distance", 2.0d, chrom1.distance( chrom4, config.getSpeciationParms() ),
			0.0d );
	assertEquals( "wrong distance", 2.0d, chrom4.distance( chrom1, config.getSpeciationParms() ),
			0.0d );

	// 1 common gene same value, 1 common gene different value, 1 disjoint gene each
	double expectedDistance = ( 2.0d / 3.0d ) + ( 2.0d * 0.4d );
	assertEquals( "wrong distance", expectedDistance, chrom1.distance( chrom5, config
			.getSpeciationParms() ), 0.0d );
	assertEquals( "wrong distance", expectedDistance, chrom5.distance( chrom1, config
			.getSpeciationParms() ), 0.0d );

	//	chrom1.cleanup();
	//	chrom2.cleanup();
	//	chrom3.cleanup();
	//	chrom4.cleanup();
	//	chrom5.cleanup();
}

/**
 * test parents
 */
public void testParents() {
	// chromosome 1
	List alleles = new ArrayList( 3 );
	for ( int i = 0; i < 3; ++i ) {
		Allele allele = (Allele) testAlleles.get( i );
		alleles.add( allele.cloneAllele() );
	}
	ChromosomeMaterial material = new ChromosomeMaterial( alleles );
	assertNull( "parent1 not null", material.getPrimaryParentId() );
	assertNull( "parent2 not null", material.getSecondaryParentId() );
	Chromosome chrom1 = new Chromosome( material, config.nextChromosomeId() );
	assertNull( "parent1 not null", chrom1.getPrimaryParentId() );
	assertNull( "parent2 not null", chrom1.getSecondaryParentId() );

	// chromosome 2
	Chromosome chrom2 = new Chromosome( chrom1.cloneMaterial(), config.nextChromosomeId() );
	assertEquals( "parent1 not clonee", chrom1.getId(), chrom2.getPrimaryParentId() );
	assertNull( "parent2 not null", chrom2.getSecondaryParentId() );

	Long id = new Long( 22 );
	material = new ChromosomeMaterial( alleles, id );
	assertEquals( "parent1 wrong", id, material.getPrimaryParentId() );

	Long id2 = new Long( 44 );
	material = new ChromosomeMaterial( alleles, id, id2 );
	assertEquals( "parent1 wrong", id, material.getPrimaryParentId() );
	assertEquals( "parent2 wrong", id2, material.getSecondaryParentId() );
}

}
