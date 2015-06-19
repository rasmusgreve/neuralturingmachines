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
 * created by Philip Tucker on Jul 3, 2003
 */
package com.anji.neat.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.jgap.Allele;
import org.jgap.Chromosome;
import org.jgap.ChromosomeMaterial;

import com.anji.neat.ConnectionAllele;
import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.NeuronAllele;
import com.anji.neat.NeuronType;

/**
 * @author Philip Tucker
 */
public class TestChromosomeFactory {

/**
 * expected fitness for exponential nonrecurrent
 */
public final static long EXPONENTIAL_XOR_FITNESS_NONRECURRENT = 1209699185;

/**
 * expected fitness for NEAT nonrecurrent
 */
public final static long NEAT_XOR_FITNESS_NONRECURRENT = 122510665;

/**
 * expected fitness for exponential recurrent
 */
public final static long EXPONENTIAL_XOR_FITNESS_RECURRENT = 1209699185;

/**
 * expected fitness for NEAT recurrent
 */
public final static long NEAT_XOR_FITNESS_RECURRENT = 122510665;

private final static int DIM_STIMULI = 9;

private final static int DIM_HIDDEN_1 = 12;

private final static int DIM_HIDDEN_2 = 6;

private final static int DIM_RESPONSE = 4;

private NeatConfiguration config = null;

/**
 * ctor
 * @param aConfig
 */
public TestChromosomeFactory( NeatConfiguration aConfig ) {
	config = aConfig;
}

/**
 * @param isRecurrent
 * @return new chromosome
 */
public Chromosome newChromosome( boolean isRecurrent ) {
	return new Chromosome( newChromosomeMaterial( isRecurrent ), config.nextChromosomeId() );
}

/**
 * @param isRecurrent
 * @param dimStimuli
 * @param dimResponse
 * @return new chromosome
 */
public Chromosome newChromosome( boolean isRecurrent, int dimStimuli, int dimResponse ) {
	return new Chromosome( newChromosomeMaterial( isRecurrent, dimStimuli, dimResponse ), config
			.nextChromosomeId() );
}

/**
 * @param isRecurrent
 * @return new chromosome material
 */
public ChromosomeMaterial newChromosomeMaterial( boolean isRecurrent ) {
	return new ChromosomeMaterial( newAlleles( isRecurrent ) );
}

/**
 * @param isRecurrent
 * @param dimStimuli
 * @param dimResponse
 * @return new chromosome material
 */
public ChromosomeMaterial newChromosomeMaterial( boolean isRecurrent, int dimStimuli,
		int dimResponse ) {
	return new ChromosomeMaterial( newAlleles( dimStimuli, dimResponse, DIM_HIDDEN_1, DIM_HIDDEN_2,
			isRecurrent ) );
}

/**
 * @param isRecurrent
 * @return new alleles
 */
public List newAlleles( boolean isRecurrent ) {
	return newAlleles( DIM_STIMULI, DIM_RESPONSE, DIM_HIDDEN_1, DIM_HIDDEN_2, isRecurrent );
}

/**
 * @param dimStimuli
 * @param dimResponse
 * @param dimHidden1
 * @param dimHidden2
 * @param isRecurrent
 * @return List contains Allele objects
 */
public List newAlleles( int dimStimuli, int dimResponse, int dimHidden1, int dimHidden2,
		boolean isRecurrent ) {
	int numNeurons = dimStimuli + dimHidden1 + dimHidden2 + dimResponse;
	int numConns = ( dimStimuli * dimHidden1 ) + ( dimHidden1 * dimHidden2 )
			+ ( dimHidden2 * dimResponse ) + 1;
	if ( isRecurrent ) {
		numNeurons += 1; // recurrent neuron
		numConns += 7; // out -> recurrent, recurrent -> in, 3xself
		if ( dimStimuli > 2 ) // stimuli -> stimuli
			numConns += 1;
		if ( dimHidden1 > 2 ) // hidden1 -> hidden2
			numConns += 1;
		if ( dimResponse > 2 ) // response -> response
			numConns += 1;
	}

	// neurons
	Allele[] alleles = new Allele[ numNeurons + numConns ];
	int alleleIdx = 0;
	alleleIdx = addNeurons( alleles, alleleIdx, dimStimuli, NeuronType.INPUT );
	alleleIdx = addNeurons( alleles, alleleIdx, dimHidden1, NeuronType.HIDDEN );
	alleleIdx = addNeurons( alleles, alleleIdx, dimHidden2, NeuronType.HIDDEN );
	alleleIdx = addNeurons( alleles, alleleIdx, dimResponse, NeuronType.OUTPUT );

	// full forward connections
	alleleIdx = addFullConns( alleles, alleleIdx, 0, dimStimuli, dimHidden1 );
	alleleIdx = addFullConns( alleles, alleleIdx, dimStimuli, dimHidden1, dimHidden2 );
	alleleIdx = addFullConns( alleles, alleleIdx, dimStimuli + dimHidden1, dimHidden2, dimResponse );

	// additional connections
	int outIdx = dimStimuli + dimHidden1 + dimHidden2 + ( dimResponse / 2 );
	int hid1Idx = dimStimuli + ( dimHidden1 / 2 );
	int inIdx = dimStimuli / 2;
	//   in -> out
	NeuronAllele src = (NeuronAllele) alleles[ inIdx ];
	NeuronAllele dest = (NeuronAllele) alleles[ outIdx ];
	ConnectionAllele conn = config
			.newConnectionAllele( src.getInnovationId(), dest.getInnovationId() );
	conn.setToRandomValue( config.getRandomGenerator() );
	alleles[ alleleIdx++ ] = conn;
	if ( isRecurrent ) {
		// recurrent-only node; i.e., it only exists as a loopback from the
		//   response layer
		NeuronAllele recurrentNeuron = config.newNeuronAllele( NeuronType.HIDDEN );
		alleles[ alleleIdx++ ] = recurrentNeuron;

		// out -> recurrent
		src = (NeuronAllele) alleles[ outIdx ];
		dest = recurrentNeuron;
		conn = config.newConnectionAllele( src.getInnovationId(), dest.getInnovationId() );
		conn.setToRandomValue( config.getRandomGenerator() );
		alleles[ alleleIdx++ ] = conn;

		// recurrent -> in
		src = recurrentNeuron;
		dest = (NeuronAllele) alleles[ inIdx ];
		conn = config.newConnectionAllele( src.getInnovationId(), dest.getInnovationId() );
		conn.setToRandomValue( config.getRandomGenerator() );
		alleles[ alleleIdx++ ] = conn;

		// hidden1 -> in
		src = (NeuronAllele) alleles[ hid1Idx ];
		dest = (NeuronAllele) alleles[ inIdx ];
		conn = config.newConnectionAllele( src.getInnovationId(), dest.getInnovationId() );
		conn.setToRandomValue( config.getRandomGenerator() );
		alleles[ alleleIdx++ ] = conn;

		// out -> in
		src = (NeuronAllele) alleles[ outIdx ];
		dest = (NeuronAllele) alleles[ inIdx ];
		conn = config.newConnectionAllele( src.getInnovationId(), dest.getInnovationId() );
		conn.setToRandomValue( config.getRandomGenerator() );
		alleles[ alleleIdx++ ] = conn;

		// out -> self
		src = (NeuronAllele) alleles[ outIdx ];
		conn = config.newConnectionAllele( src.getInnovationId(), src.getInnovationId() );
		conn.setToRandomValue( config.getRandomGenerator() );
		alleles[ alleleIdx++ ] = conn;

		// hidden -> self
		src = (NeuronAllele) alleles[ hid1Idx ];
		conn = config.newConnectionAllele( src.getInnovationId(), src.getInnovationId() );
		conn.setToRandomValue( config.getRandomGenerator() );
		alleles[ alleleIdx++ ] = conn;

		// in -> self
		src = (NeuronAllele) alleles[ inIdx ];
		conn = config.newConnectionAllele( src.getInnovationId(), src.getInnovationId() );
		conn.setToRandomValue( config.getRandomGenerator() );
		alleles[ alleleIdx++ ] = conn;

		// in -> in
		if ( dimStimuli > 2 ) {
			src = (NeuronAllele) alleles[ inIdx ];
			dest = (NeuronAllele) alleles[ inIdx + 1 ];
			conn = config.newConnectionAllele( src.getInnovationId(), dest.getInnovationId() );
			conn.setToRandomValue( config.getRandomGenerator() );
			alleles[ alleleIdx++ ] = conn;
		}

		// hidden -> hidden
		if ( dimHidden1 > 2 ) {
			src = (NeuronAllele) alleles[ hid1Idx ];
			dest = (NeuronAllele) alleles[ hid1Idx + 1 ];
			conn = config.newConnectionAllele( src.getInnovationId(), dest.getInnovationId() );
			conn.setToRandomValue( config.getRandomGenerator() );
			alleles[ alleleIdx++ ] = conn;
		}

		// out -> out
		if ( dimResponse > 2 ) {
			src = (NeuronAllele) alleles[ outIdx ];
			dest = (NeuronAllele) alleles[ outIdx + 1 ];
			conn = config.newConnectionAllele( src.getInnovationId(), dest.getInnovationId() );
			conn.setToRandomValue( config.getRandomGenerator() );
			alleles[ alleleIdx++ ] = conn;
		}
	}

	return Arrays.asList( alleles );
}

/**
 * @return new chromosome
 */
public Chromosome newSolveXorChromosome() {
	List alleles = new ArrayList( 17 );
	alleles.add( config.newNeuronAllele( NeuronType.INPUT ) );
	Long in1id = ( (NeuronAllele) alleles.get( 0 ) ).getInnovationId();
	alleles.add( config.newNeuronAllele( NeuronType.INPUT ) );
	Long in2id = ( (NeuronAllele) alleles.get( 1 ) ).getInnovationId();
	alleles.add( config.newNeuronAllele( NeuronType.INPUT ) );
	Long in3id = ( (NeuronAllele) alleles.get( 2 ) ).getInnovationId();
	alleles.add( config.newNeuronAllele( NeuronType.HIDDEN ) );
	Long hid1id = ( (NeuronAllele) alleles.get( 3 ) ).getInnovationId();
	alleles.add( config.newNeuronAllele( NeuronType.HIDDEN ) );
	Long hid2id = ( (NeuronAllele) alleles.get( 4 ) ).getInnovationId();
	alleles.add( config.newNeuronAllele( NeuronType.HIDDEN ) );
	Long hid3id = ( (NeuronAllele) alleles.get( 5 ) ).getInnovationId();
	alleles.add( config.newNeuronAllele( NeuronType.OUTPUT ) );
	Long out1id = ( (NeuronAllele) alleles.get( 6 ) ).getInnovationId();
	alleles.add( config.newConnectionAllele( in1id, hid1id ) );
	( (ConnectionAllele) alleles.get( 7 ) ).setWeight( 1 );
	alleles.add( config.newConnectionAllele( in1id, hid2id ) );
	( (ConnectionAllele) alleles.get( 8 ) ).setWeight( -1 );
	alleles.add( config.newConnectionAllele( in2id, hid1id ) );
	( (ConnectionAllele) alleles.get( 9 ) ).setWeight( -1 );
	alleles.add( config.newConnectionAllele( in2id, hid2id ) );
	( (ConnectionAllele) alleles.get( 10 ) ).setWeight( 1 );
	alleles.add( config.newConnectionAllele( in3id, hid1id ) );
	( (ConnectionAllele) alleles.get( 11 ) ).setWeight( -0.5 );
	alleles.add( config.newConnectionAllele( in3id, hid2id ) );
	( (ConnectionAllele) alleles.get( 12 ) ).setWeight( -0.5 );
	alleles.add( config.newConnectionAllele( hid1id, out1id ) );
	( (ConnectionAllele) alleles.get( 13 ) ).setWeight( 1 );
	alleles.add( config.newConnectionAllele( hid2id, out1id ) );
	( (ConnectionAllele) alleles.get( 14 ) ).setWeight( 1 );
	alleles.add( config.newConnectionAllele( in3id, hid3id ) );
	( (ConnectionAllele) alleles.get( 15 ) ).setWeight( 1.0 );
	alleles.add( config.newConnectionAllele( hid3id, out1id ) );
	( (ConnectionAllele) alleles.get( 16 ) ).setWeight( 0.5 );
	return new Chromosome( new ChromosomeMaterial( alleles ), config.nextChromosomeId() );
}

private int addNeurons( Allele[] alleles, int alleleIdx, int count, NeuronType type ) {
	for ( int i = 0; i < count; ++i ) {
		NeuronAllele allele = config.newNeuronAllele( type );
		alleles[ alleleIdx++ ] = allele;
	}
	return alleleIdx;
}

private int addFullConns( Allele[] alleles, int alleleIdx, int srcBase, int srcCount, int destCount ) {
	for ( int i = srcBase; i < srcBase + srcCount; ++i ) {
		for ( int j = srcBase + srcCount; j < srcBase + srcCount + destCount; ++j ) {
			NeuronAllele src = (NeuronAllele) alleles[ i ];
			NeuronAllele dest = (NeuronAllele) alleles[ j ];
			ConnectionAllele conn = config.newConnectionAllele( src.getInnovationId(), dest
					.getInnovationId() );
			conn.setToRandomValue( config.getRandomGenerator() );
			alleles[ alleleIdx++ ] = conn;
		}
	}
	return alleleIdx;
}

/**
 * @param config
 * @return new chromosome material
 * @throws Exception
 */
public static ChromosomeMaterial newMatureChromosomeMaterial( NeatConfiguration config )
		throws Exception {
	// get random initial seed chromosome, and loop through input and output neurons
	ChromosomeMaterial seedMaterial = ChromosomeMaterial.randomInitialChromosomeMaterial( config );
	SortedSet newAlleles = seedMaterial.getAlleles();
	Map inputNeurons = NeatChromosomeUtility.getNeuronMap( seedMaterial.getAlleles(),
			NeuronType.INPUT );
	Map outputNeurons = NeatChromosomeUtility.getNeuronMap( seedMaterial.getAlleles(),
			NeuronType.OUTPUT );
	Iterator inIter = inputNeurons.values().iterator();
	while ( inIter.hasNext() ) {
		NeuronAllele in = (NeuronAllele) inIter.next();
		Iterator outIter = outputNeurons.values().iterator();
		while ( outIter.hasNext() ) {
			NeuronAllele out = (NeuronAllele) outIter.next();
			// for a random half of them, create a new neuron and associated connections between them
			if ( config.getRandomGenerator().nextBoolean() ) {
				ConnectionAllele cAllele = config.newConnectionAllele( in.getInnovationId(), out
						.getInnovationId() );
				NeuronAllele hid = config.newNeuronAllele( cAllele.getInnovationId() );
				ConnectionAllele in2hid = config.newConnectionAllele( in.getInnovationId(), hid
						.getInnovationId() );
				in2hid.setToRandomValue( config.getRandomGenerator() );
				ConnectionAllele hid2out = config.newConnectionAllele( hid.getInnovationId(), out
						.getInnovationId() );
				hid2out.setToRandomValue( config.getRandomGenerator() );

				// create new alleles from old alleles + 3 new alleles
				newAlleles.add( hid );
				newAlleles.add( in2hid );
				newAlleles.add( hid2out );
			}
		}
	}
	return new ChromosomeMaterial( newAlleles );
}

}
