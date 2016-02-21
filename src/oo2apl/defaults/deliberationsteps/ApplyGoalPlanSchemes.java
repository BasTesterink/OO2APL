package oo2apl.defaults.deliberationsteps;

import java.util.List;

import oo2apl.agent.DeliberationStepToAgentInterface; 
import oo2apl.agent.Trigger;
import oo2apl.deliberation.DeliberationStepException;
import oo2apl.plan.PlanScheme;
/**
 * Step that applies the goal plan schemes to the current goals.
 * @author Bas Testerink
 */

public final class ApplyGoalPlanSchemes extends DefaultDeliberationStep { 
	
	public  ApplyGoalPlanSchemes(final DeliberationStepToAgentInterface deliberationInterface){
		super(deliberationInterface);
	}
	
	/** First clears all achieved goals and then grabs the goals and goal plan schemes and tries to apply the plan schemes. */
	public final void execute() throws DeliberationStepException{
		super.deliberationInterface.clearAchievedGoals();
		List<? extends Trigger> triggers = super.deliberationInterface.getGoals(); 
		super.applyTriggerInterceptors(triggers, super.deliberationInterface.getGoalInterceptors());
		List<PlanScheme> planSchemes = super.deliberationInterface.getGoalPlanSchemes();
		super.applyPlanSchemes(triggers, planSchemes);
	}
} 