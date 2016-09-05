package oo2apl.plan.builtin;

import oo2apl.agent.AgentContextInterface;
import oo2apl.agent.Trigger;
/**
 *	An interface for creating FunctionalPlanSchemes. 
 *  @author Bas Testerink
 */
public interface FunctionalPlanSchemeInterface {
	/** Get the plan for a given trigger and context interface. Return SubPlanInterface.UNINSTANTIATED if the plan did not fire. */
	public SubPlanInterface getPlan(Trigger trigger, AgentContextInterface contextInterface);
}
