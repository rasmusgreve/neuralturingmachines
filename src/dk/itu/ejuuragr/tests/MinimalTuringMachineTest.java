package dk.itu.ejuuragr.tests;

import com.anji.util.Properties;

import dk.itu.ejuuragr.fitness.Utilities;
import dk.itu.ejuuragr.turing.MinimalTuringMachine;

public class MinimalTuringMachineTest {

	public static void main(String[] args) {
		Properties props = new Properties();
		props.setProperty("tm.m", "2");
		props.setProperty("tm.shift.length", "3");
		MinimalTuringMachine tm = new MinimalTuringMachine(props);
		
		//Inputs to tm:
		//m * write
		//1 * interpolation
		//(3) * shift
		
		double[] tmstim = new double[]{
				0, 1,	//Write
				1,		//Write interpolation
				0, 0, 1	//Shift
		};
		
		double[] result = tm.processInput(tmstim)[0];
		System.out.println(Utilities.toString(result));
		
		
		tmstim = new double[]{
				0, 0,	//Write
				0,		//Write interpolation
				0, 0, 1	//Shift
		};
		
		result = tm.processInput(tmstim)[0];
		
		System.out.println(Utilities.toString(result));
		
	}
	
	//write
	//jump
	//shift
	//read
	
}
