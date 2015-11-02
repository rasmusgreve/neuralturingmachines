package com.ojcoleman.ahni.hyperneat;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.imageio.ImageIO;











import org.apache.log4j.Logger;
import org.jgapcustomised.BulkFitnessFunction;
import org.jgapcustomised.Chromosome;
import org.jgapcustomised.Genotype;
import org.jgapcustomised.NaturalSelector;
import org.jgapcustomised.Species;
import org.jgapcustomised.event.GeneticEvent;
import org.jgapcustomised.event.GeneticEventListener;

import com.anji_ahni.integration.Activator;
import com.anji_ahni.integration.ActivatorTranscriber;
import com.anji_ahni.integration.AnjiNetTranscriber;
import com.anji_ahni.integration.LogEventListener;
import com.anji_ahni.integration.PersistenceEventListener;
import com.anji_ahni.integration.PresentationEventListener;
import com.anji_ahni.integration.Transcriber;
import com.anji_ahni.integration.TranscriberException;
import com.anji_ahni.neat.AddConnectionMutationOperator;
import com.anji_ahni.neat.AddNeuronAnywhereMutationOperator;
import com.anji_ahni.neat.AddNeuronMutationOperator;
import com.anji_ahni.neat.Evolver;
import com.anji_ahni.neat.NEATGenotype;
import com.anji_ahni.persistence.Persistence;
import com.anji_ahni.run.Run;
import com.anji_ahni.util.Misc;
import com.anji_ahni.util.Randomizer;
import com.anji_ahni.util.Reset;
import com.ojcoleman.ahni.evaluation.AHNIFitnessFunction;
import com.ojcoleman.ahni.event.AHNIEvent;
import com.ojcoleman.ahni.event.AHNIEventListener;
import com.ojcoleman.ahni.nn.BainNN;
import com.ojcoleman.ahni.nn.NNAdaptor;
import com.ojcoleman.ahni.transcriber.HyperNEATTranscriber;
import com.ojcoleman.ahni.transcriber.TranscriberAdaptor;
import com.ojcoleman.ahni.transcriber.HyperNEATTranscriber.CPPN;
import com.ojcoleman.ahni.util.ArrayUtil;
import com.ojcoleman.ahni.util.NiceWriter;
import com.ojcoleman.ahni.util.PaddingDecimalFormat;

/**
 * Configures and performs an AHNI evolutionary run.
 * 
 * @see Run
 * 
 * @author Oliver Coleman
 */
public class HyperNEATEvolver implements Configurable, GeneticEventListener {
	private static Logger logger = Logger.getLogger(HyperNEATEvolver.class);
	private static final Runtime runtime = Runtime.getRuntime();

	private static final DecimalFormat nf4 = new DecimalFormat("0.0000");
	private static final DecimalFormat nf3 = new DecimalFormat("0.000");
	private static final DecimalFormat nf1 = new DecimalFormat("0.0");
	private static final PaddingDecimalFormat nfi9 = new PaddingDecimalFormat("0", 9);

	/**
	 * properties key, # generations in run
	 */
	public static final String NUM_GENERATIONS_KEY = "num.generations";
	public static final String PERFORMANCE_TARGET_TYPE_KEY = "performance.target.type";
	public static final String PERFORMANCE_TARGET_AVERAGE_KEY = "performance.target.average";
	public static final String PERFORMANCE_TARGET_KEY = "performance.target";
	/**
	 * properties key, fitness function class
	 */
	public static final String FITNESS_FUNCTION_CLASS_KEY = "fitness_function";
	// private static final String FITNESS_THRESHOLD_KEY = "fitness.threshold";
	public static final String RESET_KEY = "run.reset";
	public static final String HIBERNATE_ENABLE_KEY = "hibernate.enable";
	public static final String LOGGING_ENABLE_KEY = "logging.enable";
	public static final String LOAD_GENOTYPE_KEY = "persist.load.genotype";
	public static final String PERSIST_ENABLE_KEY = "persist.enable";
	public static final String PRESENTATION_GENERATE_KEY = "presentation.generate";
	public static final String LOG_PER_GENERATIONS_KEY = "log.pergenerations";
	public static final String LOG_CHAMP_TOSTRING_KEY = "log.champ.tostring";
	public static final String LOG_CHAMP_TOIMAGE_KEY = "log.champ.toimage";
	public static final String LOG_SPECIES_HISTORY_KEY = "log.species_history";
	public static final String INITIAL_CPPN = "hyperneat.cppn.initial";

	private HyperNEATConfiguration config = null;
	private List<AHNIEventListener> listeners = new ArrayList<AHNIEventListener>();
	private Properties properties = null;
	private NEATGenotype genotype = null;
	private int numEvolutions = 0;
	private double targetPerformance = 1;
	private Persistence db = null;
	private boolean loadGenotypeFromDB = false;
	private BulkFitnessFunction bulkFitnessFunc;
	private int logPerGenerations = 1;
	int logChampToString = -1;
	int logChampToImage = -1;
	
	protected int generation = 0;
	protected Chromosome fittest = null;
	protected Chromosome bestPerforming = null;

	/**
	 * The fittest Chromosome from each generation.
	 */
	protected Chromosome[] fittestChromosomes;
	/**
	 * The best performing Chromosome from each generation. Sometimes this is equivalent to fittest depending on whether
	 * the fitness function defines performance independently.
	 */
	protected Chromosome[] bestPerformingChromosomes;
	/**
	 * The highest fitness achieved in each generation, as a fraction of the maximum possible fitness. This is
	 * maintained as well as fittestChromosomes because the fitness achieved can vary when the fitness function is
	 * stochastic.
	 */
	protected double[] bestFitnesses;
	/**
	 * The highest performance achieved in each generation. This is maintained as well as bestPerformingChromosomes
	 * because the performance achieved can vary when the fitness function is stochastic.
	 */
	protected double[] bestPerformances;

	/**
	 * ctor; must call {@link #init(Properties)} before using this object. Generally the
	 */
	public HyperNEATEvolver() {
	}

	/**
	 * Construct new evolver with given properties. See <a href=" {@docRoot} /params.htm" target="anji_params">Parameter
	 * Details </a> for specific property settings.
	 * 
	 * @see Configurable#init(Properties)
	 */
	public void init(Properties props) throws Exception {
		properties = props;
		properties.setEvolver(this);
		
		if (props.getBooleanProperty(PERSIST_ENABLE_KEY, false)) {
			boolean doReset = props.getBooleanProperty(RESET_KEY, false);
			if (doReset) {
				// logger.warn( "Resetting previous run !!!" );
				Reset resetter = new Reset(props);
				resetter.setUserInteraction(false);
				resetter.reset();
			}
		}
		// fitness function
		bulkFitnessFunc = (BulkFitnessFunction) props.singletonObjectProperty(FITNESS_FUNCTION_CLASS_KEY);

		config = (HyperNEATConfiguration) props.singletonObjectProperty(HyperNEATConfiguration.class);
		config.setBulkFitnessFunction(bulkFitnessFunc);
		
		// Don't do this set-up if this is a minion instance, see {@link com.ojcoleman.ahni.hyperneat.Minion}.
		if (!props.getBooleanProperty("isminion", false)) {
			// peristence
			db = (Persistence) props.singletonObjectProperty(Persistence.PERSISTENCE_CLASS_KEY);
	
			loadGenotypeFromDB = props.getBooleanProperty(LOAD_GENOTYPE_KEY);
	
			numEvolutions = props.getIntProperty(NUM_GENERATIONS_KEY);
			targetPerformance = props.getFloatProperty(PERFORMANCE_TARGET_KEY, 1);
			// targetFitness = props.getFloatProperty(FITNESS_TARGET_KEY, 1);
			// thresholdFitness = props.getFloatProperty(FITNESS_THRESHOLD_KEY, targetFitness);
			logPerGenerations = props.getIntProperty(LOG_PER_GENERATIONS_KEY, 1);
			logChampToString = props.getIntProperty(LOG_CHAMP_TOSTRING_KEY, -1);
			logChampToImage = props.getIntProperty(LOG_CHAMP_TOIMAGE_KEY, -1);
	
			//
			// event listeners
			//
	
			// run
			// TODO - hibernate
			Run run = (Run) props.singletonObjectProperty(Run.class);
			if (props.getBooleanProperty(HIBERNATE_ENABLE_KEY, true)) {
				db.startRun(run.getName());
				config.getEventManager().addEventListener(GeneticEvent.GENOTYPE_EVALUATED_EVENT, run);
			}
	
			if (props.getBooleanProperty(LOGGING_ENABLE_KEY, true)) {
				// logging
				LogEventListener logListener = new LogEventListener(config);
				config.getEventManager().addEventListener(GeneticEvent.GENOTYPE_EVOLVED_EVENT, logListener);
				config.getEventManager().addEventListener(GeneticEvent.GENOTYPE_EVALUATED_EVENT, logListener);
			}
	
			// persistence
			if (props.getBooleanProperty(PERSIST_ENABLE_KEY, false)) {
				PersistenceEventListener dbListener = new PersistenceEventListener(config, run);
				dbListener.init(props);
				config.getEventManager().addEventListener(GeneticEvent.GENOTYPE_START_GENETIC_OPERATORS_EVENT, dbListener);
				config.getEventManager().addEventListener(GeneticEvent.GENOTYPE_FINISH_GENETIC_OPERATORS_EVENT, dbListener);
				config.getEventManager().addEventListener(GeneticEvent.GENOTYPE_EVALUATED_EVENT, dbListener);
			}
			// else {
			// config.load();
			// }
	
			// presentation
			if (props.getBooleanProperty(PRESENTATION_GENERATE_KEY, false)) {
				PresentationEventListener presListener = new PresentationEventListener(run);
				presListener.init(props);
				config.getEventManager().addEventListener(GeneticEvent.GENOTYPE_EVALUATED_EVENT, presListener);
				config.getEventManager().addEventListener(GeneticEvent.RUN_COMPLETED_EVENT, presListener);
			}
	
			config.getEventManager().addEventListener(GeneticEvent.GENOTYPE_START_EVALUATION_EVENT, this);
			config.getEventManager().addEventListener(GeneticEvent.GENOTYPE_EVALUATED_EVENT, this);
			config.getEventManager().addEventListener(GeneticEvent.GENOTYPE_START_GENETIC_OPERATORS_EVENT, this);
			config.getEventManager().addEventListener(GeneticEvent.GENOTYPE_FINISH_GENETIC_OPERATORS_EVENT, this);
		}
	}

	/**
	 * Get the configuration object, primarily used by JGAP and ANJI.
	 * 
	 * @see org.jgapcustomised.Configuration
	 */
	public HyperNEATConfiguration getConfig() {
		return config;
	}

	/**
	 * Add the given event listener to this evolver.
	 */
	public void addEventListener(AHNIEventListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove the given event listener from this evolver.
	 */
	public void removeEventListener(AHNIEventListener listener) {
		listeners.remove(listener);
	}

	protected void fireEvent(AHNIEvent event) {
		for (AHNIEventListener listener : listeners) {
			listener.ahniEventOccurred(event);
		}
	}

	/**
	 * Perform a single run. This is synchronised as the object contains state variables (other than configurable
	 * properties) and so multiple runs should not be performed in parallel. This is in contrast to most
	 * {@link Configurable} objects that do not contain state variables other than configurable
	 * properties.
	 * 
	 * @return An array containing the best performance achieved in each generation.
	 * @throws Exception
	 */
	public synchronized double[] run() throws Exception {
		if (properties.containsKey(HyperNEATConfiguration.SEED_CHROMOSOME_KEY)) {
			Chromosome sampleChrom = new Chromosome(config.getSampleChromosomeMaterial(), -1L, config.getObjectiveCount(), config.getNoveltyObjectiveCount());
			logChamp(sampleChrom, true, "seed_");
		}
		
		fittestChromosomes = new Chromosome[numEvolutions];
		bestPerformingChromosomes = new Chromosome[numEvolutions];
		bestFitnesses = new double[numEvolutions];
		bestPerformances = new double[numEvolutions];

		// TODO loading genotype from storage is broken?
		/*if (loadGenotypeFromDB) {
			// load population, either from previous run or random
			genotype = db.loadGenotype(config);
			if (genotype != null) {
			} else {
				genotype = NEATGenotype.randomInitialGenotype(properties, config);
			}
		} else {*/
			genotype = NEATGenotype.randomInitialGenotype(properties, config);
		//}

		if (logger.isDebugEnabled()) {
			// Log CPPN represented by each initial Chromosome.
			Transcriber transcriber = properties.singletonObjectProperty(AnjiNetTranscriber.class);
			for (Chromosome c : genotype.getChromosomes()) {
				Activator substrate = transcriber.transcribe(c);
				logger.debug(substrate.toXml());
			}
		}
		
		NaturalSelector selector = config.getNaturalSelector();

		// Initialise result data.
		int generationOfFirstSolution = -1;
		fittest = genotype.getFittestChromosome();

		BufferedWriter speciesInfoWriter = null;
		if (properties.logFilesEnabled() && properties.getBooleanProperty(LOG_SPECIES_HISTORY_KEY, false)) {
			File dirFile = new File(properties.getProperty(HyperNEATConfiguration.OUTPUT_DIR_KEY));
			if (!dirFile.exists())
				dirFile.mkdirs();
			speciesInfoWriter = new BufferedWriter(new FileWriter(properties.getProperty(HyperNEATConfiguration.OUTPUT_DIR_KEY) + properties.getProperty(HyperNEATConfiguration.OUTPUT_PREFIX_KEY, "") + "species-history-size.csv"));
			StringBuffer output = new StringBuffer();
			output.append("Gen,\tTSE,\tTS,\tNew,\tExt");
			for (int i = 0; i < 100; i++)
				output.append(",\t" + i);
			output.append("\n");
			speciesInfoWriter.write(output.toString());
			speciesInfoWriter.flush();
		}

		double avgGenTime = 0, cumulativeDurationBetweenLogging = 0;
		int previousSpeciesCount = 0;
		TreeMap<Long, Species> allSpeciesEver = new TreeMap<Long, Species>();

		fireEvent(new AHNIEvent(AHNIEvent.Type.RUN_START, this, this));

		for (generation = 0; generation < numEvolutions && !bulkFitnessFunc.endRun(); generation++) {
			long start = System.currentTimeMillis();
			
			fireEvent(new AHNIEvent(AHNIEvent.Type.GENERATION_START, this, this));

			previousSpeciesCount = genotype.getSpecies().size();

			// Perform one evolutionary generation: evaluate individuals, generate new population.
			fittest = genotype.evolve();
			
			bestPerforming = genotype.getBestPerforming();

			// result data
			if (bulkFitnessFunc.endRun())
				generationOfFirstSolution = generation;

			fittestChromosomes[generation] = fittest;
			bestPerformingChromosomes[generation] = bestPerforming;
			bestFitnesses[generation] = fittest.getFitnessValue();
			bestPerformances[generation] = bestPerforming.getPerformanceValue();
			
			int numSpecies = genotype.getSpecies().size();
			int minSpeciesSize = Integer.MAX_VALUE;
			int maxSpeciesSize = 0;
			int numSpeciesWithNewPerformance = 0;
			int numNewSpecies = 0;
			int maxSpeciesAge = 0;
			int minSpeciesAge = Integer.MAX_VALUE;
			//double avgBestSpeciesPerformance = 0;
			for (Species species : genotype.getSpecies()) {
				if (species.previousOriginalSize > maxSpeciesSize)
					maxSpeciesSize = species.previousOriginalSize;
				if (species.previousOriginalSize < minSpeciesSize)
					minSpeciesSize = species.previousOriginalSize;

				if (species.getAge() > maxSpeciesAge)
					maxSpeciesAge = species.getAge();
				if (species.getAge() < minSpeciesAge)
					minSpeciesAge = species.getAge();

				//if (species.getBestPerforming() != null)
				//	avgBestSpeciesPerformance += species.getBestPerforming().getPerformanceValue();

				Long speciesKey = new Long(species.getID());
				if (allSpeciesEver.containsKey(speciesKey)) { // if existing species
					if (species.performanceIncreasedLastGen())
						numSpeciesWithNewPerformance++;
				} else {
					numNewSpecies++;
					allSpeciesEver.put(speciesKey, species);
				}
			}
			//avgBestSpeciesPerformance /= numSpecies;
			int numExtinctSpecies = previousSpeciesCount - numSpecies + numNewSpecies;

			if (properties.logFilesEnabled() && properties.getBooleanProperty(LOG_SPECIES_HISTORY_KEY, false)) {
				// write out some info about species history
				StringBuffer output = new StringBuffer(generation + ",\t" + allSpeciesEver.size() + ",\t" + numSpecies + ",\t" + numNewSpecies + ",\t" + numExtinctSpecies);
				for (Species species : allSpeciesEver.values()) {
					output.append(",\t");
					output.append(species.previousOriginalSize);
				}
				output.append("\n");
				speciesInfoWriter.write(output.toString());
				speciesInfoWriter.flush();
			}
			
			
			if (properties.logFilesEnabled()) {
				logChamp(bestPerforming, false, "");
				//logChamp(fittest);
			}
			
			fireEvent(new AHNIEvent(AHNIEvent.Type.GENERATION_END, this, this));
			
			double duration = (System.currentTimeMillis() - start) / 1000d;
			if (avgGenTime == 0)
				avgGenTime = duration;
			else
				avgGenTime = avgGenTime * 0.9 + duration * 0.1;
			cumulativeDurationBetweenLogging += duration;
			
			if (generation % logPerGenerations == 0) {
				int objectiveCount = fittest.getFitnessValues().length;
				int avgSize = 0;
				int maxSize = 0;
				int minSize = Integer.MAX_VALUE;
				for (Chromosome c : genotype.getChromosomes()) {
					avgSize += c.size();
					if (c.size() > maxSize) maxSize = c.size();
					if (c.size() < minSize) minSize = c.size();
				}
				avgSize /= genotype.getChromosomes().size();
				
				double speciationCompatThreshold = genotype.getParameters().getSpeciationThreshold();

				long memTotal = Math.round(runtime.totalMemory() / 1048576);
				long memFree = Math.round(runtime.freeMemory() / 1048576);
				long memUsed = memTotal - memFree;

				int eta = (int) Math.round(avgGenTime * (numEvolutions - generation));

				StringBuilder m = new StringBuilder();
				if (generation % (logPerGenerations * 20) == 0) {
					String fitLabels = "";
					for (String label : bulkFitnessFunc.getObjectiveLabels()) {
						fitLabels += label.substring(0, Math.min(label.length(), 6)) + "\t";
					}
					
					String perfLabels = "";
					for (Map.Entry<String, Double> pv : fittest.getAllPerformanceValues().entrySet()) {
						String key = pv.getKey().replaceFirst("^\\d*", "");
						perfLabels += key.substring(0, Math.min(key.length(), 6)) + "\t";
					}
					
					
					m.append("Gen\tFittest  \t");
					if ((selector.changesOverallFitness() || objectiveCount > 1))
						m.append("OvFtns\t");
					m.append(fitLabels);
					m.append(perfLabels);
					
					m.append("BestPrf  \t");
					if ((selector.changesOverallFitness() || objectiveCount > 1))
						m.append("OvFtns\t");
					m.append(fitLabels);
					m.append(perfLabels);
					
					m.append("ZPC\tZFC\tSC\tNS\tES\tSCT\tSS\tSA    \tSNBP\tGS      \tTime\tETA      \tMem");
					
					logger.info(m);
					m = new StringBuilder();
				}
				
				m.append(generation + " \t");
				
				m.append(nfi9.format(fittest.getId()) + "\t");
				if (selector.changesOverallFitness() || objectiveCount > 1)
					m.append(nf4.format(fittest.getFitnessValue()) + "\t");
				m.append(ArrayUtil.toString(fittest.getFitnessValues(), "\t", nf4) + "\t");
				for (Map.Entry<String, Double> pv : fittest.getAllPerformanceValues().entrySet())
					m.append(nf4.format(pv.getValue()) + "\t");
				
				m.append(nfi9.format(bestPerforming.getId()) + "\t");
				if (selector.changesOverallFitness() || objectiveCount > 1)
					m.append(nf4.format(bestPerforming.getFitnessValue()) + "\t");
				m.append(ArrayUtil.toString(bestPerforming.getFitnessValues(), "\t", nf4) + "\t");
				for (Map.Entry<String, Double> pv : bestPerforming.getAllPerformanceValues().entrySet())
					m.append(nf4.format(pv.getValue()) + "\t");
				
				m.append(genotype.getNumberOfChromosomesWithZeroPerformanceFromLastGen() + "\t" + genotype.getNumberOfChromosomesWithZeroFitnessFromLastGen() + "\t");
				//m.append(nf4.format(avgBestSpeciesPerformance) + "\t");
				m.append(numSpecies + "\t" + numNewSpecies + "\t" + numExtinctSpecies + "\t" + nf1.format(speciationCompatThreshold) + "\t" + minSpeciesSize + "/" + maxSpeciesSize + "\t" + minSpeciesAge + "/" + maxSpeciesAge + "\t" + numSpeciesWithNewPerformance + "\t");
				m.append(minSize + "/" + avgSize + "/" + maxSize + "\t");
				m.append(nf3.format(cumulativeDurationBetweenLogging / logPerGenerations) + "\t" + Misc.formatTimeInterval(eta) + "\t" + memUsed + "MB");
				
				logger.info(m);
				cumulativeDurationBetweenLogging = 0;
			}
		}
		
		fireEvent(new AHNIEvent(AHNIEvent.Type.RUN_END, this, this));

		if (speciesInfoWriter != null) {
			speciesInfoWriter.close();
		}

		// if evolution was terminated before the max number of gens was
		// performed (eg because solution was found sooner)
		if (generation != numEvolutions) {
			// fill in rest of array with last fitness and performance values (for stats generation later)
			double lastPerf = bestPerformances[generation - 1];
			double lastFitness = bestFitnesses[generation - 1];
			for (int i = generation; i < numEvolutions; i++) {
				bestPerformances[i] = lastPerf;
				bestFitnesses[i] = lastFitness;
			}
		}

		// run finish
		config.getEventManager().fireGeneticEvent(new GeneticEvent(GeneticEvent.RUN_COMPLETED_EVENT, genotype));
		
		logConclusion(generationOfFirstSolution, bestPerforming);
		
		bulkFitnessFunc.evolutionFinished(this);
		bulkFitnessFunc.dispose();

		return bestPerformances;
	}

	/**
	 * Returns an array containing the fittest Chromosome from each generation.
	 */
	public Chromosome[] getFittestChromosomesForEachGen() {
		return fittestChromosomes;
	}

	/**
	 * Returns an array containing the best performing Chromosome from each generation. This may be equivalent to the
	 * fittest Chromosomes depending on whether the fitness function defines performance independently.
	 */
	public Chromosome[] getBesttPerformingChromosomesForEachGen() {
		return bestPerformingChromosomes;
	}

	/**
	 * Returns an array of the highest fitness achieved, as a fraction of the maximum possible fitness, in each
	 * generation. This is maintained as well as the fittest Chromosomes because the fitness achieved can vary when the
	 * fitness function is stochastic.
	 */
	public double[] getBestFitness() {
		return bestFitnesses;
	}

	/**
	 * Returns an array of the highest performance achieved in each generation. This is maintained as well as best
	 * performing Chromosomes because the performance achieved can vary when the fitness function is stochastic.
	 */
	public double[] getBestPerformance() {
		return bestPerformances;
	}

	/**
	 * Log summary data of run including generation in which the first solution occurred, and the champion of the final
	 * generation.
	 * 
	 * @param generationOfFirstSolution
	 * @param champ
	 */
	private void logConclusion(int generationOfFirstSolution, Chromosome champ) {		
		if (!properties.containsKey(HyperNEATConfiguration.OUTPUT_DIR_KEY)) {
			// If we're not logging to separate files, output the final champ to the console.
			Map<String, Object> transcribeOptions = new HashMap<String, Object>();
			transcribeOptions.put("recordCoordinates", Boolean.TRUE);
			Transcriber<? extends Activator> transcriber = (Transcriber<? extends Activator>) properties.singletonObjectProperty(ActivatorTranscriber.TRANSCRIBER_KEY);
			try {
				Activator substrate = (transcriber instanceof TranscriberAdaptor) ? ((TranscriberAdaptor) transcriber).transcribe(champ, null, transcribeOptions) : transcriber.transcribe(champ, null);
				HyperNEATTranscriber.CPPN cppn = (transcriber instanceof HyperNEATTranscriber) ? ((HyperNEATTranscriber) transcriber).getCPPN(champ) : null;
				if (substrate == null) {
					logger.warn("Champ substrate is null, which probably means it's been classified as a dud by the transcriber (e.g. perhaps because there are no connections from input to output.");
				} else {
					logger.info("\nString representation of substrate:\n" + substrate + "\nEnd string representation of substrate.");
					if (cppn != null) {
						logger.info("\nString representation of CPPN:\n" + cppn + "\nEnd string representation of CPPN.");
					}
					logger.info("\nString representation of chromosome:\n" + champ.getMaterial().toXML() + "\nEnd string representation of chromosome.");
				}
			} catch (TranscriberException e) {
				System.err.println("Error transcribing best performing individual.");
				e.printStackTrace();
			}
		}
		else {
			try {
				// Write out all the properties to a file to make sure we can refer back to them later or re-run the 
				// experiment exactly (including random seed).
				BufferedWriter propsWriter = new BufferedWriter(new FileWriter(properties.getProperty(HyperNEATConfiguration.OUTPUT_DIR_KEY) + properties.getProperty(HyperNEATConfiguration.OUTPUT_PREFIX_KEY, "") + "run.properties"));
				propsWriter.write("# " + (new Date()).toString() + "\n");
				SortedSet<String> propNames = new TreeSet<String>(properties.stringPropertyNames());
				for (String key : propNames) {
					propsWriter.write(key + "=" + properties.getProperty(key) + "\n");
				}
				if (!properties.containsKey("random.seed")) {
					propsWriter.write("random.seed=" + ((Randomizer) properties.singletonObjectProperty(Randomizer.class)).getSeed() + "\n");
				}
				propsWriter.close();
			} catch (IOException e) {
				System.err.println("Error saving run properties to file.");
				e.printStackTrace();
			}
		}
		
		logger.info("    Generation of first solution: " + generationOfFirstSolution);
		logger.info("    Final species compatability threshold: " + config.getSpeciationParms().getSpeciationThreshold());
		
		DecimalFormat nf3 = new DecimalFormat("0.000");
		logger.info("Best performance for this run (average per 10 gens): ");
		int i;
		String bpAvg = "";
		for (int generation = 0; generation < numEvolutions;) {
			double v = 0;
			for (i = 0; i < 10 && generation < numEvolutions; i++, generation++)
				v += bestPerformances[generation];
			v /= i;
			bpAvg += nf3.format(v) + ", ";
		}
		logger.info(bpAvg);
		logger.info("Best fitness for this run (average per 10 gens): ");
		String bfAvg = "";
		for (int generation = 0; generation < numEvolutions;) {
			double v = 0;
			for (i = 0; i < 10 && generation < numEvolutions; i++, generation++)
				v += bestFitnesses[generation];
			v /= i;
			bfAvg += nf3.format(v) + ", ";
		}
		logger.info(bfAvg);
	}

	public void logChamp(Chromosome champ, boolean force, String label) {
		boolean finished = evolutionFinished();
		boolean finishedOrForce = force || evolutionFinished();
		boolean logString = (finishedOrForce && logChampToString >= 0) || (logChampToString > 0 && generation % logChampToString == 0);
		boolean logImage = (finishedOrForce && logChampToImage >= 0) || (logChampToImage > 0 && generation % logChampToImage == 0);
		String msg = "best performing substrate from " + (finishedOrForce ? "final generation" : "from generation " + generation);
		if (logString || logImage) {
			try {
				Map<String, Object> transcribeOptions = new HashMap<String, Object>();
				transcribeOptions.put("recordCoordinates", Boolean.TRUE);
				
				Transcriber<? extends Activator> transcriber = (Transcriber<? extends Activator>) properties.singletonObjectProperty(ActivatorTranscriber.TRANSCRIBER_KEY);
				
				Activator substrate = (transcriber instanceof TranscriberAdaptor) ? ((TranscriberAdaptor) transcriber).transcribe(champ, null, transcribeOptions) : transcriber.transcribe(champ, null);
				HyperNEATTranscriber.CPPN cppn = (transcriber instanceof HyperNEATTranscriber) ? ((HyperNEATTranscriber) transcriber).getCPPN(champ) : null;
				
				if (substrate == null) {
					logger.warn("Champ substrate is null, which probably means it's been classified as a dud by the transcriber (e.g. perhaps because there are no connections from input to output.");
				} else {
					String baseFileName = properties.getProperty(HyperNEATConfiguration.OUTPUT_DIR_KEY) + properties.getProperty(HyperNEATConfiguration.OUTPUT_PREFIX_KEY, "") + label + "best_performing-" + (finished ? "final" : generation) + "-" + champ.getId();
					if (logString) {
						BufferedWriter outputfile = new BufferedWriter(new FileWriter(baseFileName + ".txt"));
						outputfile.write("String representation of " + msg + ":\n" + substrate);
						if (cppn != null) {
							outputfile.write("\n\n\nString representation of CPPN:\n" + cppn);
						}
						outputfile.write("\n\n\nString representation of Chromosome:\n" + champ.getMaterial().toXML());
						outputfile.close();
					}

					if (logImage) {
						BufferedImage image = new BufferedImage(1200, 1200, BufferedImage.TYPE_3BYTE_BGR);
						Graphics2D g = image.createGraphics();
						g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						boolean success = substrate.render(g, image.getWidth(), image.getHeight(), 30);
						if (success) {
							File outputfile = new File(baseFileName + ".png");
							ImageIO.write(image, "png", outputfile);
							logger.info("Rendered " + msg + " to " + outputfile);
						}
					}
					
					if (bulkFitnessFunc instanceof AHNIFitnessFunction) {
						// Record original performance and fitness values
						Map<String, Double> origPerformanceValues = new HashMap<String, Double>(champ.getAllPerformanceValues());
						double origFitness = champ.getFitnessValue();
						double[] origFitnesses = new double[champ.getFitnessValues().length];
						System.arraycopy(champ.getFitnessValues(), 0, origFitnesses, 0, origFitnesses.length);
						
						((AHNIFitnessFunction) bulkFitnessFunc).evaluate(champ, substrate, baseFileName + "-evaluation", logString, logImage);
						if (((AHNIFitnessFunction) bulkFitnessFunc).evaluateGeneralisation(champ, substrate, baseFileName + "-evaluation-generalisation", logString, logImage)) {
							String genRes = "Generalisation results:";
							for (int i = 0; i < bulkFitnessFunc.getObjectiveLabels().length; i++) {
								genRes += "\n\t" + bulkFitnessFunc.getObjectiveLabels()[i] + ": " + nf4.format(champ.getFitnessValue(i));
							}
							for (Map.Entry<String, Double> pv : champ.getAllPerformanceValues().entrySet()) {
								genRes += "\n\t" + pv.getKey().replaceFirst("^\\d*", "") + ": " + nf4.format(pv.getValue());
							}
							logger.info(genRes);
						}
						
						champ.resetPerformanceValues();
						for (Map.Entry<String, Double> e : origPerformanceValues.entrySet()) {
							champ.setPerformanceValue(e.getKey(), e.getValue());
						}
						if (!Double.isNaN(origFitness)) champ.setFitnessValue(origFitness);
						System.arraycopy(origFitnesses, 0, champ.getFitnessValues(), 0, origFitnesses.length);
					}
				}
			} catch (TranscriberException e) {
				System.err.println("Error transcribing best performing individual.");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Error saving image of best performing network.");
				e.printStackTrace();
			}
		}
	}
	

	/**
	 * @return The fittest Chromosome from the last generation.
	 */
	public Chromosome getFittestFromLastGen() {
		return fittest;
	}

	/**
	 * @return The best performing Chromosome from the last generation. This may be equivalent to the fittest Chromosome
	 *         depending on whether the fitness function defines performance independently.
	 */
	public Chromosome getBestPerformingFromLastGen() {
		return bestPerforming;
	}

	/**
	 * Fitness of given Chromosome in proportion to maximum possible fitness.
	 * 
	 * @return maximum fitness value
	 */
	public double getProportionalFitness(Chromosome c) {
		return (c == null) ? 0 : c.getFitnessValue();
	}

	/**
	 * @return target fitness value, 0 ... 1
	 */
	public double getTargetPerformance() {
		return targetPerformance;
	}

	public void dispose() {
		bulkFitnessFunc.dispose();
	}

	/**
	 * Provides a translation from and passes through {@link org.jgapcustomised.event.GeneticEvent}s to
	 * {@link com.ojcoleman.ahni.event.AHNIEvent}s via {@link com.ojcoleman.ahni.event.AHNIEventListener}s.
	 */
	@Override
	public void geneticEventFired(GeneticEvent event) {
		String e = event.getEventName();
		if (e.equals(GeneticEvent.GENOTYPE_START_EVALUATION_EVENT)) {
			fireEvent(new AHNIEvent(AHNIEvent.Type.EVALUATION_START, this, this));
		}
		else if (e.equals(GeneticEvent.GENOTYPE_EVALUATED_EVENT)) {
			fireEvent(new AHNIEvent(AHNIEvent.Type.EVALUATION_END, this, this));
		}
		else if (e.equals(GeneticEvent.GENOTYPE_START_GENETIC_OPERATORS_EVENT)) {
			fireEvent(new AHNIEvent(AHNIEvent.Type.REGENERATE_POPULATION_START, this, this));
		}
		else if (e.equals(GeneticEvent.GENOTYPE_FINISH_GENETIC_OPERATORS_EVENT)) {
			fireEvent(new AHNIEvent(AHNIEvent.Type.REGENERATE_POPULATION_END, this, this));
		}
	}

	/**
	 * Returns the current generation number.
	 */
	public int getGeneration() {
		return generation;
	}

	/**
	 * Sets the current generation number, used by {@link com.ojcoleman.ahni.evaluation.Minion}.
	 */
	public void setGeneration(int gen) {
		generation = gen;
	}

	/**
	 * Returns true iff evolution has finished or iff the current generation is the last one.
	 */
	public boolean evolutionFinished() {
		return generation >= numEvolutions - 1 || bulkFitnessFunc.endRun();
	}
}
