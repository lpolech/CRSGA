package runners.mtsp;

import algorithms.evaluation.EvaluatorType;
import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.converters.ConverterType;
import algorithms.evolutionary_algorithms.crossover.CrossoverType;
import algorithms.evolutionary_algorithms.genetic_algorithm.NondominatedTournamentGA;
import algorithms.evolutionary_algorithms.initial_population.InitialPopulationType;
import algorithms.evolutionary_algorithms.mutation.MutationType;
import algorithms.evolutionary_algorithms.selection.SelectionType;
import algorithms.factories.*;
import algorithms.io.MTSPIO;
import algorithms.problem.BaseIndividual;
import algorithms.problem.mtsp.City;
import algorithms.problem.mtsp.DistanceMatrix;
import algorithms.problem.mtsp.TSP;
import util.random.RandomInt;

import java.util.List;
import java.util.logging.Logger;

public class NSGAMTSPRunner {

  private static final Logger LOGGER = Logger.getLogger(NSGAMTSPRunner.class
      .getName());
  private static String definitionFileA = "assets/definitions/MTSP/kroA100.tsp";
  private static String definitionFileB = "assets/definitions/MTSP/kroB100.tsp";

  public static void main(String[] args) {
    run();
  }

  private static List<BaseIndividual<Integer, TSP>> run() {
    MTSPIO reader = new MTSPIO();
    City[] citiesA = reader.getCities(definitionFileA);
    City[] citiesB = reader.getCities(definitionFileB);
    DistanceMatrix[] distances = new DistanceMatrix[2];
    distances[0] = new DistanceMatrix();
    distances[0].setDistances(distances[0].execute(citiesA));
    distances[1] = new DistanceMatrix();
    distances[1].setDistances(distances[1].execute(citiesB));

    TSP tsp = new TSP(2);
    tsp.calculateDistanceMatrix(0, citiesA);
    tsp.calculateDistanceMatrix(1, citiesB);
    tsp.createPaths();

    ParameterSet<Integer, TSP> parameters = new ParameterSet<>();
    parameters.random = new RandomInt(System.currentTimeMillis());
    parameters.evalRate = 1.0;
    int populationSize = 50;
    int generationLimit = 1000;
    double mutationProbability = 0.1;
    double crossoverProbability = 0.9;
    parameters.tournamentSize = 2;
    boolean enhanceDiversity = false;
    double diversityThreshold = 0.8;

    long startTime;
    long endTime;

    parameters.initialPopulation = new InitialPopulationGeneratorFactory(parameters).createInitialPopulation(InitialPopulationType.SHUFFLE);
    parameters.selection = new SelectionFactory(parameters).createSelection(SelectionType.NONDOMINATED_SORTING_TOURNAMENT);
    parameters.crossover = new CrossoverFactory().createCrossover(CrossoverType.ORDERED);
    parameters.mutation = new MutationFactory(parameters).createMutation(MutationType.SWAP_BIT);
    parameters.converter = new ConverterFactory(parameters).createConverter(ConverterType.TRUNCATING);
    parameters.evaluator = new EvaluatorFactory().createEvaluator(EvaluatorType.BASE_TSP_EVALUATOR, parameters.evalRate);
    parameters.evaluator.setIndividual(new BaseIndividual<>(tsp, parameters.evaluator));

    NondominatedTournamentGA<TSP> geneticAlgorithm = new NondominatedTournamentGA<>(tsp,
        populationSize, generationLimit, parameters, mutationProbability,
        crossoverProbability, diversityThreshold, enhanceDiversity);
//    NondominatedSortingGA<TSP> geneticAlgorithm = new NondominatedSortingGA<>(
//        tsp, populationSize, generationLimit, parameters,
//        mutationProbability, crossoverProbability);

    startTime = System.nanoTime();
    List<BaseIndividual<Integer, TSP>> resultIndividuals = geneticAlgorithm.optimize();
    endTime = System.nanoTime();

    return resultIndividuals;
  }
}
