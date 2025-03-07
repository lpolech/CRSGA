package runners.mtsp;


import algorithms.evaluation.EvaluatorType;
import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.crossover.CrossoverType;
import algorithms.evolutionary_algorithms.genetic_algorithm.GeneticAlgorithm;
import algorithms.evolutionary_algorithms.initial_population.InitialPopulationType;
import algorithms.evolutionary_algorithms.mutation.MutationType;
import algorithms.evolutionary_algorithms.selection.SelectionType;
import algorithms.factories.*;
import algorithms.io.MTSPIO;
import algorithms.problem.BaseIndividual;
import algorithms.problem.mtsp.City;
import algorithms.problem.mtsp.TSP;
import util.random.RandomInt;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TSPGARunner {

  private static final Logger LOGGER = Logger.getLogger( TSPGARunner.class.getName() );

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
    ParameterSet<Integer, TSP> parameters = new ParameterSet<>();
    int populationSize = 100;
    int generationLimit = 1000;
    double mutationProbability = 0.1;
    double crossoverProbability = 0.9;
    parameters.evalRate = 1.0;
    parameters.tournamentSize = 2;
    parameters.random = new RandomInt(0);

    parameters.initialPopulation = new InitialPopulationGeneratorFactory(parameters).createInitialPopulation(InitialPopulationType.SHUFFLE);
    parameters.selection = new SelectionFactory(parameters).createSelection(SelectionType.TOURNAMENT);
    parameters.crossover = new CrossoverFactory().createCrossover(CrossoverType.TTP_ORDERED);
    parameters.mutation = new MutationFactory(parameters).createMutation(MutationType.TTP_SWAP_BIT);
    parameters.evaluator = new EvaluatorFactory().createEvaluator(EvaluatorType.BASE_TSP_EVALUATOR, parameters.evalRate);

    TSP tsp = new TSP(1);
    tsp.calculateDistanceMatrix(0, cities);
    tsp.createPaths();

    GeneticAlgorithm<TSP> geneticAlgorithm = new GeneticAlgorithm<>(
        tsp, populationSize, generationLimit, parameters, mutationProbability, crossoverProbability);
    List<BaseIndividual<Integer, TSP>> resultIndividuals = geneticAlgorithm.optimize();

    for (int i = 0; i < resultIndividuals.size(); ++i) {
      System.out.println("Distance: " + resultIndividuals.get(i).getObjectives()[0]);
    }

    return resultIndividuals;
  }

}
