package oo2apl.plan.builtin;

import oo2apl.agent.PlanToAgentInterface; 
import oo2apl.plan.PlanExecutionError;
/**
 * This interface is used to easily adopt a trigger interceptor. Note that implementing this interface does not 
 * give you access to the <code>setFinished(boolean)</code> method of a plan! Hence if a plan is constructed that 
 * depends on the execution of an implementation of this interface, then it is safest to ensure that this plan is 
 * a <code>RunOncePlan</code>. 
 * 
 * @author Bas Testerink
 * @param <T>
 */
public interface DecoupledPlanBodyInterface <T> {
	/** Implement this function in order to specify how an interceptor must process a given trigger. */
	public void execute(final T trigger, final PlanToAgentInterface planInterface) throws PlanExecutionError;
}
