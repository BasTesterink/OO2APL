package oo2apl.agent;
/**
 * Goals are a type of trigger that when adopted as goals (they can serve as non-
 * goal triggers as well) then they will remain active until they are achieved. A goal
 * triggers goal plan schemes, but maximally one at a time. I.e. if two schemes can be
 * triggered by the same goal, then the first scheme in the plan scheme base list will 
 * be applied.
 * 
 * @author Bas Testerink
 */
public abstract class Goal implements Trigger {
	/** Whether a plan is active for this goal. */
	private boolean pursued = false;
	
	public final void setPursued(final boolean b){ this.pursued = b; }
	public final boolean isPursued(){ return this.pursued; }
	
	/**
	 * Implement to check whether the goal is achieved according to the information 
	 * that is available to the agent. If a goal is achieved, it will be automatically 
	 * removed. Re-adopt the goal if the goal should be achieved again.
	 * 
	 * @param contextInterface Interface that exposes the context container of the agent.
	 * @return True iff the goal should be considered achieved.
	 */
	public abstract boolean isAchieved(final AgentContextInterface contextInterface);
}
