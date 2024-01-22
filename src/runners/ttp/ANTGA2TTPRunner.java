package runners.ttp;

import algorithms.evaluation.EvaluatorType;
import algorithms.evaluation.MOTTPEvaluator;
import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.crossover.CrossoverType;
import algorithms.evolutionary_algorithms.genetic_algorithm.ANTGA2;
import algorithms.evolutionary_algorithms.genetic_algorithm.NondominatedSortingGA;
import algorithms.evolutionary_algorithms.genetic_algorithm.NondominatedTournamentGA;
import algorithms.evolutionary_algorithms.initial_population.InitialPopulationType;
import algorithms.evolutionary_algorithms.mutation.MutationType;
import algorithms.evolutionary_algorithms.selection.SelectionType;
import algorithms.factories.*;
import algorithms.io.TTPIO;
import algorithms.problem.BaseIndividual;
import algorithms.problem.TTP;
import algorithms.quality_measure.BaseMeasure;
import algorithms.quality_measure.HVMany;
import algorithms.quality_measure.Purity;
import util.random.RandomInt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ANTGA2TTPRunner {
    private static final Logger LOGGER = Logger.getLogger( ANTGA2TTPRunner.class.getName() );
    private static final String baseDir = "assets/definitions/TTP/selected_01/";
    private static final String[] files = new String[]{"kroA100_n990_uncorr_01.ttp"};//"eil51_n50_uncorr-similar-weights_01.ttp", "kroA100_n990_uncorr_01.ttp"
    public static void main(String[] args) {
        run(args);
    }

    private static List<BaseIndividual<Integer, TTP>> run(String[] args) {
        int generationLimit = 500;
        int populationSize = 1000;
        int maxAdditionalPopulationSize = populationSize / 2;
        int minAdditionalPopulationSize = populationSize / 10;
        double mutationProbability = 0.4;
        double crossoverProbability = 0.4;
        int clusterSize = 10;

        for (int k = 0; k < files.length; k++) {
            TTP ttp = readFile(k);
            if (ttp == null) return null;

            ParameterSet<Integer, TTP> parameters = setParameters(ttp);
            ANTGA2<TTP> geneticAlgorithm = new ANTGA2<>(
                    ttp,
                    populationSize,
                    generationLimit,
                    parameters,
                    mutationProbability,
                    crossoverProbability,
                    files[k].split("\\.")[0],
                    clusterSize,
                    maxAdditionalPopulationSize,
                    minAdditionalPopulationSize);

            var result = geneticAlgorithm.optimize();
            printResults(result);
        }
        return null;
    }

    private static TTP readFile(int k) {
        var definitionFile = baseDir + files[k];
        TTPIO reader = new TTPIO();
        TTP ttp = reader.readDefinition(definitionFile);
        if (null == ttp) {
            LOGGER.log(Level.WARNING, "Could not read the Definition " + definitionFile);
            return null;
        }
        return ttp;
    }

    private static ParameterSet<Integer, TTP> setParameters(TTP ttp) {
        ParameterSet<Integer, TTP> parameters = new ParameterSet<>();
        parameters.upperBounds = ttp.getUpperBounds();
        parameters.populationMultiplicationFactor = 1;
        parameters.evalRate = 1.0;
        parameters.tournamentSize = 6;
        parameters.random = new RandomInt(System.currentTimeMillis());
        parameters.geneSplitPoint = ttp.getSplitPoint();
        parameters.initialPopulation = new InitialPopulationGeneratorFactory(parameters).createInitialPopulation(InitialPopulationType.RANDOM_TTP);
        parameters.selection = new SelectionFactory(parameters).createSelection(SelectionType.NONDOMINATED_SORTING_NO_CROWDING_TOURNAMENT);
        parameters.crossover = new CrossoverFactory().createCrossover(CrossoverType.COMPETITION);
        parameters.mutation = new MutationFactory(parameters).createMutation(MutationType.COMPETITION);
        parameters.evaluator = new EvaluatorFactory().createEvaluator(EvaluatorType.MULTI_OBJECTIVE_TTP_EVALUATOR, parameters.evalRate);
        parameters.evaluator.setIndividual(new BaseIndividual<>(ttp, parameters.evaluator));
        return parameters;
    }

    private static void printResults(List<BaseIndividual<Integer, TTP>> resultIndividuals) {
        System.out.println("Profit; Travelling Time");
        for (int i = 0; i < resultIndividuals.size(); ++i) {
            double profit = 0;
            double travellingTime = resultIndividuals.get(i).getProblem().getTravellingTime();
            int[] selection = resultIndividuals.get(i).getProblem().getSelection();
            for (int j = 0; j < selection.length; ++j) {
                if (selection[j] > 0) {
                    profit += resultIndividuals.get(i).getProblem().getKnapsack().getItem(j).getProfit();
                }
            }

            System.out.println(profit + ";" + travellingTime);
        }
    }
}
