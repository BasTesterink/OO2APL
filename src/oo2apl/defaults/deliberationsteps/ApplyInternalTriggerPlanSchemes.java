package oo2apl.defaults.deliberationsteps;

import java.util.List;

import oo2apl.agent.DeliberationStepToAgentInterface;
import oo2apl.agent.Trigger;
import oo2apl.deliberation.DeliberationStepException;
import oo2apl.plan.PlanScheme;
/**
 * Step that applies the internal trigger plan schemes to the internal triggers.
 * @author Bas Testerink
 */
public final class ApplyInternalTriggerPlanSchemes extends DefaultDeliberationStep { 
	
	public  ApplyInternalTriggerPlanSchemes(final DeliberationStepToAgentInterface deliberationInterface){
		super(deliberationInterface);
	}
	
	/** Simply grab the internal triggers and relevant plan schemes and try their application. */
	public final void execute() throws DeliberationStepException{
		List<Trigger> triggers = super.deliberationInterface.getAndRemoveInternalTriggers(); 
		List<PlanScheme> planSchemes = super.deliberationInterface.getInternalTriggerPlanSchemes();
		super.applyPlanSchemes(triggers, planSchemes);
	}
}