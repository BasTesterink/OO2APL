package oo2apl.deliberation;
/**
 * A deliberation step furthers the state of an agent. 
 * @author Bas Testerink
 */
public interface DeliberationStep {
	/** Execution of this deliberation step. It is intended that steps are
	 * designed in a modular fashion. For instance the default deliberation steps
	 * each implement a single step from the 2APL deliberation cycle. This way
	 * an agent component factory can decide at runtime which deliberation steps 
	 * should be part of an agents' deliberation cycle.  */ 
	public void execute() throws DeliberationStepException;
}
