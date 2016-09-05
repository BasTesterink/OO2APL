package oo2apl.plan.builtin;

import java.util.concurrent.ExecutorService; 
  
import oo2apl.agent.Context;

/**
 * This is an auxiliary context interface that allows agents to execute code concurrently to the rest of the agent. This is intended for heavy/long calculations which may 
 * block the agent's execution. The context exposes an executor service. By using a context it is easier to share for instance this service among agents 
 * so that not each agent spawns one or more threads, which might be problematic in a large multi-agent system. 
 * 
 * Adding a concurrency context to an agent (in its AgentComponentFactory) enables the waitForX methods in the PlanToAgentInterface.
 *
 * @author Bas Testerink
 *
 */
public interface ConcurrencyContext extends Context{ 
	public ExecutorService getExecutor();
}
