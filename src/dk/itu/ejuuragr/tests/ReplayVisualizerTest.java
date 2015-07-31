package dk.itu.ejuuragr.tests;

import java.util.ArrayList;
import java.util.List;

import dk.itu.ejuuragr.graph.StaticReplayVisualizer;
import dk.itu.ejuuragr.replay.TimeStep;
import dk.itu.ejuuragr.turing.GravesTuringMachine.HeadTimeStep;
import dk.itu.ejuuragr.turing.GravesTuringMachine.GravesTuringMachineTimeStep;

public class ReplayVisualizerTest {

	/*
	public void testFocused(){
		List<TimeStep> steps = new ArrayList<TimeStep>();
		
		TimeStep step;
		TuringTimeStep tstep;
		
		step = new TimeStep();
		tstep = new TuringTimeStep();
		tstep.getWriteHeads().add(new HeadTimeStep(new double[]{1,0,0,0,0}, new double[]{0}));
		step.setTuringStep(tstep);
		steps.add(step);
		
		step = new TimeStep();
		tstep = new TuringTimeStep();
		tstep.getWriteHeads().add(new HeadTimeStep(new double[]{0,1,0,0,0}, new double[]{0}));
		step.setTuringStep(tstep);
		steps.add(step);
		
		step = new TimeStep();
		tstep = new TuringTimeStep();
		tstep.getWriteHeads().add(new HeadTimeStep(new double[]{0,0,1,0,0}, new double[]{0}));
		step.setTuringStep(tstep);
		steps.add(step);

		
		ReplayVisualizer viz = new ReplayVisualizer();
		viz.show(steps);
	}
	
	public void testBlurry(){
		List<TimeStep> steps = new ArrayList<TimeStep>();
		
		TimeStep step;
		TuringTimeStep tstep;
		
		step = new TimeStep();
		tstep = new TuringTimeStep();
		tstep.getWriteHeads().add(new HeadTimeStep(new double[]{.9,.1,0,0,0}, new double[]{0}));
		tstep.getReadHeads().add(new HeadTimeStep(new double[]{.1,.9,0,0,0}, new double[]{1.0}));
		step.setTuringStep(tstep);
		steps.add(step);
		
		step = new TimeStep();
		tstep = new TuringTimeStep();
		tstep.getWriteHeads().add(new HeadTimeStep(new double[]{.8,.2,0,0,0}, new double[]{0.1}));
		tstep.getReadHeads().add(new HeadTimeStep(new double[]{.2,.8,0,0,0}, new double[]{.9}));
		step.setTuringStep(tstep);
		steps.add(step);
		
		step = new TimeStep();
		tstep = new TuringTimeStep();
		tstep.getWriteHeads().add(new HeadTimeStep(new double[]{.7,.3,0,0,0}, new double[]{0.2}));
		tstep.getReadHeads().add(new HeadTimeStep(new double[]{.3,.7,0,0,0}, new double[]{.8}));
		step.setTuringStep(tstep);
		steps.add(step);

		step = new TimeStep();
		tstep = new TuringTimeStep();
		tstep.getWriteHeads().add(new HeadTimeStep(new double[]{.6,.4,0,0,0}, new double[]{0.3}));
		tstep.getReadHeads().add(new HeadTimeStep(new double[]{.4,.6,0,0,0}, new double[]{.7}));
		step.setTuringStep(tstep);
		steps.add(step);
		
		step = new TimeStep();
		tstep = new TuringTimeStep();
		tstep.getWriteHeads().add(new HeadTimeStep(new double[]{.5,.5,0,0,0}, new double[]{0.4}));
		tstep.getReadHeads().add(new HeadTimeStep(new double[]{.5,.5,0,0,0}, new double[]{.6}));
		step.setTuringStep(tstep);
		steps.add(step);

		ReplayVisualizer viz = new ReplayVisualizer();
		viz.show(steps);
	}
	
	
	public static void main(String[] args) {
		ReplayVisualizerTest tester = new ReplayVisualizerTest();
		//tester.testFocused();
		tester.testBlurry();
	}
	*/
}
