package dk.itu.ejuuragr.fitness;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;

import com.anji.integration.ActivatorTranscriber;
import com.anji.integration.TranscriberException;
import com.anji.util.Configurable;
import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.Simulator;
import dk.itu.ejuuragr.turing.TuringController;

/**
 * The FitnessFunction class required by ANJI to evaluate
 * a generation of ANNs. This class will use the properties
 * file to load the chosen Controller and Simulator via
 * reflection and get our various properties for the
 * settings. The actual evaluations can be done multithreaded
 * (through setting a specific property).
 * 
 * @author Emil
 *
 */
public class FitnessEvaluator implements BulkFitnessFunction, Configurable {
	private static final long serialVersionUID = 1L;

	ActivatorTranscriber activatorFactory;
	private TuringController[] controllers;

	private int generation;
	private int newSeedAfter = -1;
	private int cores;
	private boolean threading;
	private boolean threadPooling;
	ExecutorService threadPool;
	private Controller cachedController;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	final public void evaluate(List arg0) {
		final List<Chromosome> list = (List<Chromosome>) arg0;

		if (threading) {
			int perThread = list.size() / cores;
			final CountDownLatch latch = new CountDownLatch(cores);

			for (int i = 0; i < cores; i++) {
				if (newSeedAfter > 0)
					controllers[i].getSimulator().setRandomOffset(generation / newSeedAfter);

				final int finalI = i;
				final int start = i * perThread;
				final int end = i + 1 == cores ? list.size() : (i + 1) * perThread;

				Thread th = new Thread() {
					@Override
					public void run() {
						handleSubset(list, start, end, finalI);
						latch.countDown();
					}
				};

				th.start();
			}

			try {
				latch.await(); // Wait for countdown
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ArrayIndexOutOfBoundsException e){
				e.printStackTrace();
				throw e;
			}
		} else if (threadPooling){
			final CountDownLatch latch = new CountDownLatch(list.size());
			for (Chromosome chrom : list){
				final Chromosome ch = chrom;
				threadPool.execute(new Runnable() {
					@Override
					public void run() {
						Controller controller = loadController(cachedProps);
						if (newSeedAfter > 0)
							controller.getSimulator().setRandomOffset(generation / newSeedAfter);
						double score;
						try {
							score = controller.evaluate(activatorFactory.newActivator(ch));
							ch.setFitnessValue((int)score);
							latch.countDown();
						} catch (TranscriberException e) {
							throw new RuntimeException(e);
						}
					}
				});
			};
			try {
				latch.await(); // Wait for countdown
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else {
			handleSubset(list, 0, list.size(), 0);
		}

		generation++;
	}

	private void handleSubset(List<Chromosome> list, int start, int end,
			int myNumber) {
		for (int j = start; j < end; j++) {
			try {
				double score = controllers[myNumber].evaluate(activatorFactory
						.newActivator(list.get(j)));
				list.get(j).setFitnessValue((int)score);
				// System.out.printf("Thread %d: Finished Chromosome %d\n",finalI,j);

			} catch (TranscriberException e) {
				e.printStackTrace();
			} catch (ArrayIndexOutOfBoundsException e){
				e.printStackTrace();
				throw e;
			}
		}
	}

	
	
	@Override
	public int getMaxFitnessValue() {
		int result;
		if (threadPooling){
			result = cachedController.getMaxScore();
		}
		else {
			result = controllers[0].getMaxScore();
		}
		return result;
	}

	Properties cachedProps;
	@Override
	public void init(Properties properties) {
		// Load properties
		activatorFactory = (ActivatorTranscriber) properties
				.singletonObjectProperty(ActivatorTranscriber.class);
		newSeedAfter = properties
				.getIntProperty("simulate.generations.identical", -1);
		threading = properties.getBooleanProperty("threading", false);
		threadPooling = properties.getBooleanProperty("thread.pooling", false);
		
		if (threading && threadPooling){
			throw new RuntimeException("Cannot have both threading and thread pooling!");
		}

		if (threading) {
			// Prepare for multi-threading
			cores = threading ? Runtime.getRuntime().availableProcessors() : 1;
			controllers = new TuringController[cores];
		} else if (threadPooling){
			threadPool = Executors.newCachedThreadPool();
			cachedController = loadController(properties);
		} else {
			cores = 1;
			controllers = new TuringController[1];
		}
		// Initialize
		generation = 0;
		cachedProps = properties;

		for (int i = 0; i < cores; i++) {
			controllers[i] = FitnessEvaluator.loadController(properties);
		}
	}
	
	public static TuringController loadController(Properties props) {
		Simulator simulator = (Simulator) Utilities.instantiateObject(
				props.getProperty("simulator.class"),
				new Object[] { props }, null);
		TuringController result = new TuringController(props,simulator);
		simulator.setController(result);
		simulator.reset();
		simulator.restart();
		result.reset();
		return result;
	}
}
