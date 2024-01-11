package algorithms.evaluation;

import algorithms.problem.BaseIndividual;
import algorithms.problem.scheduling.Schedule;

public class BiScheduleEvaluator<GENE extends Number> extends BaseScheduleEvaluator<GENE> {

  public BiScheduleEvaluator() {
    super();
  }

  public BiScheduleEvaluator(BaseIndividual<GENE, Schedule> individual) {
    super(individual);
    setMaxValues();
  }

  @Override
  public double evaluate() {
    return 0.0d;
  }

  @Override
  public BiScheduleEvaluator getCopy(BaseIndividual<GENE, Schedule> individual) {
    return new BiScheduleEvaluator<>(individual);
  }

  @Override
  public EvaluatorType getType() {
    return EvaluatorType.BASE_SCHEDULE_EVALUATOR;
  }

  @Override
  public int getNumObjectives() {
    return 2;
  }

}
