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

import java.util.*;


/**
 * A basic implementation of NaturalSelector that models a
 * roulette wheel. When a Chromosome is added, it gets a number
 * of "slots" on the wheel equal to its fitness value. When the
 * select method is invoked, the wheel is "spun" and the Chromosome
 * occupying the spot on which it lands is selected. Then the wheel
 * is spun again and again until the requested number of
 * Chromosomes have been selected. Since Chromosomes with higher
 * fitness values get more slots on the wheel, there's a higher
 * statistical probability that they'll be chosen, but it's not
 * guaranteed.
 *
 * @author Neil Rotstan (neil at bluesock.org)
 */
public class WeightedRouletteSelector implements NaturalSelector
{
  private static Random generator = new Random();

  private HashMap population = new HashMap();
  private int totalInstances = 0;


  public void add( Chromosome chromosome, int fitness )
  {
    if( population.containsKey( chromosome ) )
    {
      ((Counter) population.get( chromosome )).increment( fitness );
    }

    else
    {
      population.put( chromosome, new Counter( fitness ) );
    }

    totalInstances += fitness;
  }

 
  public Chromosome[] select( int howMany )
  {
    Chromosome[] selections = new Chromosome[ howMany ];
    int[] selectedIndices = new int[ howMany ];

    for( int i = 0; i < howMany; i++ )
    {
      selectedIndices[i] = generator.nextInt( totalInstances );
    }

    Arrays.sort( selectedIndices );

    Iterator iterator = population.keySet().iterator();
    int currentSelection = 0;
    int currentIndex = 0;
    int currentCount = 0;

    Chromosome currentChromosome;
 
    while( iterator.hasNext() &&
           currentIndex < selectedIndices.length )
    {
      currentChromosome = (Chromosome ) iterator.next();

      currentCount +=
        ((Counter) population.get( currentChromosome )).getCount();

      while( currentIndex < selectedIndices.length &&
             currentCount >= selectedIndices[currentIndex] )
      {
        selections[currentSelection] = currentChromosome;

        currentSelection++;
        currentIndex++;
      }
    }

    return selections;
  }


  public void empty()
  {
    population.clear();
    totalInstances = 0;
  }
}  


class Counter
{
  private int count;


  public Counter( int initialCount )
  {
    count = initialCount;
  }


  public Counter()
  {
    this( 0 );
  }


  public void increment()
  {
    count++;
  }


  public void increment( int howMany )
  {
    count += howMany;
  }


  public int getCount()
  {
    return count;
  }


  public void reset()
  {
    count = 0;
  }
}

