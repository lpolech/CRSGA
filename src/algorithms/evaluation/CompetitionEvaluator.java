package algorithms.evaluation;

import algorithms.problem.BaseIndividual;
import algorithms.problem.TTP;

public class CompetitionEvaluator extends MOTTPEvaluator<Integer> {

  public CompetitionEvaluator() { }

  public CompetitionEvaluator(BaseIndividual<Integer, TTP> individual) {
    super(individual);
  }

  @Override
  public BaseEvaluator<Integer, TTP> getCopy(BaseIndividual<Integer, TTP> individual) {
    return new CompetitionEvaluator(individual);
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

    objectives[0] /= 2613.0d;
    objectives[1] /= -489194.0d;

    return objectives;
  }

  @Override
  public BaseIndividual getNadirPoint() {
    TTP problem = individual.getProblem();
    BaseIndividual<Integer, TTP> nadirPoint = new BaseIndividual<>(problem, this);

    double[] objectives = new double[2];
    objectives[0] = 5444.0;
    objectives[1] = -0.0d;
    nadirPoint.setObjectives(objectives);

    objectives = new double[2];
    objectives[0] = 1.0;
    objectives[1] = -1.0;
    nadirPoint.setNormalObjectives(objectives);

    return nadirPoint;
  }

  @Override
  public BaseIndividual getPerfectPoint() {
    TTP problem = individual.getProblem();
    BaseIndividual<Integer, TTP> nadirPoint = new BaseIndividual<>(problem, this);

    double[] objectives = new double[2];
    objectives[0] = 2613.0d;
    objectives[1] = -42036.0d;
    nadirPoint.setObjectives(objectives);

    objectives = new double[2];
    objectives[0] = 0d;
    objectives[1] = 0d;
    nadirPoint.setNormalObjectives(objectives);

    return nadirPoint;
  }

}
