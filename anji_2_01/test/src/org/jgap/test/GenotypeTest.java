/**
 * ----------------------------------------------------------------------------| Created on Apr
 * 12, 2003
 */
package org.jgap.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.jgap.Chromosome;
import org.jgap.ChromosomeMaterial;
import org.jgap.Configuration;
import org.jgap.Genotype;
import org.jgap.Specie;

import com.anji.util.DummyConfiguration;

/**
 * @author Philip Tucker
 */
public class GenotypeTest extends TestCase {

/**
 * ctor
 */
public GenotypeTest() {
	this( GenotypeTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public GenotypeTest( String name ) {
	super( name );
}

/**
 * test ctor
 * @throws Exception
 */
public void testCtor() throws Exception {
	Configuration config = new DummyConfiguration();
	int popSize = 2;
	config.setPopulationSize( popSize);

	List initChroms = new ArrayList( 1 );
	ChromosomeMaterial material = config.getSampleChromosomeMaterial();
	initChroms.add( new Chromosome( material, config.nextChromosomeId() ) );
	Chromosome fittest = new Chromosome( material, config.nextChromosomeId() );
	fittest.setFitnessValue( 100 );
	initChroms.add( fittest );

	Genotype uut = new Genotype( config, initChroms );
	doTestInitialPolulationAdjust( popSize, popSize );
	assertEquals( "different chromosomes", initChroms, uut.getChromosomes() );
	assertSame( "wrong fittest chromosome", fittest, uut.getFittestChromosome() );
	assertEquals( "wrong # species", 1, uut.getSpecies().size() );
	Specie specie = (Specie) uut.getSpecies().get( 0 );
	assertEquals( "different chromosomes in specie", initChroms, specie.getChromosomes() );
}

/**
 * test initial population size
 * @throws Exception
 */
public void testInitialPolulationSize() throws Exception {
	doTestInitialPolulationAdjust( 2, 3 );
	doTestInitialPolulationAdjust( 4, 2 );
}

private void doTestInitialPolulationAdjust( int popSize, int initialChromosomeSize )
		throws Exception {
	Configuration config = new DummyConfiguration();
	config.setPopulationSize( popSize );

	List initChroms = new ArrayList( 1 );
	ChromosomeMaterial material = config.getSampleChromosomeMaterial();
	for ( int i = 0; i < initialChromosomeSize; ++i )
		initChroms.add( new Chromosome( material, config.nextChromosomeId() ) );

	Genotype uut = new Genotype( config, initChroms );
	assertEquals( "wrong population size", popSize, uut.getChromosomes().size() );
	assertEquals( "wrong # species", 1, uut.getSpecies().size() );
	Specie specie = (Specie) uut.getSpecies().get( 0 );
	assertEquals( "wrong specie size", popSize, specie.getChromosomes().size() );
}

/**
 * test <code>Genotype.randomInitialGenotype()</code>
 * @throws Exception
 */
public void testRandomInitialGenotype() throws Exception {
	Configuration config = new DummyConfiguration();
	config.setPopulationSize( 100 );

	Genotype genotype = Genotype.randomInitialGenotype( config );
	assertEquals( "wrong population size", 100, genotype.getChromosomes().size() );
}

/**
 * test <code>Genotype.evolve()</code>
 * @throws Exception
 */
public void testEvolve() throws Exception {
	Configuration config = new DummyConfiguration();
	config.setPopulationSize( 100 );
	Genotype genotype = Genotype.randomInitialGenotype( config );

	genotype.evolve();
	assertEquals( "wrong population size", 100, genotype.getChromosomes().size() );
}

}
