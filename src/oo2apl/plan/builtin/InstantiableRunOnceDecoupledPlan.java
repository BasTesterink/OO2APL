package oo2apl.plan.builtin;

import oo2apl.agent.PlanToAgentInterface;
import oo2apl.agent.Trigger;
import oo2apl.plan.PlanExecutionError;
/**
 * This class is a decoupled plan which executes only once and can be constructed with a DecoupledPlanBodyInterface. It is used by the PlanToAgentInterface 
 * built-in functionalities. 
 * 
 * @author Bas Testerink
 * @param <T>
 */
public final class InstantiableRunOnceDecoupledPlan<T extends Trigger> extends DecoupledPlan {
	private final DecoupledPlanBodyInterface<T> body;
	
	public InstantiableRunOnceDecoupledPlan(final DecoupledPlanBodyInterface<T> body){
		this.body = body;
	}
	
	public final void execute(final Trigger trigger, final PlanToAgentInterface planInterface) throws PlanExecutionError {
		try {
			@SuppressWarnings("unchecked")
			T cast = (T) trigger;  
			this.body.execute(cast, planInterface);
			this.setFinished(true);
		} catch(ClassCastException e){
			e.printStackTrace();
			throw new PlanExecutionError(); // TODO: design and implement a proper plan execution error that hints that the selector should ensure the correct type
		}
	}
	
}
