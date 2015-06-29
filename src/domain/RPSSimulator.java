package domain;

import java.util.Random;

import com.anji.util.Properties;

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

	static final int[][] WINNER = new int[][] { {0,-1,1},
												{1,0,-1},
												{-1,1,0}
											  };
	private int chosenAction; // chosen at the beginning
	private int score;
	private int steps;
	private Random random = new Random();
	
	public RPSSimulator(Properties props){
		steps = props.getIntProperty("controller.steps.max");
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
		return new double[]{(curScore + 1) / 2.0}; // 0 is losing, � is tie, 1 is winning.
	}

	@Override
	public int getCurrentScore() {
		return score;
	}

	@Override
	public boolean isTerminated() {
		return score >= steps;
	}

	@Override
	public int getMaxScore() {
		return steps; // if you win all
	}

	@Override
	public void reset() {
		score = 0;
		chosenAction = random.nextInt(3);
	}
}
