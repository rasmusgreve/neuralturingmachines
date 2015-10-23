package dk.itu.ejuuragr.domain.tmaze;

import java.util.HashSet;

import com.anji.util.Properties;

public class RoundsTMaze extends TMaze {
	
	private static final boolean DEBUG = true; // True if it should print scores for each round

	private final boolean SWAP_FIX;
	private double swapFraction; // The center fraction of the stepLength
	private int rounds = -1; // The number of rounds in the test
	private int[] switchSpots; // The spot to switch goal at
	private int swapCount;
	private int swapRounds;
	
	private int curRound; // The current round number
	private int totalScore; // The accumulated score over all rounds

	private int goals[]; // for the switching
	
	private boolean isResetting = false;

	private RestartListener listener; // Hack for letting the controller know 

	private final int numGoals;
	private HashSet<Integer> highCanBeIn;

	public RoundsTMaze(Properties props) {
		super(props);
		int roundsPerPer = props.getIntProperty("simulator.tmaze.rounds", 5);
		this.swapFraction = props.getDoubleProperty("simulator.tmaze.swap.fraction", 0.3);
		this.swapCount = props.getIntProperty("simulator.tmaze.swap.swapcount",1);
		this.SWAP_FIX = props.getBooleanProperty("simulator.tmaze.swapfix", false);
		
		this.numGoals = this.getMap().getOfType(MAP_TYPE.goal).size();
		int pairGoals = numGoals / 2;
		this.rounds = roundsPerPer * (swapCount+1) * pairGoals;
		this.swapRounds = (int) (this.swapFraction * roundsPerPer * pairGoals); // For each swap
		
		this.highCanBeIn = new HashSet<>(this.numGoals);
		
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
		resetHashSet();
		
		switchSpots = new int[swapCount];
		int goalCount = this.getMap().getOfType(MAP_TYPE.goal).size();
		for (int i = 0; i < swapCount; i++){
			//int swapSize = (int)(rounds * swapFraction);
			int rawPoint = (int)((rounds/(swapCount+1.0))*(i+1));
			int fuzzedPoint = rawPoint + getRandom().nextInt(swapRounds+1);
			if(SWAP_FIX)
				fuzzedPoint -= swapRounds / 2;
			switchSpots[i] = fuzzedPoint;
			
			// choose locations already
			int nextGoal = getRandom().nextInt(goalCount-1);
			this.goals[i+1] = nextGoal == this.goals[i] ? goalCount-1 : nextGoal;
		}
		
		if(this.listener != null)
			this.listener.onRestart(this);
	}

	private void resetHashSet() {
		highCanBeIn.clear();
		for(int i = 0; i < numGoals; i++)
			highCanBeIn.add(i);
	}

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
				super.swapGoal(goals[swapRound+1]); // switch goal to another of the options
			}
			return super.getInitialObservation();
		}
		double[] superResult = super.performAction(action);
		
		if(super.isTerminated()){ // Round over
			if(DEBUG) {
				System.out.printf("Round %d: %.0f (G%s) step=%02d %s",curRound,super.getCurrentScore(),super.getGoalId(super.getPositionTile()),getStep(),swapRound(curRound) > -1 ? "~" : "");
				System.out.println();
			}
			
			// Determine score
			int curGoal = super.getGoalId(super.getPositionTile());
			if(this.highCanBeIn.contains(curGoal))
				this.totalScore++;
			
			if(curGoal < 0) { // Stop hitting the wall...
				if(DEBUG) System.out.println("> Crash");
				
			}else if(this.highCanBeIn.size() == 1 && this.highCanBeIn.contains(curGoal)) { // You should know this one
				if(super.isInHighGoal()) {
					if(DEBUG) System.out.println("> Exploiting: SUCCESS");
				} else {
					if(DEBUG) System.out.println("> Exploiting: SUCCESS (but has moved)");
					resetHashSet();
					this.highCanBeIn.remove(curGoal);
				}
				
			} else if(this.highCanBeIn.size() == 1 && !this.highCanBeIn.contains(curGoal)){ // Revisit, MISTAKE
				if(DEBUG) System.out.println("> Exploiting: MISTAKE! (know the right one)");
				
			} else if(!this.highCanBeIn.contains(curGoal)) { // Exploring multiple times
				if (DEBUG) System.out.println("> Exploring: MISTAKE! (explored before)");
				
			} else if(super.isInHighGoal()) { // Found right by chance
				if(DEBUG) System.out.println("> Exploring: Found");
				this.highCanBeIn.clear();
				this.highCanBeIn.add(curGoal);
				
			} else { // Didn't find correct one by chance
				if(this.highCanBeIn.isEmpty()) {
					if(DEBUG) System.out.println("> Exploring: Miss (must have moved)");
					resetHashSet();
				} else {
					if(DEBUG) System.out.println("> Exploring: Miss");
				}
				this.highCanBeIn.remove(curGoal);
				
			}
			
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
		if(DEBUG) System.out.println("Score = "+totalScore+" / "+this.rounds);
		return totalScore;
	}

	@Override
	public int getMaxScore() {
		return this.rounds;
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
	
	public int getSwapCount() {
		return this.swapCount;
	}
	
	public void setSwaps(int[] newGoals) {
		super.swapGoal(newGoals[0]);
		this.goals = newGoals;
	}
	
	public void setRestartListener(RestartListener listener) { this.listener = listener; }
	
	public interface RestartListener {
		void onRestart(RoundsTMaze tmaze);
	}
}
