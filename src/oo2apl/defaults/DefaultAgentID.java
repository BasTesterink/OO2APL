package oo2apl.defaults;

import oo2apl.agent.AgentID;
import oo2apl.agent.AgentType;
/**
 * This default ID can be used for many applications. An agent's id is composed of 
 * the agent type that its factory has and a unique (for that type) id number.
 *  
 * @author Bas Testerink
 */
public final class DefaultAgentID implements AgentID {
	/** The id number of the agent. */
	private final long idNumber;
	
	/** The type of the agent. */
	private final AgentType agentType;
	
	public DefaultAgentID(final long idNumber, final AgentType agentType){
		this.idNumber = idNumber;
		this.agentType = agentType;
	}
	
	/** Returns a JSON format now. Probably changes in the future. */
	public String toString(){
		return "\"agentID\":[\"instance\": \""+Long.toString(this.idNumber)+"\", \"agentType\": "+this.agentType+"]";
	}
}