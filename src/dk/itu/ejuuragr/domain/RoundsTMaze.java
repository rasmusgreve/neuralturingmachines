package dk.itu.ejuuragr.domain;

import java.util.Arrays;

import com.anji.util.Properties;

public class RoundsTMaze extends TMaze {
	
	private static final boolean DEBUG = false; // True if it should print scores for each round

	private final boolean SWAP_FIX;
	private double swapFraction; // The center fraction of the stepLength
	private int rounds = -1; // The number of rounds in the test
	private int[] switchSpots; // The spot to switch goal at
	private int swapCount;
	private int swapRounds;
	
	private int curRound; // The current round number
	private int totalScore; // The accumulated score over all rounds

	private int goals[]; // for the toString method

	public RoundsTMaze(Properties props) {
		super(props);
		int roundsPerPer = props.getIntProperty("simulator.tmaze.rounds", 10);
		this.swapFraction = props.getDoubleProperty("simulator.tmaze.swap.fraction", 0.3);
		this.swapCount = props.getIntProperty("simulator.tmaze.swap.swapcount",1);
		this.SWAP_FIX = props.getBooleanProperty("simulator.tmaze.swapfix", false);
		
		int pairGoals = this.getMap().getOfType(MAP_TYPE.goal).size() / 2;
		this.rounds = roundsPerPer * (swapCount+1) * pairGoals;
		this.swapRounds = (int) (this.swapFraction * roundsPerPer); // For each swap
		
//		System.out.println("Rounds: "+this.rounds);
//		System.out.println("Swap Rounds: "+this.swapRounds);
	}

	@Override
	public void restart() {
		if(DEBUG) System.out.println("---------------------");
		super.restart();
		super.swapGoal(true); // select new goal randomly
		this.goals = new int[swapCount+1];
		this.goals[0] = getGoalId(goal);
		curRound = 0;
		totalScore = 0;
		
		switchSpots = new int[swapCount];
		for (int i = 0; i < swapCount; i++){
			//int swapSize = (int)(rounds * swapFraction);
			int rawPoint = (int)((rounds/(swapCount+1.0))*(i+1));
			int fuzzedPoint = rawPoint + getRandom().nextInt(swapRounds+1);
			if(SWAP_FIX)
				fuzzedPoint -= swapRounds / 2;
			switchSpots[i] = fuzzedPoint;
		}
//		int swapArea = (int)(rounds * swapFraction); // The middle X rounds it can switch
//		this.switchSpot = (rounds - swapArea) / 2 + getRandom().nextInt(swapArea+1);
	}
	
	private boolean isResetting = false;

	private int swapRound(int round){
		for (int index = 0; index < switchSpots.length; index++) {
			if (round == switchSpots[index])
				return index;
		}
		return -1;
	}
	
	@Override
	public double[] performAction(double[] action) {
		if (isResetting){
			isResetting = false;
			super.restart();
			curRound++;
			
			int swapRound = swapRound(curRound);
			if(swapRound > -1) {
				super.swapGoal(false); // switch goal to another of the options
				goals[swapRound+1] = getGoalId(goal);
			}
			return super.getInitialObservation();
		}
		double[] superResult = super.performAction(action);
		
		if(super.isTerminated()){ // Round over
			if(DEBUG) {
				System.out.printf("Round %d: %d (G%s) step=%02d %s",curRound,super.getCurrentScore(),super.getGoalId(super.getPositionTile()),getStep(),swapRound(curRound) > -1 ? "~" : "");
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
		sb.append("TMaze [");
		for(int i = 0; i < goals.length; i++ ) {
			sb.append(" h");
			sb.append(i+1);
			sb.append("=");
			sb.append(this.goals[i]);
			if(i > 0) {
				sb.append(" (");
				sb.append(this.switchSpots[i-1]);
				sb.append(")");
			}
			if(i < goals.length - 1) {
				sb.append(",");
			}

		}
		sb.append("]");
		
		return sb.toString();
	}
}
