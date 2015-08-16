package dk.itu.ejuuragr.run;

import java.io.IOException;

import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.TMaze;
import dk.itu.ejuuragr.graph.TMazeVisualizerAsNN;

public class PlayTMaze {

	public static void main(String[] args) throws IOException{
		Properties props = new Properties("tmaze.properties");
		
		
		TMaze maze = new TMaze(props);
		maze.reset();
		maze.restart();
		TMazeVisualizerAsNN viz = new TMazeVisualizerAsNN(maze);

	}
	
}
