package algorithms.problem.mtsp;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.problem.BaseProblemRepresentation;

import java.util.Arrays;
import java.util.List;

/**
 * Describes the solution to Traveling Salesman Problem.
 * Contains a distance matrix for all cities.
 */
public class TSP extends BaseProblemRepresentation {

  private DistanceMatrix[] distances;
  private int[] path;

  public TSP(int numObjectives) {
    distances = new DistanceMatrix[numObjectives];
    for (int i = 0; i < numObjectives; ++i) {
      distances[i] = new DistanceMatrix();
    }
  }

  @Override
  public TSP buildSolution(List<? extends Number> genes, ParameterSet<? extends Number, ? extends BaseProblemRepresentation> parameters) {
    for (int i = 0; i < path.length; ++i) {
      path[i] = genes.get(i).intValue();
    }
    this.setHashCode();
    return this;
  }

  /**
   * Calculates a distance matrix that contains all distances
   * between cities. Only half of the matrix is calculated.
   * The rest is rewritten from the other half.
   *
   * @param objective num of objective for, which to calculate
   *                  the distance matrix
   * @param cities array of cities
   * @return distance matrix
   */
  public DistanceMatrix[] calculateDistanceMatrix(int objective, City[] cities) {
    distances[objective].setDistances(distances[objective].execute(cities));
    return distances;
  }

  /**
   * Creates array that store the final route.
   *
   * @return empty two dimensional array, first dimension
   * contains objectives, second genes.
   */
  public int[] createPaths() {
    int numGenes = getNumGenes();
    path = new int[numGenes];
    return path;
  }

  @Override
  public BaseProblemRepresentation cloneDeep() {
    TSP tsp = new TSP(distances.length);
    // no reason to copy distance matrices, they are constant
    for (int i = 0; i < distances.length; ++i) {
      DistanceMatrix distanceMatrix = distances[i];
      tsp.distances[i] = distanceMatrix;
    }
    tsp.setPath(path.clone());
    return tsp;
  }

  @Override
  public void setHashCode() {
    this.hashCode = Arrays.hashCode(path);
  }

  @Override
  public int getNumGenes() {
    return distances[0].getDistances().length;
  }

  public DistanceMatrix[] getDistances() {
    return distances;
  }

  public void setDistances(DistanceMatrix[] distances) {
    this.distances = distances;
  }

  public int[] getPath() {
    return path;
  }

  public void setPath(int[] path) {
    this.path = path;
  }

}
