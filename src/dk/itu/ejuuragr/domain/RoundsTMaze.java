package dk.itu.ejuuragr.domain;

import java.util.Arrays;

import com.anji.util.Properties;

public class RoundsTMaze extends TMaze {
	
	private static final boolean DEBUG = false; // True if it should print scores for each round
	
	private double swapFraction; // The center fraction of the stepLength
	private int rounds = -1; // The number of rounds in the test
	private int[] switchSpots; // The spot to switch goal at
	private int swapCount;
	
	private int curRound; // The current round number
	private int totalScore; // The accumulated score over all rounds

	private int firstGoal = -1; // for the toString method

	public RoundsTMaze(Properties props) {
		super(props);
		this.rounds = props.getIntProperty("simulator.tmaze.rounds", 10);
		this.swapFraction = props.getDoubleProperty("simulator.tmaze.swap.fraction", 0.3);
		this.swapCount = props.getIntProperty("simulator.tmaze.swap.swapcount",1);
	}

	@Override
	public void restart() {
		if(DEBUG) System.out.println("---------------------");
		super.restart();
		super.swapGoal(true); // select new goal randomly
		this.firstGoal = getGoalId(goal);
		curRound = 0;
		totalScore = 0;
		
		switchSpots = new int[swapCount];
		for (int i = 0; i < swapCount; i++){
			int swapSize = (int)(rounds * swapFraction);
			int rawPoint = (int)((rounds/(swapCount+1.0))*(i+1));
			int fuzzedPoint = rawPoint + getRandom().nextInt(swapSize+1);// - swapSize / 2;
			switchSpots[i] = fuzzedPoint;
		}
//		int swapArea = (int)(rounds * swapFraction); // The middle X rounds it can switch
//		this.switchSpot = (rounds - swapArea) / 2 + getRandom().nextInt(swapArea+1);
	}
	
	private boolean isResetting = false;

	private boolean isSwapRound(int round){
		for (int point : switchSpots) if (round == point) return true;
		return false;
	}
	
	@Override
	public double[] performAction(double[] action) {
		if (isResetting){
			isResetting = false;
			super.restart();
			curRound++;
			if(isSwapRound(curRound))
				super.swapGoal(false); // switch goal to another of the options
			return super.getInitialObservation();
		}
		double[] superResult = super.performAction(action);
		
		if(super.isTerminated()){ // Round over
			if(DEBUG) {
				System.out.printf("Round %d: %d (G%s) step=%02d %s",curRound,super.getCurrentScore(),super.getGoalId(super.getPositionTile()),getStep(),isSwapRound(curRound) ? "~" : "");
				System.out.println();
			}
			this.totalScore += super.getCurrentScore();
			isResetting = true;
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
	public double getCurrentScore() {
//		System.out.println(totalScore+" of "+getMaxScore());
		return totalScore;
	}

	@Override
	public int getMaxScore() {
		return super.getMaxScore() * rounds;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("TMaze [h1=");
		sb.append(firstGoal);
		sb.append(" h2=");
		sb.append(this.getGoalId(goal));
		sb.append(" swap=");
		sb.append(Arrays.toString(this.switchSpots));
		sb.append("]");
		
		return sb.toString();
	}
}
