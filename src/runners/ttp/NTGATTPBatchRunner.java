package runners.ttp;

import algorithms.evaluation.EvaluatorType;
import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.crossover.CrossoverType;
import algorithms.evolutionary_algorithms.genetic_algorithm.NondominatedTournamentGA;
import algorithms.evolutionary_algorithms.initial_population.InitialPopulationType;
import algorithms.evolutionary_algorithms.mutation.MutationType;
import algorithms.evolutionary_algorithms.selection.SelectionType;
import algorithms.factories.*;
import algorithms.io.TTPIO;
import algorithms.problem.BaseIndividual;
import algorithms.problem.TTP;
import util.random.RandomInt;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NTGATTPBatchRunner {

  private static final Logger LOGGER = Logger.getLogger( NTGATTPBatchRunner.class.getName() );

  public static void main(String[] args) {
    run();
  }

  private static List<BaseIndividual<Integer, TTP>> run() {
    java.io.PrintStream o = null;
    try {
      o = new java.io.PrintStream(new java.io.File("for_khaled.csv"));
    } catch (java.io.FileNotFoundException e) {
      e.printStackTrace();
    }
    System.setOut(o);

    ParameterSet<Integer, TTP> parameters = new ParameterSet<>();
    parameters.populationMultiplicationFactor = 1;
    int populationSize = 100;
    int generationLimit = 5000;
    double mutationProbability = 0.9;
    double crossoverProbability = 0.3;
    parameters.evalRate = 1.0;
    parameters.tournamentSize = 6;
    boolean enhanceDiversity = true;
    double diversityThreshold = 0.8;
    parameters.random = new RandomInt(System.currentTimeMillis());

    parameters.initialPopulation = new InitialPopulationGeneratorFactory(parameters).createInitialPopulation(InitialPopulationType.RANDOM_TTP);
    parameters.selection = new SelectionFactory(parameters).createSelection(SelectionType.NONDOMINATED_SORTING_NO_CROWDING_TOURNAMENT);
    parameters.crossover = new CrossoverFactory().createCrossover(CrossoverType.TTP_COMPETITION);
    parameters.mutation = new MutationFactory(parameters).createMutation(MutationType.TTP_COMPETITION);
    parameters.evaluator = new EvaluatorFactory().createEvaluator(EvaluatorType.MULTI_OBJECTIVE_TTP_EVALUATOR, parameters.evalRate);

    File assets = new File("assets/definitions/TTP/for_khaled");
    File[] files = assets.listFiles();
    Arrays.sort(files, new Comparator<File>() {
      public int compare(File o1, File o2) {
        return extractInt(o1.getPath()) - extractInt(o2.getPath());
      }

      int extractInt(String s) {
        String num = s.replaceAll("\\D", "");
        // return 0 if no digits found
        return num.isEmpty() ? 0 : Integer.parseInt(num);
      }
    });
    for (File file : files) {
      TTPIO reader = new TTPIO();
      TTP ttp = reader.readDefinition(file.getPath());
      if (null == ttp) {
        LOGGER.log(Level.WARNING, "Could not read the Definition " + file);
        return null;
      }

      parameters.upperBounds = ttp.getUpperBounds();
      parameters.evaluator.setIndividual(new BaseIndividual<>(ttp, parameters.evaluator));
      parameters.geneSplitPoint = ttp.getSplitPoint();

      System.out.println(file);
      for (int i = 0; i < 20; ++i) {
        NondominatedTournamentGA<TTP> geneticAlgorithm = new NondominatedTournamentGA<>(ttp,
            populationSize, generationLimit, parameters, mutationProbability,
            crossoverProbability, diversityThreshold, enhanceDiversity);
        List<BaseIndividual<Integer, TTP>> resultIndividuals = geneticAlgorithm.optimize();

        for (BaseIndividual<Integer, TTP> individual : resultIndividuals) {
          for (int gene : individual.getGenes()) {
            System.out.print(gene + " ");
          }
          System.out.println();
          System.out.println(individual.getObjectives()[0] + ";" + individual.getObjectives()[1]);
        }
        System.out.println();
      }

    }

    return null;
  }

}
