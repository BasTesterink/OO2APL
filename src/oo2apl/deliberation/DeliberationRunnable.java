package oo2apl.deliberation;
 
import oo2apl.agent.AgentID;
import oo2apl.agent.DeliberationRunnableToAgentInterface;
import oo2apl.platform.DeliberationRunnableToPlatformInterface; 
/**
 * A deliberation runnable implements how an agent is executed. This is done by 
 * grabbing the agent's deliberation cycle and execute each step. Then, if the 
 * agent is done, it will not reschedule itself, otherwise it will reschedule itself.
 *
 * @author Bas Testerink
 */
public final class DeliberationRunnable implements Runnable { 
	/** Interface to obtain the relevant agent's data. */
	private final DeliberationRunnableToAgentInterface agentInterface;
	/** Interface to the relevant platform functionalities. */
	private final DeliberationRunnableToPlatformInterface platform;

	/**
	 * Creation of the deliberation runnable will also result in the setting of a self-rescheduler for this runnable  
	 * through the agent interface. 
	 * @param agent
	 * @param platform
	 */
	public DeliberationRunnable(final DeliberationRunnableToAgentInterface agent, final DeliberationRunnableToPlatformInterface platform){
		this.agentInterface = agent;
		this.platform = platform;
		this.agentInterface.addSelfRescheduler(new SelfRescheduler(this));
	}

	/**
	 * Run the deliberation cycle of the agent once. Will ask the platform
	 * to execute again sometime in the future if the agent is not done
	 * according to its <code>isDone</code> method. The agent is killed
	 * in case it is done, or if a <code>DeliberationStepException</code> occurs.
	 * If the agent is done or if a deliberation step exception occurs, then it will be
	 * killed and removed from the platform.
	 */
	public void run(){
		if(!this.agentInterface.isAgentDone()){ // Check first if agent was killed outside of this runnable
			try {   
				// Go through the cycle and execute each step.
				// Note that the deliberation cycle cannot change at runtime.  
				for(DeliberationStep step : this.agentInterface.getDeliberationCycle()){
					step.execute();
				}

				// If all deliberation steps are finished, then check whether
				// the agent is done, so it can be killed.
				if(this.agentInterface.isAgentDone()){
					this.platform.killAgent(this.agentInterface.getAgentID()); 
				} else {
					if(!this.agentInterface.checkSleeping()){ // If the agents goes to sleep then it will be woken upon any external input (message, external trigger)
						reschedule();
					}  
				}
			} catch(DeliberationStepException exception){ 
				// Deliberation exceptions should not occur. The agent is 
				// killed and removed from the platform. All proxy's are
				// notified of the agent's death. The rest of the multi-
				// agent system will continue execution by default.
				this.platform.killAgent(this.agentInterface.getAgentID()); 
			} 
		} else {
			this.platform.killAgent(this.agentInterface.getAgentID()); 
		}
	}  
	
	/** Returns the id of the agent to which this runnable belongs. */
	public final AgentID getAgentID(){ return this.agentInterface.getAgentID(); }
	
	/** Reschedule this deliberation runnable so it will be executed again in the future. */
	public final void reschedule(){
		this.platform.scheduleForExecution(this);
	} 
}