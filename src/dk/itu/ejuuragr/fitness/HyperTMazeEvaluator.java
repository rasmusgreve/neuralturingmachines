package dk.itu.ejuuragr.fitness;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jgapcustomised.BulkFitnessFunction;
import org.jgapcustomised.Chromosome;

import com.anji.integration.TranscriberException;
import com.ojcoleman.ahni.hyperneat.HyperNEATEvolver;

//TODO: Extend BulkFitnessFunctionMT instead
public class HyperFitnessEvaluator extends BulkFitnessFunction {

	ExecutorService threadPool = Executors.newCachedThreadPool();;
	
	@Override
	public void evaluate(List<Chromosome> subjects) {
//		subjects.get(0).setFitnessValue(a_newFitnessValue, objective);
		
		//NOTES:
		//selector.class defaults to auto which uses the overall fitness of the chromosome set via Chromosome.setFitnessValue(Double);
		
//		final CountDownLatch latch = new CountDownLatch(list.size());
//		for (Chromosome chrom : subjects){
//			final Chromosome ch = chrom;
//			threadPool.execute(new Runnable() {
//				@Override
//				public void run() {
//					Controller controller = loadController(cachedProps);
//					if (newSeedAfter > 0)
//						controller.getSimulator().setRandomOffset(generation / newSeedAfter);
//					double score;
//					try {
//						score = controller.evaluate(activatorFactory.newActivator(ch));
//						ch.setFitnessValue((int)score);
//						latch.countDown();
//					} catch (TranscriberException e) {
//						throw new RuntimeException(e);
//					}
//				}
//			});
//		};
//		try {
//			latch.await(); // Wait for countdown
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		
		
	}

	@Override
	public boolean endRun() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void evolutionFinished(HyperNEATEvolver evolver) {
		//Deprecated - do not implement
	}

}
