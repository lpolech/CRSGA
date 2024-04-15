package algorithms.evaluation;

import algorithms.problem.BaseIndividual;
import algorithms.problem.TTP;

/**
 * Evaluator for multi objective Travelling Salesman Problem
 */
public class MOTTPEvaluator<GENE extends Number> extends BaseTTPEvaluator<GENE> {

  public MOTTPEvaluator() { }

  public MOTTPEvaluator(BaseIndividual<GENE, TTP> individual) {
    super(individual);
  }

  @Override
  public double evaluate() {
    double[] normalObjectives = this.getNormalObjectives();
    double travelTime = normalObjectives[0];
    double cost = normalObjectives[1];
    double normalisedCost = 1 + cost;

    return travelTime + normalisedCost;
  }

  @Override
  public BaseEvaluator<GENE, TTP> getCopy(BaseIndividual<GENE, TTP> individual) {
    return new MOTTPEvaluator<>(individual);
  }

  @Override
  public double[] getObjectives() {
    double[] objectives = new double[2];
    TTP problem = individual.getProblem();
    objectives[0] = problem.getTravellingTime();
    objectives[1] = -getProfit();

    return objectives;
  }

  @Override
  public double[] getNormalObjectives() {
    double[] objectives = getObjectives();
    TTP problem = individual.getProblem();

    objectives[0] /= (problem.getMaxTravellingTime() - problem.getMinTravellingTime());
    objectives[1] /= problem.getMaxProfit();

    return objectives;
  }

  @Override
  public EvaluatorType getType() {
    return EvaluatorType.MULTI_OBJECTIVE_TTP_EVALUATOR;
  }

  @Override
  public BaseIndividual getNadirPoint() {
    TTP problem = individual.getProblem();
    BaseIndividual<GENE, TTP> nadirPoint = new BaseIndividual<>(problem, this);

    double[] objectives = new double[2];
    objectives[0] = problem.getMaxTravellingTime() - problem.getMinTravellingTime();
    objectives[1] = 0d;
    nadirPoint.setObjectives(objectives);

    objectives = new double[2];
    objectives[0] = 1.0d;
    objectives[1] = 0.0d;
    nadirPoint.setNormalObjectives(objectives);

    return nadirPoint;
  }

  @Override
  public BaseIndividual getPerfectPoint() {
    TTP problem = individual.getProblem();
    BaseIndividual<GENE, TTP> nadirPoint = new BaseIndividual<>(problem, this);

    double[] objectives = new double[2];
    objectives[0] = 0d;
    objectives[1] = -problem.getMaxProfit();
    nadirPoint.setObjectives(objectives);

    objectives = new double[2];
    objectives[0] = 0d;
    objectives[1] = -1.0d;
    nadirPoint.setNormalObjectives(objectives);

    return nadirPoint;
  }

  @Override
  public int getNumObjectives() {
    return 2;
  }
}
