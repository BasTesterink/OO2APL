package oo2apl.agent;
 
import java.util.List;

import oo2apl.deliberation.DeliberationStep;
import oo2apl.deliberation.SelfRescheduler;
/**
 * This interface exposes the functionalities of an agent that include obtaining the 
 * deliberation cycle and checking whether the agent is done.
 * 
 * @author Bas Testerink
 */
public final class DeliberationRunnableToAgentInterface {
	/** The agent that is exposed by this interface. */
	private final AgentRuntimeData agent;
	
	public DeliberationRunnableToAgentInterface(final AgentRuntimeData agent){
		this.agent = agent;
	}
	
	/** Obtain the id of the agent that is exposed through this interface. */
	public final AgentID getAgentID(){ return this.agent.getAgentID(); }
	
	/** Obtain the agent's deliberation cycle. */
	public final List<DeliberationStep> getDeliberationCycle(){ 
		return this.agent.getDeliberationCycle();
	}
	
	/**
	 * Check whether the agent is done with execution.
	 * @return True iff the agent is forcibly stopped or is finished. 
	 */
	public final boolean isAgentDone(){
		return this.agent.isDone();
	}
	
	public final void addSelfRescheduler(final SelfRescheduler rescheduler){
		this.agent.setSelfRescheduler(rescheduler);
	}
	
	/**
	 * A check to determine whether the agent should go to sleep.
	 * @return True iff the agent is already sleeping or there are no current plans and triggers.
	 */
	public final boolean checkSleeping(){ return this.agent.checkSleeping(); }
}
