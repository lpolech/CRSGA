package runners.msrcpsp;

import algorithms.evaluation.EvaluatorType;
import algorithms.evaluation.WeightedFiveScheduelEvaluator;
import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.converters.ConverterType;
import algorithms.evolutionary_algorithms.crossover.CrossoverType;
import algorithms.evolutionary_algorithms.differential_evolution.DifferentialEvolution;
import algorithms.evolutionary_algorithms.differential_evolution.clone_handlers.CloneHandlerType;
import algorithms.evolutionary_algorithms.differential_evolution.constraint_preservers.ConstraintPreserverType;
import algorithms.evolutionary_algorithms.initial_population.InitialPopulationType;
import algorithms.evolutionary_algorithms.mutation.MutationType;
import algorithms.evolutionary_algorithms.selection.SelectionType;
import algorithms.factories.*;
import algorithms.io.MSRCPSPIO;
import algorithms.problem.BaseIndividual;
import algorithms.problem.scheduling.Schedule;
import algorithms.problem.scheduling.schedule_builders.ScheduleBuilderType;
import algorithms.quality_measure.BaseMeasure;
import util.random.RandomDouble;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeightedFiveDEGRRunner {

  private static final Logger LOGGER = Logger.getLogger( WeightedFiveDEGRRunner.class.getName() );
  private static final String definitionFile = "assets/definitions/MSRCPSP/100_20_47_9.def";

  public static void main(String[] args) {
    run();
  }

  private static List<BaseIndividual<Double, Schedule>> run() {
    MSRCPSPIO reader = new MSRCPSPIO();
    Schedule schedule = reader.readDefinition(definitionFile);
    Schedule resultSchedule = null;
    if (null == schedule) {
      LOGGER.log(Level.WARNING, "Could not read the Definition " + definitionFile);
      return null;
    }

    ParameterSet<Double, Schedule> parameters = new ParameterSet<>();
    parameters.upperBounds = schedule.getUpperBounds();
    parameters.random = new RandomDouble(0);
    parameters.hasSuccesors = schedule.getSuccesors();
    parameters.evalRate = 1.0;
    parameters.populationMultiplicationFactor = 1;
    int populationSize = 100;
    int mutationRank = 1;
    double f = 0.1;
    double cr = 0.1;
    int generationLimit = 500;
    int staleLimit = 2000;
    int maxClones = 2000;
    double swaps = 0.5;
    long swapsTime = 1000;

    double[] evalRates = {
        1.0d, // Duration
        0.0d, // Cost
        0.0d, // Average Cash Flow
        0.0d, // Skill Use
        0.0d, // Average Use of Resource Time
    };

    long startTime;
    long endTime;

    parameters.numSwaps = swaps;
    parameters.swapsTime = swapsTime;
    parameters.mutationRank = mutationRank;

    parameters.cloneHandler = new CloneHandlerFactory(parameters).createCloneHandler(CloneHandlerType.ALLOW);
    parameters.constraintPreserver = new ConstraintPreserverFactory(parameters).createConstraintPreserver(ConstraintPreserverType.RANDOM);
    parameters.converter = new ConverterFactory(parameters).createConverter(ConverterType.TRUNCATING);
    parameters.crossover = new CrossoverFactory().createCrossover(CrossoverType.BINOMIAL);
    parameters.mutation = new MutationFactory(parameters).createMutation(MutationType.BEST);
    parameters.initialPopulation = new InitialPopulationGeneratorFactory(parameters).createInitialPopulation(InitialPopulationType.RANDOM);
    parameters.selection = new SelectionFactory(parameters).createSelection(SelectionType.ONE_TO_ONE);
    parameters.scheduleBuilder = new ScheduleBuilderFactory(parameters).createScheduleBuilder(ScheduleBuilderType.FORWARD_SCHEDULE_BUILDER);
    parameters.evaluator = new EvaluatorFactory().createEvaluator(EvaluatorType.WEIGHTED_FIVE_SCHEDULE_EVALUATOR, parameters.evalRate);
    ((WeightedFiveScheduelEvaluator<Double>)parameters.evaluator).setEvalRates(evalRates);

    DifferentialEvolution<Schedule> differentialEvolution = new DifferentialEvolution<>(
        schedule, populationSize, f, cr, generationLimit,
        staleLimit, maxClones, parameters, null);

    List<BaseIndividual<Double, Schedule>> resultIndividuals = differentialEvolution.optimize();
    BaseIndividual<Double, Schedule> best = resultIndividuals.get(0);
    for (double objective : best.getObjectives()) {
      System.out.print(objective + " ");
    }
    System.out.println();
    for (double gene : best.getGenes()) {
      System.out.print(gene + " ");
    }

    return resultIndividuals;
  }

}
