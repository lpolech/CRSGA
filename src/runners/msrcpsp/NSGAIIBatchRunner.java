package runners.msrcpsp;

import algorithms.evaluation.EvaluatorType;
import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.converters.ConverterType;
import algorithms.evolutionary_algorithms.crossover.CrossoverType;
import algorithms.evolutionary_algorithms.differential_evolution.constraint_preservers.ConstraintPreserverType;
import algorithms.evolutionary_algorithms.genetic_algorithm.NondominatedSortingGA;
import algorithms.evolutionary_algorithms.initial_population.InitialPopulationType;
import algorithms.evolutionary_algorithms.mutation.MutationType;
import algorithms.evolutionary_algorithms.selection.SelectionType;
import algorithms.factories.*;
import algorithms.io.MSRCPSPIO;
import algorithms.problem.BaseIndividual;
import algorithms.problem.scheduling.Schedule;
import algorithms.problem.scheduling.schedule_builders.ScheduleBuilderType;
import util.random.RandomInt;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NSGAIIBatchRunner {

  private static final Logger LOGGER = Logger.getLogger(NSGAIIBatchRunner.class.getName());

  public static void main(String[] args) {
    run(args);
  }

  private static List<BaseIndividual<Integer, Schedule>> run(String[] args) {

    ParameterSet<Integer, Schedule> parameters = new ParameterSet<>();
    parameters.random = new RandomInt(System.currentTimeMillis());
    parameters.evalRate = 1.0;
    parameters.populationMultiplicationFactor = 1;
    int populationSize = 200;
    int generationLimit = 2000;
    double mutationProbability = 0.05;
    double crossoverProbability = 0.9;
    parameters.tournamentSize = 2;

    long startTime;
    long endTime;

    parameters.initialPopulation = new InitialPopulationGeneratorFactory(parameters).createInitialPopulation(InitialPopulationType.READ_MSRCPSP);
    parameters.selection = new SelectionFactory(parameters).createSelection(SelectionType.NONDOMINATED_SORTING_TOURNAMENT);
    parameters.crossover = new CrossoverFactory().createCrossover(CrossoverType.SINGLE_POINT);
    parameters.mutation = new MutationFactory(parameters).createMutation(MutationType.RANDOM_BIT);
    parameters.converter = new ConverterFactory(parameters).createConverter(ConverterType.TRUNCATING);
    parameters.evaluator = new EvaluatorFactory().createEvaluator(EvaluatorType.WEIGHTED_EVALUATOR, parameters.evalRate);

    File assets = new File("assets/definitions/MSRCPSP");
    File[] files = assets.listFiles();
    Arrays.sort(files);
    for (File file : files) {

      MSRCPSPIO reader = new MSRCPSPIO();
      Schedule schedule = reader.readDefinition(file.getPath());
      if (null == schedule) {
        LOGGER.log(Level.WARNING, "Could not read the Definition " + file);
        return null;
      }
      parameters.evaluator.setIndividual(new BaseIndividual<>(schedule, parameters.evaluator));
      parameters.upperBounds = schedule.getUpperBounds();
      parameters.hasSuccesors = schedule.getSuccesors();
      parameters.scheduleBuilder = new ScheduleBuilderFactory(parameters).createScheduleBuilder(ScheduleBuilderType.FORWARD_SCHEDULE_BUILDER);
      parameters.constraintPreserver = new ConstraintPreserverFactory(parameters).createConstraintPreserver(ConstraintPreserverType.RANDOM);

      System.out.println(file);
      for (int i = 0; i < 10; ++i) {
        ((algorithms.evolutionary_algorithms.initial_population.ReadMSRCPSPInitialPopulation) parameters.initialPopulation)
            .setInputFile("init_research/msrcpsp2/" + file.getName().split("\\.")[0] + ".init");
        NondominatedSortingGA<Schedule> geneticAlgorithm = new NondominatedSortingGA<>(
            schedule, populationSize, generationLimit, parameters,
            mutationProbability, crossoverProbability);

        startTime = System.nanoTime();
        List<BaseIndividual<Integer, Schedule>> resultIndividuals = geneticAlgorithm.optimize();
        endTime = System.nanoTime();
        System.out.println((endTime - startTime) / 1000000);

//        for (BaseIndividual individual : resultIndividuals) {
//          System.out.println(individual.getObjectives()[0] + ";" + individual.getObjectives()[1]);
//        }
//        System.out.println();
      }

    }

    return null;
  }

}