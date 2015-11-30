package dk.itu.ejuuragr.domain.tmaze;

/**
 * The basic scoring function for RoundsTMaze which
 * simply sums up the reward collected when reaching
 * goals.
 * 
 * @author Emil
 *
 */
public class RewardScorer implements RoundScorer {

	private RoundsTMaze tmaze;

	public RewardScorer(RoundsTMaze tmaze) {
		this.tmaze = tmaze;
	}

	@Override
	public double scoreRound() {
		if(tmaze.getGoalId(tmaze.getPositionTile()) >= 0) {
			return tmaze.isInHighGoal() ? 1.0 : (1.0*tmaze.getLowReward()) / tmaze.getHighReward();
		}
		return 0.0;
	}

}
