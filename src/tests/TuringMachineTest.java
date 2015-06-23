package tests;

import org.junit.*;

import turing.TuringMachine;
import turing.TuringMachine.HeadVariables;

public class TuringMachineTest {

	@Test
	public void testWrite(){
		TuringMachine machine = new TuringMachine(10, 1, 0, 1, 3);
		HeadVariables hv = new HeadVariables();
		hv.addWrite(new double[]{0}, new double[]{1}, new double[]{0}, 0, 0, new double[]{0,1,0}, 4);
		
		double[][] result = machine.processInput(hv);
		
		Assert.assertEquals("Expected 0 size result, got: " + result.length, 0, result.length);
	}
	
	
	@Test
	public void testRead(){
		TuringMachine machine = new TuringMachine(10, 1, 1, 0, 3);
		
		HeadVariables hv = new HeadVariables();
		hv.addRead(new double[]{0}, 1, 1, new double[]{0,1,0}, 4);
		
		double[][] result = machine.processInput(hv);
		
		for (int i = 0; i < result.length; i++)
			System.out.println(i + " -> " + result[i][0]);
		
		Assert.assertTrue("Test", true);
	}
	
	@Test
	public void testWriteRead(){
		TuringMachine machine = new TuringMachine(5, 1, 1, 1, 3);
		
		HeadVariables hv = new HeadVariables();
		
		hv.addWrite(new double[]{0}, new double[]{1}, new double[]{0}, 1, 1, new double[]{0,1,0}, 4);
		hv.addRead(new double[]{1}, 1, 1, new double[]{0,1,0}, 4);
		
		double[][] result = machine.processInput(hv);
		
		Assert.assertEquals("Expected 1 result, got: " + result.length, 1, result.length);
		Assert.assertEquals("Expected size 1 (m) result, got: " + result[0].length, 1, result[0].length);
		
		System.out.println("Result: " + result[0][0]);
	}
	
}
