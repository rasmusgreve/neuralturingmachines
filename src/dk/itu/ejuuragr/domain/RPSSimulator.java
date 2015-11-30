package dk.itu.ejuuragr.domain;

import com.anji.util.Properties;

import dk.itu.ejuuragr.fitness.Utilities;

/**
 * A simpe sample simulator which plays Rock, Paper,
 * Scissors againt the agent. This opponent is predictable
 * (just cycles through the options) so an agent with
 * memory should be able to easily beat it.)
 * 
 * @author Emil
 *
 */
public class RPSSimulator extends BaseSimulator {
	
	static final boolean DEBUG = false;

	static final int[][] WINNER = new int[][] { {0,1,-1},
												{-1,0,1},
												{1,-1,0}
											  };
	static final double SWAP_AREA = 0.3; // The center 30% of the stepLength

	private int score;
	private int stepsTotal;
	private String mode;
	
	private int[] sequence;
	private int step; // how far we are in the sequence

	private int sequenceLength;
	
	public RPSSimulator(Properties props) {
		super(props);
		stepsTotal = props.getIntProperty("simulator.steps.max", 5);
		sequenceLength = props.getIntProperty("simulator.rps.sequence.length", 3);
		mode = props.getProperty("simulator.rps.mode", "fixed");
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
		int index = step % sequenceLength;
		int choice = Utilities.maxPos(action);
		int curScore = WINNER[choice][sequence[index]];
		if(curScore == 1) score++; // Only get a point for winning
		
		if(DEBUG) {
			System.out.printf("Step %d: Choice=%d, Opponent=%d, Result=% 2d, Score=%d",step,choice,sequence[index],curScore,score);
			System.out.println();
		}
		
		step++;
		
		return new double[]{(curScore + 1) / 2.0}; // 0 is losing, � is tie, 1 is winning.
	}

	@Override
	public double getCurrentScore() {
		return score;
	}

	@Override
	public boolean isTerminated() {
		return step >= (stepsTotal * sequence.length);
	}

	@Override
	public int getMaxScore() {
		return stepsTotal * sequence.length; // if you win all
	}

	@Override
	public void restart() {
		if(DEBUG) System.out.println("-----------------");
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
			break;
		default:
			throw new IllegalArgumentException(mode+" is not a legal mode");
		}
	}

	private int[] swapSequence() {
		int[] result = new int[stepsTotal];
		int swapArea = (int)(stepsTotal * SWAP_AREA);
		int switchSpot = (stepsTotal - swapArea) / 2 + getRandom().nextInt(swapArea+1);
		int first = getRandom().nextInt(3);
		int second = getRandom().nextInt(2);
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
			result[i] = getRandom().nextInt(3);
		return result;
	}
}
