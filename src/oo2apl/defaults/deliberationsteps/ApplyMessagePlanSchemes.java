package oo2apl.defaults.deliberationsteps;

import java.util.List;

import oo2apl.agent.DeliberationStepToAgentInterface;
import oo2apl.agent.Trigger;
import oo2apl.deliberation.DeliberationStepException;
import oo2apl.plan.PlanScheme;
/**
 * Step that applies the message plan schemes to the messages.
 * @author Bas Testerink
 */
public final class ApplyMessagePlanSchemes extends DefaultDeliberationStep { 
	
	public  ApplyMessagePlanSchemes(final DeliberationStepToAgentInterface deliberationInterface){
		super(deliberationInterface);
	}
	
	/** Simply grab the messages and message plan schemes and try their application. */
	public final void execute() throws DeliberationStepException{
		List<Trigger> triggers = super.deliberationInterface.getAndRemoveMessages(); 
		List<PlanScheme> planSchemes = super.deliberationInterface.getMessagePlanSchemes();
		super.applyPlanSchemes(triggers, planSchemes);
	}
}