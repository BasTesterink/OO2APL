package oo2apl.defaults.messenger; 
import java.util.HashMap;
import java.util.Map; 

import oo2apl.agent.AgentID;
import oo2apl.agent.MessengerToAgentInterface;
import oo2apl.agent.Trigger; 
import oo2apl.messaging.Messenger;
/**
 * The default messenger is a very simple implementation for communication between 
 * agents on the same JVM instance. 
 * 
 * @author Bas Testerink
 */
public final class DefaultMessenger implements Messenger { 
	/** Stores the interfaces to agents to inject messages. */
	private final Map<AgentID, MessengerToAgentInterface> agentInterfaces;	// Integers in the key field are referred to as agent id's

	public DefaultMessenger(){
		this.agentInterfaces = new HashMap<>(); 
	} 

	/** Store the agent interface. */
	public final void register(final MessengerToAgentInterface agentInterface){ 
		synchronized(this.agentInterfaces){ 
			this.agentInterfaces.put(agentInterface.getAgentID(), agentInterface);
		}
	}

	/** Remove the agent interface from the messenger. */
	public final void deregister(final AgentID agentID){ 
		synchronized(this.agentInterfaces){ 
			this.agentInterfaces.remove(agentID); 
		}
	} 
	
	/** Grab the agent interface of the receiver and add the message in the receiving agent. */
	public final void sendMessage(final AgentID receiver, final Trigger message) throws MessageReceiverNotFoundException{
		synchronized(this.agentInterfaces){
			MessengerToAgentInterface agentInterface = this.agentInterfaces.get(receiver);
			if(agentInterface == null){
				//TODO send message to sender that receiver is unknown instead of exception
				throw new MessageReceiverNotFoundException("Trying to send to non-existent agent "+receiver+".");
			} else {
				agentInterface.deliverMessage(message);
			}
		} 
	} 
} 