package oo2apl.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import oo2apl.defaults.deliberationsteps.ApplyExternalTriggerPlanSchemes;
import oo2apl.defaults.deliberationsteps.ApplyGoalPlanSchemes;
import oo2apl.defaults.deliberationsteps.ApplyInternalTriggerPlanSchemes;
import oo2apl.defaults.deliberationsteps.ApplyMessagePlanSchemes;
import oo2apl.defaults.deliberationsteps.ExecutePlans;
import oo2apl.deliberation.DeliberationStep;
import oo2apl.plan.Plan;
import oo2apl.plan.PlanSchemeBase;
import oo2apl.plan.PlanSchemeBaseArguments;
/**
 * An agent component factory produces the relevant components from which an agent is build.
 * Specifically it produces the context container, plan scheme base, deliberation cycle and agent
 * identity. 
 * 
 * @author Bas Testerink
 */
public interface AgentComponentFactory { 
	/** Get  the type of agents for which this factory can produce components. Should be unique for each factory. */
	public AgentType getAgentType();
	
	/** Produce a context container given the provided context arguments. */
	public ContextContainer produceContextContainer(final ContextArguments contextArgs);
	
	/** Produce a plan scheme base given the provided plan scheme base arguments. */
	public PlanSchemeBase producePlanSchemeBase(final PlanSchemeBaseArguments planSchemeBaseArgs);
	
	/** Produce the deliberation cycle of the agent. The provided interface can be used by deliberation steps to perform their functionalities on the agent. 
	 * The default implementation is that the 2APL deliberation cycle is used: ApplyGoalPlanSchemes -> ApplyExternalTriggerPlanSchemes -> 
	 *  ApplyInternalTriggerPlanSchemes -> ApplyMessagePlanSchemes -> ExecutePlans. */
	public default List<DeliberationStep> produceDeliberationCycle(final DeliberationStepToAgentInterface deliberationInterface){
		// Produces the default 2APL deliberation cycle.
		List<DeliberationStep> deliberationCycle = new ArrayList<>();
		deliberationCycle.add(new ApplyGoalPlanSchemes(deliberationInterface));
		deliberationCycle.add(new ApplyExternalTriggerPlanSchemes(deliberationInterface));
		deliberationCycle.add(new ApplyInternalTriggerPlanSchemes(deliberationInterface));
		deliberationCycle.add(new ApplyMessagePlanSchemes(deliberationInterface));
		deliberationCycle.add(new ExecutePlans(deliberationInterface));
		return deliberationCycle;
	} 
	
	/** Produce the initial plans of the agent. These will be executed upon the first deliberation cycle. */
	public default List<Plan> produceInitialPlans(){
		return Collections.emptyList();
	}
}