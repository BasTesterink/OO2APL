package oo2apl.plan;

import oo2apl.agent.Trigger;
/**
 * This exception represents an error during the execution of a plan. 
 * 
 * @author Bas Testerink
 */
public class PlanExecutionError extends Exception implements Trigger { 
	private static final long serialVersionUID = 1L;
	//TODO: put more standard data here s.a. reference to failed plan
}
