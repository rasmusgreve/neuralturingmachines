package dk.itu.ejuuragr.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.anji.util.Properties;

import dk.itu.ejuuragr.fitness.Utilities;
import dk.itu.ejuuragr.turing.MinimalTuringMachine;

public class MinimalTuringMachineTest {

	MinimalTuringMachine tm;
	
	@Before
	public void before(){
		Properties props = new Properties();
		props.setProperty("tm.m", "2");
		props.setProperty("tm.shift.length", "3");
		tm = new MinimalTuringMachine(props);
	}
	
	//Inputs to tm:
		//m * write
		//1 * interpolation
		//1 * content jump
		//(3) * shift
	
	//write
		//jump
		//shift
		//read
	
	@Test
	public void testShift1(){
		double[] tmstim = new double[]{
				0, 1,	//Write
				1,		//Write interpolation
				0,		//Content jump
				1, 0, 0	//Shift
		};
		
		double[] result = tm.processInput(tmstim)[0];
		Assert.assertArrayEquals(new double[]{0, 0}, result, 0.0);
		
		tmstim = new double[]{
				0, 0,	//Write
				0,		//Write interpolation
				0,		//Content jump
				0, 0, 1	//Shift
		};
		
		result = tm.processInput(tmstim)[0];

		Assert.assertArrayEquals(new double[]{0, 1}, result, 0.0);
	}
	
	@Test
	public void testContentBasedJump1(){
		//Write jump target
		double[] tmstim = new double[]{
				0, 1,	//Write
				1,		//Write interpolation
				0,		//Content jump
				0, 0, 1	//Shift
		};
		double[] result = tm.processInput(tmstim)[0];
		
		//Write jump result
		tmstim = new double[]{
				1, 0,	//Write
				1,		//Write interpolation
				0,		//Content jump
				0, 0, 1	//Shift
		};
		result = tm.processInput(tmstim)[0];
		
		//Jump, shift, and read
		tmstim = new double[]{
				0, 1,	//Write
				0,		//Write interpolation
				1,		//Content jump
				0, 0, 1	//Shift
		};
		result = tm.processInput(tmstim)[0];
		System.out.println(Utilities.toString(result));
		Assert.assertArrayEquals(new double[]{1, 0}, result, 0.0);
	}
	
	
	
	
	
}
