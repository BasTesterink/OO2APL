package oo2apl.agent;

/**
 * Exposes the possibility of an external process/module to send an external trigger to
 * the agent, listen to its death and obtain its identity.
 *  
 * @author Bas Testerink
 */
public final class ExternalProcessToAgentInterface {
	/** The agent that is exposed by this interface. */
	private final AgentRuntimeData agent;
	
	public ExternalProcessToAgentInterface(final AgentRuntimeData agent){
		this.agent = agent;
	}
	
	/** Add a listener that listens for the death of this agent. */
	public final void addAgentDeathListener(final AgentDeathListener listener){
		this.agent.addAgentDeathListener(listener);
	}
	
	/** Put an external event in this agent. Will be processed the next deliberation cycle. */
	public final void addExternalTrigger(final Trigger externalTrigger){
		this.agent.addExternalTrigger(externalTrigger);
	}
	
	/** Obtain the id of the agent that is exposed through this interface. */
	public final AgentID getAgentID(){ return this.agent.getAgentID(); }
}
