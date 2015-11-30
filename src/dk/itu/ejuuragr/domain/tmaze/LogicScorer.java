package dk.itu.ejuuragr.domain.tmaze;

import java.util.HashSet;

import dk.itu.ejuuragr.domain.tmaze.TMaze.MAP_TYPE;

/**
 * The scoring function for RoundsTMaze which counts
 * the number of logically sound choices the agent
 * makes in its choice of goal.
 * 
 * @author Emil
 *
 */
public class LogicScorer implements RoundScorer {
	
	private HashSet<Integer> highCanBeIn;
	private int numGoals;
	private RoundsTMaze tmaze;

	public LogicScorer(RoundsTMaze tmaze) {
		this.numGoals = tmaze.getMap().getOfType(MAP_TYPE.goal).size();
		this.highCanBeIn = new HashSet<>();
		this.tmaze = tmaze;
		
		resetHashSet();
	}

	@Override
	public double scoreRound() {
		int result = 0;
		int curGoal = tmaze.getGoalId(tmaze.getPositionTile());
		
		if(this.highCanBeIn.contains(curGoal)) {
			result = 1;
		}
		
		if(curGoal < 0) { // Stop hitting the wall...
			if(RoundsTMaze.DEBUG) System.out.println("> Crash");
			
		}else if(this.highCanBeIn.size() == 1 && this.highCanBeIn.contains(curGoal)) { // You should know this one
			if(tmaze.isInHighGoal()) {
				if(RoundsTMaze.DEBUG) System.out.println("> Exploiting: SUCCESS");
			} else {
				if(RoundsTMaze.DEBUG) System.out.println("> Exploiting: SUCCESS (but has moved)");
				resetHashSet();
				this.highCanBeIn.remove(curGoal);
			}
			
		} else if(this.highCanBeIn.size() == 1 && !this.highCanBeIn.contains(curGoal)){ // Revisit, MISTAKE
			if(RoundsTMaze.DEBUG) System.out.println("> Exploiting: MISTAKE! (know the right one)");
			if(tmaze.isInHighGoal()) { // But was swapped, so got lucky. No score but should know for future.
				this.highCanBeIn.clear();
				this.highCanBeIn.add(curGoal);
			}
		} else if(!this.highCanBeIn.contains(curGoal)) { // Exploring multiple times
			if (RoundsTMaze.DEBUG) System.out.println("> Exploring: MISTAKE! (explored before)");
			if(tmaze.isInHighGoal()) { // But was swapped, so got lucky. No score but should know for future.
				this.highCanBeIn.clear();
				this.highCanBeIn.add(curGoal);
			}
		} else if(tmaze.isInHighGoal()) { // Found right by chance
			if(RoundsTMaze.DEBUG) System.out.println("> Exploring: Found");
			this.highCanBeIn.clear();
			this.highCanBeIn.add(curGoal);
			
		} else { // Didn't find correct one by chance
			if(this.highCanBeIn.isEmpty()) {
				if(RoundsTMaze.DEBUG) System.out.println("> Exploring: Miss (must have moved)");
				resetHashSet();
			} else {
				if(RoundsTMaze.DEBUG) System.out.println("> Exploring: Miss");
			}
			this.highCanBeIn.remove(curGoal);
		}

		return result;
	}
	
	private void resetHashSet() {
		highCanBeIn.clear();
		for(int i = 0; i < numGoals; i++)
			highCanBeIn.add(i);
	}
}
