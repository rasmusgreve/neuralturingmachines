package com.ojcoleman.ahni.evaluation.mocostfunctions;

import org.jgapcustomised.Chromosome;

import com.anji_ahni.integration.Activator;
import com.ojcoleman.ahni.evaluation.BulkFitnessFunctionMT;

public class CPPNSizeCost extends BulkFitnessFunctionMT {
	@Override
	public boolean fitnessValuesStable() {
		return true;
	}
	
	@Override
	protected double evaluate(Chromosome genotype, Activator substrate, int evalThreadIndex) {
		return 1.0 / (1.0 + (genotype.getAlleles().size() * genotype.getAlleles().size()));
	}
}
