package runners.mtsp;


import algorithms.evaluation.BaseTSPEvaluator;
import algorithms.io.MTSPIO;
import algorithms.problem.BaseIndividual;
import algorithms.problem.mtsp.City;
import algorithms.problem.mtsp.TSP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TSPGreedyRunner {

  private static final Logger LOGGER = Logger.getLogger( TSPGreedyRunner.class.getName() );

  private static String definitionFile = "assets/definitions/MTSP/kroA100.tsp";

  public static void main(String[] args) {
    run();
  }

  private static List<BaseIndividual<Integer, TSP>> run() {
    MTSPIO reader = new MTSPIO();
    City[] cities = reader.getCities(definitionFile);

    if (null == cities) {
      LOGGER.log(Level.WARNING, "Could not read the Definition " + definitionFile);
      return null;
    }

    TSP tsp = new TSP(1);
    tsp.calculateDistanceMatrix(0, cities);
    tsp.createPaths();
    BaseTSPEvaluator<Integer> evaluator = new BaseTSPEvaluator<>();

    BaseIndividual<Integer, TSP> individual = new BaseIndividual<>(tsp, evaluator);

    individual = solveGreedy(individual);
    int[] path = new int[individual.getGenes().size()];
    for (int i = 0; i < path.length; ++i) {
      path[i] = individual.getGenes().get(i);
    }
    individual.getProblem().setPath(path);
    individual.setObjectives();
    System.out.println("Distance: " + individual.getObjectives()[0]);

    System.out.println();
    return null;
  }

  private static BaseIndividual<Integer,TSP> solveGreedy(BaseIndividual<Integer, TSP> individual) {
    List<Integer> genes = new ArrayList<>(Collections.nCopies(individual.getProblem().getNumGenes(), null));;
    double[][] distances = individual.getProblem().getDistances()[0].getDistances();
    genes.set(0, 0);
    int previous = 0;
    for (int i = 1; i < distances.length; ++i) { // -1, as the last path is back to the first node
      int shortestIndex = -1;
      double shortestDistance = Double.MAX_VALUE;
      for (int j = 0; j < distances[previous].length; ++j) {
        if (distances[previous][j] < shortestDistance && !isCityVisited(genes, j)) {
          shortestDistance = distances[previous][j];
          shortestIndex = j;
        }
      }
      genes.set(i, shortestIndex);
      previous = shortestIndex;
    }

    individual.setGenes(genes);
    return individual;
  }

  private static boolean isCityVisited(List<Integer> genes, int cityIndex) {
    return genes.contains(cityIndex);
  }

}
