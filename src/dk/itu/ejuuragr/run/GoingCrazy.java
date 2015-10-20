package dk.itu.ejuuragr.run;

import java.io.IOException;
import java.util.Arrays;

import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.tmaze.TMaze;
import dk.itu.ejuuragr.fitness.Utilities;

public class GoingCrazy {

	public static void main(String[] args) throws IOException {

		Properties props = new Properties("tmaze.properties");
		props.setProperty("base.dir", "./db");

		int first = 0, second = 0;
		int firstSecond = -1;
		for (int i = 0; i < 100000; i++) {
			props.setProperty("random.seed", "" + i);
			TMaze simulator = (TMaze) Utilities.instantiateObject(props.getProperty("simulator.class"), new Object[] { props }, null);
			if (firstSecond == -1 && simulator.goal[0] == 1) firstSecond = i;
			if (simulator.goal[0] == 3) first++;
			else second++;
			//System.out.println(Arrays.toString(simulator.goal));
		}
		System.out.println("First: " + first);
		System.out.println("Second: " + second);
		System.out.println("First second: " + firstSecond);
	}

}
