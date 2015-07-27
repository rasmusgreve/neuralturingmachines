package dk.itu.ejuuragr.fitness;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;

import com.anji.integration.ActivatorTranscriber;
import com.anji.integration.TranscriberException;
import com.anji.util.Configurable;
import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.Simulator;

public class FitnessEvaluator implements BulkFitnessFunction, Configurable {
	private static final long serialVersionUID = 1L;

	ActivatorTranscriber activatorFactory;
	private Controller[] controllers;

	private int generation;
	private boolean toOffset = true;
	private int cores;
	private boolean threading;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	final public void evaluate(List arg0) {
		final List<Chromosome> list = (List<Chromosome>) arg0;

		if (threading) {
			int perThread = list.size() / cores;
			final CountDownLatch latch = new CountDownLatch(cores);

			for (int i = 0; i < cores; i++) {
				if (toOffset)
					controllers[i].getSimulator().setRandomOffset(generation);

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
				int score = controllers[myNumber].evaluate(activatorFactory
						.newActivator(list.get(j)));
				list.get(j).setFitnessValue(score);
				// System.out.printf("Thread %d: Finished Chromosome %d\n",finalI,j);

			} catch (TranscriberException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int getMaxFitnessValue() {
		int result = controllers[0].getMaxScore();
		// System.out.println("Max Score: "+result);
		return result;
	}

	@Override
	public void init(Properties properties) {
		// Load properties
		activatorFactory = (ActivatorTranscriber) properties
				.singletonObjectProperty(ActivatorTranscriber.class);
		toOffset = !properties
				.getBooleanProperty("simulate.generations.identical");
		threading = properties.getBooleanProperty("threading");

		// Prepare for multi-threading
		cores = threading ? Runtime.getRuntime().availableProcessors() : 1;
		controllers = new Controller[cores];

		// Initialize
		generation = 0;

		for (int i = 0; i < cores; i++) {
			Simulator simulator = (Simulator) Utilities.instantiateObject(
					properties.getProperty("simulator.class"),
					new Object[] { properties }, null);
			Controller controller = (Controller) Utilities.instantiateObject(
					properties.getProperty("controller.class"), new Object[] {
							properties, simulator }, new Class<?>[] {
							Properties.class, Simulator.class });
			simulator.reset();
			simulator.restart();
			controller.reset();
			controllers[i] = controller;
		}

	}
}
