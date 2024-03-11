package runners.msrcpsp;

import algorithms.evaluation.EvaluatorType;
import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.converters.ConverterType;
import algorithms.evolutionary_algorithms.crossover.CrossoverType;
import algorithms.evolutionary_algorithms.genetic_algorithm.CNTGA2;
import algorithms.evolutionary_algorithms.initial_population.InitialPopulationType;
import algorithms.evolutionary_algorithms.mutation.MutationType;
import algorithms.evolutionary_algorithms.selection.SelectionType;
import algorithms.factories.*;
import algorithms.io.MSRCPSPIO;
import algorithms.io.TTPIO;
import algorithms.problem.BaseIndividual;
import algorithms.problem.TTP;
import algorithms.problem.scheduling.Schedule;
import algorithms.problem.scheduling.schedule_builders.ScheduleBuilderType;
import algorithms.quality_measure.ConvergenceMeasure;
import algorithms.quality_measure.HVMany;
import algorithms.quality_measure.ONVG;
import algorithms.quality_measure.Spacing;
import internal_measures.*;
import util.random.RandomInt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CNTGA2MSRCPSPRunner {
    private static final Logger LOGGER = Logger.getLogger( CNTGA2MSRCPSPRunner.class.getName() );
    private static final String baseDir = "." + File.separator; //assets/definitions/MSRCPSP/";
    private static final String[] files = new String[]{"100_10_26_15.def"};
    public static void main(String[] args) {
        run(args);
    }

    private static List<BaseIndividual<Integer, TTP>> run(String[] args) {
        for (int k = 0; k < files.length; k++) {
            Schedule schedule = readFile(k);
            if (null == schedule) {
                LOGGER.log(Level.WARNING, "Could not read the Definition "
                        + files[k]);
                return null;
            }

            int NUMBER_OF_REPEATS = 5;
            int[] generationLimitList = new int[] {1_000};//{5_000};//{25_000, 12_500, 5_000, 2_500, 1_666, 1_250, 500, 250};//500};
            int[] populationSizeList = new int[] {50};//{10, 20, 50, 100, 150, 200, 500, 1000};// 100};
            double[] mutationProbabilityList = new double[] {0.004};//{0.0, 0.0001, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.9};//{0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.0, 0.0001, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6}; //{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
            double[] crossoverProbabilityList = new double[] {0.7};//{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.05, 0.1, 0.2, 0.3, 0.4, 0.5};//{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
            int[] numberOfClusterList = new int[]{3};
            int[] clusterisationAlgorithmIterList = new int[]{50};//100};
            double[] edgeClustersDispersion = new double[]{4};//{0, 0.001, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1, 5, 10.0, 50, 100, 1_000, 5_000, 10_000, 15_000, 20_000, 50_000, 100_000};//{0.1, 0.2, 0.3, 0.4, 0.5, 1.0, 1.5, 2.0};//{0.5, 1.0, 1.5, 2.0}; //}{0.1, 0.2, 0.3, 0.4, 0.5, 1.0, 1.5, 2.0};
            int[] tournamentSizeList = new int[]{30};//{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 50, 100};
            boolean enhanceDiversity = true;
            double diversityThreshold = 0.8;

            ArrayList<HashMap<String, Double>> cartesianProductOfParams = new ArrayList<>();
            for(int i = 0; i < generationLimitList.length; i++) {
                int generationLimitVal = generationLimitList[i];
                for(int j = 0; j < populationSizeList.length; j++) {
                    int populationSizeVal = populationSizeList[j];
                    for(int l = 0; l < mutationProbabilityList.length; l++) {
                        double mutationProbabilityVal = mutationProbabilityList[l];
                        for (int m = 0; m < crossoverProbabilityList.length; m++) {
                            double crossoverProbabilityVal = crossoverProbabilityList[m];
                            for (int n = 0; n < numberOfClusterList.length; n++) {
                                int numberOfClusterVal = numberOfClusterList[n];
                                for (int o = 0; o < clusterisationAlgorithmIterList.length; o++) {
                                    int clusterisationAlgorithmIterVal = clusterisationAlgorithmIterList[o];
                                    for(int p = 0; p < edgeClustersDispersion.length; p++) {
                                        double edgeClustersDispersionVal = edgeClustersDispersion[p];
                                        for(int q = 0; q < tournamentSizeList.length; q++) {
                                            int tournamentSize = tournamentSizeList[q];

                                            var paramsMap = new HashMap<String, Double>();
                                            paramsMap.put("generationLimit", (double) generationLimitVal);
                                            paramsMap.put("populationSize", (double) populationSizeVal);
                                            paramsMap.put("mutationProbability", mutationProbabilityVal);
                                            paramsMap.put("crossoverProbability", crossoverProbabilityVal);
                                            paramsMap.put("numberOfClusters", (double) numberOfClusterVal);
                                            paramsMap.put("clusterIterLimit", (double) clusterisationAlgorithmIterVal);
                                            paramsMap.put("edgeClustersDispersion", edgeClustersDispersionVal);
                                            paramsMap.put("tournamentSize", (double) tournamentSize);

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
            String header = "dataset;counter;no of repeats;avgHV;stdev;avgND;stdev;uber pareto size;final uber pareto HV;avg uber pareto hv;stdev;"
                    + "generationLimit;populationSize;mutationProbability;crossoverProbability;numberOfClusters" +
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
                var eachRepeatCM = new ArrayList<Double>();
                var eachRepeatSpacing = new ArrayList<Double>();
                var eachRepeatUberParetoHV = new ArrayList<Double>();
                var eachRepeatND = new ArrayList<Integer>();
                paramCounter += 1;

                int generationLimit = params.get("generationLimit").intValue();
                int populationSize = params.get("populationSize").intValue();
                int maxAdditionalPopulationSize = populationSize / 2;
                int minAdditionalPopulationSize = populationSize / 10;
                double mutationProbability = params.get("mutationProbability");
                double crossoverProbability = params.get("crossoverProbability");
                int numberOfClusters = params.get("numberOfClusters").intValue();
                int clusterIterLimit = params.get("clusterIterLimit").intValue();
                double edgeClustersDispVal = params.get("edgeClustersDispersion");
                int tournamentSize = params.get("tournamentSize").intValue();

                List<BaseIndividual<Integer, Schedule>> bestAPF = null;
                double bestAPFHV = -Double.MIN_VALUE;

                String outputFilename = "." + File.separator + files[k] + "_genLmt-" + generationLimit + "_popSiz-"
                        + populationSize + "_maxAddPopSiz-" + maxAdditionalPopulationSize + "_minAddPopSiz-" + minAdditionalPopulationSize
                        + "_mutP-" + mutationProbability + "_crP-" + crossoverProbability + "_noClus-"
                        + numberOfClusters + "_clsIterLmt-" + clusterIterLimit + "_edgClusDispVal-"
                        + edgeClustersDispVal + "_tourSize-" + tournamentSize;

                String bestAPFoutputFile = "bestAPF";
                int bestIterNumber = 0;
                File theDir = new File(outputFilename);
                if (!theDir.exists()){
                    theDir.mkdirs();
                }

                List<BaseIndividual<Integer, Schedule>> uberPareto = new ArrayList<>();
                for(int i = 0; i < NUMBER_OF_REPEATS; i++) {
                    ParameterSet<Integer, Schedule> parameters = setParameters(schedule);
                    HVMany hv = new HVMany(parameters.evaluator.getNadirPoint());
                    ConvergenceMeasure ed = new ConvergenceMeasure(parameters.evaluator.getPerfectPoint());
                    Spacing spacing = new Spacing();
                    CNTGA2<Schedule> geneticAlgorithm = new CNTGA2<>(
                            schedule,
                            null,
                            populationSize,
                            generationLimit,
                            parameters,
                            mutationProbability,
                            -666,
                            crossoverProbability,
                            -666,
                            files[k].split("\\.")[0],
                            numberOfClusters,
                            clusterIterLimit,
                            edgeClustersDispVal,
                            tournamentSize,
                            maxAdditionalPopulationSize,
                            minAdditionalPopulationSize,
                            diversityThreshold,
                            enhanceDiversity);

                    var result = geneticAlgorithm.optimize();
                    uberPareto = geneticAlgorithm.getNondominatedFromTwoLists(result, uberPareto);
                    //            printResults(result);
                    eachRepeatUberParetoHV.add(hv.getMeasure(uberPareto));
                    var hvValue = hv.getMeasure(result);
                    eachRepeatHV.add(hvValue);

                    if(hvValue > bestAPFHV) {
                        bestAPFHV = hvValue;
                        bestAPF = result;
                        bestIterNumber = i;
                    }

                    eachRepeatND.add(result.size());
                    var convergenceValue = ed.getMeasure(result);
                    eachRepeatCM.add(convergenceValue);

                    var spacingValue = spacing.getMeasure(result);
                    eachRepeatSpacing.add(spacingValue);

                    String instanceName = files[k];
                    if(instanceName.endsWith(".def"))
                        instanceName = instanceName.substring(0, instanceName.lastIndexOf(".def"));

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

//                eachRepeatSpacing
//                        eachRepeatCM
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
                        + NUMBER_OF_REPEATS + ";" + avgHV + ";" + standardDeviation
                        + ";" + avgND + ";" + NDstandardDeviation + ";" + uberPareto.size() + ";" +
                        eachRepeatUberParetoHV.get(eachRepeatUberParetoHV.size()-1) + ";" + uberParetoHV + ";" + uberParetostdev
                        + ";" + generationLimit
                        + ";" + populationSize + ";" + mutationProbability + ";" + crossoverProbability
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

    private static Schedule readFile(int k) {
        var definitionFile = baseDir + files[k];
        MSRCPSPIO reader = new MSRCPSPIO();
        Schedule schedule = reader.readDefinition(definitionFile);
        if (null == schedule) {
            LOGGER.log(Level.WARNING, "Could not read the Definition "
                    + definitionFile);
            return null;
        }
        return schedule;
    }

    private static ParameterSet<Integer, Schedule> setParameters(Schedule schedule) {
        ParameterSet<Integer, Schedule> parameters = new ParameterSet<>();
        parameters.upperBounds = schedule.getUpperBounds();
        parameters.hasSuccesors = schedule.getSuccesors();
        parameters.populationMultiplicationFactor = 1;
        parameters.evalRate = 1.0;
        parameters.tournamentSize = 6;
        parameters.random = new RandomInt(System.currentTimeMillis());
        parameters.initialPopulation = new InitialPopulationGeneratorFactory(parameters).createInitialPopulation(InitialPopulationType.RANDOM);
        parameters.selection = new SelectionFactory(parameters).createSelection(SelectionType.NONDOMINATED_SORTING_NO_CROWDING_TOURNAMENT);
        parameters.crossover = new CrossoverFactory().createCrossover(CrossoverType.UNIFORM);
        parameters.mutation = new MutationFactory(parameters).createMutation(MutationType.RANDOM_BIT);
        parameters.converter = new ConverterFactory(parameters).createConverter(ConverterType.TRUNCATING);
        parameters.scheduleBuilder = new ScheduleBuilderFactory(parameters).createScheduleBuilder(ScheduleBuilderType.FORWARD_SCHEDULE_BUILDER);
        parameters.evaluator = new EvaluatorFactory().createEvaluator(EvaluatorType.WEIGHTED_EVALUATOR, parameters.evalRate);
        parameters.evaluator.setIndividual(new BaseIndividual<>(schedule, parameters.evaluator));
        return parameters;
    }

    private static String printResults(List<BaseIndividual<Integer, Schedule>> resultIndividuals, boolean isVerbose) {
        String output = "";
        if(isVerbose) {
            output += "Profit; Travelling Time\n";
            System.out.println("Profit; Travelling Time");
        }
//        for (int i = 0; i < resultIndividuals.size(); ++i) {
//            double profit = 0;
//            double travellingTime = resultIndividuals.get(i).getProblem().getTravellingTime();
//            int[] selection = resultIndividuals.get(i).getProblem().getSelection();
//            for (int j = 0; j < selection.length; ++j) {
//                if (selection[j] > 0) {
//                    profit += resultIndividuals.get(i).getProblem().getKnapsack().getItem(j).getProfit();
//                }
//            }
//            output += travellingTime + ";" + (-1)*profit + "\n";
//            if(isVerbose) {
//                System.out.println(travellingTime + ";" + (-1)*profit);
//            }
//        }
        return output;
    }
}
