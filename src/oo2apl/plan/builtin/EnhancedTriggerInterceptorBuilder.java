package oo2apl.plan.builtin;

import java.util.function.Predicate; 
import oo2apl.agent.PlanToAgentInterface;
import oo2apl.agent.Trigger; 
import oo2apl.plan.PlanExecutionError; 
/**
 * This is an auxilary class to help create trigger interceptors. 
 * @author Bas Testerink
 */
public final class EnhancedTriggerInterceptorBuilder {
	/** The selector that inspects triggers and may fire the interceptor. */
	private Predicate<Trigger> selector; 
	/** The plan to execute. */
	private DecoupledPlan plan;
	/** Whether trigger is consumed after the interceptor has fired, and whether the plan is finished after executing once. */
	private boolean consuming, forceRunOnce; 

	public EnhancedTriggerInterceptorBuilder(){
		init();
	}

	private void init(){
		// Initially the interceptor will accept any trigger but not consume it.
		// Also, the initially set plan simply does not do anything.
		this.selector = (Trigger t) -> {return true;}; 
		this.consuming = false;
		this.forceRunOnce = false;
		this.plan = new DecoupledPlan() { 
			public void execute(Trigger trigger, PlanToAgentInterface planInterface) throws PlanExecutionError {setFinished(true);}
		};
	} 
	
	/** Set whether trigger is consumed after the interceptor has fired. */
	public final EnhancedTriggerInterceptorBuilder setConsuming(final boolean b){
		this.consuming = b;
		return this;
	}
	
	/** Set whether the plan is finished after executing once. */
	public final EnhancedTriggerInterceptorBuilder setForceRunOnce(final boolean b){
		this.forceRunOnce = b;
		return this;
	}

	/** Set the selector that inspects triggers and may fire the interceptor. */
	public final EnhancedTriggerInterceptorBuilder setSelector(final Predicate<Trigger> selector){
		this.selector = selector;
		return this;
	}

	/** Set the plan to return if this interceptor is fired. */
	public final EnhancedTriggerInterceptorBuilder setPlan(final DecoupledPlan plan){
		this.plan = plan;
		return this;
	}

	/** Create and return the trigger interceptor. This will also reset all the values of this builder. */
	public final EnhancedTriggerInterceptor build(){
		DecoupledPlan chosenPlan;
		if(this.forceRunOnce){ // Force that the plan is one-shot
			DecoupledPlanBodyInterface<Trigger> body = (Trigger trigger, PlanToAgentInterface planInterface) -> {this.plan.execute(trigger, planInterface);};
			chosenPlan = new InstantiableRunOnceDecoupledPlan<Trigger>(body);
		} else chosenPlan = this.plan;
		EnhancedTriggerInterceptor interceptor = new EnhancedTriggerInterceptor(this.consuming, this.selector, chosenPlan); 
		return interceptor;
	} 
}
