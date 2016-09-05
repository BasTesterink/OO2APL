package oo2apl.agent;

import java.util.ArrayList;
import java.util.List;

import oo2apl.plan.Plan;
import oo2apl.plan.PlanScheme;
import oo2apl.plan.PlanSchemeBase;
import oo2apl.plan.builtin.FunctionalPlanScheme;
import oo2apl.plan.builtin.FunctionalPlanSchemeInterface;
/**
 * Agent builders provide an alternative to agent component factories and regularly provide more concise code. 
 * 
 * An agent builder can be used to gather relevant data to instantiate an agent such as its plan schemes and 
 * contexts. It will produce for the AgentBuilderFactory the correct ContextContainer and PlanSchemeBase. These 
 * are not exposed to classes outside of the agent package in order to prevent misuse. 
 * 
 * To construct an agent with the builder use the AdminToPlatform interface and its newAgent method. After the agent 
 * is created it will not be affected by any additional changes to the builder.
 * 
 * @author Bas Testerink
 */
public class AgentBuilder {
	private final List<PlanScheme> goalPlanSchemes, internalTriggerPlanSchemes, externalTriggerPlanSchemes, messagePlanSchemes;
	private final List<Context> contexts;
	private final List<Plan> initialPlans;
	
	public AgentBuilder(){
		this.goalPlanSchemes = new ArrayList<>();
		this.internalTriggerPlanSchemes = new ArrayList<>();
		this.externalTriggerPlanSchemes = new ArrayList<>();
		this.messagePlanSchemes = new ArrayList<>();
		this.contexts = new ArrayList<>();
		this.initialPlans = new ArrayList<>();
	}
	
	/** Builds the plan scheme base. This is intentionally package-only so that a programmer cannot accidentally mess with the plan scheme base. */
	final PlanSchemeBase buildPlanSchemeBase(){
		return new PlanSchemeBase(this.goalPlanSchemes, this.internalTriggerPlanSchemes, this.externalTriggerPlanSchemes, this.messagePlanSchemes);
	}

	/** Builds the context container. This is intentionally package-only so that a programmer cannot accidentally mess with the container. */
	final ContextContainer buildContextContainer(){
		ContextContainer container = new ContextContainer();
		for(Context context : this.contexts)
			container.addContext(context);
		return container;
	}
	
	/** Returns a list of plans that will be executed upon the agent's first deliberation cycle. */
	final List<Plan> getInitialPlans(){
		return new ArrayList<>(this.initialPlans); // Ensure that no further additions will affect the the agent after creation
	}
	
	// Getters are made package-only in order to allow one builder to absorb another
	final List<PlanScheme> getExternalTriggerPlanSchemes(){ return this.externalTriggerPlanSchemes; }
	final List<PlanScheme> getInternalTriggerPlanSchemes(){ return this.internalTriggerPlanSchemes; }
	final List<PlanScheme> getMessagePlanSchemes(){ return this.messagePlanSchemes; }
	final List<PlanScheme> getGoalPlanSchemes(){ return this.goalPlanSchemes; }
	final List<Context> getContexts(){ return this.contexts; }
	
	// Filling the builder
	/** Add a plan scheme that processes external triggers. */
	public final AgentBuilder addExternalTriggerPlanScheme(final PlanScheme planScheme){ this.externalTriggerPlanSchemes.add(planScheme); return this; }
	/** Add a plan scheme that processes internal triggers. */
	public final AgentBuilder addInternalTriggerPlanScheme(final PlanScheme planScheme){ this.internalTriggerPlanSchemes.add(planScheme); return this; }
	/** Add a plan scheme that processes messages. */
	public final AgentBuilder addMessagePlanScheme(final PlanScheme planScheme){ this.messagePlanSchemes.add(planScheme); return this; }
	/** Add a plan scheme that try to achieve goals. */
	public final AgentBuilder addGoalPlanScheme(final PlanScheme planScheme){ this.goalPlanSchemes.add(planScheme); return this; }
	/** Add a plan scheme that processes external triggers. */
	public final AgentBuilder addExternalTriggerPlanScheme(final FunctionalPlanSchemeInterface planScheme){ this.externalTriggerPlanSchemes.add(new FunctionalPlanScheme(planScheme)); return this; }
	/** Add a plan scheme that processes internal triggers. */
	public final AgentBuilder addInternalTriggerPlanScheme(final FunctionalPlanSchemeInterface planScheme){ this.internalTriggerPlanSchemes.add(new FunctionalPlanScheme(planScheme)); return this; }
	/** Add a plan scheme that processes messages. */
	public final AgentBuilder addMessagePlanScheme(final FunctionalPlanSchemeInterface planScheme){ this.messagePlanSchemes.add(new FunctionalPlanScheme(planScheme)); return this; }
	/** Add a plan scheme that try to achieve goals. */
	public final AgentBuilder addGoalPlanScheme(final FunctionalPlanSchemeInterface planScheme){ this.goalPlanSchemes.add(new FunctionalPlanScheme(planScheme)); return this; }
	/** Add a context that is used for decision making and plan execution. */
	public final AgentBuilder addContext(final Context context){ this.contexts.add(context); return this; }
	/** Add a plan that will be executed in the first deliberation cycle. */
	public final AgentBuilder addInitialPlan(final Plan plan){ this.initialPlans.add(plan); return this; }
	 
	/** Copies the planschemes, contexts and initial plan of another 
	 * builder into this builder. This can be used to for instance include a 
	 * builder that represents a premade set of plan schemes, etc, that forms a 
	 * coherent capability. */
	public final AgentBuilder include(final AgentBuilder builder){
		this.externalTriggerPlanSchemes.addAll(builder.getExternalTriggerPlanSchemes());
		this.internalTriggerPlanSchemes.addAll(builder.getInternalTriggerPlanSchemes());
		this.messagePlanSchemes.addAll(builder.getMessagePlanSchemes());
		this.goalPlanSchemes.addAll(builder.getGoalPlanSchemes());
		this.initialPlans.addAll(builder.getInitialPlans());
		this.contexts.addAll(builder.getContexts());
		return this;
	}
} 