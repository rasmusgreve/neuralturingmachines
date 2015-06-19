/**
 * ----------------------------------------------------------------------------| Created on Apr
 * 12, 2003
 */
package org.jgap.test;

import java.util.Properties;
import java.util.Random;

import org.jgap.Chromosome;
import org.jgap.FitnessFunction;

/**
 * @author Philip Tucker
 */
public class DummyFitnessFunction extends FitnessFunction {

private Random rand = null;

/**
 * ctor
 * @param newRand
 */
public DummyFitnessFunction( Random newRand ) {
	rand = newRand;
}

/**
 * ctor
 */
public DummyFitnessFunction() {
	rand = new Random();
}

/**
 * assign random fitness value
 * @see org.jgap.FitnessFunction#evaluate(org.jgap.Chromosome)
 */
protected int evaluate( Chromosome a_subject ) {
	return rand.nextInt( 100 );
}

/**
 * @see org.jgap.FitnessFunction#init(java.util.Properties)
 */
public void init( Properties newProps ) {
	// no-op
}

}
