package oo2apl.plan.builtin;

import oo2apl.agent.Trigger;
/**
 * This class is used by the PlanToAgentInterface class in order to obtain the return value of a Future. 
 * @author Bas Testerink
 */
public class InternalTriggerReturnValue<V> implements Trigger {
	private V value; 
	public final void setValue(V value){ this.value = value; }
	public final V getValue(){ return this.value; }
}
