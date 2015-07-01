package dk.itu.ejuuragr.domain;

import java.util.Random;

import com.anji.util.Properties;

public class RoundsTMaze extends TMaze {
	
	private double swapFraction; // The center fraction of the stepLength
	private int rounds = -1; // The number of rounds in the test
	private Random rand;
	private int switchSpot;
	
	private int curRound;
	private int totalScore;

	public RoundsTMaze(Properties props) {
		super(props);
		this.rounds = props.getIntProperty("simulator.tmaze.rounds");
		this.swapFraction = props.getDoubleProperty("simulator.tmaze.swap.fraction");
		this.rand = new Random(props.getIntProperty("random.seed"));
		reset();
	}

	@Override
	public void reset() {
		super.reset();
		super.swapGoal(true); // select new goal randomly
		curRound = 0;
			
		int swapArea = (int)(rounds * swapFraction); // The middle X rounds it can switch
		this.switchSpot = (rounds - swapArea) / 2 + rand.nextInt(swapArea+1);
	}

	@Override
	public double[] performAction(double[] action) {
		double[] superResult = super.performAction(action);
		
		if(super.isTerminated()){ // Round over
			this.totalScore = super.getCurrentScore();
			
			super.reset();
			curRound++;
			if(curRound == switchSpot)
				super.swapGoal(false); // switch goal to another of the options
		}
		
		return superResult;
	}

	@Override
	public boolean isTerminated() {
		return curRound == rounds - 1;
	}

	@Override
	public int getCurrentScore() {
//		System.out.println(totalScore);
		return totalScore;
	}

	@Override
	public int getMaxScore() {
		return super.getMaxScore() * rounds;
	}
}
