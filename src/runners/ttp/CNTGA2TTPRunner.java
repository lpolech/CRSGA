package runners.ttp;

import algorithms.evaluation.EvaluatorType;
import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.crossover.CrossoverType;
import algorithms.evolutionary_algorithms.genetic_algorithm.ANTGA2;
import algorithms.evolutionary_algorithms.genetic_algorithm.CNTGA2;
import algorithms.evolutionary_algorithms.initial_population.InitialPopulationType;
import algorithms.evolutionary_algorithms.mutation.MutationType;
import algorithms.evolutionary_algorithms.selection.SelectionType;
import algorithms.factories.*;
import algorithms.io.TTPIO;
import algorithms.problem.BaseIndividual;
import algorithms.problem.TTP;
import algorithms.quality_measure.HVMany;
import util.random.RandomInt;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CNTGA2TTPRunner {
    private static final Logger LOGGER = Logger.getLogger( CNTGA2TTPRunner.class.getName() );
    private static final String baseDir = "./"; //assets/definitions/TTP/selected_01/";
    private static final String[] files = new String[]{"eil51_n50_uncorr-similar-weights_01.ttp"};//"kroA100_n990_uncorr_01.ttp"};//"kroA100_n297_bounded-strongly-corr_01.ttp"};//  "eil51_n50_uncorr-similar-weights_01.ttp", "kroA100_n990_uncorr_01.ttp"
    public static void main(String[] args) {
        run(args);
    }

    private static List<BaseIndividual<Integer, TTP>> run(String[] args) {
        for (int k = 0; k < files.length; k++) {
            TTP ttp = readFile(k);
            if (ttp == null) return null;

            int NUMBER_OF_REPEATS = 5;
            int[] generationLimitList = new int[] {1000};//500};
            int[] populationSizeList = new int[] {200};// 100};
            double[] TSPmutationProbabilityList = new double[] {0.8, 1.0};
            double[] KNAPmutationProbabilityList = new double[] {0.01};
            double[] TSPcrossoverProbabilityList = new double[] {0.1, 0.3, 0.5};
            double[] KNAPcrossoverProbabilityList = new double[] {0.6, 0.8};
            int[] numberOfClusterList = new int[]{7, 8, 9, 10};
            int[] clusterisationAlgorithmIterList = new int[]{100};//50};

            ArrayList<HashMap<String, Double>> cartesianProductOfParams = new ArrayList<>();
            for(int i = 0; i < generationLimitList.length; i++) {
                int generationLimitVal = generationLimitList[i];
                for(int j = 0; j < populationSizeList.length; j++) {
                    int populationSizeVal = populationSizeList[j];
                    for(int l = 0; l < TSPmutationProbabilityList.length; l++) {
                        double TSPmutationProbabilityVal = TSPmutationProbabilityList[l];
                        for(int x = 0; x < KNAPmutationProbabilityList.length; x++) {
                            double KANPmutationProbabilityVal = KNAPmutationProbabilityList[x];
                            for (int m = 0; m < TSPcrossoverProbabilityList.length; m++) {
                                double TSPcrossoverProbabilityVal = TSPcrossoverProbabilityList[m];
                                for (int y = 0; y < KNAPcrossoverProbabilityList.length; y++) {
                                    double KNAPcrossoverProbabilityVal = KNAPcrossoverProbabilityList[y];
                                    for (int n = 0; n < numberOfClusterList.length; n++) {
                                        int numberOfClusterVal = numberOfClusterList[n];
                                        for (int o = 0; o < clusterisationAlgorithmIterList.length; o++) {
                                            int clusterisationAlgorithmIterVal = clusterisationAlgorithmIterList[o];

                                            var paramsMap = new HashMap<String, Double>();
                                            paramsMap.put("generationLimit", (double) generationLimitVal);
                                            paramsMap.put("populationSize", (double) populationSizeVal);
                                            paramsMap.put("TSPmutationProbability", TSPmutationProbabilityVal);
                                            paramsMap.put("KNAPmutationProbability", KANPmutationProbabilityVal);
                                            paramsMap.put("TSPcrossoverProbability", TSPcrossoverProbabilityVal);
                                            paramsMap.put("KNAPcrossoverProbability", KNAPcrossoverProbabilityVal);
                                            paramsMap.put("numberOfClusters", (double) numberOfClusterVal);
                                            paramsMap.put("clusterIterLimit", (double) clusterisationAlgorithmIterVal);

                                            cartesianProductOfParams.add(paramsMap);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Collections.shuffle(cartesianProductOfParams);
            System.out.println("counter;\tavgHV;\tstdev;\tgenerationLimit;\tpopulationSize;\tTSPmutationProbability" +
                    ";\tKNAPmutationProbability;\tTSPcrossoverProbability;\tKNAPcrossoverProbability;\tnumberOfClusters" +
                    ";\tclusterIterLimit");
            int paramCounter = 0;
            int numberOfParamConfigs = cartesianProductOfParams.size();
            for(var params: cartesianProductOfParams) {
                var eachRepeatHV = new ArrayList<Double>();
                paramCounter += 1;

                int generationLimit = params.get("generationLimit").intValue();
                int populationSize = params.get("populationSize").intValue();
                int maxAdditionalPopulationSize = populationSize / 2;
                int minAdditionalPopulationSize = populationSize / 10;
                double TSPmutationProbability = params.get("TSPmutationProbability");
                double KNAPmutationProbability = params.get("KNAPmutationProbability");
                double TSPcrossoverProbability = params.get("TSPcrossoverProbability");
                double KNAPcrossoverProbability = params.get("KNAPcrossoverProbability");
                int numberOfClusters = params.get("numberOfClusters").intValue();
                int clusterIterLimit = params.get("clusterIterLimit").intValue();

                for(int i = 0; i < NUMBER_OF_REPEATS; i++) {
                    ParameterSet<Integer, TTP> parameters = setParameters(ttp);
                    HVMany hv = new HVMany(parameters.evaluator.getNadirPoint());
                    CNTGA2<TTP> geneticAlgorithm = new CNTGA2<>(
                            ttp,
                            populationSize,
                            generationLimit,
                            parameters,
                            TSPmutationProbability,
                            KNAPmutationProbability,
                            TSPcrossoverProbability,
                            KNAPcrossoverProbability,
                            files[k].split("\\.")[0],
                            numberOfClusters,
                            clusterIterLimit,
                            maxAdditionalPopulationSize,
                            minAdditionalPopulationSize);

                    var result = geneticAlgorithm.optimize();
                    //            printResults(result);
                    eachRepeatHV.add(hv.getMeasure(result));
                }

                OptionalDouble average = eachRepeatHV
                        .stream()
                        .mapToDouble(a -> a)
                        .average();
                var avgHV = average.isPresent() ? average.getAsDouble() : -666.0;

                double standardDeviation = 0.0;
                for(double num: eachRepeatHV) {
                    standardDeviation += Math.pow(num - avgHV, 2);
                }

                standardDeviation = Math.sqrt(standardDeviation/eachRepeatHV.size());

                System.out.println(paramCounter + "/" + numberOfParamConfigs + ";\t" + avgHV + ";\t" + standardDeviation
                        + ";\t" + generationLimit + ";\t" + populationSize + ";\t" + TSPmutationProbability
                        + ";\t" + KNAPmutationProbability + ";\t" + TSPcrossoverProbability + ";\t" + KNAPcrossoverProbability
                        + ";\t" + numberOfClusters + ";\t" + clusterIterLimit);
            }
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
