package algorithms.evaluation;

import algorithms.problem.BaseIndividual;
import algorithms.problem.scheduling.Schedule;

/**
 * Single objective evaluator for duration.
 */
public class SkillOveruseEvaluator<GENE extends Number> extends BaseScheduleEvaluator<GENE> {

  public SkillOveruseEvaluator() {
    super();
  }

  public SkillOveruseEvaluator(BaseIndividual<GENE, Schedule> individual) {
    super(individual);
  }

  /**
   * Duration of the individual
   *
   * @return duration of the individual
   */
  @Override
  public double evaluate() {
    return super.getSkillOveruse();
  }

  @Override
  public BaseScheduleEvaluator<GENE> getCopy(BaseIndividual<GENE, Schedule> individual) {
    return new SkillOveruseEvaluator<>(individual);
  }

  @Override
  public EvaluatorType getType() {
    return EvaluatorType.AVERAGE_CASH_FLOW_DEVIATION_EVALUATOR;
  }

  @Override
  public int getNumObjectives() {
    return 1;
  }

}
