package dk.itu.ejuuragr.domain.tmaze;

/**
 * The interface of a class that can score a round of T-Maze.
 * 
 * IMPORTANT: Implementations of this interface are REQUIRED to
 * have a Constructor taking a single argument of the type RoundsTMaze.
 * 
 * @author Emil
 *
 */
public interface RoundScorer {

	/**
	 * This method will be called by the RoundsTMaze parent when a
	 * round is over (i.e. the agent has just reached a goal or hit
	 * a wall.
	 * 
	 * @return A double between 0.0 and 1.0 (inclusive) of how well
	 * the RoundScorer thinks the agent performed.
	 */
	public double scoreRound();
}
