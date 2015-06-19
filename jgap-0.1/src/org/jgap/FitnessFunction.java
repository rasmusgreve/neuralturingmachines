/*
 * Copyright 2001, Neil Rotstan
 *
 * This file is part of JGAP.
 *
 * JGAP is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * JGAP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with JGAP; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.jgap;

/**
 * Fitness functions are used to determine how optimal a
 * particular solution is relative to other solutions.
 * This interface should be implemented by all concrete
 * fitness functions. The fitness function is given a
 * Chromosome to evaluate and should return its fitness
 * value. The higher the value, the more fit the Chromosome.
 * The actual range of fitness values isn't important (other
 * than the fact that the values must be of type int): it's
 * the relative difference as a percentage that matters. So
 * in other words, two Chromosomes with respective fitness 
 * values of 1 and 100 have the same relative fitness to each
 * other as two Chromosomes with respective fitness values of
 * 10 and 1000 (in each case, the first is 1% as fit as the
 * second).
 * <p>
 * Note: Two Chromosomes of the same size and with equivalent
 * sets of genes should always be assigned the same fitness
 * value by any implementation of this method.
 *
 * @author Neil Rotstan (neil at bluesock.org)
 */
public interface FitnessFunction
{
  /**
   * Determine the fitness of the given Chromosome instance.
   * The higher the return value, the more fit the instance.
   * This method should always return the same fitness value
   * for two equivalent Chromosome instances.
   *
   * @param subject: The Chromosome instance to evaluate.
   *
   * @return The fitness rating of the given Chromosome.
   */
  public int evaluate( Chromosome subject );
}  
