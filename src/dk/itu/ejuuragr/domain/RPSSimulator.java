package dk.itu.ejuuragr.domain;

import java.util.Arrays;
import java.util.Random;

import com.anji.util.Properties;

import dk.itu.ejuuragr.fitness.Utilities;

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
	static final double SWAP_AREA = 0.3; // The center 30% of the stepLength

	private int score;
	private int stepsTotal;
	private Random rand;
	private String mode;
	
	private int[] sequence;
	private int step; // how far we are in the sequence

	private int sequenceLength;
	
	public RPSSimulator(Properties props){
		stepsTotal = props.getIntProperty("controller.steps.max");
		sequenceLength = props.getIntProperty("simulator.rps.sequence.length");
		mode = props.getProperty("simulator.rps.mode");
		rand = new Random(props.getIntProperty("random.seed"));
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
		int curScore = WINNER[Utilities.maxPos(action)][sequence[step]];
		step = (step + 1) % sequence.length;

		score += curScore;
		return new double[]{(curScore + 1) / 2.0}; // 0 is losing, � is tie, 1 is winning.
	}

	@Override
	public int getCurrentScore() {
		return score;
	}

	@Override
	public boolean isTerminated() {
		return score >= stepsTotal;
	}

	@Override
	public int getMaxScore() {
		return stepsTotal; // if you win all
	}

	@Override
	public void reset() {
		score = 0;
		step = 0;
		
		switch(mode) {
		case "fixed":
			sequence = randomSequence(1);
			sequenceLength = 1;
			break;
		case "sequence":
			sequence = randomSequence(sequenceLength);
			break;
		case "swap":
			sequence = swapSequence();
			sequenceLength = stepsTotal;
		}
		//System.out.println(Arrays.toString(sequence));
	}

	private int[] swapSequence() {
		int[] result = new int[stepsTotal];
		int swapArea = (int)(stepsTotal * SWAP_AREA);
		int switchSpot = (stepsTotal - swapArea) / 2 + rand.nextInt(swapArea+1);
		int first = rand.nextInt(3);
		int second = rand.nextInt(2);
		if(second == first)
			second = 2;
		
		for(int i = 0; i < result.length; i++) {
			result[i] = i < switchSpot ? first : second;
		}
		return result;
	}

	private int[] randomSequence(int size) {
		int[] result = new int[size];
		for(int i = 0; i < size; i++)
			result[i] = rand.nextInt(3);
		return result;
	}
}
