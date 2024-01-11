package runners.msrcpsp;

import algorithms.evaluation.EvaluatorType;
import algorithms.evaluation.ThreeScheduleEvaluator;
import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.converters.ConverterType;
import algorithms.evolutionary_algorithms.crossover.CrossoverType;
import algorithms.evolutionary_algorithms.genetic_algorithm.BNSGAIII;
import algorithms.evolutionary_algorithms.genetic_algorithm.UNSGAIII;
import algorithms.evolutionary_algorithms.initial_population.InitialPopulationType;
import algorithms.evolutionary_algorithms.mutation.MutationType;
import algorithms.evolutionary_algorithms.selection.SelectionType;
import algorithms.factories.*;
import algorithms.io.MSRCPSPIO;
import algorithms.problem.BaseIndividual;
import algorithms.problem.scheduling.Schedule;
import algorithms.problem.scheduling.schedule_builders.ScheduleBuilderType;
import algorithms.quality_measure.*;
import util.random.RandomInt;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UNSGAIIIRunner {

  private static final Logger LOGGER = Logger.getLogger(UNSGAIIIRunner.class.getName());
  private static String definitionFile = "assets/definitions/MSRCPSP/100_10_26_15.def";
  private static String tpfDir = "assets/points/PFs/3obj/";

  public static void main(String[] args) {
    run();
  }

  private static List<BaseIndividual<Integer, Schedule>> run() {
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
    int populationSize = 1000;
    int generationLimit = 200;
    double mutationProbability = 0.01;
    double crossoverProbability = 0.9;
    parameters.tournamentSize = 2;

    parameters.initialPopulation = new InitialPopulationGeneratorFactory(parameters).createInitialPopulation(InitialPopulationType.RANDOM);
    parameters.selection = new SelectionFactory(parameters).createSelection(SelectionType.NONDOMINATED_SORTING_NO_CROWDING_TOURNAMENT); // TODO: Random selection
    parameters.crossover = new CrossoverFactory().createCrossover(CrossoverType.SINGLE_POINT);
    parameters.mutation = new MutationFactory(parameters).createMutation(MutationType.RANDOM_BIT);
    parameters.converter = new ConverterFactory(parameters).createConverter(ConverterType.TRUNCATING);
    parameters.scheduleBuilder = new ScheduleBuilderFactory(parameters).createScheduleBuilder(ScheduleBuilderType.FORWARD_SCHEDULE_BUILDER);
    parameters.evaluator = new EvaluatorFactory().createEvaluator(EvaluatorType.FOUR_SCHEDULE_EVALUATOR, parameters.evalRate);
    parameters.evaluator.setIndividual(new BaseIndividual<>(schedule, parameters.evaluator));

    UNSGAIII<Schedule> geneticAlgorithm = new UNSGAIII<>(
        schedule, populationSize, generationLimit, parameters,
        mutationProbability, crossoverProbability);

    List<BaseIndividual<Integer, Schedule>> resultIndividuals;
    resultIndividuals = geneticAlgorithm.optimize();

//    List<BaseIndividual<Integer, Schedule>> tpf = getTPF(schedule, (ThreeScheduleEvaluator<Integer>)parameters.evaluator);
//
//    BaseMeasure igd = new InvertedGenerationalDistance(tpf);
//    System.out.print(igd.getMeasure(resultIndividuals));
//    System.out.print(" ");

    EDMany ed = new EDMany(
        parameters.evaluator.getPerfectPoint());
    System.out.print(ed.getMeasure(resultIndividuals));
    System.out.print(" ");

//    HVMany hv = new HVMany(parameters.evaluator.getNadirPoint());
//    System.out.print(hv.getMeasure(resultIndividuals));
//    System.out.print(" ");

    ONVG pfs = new ONVG();
    System.out.print(pfs.getMeasure(resultIndividuals));
    System.out.print(" ");

    return resultIndividuals;
  }

  private static List<BaseIndividual<Integer, Schedule>> getTPF(Schedule schedule, ThreeScheduleEvaluator<Integer> evaluator) {
    List<BaseIndividual<Integer, Schedule>> result = new ArrayList<>();
    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(tpfDir + definitionFile.substring(27).replace("def", "csv")));
      try {
        String line;
        String parts[];
        BaseIndividual<Integer, Schedule> newGuy;
        while ((line = reader.readLine()) != null) {

          parts = line.split(";");

          newGuy = new BaseIndividual<>(schedule, new ArrayList<>(), evaluator);
          evaluator.setIndividual(newGuy);
          double[] objectives = new double[3];
          objectives[0] = Double.parseDouble(parts[0]);
          objectives[1] = Double.parseDouble(parts[1]);
          objectives[2] = Double.parseDouble(parts[2]);
          newGuy.setObjectives(objectives);

          objectives = new double[3];
          objectives[0] = Double.parseDouble(parts[0]) / evaluator.getMaxDuration();
          objectives[1] = Double.parseDouble(parts[1]) / evaluator.getMaxCost();
          objectives[2] = Double.parseDouble(parts[2]) / evaluator.getMaxAverageCashFlowDeviation();
          newGuy.setNormalObjectives(objectives);

          result.add(newGuy);

        }
      } catch (IOException e) {
        LOGGER.log(Level.FINE, e.toString());
        return null;
      } finally {
        try {
          reader.close();
        } catch (IOException e) {
          LOGGER.log(Level.FINE, e.toString());
        }
      }
    } catch (FileNotFoundException e) {
      LOGGER.log(Level.FINE, e.toString());
      return null;
    }
    return result;
  }

}
