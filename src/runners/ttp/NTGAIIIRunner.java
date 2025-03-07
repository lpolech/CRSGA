package runners.ttp;

import algorithms.evaluation.EvaluatorType;
import algorithms.evaluation.MOTTPEvaluator;
import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.crossover.CrossoverType;
import algorithms.evolutionary_algorithms.genetic_algorithm.NTGAIII;
import algorithms.evolutionary_algorithms.initial_population.InitialPopulationType;
import algorithms.evolutionary_algorithms.mutation.MutationType;
import algorithms.factories.CrossoverFactory;
import algorithms.factories.EvaluatorFactory;
import algorithms.factories.InitialPopulationGeneratorFactory;
import algorithms.factories.MutationFactory;
import algorithms.io.TTPIO;
import algorithms.problem.BaseIndividual;
import algorithms.problem.TTP;
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
import java.util.stream.Collectors;

public class NTGAIIIRunner {

  private static final Logger LOGGER = Logger.getLogger( NTGAIIIRunner.class.getName() );
  private static final String definitionFile = "assets/definitions/TTP/selected/eil51_n250_uncorr_01.ttp";
  private static String tpfDir = "assets/points/PFs/ttp/";

  public static void main(String[] args) {
    run(args);
  }

  private static List<BaseIndividual<Integer, TTP>> run(String[] args) {
    TTPIO reader = new TTPIO();
    TTP ttp = reader.readDefinition(definitionFile);
    if (null == ttp) {
      LOGGER.log(Level.WARNING, "Could not read the Definition " + definitionFile);
      return null;
    }

    ParameterSet<Integer, TTP> parameters = new ParameterSet<>();
    parameters.upperBounds = ttp.getUpperBounds();
    parameters.populationMultiplicationFactor = 1;
    int populationSize = 100;
    int generationLimit = 5000;
    double mutationProbability = 0.1;
    double crossoverProbability = 0.3;
    parameters.evalRate = 1.0;
    parameters.tournamentSize = 6;
    parameters.random = new RandomInt(System.currentTimeMillis());
    parameters.geneSplitPoint = ttp.getSplitPoint();

    parameters.initialPopulation = new InitialPopulationGeneratorFactory(parameters).createInitialPopulation(InitialPopulationType.RANDOM_TTP);
    parameters.crossover = new CrossoverFactory().createCrossover(CrossoverType.TTP_COMPETITION);
    parameters.mutation = new MutationFactory(parameters).createMutation(MutationType.TTP_COMPETITION);
    parameters.evaluator = new EvaluatorFactory().createEvaluator(EvaluatorType.MULTI_OBJECTIVE_TTP_EVALUATOR, parameters.evalRate);
    parameters.evaluator.setIndividual(new BaseIndividual<>(ttp, parameters.evaluator));

    NTGAIII<TTP> geneticAlgorithm = new NTGAIII<>(ttp, populationSize, generationLimit,
        parameters, mutationProbability, crossoverProbability);
    List<BaseIndividual<Integer, TTP>> resultIndividuals = geneticAlgorithm.optimize();

    List<BaseIndividual<Integer, TTP>> tpf = getTPF(ttp, (MOTTPEvaluator<Integer>)parameters.evaluator);
    tpf.addAll(resultIndividuals);
    final List<BaseIndividual<Integer, TTP>> c = tpf;
    tpf = tpf.stream().filter(ind -> ind.isNotDominatedBy(c)).collect(Collectors.toList());

    BaseMeasure purity = new Purity(tpf);
    System.out.print(purity.getMeasure(resultIndividuals));
    System.out.print(" ");

//    EDMany ed = new EDMany(parameters.evaluator.getPerfectPoint());
//    System.out.print(ed.getMeasure(resultIndividuals));
//    System.out.print(" ");
//
//    HVMany hv = new HVMany(parameters.evaluator.getNadirPoint());
//    System.out.print(hv.getMeasure(resultIndividuals));
//    System.out.print(" ");
//
//    ONVG pfs = new ONVG();
//    System.out.print(pfs.getMeasure(resultIndividuals));
//    System.out.print(" ");

    return resultIndividuals;
  }

  private static List<BaseIndividual<Integer, TTP>> getTPF(TTP ttp, MOTTPEvaluator<Integer> evaluator) {
    List<BaseIndividual<Integer, TTP>> result = new ArrayList<>();
    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(tpfDir + definitionFile.substring(31).replace("ttp", "csv")));
      try {
        String line;
        String parts[];
        BaseIndividual<Integer, TTP> newGuy;
        while ((line = reader.readLine()) != null) {

          parts = line.split(";");

          newGuy = new BaseIndividual<>(ttp, new ArrayList<>(), evaluator);
          evaluator.setIndividual(newGuy);
          double[] objectives = new double[2];
          objectives[0] = Double.parseDouble(parts[0]);
          objectives[1] = Double.parseDouble(parts[1]);
          newGuy.setObjectives(objectives);

          objectives = new double[2];
          objectives[0] = Double.parseDouble(parts[0]) / (ttp.getMaxTravellingTime() - ttp.getMinTravellingTime());
          objectives[1] = Double.parseDouble(parts[1]) / ttp.getMaxProfit();
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
