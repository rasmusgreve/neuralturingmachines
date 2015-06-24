package domain.tmaze;

import java.util.Random;

import domain.Simulator;
import fitness.Utilities;

/**
 * A simpe sample simulator which plays Rock, Paper,
 * Scissors againt the agent. This opponent is predictable
 * (just cycles through the options) so an agent with
 * memory should be able to easily beat it.)
 * @author Emil
 *
 */
public class RPSSimulator implements Simulator {

	static final int MAXSCORE = 10;
	static final int[][] WINNER = new int[][] { {0,-1,1},
												{1,0,-1},
												{-1,1,0}
											  };
	int chosenAction; // chosen at the beginning
	int score;
	
	public RPSSimulator(){
		reset();
	}
	
	@Override
	public int getInputCount() {
		return 3;
	}

	@Override
	public int getOutputCount() {
		return 1;
	}

	@Override
	public double[] getInitialObservation() {
		return new double[1];
	}

	@Override
	public double[] performAction(double[] action) {
		int curScore = WINNER[Utilities.maxPos(action)][chosenAction];
		score += curScore;
		return new double[]{(curScore + 1) / 2.0}; // 0 is losing, ½ is tie, 1 is winning.
	}

	@Override
	public int getCurrentScore() {
		return score;
	}

	@Override
	public boolean isTerminated() {
		return score >= MAXSCORE;
	}

	@Override
	public int getMaxScore() {
		return MAXSCORE;
	}

	@Override
	public void reset() {
		score = 0;
		chosenAction = new Random().nextInt(3);
	}
}
