package dk.itu.ejuuragr.run;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.tmaze.RoundsTMaze;
import dk.itu.ejuuragr.domain.tmaze.TMaze;
import dk.itu.ejuuragr.graph.TMazeVisualizer;
import dk.itu.ejuuragr.graph.TMazeVisualizerAsNN;

/**
 * Allows you to play T-Maze yourself rather than
 * seeing an ANN play it. Use the arrow keys.
 * 
 * @author Rasmus
 *
 */
public class PlayTMaze {

	public static void main(String[] args) throws IOException{
		Properties props = new Properties("tmaze.properties");
		
		TMaze maze = new RoundsTMaze(props);
		maze.reset();
		maze.restart();
		
		String res = prompt("Walls [v]isible or [i]nvisible?");
		if (res.equals("i"))
			new TMazeVisualizerAsNN(maze);
		else
			new TMazeVisualizer(maze, true);
			

	}
	
	private static String prompt(String string) {
		System.out.println(string);
		try {
			return new BufferedReader(new InputStreamReader(System.in)).readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
