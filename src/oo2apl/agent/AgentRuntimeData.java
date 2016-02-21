package oo2apl.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import oo2apl.defaults.messenger.MessageReceiverNotFoundException;
import oo2apl.deliberation.DeliberationStep;
import oo2apl.deliberation.SelfRescheduler;
import oo2apl.messaging.AgentToMessengerInterface;
import oo2apl.plan.Plan; 
import oo2apl.plan.PlanExecutionError;
import oo2apl.plan.PlanScheme;
import oo2apl.plan.PlanSchemeBase;
/**
 * This class is the main container for a single agent. It contains all the references to its 
 * relevant data s.a. its id, messenger client, context container, plan scheme base, 
 * deliberation cycle, current triggers, current plans, etc.
 * 
 * @author Bas Testerink
 */
import oo2apl.plan.TriggerInterceptor;
public final class AgentRuntimeData {
	/** The identifier of the agent. */
	private final AgentID agentID;
	
	/** The messenger client that the agent can use to send messages.  */
	private final AgentToMessengerInterface messengerClient;
	
	/** The context container which contains contexts for decision making and actuation. */
	private final ContextContainer contextContainer;
	
	/** The current goals. */
	private final List<Goal> goals;
	
	/** The current internal, external and message triggers. */
	private List<Trigger> internalTriggers, externalTriggers, messages;
	
	/** The current trigger interceptors. */
	private List<TriggerInterceptor> internalTriggerInterceptors, externalTriggerInterceptors, messageInterceptors, goalInterceptors;
	
	/** The agent's plan scheme base that defines its decision making. */
	private final PlanSchemeBase planSchemeBase;
	
	/** The current plans of the agent. */
	private final List<Plan> plans;
	
	/** The deliberation cycle of the agent. */
	private final List<DeliberationStep> deliberationCycle;
	
	/** Whether the agent is forced to stop, is finished, or is sleeping. */
	private boolean forciblyStop, finished, sleep;
	
	/** Interface that exposes the relevant parts of the agent run time data for plans. */
	private final PlanToAgentInterface planInterface;
	
	/** Interface that exposes the context container of this agent. Is given to goals for checking whether they are achieved. */
	private final AgentContextInterface contextInterface;
	
	/** Listeners that are notified when this agent dies. */
	private final List<AgentDeathListener> deathListeners;
	
	/** Interface to the platform that allows the agent to reschedule its own deliberation runnable. */
	private SelfRescheduler rescheduler = null;

	/** The constructor also automatically registers the agent at the provided messenger. */
	public AgentRuntimeData(final AgentID agentID, final AgentToMessengerInterface messengerClient, 
			final ContextContainer contextContainer, final PlanSchemeBase planSchemeBase,
			final List<DeliberationStep> deliberationCycle){
		this.agentID = agentID;
		this.contextContainer = contextContainer;
		this.goals = new ArrayList<>();
		this.internalTriggers = new ArrayList<>();
		this.externalTriggers = new ArrayList<>();
		this.messages = new ArrayList<>();
		this.internalTriggerInterceptors = new ArrayList<>();
		this.externalTriggerInterceptors = new ArrayList<>();
		this.messageInterceptors = new ArrayList<>();
		this.goalInterceptors = new ArrayList<>();
		this.planSchemeBase = planSchemeBase;
		this.plans = new ArrayList<>();
		this.deliberationCycle = Collections.unmodifiableList(deliberationCycle);
		this.planInterface = new PlanToAgentInterface(this);
		this.contextInterface = new AgentContextInterface(this);
		this.deathListeners = new ArrayList<>();
		
		this.messengerClient = messengerClient;
		this.messengerClient.register(produceMessengerToAgentInterface());
	} 
	
	/**
	 * If the agent receives input then this method will be called upon to check 
	 * whether it is required to reschedule. If so (i.e. when the agent is currently 
	 * sleeping) then the agent's runnable will be rescheduled by this method.  
	 */
	private final void checkWhetherToReschedule(){
		synchronized(this.rescheduler){
			if(this.rescheduler == null){
				throw new IllegalStateException("No selfrescheduler set for AgentRuntimeData");
			}
			if(this.sleep){
				this.sleep = false; 
				this.rescheduler.wakeUp();
			}
		}
	}
	
	///////////////////////////////////////////
	//// MESSENGER INTERFACE FUNCTIONALITY ////
	///////////////////////////////////////////
	
	/** Produce an interface that a messenger can use to insert a message in this agent. */
	private final MessengerToAgentInterface produceMessengerToAgentInterface(){
		return new MessengerToAgentInterface(this);
	}
	
	/** Insert a message in the message queue. */
	public final void deliverMessage(final Trigger message){
		synchronized(this.messages){
			this.messages.add(message);
			checkWhetherToReschedule();
		}
	}
	
	//////////////////////////////////////////
	//// EXTERNAL INTERFACE FUNCTIONALITY ////
	//////////////////////////////////////////
	
	/** Produce an interface that an external process can use to insert external triggers in this agent. */
	public final ExternalProcessToAgentInterface produceAgentExternalInterface(){
		return new ExternalProcessToAgentInterface(this);
	} 

	/** Add a listener that listens for the death of this agent. */
	public final void addAgentDeathListener(final AgentDeathListener listener){
		synchronized(this.deathListeners){ 
			this.deathListeners.add(listener);
		}
	}
	
	/** Remove a listener that listens for the death of this agent. */
	public final void unsubscribeDeathListener(final AgentDeathListener listener){
		synchronized(this.deathListeners){
			this.deathListeners.remove(listener);
		}
	}
	
	/** Put an external event in this agent. Will be processed the next deliberation cycle. */
	public final void addExternalTrigger(final Trigger trigger){
		synchronized(this.externalTriggers){ 
			this.externalTriggers.add(trigger);
			checkWhetherToReschedule();
		}
	}

	//////////////////////////////////////
	//// PLAN INTERFACE FUNCTIONALITY ////
	//////////////////////////////////////  
	
	/** Obtain the context that belongs to a given class. */
	public final <C extends Context> C getContext(final Class<C> klass){
		C context = this.contextContainer.getContext(klass);
		if(context == null){ // Context with that type unknown. This should not occur.
			throw new IllegalArgumentException("Trying to obtain a context with class "+klass+" which doesn't exist.");
		}
		return context;
	}
	
	/** Send a message through the agent's messenger client. */
	public final void sendMessage(final AgentID receiver, final Trigger message) throws MessageReceiverNotFoundException {
		this.messengerClient.sendMessage(receiver, message);
	}
	
	 // No synchronize on goals as maximally 1 thread at a time can call these methods
	
	/** Check whether the list of current goals contains the provided argument goal. */
	public final boolean hasGoal(final Goal goal){
		return this.goals.contains(goal);
	}
	
	/** Remove the provided goal from the list of current goals. */
	public final void dropGoal(final Goal goal){
		this.goals.remove(goal);
	}
	
	/** Add a goal to the list of current goals. Will check whether the list of 
	 * current goals already contains the provided goal. */
	public final void adoptGoal(final Goal goal){
		if(!hasGoal(goal)){
			this.goals.add(goal);
		}
	}
	
	/** Add a plan to the list of current plans. This plan will be executed during
	 * the next "execute plans" deliberation step. */
	public final void adoptPlan(final Plan plan){
		this.plans.add(plan);
	}

	/** Add an interceptor for goals. */
	public final void adoptGoalInterceptor(final TriggerInterceptor interceptor){
		this.goalInterceptors.add(interceptor);
	}
	
	/** Add an interceptor for external triggers. */
	public final void adoptExternalTriggerInterceptor(final TriggerInterceptor interceptor){
		this.externalTriggerInterceptors.add(interceptor);
	}

	/** Add an interceptor for internal triggers. */
	public final void adoptInternalTriggerInterceptor(final TriggerInterceptor interceptor){
		this.internalTriggerInterceptors.add(interceptor);
	}
	
	/** Add an interceptor for messages. */
	public final void adoptMessageInterceptor(final TriggerInterceptor interceptor){
		this.messageInterceptors.add(interceptor);
	}
	
	/** Add an internal trigger to the list of current internal triggers. This trigger 
	 * will be processed during the next deliberation cycle.*/
	public final void addInternalTrigger(final Trigger trigger){
		this.internalTriggers.add(trigger);
	} 

	/**
	 * By default an agent is never finished, unless this method is called explicitly
	 * from within a plan. If this method is called then the agent will be killed and 
	 * removed from the platform before it can start a new deliberation cycle.
	 */
	public final void finished(){
		this.finished = true;
	}
	 
	/////////////////////////////////////////
	//// DELIBERATION STEP FUNCTIONALITY ////
	/////////////////////////////////////////
	
	/** Produce an interface that exposes all the required functionalities for deliberation steps. */
	public final DeliberationStepToAgentInterface produceDeliberationInterface(){
		return new DeliberationStepToAgentInterface(this);
	}
	
	/** Obtain and remove the current external triggers. This will return a new 
	 * list of triggers. */
	public final List<Trigger> getAndRemoveExternalTriggers(){ 
		synchronized(this.externalTriggers){
			if(this.externalTriggers.isEmpty()) return Collections.emptyList();
			else {
				List<Trigger> snapshot = new ArrayList<>(this.externalTriggers);
				this.externalTriggers.clear();
				return snapshot;
			} 
			// TODO: if I used snapshot = this.externalTriggers; this.externalTriggers = new ArrayList<>(); then some triggers were lost.
			// 		 Is this because the lock is bound to address that this.externaltriggers points to, and not to the field this.externalTriggers?
		}
	}
	// get internal triggers, no need to synchronize as only the deliberation thread 
	// can add new internal triggers, which is the same thread as the one that calls this method.
	/** Obtain and remove the current internal triggers. This will return a new 
	 * listof triggers. */
	public final List<Trigger> getAndRemoveInternalTriggers(){
		if(this.internalTriggers.isEmpty()) return Collections.emptyList();
		else {
			List<Trigger> snapshot = this.internalTriggers;
			this.internalTriggers = new ArrayList<>();
			return snapshot;
		}
	}
	
	// get goals, returns new list as it should not be possible to add goals outside of adopt goal (similar for dropgoal)
	/** Obtain new list that contains the current goals. Manipulating the returned list 
	 * will not add/remove goals to the agent. The goals itself though are not cloned. */
	public final List<Goal> getGoals(){
		if(this.goals.isEmpty()) return Collections.emptyList();
		else return new ArrayList<>(this.goals);
	}
	
	/** Remove all goals that are achieved given the contexts of the agent. */
	public final void clearAchievedGoals(){  
		if(!this.goals.isEmpty()){
			List<Goal> snapshot = new ArrayList<>(this.goals);
			for(Goal goal : snapshot){
				if(goal.isAchieved(this.contextInterface)){ 
					this.goals.remove(goal);
				}
			} 
		}
	}
	
	/** Obtain and remove the current message triggers. This will return a new 
	 * list of triggers. */
	public final List<Trigger> getAndRemoveMessages(){
		synchronized(this.messages){
			if(this.messages.isEmpty()) return Collections.emptyList();
			else {
				List<Trigger> snapshot = new ArrayList<>(this.messages);
				this.messages.clear();
				return snapshot;
			}
		}
	}
	 
	/** Get the goal plan schemes of the plan scheme base. */
	public final List<PlanScheme> getGoalPlanSchemes(){
		return this.planSchemeBase.getGoalPlanSchemes();
	}

	/** Get the external trigger plan schemes of the plan scheme base. */
	public final List<PlanScheme> getExternalTriggerPlanSchemes(){
		return this.planSchemeBase.getExternalTriggerPlanSchemes();
	}
	
	/** Get the internal trigger plan schemes of the plan scheme base. */
	public final List<PlanScheme> getInternalTriggerPlanSchemes(){
		return this.planSchemeBase.getInternalTriggerPlanSchemes();
	}

	/** Get the message plan schemes of the plan scheme base. */
	public final List<PlanScheme> getMessagePlanSchemes(){
		return this.planSchemeBase.getMessagePlanSchemes();
	}

	/** Get the goal interceptors. */
	public final Iterator<TriggerInterceptor> getGoalInterceptors(){
		return this.goalInterceptors.iterator();
	}
	
	/** Get the external trigger interceptors. */
	public final Iterator<TriggerInterceptor> getExternalTriggerInterceptors(){
		return this.externalTriggerInterceptors.iterator();
	}
	
	/** Get the internal trigger interceptors. */
	public final Iterator<TriggerInterceptor> getInternalTriggerInterceptors(){
		return this.internalTriggerInterceptors.iterator();
	}
	
	/** Get the message interceptors. */
	public final Iterator<TriggerInterceptor> getMessageInterceptors(){
		return this.messageInterceptors.iterator();
	}
	
	/**
	 * Try to apply for a given trigger a given plan scheme. If the plan scheme can 
	 * instantiate given the trigger and the current contexts of the agent, then the 
	 * plan will be inserted into the list of current plans. 
	 * @param trigger Trigger that may trigger the plan scheme. 
	 * @param planScheme Plan scheme to try out.
	 * @return True iff the plan scheme was instantiated. 
	 */
	public final boolean tryApplication(final Trigger trigger, final PlanScheme planScheme){
		Plan result = planScheme.instantiate(trigger, this.contextInterface);
		if(result != null){
			adoptPlan(result);
		}
		return result != null;
	}
	
	/**
	 * Execute a given plan. This method will first check whether the plan has a goal 
	 * and if so, whether that goal is still relevant. In case the plan has a goal and the 
	 * goal is not relevant anymore (because it is not in the list of current goals anymore) 
	 * then the plan will not be executed.
	 */
	public final void executePlan(final Plan plan) throws PlanExecutionError {
		if(plan.goalIsRelevant(planInterface))
			plan.execute(this.planInterface);
	} 
	
	/** Get a new list with the current instantiated plans of the agent.	 */
	public final List<Plan> getPlans(){ 
		if(this.plans.isEmpty()) return Collections.emptyList();
		else return new ArrayList<>(this.plans); 
	}
	
	/** Remove a plan from the list of current plans. */
	public final void removePlan(final Plan plan){
		this.plans.remove(plan);
	}
	
	///////////////////////////////////
	//// KILL SWITCH FUNCTIONALITY ////
	///////////////////////////////////
	/** This will forcibly kill the agent. It may finish its current deliberation cycle
	 * if applicable. It will be killed an removed from the agent platform before the next
	 * cycle executes. All death listeners will be notified.  */
	public final void forceStop(){
		this.forciblyStop = true;
		this.messengerClient.deregister(); // Makes sure no more messages can  be send to this agent
		synchronized(this.deathListeners){
			if(!this.deathListeners.isEmpty()){
				 // TODO: upon executing the following it might be the case that a listener is added whilst the agent is dying. This listener would not be notified.
				for(AgentDeathListener listener : this.deathListeners){
					listener.agentDied(this.agentID);
				}
			} 
		}
	} 
	 
	/////////////////////////////////////////////
	//// DELIBERATION RUNNABLE FUNCTIONALITY ////
	/////////////////////////////////////////////
	/**
	 * Check whether the agent is done with execution.
	 * @return True iff the agent is forcibly stopped or is finished. 
	 */
	public final boolean isDone(){
		return this.forciblyStop || this.finished;
	}
	
	public final void setSelfRescheduler(final SelfRescheduler rescheduler){
		this.rescheduler = rescheduler; 
	}
	
	/**
	 * A check to determine whether the agent should go to sleep.
	 * @return True iff the agent is already sleeping or there are no current plans and triggers.
	 */
	public final boolean checkSleeping(){
		synchronized (this.externalTriggers) {
			synchronized(this.messages){
				synchronized(this.rescheduler){
					if(this.sleep) return true;
					else if(this.plans.size() == 0 &&
					   this.externalTriggers.size() == 0 &&
					   this.internalTriggers.size() == 0 &&
					   this.messages.size() == 0 &&
					   this.goals.size() == 0){
						this.sleep = true;
					}
					return this.sleep; 
				}
			}
		} 
	}
	 
	/** Obtain the agent's ID. */
	public final AgentID getAgentID(){ return this.agentID; }
	
	/** Obtain the agent's deliberation cycle. */
	public final List<DeliberationStep> getDeliberationCycle(){
		return this.deliberationCycle;
	}
}