package dk.itu.ejuuragr.domain.tmaze;

import java.util.Arrays;

import com.anji.util.Properties;

/**
 * Makes sure the evaluation is run on every possible
 * permutation of initial goal placements and location
 * after the switch.
 * 
 * @author Emil
 *
 */
public class PermutationsTMaze extends RoundsTMaze {
	
	private int numPermutations;
	private int numGoals;
	
	private int completedRounds;
	private int[] currentPermutation;
	private double totalScore;

	public PermutationsTMaze(Properties props) {
		super(props);

		// calculate number of permutations
		this.numGoals = this.getMap().getOfType(MAP_TYPE.goal).size();
		numPermutations = numGoals;
		for(int i = 0; i < this.getSwapCount(); i++)
			numPermutations *= (numGoals-1);
	}

	@Override
	public void restart() {
		super.restart();
		
		totalScore = 0.0;
		completedRounds = 0;
		currentPermutation = new int[this.getSwapCount() + 1];
		// set lowest swap
		this.setToLowestAfterIndex(0);
		enforcePermutation();
	}

	@Override
	public double getCurrentScore() {
		return numPermutations * super.getCurrentScore();
	}

	@Override
	public int getMaxScore() {
		return numPermutations * super.getMaxScore();
	}

	@Override
	public double[] performAction(double[] action) {
		double[] superResult = super.performAction(action);
		
		if(super.isTerminated()) { // rounds simulation over
			double score = super.getCurrentScore();
			this.totalScore += score;
			
			super.restart();
			this.incrementPermutation();
			this.enforcePermutation();
			completedRounds++;
		}
		
		return superResult;
	}

	private void enforcePermutation() {
		super.setSwaps(currentPermutation);
	}

	@Override
	public boolean isTerminated() {
		return completedRounds == numPermutations;
	}
	
	private void setToLowestAfterIndex(int index) {
		for(int i = index + 1; i < currentPermutation.length; i++) {
			currentPermutation[i] = currentPermutation[i - 1] == 0 ? 1 : 0;
		}
	}

	private void incrementPermutation() {
		for(int i = currentPermutation.length-1; i >= 0; i--) {
			currentPermutation[i] = currentPermutation[i] + 1;
			// if we get up to the one as before, continue
			if(i > 0 && currentPermutation[i] == currentPermutation[i - 1]) 
				currentPermutation[i] = currentPermutation[i] + 1;
			
			if(i > 0 && currentPermutation[i] + 1 >= this.numGoals) { // roll over
				setToLowestAfterIndex(i-1);
			} else {
				return;
			}
		}
		
		System.out.println(completedRounds+": "+Arrays.toString(currentPermutation));
	}
}
