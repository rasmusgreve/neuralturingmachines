/**
 * ----------------------------------------------------------------------------| Created on Apr
 * 12, 2003
 */
package org.jgap.test;

import java.util.Set;

import org.jgap.ChromosomeMaterial;
import org.jgap.Configuration;
import org.jgap.MutationOperator;

/**
 * @author Philip Tucker
 */
public class DummyMutationOperator extends MutationOperator {

/**
 * ctor
 */
public DummyMutationOperator() {
	super( 0.1f );
}

/**
 * @see org.jgap.MutationOperator#mutate(org.jgap.Configuration, org.jgap.ChromosomeMaterial,
 * java.util.Set, java.util.Set)
 */
protected void mutate( final Configuration a, ChromosomeMaterial cm, Set add, Set remove ) {
	// no-op
}
}
