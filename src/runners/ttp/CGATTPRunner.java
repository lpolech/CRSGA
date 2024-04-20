package runners.ttp;

import algorithms.evaluation.EvaluatorType;
import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.crossover.CrossoverType;
import algorithms.evolutionary_algorithms.genetic_algorithm.CGA;
import algorithms.evolutionary_algorithms.genetic_algorithm.utils.OptimisationResult;
import algorithms.evolutionary_algorithms.initial_population.InitialPopulationType;
import algorithms.evolutionary_algorithms.mutation.MutationType;
import algorithms.evolutionary_algorithms.selection.SelectionType;
import algorithms.factories.*;
import algorithms.io.TTPIO;
import algorithms.problem.BaseIndividual;
import algorithms.problem.TTP;
import algorithms.quality_measure.HVMany;
import distance_measures.Euclidean;
import interfaces.QualityMeasure;
import internal_measures.FlatWithinBetweenIndex;
import util.random.RandomInt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CGATTPRunner {
    private static final Logger LOGGER = Logger.getLogger( CGATTPRunner.class.getName() );
    private static final String baseDir = "." + File.separator; //assets/definitions/TTP/selected_01/";
    private static final String[] files = new String[]{
//            "eil51_n50_bounded-strongly-corr_01.ttp", "eil51_n50_uncorr_01.ttp", "eil51_n50_uncorr-similar-weights_01.ttp",
//            "eil51_n150_bounded-strongly-corr_01.ttp", "eil51_n150_uncorr_01.ttp", "eil51_n150_uncorr-similar-weights_01.ttp",
//            "eil51_n250_bounded-strongly-corr_01.ttp", "eil51_n250_uncorr_01.ttp", "eil51_n250_uncorr-similar-weights_01.ttp",
            "eil51_n500_bounded-strongly-corr_01.ttp"//, "eil51_n500_uncorr_01.ttp", "eil51_n500_uncorr-similar-weights_01.ttp",
            };//"kroA100_n990_uncorr_01.ttp"};//"kroA100_n297_bounded-strongly-corr_01.ttp"};//  "eil51_n50_uncorr-similar-weights_01.ttp", "kroA100_n990_uncorr_01.ttp"
    public static void main(String[] args) {
        run(args);
    }

    private static List<BaseIndividual<Integer, TTP>> run(String[] args) {
        for (int k = 0; k < files.length; k++) {
            TTP ttp = readFile(k);
            if (ttp == null) return null;

            QualityMeasure[] clusterWeightMeasureList = new QualityMeasure[] {
//                new FlatCalinskiHarabasz(new Euclidean()), //this measures is sensitive to useSubtree toggle
//                new FlatDaviesBouldin(new Euclidean()), //this measures is sensitive to useSubtree toggle
//                new FlatDunn1(new Euclidean()), //this measures is sensitive to useSubtree toggle
//                new FlatDunn4(new Euclidean()), //this measures is sensitive to useSubtree toggle
                new FlatWithinBetweenIndex(new Euclidean()), //this measures is sensitive to useSubtree toggle
//                new FlatDunn2(new Euclidean()),
//                new FlatDunn3(new Euclidean())
            };

            int NUMBER_OF_REPEATS = 30;
            int[] generationLimitList = new int[] {50_000};//{250_000};//{50_000};//{250_000};//{5_000};//{5_000};//{25_000, 12_500, 5_000, 2_500, 1_666, 1_250, 500, 250};//500};
            int[] populationSizeList = new int[] {500};//{20};//{10, 20, 50, 100};//{50};// 100};
            double[] TSPmutationProbabilityList = new double[] {0.01};//{0.007};//{0.002, 0.004, 0.006, 0.008};//{0.004};//{0.0, 0.0001, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.9};//{0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.0, 0.0001, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6}; //{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
            double[] KNAPmutationProbabilityList = new double[] {0.01};//{0.006};//{0.004, 0.005, 0.006, 0.007};//{0.01};//{0.01, 0.02, 0.03, 0.04};//, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.0, 0.0025, 0.005, 0.0075}; //{0.005, 0.01, 0.015};//, 0.005, 0.015};
            double[] TSPcrossoverProbabilityList = new double[] {0.5};//{0.2};//{0.0, 0.05, 0.1, 0.15, 0.2, 0.25, 0.3};//{0.5};//{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.0, 0.05, 0.1, 0.15, 0.2}; //{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
            double[] KNAPcrossoverProbabilityList = new double[] {0.1};//{0.95};//{0.75, 0.8, 0.85, 0.9, 0.95, 1.0, 1.5};//{0.7};//{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.05, 0.1, 0.2, 0.3, 0.4, 0.5};//{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
            int[] numberOfClusterList = new int[]{20};//{2};//{2, 3, 4, 5, 10, 20};//{3};
            int[] clusterisationAlgorithmIterList = new int[]{50};//100};
            double[] edgeClustersDispersion = new double[]{0.5};//{4};//{0.1, 0.5, 1, 2, 4, 10, 100};//{4}//{0.05, 0.1, 0.3, 0.5, 0.7, 0.9, 1, 4, 5, 10.0, 50, 100, 1_000, 5_000}; //{4};//, 10_000, 15_000, 20_000, 50_000, 100_000};//{0.1, 0.2, 0.3, 0.4, 0.5, 1.0, 1.5, 2.0};//{0.5, 1.0, 1.5, 2.0}; //}{0.1, 0.2, 0.3, 0.4, 0.5, 1.0, 1.5, 2.0};
            int[] tournamentSizeList = new int[]{10};//{80};//{10, 20, 30, 40, 50, 60, 70, 80, 90, 100}; //{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30, 50, 100}; //{90};

            ArrayList<HashMap<String, Object>> cartesianProductOfParams = new ArrayList<>();
            for(int wmNum = 0; wmNum < clusterWeightMeasureList.length; wmNum++) {
                QualityMeasure clusterWeightMeasureVal = clusterWeightMeasureList[wmNum];
                for (int i = 0; i < generationLimitList.length; i++) {
                    int generationLimitVal = generationLimitList[i];
                    for (int j = 0; j < populationSizeList.length; j++) {
                        int populationSizeVal = populationSizeList[j];
                        for (int l = 0; l < TSPmutationProbabilityList.length; l++) {
                            double TSPmutationProbabilityVal = TSPmutationProbabilityList[l];
                            for (int x = 0; x < KNAPmutationProbabilityList.length; x++) {
                                double KANPmutationProbabilityVal = KNAPmutationProbabilityList[x];
                                for (int m = 0; m < TSPcrossoverProbabilityList.length; m++) {
                                    double TSPcrossoverProbabilityVal = TSPcrossoverProbabilityList[m];
                                    for (int y = 0; y < KNAPcrossoverProbabilityList.length; y++) {
                                        double KNAPcrossoverProbabilityVal = KNAPcrossoverProbabilityList[y];
                                        for (int n = 0; n < numberOfClusterList.length; n++) {
                                            int numberOfClusterVal = numberOfClusterList[n];
                                            for (int o = 0; o < clusterisationAlgorithmIterList.length; o++) {
                                                int clusterisationAlgorithmIterVal = clusterisationAlgorithmIterList[o];
                                                for (int p = 0; p < edgeClustersDispersion.length; p++) {
                                                    double edgeClustersDispersionVal = edgeClustersDispersion[p];
                                                    for (int q = 0; q < tournamentSizeList.length; q++) {
                                                        int tournamentSize = tournamentSizeList[q];
                                                        var cost = generationLimitVal * populationSizeVal;
//                                                        if(cost < 200_000 || cost > 250_000){
//                                                            System.out.print("mama");
//                                                            continue;
//                                                        } else {
//                                                            System.out.print("TATA " + generationLimitVal + " " + populationSizeVal + "\n");
                                                            var paramsMap = new HashMap<String, Object>();
                                                            paramsMap.put("clusterWeightMeasure", clusterWeightMeasureVal);
                                                            paramsMap.put("generationLimit", generationLimitVal);
                                                            paramsMap.put("populationSize", populationSizeVal);
                                                            paramsMap.put("TSPmutationProbability", TSPmutationProbabilityVal);
                                                            paramsMap.put("KNAPmutationProbability", KANPmutationProbabilityVal);
                                                            paramsMap.put("TSPcrossoverProbability", TSPcrossoverProbabilityVal);
                                                            paramsMap.put("KNAPcrossoverProbability", KNAPcrossoverProbabilityVal);
                                                            paramsMap.put("numberOfClusters", numberOfClusterVal);
                                                            paramsMap.put("clusterIterLimit", clusterisationAlgorithmIterVal);
                                                            paramsMap.put("edgeClustersDispersion", edgeClustersDispersionVal);
                                                            paramsMap.put("tournamentSize", tournamentSize);

                                                            cartesianProductOfParams.add(paramsMap);
//                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            System.out.println("Number of param configurations: " + cartesianProductOfParams.size());
            Collections.shuffle(cartesianProductOfParams);
            String header = "dataset;counter;measure;no of repeats;avgHV;stdev;avgND;stdev;uber pareto size;final uber pareto HV;avg uber pareto hv;stdev;"
                    + ";" + "AvgAfterCrossParentDominationCounter"
                    + ";" + "AvgAfterCrossParentDominationProp"
                    + ";" + "AvgAfterCrossAndMutParentDominationCounter"
                    + ";" + "AvgAfterCrossAndMutParentDominationProp"
                    + ";" + "AvgAfterCrossAfterCrossAndMutDominationCounter"
                    + ";" + "AvgAfterCrossAfterCrossAndMutDominationProp"
                    + ";" + "AvgAfterCrossAndMutAfterCrossDominationCounter"
                    + ";" + "AvgAfterCrossAndMutAfterCrossDominationProp"
                    + "generationLimit;populationSize;TSPmutationProbability" +
                    ";KNAPmutationProbability;TSPcrossoverProbability;KNAPcrossoverProbability;numberOfClusters" +
                    ";clusterIterLimit;edgeClustersProb;tournamentSize";

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
                var eachRepeatUberParetoHV = new ArrayList<Double>();
                var eachRepeatND = new ArrayList<Integer>();
                var eachRepeatOptimisationResult = new ArrayList<OptimisationResult>();
                paramCounter += 1;

                QualityMeasure clusterWeightMeasure = (QualityMeasure) params.get("clusterWeightMeasure");
                int generationLimit = (int) params.get("generationLimit");
                int populationSize = (int) params.get("populationSize");
                int maxAdditionalPopulationSize = populationSize / 2;
                int minAdditionalPopulationSize = populationSize / 10;
                double TSPmutationProbability = (double) params.get("TSPmutationProbability");
                double KNAPmutationProbability = (double) params.get("KNAPmutationProbability");
                double TSPcrossoverProbability = (double) params.get("TSPcrossoverProbability");
                double KNAPcrossoverProbability = (double) params.get("KNAPcrossoverProbability");
                int numberOfClusters = (int) params.get("numberOfClusters");
                int clusterIterLimit = (int) params.get("clusterIterLimit");
                double edgeClustersDispVal = (double) params.get("edgeClustersDispersion");
                int tournamentSize = (int) params.get("tournamentSize");

                List<BaseIndividual<Integer, TTP>> bestAPF = null;
                double bestAPFHV = -Double.MIN_VALUE;

                String outputFilename = "." + File.separator + files[k] + "_measure-" + clusterWeightMeasure.getClass().getName()
                        + "_genLmt-" + generationLimit + "_popSiz-" + populationSize
                        + "_maxAddPopSiz-" + maxAdditionalPopulationSize + "_minAddPopSiz-" + minAdditionalPopulationSize
                        + "_TSPmutP-" + TSPmutationProbability + "_KNAPmutP-" + KNAPmutationProbability
                        + "_TSPcrP-" + TSPcrossoverProbability + "_KNAPcrP-" + KNAPcrossoverProbability
                        + "_noClus-" + numberOfClusters + "_clsIterLmt-" + clusterIterLimit + "_edgClusDispVal-"
                        + edgeClustersDispVal + "_tourSize-" + tournamentSize;

                String bestAPFoutputFile = "bestAPF";
                int bestIterNumber = 0;
                File theDir = new File(outputFilename);
                if (!theDir.exists()){
                    theDir.mkdirs();
                }

                List<BaseIndividual<Integer, TTP>> uberPareto = new ArrayList<>();
                for(int i = 0; i < NUMBER_OF_REPEATS; i++) {
                    ParameterSet<Integer, TTP> parameters = setParameters(ttp);
                    HVMany hv = new HVMany(parameters.evaluator.getNadirPoint());
                    CGA<TTP> geneticAlgorithm = new CGA<>(
                            ttp,
                            clusterWeightMeasure,
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
                            edgeClustersDispVal,
                            tournamentSize,
                            maxAdditionalPopulationSize,
                            minAdditionalPopulationSize,
                            -666, true);

                    var result = geneticAlgorithm.optimize();
                    uberPareto = geneticAlgorithm.getNondominatedFromTwoLists(result, uberPareto);
                    //            printResults(result);
                    eachRepeatUberParetoHV.add(hv.getMeasure(uberPareto));
                    var hvValue = hv.getMeasure(result);
                    eachRepeatHV.add(hvValue);
                    eachRepeatND.add(result.size());

                    if(hvValue > bestAPFHV) {
                        bestAPFHV = hvValue;
                        bestAPF = result;
                        bestIterNumber = i;
                    }

                    eachRepeatOptimisationResult.add(geneticAlgorithm.getOptimisationResult());

                    String instanceName = files[k];
                    if(instanceName.endsWith(".ttp"))
                        instanceName = instanceName.substring(0, instanceName.lastIndexOf(".ttp"));

                    try {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename
                                + File.separator + instanceName + "_config0_run" + i + "_archive.csv"));
                        writer.write(printResults(result, false));
                        writer.close();

                        writer = new BufferedWriter(new FileWriter(outputFilename
                                + File.separator + instanceName + "_" + i + "_UBER_PARETO.csv"));
                        writer.write(printResults(uberPareto, false));
                        writer.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    };
                }


                OptionalDouble NDaverage = eachRepeatND
                        .stream()
                        .mapToDouble(a -> a)
                        .average();
                var avgND = NDaverage.isPresent() ? NDaverage.getAsDouble() : -666.0;

                double NDstandardDeviation = 0.0;
                for(double num: eachRepeatND) {
                    NDstandardDeviation += Math.pow(num - avgND, 2);
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


                OptionalDouble averageUberHv = eachRepeatUberParetoHV
                        .stream()
                        .mapToDouble(a -> a)
                        .average();
                var uberParetoHV = averageUberHv.isPresent() ? averageUberHv.getAsDouble() : -666.0;

                double uberParetostdev = 0.0;
                for(double num: eachRepeatUberParetoHV) {
                    uberParetostdev += Math.pow(num - uberParetoHV, 2);
                }

                uberParetostdev = Math.sqrt(uberParetostdev/eachRepeatUberParetoHV.size());

                String runResult = files[k] + ";" + paramCounter + "/" + numberOfParamConfigs + ";"
                        + clusterWeightMeasure.getClass().getName() + ";" + NUMBER_OF_REPEATS + ";" + avgHV + ";" + standardDeviation
                        + ";" + avgND + ";" + NDstandardDeviation + ";" + uberPareto.size() + ";" +
                        eachRepeatUberParetoHV.get(eachRepeatUberParetoHV.size()-1) + ";" + uberParetoHV + ";" + uberParetostdev
                        + ";" + OptimisationResult.getAvgAfterCrossParentDominationCounter(eachRepeatOptimisationResult)
                        + ";" + OptimisationResult.getAvgAfterCrossParentDominationProp(eachRepeatOptimisationResult)
                        + ";" + OptimisationResult.getAvgAfterCrossAndMutParentDominationCounter(eachRepeatOptimisationResult)
                        + ";" + OptimisationResult.getAvgAfterCrossAndMutParentDominationProp(eachRepeatOptimisationResult)
                        + ";" + OptimisationResult.getAvgAfterCrossAfterCrossAndMutDominationCounter(eachRepeatOptimisationResult)
                        + ";" + OptimisationResult.getAvgAfterCrossAfterCrossAndMutDominationProp(eachRepeatOptimisationResult)
                        + ";" + OptimisationResult.getAvgAfterCrossAndMutAfterCrossDominationCounter(eachRepeatOptimisationResult)
                        + ";" + OptimisationResult.getAvgAfterCrossAndMutAfterCrossDominationProp(eachRepeatOptimisationResult)
                        + ";" + generationLimit
                        + ";" + populationSize + ";" + TSPmutationProbability
                        + ";" + KNAPmutationProbability + ";" + TSPcrossoverProbability + ";" + KNAPcrossoverProbability
                        + ";" + numberOfClusters + ";" + clusterIterLimit + ";" + edgeClustersDispVal + ";" + tournamentSize;
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
                    BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename + File.separator
                            + bestAPFoutputFile + bestIterNumber + ".csv"));
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
