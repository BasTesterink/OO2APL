package oo2apl.messaging;
 
import oo2apl.agent.AgentID;
import oo2apl.agent.MessengerToAgentInterface;
import oo2apl.agent.Trigger;
import oo2apl.defaults.messenger.MessageReceiverNotFoundException;
/**
 * Implement a messenger to allow agents to communicate with each other.
 * 
 * @author Bas Testerink
 */
public interface Messenger {
	/** Intended to make the messenger aware of the agents' existence. Registering is required for the agent to send and receive messages. */
	public void register(final MessengerToAgentInterface agentInterface); 
	
	/** Deregister to announce that this agent will no longer listen to messages that are received (will also disable the possiblity for sending messages). */
	public void deregister(final AgentID agentID); 
	
	/** Send a message to the receiver. Will throw an exception if that receiver is unknown to the messenger. 
	 * Take care that if you implement this method, that then the receiving party obtains this message through its 
	 * messenger to agent interface. */
	public void sendMessage(final AgentID receiver, final Trigger message) throws MessageReceiverNotFoundException;
}
