package oo2apl.defaults;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; 
import oo2apl.plan.builtin.ConcurrencyContext;

/**
 * This is an auxiliary context class that allows agents to execute code concurrently to the rest of the agent. This is intended for heavy/long calculations which may 
 * block the agent's execution. The context exposes an executor service. By using a context it is easier to share for instance this service among agents 
 * so that not each agent spawns one or more threads, which might be problematic in a large multi-agent system. 
 * 
 * This particular default implementation uses a standard single thread executor or fixed thread pool, depending on whether a number of threads 
 * is specified in the constructor. 
 *
 * @author Bas Testerink
 *
 */
public class DefaultConcurrencyContext implements ConcurrencyContext {
	private final ExecutorService executor;

	public DefaultConcurrencyContext(){
		this.executor = Executors.newSingleThreadExecutor();
	}
	
	public DefaultConcurrencyContext(final int nrOfThreads){
		this.executor = Executors.newFixedThreadPool(nrOfThreads);  
	}
	
	public final ExecutorService getExecutor(){ return this.executor; }
}
