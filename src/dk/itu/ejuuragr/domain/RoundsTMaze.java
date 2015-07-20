package dk.itu.ejuuragr.domain;

import com.anji.util.Properties;

public class RoundsTMaze extends TMaze {
	
	private static final boolean DEBUG = false; // True if it should print scores for each round
	
	private double swapFraction; // The center fraction of the stepLength
	private int rounds = -1; // The number of rounds in the test
	private int switchSpot; // The spot to switch goal at
	
	private int curRound; // The current round number
	private int totalScore; // The accumulated score over all rounds

	public RoundsTMaze(Properties props) {
		super(props);
		this.rounds = props.getIntProperty("simulator.tmaze.rounds");
		this.swapFraction = props.getDoubleProperty("simulator.tmaze.swap.fraction");
	}

	@Override
	public void restart() {
		if(DEBUG) System.out.println("---------------------");
		super.restart();
		super.swapGoal(true); // select new goal randomly
		curRound = 0;
		totalScore = 0;
			
		int swapArea = (int)(rounds * swapFraction); // The middle X rounds it can switch
		this.switchSpot = (rounds - swapArea) / 2 + getRandom().nextInt(swapArea+1);
	}

	@Override
	public double[] performAction(double[] action) {
		double[] superResult = super.performAction(action);
		
		if(super.isTerminated()){ // Round over
			if(DEBUG) {
				System.out.printf("Round %d: %d (G%s) step=%02d %s",curRound,super.getCurrentScore(),super.getGoalId(super.getPositionTile()),getStep(),curRound == switchSpot ? "~" : "");
				System.out.println();
			}
			this.totalScore += super.getCurrentScore();
			
			super.restart();
			curRound++;
			if(curRound == switchSpot)
				super.swapGoal(false); // switch goal to another of the options
		}
		
		return superResult;
	}

	@Override
	public boolean isTerminated() {
		boolean result = curRound == rounds;
		if(DEBUG && result) System.out.println("Rounds done");
		return result;
	}

	@Override
	public int getCurrentScore() {
//		System.out.println(totalScore+" of "+getMaxScore());
		return totalScore;
	}

	@Override
	public int getMaxScore() {
		return super.getMaxScore() * rounds;
	}
}
