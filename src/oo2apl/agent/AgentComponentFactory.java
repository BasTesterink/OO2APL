package oo2apl.agent;

import java.util.List;

import oo2apl.deliberation.DeliberationStep;
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
	
	/** Produce the deliberation cycle of the agent. The provided interface can be used by deliberation steps to perform their functionalities on the agent. */
	public List<DeliberationStep> produceDeliberationCycle(final DeliberationStepToAgentInterface deliberationInterface);
	
	/** Produce an agent identifier, should never return the same identity twice. */
	public AgentID produceNextAgentID();
}