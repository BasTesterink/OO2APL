package oo2apl.plan;
  
import java.util.Collections;
import java.util.List; 
/**
 * The plan scheme base of an agent stores its plan schemes. Currently it is assumed that 
 * the plan scheme base does not change over time. This might be a future expansion to allow 
 * this.
 * @author Bas Testerink
 *
 */
public final class PlanSchemeBase {
	/** Various lists containing the different schemes of the agent. */
	private final List<PlanScheme> goalPlanSchemes,				// Equivalent of PG rules
								   internalTriggerPlanSchemes, 	// Equivalent of PR rules and also PC rules which have no external trigger as head
								   externalTriggerPlanSchemes,	// Equivalent of PC rules that have an external trigger as head
								   messagePlanSchemes;			// Equivalent of PC rules that have a message as head
	
	/**
	 * Creates a plan scheme base with the provided plans. Note that adding/removing a plan scheme from
	 *  a list that is provided as an argument will NOT add/remove that PlanScheme to/from the plan scheme base
	 *  after its creation.
	 * @param goalPlanSchemes
	 * @param internalTriggerPlanSchemes
	 * @param externalTriggerPlanSchemes
	 * @param messagePlanSchemes
	 */
	public PlanSchemeBase(final List<PlanScheme> goalPlanSchemes,
						  final List<PlanScheme> internalTriggerPlanSchemes,
						  final List<PlanScheme> externalTriggerPlanSchemes,
						  final List<PlanScheme> messagePlanSchemes){
		this.goalPlanSchemes = goalPlanSchemes.isEmpty()? Collections.emptyList() : Collections.unmodifiableList(goalPlanSchemes);
		this.internalTriggerPlanSchemes = internalTriggerPlanSchemes.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(internalTriggerPlanSchemes);
		this.externalTriggerPlanSchemes = externalTriggerPlanSchemes.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(externalTriggerPlanSchemes);
		this.messagePlanSchemes = messagePlanSchemes.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(messagePlanSchemes);
	}

	// Return new lists so that deliberation cannot accidentally change the plan scheme lists
	
	public final List<PlanScheme> getGoalPlanSchemes(){ return this.goalPlanSchemes; }
	public final List<PlanScheme> getInternalTriggerPlanSchemes(){ return this.internalTriggerPlanSchemes; }
	public final List<PlanScheme> getExternalTriggerPlanSchemes(){ return this.externalTriggerPlanSchemes; }
	public final List<PlanScheme> getMessagePlanSchemes(){ return this.messagePlanSchemes; }
} 