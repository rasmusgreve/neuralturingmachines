package dk.itu.ejuuragr.replay;

import java.util.ArrayList;

/**
 * A specific list for storing time steps.
 * 
 * @author Rasmus
 *
 * @param <T> The TuringTimeStep to store for each step.
 */
public class Recording<T extends TuringTimeStep> extends ArrayList<TimeStep<T>> {
	private static final long serialVersionUID = 1L;
}
