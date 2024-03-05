package runners.msrcpsp;


import algorithms.evaluation.EvaluatorType;
import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.converters.ConverterType;
import algorithms.evolutionary_algorithms.crossover.CrossoverType;
import algorithms.evolutionary_algorithms.genetic_algorithm.NondominatedTournamentGA;
import algorithms.evolutionary_algorithms.initial_population.InitialPopulationType;
import algorithms.evolutionary_algorithms.mutation.MutationType;
import algorithms.evolutionary_algorithms.selection.SelectionType;
import algorithms.factories.*;
import algorithms.io.MSRCPSPIO;
import algorithms.problem.BaseIndividual;
import algorithms.problem.scheduling.Schedule;
import algorithms.problem.scheduling.schedule_builders.ScheduleBuilderType;
import algorithms.quality_measure.ConvergenceMeasure;
import algorithms.quality_measure.HyperVolume;
import algorithms.quality_measure.ONVG;
import algorithms.quality_measure.Spacing;
import util.random.RandomInt;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NTGARunner {

  private static final Logger LOGGER = Logger.getLogger( NTGARunner.class.getName() );

  private static String definitionFile = "assets/definitions/MSRCPSP/100_10_26_15.def";

  public static void main(String[] args) {
    run();
  }

  private static List<BaseIndividual<Integer, Schedule>> run() {
//    java.io.PrintStream o = null;
//    try {
//      o = new java.io.PrintStream(new java.io.File("100_10_27_9_D2_NTGA2.csv"));
//    } catch (java.io.FileNotFoundException e) {
//      e.printStackTrace();
//    }
//    System.setOut(o);
//    String[] parts = definitionFile.split("/");
//    System.out.println(parts[parts.length - 1]);
//    System.out.println("Duration;Cost");

    MSRCPSPIO reader = new MSRCPSPIO();
    Schedule schedule = reader.readDefinition(definitionFile);
    if (null == schedule) {
      LOGGER.log(Level.WARNING, "Could not read the Definition "
          + definitionFile);
      return null;
    }

    ParameterSet<Integer, Schedule> parameters = new ParameterSet<>();
    parameters.upperBounds = schedule.getUpperBounds();
    parameters.random = new RandomInt(System.currentTimeMillis());
    parameters.hasSuccesors = schedule.getSuccesors();
    parameters.populationMultiplicationFactor = 1;
    parameters.evalRate = 1.0;
    parameters.tournamentSize = 6;
    parameters.initialPopulation = new InitialPopulationGeneratorFactory(parameters).createInitialPopulation(InitialPopulationType.RANDOM);
    parameters.selection = new SelectionFactory(parameters).createSelection(SelectionType.NONDOMINATED_SORTING_NO_CROWDING_TOURNAMENT);

    int populationSize = 50;
    int generationLimit = 2000;
    double mutationProbability = 0.005;
    double crossoverProbability = 0.9;
    boolean enhanceDiversity = true;
    double diversityThreshold = 0.8;

    parameters.crossover = new CrossoverFactory().createCrossover(CrossoverType.UNIFORM);
    parameters.mutation = new MutationFactory(parameters).createMutation(MutationType.RANDOM_BIT);
    parameters.converter = new ConverterFactory(parameters).createConverter(ConverterType.TRUNCATING);
    parameters.scheduleBuilder = new ScheduleBuilderFactory(parameters).createScheduleBuilder(ScheduleBuilderType.FORWARD_SCHEDULE_BUILDER);
    parameters.evaluator = new EvaluatorFactory().createEvaluator(EvaluatorType.WEIGHTED_EVALUATOR, parameters.evalRate);
    parameters.evaluator.setIndividual(new BaseIndividual<>(schedule, parameters.evaluator));


    NondominatedTournamentGA<Schedule> geneticAlgorithm = new NondominatedTournamentGA<>(schedule,
        populationSize, generationLimit, parameters, mutationProbability,
        crossoverProbability, diversityThreshold, enhanceDiversity);

    List<BaseIndividual<Integer, Schedule>> resultIndividuals;
    resultIndividuals = geneticAlgorithm.optimize();

    ConvergenceMeasure ed = new ConvergenceMeasure(
        parameters.evaluator.getPerfectPoint());
    System.out.print(ed.getMeasure(resultIndividuals));
    System.out.print(" ");

    HyperVolume hv = new HyperVolume(parameters.evaluator.getNadirPoint());
    System.out.print(hv.getMeasure(resultIndividuals));
    System.out.print(" ");

    ONVG pfs = new ONVG();
    System.out.print(pfs.getMeasure(resultIndividuals));
    System.out.print(" ");

    Spacing spacing = new Spacing();
    System.out.print(spacing.getMeasure(resultIndividuals));

    return resultIndividuals;
  }

}
