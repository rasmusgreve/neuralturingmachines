/**
 * ----------------------------------------------------------------------------| Created on Apr
 * 12, 2003
 */
package com.anji_ahni.util;

import java.util.List;

import org.jgapcustomised.Chromosome;
import org.jgapcustomised.Configuration;
import org.jgapcustomised.ReproductionOperator;

/**
 * @author Philip Tucker
 */
public class DummyReproductionOperator extends ReproductionOperator {

	/**
	 * @see org.jgapcustomised.ReproductionOperator#reproduce(org.jgapcustomised.Configuration, java.util.List, int,
	 *      java.util.List)
	 */
	public void reproduce(final Configuration config, final List parentChroms, int numOffspring, List offspring) {
		for (int i = 0; i < numOffspring; ++i) {
			Chromosome c = (Chromosome) parentChroms.get(0);
			offspring.add(c.cloneMaterial());
		}
	}
}
