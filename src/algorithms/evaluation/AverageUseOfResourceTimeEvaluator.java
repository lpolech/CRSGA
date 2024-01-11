package algorithms.evaluation;

import algorithms.problem.BaseIndividual;
import algorithms.problem.scheduling.Schedule;

/**
 * Single objective evaluator for duration.
 */
public class AverageUseOfResourceTimeEvaluator<GENE extends Number> extends BaseScheduleEvaluator<GENE> {

  public AverageUseOfResourceTimeEvaluator() {
    super();
  }

  public AverageUseOfResourceTimeEvaluator(BaseIndividual<GENE, Schedule> individual) {
    super(individual);
  }

  /**
   * Duration of the individual
   *
   * @return duration of the individual
   */
  @Override
  public double evaluate() {
    return super.getAverageUseOfResourceTime();
  }

  @Override
  public BaseScheduleEvaluator<GENE> getCopy(BaseIndividual<GENE, Schedule> individual) {
    return new AverageUseOfResourceTimeEvaluator<>(individual);
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
