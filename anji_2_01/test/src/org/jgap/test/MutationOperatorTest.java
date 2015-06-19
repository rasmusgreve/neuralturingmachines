/**
 * ----------------------------------------------------------------------------| Created on Mar
 * 15, 2003
 */
package org.jgap.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.jgap.Chromosome;
import org.jgap.ChromosomeMaterial;
import org.jgap.Configuration;
import org.jgap.MutationOperator;

/**
 * @author Philip Tucker
 */
public abstract class MutationOperatorTest extends TestCase {

private static final short NUM_GENERATIONS = 25;

/**
 * unit under test
 */
protected MutationOperator uut = null;

/**
 * config object
 */
protected Configuration config = null;

/**
 * pre-mutation chromosomes
 */
protected ArrayList preMutants = new ArrayList();

/**
 * holds onto chromosomes created for evaluation so they can be cleand up in tear down
 */
private ArrayList chroms = new ArrayList();

/**
 * ctor
 */
public MutationOperatorTest() {
	this( MutationOperatorTest.class.toString() );
}

/**
 * @param name
 */
public MutationOperatorTest( String name ) {
	super( name );
}

/**
 * @see junit.framework.TestCase#setUp()
 */
protected void setUp() throws Exception {
	initConfig();
	config.lockSettings();
	initUut();
	initPreMutants();
}

/**
 * add chromosome to list
 * @param c
 */
protected void addChromosome( Chromosome c ) {
	chroms.add( c );
}

/**
 * initialize unit under test
 * @throws Exception
 */
protected abstract void initUut() throws Exception;

/**
 * initialize configuration
 * @throws Exception
 */
protected abstract void initConfig() throws Exception;

/**
 * initialize pre-mutated chromosomes
 * @throws Exception
 */
protected abstract void initPreMutants() throws Exception;

/**
 * @param mutants <code>List</code> contains <code>Chromosome</code> objects
 * @throws Exception
 */
protected abstract void doTestAfterMutate( List mutants ) throws Exception;

/**
 * 
 * @throws Exception
 */
public void testMutationOperator() throws Exception {
	for ( int i = 0; i < NUM_GENERATIONS; ++i ) {
		List mutants = cloneList( preMutants );
		uut.mutate( config, mutants );
		assertEquals( "size modified", preMutants.size(), mutants.size() );
		doTestAfterMutate( mutants );

		preMutants.clear();
		preMutants.addAll( mutants );
	}
}

/**
 * @param srcs <code>List</code> contains <code>ChromosomeMaterial</code> objects
 * @return <code>List</code> contains <code>ChromosomeMaterial</code> objects
 */
private List cloneList( List srcs ) {
	List result = new ArrayList();
	Iterator iter = srcs.iterator();
	while ( iter.hasNext() ) {
		ChromosomeMaterial src = (ChromosomeMaterial) iter.next();
		ChromosomeMaterial dest = src.clone( null );
		result.add( dest );
	}
	return result;
}

}
