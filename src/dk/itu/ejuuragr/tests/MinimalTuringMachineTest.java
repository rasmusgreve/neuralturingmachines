package dk.itu.ejuuragr.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.anji.util.Properties;

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
	
	@Test
	public void testShift(){
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
	public void testContentBasedJump(){
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
		Assert.assertArrayEquals(new double[]{1, 0}, result, 0.0);
	}
	
	@Test
	public void testContentBasedJumpLonger(){
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
		
		//Move right
		for (int k = 0; k < 10; k++){
			tmstim = new double[]{
					0, 0,	//Write
					0,		//Write interpolation
					0,		//Content jump
					0, 0, 1	//Shift
			};
			result = tm.processInput(tmstim)[0];
		}
		//Jump, shift, and read
		tmstim = new double[]{
				0, 1,	//Write
				0,		//Write interpolation
				1,		//Content jump
				0, 0, 1	//Shift
		};
		result = tm.processInput(tmstim)[0];
		Assert.assertArrayEquals(new double[]{1, 0}, result, 0.0);
	}
	
	@Test
	public void testCopyTaskSimple(){
		double[][] seq = new double[][]{
				{0,1,0}, //Start
				{1,0,0}, //Data
				{0,0,0}, //Data
				{0,0,0}, //Data
				{1,0,0}, //Data
				{1,0,0}, //Data
				{0,0,1}, //End
				{0,0,0}, //Poll
				{0,0,0}, //Poll
				{0,0,0}, //Poll
				{0,0,0}, //Poll
				{0,0,0}, //Poll
		};
		
		double[] lastRoundRead = new double[]{0,0}; 
		
		for (int round = 0; round < seq.length; round++){
			
			double d = seq[round][0];
			double s = seq[round][1];
			double b = seq[round][2];
			
			lastRoundRead = act(d + lastRoundRead[0],s + b + lastRoundRead[1], 1-b, b, 0, b ,1-b);
			double roundResult = lastRoundRead[0];

			if (round > 6){
				double verify = seq[round-6][0];
				Assert.assertEquals(verify, roundResult, 0.00);
			}
		}
	}

	private double[] act(double d1, double d2, double write, double jump, double shiftLeft, double shiftStay, double shiftRight){
		return tm.processInput(new double[]{
				d1, d2,		//Write
				write,		//Write interpolation
				jump,		//Content jump
				shiftLeft, shiftStay, shiftRight	//Shift
		})[0];
	}
	
	
	
	
	
}
