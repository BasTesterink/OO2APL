package oo2apl.messaging;
 
import oo2apl.agent.AgentID;
import oo2apl.agent.MessengerToAgentInterface;
import oo2apl.agent.Trigger;
import oo2apl.defaults.messenger.MessageReceiverNotFoundException;
/**
 * This interface exposes the methods of a messenger to an agent so that it 
 * can register/deregister and send messages. If registered, then messages will 
 * be inserted in the agent's queue automatically upon arrival in the messenger. 
 * 
 * @author Bas Testerink
 */
public final class AgentToMessengerInterface {
	/** Underlying messenger. */
	private final Messenger messenger;
	/** ID of the agent to which this interface belongs. */
	private final AgentID agentID;
	
	/** Simply sets the fields. */
	public AgentToMessengerInterface(final Messenger messenger, final AgentID agentID){
		this.messenger = messenger;
		this.agentID = agentID; 
	}
	
	/** Make the messenger aware of the agents' existence. Registering is required for the agent to send and receive messages. */
	public final void register(final MessengerToAgentInterface agentInterface){
		this.messenger.register(agentInterface);
	}
	 
	/** Deregister to announce that this agent will no longer listen to messages that are received (will also disable the possiblity for sending messages). */
	public final void deregister(){
		this.messenger.deregister(this.agentID);
	}
	
	/** Send a message to the receiver. */
	public final void sendMessage(final AgentID receiver, final Trigger message) {
		try {
			this.messenger.sendMessage(receiver, message);
		} catch (MessageReceiverNotFoundException e) { 
			e.printStackTrace();
		}
	} 
}
