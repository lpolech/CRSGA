package algorithms.quality_measure;

import algorithms.problem.BaseIndividual;

import java.util.List;

/**
 * Measures the distance between the solutions
 * in optimal pareto front from the closest solution
 * in potential pareto front
 */
public class ApfDistance extends BaseMeasure {

  public ApfDistance(List<? extends BaseIndividual> optimalParetoFront) {
    this.referencePopulation = optimalParetoFront;
  }

  /**
   * Average distance between the solutions in the
   * optimal pareto front and closest solutions from
   * potential pareto front.
   *
   * @return inverted generational distance
   */
  @Override
  public <T extends BaseIndividual> double getMeasure(List<T> population) {
    double sum = 0.0;
    for (BaseIndividual individual : referencePopulation) {
      sum += getMinDistance(individual, population);
    }
    return sum / referencePopulation.size();
  }

  // USE RMSE TO FIND THE CLOSES POINT BUT THE MEASURE IS JUST THE DIFF
  protected double getMinDistance(BaseIndividual optimalParetoIndividual, List<? extends BaseIndividual> solutionPareto) {
    double min = Double.MAX_VALUE;
    BaseIndividual closestSolutionIndividual = solutionPareto.get(0);
    double distance;
    for (BaseIndividual solutionIndividual : solutionPareto) {
      // We avoid calculating powers and sqrt to speed up the algorithm
      distance = 0.0d;
      for (int i = 0; i < optimalParetoIndividual.getNormalObjectives().length; ++i) {
        distance += Math.abs(optimalParetoIndividual.getNormalObjectives()[i] - solutionIndividual.getNormalObjectives()[i]);
      }
      if (distance < min) {
        closestSolutionIndividual = solutionIndividual;
        min = distance;
      }
    }
    return getDistance(optimalParetoIndividual, closestSolutionIndividual);
  }

  protected double getDistance(BaseIndividual optimalParetoIndividual, BaseIndividual closestSolutionIndividual) {
    double distance = 0.0d;
    for (int i = 0; i < optimalParetoIndividual.getNormalObjectives().length; ++i) {
      distance += optimalParetoIndividual.getNormalObjectives()[i] - closestSolutionIndividual.getNormalObjectives()[i];
    }
    return distance;
  }
}
