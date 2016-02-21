package oo2apl.agent;

import oo2apl.defaults.messenger.MessageReceiverNotFoundException;
import oo2apl.plan.Plan;
import oo2apl.plan.TriggerInterceptor;

/**
 * Exposes all functionalities to an agent that the execution of a plan might need/is allowed
 * to call upon according to 2APL standards. 
 * 
 * @author Bas Testerink 
 */
public final class PlanToAgentInterface {
	/** The agent that is exposed by this interface. */
	private final AgentRuntimeData agent;
	
	public PlanToAgentInterface(final AgentRuntimeData agent){
		this.agent = agent;
	}

	/** Obtain the id of the agent that is exposed through this interface. */
	public final AgentID getAgentID(){ return this.agent.getAgentID(); }
	
	/** Obtain the context that belongs to a certain class. */
	public final <C extends Context> C getContext(final Class<C> klass){ return this.agent.getContext(klass); }
	
	/** Check whether the list of current goals contains the provided argument goal. */
	public final boolean hasGoal(final Goal goal){ return this.agent.hasGoal(goal); }
	
	/** Remove the provided goal from the list of current goals. */
	public final void dropGoal(final Goal goal){ this.agent.dropGoal(goal); }
	
	/** Add a goal to the list of current goals. Will check whether the list of 
	 * current goals already contains the provided goal. */
	public final void adoptGoal(final Goal goal){ this.agent.adoptGoal(goal); }
	
	/** Add a plan to the list of current plans. This plan will be executed during
	 * the next "execute plans" deliberation step. */
	public final void adoptPlan(final Plan plan){ this.agent.adoptPlan(plan); }
	
	/** Add an internal trigger to the list of current internal triggers. This trigger 
	 * will be processed during the next deliberation cycle.*/
	public final void addInternalTrigger(final Trigger trigger){ this.agent.addInternalTrigger(trigger); }
	
	/**
	 * By default an agent is never finished, unless this method is called explicitly
	 * from within a plan. If this method is called then the agent will be killed and 
	 * removed from the platform before it can start a new deliberation cycle.
	 */
	public final void finished(){ this.agent.finished(); } // The agent is finished with its execution
	
	/** Add an interceptor for goals. */
	public final void adoptGoalInterceptor(final TriggerInterceptor interceptor){
		this.agent.adoptGoalInterceptor(interceptor);
	}
	
	/** Add an interceptor for external triggers. */
	public final void adoptExternalTriggerInterceptor(final TriggerInterceptor interceptor){
		this.agent.adoptExternalTriggerInterceptor(interceptor);
	}

	/** Add an interceptor for internal triggers. */
	public final void adoptInternalTriggerInterceptor(final TriggerInterceptor interceptor){
		this.agent.adoptInternalTriggerInterceptor(interceptor);
	}
	
	/** Add an interceptor for messages. */
	public final void adoptMessageInterceptor(final TriggerInterceptor interceptor){
		this.agent.adoptMessageInterceptor(interceptor);
	}
	
	/** Send a message through the agent's messenger client. */
	public final void sendMessage(final AgentID receiver, final Trigger message) {
		try {
			this.agent.sendMessage(receiver, message);
		} catch (MessageReceiverNotFoundException e) { 
			e.printStackTrace();
		}
	} 
	
	/**
	 * A plan can obtain AgentExternalInterface instances to use them for instance to create interfaces for environments
	 * to send external event to the agent. It is NOT intended that an agent adds external triggers to itself through 
	 * such an instance.  
	 */
	public final ExternalProcessToAgentInterface produceAgentExternalInterface(){
		return this.agent.produceAgentExternalInterface();
	}
} 
