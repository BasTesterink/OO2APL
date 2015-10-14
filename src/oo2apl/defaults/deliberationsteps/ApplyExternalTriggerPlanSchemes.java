package oo2apl.defaults.deliberationsteps; 
 
import java.util.List;

import oo2apl.agent.DeliberationStepToAgentInterface;
import oo2apl.agent.Trigger;
import oo2apl.deliberation.DeliberationStepException;
import oo2apl.plan.PlanScheme;
/**
 * Step that applies the external trigger plan schemes to the external triggers.
 * @author Bas Testerink
 */
public final class ApplyExternalTriggerPlanSchemes extends DefaultDeliberationStep { 
	
	public  ApplyExternalTriggerPlanSchemes(final DeliberationStepToAgentInterface deliberationInterface){
		super(deliberationInterface);
	}
	
	/** Simply grab the external triggers and relevant plan schemes and try their application. */
	public final void execute() throws DeliberationStepException{
		List<Trigger> triggers = super.deliberationInterface.getAndRemoveExternalTriggers(); 
		List<PlanScheme> planSchemes = super.deliberationInterface.getExternalTriggerPlanSchemes();
		super.applyPlanSchemes(triggers, planSchemes);
	}
}
