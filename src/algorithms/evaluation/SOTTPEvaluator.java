package algorithms.evaluation;

import algorithms.problem.BaseIndividual;
import algorithms.problem.TTP;

/**
 * Evaluator for single objective Travelling Salesman Problem
 */
public class SOTTPEvaluator<GENE extends Number> extends BaseTTPEvaluator<GENE> {

  public SOTTPEvaluator() { }

  public SOTTPEvaluator(BaseIndividual<GENE, TTP> individual) {
    super(individual);
  }

  @Override
  public double evaluate() {
    return getNormalObjectives()[0];
  }

  @Override
  public BaseEvaluator<GENE, TTP> getCopy(BaseIndividual<GENE, TTP> individual) {
    return new SOTTPEvaluator<>(individual);
  }

  @Override
  public double[] getObjectives() {
    double[] objectives = new double[1];
    TTP problem = individual.getProblem();

    objectives[0] = getProfit() - (problem.getRentingRatio() * problem.getTravellingTime());
    objectives[0] = problem.getMaxProfit() - objectives[0];

    return objectives;
  }

  @Override
  public double[] getNormalObjectives() {
    double[] objectives = getObjectives();
    objectives[0] /= individual.getProblem().getKnapsack().getMaxProfit();
    return objectives;
  }

  @Override
  public EvaluatorType getType() {
    return EvaluatorType.SINGLE_OBJECTIVE_TTP_EVALUATOR;
  }

  @Override
  public BaseIndividual getNadirPoint() {
    return null;
  }

  @Override
  public BaseIndividual getPerfectPoint() {
    return null;
  }

  @Override
  public int getNumObjectives() {
    return 1;
  }
}
