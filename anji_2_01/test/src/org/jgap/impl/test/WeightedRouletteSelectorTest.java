/*
 * Created on Jan 26, 2004
 */
package org.jgap.impl.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.jgap.Chromosome;
import org.jgap.ChromosomeFitnessComparator;
import org.jgap.ChromosomeMaterial;
import org.jgap.Configuration;
import org.jgap.Specie;
import org.jgap.impl.WeightedRouletteSelector;

import com.anji.util.DummyConfiguration;

/**
 * @author Philip Tucker
 */
public class WeightedRouletteSelectorTest extends TestCase {

private Configuration config = new DummyConfiguration();

/**
 * ctor
 */
public WeightedRouletteSelectorTest() {
	super( WeightedRouletteSelectorTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public WeightedRouletteSelectorTest( String name ) {
	super( name );
}

/**
 * @see junit.framework.TestCase#setUp()
 */
protected void setUp() throws Exception {
	config.getRandomGenerator().setSeed( 0 );
	config.getSpeciationParms().setSpeciationThreshold( 100.0d );
}

/**
 * test defaults
 * @throws Exception
 */
public void testSelectorDefault() throws Exception {
	doTestSelector( null, null );
}

/**
 * test with elitism
 * @throws Exception
 */
public void testSelectorElitismDefault() throws Exception {
	doTestSelector( Boolean.TRUE, null );
}

/**
 * test with elitism and minimum specie size of 1
 * @throws Exception
 */
public void testSelectorElitismMinSpecieSizeOne() throws Exception {
	doTestSelector( Boolean.TRUE, new Integer( 1 ) );
}

private void doTestSelector( Boolean elitismVal, Integer minSpecieSizeVal ) {
	float survivalRate = 0.20f;

	// configure selector
	WeightedRouletteSelector uut = new WeightedRouletteSelector();
	if ( elitismVal != null )
		uut.setElitism( elitismVal.booleanValue() );
	if ( minSpecieSizeVal != null )
		uut.setElitismMinSpecieSize( minSpecieSizeVal.intValue() );
	uut.setSurvivalRate( survivalRate );

	// validate getters
	assertEquals( "wrong min specie size", ( minSpecieSizeVal == null ? 6 : minSpecieSizeVal
			.intValue() ), uut.getElitismMinSpecieSize() );
	assertEquals( "wrong elitism", survivalRate, uut.getSurvivalRate(), 0.0f );

	// test species and chromosomes
	List species = new ArrayList();
	List chroms = new ArrayList();
	for ( int specieIdx = 0; specieIdx < 5; ++specieIdx ) {
		Chromosome rep = new Chromosome( new ChromosomeMaterial( new ArrayList() ), config
				.nextChromosomeId() );
		rep.setFitnessValue( ( specieIdx + 1 ) * 100 );
		chroms.add( rep );
		Specie specie = new Specie( config.getSpeciationParms(), rep );
		species.add( specie );
		int specieSize = ( ( specieIdx + 1 ) % 2 == 0 ? 2 : 10 );
		for ( int chromIdx = 1; chromIdx < specieSize; ++chromIdx ) {
			Chromosome c = new Chromosome( new ChromosomeMaterial( new ArrayList() ), config
					.nextChromosomeId() );
			c.setFitnessValue( ( specieIdx + 1 ) * 100 + ( chromIdx * 10 ) );
			chroms.add( c );
			specie.add( c );
		}
	}

	// before adding chromosomes
	List survivors = uut.select( config );
	assertTrue( "not empty before add", survivors.isEmpty() );

	// add chromosomes
	uut.add( config, chroms );

	// after adding chromosomes
	survivors = uut.select( config );
	int numSurvivors = (int) ( ( chroms.size() * uut.getSurvivalRate() ) + 0.5 );
	assertEquals( "wrong # survivors", numSurvivors, survivors.size() );
	if ( elitismVal != null && elitismVal.booleanValue() ) {
		Iterator specieIter = species.iterator();
		while ( specieIter.hasNext() ) {
			Specie s = (Specie) specieIter.next();
			if ( s.getChromosomes().size() >= uut.getElitismMinSpecieSize() )
				assertTrue( "elite didn't survive", survivors.contains( s.getFittest() ) );
		}
	}
	// TODO - test other survivors

	// raw fitnesses 100, 110 ... *190, 200, *210, 300, 310 ... *390, 400, *410, 500, 510 ... *590
	// speciated fitnesses 10, 11 ... 19, 100, 105, 30, 31 ... 39, 200, 205, 50, 51 ... 59
	// * == elite

	// select with lower survival rate such that there are more elite than suriviors
	uut.setSurvivalRate( 0.10f );
	survivors = uut.select( config );
	numSurvivors = (int) ( chroms.size() * uut.getSurvivalRate() );
	assertEquals( "wrong # survivors", numSurvivors, survivors.size() );
	if ( elitismVal != null && elitismVal.booleanValue() ) {
		Collections.sort( survivors, new ChromosomeFitnessComparator() );
		if ( uut.getElitismMinSpecieSize() <= 2 ) {
			assertEquals( "wrong fitness for elite survivor 1", 39,
					( (Chromosome) survivors.get( 0 ) ).getSpeciatedFitnessValue() );
			assertEquals( "wrong fitness for elite survivor 2", 59,
					( (Chromosome) survivors.get( 1 ) ).getSpeciatedFitnessValue() );
			assertEquals( "wrong fitness for elite survivor 3", 205,
					( (Chromosome) survivors.get( 2 ) ).getSpeciatedFitnessValue() );
		}
		else {
			assertEquals( "wrong fitness for elite survivor 1", 19,
					( (Chromosome) survivors.get( 0 ) ).getSpeciatedFitnessValue() );
			assertEquals( "wrong fitness for elite survivor 2", 39,
					( (Chromosome) survivors.get( 1 ) ).getSpeciatedFitnessValue() );
			assertEquals( "wrong fitness for elite survivor 3", 59,
					( (Chromosome) survivors.get( 2 ) ).getSpeciatedFitnessValue() );
		}
	}

	// empty selector
	uut.empty();
	survivors = uut.select( config );
	assertTrue( "not empty after empty", survivors.isEmpty() );

	//		// clean up
	//		Iterator chromIter = chroms.iterator();
	//		while ( chromIter.hasNext() ) {
	//			Chromosome c = (Chromosome) chromIter.next();
	//			c.cleanup();
	//		}
}

}
