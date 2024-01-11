package runners.msrcpsp;


import algorithms.evaluation.EvaluatorType;
import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.converters.ConverterType;
import algorithms.evolutionary_algorithms.crossover.CrossoverType;
import algorithms.evolutionary_algorithms.differential_evolution.constraint_preservers.ConstraintPreserverType;
import algorithms.evolutionary_algorithms.genetic_algorithm.NondominatedTournamentGA;
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

public class NTGAManyBatchRunner {

  private static final Logger LOGGER = Logger.getLogger( NTGAManyBatchRunner.class.getName() );

  public static void main(String[] args) {
    run(args);
  }

  private static List<BaseIndividual<Integer, Schedule>> run(String[] args) {

    String input = args[0];
    String method = args[1];

    java.io.PrintStream o = null;
    try {
      o = new java.io.PrintStream(new java.io.File(method + "_" + input + ".csv"));
    } catch (java.io.FileNotFoundException e) {
      e.printStackTrace();
    }
    System.setOut(o);

    ParameterSet<Integer, Schedule> parameters = new ParameterSet<>();
    parameters.random = new RandomInt(System.currentTimeMillis());
    parameters.populationMultiplicationFactor = 1;
    parameters.evalRate = 1.0;
    int populationSize = 50;
    int generationLimit = 4000;
    double mutationProbability = 0.01;
    double crossoverProbability = 0.6;
    parameters.tournamentSize = 6;
    boolean enhanceDiversity = true;
    double diversityThreshold = 0.8;

    parameters.initialPopulation = new InitialPopulationGeneratorFactory(parameters).createInitialPopulation(InitialPopulationType.RANDOM);
    parameters.selection = new SelectionFactory(parameters).createSelection(SelectionType.NONDOMINATED_SORTING_NO_CROWDING_TOURNAMENT);
    parameters.crossover = new CrossoverFactory().createCrossover(CrossoverType.SINGLE_POINT);
    parameters.mutation = new MutationFactory(parameters).createMutation(MutationType.RANDOM_BIT);
    parameters.converter = new ConverterFactory(parameters).createConverter(ConverterType.TRUNCATING);
    parameters.evaluator = new EvaluatorFactory().createEvaluator(EvaluatorType.FIVE_SCHEDULE_EVALUATOR, parameters.evalRate);

    File assets = new File("assets/definitions/MSRCPSP_small");
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
      parameters.constraintPreserver = new ConstraintPreserverFactory(parameters).createConstraintPreserver(ConstraintPreserverType.RANDOM);
      parameters.scheduleBuilder = new ScheduleBuilderFactory(parameters).createScheduleBuilder(ScheduleBuilderType.FORWARD_SCHEDULE_BUILDER);

      System.out.println(file);
      for (int i = 0; i < 10; ++i) {
//        ((algorithms.evolutionary_algorithms.initial_population.ReadMSRCPSPInitialPopulation) parameters.initialPopulation)
//            .setInputFile("init_research/" + input + "/" + file.getName().split("\\.")[0] + ".init");
        NondominatedTournamentGA<Schedule> geneticAlgorithm = new NondominatedTournamentGA<>(schedule,
            populationSize, generationLimit, parameters, mutationProbability,
            crossoverProbability, diversityThreshold, enhanceDiversity);

        List<BaseIndividual<Integer, Schedule>> resultIndividuals = geneticAlgorithm.optimize();

        for (BaseIndividual individual : resultIndividuals) {
          for (double objective : individual.getObjectives()) {
            System.out.print(objective + ";");
          }
          System.out.println();
        }
        System.out.println();
      }

    }

    return null;
  }

}
