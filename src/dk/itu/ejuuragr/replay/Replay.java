package dk.itu.ejuuragr.replay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jgap.Chromosome;

import com.anji.integration.Activator;
import com.anji.integration.ActivatorTranscriber;
import com.anji.persistence.FilePersistence;
import com.anji.util.DummyConfiguration;
import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.Simulator;
import dk.itu.ejuuragr.domain.tmaze.TMaze;
import dk.itu.ejuuragr.fitness.Controller;
import dk.itu.ejuuragr.fitness.Utilities;
import dk.itu.ejuuragr.graph.StaticMemoryFocusVisualizer;
import dk.itu.ejuuragr.graph.TMazeStepReplayVisualizer;
import dk.itu.ejuuragr.graph.TMazeVisualizer;
import dk.itu.ejuuragr.replay.StepSimulator.Stepper;
import dk.itu.ejuuragr.turing.TuringController;

public class Replay {

	public static void main(String[] args) throws Exception {
		if (args.length == 0){
			args = getArgsFromStdIn();
		}
		
		//Setup
		String propsFilename = args.length > 0 ? args[0] : prompt("Properties filename: ");
		if(!propsFilename.endsWith(".properties"))
			propsFilename += ".properties";
		Properties props = new Properties(propsFilename);
		props.setProperty("base.dir", "./db");
		Chromosome chrom = loadChromosome(args.length > 1 ? args[1] : prompt("Chromosome ID: "), props);
		
//		props.setProperty("simulator.tmaze.rounds", "2");
		
		//Setup activator
		ActivatorTranscriber activatorFactory = (ActivatorTranscriber) props.singletonObjectProperty(ActivatorTranscriber.class);
		Activator activator = activatorFactory.newActivator(chrom);
	
		//Initiate simulator and controller from properties to test their types
		Simulator simulator = (Simulator) Utilities.instantiateObject(props.getProperty("simulator.class"),new Object[]{props},null);
		simulator.reset();
		simulator.restart();
		StepSimulator stepSim = new StepSimulator(simulator);
		
		Controller controller = (Controller) Utilities.instantiateObject(props.getProperty("controller.class"),new Object[]{props,stepSim}, new Class<?>[]{Properties.class,Simulator.class});
	
		if (controller instanceof TuringController){
			controller = new TuringControllerRecorder(props, stepSim);
		}
		
		//In the TMaze we want to be able to step through the simulation
		if (simulator instanceof TMaze)
		{
			final TMaze tmaze = (TMaze)simulator;
			final TMazeVisualizer mazeViz = new TMazeVisualizer(tmaze, false);
			final TMazeStepReplayVisualizer memViz = 
					(controller instanceof TuringControllerRecorder) ? 
							new TMazeStepReplayVisualizer(((TuringControllerRecorder)controller).getRecording())
							:null;
			memViz.show();
			stepSim.setStepper(new Stepper(){

				@Override
				public void step() {
					mazeViz.update();
					if (memViz != null){
						memViz.update();
					}
//					while (true){
//						if (mazeViz.getDoProgressAndReset())
//							break;
//					}
				}
				
			});
		}
		
		//Simulator and controller

		double fitness = controller.evaluate(activator);
		System.out.println("FINAL FITNESS: "+fitness + " / " + controller.getMaxScore());
		if (controller instanceof TuringControllerRecorder){
			//new ReplayVisualizer().show(((TuringControllerMemoryVizProxy)controller).getSteps());
			
			Recording<?> recording = ((TuringControllerRecorder)controller).getRecording();
//			new StaticReplayVisualizer(recording).show();
			new StaticMemoryFocusVisualizer(recording).show();
//			new StepReplayVisualizer(recording).show();
//			new ReplayStepVisualizer().show((List<TimeStep<GravesTuringMachineTimeStep>>)timeSteps);
		}
	}
	
	private static String prompt(String string) {
		System.out.println(string);
		try {
			return new BufferedReader(new InputStreamReader(System.in)).readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Chromosome loadChromosome(String id, Properties props){
		FilePersistence db = new FilePersistence();
		db.init(props);
		return db.loadChromosome(id, new DummyConfiguration());
	}
	
	private static String[] getArgsFromStdIn() throws IOException{
		String[] result = new String[2];
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Properties filename: ");
		result[0] = br.readLine();
		System.out.println("Chromosome ID: ");
		result[1] = br.readLine();
		return result;
	}
	
	private static void printActivation(Activator activator, double[] activation){
		System.out.println("Activating with " + arrayString(activation));
		System.out.println("Result: " + arrayString(activator.next(activation)));
	}
	
	private static String arrayString(double[] arr){
		StringBuilder sb = new StringBuilder("[");
		for (double d : arr)
			sb.append(d).append(" ,");
		return sb.substring(0, sb.length()-2) + "]";
	}
	
}
