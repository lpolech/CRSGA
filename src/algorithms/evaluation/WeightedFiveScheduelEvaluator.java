package algorithms.evaluation;

import algorithms.problem.BaseIndividual;
import algorithms.problem.scheduling.Schedule;

import java.util.Arrays;

public class WeightedFiveScheduelEvaluator<GENE extends Number> extends FiveScheduleEvaluator<GENE> {

  private double evalRates[];

  public WeightedFiveScheduelEvaluator() {
    super();
    evalRates = new double[this.getNumObjectives()];
  }

  public WeightedFiveScheduelEvaluator(BaseIndividual<GENE, Schedule> individual, double[] evalRates) {
    super(individual);
    setMaxValues();
    this.evalRates = evalRates;
  }

  @Override
  public double evaluate() {
    if (Arrays.stream(evalRates).anyMatch(rate -> rate < 0.0d || rate > 1.0d)) {
      throw new IllegalArgumentException(
          "Cannot provide the evalRate smaller than 0 or bigger than 1!");
    }
    double evalValue = 0.0d;
    double[] normalObjectives = this.getNormalObjectives();
    for (int i = 0; i < this.getNumObjectives(); ++i) {
      evalValue += normalObjectives[i] * evalRates[i];
    }
    return evalValue;
  }

  public double[] getEvalRates() {
    return evalRates;
  }

  public void setEvalRates(double[] evalRates) {
    this.evalRates = evalRates;
  }

  @Override
  public FiveScheduleEvaluator getCopy(BaseIndividual<GENE, Schedule> individual) {
    return new WeightedFiveScheduelEvaluator<>(individual, evalRates);
  }

  @Override
  public EvaluatorType getType() {
    return EvaluatorType.WEIGHTED_FIVE_SCHEDULE_EVALUATOR;
  }

}
