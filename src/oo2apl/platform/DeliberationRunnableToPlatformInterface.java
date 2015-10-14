package oo2apl.platform;

import oo2apl.agent.AgentID;
import oo2apl.deliberation.DeliberationRunnable;
/**
 * Exposes the platform functionalities that a deliberation runnable requires. 
 * These are the ability to reschedule a runnable or to kill an agent.
 * @author Bas Testerink
 */
public final class DeliberationRunnableToPlatformInterface {
	/** Platform that is exposed. */
	private final Platform platform;
	
	public DeliberationRunnableToPlatformInterface(final Platform platform){
		this.platform = platform;
	}
	
	/**
	 * Removes the agent's references in the platform and notifies the agent so that it will
	 * stop executing after the current/next deliberation cycle.
	 * @param agentID ID of the agent to be killed.
	 */
	public final void killAgent(final AgentID agentID){
		this.platform.killAgent(agentID);
	}
	
	/**
	 * Will schedule the deliberation runnable (that executes an agent's deliberation cycle)
	 * for execution in the thread pool. If the pool is already shut down, then the agent will
	 * be killed. Can be used by a deliberation cycle to schedule a different runnable than itself.
	 * @param deliberationRunnable Deliberation cycle to be executed sometime in the future.
	 */
	public final void scheduleForExecution(final DeliberationRunnable runnable){
		this.platform.scheduleForExecution(runnable);
	} 
}
