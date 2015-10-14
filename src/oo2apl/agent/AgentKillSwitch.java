package oo2apl.agent;
/**
 * When the kill switch of an agent kills an agent, then the agent will not be scheduled for
 * execution anymore when it finishes its current deliberation cycle. After that cycle it will
 * publish its own death to any listeners and the messenger. 
 * 
 * @author Bas Testerink 
 */
public final class AgentKillSwitch {
	/** The agent that is exposed by this interface. */
	private final AgentRuntimeData agent;
	
	public AgentKillSwitch(final AgentRuntimeData agent){
		this.agent = agent;
	}
	
	/** This will forcibly kill the agent. It may finish its current deliberation cycle
	 * if applicable. It will be killed an removed from the agent platform before the next
	 * cycle executes. All death listeners will be notified.  */
	public final void killAgent(){
		this.agent.forceStop(); 
	}
} 