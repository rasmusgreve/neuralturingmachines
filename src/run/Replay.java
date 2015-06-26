package run;

import org.jgap.Chromosome;

import com.anji.integration.Activator;
import com.anji.integration.ActivatorTranscriber;
import com.anji.neat.NeatActivator;
import com.anji.persistence.FilePersistence;
import com.anji.util.DummyConfiguration;
import com.anji.util.Properties;

public class Replay {

	public static void main(String[] args) throws Exception {

		
		Properties props = new Properties("turingmachine.properties");
		props.setProperty("persistence.base.dir", "./anji_2_01/db");
		props.setProperty("base.dir", "./anji_2_01/db");
		
		FilePersistence db = new FilePersistence();
		db.init(props);
		
		Chromosome chrom = db.loadChromosome( "11253", new DummyConfiguration());
		System.out.println("Size: " + chrom.getAlleles().size());
		
		ActivatorTranscriber activatorFactory = (ActivatorTranscriber) props
				.singletonObjectProperty( ActivatorTranscriber.class );
		
		Activator activator = activatorFactory.newActivator( chrom );
		
		//double[][] result = activator.next(new double[][]{new double[]{0},new double[]{0}});
		double[] result = activator.next(new double[]{0,0});
		/*
		System.out.println("Result:");
		for (double[] r : result){
			for (double d : r)
				System.out.print(d + " ");
			System.out.println();
		}
		*/
		for (double d : result){
			System.out.println(d);
		}
		/*Activator activator = activatorFactory.newActivator( chrom );
		
		
		NeatActivator na = new NeatActivator();
		Properties props = new Properties("turingmachine.properties");
		props.setProperty("persistence.base.dir", "./anji_2_01/db");
		na.init( props );
		na.
		System.out.println( "\n" + na.displayActivation(  "11253") );*/
		
	}
}
