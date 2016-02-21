package oo2apl.defaults.deliberationsteps;
 

import oo2apl.agent.DeliberationStepToAgentInterface;
import oo2apl.deliberation.DeliberationStepException;
import oo2apl.plan.Plan;
import oo2apl.plan.PlanExecutionError;
/**
 * Deliberation step for executing the current plans of the agent.
 * @author Bas Testerink
 */
public final class ExecutePlans extends DefaultDeliberationStep { 
	
	public  ExecutePlans(final DeliberationStepToAgentInterface deliberationInterface){
		super(deliberationInterface);
	}
	
	/** This steps executes by going through each of the agent's plans. If the plan is finished 
	 * after its execution, then it is removed. If an error occurs, then a plan execution error
	 * will be inserted as an internal trigger. */
	public final void execute() throws DeliberationStepException {
		for(Plan plan : super.deliberationInterface.getPlans()){ 
			try {
				super.deliberationInterface.executePlan(plan);
				if(plan.isFinished())
					super.deliberationInterface.removePlan(plan);
			} catch(PlanExecutionError executionError){ 
				// NOTE: if a plan has an execution error, and a goal is being pursued by the plan, then the goal still is 
				// flagged as being pursued. Therefore it is important to ALWAYS have repair plan schemes for failed goal plan schemes.
				super.deliberationInterface.removePlan(plan); // Remove plan from execution
				super.deliberationInterface.addPlanExecutionError(executionError); // Add the error
			}
		}
	}
}
