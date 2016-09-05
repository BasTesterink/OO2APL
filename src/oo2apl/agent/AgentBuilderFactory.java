package oo2apl.agent; 

import java.util.Collections;
import java.util.List;

import oo2apl.defaults.DefaultAgentComponentFactory;
import oo2apl.plan.Plan;
import oo2apl.plan.PlanSchemeBase;
import oo2apl.plan.PlanSchemeBaseArguments;
/**
 * This is a built-in factory that allows a developer to construct agents using the Builder 
 * pattern. The factory is loaded by an <code>AgentBuilder</code> when used. There is no need to 
 * extend this class in order to use it.
 * 
 * @author Bas Testerink
 *
 */
public final class AgentBuilderFactory extends DefaultAgentComponentFactory {
	public static final AgentType AGENTTYPE = new AgentType(){};
	
	private ContextContainer contextContainer; // The context container of the next agent instantiation
	private PlanSchemeBase planSchemeBase; // The plan scheme base of the next agent instantiation
	private List<Plan> initialPlans; // The initial plans of the next agent
	
	public final AgentType getAgentType() { 
		return AGENTTYPE;
	}

	/** Returns the context container as provided by the loaded builder. */
	public final ContextContainer produceContextContainer(final ContextArguments contextArgs) {
		return this.contextContainer;
	}

	/** Returns the plan scheme base as provided by the loaded builder. */
	public final PlanSchemeBase producePlanSchemeBase(final PlanSchemeBaseArguments planSchemeBaseArgs) {
		return this.planSchemeBase;
	}
	
	/** Returns the initial plans of the loaded builder. */
	public final List<Plan> produceInitialPlans(){
		return this.initialPlans == null ? Collections.emptyList() : this.initialPlans;
	}
	
	/** Load a builder so that its components will be returned. */
	public final void setBuilder(final AgentBuilder builder){
		this.contextContainer = builder.buildContextContainer();
		this.planSchemeBase = builder.buildPlanSchemeBase();
		this.initialPlans = builder.getInitialPlans();
	} 
}
