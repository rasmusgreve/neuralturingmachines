package dk.itu.ejuuragr.tests;

import org.junit.*;

import dk.itu.ejuuragr.turing.GravesTuringMachine;
import dk.itu.ejuuragr.turing.GravesTuringMachine.HeadVariables;

public class TuringMachineTest {

	@Test
	public void testWrite(){
		GravesTuringMachine machine = new GravesTuringMachine(10, 1, 0, 1, 3);
		HeadVariables hv = new HeadVariables();
		hv.addWrite(new double[]{0}, new double[]{1}, new double[]{0}, 0, 0, new double[]{0,1,0}, 4);
		
		double[][] result = machine.processInput(hv);
		
		Assert.assertEquals("Expected 0 size result, got: " + result.length, 0, result.length);
	}
	
	@Test
	public void testWriteRead1(){
		final double value = 1.0;
		
		GravesTuringMachine machine = new GravesTuringMachine(5, 1, 1, 1, 3);
		HeadVariables hv = new HeadVariables();
		
		hv.addWrite(new double[]{0}, new double[]{value}, new double[]{0}, 1, 0, new double[]{0,1,0}, 4);
		hv.addRead(new double[]{value}, 1, 1, new double[]{0,1,0}, 6);
		
		double[][] result = machine.processInput(hv);
		
		Assert.assertEquals("Expected 1 result, got: " + result.length, 1, result.length);
		Assert.assertEquals("Expected size 1 (m) result, got: " + result[0].length, 1, result[0].length);
		Assert.assertEquals("Expected to read " + value + ", got: " + result[0][0], value, result[0][0], 0.01);
		
		//System.out.println("Result: " + result[0][0]);
	}
	
	@Test
	public void testWriteRead05(){
		final double value = 0.5;
		
		GravesTuringMachine machine = new GravesTuringMachine(5, 1, 1, 1, 3);
		HeadVariables hv = new HeadVariables();
		
		hv.addWrite(new double[]{0}, new double[]{value}, new double[]{0}, 1, 0, new double[]{0,1,0}, 4);
		hv.addRead(new double[]{value}, 1, 1, new double[]{0,1,0}, 6);
		
		double[][] result = machine.processInput(hv);
		
		Assert.assertEquals("Expected 1 result, got: " + result.length, 1, result.length);
		Assert.assertEquals("Expected size 1 (m) result, got: " + result[0].length, 1, result[0].length);
		Assert.assertEquals("Expected to read " + value + ", got: " + result[0][0], value, result[0][0], 0.01);
		
		System.out.println("Result: " + result[0][0]);
	}
	
}
