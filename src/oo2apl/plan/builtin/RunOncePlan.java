package oo2apl.plan.builtin;

import oo2apl.agent.PlanToAgentInterface;
import oo2apl.plan.Plan;
import oo2apl.plan.PlanExecutionError;

/**
 * A plan that is automatically set to finished after it has been executed. 
 * 
 * @author Bas Testerink
 */
public abstract class RunOncePlan extends Plan {
 
	public final void execute(final PlanToAgentInterface planInterface) throws PlanExecutionError {
		executeOnce(planInterface);
		setFinished(true);
	}
	
	public abstract void executeOnce(final PlanToAgentInterface planInterface) throws PlanExecutionError; 
}