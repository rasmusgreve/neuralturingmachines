package dk.itu.ejuuragr.replay;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.jgap.Chromosome;
import org.jgapcustomised.ChromosomeMaterial;

import com.anji.integration.Activator;
import com.anji.integration.ActivatorTranscriber;
import com.anji.persistence.FilePersistence;
import com.anji.util.DummyConfiguration;
import com.anji.util.Properties;
import com.ojcoleman.ahni.transcriber.HyperNEATTranscriberBain;

import dk.itu.ejuuragr.domain.Simulator;
import dk.itu.ejuuragr.domain.tmaze.TMaze;
import dk.itu.ejuuragr.fitness.Controller;
import dk.itu.ejuuragr.fitness.Utilities;
import dk.itu.ejuuragr.fitness.Utilities.ActivatorProxy;
import dk.itu.ejuuragr.graph.TMazeStepReplayVisualizer;
import dk.itu.ejuuragr.graph.TMazeVisualizer;
import dk.itu.ejuuragr.replay.StepSimulator.Stepper;
import dk.itu.ejuuragr.turing.TuringController;

/**
 * The class responsible for Replaying an activation
 * of a chromosome in its domain, to visualize how
 * it performs.
 * 
 * @author Rasmus
 *
 */
public class Replay {

	private static com.ojcoleman.ahni.hyperneat.Properties convertProps(Properties props){
		com.ojcoleman.ahni.hyperneat.Properties anjiProps = new com.ojcoleman.ahni.hyperneat.Properties();
		Iterator<Entry<Object, Object>> ite = props.entrySet().iterator();
		while(ite.hasNext()){
			Entry<Object, Object> item = ite.next();
			anjiProps.put(item.getKey(), item.getValue());
		}
		return anjiProps;
	}
	
	
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
		
		String chromFile;
		if (args.length < 2){
			chromFile = prompt("Chromosome ID: ");
		}
		else
		{
			chromFile = args[1];
		}
		
		//Load activator
		Activator activator;
		
		if (new File("db/chromosome/"+chromFile+".ahni.xml").exists()){
			String seedStr = IOUtils.toString(new FileInputStream("db/chromosome/"+chromFile+".ahni.xml"), Charset.defaultCharset());
			ChromosomeMaterial seedMaterial = ChromosomeMaterial.fromXML(seedStr);
			
			HyperNEATTranscriberBain trans = new HyperNEATTranscriberBain(convertProps(props));
			//com.anji_ahni.integration.Transcriber transcriber = (com.anji_ahni.integration.Transcriber) props.singletonObjectProperty("ann.transcriber");
			org.jgapcustomised.Chromosome chrom = new org.jgapcustomised.Chromosome(seedMaterial, 10L, 1, 1);
			activator = new ActivatorProxy(trans.transcribe(chrom, null));
			
		}
		else
		{
			Chromosome chrom = loadChromosome(chromFile, props);
			
			//Setup activator
			ActivatorTranscriber activatorFactory = (ActivatorTranscriber) props.singletonObjectProperty(ActivatorTranscriber.class);
			activator = activatorFactory.newActivator(chrom);
		}
	
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
					while (true){
						if (mazeViz.getDoProgressAndReset())
							break;
					}
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
//			new StaticMemoryFocusVisualizer(recording).show();
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
