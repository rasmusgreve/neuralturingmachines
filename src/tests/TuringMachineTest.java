package tests;

import org.junit.*;

import turing.TuringMachine;
import turing.TuringMachine.HeadVariables;

public class TuringMachineTest {

	@Test
	public void simpleTest(){
		TuringMachine machine = new TuringMachine(100, 1, 1, 1, null);
		
		HeadVariables hv = new HeadVariables();
		
		hv.addWrite(new double[]{0}, new double[]{1}, new double[]{0}, 0, 0, new double[]{0}, 4);
		hv.addRead(new double[]{1}, 1, 0, new double[]{0}, 4);
		
		double[][] result = machine.processInput(hv);
		
		for (int i = 0; i < result.length; i++)
			System.out.println(i + " -> " + result[i][0]);
		
		Assert.assertTrue("Test", true);
	}
	
}
