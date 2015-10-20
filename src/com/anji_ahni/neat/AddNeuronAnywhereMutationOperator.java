package com.anji_ahni.neat;

import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jgapcustomised.Allele;
import org.jgapcustomised.ChromosomeMaterial;
import org.jgapcustomised.Configuration;
import org.jgapcustomised.MutationOperator;

import com.anji_ahni.integration.AnjiRequiredException;
import com.anji_ahni.util.Configurable;
import com.anji_ahni.util.Properties;

/**
 * Mutation operator that adds neurons anywhere in the network and connections it with two new connections, with the
 * outgoing connection initially having a small weight value based on
 * {@link WeightMutationOperator#WEIGHT_MUTATE_STD_DEV_KEY}. Note that this operator is not enabled unless the classic
 * mutation scheme is disabled (see {@link NeatConfiguration#TOPOLOGY_MUTATION_CLASSIC_KEY}).
 * 
 * @author Oliver Coleman
 */
public class AddNeuronAnywhereMutationOperator extends MutationOperatorMultiple implements Configurable {
	/**
	 * Properties key, the mutation rate. The number of neurons added is a factor of this and the current number of
	 * neurons in the network.
	 */
	public static final String ADD_NEURON_ANYWHERE_MUTATE_RATE_KEY = "add.neuron.anywhere.mutation.rate";

	/**
	 * default mutation rate
	 */
	public static final double DEFAULT_MUTATE_RATE = 0.0;
	
	private AddConnectionMutationOperator addConnOperator;

	/**
	 * @see com.anji_ahni.util.Configurable#init(com.anji_ahni.util.Properties)
	 */
	public void init(Properties props) throws Exception {
		setMutationRate(props.getDoubleProperty(ADD_NEURON_ANYWHERE_MUTATE_RATE_KEY, DEFAULT_MUTATE_RATE));
		addConnOperator = (AddConnectionMutationOperator) props.singletonObjectProperty(AddConnectionMutationOperator.class);
	}

	/**
	 * @see AddNeuronAnywhereMutationOperator#AddNeuronAnywhereMutationOperator(double)
	 */
	public AddNeuronAnywhereMutationOperator() {
		this(DEFAULT_MUTATE_RATE);
	}

	/**
	 * @see MutationOperator#MutationOperator(double)
	 */
	public AddNeuronAnywhereMutationOperator(double newMutationRate) {
		super(newMutationRate);
	}

	protected void mutate(Configuration jgapConfig, final ChromosomeMaterial target, Set<Allele> allelesToAdd, Set<Allele> allelesToRemove) {
		if ((jgapConfig instanceof NeatConfiguration) == false)
			throw new AnjiRequiredException("com.anji.neat.NeatConfiguration");
		NeatConfiguration config = (NeatConfiguration) jgapConfig;

		// Add neurons.
		int numMutations = numMutations(config.getRandomGenerator(), 0);
		for (int i = 0; i < numMutations; i++) {
			List<NeuronAllele> neurons = NeatChromosomeUtility.getNeuronList(target.getAlleles());
			List<ConnectionAllele> connections = NeatChromosomeUtility.getConnectionList(target.getAlleles());
			addNeuron(config, neurons, connections, allelesToAdd, allelesToRemove);
		}
	}

	public void addNeuron(NeatConfiguration config, List<NeuronAllele> neurons, List<ConnectionAllele> connections, Set<Allele> allelesToAdd, Set<Allele> allelesToRemove) {
		NeuronAllele newNeuronAllele = config.newNeuronAllele(NeuronType.HIDDEN);
		allelesToAdd.add(newNeuronAllele);

		Random random = config.getRandomGenerator();
		boolean connected = false;
		while (!connected) {
			NeuronAllele src = neurons.get(random.nextInt(neurons.size()));
			NeuronAllele dest = neurons.get(random.nextInt(neurons.size()));
			if (NeatChromosomeUtility.connectionAllowed(config, src, dest, connections)) {
				ConnectionAllele newConn = config.newConnectionAllele(src.getInnovationId(), newNeuronAllele.getInnovationId());
				newConn.setToRandomValue(random, false);
				allelesToAdd.add(newConn);
				connections.add(newConn);

				newConn = config.newConnectionAllele(newNeuronAllele.getInnovationId(), dest.getInnovationId());
				newConn.setWeight(random.nextGaussian() * ConnectionAllele.RANDOM_STD_DEV_INITIAL);
				//newConn.setWeight((random.nextBoolean() ? 1 : -1) * random.nextDouble() * ConnectionAllele.RANDOM_STD_DEV);
				allelesToAdd.add(newConn);
				connections.add(newConn);
				connected = true;
			}
		}
	}
}
