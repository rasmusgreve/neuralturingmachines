package dk.itu.ejuuragr.graph;

import dk.itu.ejuuragr.replay.Recording;
import dk.itu.ejuuragr.replay.TuringTimeStep;

/**
 * Records steps for T-Maze.
 * 
 * @author Rasmus
 *
 */
public class TMazeStepReplayVisualizer extends StepReplayVisualizer {

	public TMazeStepReplayVisualizer(Recording<TuringTimeStep> recording){
		super (recording);
	}
	
	public void update(){
		stepIndex = recording.size()-1;
		repaint();
	}
	
	
}
