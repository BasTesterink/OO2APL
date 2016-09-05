package oo2apl.plan.builtin;

import oo2apl.agent.AgentContextInterface;
import oo2apl.agent.PlanToAgentInterface;
import oo2apl.agent.Trigger;
import oo2apl.plan.Plan;
import oo2apl.plan.PlanExecutionError;
import oo2apl.plan.PlanScheme;
/**
 * A premade plan scheme to make code more concise when developing an agent. 
 * @author Bas Testerink
 */
public final class FunctionalPlanScheme implements PlanScheme {
	private final FunctionalPlanSchemeInterface myInterface;
	
	public FunctionalPlanScheme(final FunctionalPlanSchemeInterface myInterface){
		this.myInterface = myInterface;
	}
	
	public final Plan instantiate(final Trigger trigger, final AgentContextInterface contextInterface){ 
		SubPlanInterface plan = this.myInterface.getPlan(trigger, contextInterface);
		if(plan == SubPlanInterface.UNINSTANTIATED) return Plan.UNINSTANTIATED;
		else return new RunOncePlan() {
			public final void executeOnce(final PlanToAgentInterface planInterface)
					throws PlanExecutionError {
				plan.execute(planInterface);
			}
		};
	}

}
