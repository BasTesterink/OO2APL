package oo2apl.platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; 

import oo2apl.agent.AgentKillSwitch;
import oo2apl.agent.AgentRuntimeData;
import oo2apl.agent.AgentBuilderFactory;
import oo2apl.agent.AgentBuilder;
import oo2apl.agent.AgentComponentFactory;
import oo2apl.agent.AgentCreationFailedException;
import oo2apl.agent.ExternalProcessToAgentInterface;
import oo2apl.agent.AgentID; 
import oo2apl.agent.AgentType; 
import oo2apl.agent.ContextArguments;
import oo2apl.agent.ContextContainer;
import oo2apl.agent.DeliberationRunnableToAgentInterface;
import oo2apl.agent.DeliberationStepToAgentInterface;
import oo2apl.defaults.messenger.DefaultMessenger;
import oo2apl.deliberation.DeliberationRunnable;
import oo2apl.deliberation.DeliberationStep;
import oo2apl.messaging.Messenger;
import oo2apl.messaging.AgentToMessengerInterface;
import oo2apl.plan.Plan;
import oo2apl.plan.PlanSchemeBase;
import oo2apl.plan.PlanSchemeBaseArguments;
/**
 * A Platform is a container that maintains the available thread pool, agent factories, 
 * agent kill switches (to stop an agent from outside itself) and a messenger service. 
 * Operating the platform by code is done through an <code>AdminToPlatformInterface</code>.
 * 
 * @author Bas Testerink
 */
public final class Platform {
	/** The thread pool that is used to execute agents. */
	private final ExecutorService threadPool;
	/** The factories that can produce components from which agents are made. */
	private final Map<AgentType, AgentComponentFactory> factories;
	/** Kill switches that can force an agent to stop executing the next time it wants to deliberate. */
	private final Map<AgentID, AgentKillSwitch> agentKillSwitches; 
	/** The messenger that is used for direct communication between agents. */
	private final Messenger messenger; 

	/**
	 * Sets the threadpool to a new FixedThreadPool with the given amount of execution threads. 
	 * @param nrOfExecutionThreads Number of execution threads that are available for executing agents.
	 * @param messenger Messenger that agents will use to communicate.
	 */
	private Platform(final int nrOfExecutionThreads, final Messenger messenger){
		this.threadPool = Executors.newFixedThreadPool(nrOfExecutionThreads); 
		this.messenger = messenger;
		this.factories = new HashMap<>();
		this.agentKillSwitches = new HashMap<>(); 
		this.factories.put(AgentBuilderFactory.AGENTTYPE, new AgentBuilderFactory()); 
	}

	/**
	 * Create a new <code>Platform</code> and return the administrator's interface
	 * for it. This interface exposes all methods to maintain agent factories,
	 * produce agents, obtain external interfaces to agents and halt agents.
	 * @param nrOfExecutionThreads Maximum number of threads that this platform will use.
	 * @param messenger Messenger for agent to agent communication. Will be the default messenger in case the argument is null.
	 * @return An interface to control the platform.
	 */
	public final static AdminToPlatformInterface newPlatform(final int nrOfExecutionThreads, final Messenger messenger){
		Platform platform;
		if(messenger == null){
			platform = new Platform(nrOfExecutionThreads, new DefaultMessenger());
		} else {
			platform = new Platform(nrOfExecutionThreads, messenger); 
		} 
		return new AdminToPlatformInterface(platform);
	}


	////////////////////////////////////
	//// ADMINISTRATOR FUNCTIONALITY ///
	////////////////////////////////////

	/**
	 * Add a new factory. If for the agent type of the factory there already 
	 * exists a factory, then the old factory is overwritten.
	 * @param factory Factory that can produce agent components. 
	 */
	public final void addFactory(final AgentComponentFactory factory){
		if(factory == null) throw new IllegalArgumentException("Factory argument is null.");
		else {
			AgentType agentType = factory.getAgentType();
			if(agentType == null) throw new IllegalStateException("Factory "+factory+" had a null agent type.");
			else {
				synchronized(this.factories){
					this.factories.put(agentType, factory);
				}
			}
		}
	}

	/**
	 * Remove the factory for a given type of agent. This will prevent new productions of
	 * agents of said type. This will not kill existing agents of that type.
	 * @param agentType Agent type for which to remove the factory. If the type is unknown then nothing happens.
	 */
	public final void removeFactory(final AgentType agentType){
		if(agentType == null) throw new IllegalArgumentException("Agent type argument is null.");
		else {
			synchronized(this.factories){
				this.factories.remove(agentType);
			}
		}
	}

	/**
	 * Create a new agent and schedule it for execution. 
	 * @param agentType The type of agent that must be instantiated.
	 * @param contextArgs The arguments for building the context of the agent.
	 * @param planSchemeBaseArgs The arguments for building the plan scheme base of the agent.
	 * @return An interface so that an external process can send triggers to the agent and get its ID.
	 * @throws AgentCreationFailedException 
	 */
	public final ExternalProcessToAgentInterface newAgent(final AgentType agentType, final ContextArguments contextArgs, final PlanSchemeBaseArguments planSchemeBaseArgs) throws AgentCreationFailedException {
		if(agentType == null) throw new IllegalArgumentException("Agent type argument is null.");
		else {
			AgentComponentFactory factory;
			synchronized(this.factories){
				factory = this.factories.get(agentType);
			}
			if(factory == null) throw new AgentCreationFailedException("AgentType "+agentType+" has no component factory.");
			else {
				AgentRuntimeData agent = produceAgent(factory, contextArgs, planSchemeBaseArgs); 
				AgentID agentID = agent.getAgentID(); 
				if(agentID == null) throw new AgentCreationFailedException("Agent "+agent+" has no agent ID.");
				else {
					DeliberationRunnable deliberationRunnable = new DeliberationRunnable(new DeliberationRunnableToAgentInterface(agent), new DeliberationRunnableToPlatformInterface(this));
					ExternalProcessToAgentInterface externalInterface = agent.produceAgentExternalInterface();
					AgentKillSwitch killSwitch = new AgentKillSwitch(agent);
					synchronized(this.agentKillSwitches){
						this.agentKillSwitches.put(agentID, killSwitch);
					} 
					scheduleForExecution(deliberationRunnable);
					return externalInterface;
				} 
			}
		}
	}
	
	/** 
	 * Create a new agent and schedule it for execution, using an AgentBuilder.
	 * @param builder The builder that is used to make the agent. 
	 * @return An interface so that an external process can send triggers to the agent and get its ID.
	 * @throws AgentCreationFailedException 
	 */
	public final ExternalProcessToAgentInterface newAgent(final AgentBuilder builder) throws AgentCreationFailedException{
		ExternalProcessToAgentInterface agent;
		// We synchronize here because otherwise it is possible that this call is made before the factory is finished, in which 
		// case the data of another builder could potentially be used instead of the provided builder. The cause is that the 
		// factory is 'loaded' with the builder before its produce methods are called. Hence, we do not want another builder to be 
		// loaded in the factory in the meantime.
		synchronized(AgentBuilderFactory.AGENTTYPE){
			AgentBuilderFactory factory;
			synchronized(this.factories){
				factory = (AgentBuilderFactory) this.factories.get(AgentBuilderFactory.AGENTTYPE);
			}
			factory.setBuilder(builder);
			agent = newAgent(AgentBuilderFactory.AGENTTYPE, null, null);
		}
		return agent;
	}
	

	/**
	 * Produce the data container that specifies the agent runtime configuration.  
	 * @param componentFactory The factory for production.
	 * @param contextArgs The arguments for building the context of the agent.
	 * @param planSchemeBaseArgs The arguments for building the plan scheme base of the agent.
	 * @return The data container that is used for the agent's execution.
	 */ 
	private final AgentRuntimeData produceAgent(final AgentComponentFactory componentFactory, final ContextArguments contextArgs, final PlanSchemeBaseArguments planSchemeBaseArgs){
		// TODO: check if all products are nonnull
		AgentID agentID = componentFactory.produceNextAgentID();
		ContextContainer contextContainer = componentFactory.produceContextContainer(contextArgs);
		PlanSchemeBase planSchemeBase = componentFactory.producePlanSchemeBase(planSchemeBaseArgs);
		List<DeliberationStep> deliberationCycle = new ArrayList<>();
		AgentRuntimeData agent = new AgentRuntimeData(agentID, new AgentToMessengerInterface(this.messenger, agentID), contextContainer, planSchemeBase, deliberationCycle);
		List<Plan> initialPlans = componentFactory.produceInitialPlans();
		for(Plan plan : initialPlans) agent.adoptPlan(plan);
		DeliberationStepToAgentInterface deliberationInterface = agent.produceDeliberationInterface();
		deliberationCycle.addAll(componentFactory.produceDeliberationCycle(deliberationInterface)); 
		return agent; 
	}

	////////////////////////////////
	//// EXECUTION FUNCTIONALITY ///
	////////////////////////////////

	/**
	 * Will schedule the deliberation runnable (that executes an agent's deliberation cycle)
	 * for execution in the thread pool. If the pool is already shut down, then the agent will
	 * be killed.
	 * @param deliberationRunnable Deliberation cycle to be executed sometime in the future.
	 */
	public final void scheduleForExecution(final DeliberationRunnable deliberationRunnable){
		boolean scheduled = false;
		synchronized(this.threadPool){
			if(!this.threadPool.isShutdown()){
				scheduled = true;
				this.threadPool.execute(deliberationRunnable);
			}
		}
		// If the thread pool was already shut down, then kill the agent
		if(!scheduled){
			killAgent(deliberationRunnable.getAgentID());
		}
	}

	/**
	 * Removes the agent's references in the platform and notifies the agent so that it will
	 * stop executing after the current/next deliberation cycle.
	 * @param agentID ID of the agent to be killed.
	 */
	public final void killAgent(final AgentID agentID){  
		AgentKillSwitch killSwitch;
		synchronized(this.agentKillSwitches){
			killSwitch = this.agentKillSwitches.remove(agentID);
		}
		if(killSwitch != null){// It's okay if the switch is null. In that case the agent was already killed in the past.
			killSwitch.killAgent(); 
		}
	}
	 

	/**
	 * Will cause all scheduled deliberation cycles to execute, but no more new cycles
	 * are allowed. Those cycles which want to execute after this call will have their
	 * agent be killed.
	 */
	public final void haltPlatform(){
		synchronized(this.threadPool){ // Synchronized, otherwise an agent could be scheduled after a shutdown
			this.threadPool.shutdown();
		}
	} 
}