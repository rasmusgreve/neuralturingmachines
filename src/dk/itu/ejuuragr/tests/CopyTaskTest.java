package dk.itu.ejuuragr.tests;

import org.junit.Assert;
import org.junit.Test;

import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.CopyTask;

public class CopyTaskTest {

	
	@Test
	public void testCopyTask(){
		Properties props = new Properties();
		props.setProperty("simulator.copytask.leave.memory", "1");
		props.setProperty("simulator.copytask.length.max", "5");
		props.setProperty("simulator.copytask.length.rule", "fixed");
		props.setProperty("tm.m", "2");
		props.setProperty("random.seed","0");
		
		CopyTask task = new CopyTask(props);
		
		task.reset();
		task.restart();
		
		double[][] CToutSeq = new double[][]{
				{0,1,0},
				{1,0,0},
				{1,0,0},
				{0,0,0},
				{1,0,0},
				{1,0,0},
				{0,0,1},
				{0,0,0},
				{0,0,0},
				{0,0,0},
				{0,0,0},
				{0,0,0},
				{0,0,0}
		};
		
		double[][] ops = new double[][]{
				{0,0,0}, //After initial input (seq1)
				{0,0,0}, //(seq2)
				{0,0,0}, //(seq3)
				{0,0,0}, //(seq4)
				{0,0,0}, //(seq5)
				{0,0,0}, //(delimiter)
				{0,0,0}, //Not expecting anything yet
				CToutSeq[1],
				CToutSeq[2],
				CToutSeq[3],
				CToutSeq[4],
				CToutSeq[5]
		};
		
		double[] obs = task.getInitialObservation();
		Assert.assertArrayEquals(CToutSeq[0], obs, 0.00);
		for (int i = 0; i < ops.length; i++){
			obs = task.performAction(ops[i]);
			Assert.assertArrayEquals(CToutSeq[i+1], obs, 0.00);
		}
		Assert.assertEquals(task.getCurrentScore(), task.getMaxScore());
		
	}
	
}
