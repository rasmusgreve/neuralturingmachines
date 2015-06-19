/*
 * Copyright (C) 2004 Derek James and Philip Tucker
 * 
 * This file is part of ANJI (Another NEAT Java Implementation).
 * 
 * ANJI is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 * 
 * Created on Mar 27, 2005 by Philip Tucker
 */
package org.jgap;

/**
 * @author Philip Tucker
 */
public class SpeciationParms {

/**
 * default ctor
 */
public SpeciationParms() {
	// no-op
}

private final static double DEFAULT_COMPATIBILITY_EXCESS_COEFF = 1.0d;

private final static double DEFAULT_COMPATIBILITY_DISJOINT_COEFF = 1.0d;

private final static double DEFAULT_COMPATIBILITY_COMMON_COEFF = 0.4d;

/**
 * default speciation threshold
 */
public final static double DEFAULT_SPECIATION_THRESHOLD = 3.0d;

private double compatExcessCoeff = DEFAULT_COMPATIBILITY_EXCESS_COEFF;

private double compatDisjointCoeff = DEFAULT_COMPATIBILITY_DISJOINT_COEFF;

private double compatCommonCoeff = DEFAULT_COMPATIBILITY_COMMON_COEFF;

private double speciationThreshold = DEFAULT_SPECIATION_THRESHOLD;

/**
 * @return double coefficient for species compatibility based on common genes; see <a
 * href="http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf">section 3.3 of primary NEAT
 * paper </a> for details.
 */
public double getSpecieCompatCommonCoeff() {
	return compatCommonCoeff;
}

/**
 * @return double coefficient for species compatibility based on disjoint genes; see <a
 * href="http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf">section 3.3 of primary NEAT
 * paper </a> for details.
 */
public double getSpecieCompatDisjointCoeff() {
	return compatDisjointCoeff;
}

/**
 * @return double coefficient for species compatibility based on excess genes; see <a
 * href="http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf">section 3.3 of primary NEAT
 * paper </a> for details.
 */
public double getSpecieCompatExcessCoeff() {
	return compatExcessCoeff;
}

/**
 * @param d coefficient for species compatibility based on common genes; see <a
 * href="http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf">section 3.3 of primary NEAT
 * paper </a> for details.
 */
public void setSpecieCompatCommonCoeff( double d ) {
	compatCommonCoeff = d;
}

/**
 * @param d coefficient for species compatibility based on disjoint genes; see <a
 * href="http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf">section 3.3 of primary NEAT
 * paper </a> for details.
 */
public void setSpecieCompatDisjointCoeff( double d ) {
	compatDisjointCoeff = d;
}

/**
 * @param d coefficient for species compatibility based on excess genes; see <a
 * href="http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf">section 3.3 of primary NEAT
 * paper </a> for details.
 */
public void setSpecieCompatExcessCoeff( double d ) {
	compatExcessCoeff = d;
}

/**
 * @return threshold below which the difference between 2 chromosomes dictates they are in the
 * same species; see <a href="http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf">section
 * 3.3 of primary NEAT paper </a> for details.
 */
public double getSpeciationThreshold() {
	return speciationThreshold;
}

/**
 * @param d threshold below which the difference between 2 chromosomes dictates they are in the
 * same species; see <a href="http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf">section
 * 3.3 of primary NEAT paper </a> for details.
 */
public void setSpeciationThreshold( double d ) {
	speciationThreshold = d;
}

}
