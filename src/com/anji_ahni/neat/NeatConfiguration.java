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
 * created by Philip Tucker on Feb 22, 2003
 */
package com.anji_ahni.neat;

import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.jgapcustomised.BulkFitnessFunction;
import org.jgapcustomised.ChromosomeMaterial;
import org.jgapcustomised.Configuration;
import org.jgapcustomised.IdFactory;
import org.jgapcustomised.InvalidConfigurationException;
import org.jgapcustomised.NaturalSelector;
import org.jgapcustomised.event.EventManager;
import org.jgapcustomised.impl.CloneReproductionOperator;
import org.jgapcustomised.impl.WeightedRouletteSelector;

import com.anji_ahni.integration.SimpleSelector;
import com.anji_ahni.nn.RecurrencyPolicy;
import com.anji_ahni.util.Configurable;
import com.anji_ahni.util.Properties;
import com.anji_ahni.util.Randomizer;
import com.ojcoleman.ahni.util.ArrayUtil;

/**
 * Extension of JGAP configuration with NEAT-specific features added.
 * 
 * @author Philip Tucker
 */
public class NeatConfiguration extends Configuration implements Configurable {
	private static final Logger logger = Logger.getLogger(NeatConfiguration.class);
	protected static final String PERSIST_ENABLE_KEY = "persist.enable";
	/**
	 * properties key, file in which unique ID sequence number is stored
	 */
	public static final String ID_FACTORY_KEY = "id.file";
	public static final short DEFAULT_STIMULUS_SIZE = 3;
	protected static final short DEFAULT_INITIAL_HIDDEN_SIZE = 0;
	public static final short DEFAULT_RESPONSE_SIZE = 3;
	/**
	 * default survival rate
	 */
	public static final double DEFAULT_SURVIVAL_RATE = 0.20f;
	/**
	 * default crossover proportion
	 */
	public static final double DEFAULT_CROSSOVER_PROPORTION = 0.5f;
	/**
	 * default population size
	 */
	public static final int DEFAULT_POPUL_SIZE = 100;
	/**
	 * properties key, dimension of neural net stimulus
	 */
	public static final String STIMULUS_SIZE_KEY = "stimulus.size";
	/**
	 * properties key, dimension of neural net response
	 */
	public static final String RESPONSE_SIZE_KEY = "response.size";
	/**
	 * properties key, survival rate
	 */
	public static final String SURVIVAL_RATE_KEY = "survival.rate";
	/**
	 * properties key, proportion of new population that is generated via crossover from two parents (as opposed to mutation of a single parent), [0, 1].
	 */
	public static final String CROSSOVER_PROPORTION_KEY = "crossover.proportion";
	/**
	 * properties key, the probability that an individual produced by the crossover operator will be a candidate for having mutations applied to it (independent of other mutation probabilities).
	 */
	public static final String CROSSOVER_MUTATE_RATE_KEY = "crossover.mutate.probability";
	/**
	 * properties key, topology mutation type; if true, use "classic" method where at most a single topological mutation
	 * occurs per generation per individual
	 */
	public static final String TOPOLOGY_MUTATION_CLASSIC_KEY = "topology.mutation.classic";
	/**
	 * properties key, maximum connection weight
	 */
	public static final String WEIGHT_MAX_KEY = "weight.max";
	/**
	 * properties key, minimum connection weight
	 */
	public static final String WEIGHT_MIN_KEY = "weight.min";
	/**
	 * properties key, population size
	 */
	public static final String POPUL_SIZE_KEY = "popul.size";
	/**
	 * properties key, speciation chromosome compatibility excess coefficient
	 */
	public final static String CHROM_COMPAT_EXCESS_COEFF_KEY = "chrom.compat.excess.coeff";
	/**
	 * properties key, speciation chromosome compatibility disjoint coefficient
	 */
	public final static String CHROM_COMPAT_DISJOINT_COEFF_KEY = "chrom.compat.disjoint.coeff";
	/**
	 * properties key, speciation chromosome compatibility common coefficient
	 */
	public final static String CHROM_COMPAT_COMMON_COEFF_KEY = "chrom.compat.common.coeff";
	/**
	 * properties key, enable speciation chromosome compatibility normalisation (default is false).
	 */
	public final static String CHROM_COMPAT_NORMALISE_KEY = "chrom.compat.normalise";
	/**
	 * properties key, specifies whether mismatched genes (disjoint or excess) in chromosome distance calculations 
	 * should have their (weight) values added to the distance instead of adding a constant amount to the difference.
	 */
	public final static String CHROM_COMPAT_MISMATCH_USE_VALUES = "chrom.compat.mismatch_use_values";
	/**
	 * properties key, speciation threshold
	 */
	public final static String SPECIATION_THRESHOLD_KEY = "speciation.threshold";
	/**
	 * properties key, speciation threshold minimum value for auto-adjustment
	 */
	public final static String SPECIATION_THRESHOLD_MIN_KEY = "speciation.threshold.min";
	/**
	 * properties key, speciation threshold maximum value for auto-adjustment
	 */
	public final static String SPECIATION_THRESHOLD_MAX_KEY = "speciation.threshold.max";
	/**
	 * properties key, target number of species, default is popul.size / 15.
	 */
	public final static String SPECIATION_TARGET_KEY = "speciation.target";
	/**
	 * @deprecated Adjustment amounts are now calculated as a ratio of the target and current species counts. Properties key, amount to change speciation threshold to maintain speciation target
	 */
	public final static String SPECIATION_THRESHOLD_CHANGE_KEY = "speciation.threshold.change";
	/**
	 * properties key, elitism proportion
	 */
	public final static String ELITISM_PROPORTION_KEY = "selector.elitism.proportion";
	/**
	 * properties key, minimum number of elite members to select from a species, default is the number of objectives defined by fitness function.
	 */
	public final static String ELITISM_MIN_TO_SELECT_KEY = "selector.elitism.min.to.select";
	/**
	 * properties key, minimum size a specie must be to produce an elite member
	 */
	public final static String ELITISM_MIN_SPECIE_SIZE_KEY = "selector.elitism.min.specie.size";
	public final static String SPECIATED_FITNESS_KEY = "selector.speciated.fitness";
	public final static String MAX_STAGNANT_GENERATIONS_KEY = "selector.max.stagnant.generations";
	public final static String MAX_STAGNANT_MAINTAIN_FITTEST_GENERATIONS_KEY = "selector.max.stagnant.maintainfittest";
	public final static String MINIMUM_AGE_KEY = "selector.min.generations";
	

	/**
	 * properties key, the NaturalSelector to use to perform the selection process. Default is com.anji.integration.SimpleSelector.
	 */
	public final static String SELECTOR_CLASS_KEY = "selector.class";
	/**
	 * properties key, enable weighted selection process. Deprecated, use SELECTOR_CLASS_KEY (selector.class).
	 */
	public final static String WEIGHTED_SELECTOR_KEY = "selector.roulette";
	/**
	 * properties key, enable fully connected initial topologies. 
	 * @see #INITIAL_TOPOLOGY_MUTATION_FACTOR_KEY
	 */
	public final static String INITIAL_TOPOLOGY_FULLY_CONNECTED_KEY = "initial.topology.fully.connected";
	/**
	 * properties key, if {@link #INITIAL_TOPOLOGY_FULLY_CONNECTED_KEY} is disabled then the initial population will be generated by applying the mutation operators
	 * to clones of the sample chromosome (which will just contain the input and output neurons and possibly some hidden neurons). The mutation rates of the
	 * operators may be temporarily increased during this process by setting this property to a value greater than 1.
	 */
	public final static String INITIAL_TOPOLOGY_MUTATION_FACTOR_KEY = "initial.topology.mutation.factor";
	/**
	 * properties key, number of hidden neurons in initial topology
	 */
	public final static String INITIAL_TOPOLOGY_NUM_HIDDEN_NEURONS_KEY = "initial.topology.num.hidden.neurons";
	/**
	 * properties key, activation function type of neurons
	 */
	public final static String INITIAL_TOPOLOGY_ACTIVATION_KEY = "initial.topology.activation";
	/**
	 * properties key, activation function type of input neurons
	 */
	public final static String INITIAL_TOPOLOGY_ACTIVATION_INPUT_KEY = "initial.topology.activation.input";
	/**
	 * properties key, activation function type of output neurons
	 */
	public final static String INITIAL_TOPOLOGY_ACTIVATION_OUTPUT_KEY = "initial.topology.activation.output";
	/**
	 * properties key, allowed activation function types if the INITIAL_TOPOLOGY_ACTIVATION_KEY is "random".
	 */
	public final static String INITIAL_TOPOLOGY_ACTIVATION_RANDOM_ALLOWED_KEY = "initial.topology.activation.random.allowed";
	
	/**
	 * properties key, allowed activation function types if the INITIAL_TOPOLOGY_ACTIVATION_KEY is "random".
	 */
	public final static String INITIAL_TOPOLOGY_ACTIVATION_RANDOM_PROBABILITIES_KEY = "initial.topology.activation.random.probabilities";
	
	/**
	 * properties key, if "true" then bias should be provided to the network via an input, with value set by fitness function (old way of doing it), 
	 * if "false" then the neurons will use internal bias values (new way). Default is "true".
	 */
	public final static String BIAS_VIA_INPUT_KEY = "bias.via.input";


	private Properties props;
	protected CloneReproductionOperator cloneOper = null;
	protected NeatCrossoverReproductionOperator crossoverOper = null;
	protected double maxConnectionWeight = Float.MAX_VALUE;
	protected double minConnectionWeight = -Float.MAX_VALUE;
	protected String inputActivationType;
	protected String outputActivationType;
	protected String hiddenActivationType;
	protected String[] hiddenActivationTypeRandomAllowed;
	protected double[] hiddenActivationTypeRandomDistribution;
	private NeatIdMap neatIdMap;
	RecurrencyPolicy recurrencyPolicy;
	
	/**
	 * Initialize mutation operators.
	 * 
	 * @throws InvalidConfigurationException
	 */
	protected void initMutation() throws InvalidConfigurationException {
		// remove connection
		RemoveConnectionMutationOperator removeOperator = props.singletonObjectProperty(RemoveConnectionMutationOperator.class);
		if ((removeOperator.getMutationRate() > 0.0) && (removeOperator.getMaxWeightRemoved() > 0.0)) {
			addMutationOperator(removeOperator);
		}
		
		// modify weight.
		// We do this before add connection operator so a new connection won't have its weight set to a random value 
		// and then immediately perturbed by the weight mutation operator (effectively doubling the std dev). 
		WeightMutationOperator weightOperator = props.singletonObjectProperty(WeightMutationOperator.class);
		if (weightOperator.getMutationRate() > 0.0) {
			addMutationOperator(weightOperator);
		}

		// add topology
		boolean isTopologyMutationClassic = props.getBooleanProperty(TOPOLOGY_MUTATION_CLASSIC_KEY, true);
		if (isTopologyMutationClassic) {
			SingleTopologicalMutationOperator singleOperator = props.singletonObjectProperty(SingleTopologicalMutationOperator.class);
			if (singleOperator.getMutationRate() > 0.0) {
				addMutationOperator(singleOperator);
			}
		} else {
			// add neuron
			AddNeuronMutationOperator addNeuronOperator = props.singletonObjectProperty(AddNeuronMutationOperator.class);
			if (addNeuronOperator.getMutationRate() > 0.0) {
				addMutationOperator(addNeuronOperator);
			}
			
			// add neuron anywhere
			AddNeuronAnywhereMutationOperator addNeuronAnywhereOperator = props.singletonObjectProperty(AddNeuronAnywhereMutationOperator.class);
			if (addNeuronAnywhereOperator.getMutationRate() > 0.0) {
				addMutationOperator(addNeuronAnywhereOperator);
			}

			// add connection
			AddConnectionMutationOperator addConnOperator = props.singletonObjectProperty(AddConnectionMutationOperator.class);
			if (addConnOperator.getMutationRate() > 0.0) {
				addMutationOperator(addConnOperator);
			}
		}
		
		// prune
		PruneMutationOperator pruneOperator = props.singletonObjectProperty(PruneMutationOperator.class);
		if (pruneOperator.getMutationRate() > 0.0) {
			addMutationOperator(pruneOperator);
		}
	}

	/**
	 * See <a href=" {@docRoot} /params.htm" target="anji_params">Parameter Details </a> for specific property settings.
	 * 
	 * @param newProps configuration parameters; newProps[SURVIVAL_RATE_KEY] should be < 0.50f
	 * @throws InvalidConfigurationException
	 */
	public void init(Properties newProps) throws InvalidConfigurationException {
		props = newProps;

		Randomizer r = (Randomizer) props.singletonObjectProperty(Randomizer.class);
		setRandomGenerator(r.getRand());
		setEventManager(new EventManager());

		// id persistence
		String s = props.getProperty(ID_FACTORY_KEY, null);
		try {
			if (s != null) {
				setIdFactory(new IdFactory(s));
			}
		} catch (IOException e) {
			String msg = "could not load IDs";
			logger.error(msg, e);
			throw new InvalidConfigurationException(msg);
		}
		
		recurrencyPolicy = RecurrencyPolicy.load(props);
		
		// make sure numbers add up
		double survivalRate = props.getDoubleProperty(SURVIVAL_RATE_KEY, DEFAULT_SURVIVAL_RATE);
		//double crossoverSlice = 1.0f - ( 2.0f * survivalRate );
		//if ( crossoverSlice < 0.0f )
		//throw new InvalidConfigurationException( "survival rate too large: " + survivalRate );
		//logger.info( "Crossover proportion: " + crossoverSlice);
		double crossoverProportion = props.getDoubleProperty(CROSSOVER_PROPORTION_KEY, DEFAULT_CROSSOVER_PROPORTION);

		BulkFitnessFunction bulkFitnessFunc = (BulkFitnessFunction) props.singletonObjectProperty(Evolver.FITNESS_FUNCTION_CLASS_KEY);
		
		// selector
		NaturalSelector selector = null;
		if (props.getBooleanProperty(WEIGHTED_SELECTOR_KEY, false)) {
			selector = new WeightedRouletteSelector();
			logger.warn("Property " + WEIGHTED_SELECTOR_KEY + " is deprecated. Use " + SELECTOR_CLASS_KEY + " instead.");
		} else {
			String selStr = props.getProperty(SELECTOR_CLASS_KEY, "auto").trim();
			if (selStr.toLowerCase().equals("auto")) {
				selStr = bulkFitnessFunc.getObjectiveCount() > 1 ? "com.ojcoleman.ahni.misc.NSGAIISelector" : "com.anji_ahni.integration.SimpleSelector";
				logger.info("Using " + selStr + " as the NaturalSelector.");
			}
			Class selCls;
			try {
				selCls = Class.forName(selStr);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new InvalidConfigurationException("Unable to find class for NaturalSelector.");
			}
			selector = (NaturalSelector) props.newObjectProperty(selCls);
		}
		
		selector.setSurvivalRate(survivalRate);
		selector.setElitismProportion(props.getFloatProperty(ELITISM_PROPORTION_KEY, 0.1f));
		selector.setElitismMinToSelect(props.getIntProperty(ELITISM_MIN_TO_SELECT_KEY, bulkFitnessFunc.getObjectiveCount()));
		selector.setElitismMinSpeciesSize(props.getIntProperty(ELITISM_MIN_SPECIE_SIZE_KEY, 5));
		selector.setSpeciatedFitness(props.getBooleanProperty(SPECIATED_FITNESS_KEY, true));
		int maxStagGen = props.getIntProperty(MAX_STAGNANT_GENERATIONS_KEY, Integer.MAX_VALUE);
		if (maxStagGen <= 0) maxStagGen = Integer.MAX_VALUE;
		selector.setMaxStagnantGenerations(maxStagGen);
		selector.setMinAge(props.getIntProperty(MINIMUM_AGE_KEY, 10));
		setNaturalSelector(selector);

		// reproduction
		// double reproductionSlice = 1 - survivalRate; //if a certain percentage of pop survives next to generation.
		double reproductionSlice = 1; // if only elites survive to next generation. exact number of elites changes so
										// handle at reproduction time.
		cloneOper = new CloneReproductionOperator();
		crossoverOper = new NeatCrossoverReproductionOperator();
		getCloneOperator().setSlice(reproductionSlice * (1 - crossoverProportion));
		getCrossoverOperator().setSlice(reproductionSlice * crossoverProportion);
		getCloneOperator().setMutateRate(1.0);
		getCrossoverOperator().setMutateRate(props.getDoubleProperty(CROSSOVER_MUTATE_RATE_KEY, 1.0));
		addReproductionOperator(getCloneOperator());
		addReproductionOperator(getCrossoverOperator());

		// mutation
		initMutation();

		// population
		setPopulationSize(props.getIntProperty(POPUL_SIZE_KEY, DEFAULT_POPUL_SIZE));
		hiddenActivationType = props.getProperty(INITIAL_TOPOLOGY_ACTIVATION_KEY, "sigmoid");

		if (hiddenActivationType.equals("random")) {
			hiddenActivationTypeRandomAllowed = props.getProperty(INITIAL_TOPOLOGY_ACTIVATION_RANDOM_ALLOWED_KEY, "sigmoid, gaussian, absolute, sine").split(",");
			int count = hiddenActivationTypeRandomAllowed.length;
			double[] probs = props.getDoubleArrayProperty(INITIAL_TOPOLOGY_ACTIVATION_RANDOM_PROBABILITIES_KEY, null);
			if (probs == null) {
				probs = new double[count];
				Arrays.fill(probs, 1.0 / count);
			}
			else if (probs.length != count) {
				throw new IllegalArgumentException("The number of items for " + INITIAL_TOPOLOGY_ACTIVATION_RANDOM_ALLOWED_KEY + " does not match the number of items for " +  INITIAL_TOPOLOGY_ACTIVATION_RANDOM_PROBABILITIES_KEY + ".");
			}
			else {
				ArrayUtil.normaliseSum(probs); 
			}
			double currentDist = 0;
			hiddenActivationTypeRandomDistribution = new double[count];
			for (int i = 0; i < count; i++) {
				hiddenActivationTypeRandomAllowed[i] = hiddenActivationTypeRandomAllowed[i].trim().toLowerCase();
				currentDist += probs[i];
				hiddenActivationTypeRandomDistribution[i] = currentDist;
			}
		}
		
		inputActivationType = props.getProperty(INITIAL_TOPOLOGY_ACTIVATION_INPUT_KEY, hiddenActivationType);
		outputActivationType = props.getProperty(INITIAL_TOPOLOGY_ACTIVATION_OUTPUT_KEY, hiddenActivationType);

		// System.out.println(props.getProperty(INITIAL_TOPOLOGY_ACTIVATION_INPUT_KEY, null ));
		// System.out.println(inputActivationType + ", " + hiddenActivationType + ", " + outputActivationType);

		load();

		ChromosomeMaterial sample = NeatChromosomeUtility.newSampleChromosomeMaterial(props.getShortProperty(STIMULUS_SIZE_KEY, DEFAULT_STIMULUS_SIZE), props.getShortProperty(INITIAL_TOPOLOGY_NUM_HIDDEN_NEURONS_KEY, DEFAULT_INITIAL_HIDDEN_SIZE), props.getShortProperty(RESPONSE_SIZE_KEY, DEFAULT_RESPONSE_SIZE), this, props.getBooleanProperty(INITIAL_TOPOLOGY_FULLY_CONNECTED_KEY, true));
		setSampleChromosomeMaterial(sample);

		if (props.getBooleanProperty(PERSIST_ENABLE_KEY, false)) {
			store();
		}

		// weight bounds
		maxConnectionWeight = props.getDoubleProperty(WEIGHT_MAX_KEY, Float.MAX_VALUE);
		minConnectionWeight = props.getDoubleProperty(WEIGHT_MIN_KEY, -maxConnectionWeight);

		// speciation parameters
		initSpeciationParms();
	}

	/**
	 * @see NeatConfiguration#init(Properties)
	 */
	public NeatConfiguration() {
		super();
	}

	/**
	 * See <a href=" {@docRoot} /params.htm" target="anji_params">Parameter Details </a> for specific property settings.
	 * 
	 * @param newProps
	 * @see NeatConfiguration#init(Properties)
	 * @throws InvalidConfigurationException
	 */
	public NeatConfiguration(Properties newProps) throws InvalidConfigurationException {
		super();
		init(newProps);
	}

	protected void initSpeciationParms() {
		getSpeciationParms().setSpecieCompatExcessCoeff(props.getDoubleProperty(CHROM_COMPAT_EXCESS_COEFF_KEY, 1));
		getSpeciationParms().setSpecieCompatDisjointCoeff(props.getDoubleProperty(CHROM_COMPAT_DISJOINT_COEFF_KEY, 1));
		getSpeciationParms().setSpecieCompatCommonCoeff(props.getDoubleProperty(CHROM_COMPAT_COMMON_COEFF_KEY, 1));
		getSpeciationParms().setSpecieCompatNormalise(props.getBooleanProperty(CHROM_COMPAT_NORMALISE_KEY, false));
		getSpeciationParms().setSpecieCompatMismatchUseValues(props.getBooleanProperty(CHROM_COMPAT_MISMATCH_USE_VALUES, false));
		getSpeciationParms().setSpeciationThreshold(props.getDoubleProperty(SPECIATION_THRESHOLD_KEY, 16));
		getSpeciationParms().setSpeciationThresholdMin(props.getDoubleProperty(SPECIATION_THRESHOLD_MIN_KEY, 0));
		getSpeciationParms().setSpeciationThresholdMax(props.getDoubleProperty(SPECIATION_THRESHOLD_MAX_KEY, Double.MAX_VALUE));
		getSpeciationParms().setSpeciationTarget(props.getIntProperty(SPECIATION_TARGET_KEY, (int) Math.round(Math.pow(getPopulationSize(), 0.6))));

		if (props.containsKey(SPECIATION_THRESHOLD_CHANGE_KEY)) {
			logger.warn(SPECIATION_THRESHOLD_CHANGE_KEY + " is deprecated, adjustment amounts are now calculated as a ratio of the target and current species counts.");
		}
	}

	/**
	 * factory method to construct new neuron allele with unique innovation ID of specified <code>type</code>
	 * 
	 * @param type
	 * @return NeuronAllele
	 */
	public NeuronAllele newNeuronAllele(NeuronType type) {
		String funcType;
		if (NeuronType.INPUT.equals(type)) {
			funcType = inputActivationType;
		} else if (NeuronType.OUTPUT.equals(type)) {
			funcType = outputActivationType;
		} else {
			funcType = hiddenActivationType;
		}		
		return newNeuronAllele(type, nextInnovationId(), funcType, 0);
	}

	/**
	 * Factory method to construct new neuron allele which has replaced connection <code>connectionId</code> according
	 * to NEAT add neuron mutation. If a previous mutation has occurred adding a neuron on connection connectionId,
	 * returns a neuron with that id - otherwise, a new id.
	 * 
	 * @param connectionId
	 * @return NeuronAllele
	 */
	public NeuronAllele newNeuronAllele(Long connectionId) {
		Long id = neatIdMap.findNeuronId(connectionId);
		if (id == null) {
			id = nextInnovationId();
			neatIdMap.putNeuronId(connectionId, id);
		}
		return newNeuronAllele(NeuronType.HIDDEN, id, hiddenActivationType, 0);
	}
	
	// Provides special handling for funcType == "random"
	private NeuronAllele newNeuronAllele(NeuronType type, Long id, String funcType, double bias) {
		if (funcType.equals("random")) {
			double p = getRandomGenerator().nextDouble();
			int index = 0;
			while (p > hiddenActivationTypeRandomDistribution[index]) index++;
			funcType = hiddenActivationTypeRandomAllowed[index];
		}

		NeuronGene gene = new NeuronGene(type, id, funcType);
		return new NeuronAllele(gene, bias);
	}

	/**
	 * factory method to construct new connection allele from neuron <code>srcNeuronId</code> to neuron
	 * <code>destNeuronId</code> according to NEAT add connection mutation; if a previous mutation has occurred adding a
	 * connection between srcNeuronId and destNeuronId, returns connection with that id; otherwise, new innovation id
	 * 
	 * @param srcNeuronId
	 * @param destNeuronId
	 * @return ConnectionAllele
	 */
	public ConnectionAllele newConnectionAllele(Long srcNeuronId, Long destNeuronId) {
		return newConnectionAllele(srcNeuronId, destNeuronId, 0);
	}
	
	/**
	 * factory method to construct new connection allele from neuron <code>srcNeuronId</code> to neuron
	 * <code>destNeuronId</code> according to NEAT add connection mutation; if a previous mutation has occurred adding a
	 * connection between srcNeuronId and destNeuronId, returns connection with that id; otherwise, new innovation id
	 * 
	 * @param srcNeuronId
	 * @param destNeuronId
	 * @param weight
	 * @return ConnectionAllele
	 */
	public ConnectionAllele newConnectionAllele(Long srcNeuronId, Long destNeuronId, double weight) {
		Long id = neatIdMap.findConnectionId(srcNeuronId, destNeuronId);
		if (id == null) {
			id = nextInnovationId();
			neatIdMap.putConnectionId(srcNeuronId, destNeuronId, id);
		}
		ConnectionGene gene = new ConnectionGene(id, srcNeuronId, destNeuronId);
		ConnectionAllele allele = new ConnectionAllele(gene);
		allele.setWeight(weight);
		return allele;
	}


	/**
	 * @return clone reproduction operator used to create mutated asexual offspring
	 */
	public CloneReproductionOperator getCloneOperator() {
		return cloneOper;
	}

	/**
	 * @return crossover reproduction operator used to create mutated sexual offspring
	 */
	public NeatCrossoverReproductionOperator getCrossoverOperator() {
		return crossoverOper;
	}

	/**
	 * @return maximum conneciton weight
	 */
	public double getMaxConnectionWeight() {
		return maxConnectionWeight;
	}

	/**
	 * @return minimum conneciton weight
	 */
	public double getMinConnectionWeight() {
		return minConnectionWeight;
	}
	
	/**
	 * Returns true if bias should be provided to the network via an input with value set by fitness function (old way of doing it), 
	 * or false then the neurons will use internal bias values (new way).
	 * @see #BIAS_VIA_INPUT_KEY
	 */
	public boolean biasViaInput() {
		return props.getBooleanProperty(BIAS_VIA_INPUT_KEY, true);
	}

	/**
	 * Load from persistence.
	 * 
	 * @throws InvalidConfigurationException
	 */
	public void load() throws InvalidConfigurationException {
		if (neatIdMap == null) {
			neatIdMap = new NeatIdMap(props);
			try {
				neatIdMap.load();
			} catch (IOException e) {
				String msg = "error loading ID map";
				logger.error(msg, e);
				throw new InvalidConfigurationException(msg);
			}
		}
	}

	/**
	 * Store to persistence.
	 * 
	 * @throws InvalidConfigurationException
	 */
	public void store() throws InvalidConfigurationException {
		System.out.println("\n\nhere4\n");

		try {
			getIdFactory().store();
			if (neatIdMap.store()) {
				neatIdMap = null;
			}
		} catch (IOException e) {
			String msg = "error storing ID map";
			logger.error(msg, e);
			throw new InvalidConfigurationException(msg);
		}
	}

	/**
	 * log stats for id maps
	 * 
	 * @param aLogger
	 * @param pri priority
	 */
	public void logIdMaps(Logger aLogger, Priority pri) {
		neatIdMap.log(aLogger, pri);
	}

	public RecurrencyPolicy getRecurrencyPolicy() {
		return recurrencyPolicy;
	}
}
