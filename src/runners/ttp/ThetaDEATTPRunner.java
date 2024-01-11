package runners.ttp;


import algorithms.evaluation.EvaluatorType;
import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.crossover.CrossoverType;
import algorithms.evolutionary_algorithms.genetic_algorithm.NondominatedTournamentGA;
import algorithms.evolutionary_algorithms.genetic_algorithm.ThetaDEA;
import algorithms.evolutionary_algorithms.initial_population.InitialPopulationType;
import algorithms.evolutionary_algorithms.mutation.MutationType;
import algorithms.evolutionary_algorithms.selection.SelectionType;
import algorithms.factories.*;
import algorithms.io.TTPIO;
import algorithms.problem.BaseIndividual;
import algorithms.problem.TTP;
import algorithms.problem.scheduling.Schedule;
import algorithms.quality_measure.EDMany;
import algorithms.quality_measure.HVMany;
import algorithms.quality_measure.ONVG;
import util.random.RandomInt;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThetaDEATTPRunner {

  private static final Logger LOGGER = Logger.getLogger( ThetaDEATTPRunner.class.getName() );
  private static final String definitionFile = "assets/definitions/TTP/selected/eil51_n250_uncorr_01.ttp";

  public static void main(String[] args) {
    run(args);
  }

  private static List<BaseIndividual<Integer, TTP>> run(String[] args) {
//    java.io.PrintStream o = null;
//    try {
//      o = new java.io.PrintStream(new java.io.File("ntga_ttp.csv"));
//    } catch (java.io.FileNotFoundException e) {
//      e.printStackTrace();
//    }
//    System.setOut(o);
//    String[] parts = definitionFile.split("/");
//    System.out.println(parts[parts.length - 1]);
//    System.out.println("Travelling Time;Profit");

    TTPIO reader = new TTPIO();
    TTP ttp = reader.readDefinition(definitionFile);
    if (null == ttp) {
      LOGGER.log(Level.WARNING, "Could not read the Definition " + definitionFile);
      return null;
    }

    ParameterSet<Integer, TTP> parameters = new ParameterSet<>();
    parameters.upperBounds = ttp.getUpperBounds();
    parameters.populationMultiplicationFactor = 1;
    int populationSize = 500;
    int generationLimit = 2000;
    double mutationProbability = 0.9;
    double crossoverProbability = 0.3;
    parameters.evalRate = 1.0;
    parameters.tournamentSize = 6;
    double theta = 0.1d;
    parameters.random = new RandomInt(System.currentTimeMillis());
    parameters.geneSplitPoint = ttp.getSplitPoint();

    parameters.initialPopulation = new InitialPopulationGeneratorFactory(parameters).createInitialPopulation(InitialPopulationType.RANDOM_TTP);
    parameters.selection = new SelectionFactory(parameters).createSelection(SelectionType.NONDOMINATED_SORTING_TOURNAMENT);
    parameters.crossover = new CrossoverFactory().createCrossover(CrossoverType.COMPETITION);
    parameters.mutation = new MutationFactory(parameters).createMutation(MutationType.COMPETITION);
    parameters.evaluator = new EvaluatorFactory().createEvaluator(EvaluatorType.MULTI_OBJECTIVE_TTP_EVALUATOR, parameters.evalRate);
    parameters.evaluator.setIndividual(new BaseIndividual<>(ttp, parameters.evaluator));

    ThetaDEA<TTP> geneticAlgorithm = new ThetaDEA<>(
        ttp, populationSize, generationLimit, parameters,
        mutationProbability, crossoverProbability, theta);
    List<BaseIndividual<Integer, TTP>> resultIndividuals = geneticAlgorithm.optimize();

//    System.out.println();
//    for (int i = 0; i < resultIndividuals.size(); ++i) {
//      double profit = 0;
//      double travellingTime = resultIndividuals.get(i).getProblem().getTravellingTime();
//      int[] selection = resultIndividuals.get(i).getProblem().getSelection();
//      for (int j = 0; j < selection.length; ++j) {
//        if (selection[j] > 0) {
//          profit += resultIndividuals.get(i).getProblem().getKnapsack().getItem(j).getProfit();
//        }
//      }
////      System.out.println("Profit: " + profit + "\t Travelling Time: " + travellingTime);
//      System.out.println(profit + ";" + travellingTime);
//    }

    EDMany ed = new EDMany(parameters.evaluator.getPerfectPoint());
    System.out.print(ed.getMeasure(resultIndividuals));
    System.out.print(" ");

    HVMany hv = new HVMany(parameters.evaluator.getNadirPoint());
    System.out.print(hv.getMeasure(resultIndividuals));
    System.out.print(" ");

    ONVG pfs = new ONVG();
    System.out.print(pfs.getMeasure(resultIndividuals));
    System.out.print(" ");

    return resultIndividuals;
  }

}
