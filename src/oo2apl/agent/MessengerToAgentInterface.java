package oo2apl.agent;
/**
 * This class exposes to a messenger the posibility to inject a message trigger 
 * in the agent. 
 * 
 * @author Bas Testerink 
 */
public final class MessengerToAgentInterface {
	/** The agent that is exposed by this interface. */
	private final AgentRuntimeData agent;
	
	public MessengerToAgentInterface(final AgentRuntimeData agent){
		this.agent = agent; 
	}
	
	/** Insert a message in the message queue. */
	public final void deliverMessage(final Trigger message){
		this.agent.deliverMessage(message);
	}
	
	/** Obtain the id of the agent that is exposed through this interface. */
	public final AgentID getAgentID(){ return this.agent.getAgentID(); }
}
