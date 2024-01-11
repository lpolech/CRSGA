package runners.msrcpsp;

import algorithms.evaluation.EvaluatorType;
import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.converters.ConverterType;
import algorithms.factories.ConverterFactory;
import algorithms.factories.EvaluatorFactory;
import algorithms.factories.ScheduleBuilderFactory;
import algorithms.io.MSRCPSPIO;
import algorithms.problem.BaseIndividual;
import algorithms.problem.scheduling.Schedule;
import algorithms.problem.scheduling.schedule_builders.ScheduleBuilderType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BruteForceRunner {

  private static final Logger LOGGER = Logger.getLogger(BruteForceRunner.class.getName());
  private static String definitionFile = "assets/definitions/MSRCPSP_small/15_9_12_9.def";

  public static void main(String[] args) {
    run();
  }

  private static void run() {
    MSRCPSPIO reader = new MSRCPSPIO();
    Schedule schedule = reader.readDefinition(definitionFile);
    if (null == schedule) {
      LOGGER.log(Level.WARNING, "Could not read the Definition "
          + definitionFile);
      return;
    }
    ParameterSet<Integer, Schedule> parameters = new ParameterSet<>();
    parameters.upperBounds = schedule.getUpperBounds();
    parameters.hasSuccesors = schedule.getSuccesors();
    parameters.evaluator = new EvaluatorFactory().createEvaluator(EvaluatorType.FIVE_SCHEDULE_EVALUATOR, parameters.evalRate);
    parameters.converter = new ConverterFactory(parameters).createConverter(ConverterType.TRUNCATING);
    parameters.scheduleBuilder = new ScheduleBuilderFactory(parameters).createScheduleBuilder(ScheduleBuilderType.FORWARD_SCHEDULE_BUILDER);

    List<BaseIndividual<Integer, Schedule>> allIndividuals = new ArrayList<>();
    List<Integer> genes = new ArrayList<>(Collections.nCopies(schedule.getNumGenes(), 0));
    generateSolutions(allIndividuals, genes, 0, schedule, parameters);
    System.out.println(allIndividuals.size() + " Solutions Generated");
    for (BaseIndividual<Integer, Schedule> individual : allIndividuals) {
      individual.buildSolution(individual.getGenes(), parameters);
    }
    System.out.println("Solutions Built");
    allIndividuals = getNonDominated(allIndividuals);
    for (int i = 0; i < allIndividuals.size(); ++i) {
      BaseIndividual<Integer, Schedule> trial = allIndividuals.get(i);
      if (allIndividuals.stream().anyMatch(ind -> ind != trial && ind.compareTo(trial) == 0)) {
        allIndividuals.remove(trial);
        --i;
      }
    }
    System.out.println("Pareto Front Found: " + allIndividuals.size());

//    java.io.PrintStream o = null;
//    try {
//      o = new java.io.PrintStream(new java.io.File("10_3_5_3.csv"));
//    } catch (java.io.FileNotFoundException e) {
//      e.printStackTrace();
//    }
//    System.setOut(o);

    double[][] allObjectives = new double[5][];
    for (int i = 0; i < 5; ++i) {
      allObjectives[i] = new double[allIndividuals.size()];
    }

    int indIndex = 0;
    for (BaseIndividual<Integer, Schedule> individual : allIndividuals) {
      double[] objectives = individual.getObjectives();
      int objIndex = 0;
      for (double objective : objectives) {
//        System.out.print(objective + ";");
        allObjectives[objIndex++][indIndex] = objective;
      }
      indIndex++;
//      System.out.println();
    }

    for (double[] obj : allObjectives) {
      System.out.println(Arrays.stream(obj).distinct().count());
      System.out.println(Arrays.stream(obj).min().getAsDouble());
      System.out.println(Arrays.stream(obj).max().getAsDouble());
    }

  }

  private static void generateSolutions(List<BaseIndividual<Integer, Schedule>> allIndividuals, List<Integer> genes,
                                        int gene, Schedule schedule, ParameterSet<Integer, Schedule> parameters) {
    for (int i = 0; i < parameters.upperBounds[gene]; ++i) {
      genes.set(gene, i);
      if (gene + 1 < schedule.getNumGenes()) {
        generateSolutions(allIndividuals, genes, gene + 1, schedule, parameters);
      } else {
        allIndividuals.add(new BaseIndividual<>(schedule, genes, parameters.evaluator));
      }
    }
  }

  private static List<BaseIndividual<Integer, Schedule>> getNonDominated(List<BaseIndividual<Integer, Schedule>> population) {
    List<BaseIndividual<Integer, Schedule>> result = new ArrayList<>();
    for (BaseIndividual<Integer, Schedule> individual : population) {
      if (individual.isNotDominatedBy(population)) {
        result.add(individual);
      }
    }
    return result;
  }

}
