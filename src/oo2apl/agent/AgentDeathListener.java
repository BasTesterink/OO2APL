package oo2apl.agent;
/**
 * An agent death listener is notified when an agent is dead. This will result in 
 * that the agent may finish a current deliberation cycle but will never execute 
 * a new one. 
 * 
 * @author Bas Testerink
 */
public interface AgentDeathListener {
	/** Notifies the listener that the agent, as identified by the provided id, 
	 * will be removed from the platform. */
	public void agentDied(final AgentID agentID);
}
