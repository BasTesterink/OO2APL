package oo2apl.plan.builtin;

import java.util.ArrayList; 
import java.util.List;
import java.util.function.Predicate;

import oo2apl.agent.AgentContextInterface;
import oo2apl.agent.PlanToAgentInterface;
import oo2apl.agent.Trigger;
import oo2apl.plan.Plan;
import oo2apl.plan.PlanExecutionError;
import oo2apl.plan.TriggerInterceptor;
/**
 * An enhanced trigger interceptor is an extension of a trigger interceptor. The extension consists of the 
 * capability to add other interceptors which ought to be removed when this interceptor fires. The instantiate 
 * method of an enhanced trigger interceptor does not make use of the context of the agent, instead, the programmer 
 * has to provide a predicate over Triggers which will be used to check whether the interceptor fires.
 * 
 * The plan of the interceptor is also given by the programmer, though it is wrapped in another plan that causes the 
 * deletion of other interceptors, if applicable. 
 * 
 * 
 * This class is used by the PlanToAgentInterface in order to implement for instance waitForX-functionalities.
 * @author Bas Testerink
 *
 */
public final class EnhancedTriggerInterceptor extends TriggerInterceptor {
	public final List<TriggerInterceptor> goalInterceptorsToRemove,
	  									  externalTriggerInterceptorsToRemove,
	  									  internalTriggerInterceptorsToRemove,
	  									  messageInterceptorsToRemove;
	private final Predicate<Trigger> selector;
	private final DecoupledPlan plan;
	
	public EnhancedTriggerInterceptor(final boolean consumesTrigger, final Predicate<Trigger> selector, DecoupledPlan plan){
		super(consumesTrigger);
		this.selector = selector; 
		this.plan = plan;
		this.goalInterceptorsToRemove = new ArrayList<TriggerInterceptor>();
		this.externalTriggerInterceptorsToRemove = new ArrayList<TriggerInterceptor>();
		this.internalTriggerInterceptorsToRemove = new ArrayList<TriggerInterceptor>();
		this.messageInterceptorsToRemove = new ArrayList<TriggerInterceptor>();
	}
	
	public final Plan instantiate(final Trigger trigger, final AgentContextInterface contextInterface){
		if(this.selector.test(trigger)){
			return new ExtendedInterceptorPlan(this.plan, trigger, 
					this.goalInterceptorsToRemove, 
					this.externalTriggerInterceptorsToRemove, 
					this.internalTriggerInterceptorsToRemove, 
					this.messageInterceptorsToRemove);
		}
		return Plan.UNINSTANTIATED;
	}

	/** If this interceptor is fired, then the provided interceptor is removed from the list of goal interceptors when this interceptor's plan is executed. */
	public final void addGoalInterceptorToRemove(final TriggerInterceptor interceptor){
		this.goalInterceptorsToRemove.add(interceptor);
	}

	/** If this interceptor is fired, then the provided interceptor is removed from the list of external trigger interceptors when this interceptor's plan is executed. */
	public final void addExternalTriggerInterceptorToRemove(final TriggerInterceptor interceptor){
		this.externalTriggerInterceptorsToRemove.add(interceptor);
	}

	/** If this interceptor is fired, then the provided interceptor is removed from the list of message interceptors when this interceptor's plan is executed. */
	public final void addInternalTriggerInterceptorToRemove(final TriggerInterceptor interceptor){
		this.internalTriggerInterceptorsToRemove.add(interceptor);
	}

	/** If this interceptor is fired, then the provided interceptor is removed from the list of internal trigger interceptors when this interceptor's plan is executed. */
	public final void addMessageInterceptorToRemove(final TriggerInterceptor interceptor){
		this.messageInterceptorsToRemove.add(interceptor);
	}
	
	private final class ExtendedInterceptorPlan extends Plan{
		public final List<TriggerInterceptor> goalInterceptorsToRemove,
											  externalTriggerInterceptorsToRemove,
											  internalTriggerInterceptorsToRemove,
											  messageInterceptorsToRemove;
		
		private final DecoupledPlan plan;
		private final Trigger trigger;
		private boolean firstExecute;
		
		public ExtendedInterceptorPlan(final DecoupledPlan plan, final Trigger trigger,
				final List<TriggerInterceptor> goalInterceptorsToRemove,
				final List<TriggerInterceptor> externalTriggerInterceptorsToRemove,
				final List<TriggerInterceptor> internalTriggerInterceptorsToRemove,
				final List<TriggerInterceptor> messageInterceptorsToRemove){
			this.plan = plan;
			this.trigger = trigger; 
			this.goalInterceptorsToRemove = goalInterceptorsToRemove;
			this.externalTriggerInterceptorsToRemove = externalTriggerInterceptorsToRemove;
			this.internalTriggerInterceptorsToRemove = internalTriggerInterceptorsToRemove;
			this.messageInterceptorsToRemove = messageInterceptorsToRemove;
			this.firstExecute = false;
		}
		
		public final void execute(final PlanToAgentInterface planInterface) throws PlanExecutionError { 
			// Only execute the removal of other interceptors the first time that this plan is executed
			if(this.firstExecute){
				for(TriggerInterceptor interceptor : this.goalInterceptorsToRemove)
					planInterface.removeGoalInterceptor(interceptor);
				for(TriggerInterceptor interceptor : this.externalTriggerInterceptorsToRemove)
					planInterface.removeExternalTriggerInterceptor(interceptor);
				for(TriggerInterceptor interceptor : this.internalTriggerInterceptorsToRemove)
					planInterface.removeInternalTriggerInterceptor(interceptor);
				for(TriggerInterceptor interceptor : this.messageInterceptorsToRemove)
					planInterface.removeMessageInterceptor(interceptor);
				this.firstExecute = false;
			}
			// Then proceed as if this plan is the provided plan when this interceptor was created
			this.plan.execute(this.trigger, planInterface);
			setFinished(this.plan.isFinished());
		}
		
	}
}