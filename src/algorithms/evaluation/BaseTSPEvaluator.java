package algorithms.evaluation;

import algorithms.problem.BaseIndividual;
import algorithms.problem.mtsp.TSP;

/**
 * Base evaluator for TSP.
 * Has functions related to its objectives.
 */
public class BaseTSPEvaluator<GENE extends Number> extends BaseEvaluator<GENE, TSP> {

  public BaseTSPEvaluator() { }

  public BaseTSPEvaluator(BaseIndividual<GENE, TSP> individual) {
    super(individual);
  }

  @Override
  public double evaluate() {
    return getDistance(0) / getMaxDistance(0);
  }

  @Override
  public BaseTSPEvaluator<GENE> getCopy(BaseIndividual<GENE, TSP> individual) {
    return new BaseTSPEvaluator<>(individual);
  }

  @Override
  public double[] getObjectives() {
    int numObjectives = individual.getProblem().getDistances().length;
    double[] objectives = new double[numObjectives];
    for (int i = 0; i < numObjectives; ++i) {
      objectives[i] = getDistance(i);
    }
    return objectives;
  }

  @Override
  public double[] getNormalObjectives() {
    int numObjectives = individual.getProblem().getDistances().length;
    double[] normalObjectives = new double[numObjectives];
    for (int i = 0; i < numObjectives; ++i) {
      double minDistance = getMinDistance(i);
      double maxDistance = getMaxDistance(i);
      normalObjectives[i] = (getDistance(i) - minDistance) / (maxDistance - minDistance);
    }
    return normalObjectives;
  }

  public double getMinDistance(int objectiveNum) {
    double[][] distances = individual.getProblem().getDistances()[objectiveNum].getDistances();
    double minDistance = distances[0][0];
    for (int i = 0; i < distances.length; ++i) {
      for (int j = 0; j < distances[i].length; ++j) { // do not assume symmetry
        if (distances[i][j] < minDistance) {
          minDistance = distances[i][j];
        }
      }
    }
    return minDistance * distances.length;
  }

  public double getMaxDistance(int objectiveNum) {
    double[][] distances = individual.getProblem().getDistances()[objectiveNum].getDistances();
    double maxDistance = distances[0][0];
    for (int i = 0; i < distances.length; ++i) {
      for (int j = 0; j < distances[i].length; ++j) { // do not assume symmetry
        if (distances[i][j] > maxDistance) {
          maxDistance = distances[i][j];
        }
      }
    }
    return maxDistance * distances.length;
  }

  public double getDistance(int objectiveNum) {
    double[][] distances = individual.getProblem().getDistances()[objectiveNum].getDistances();
    int[] paths = individual.getProblem().getPath();
    double distance = 0.0;
    for (int i = 0; i < paths.length - 1; ++i) {
      distance += distances[paths[i]][paths[i+1]];
    }
    distance += distances[paths[paths.length - 1]][paths[0]];
    return distance;
  }

  @Override
  public EvaluatorType getType() {
    return EvaluatorType.BASE_TSP_EVALUATOR;
  }

  @Override
  public BaseIndividual getNadirPoint() {
    TSP tsp = individual.getProblem();
    int numObjectives = tsp.getDistances().length;
    BaseIndividual<GENE, TSP> nadirPoint = new BaseIndividual<>(tsp, this);

    double[] objectives = new double[numObjectives];
    for (int i = 0; i < numObjectives; ++i) {
      objectives[i] = getMaxDistance(i);
    }
    nadirPoint.setObjectives(objectives);

    objectives = new double[numObjectives];
    for (int i = 0; i < numObjectives; ++i) {
      objectives[i] = 1.0;
    }
    nadirPoint.setNormalObjectives(objectives);

    return nadirPoint;
  }

  @Override
  public BaseIndividual getPerfectPoint() {
    TSP tsp = individual.getProblem();
    int numObjectives = tsp.getDistances().length;
    BaseIndividual<GENE, TSP> perfectPoint = new BaseIndividual<>(tsp, this);

    double[] objectives = new double[numObjectives];
    for (int i = 0; i < numObjectives; ++i) {
      objectives[i] = getMinDistance(i);
    }
    perfectPoint.setObjectives(objectives);

    double[] normalObjectives = new double[numObjectives];
    for (int i = 0; i < numObjectives; ++i) {
      normalObjectives[i] = 0.0;
    }
    perfectPoint.setNormalObjectives(normalObjectives);

    return perfectPoint;
  }

  @Override
  public int getNumObjectives() {
    return individual.getProblem().getDistances().length;
  }

}
