package oo2apl.platform;

import oo2apl.agent.AgentComponentFactory;
import oo2apl.agent.AgentCreationFailedException;
import oo2apl.agent.ExternalProcessToAgentInterface;
import oo2apl.agent.AgentID;
import oo2apl.agent.AgentType;
import oo2apl.agent.ContextArguments; 
import oo2apl.plan.PlanSchemeBaseArguments;
 
/**
 * The interface exposes methods of a platform to allow a user to register new, or remove existing,
 *  agent component factories, halt the platform, kill agents and create new agents.
 * @author Bas Testerink
 */
public final class AdminToPlatformInterface {
	/** The platform for which this is the interface.  */
	private final Platform platform;
	
	/** Create this admin interface for the given platform.
	 * @param platform 
	 */
	public AdminToPlatformInterface(final Platform platform){
		this.platform = platform;
	}
	

	/**
	 * Will cause all scheduled deliberation cycles to execute, but no more new cycles
	 * are allowed. Those cycles which want to execute after this call will have their
	 * agent be killed.
	 */
	public final void haltPlatform(){
		this.platform.haltPlatform();
	}

	/**
	 * Removes the agent's references in the platform and notifies the agent so that it will
	 * stop executing after the current/next deliberation cycle.
	 * @param agentID ID of the agent to be killed.
	 */
	public final void killAgent(final AgentID agentID){  
		this.platform.killAgent(agentID);
	}


	/**
	 * Create a new agent and schedule it for execution. 
	 * @param agentType The type of agent that must be instantiated.
	 * @param contextArgs The arguments for building the context of the agent.
	 * @param planSchemeBaseArgs The arguments for building the plan scheme base of the agent.
	 * @return An interface so that an external process can send triggers to the agent and get its ID.
	 * @throws AgentCreationFailedException 
	 */
	public final ExternalProcessToAgentInterface newAgent(final AgentType agentType, final ContextArguments contextArgs, final PlanSchemeBaseArguments planSchemeBaseArgs){
		try {
			return this.platform.newAgent(agentType, contextArgs, planSchemeBaseArgs);
		} catch (AgentCreationFailedException e) { 
			e.printStackTrace(); 
			System.exit(1);
			return null;
		}
	}

	/**
	 * Remove the factory for a given type of agent. This will prevent new productions of
	 * agents of said type. This will not kill existing agents of that type.
	 * @param agentType Agent type for which to remove the factory. If the type is unknown then nothing happens.
	 */
	public final void removeFactory(final AgentType agentType){
		this.platform.removeFactory(agentType);
	}

	/**
	 * Add a new factory. If for the agent type of the factory there already 
	 * exists a factory, then the old factory is overwritten.
	 * @param factory Factory that can produce agent components. 
	 */
	public final void addFactory(final AgentComponentFactory factory){
		this.platform.addFactory(factory);
	} 
}
