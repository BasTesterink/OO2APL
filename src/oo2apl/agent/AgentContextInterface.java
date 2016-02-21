package oo2apl.agent;
/**
 * This interface exposes the possiblity to obtain an agents contexts.
 * 
 * @author Bas Testerink
 */
public final class AgentContextInterface {
	/** The agent that is exposed by this interface. */
	private final AgentRuntimeData agent;
	
	public AgentContextInterface(AgentRuntimeData agent){
		this.agent = agent;
	}
	
	/** Obtain the context that belongs to a given class. */
	public final <C extends Context> C getContext(final Class<C> klass){
		return agent.getContext(klass);
	}
}
