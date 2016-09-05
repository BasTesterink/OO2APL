package oo2apl.plan.builtin;

import oo2apl.agent.PlanToAgentInterface;
import oo2apl.agent.Trigger;
import oo2apl.plan.Plan;
import oo2apl.plan.PlanExecutionError;
/**
 * A decoupled plan is a plan where the execute method contains a trigger. The standard use of this class is that the trigger which caused the 
 * plan scheme that instantiated the plan is the trigger which is provided in the execute method at runtime. 
 * 
 * @author Bas Testerink
 */
public abstract class DecoupledPlan extends Plan { 
	/** {@inheritDoc} */
	public final void execute(final PlanToAgentInterface planInterface) throws PlanExecutionError {}
	public abstract void execute(final Trigger trigger, final PlanToAgentInterface planInterface) throws PlanExecutionError;
}
