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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CNTGA2TTPRunner {
    private static final Logger LOGGER = Logger.getLogger( CNTGA2TTPRunner.class.getName() );
    private static final String baseDir = "." + File.separator; //assets/definitions/TTP/selected_01/";
    private static final String[] files = new String[]{
            "eil51_n50_bounded-strongly-corr_01.ttp", "eil51_n50_uncorr_01.ttp", "eil51_n50_uncorr-similar-weights_01.ttp",
            "eil51_n150_bounded-strongly-corr_01.ttp", "eil51_n150_uncorr_01.ttp", "eil51_n150_uncorr-similar-weights_01.ttp",
            "eil51_n250_bounded-strongly-corr_01.ttp", "eil51_n250_uncorr_01.ttp", "eil51_n250_uncorr-similar-weights_01.ttp",
            "eil51_n500_bounded-strongly-corr_01.ttp", "eil51_n500_uncorr_01.ttp", "eil51_n500_uncorr-similar-weights_01.ttp",
            };//"kroA100_n990_uncorr_01.ttp"};//"kroA100_n297_bounded-strongly-corr_01.ttp"};//  "eil51_n50_uncorr-similar-weights_01.ttp", "kroA100_n990_uncorr_01.ttp"
    public static void main(String[] args) {
        run(args);
    }

    private static List<BaseIndividual<Integer, TTP>> run(String[] args) {
        for (int k = 0; k < files.length; k++) {
            TTP ttp = readFile(k);
            if (ttp == null) return null;

            int NUMBER_OF_REPEATS = 30;
            int[] generationLimitList = new int[] {1000};//500};
            int[] populationSizeList = new int[] {200};// 100};
            double[] TSPmutationProbabilityList = new double[] {0.7};//, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
            double[] KNAPmutationProbabilityList = new double[] {0.005};//01};//, 0.005, 0.015};
            double[] TSPcrossoverProbabilityList = new double[] {0.5};//, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
            double[] KNAPcrossoverProbabilityList = new double[] {-666.00};
            int[] numberOfClusterList = new int[]{8};//, 2, 3, 4, 5, 6, 7, 8, 9, 10};
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
            String header = "dataset;counter;avgHV;stdev;generationLimit;populationSize;TSPmutationProbability" +
                    ";KNAPmutationProbability;TSPcrossoverProbability;KNAPcrossoverProbability;numberOfClusters" +
                    ";clusterIterLimit";
            System.out.println(header);
            try {
                File f = new File(baseDir + "result.csv");
                if(!f.exists()) {
                    FileWriter fw = new FileWriter(baseDir + "result.csv", true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(header);
                    bw.newLine();
                    bw.close();
                }
            } catch(IOException e) {
                e.printStackTrace();
            }

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
                List<BaseIndividual<Integer, TTP>> bestAPF = null;
                double bestAPFHV = -Double.MIN_VALUE;

                String outputFilename = "." + File.separator + files[k] + "_genLmt-" + generationLimit + "_popSiz-" + populationSize
                        + "_maxAddPopSiz-" + maxAdditionalPopulationSize + "_minAddPopSiz-" + minAdditionalPopulationSize
                        + "_TSPmutP-" + TSPmutationProbability + "_KNAPmutP-" + KNAPmutationProbability
                        + "_TSPcrP-" + TSPcrossoverProbability + "_KNAPcrP-" + KNAPcrossoverProbability
                        + "_noClus-" + numberOfClusters + "_clsIterLmt-" + clusterIterLimit;

                String bestAPFoutputFile = "bestAPF";
                int bestIterNumber = 0;
                File theDir = new File(outputFilename);
                if (!theDir.exists()){
                    theDir.mkdirs();
                }

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
                    var hvValue = hv.getMeasure(result);
                    eachRepeatHV.add(hvValue);

                    if(hvValue > bestAPFHV) {
                        bestAPFHV = hvValue;
                        bestAPF = result;
                        bestIterNumber = i;
                    }

                    try {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename + File.separator + "individual" + i + ".csv"));
                        writer.write(printResults(result, false));
                        writer.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    };
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

                String runResult = files[k] + ";" + paramCounter + "/" + numberOfParamConfigs + ";" + avgHV + ";" + standardDeviation
                        + ";" + generationLimit + ";" + populationSize + ";" + TSPmutationProbability
                        + ";" + KNAPmutationProbability + ";" + TSPcrossoverProbability + ";" + KNAPcrossoverProbability
                        + ";" + numberOfClusters + ";" + clusterIterLimit;
                System.out.println(runResult);
                try {

                    FileWriter fw = new FileWriter(baseDir + "result.csv", true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(runResult);
                    bw.newLine();
                    bw.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }

                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename + File.separator + bestAPFoutputFile + bestIterNumber + ".csv"));
                    writer.write(printResults(bestAPF, false));
                    writer.close();
                } catch(IOException e) {
                    e.printStackTrace();
                };
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

    private static String printResults(List<BaseIndividual<Integer, TTP>> resultIndividuals, boolean isVerbose) {
        String output = "";
        if(isVerbose) {
            output += "Profit; Travelling Time\n";
            System.out.println("Profit; Travelling Time");
        }
        for (int i = 0; i < resultIndividuals.size(); ++i) {
            double profit = 0;
            double travellingTime = resultIndividuals.get(i).getProblem().getTravellingTime();
            int[] selection = resultIndividuals.get(i).getProblem().getSelection();
            for (int j = 0; j < selection.length; ++j) {
                if (selection[j] > 0) {
                    profit += resultIndividuals.get(i).getProblem().getKnapsack().getItem(j).getProfit();
                }
            }
            output += travellingTime + ";" + (-1)*profit + "\n";
            if(isVerbose) {
                System.out.println(travellingTime + ";" + (-1)*profit);
            }
        }
        return output;
    }
}
