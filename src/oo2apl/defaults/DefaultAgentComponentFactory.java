package oo2apl.defaults;

import java.util.ArrayList;
import java.util.List;

import oo2apl.agent.AgentComponentFactory;
import oo2apl.agent.AgentID;
import oo2apl.agent.DeliberationStepToAgentInterface;
import oo2apl.defaults.deliberationsteps.ApplyExternalTriggerPlanSchemes;
import oo2apl.defaults.deliberationsteps.ApplyGoalPlanSchemes;
import oo2apl.defaults.deliberationsteps.ApplyInternalTriggerPlanSchemes;
import oo2apl.defaults.deliberationsteps.ApplyMessagePlanSchemes;
import oo2apl.defaults.deliberationsteps.ExecutePlans;
import oo2apl.deliberation.DeliberationStep;

/**
 * This default agent component factory takes care of the generation of agent id's and 
 * deliberation cycles. The cycle that is used is the standard 2APL cycle given by the 
 * default deliberation steps. The order is: 1) apply goal plan schemes, 2) apply external
 * trigger plan schemes, 3) apply internal trigger plan schemes, 4) apply message plan schemes 
 * and 5) execute any current plans. 
 * 
 * The id of an agent is a default agent id where the id number is the number of agent id's 
 * that were produced in the past by this factory. So the first agent has id number 0 and the 
 * tenth number 9. 
 * 
 * Note: do not use more than 1 default agent component factory for the same agent type. 
 * Doing so will result in non-unique agent identities, because each maintains its own 
 * counter.
 * 
 * @author Bas Testerink
 */
public abstract class DefaultAgentComponentFactory implements AgentComponentFactory {
	/** How many agent id's were produced. */
	private long instanceCount = -1; 
	
	/** Produces the default 2APL deliberation cycle.  */
	public final List<DeliberationStep> produceDeliberationCycle(final DeliberationStepToAgentInterface deliberationInterface){
		List<DeliberationStep> deliberationCycle = new ArrayList<>();
		deliberationCycle.add(new ApplyGoalPlanSchemes(deliberationInterface));
		deliberationCycle.add(new ApplyExternalTriggerPlanSchemes(deliberationInterface));
		deliberationCycle.add(new ApplyInternalTriggerPlanSchemes(deliberationInterface));
		deliberationCycle.add(new ApplyMessagePlanSchemes(deliberationInterface));
		deliberationCycle.add(new ExecutePlans(deliberationInterface));
		return deliberationCycle;
	}
	
	/** Produce a unique agent identity. Will return a default agent id where the 
	 * id number is the amount of id's that we produced in the past.  */
	public final AgentID produceNextAgentID(){
		instanceCount++;
		return new DefaultAgentID(instanceCount, getAgentType());
	}
}