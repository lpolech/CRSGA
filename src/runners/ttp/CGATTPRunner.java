package runners.ttp;

import algorithms.evaluation.BaseEvaluator;
import algorithms.evaluation.EvaluatorType;
import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.crossover.CrossoverType;
import algorithms.evolutionary_algorithms.genetic_algorithm.CGA;
import algorithms.evolutionary_algorithms.genetic_algorithm.utils.OptimisationResult;
import algorithms.evolutionary_algorithms.initial_population.InitialPopulationType;
import algorithms.evolutionary_algorithms.mutation.MutationType;
import algorithms.evolutionary_algorithms.selection.IndividualsPairingMethod;
import algorithms.evolutionary_algorithms.selection.SelectionType;
import algorithms.factories.*;
import algorithms.io.TTPIO;
import algorithms.problem.BaseIndividual;
import algorithms.problem.TTP;
import algorithms.quality_measure.*;
import distance_measures.Euclidean;
import interfaces.QualityMeasure;
import internal_measures.FlatWithinBetweenIndex;
import javafx.util.Pair;
import util.FILE_OUTPUT_LEVEL;
import util.random.RandomInt;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CGATTPRunner {
    private static final Logger LOGGER = Logger.getLogger( CGATTPRunner.class.getName() );
    private static final String baseDir = "." + File.separator; //assets/definitions/TTP/selected_01/";
    private static final String problemPath = "." + File.separator + "problems" + File.separator;
    private static final String apfsPath = "." + File.separator + "apfs" + File.separator;
    private static final List<Pair<String, String>> instanceWithOPF = Arrays.asList(
//            new Pair<>( problemPath + "eil51_n50_bounded-strongly-corr_01.ttp", apfsPath + "24-06-11_eil51_n50_bounded-strongly-corr_01_merged.csv"),
//            new Pair<>(problemPath + "eil51_n50_uncorr-similar-weights_01.ttp", apfsPath + "24-06-11_eil51_n50_uncorr-similar-weights_01_merged.csv"),
//            new Pair<>(problemPath + "eil51_n50_uncorr_01.ttp", apfsPath + "24-06-11_eil51_n50_uncorr_01_merged.csv"),
//            new Pair<>(problemPath + "eil51_n150_bounded-strongly-corr_01.ttp", apfsPath + "24-06-11_eil51_n150_bounded-strongly-corr_01_merged.csv"),
//            new Pair<>(problemPath + "eil51_n150_uncorr_01.ttp", apfsPath + "24-06-11_eil51_n150_uncorr_01_merged.csv"),
//            new Pair<>(problemPath + "eil51_n150_uncorr-similar-weights_01.ttp", apfsPath + "24-06-11_eil51_n150_uncorr-similar-weights_01_merged.csv"),
            new Pair<>(problemPath + "kroA100_n99_bounded-strongly-corr_01.ttp", apfsPath + "24-06-30_kroA100_n99_bounded-strongly-corr_01_merged_SingleFlip.csv")//,
//            new Pair<>(problemPath + "kroA100_n99_uncorr_01.ttp", apfsPath + "24-06-30_kroA100_n99_uncorr_01_merged_SingleFlip.csv"),
//            new Pair<>(problemPath + "kroA100_n99_uncorr-similar-weights_01.ttp", apfsPath + "24-06-30_kroA100_n99_uncorr-similar-weights_01_merged_SingleFlip.csv"),
//            new Pair<>(problemPath + "pr76_n75_bounded-strongly-corr_01.ttp", apfsPath + "24-06-30_pr76_n75_bounded-strongly-corr_01_merged_SingleFlip.csv"),
//            new Pair<>(problemPath + "pr76_n75_uncorr_01.ttp", apfsPath + "24-06-30_pr76_n75_uncorr_01_merged_SingleFlip.csv"),
//            new Pair<>(problemPath + "pr76_n75_uncorr-similar-weights_01.ttp", apfsPath + "24-06-30_pr76_n75_uncorr-similar-weights_01_merged_SingleFlip.csv"),
//            new Pair<>(problemPath + "rd100_n99_bounded-strongly-corr_01.ttp", apfsPath + "24-06-30_rd100_n99_bounded-strongly-corr_01_merged_SingleFlip.csv"),
//            new Pair<>(problemPath + "rd100_n99_uncorr_01.ttp", apfsPath + "24-06-30_rd100_n99_uncorr_01_merged_SingleFlip.csv"),
//            new Pair<>(problemPath + "rd100_n99_uncorr-similar-weights_01.ttp", apfsPath + "24-06-30_rd100_n99_uncorr-similar-weights_01_merged_SingleFlip.csv"),
//            new Pair<>(problemPath + "berlin52_n51_bounded-strongly-corr_01.ttp", apfsPath + "24-07-01_berlin52_n51_bounded-strongly-corr_01_merged_SingleFlip.csv"),
//            new Pair<>(problemPath + "berlin52_n51_uncorr_01.ttp", apfsPath + "24-07-01_berlin52_n51_uncorr_01_merged_SingleFlip.csv"),
//            new Pair<>(problemPath + "berlin52_n51_uncorr-similar-weights_01.ttp", apfsPath + "24-07-01_berlin52_n51_uncorr-similar-weights_01_merged_SingleFlip.csv"),
//            new Pair<>(problemPath + "berlin26_n25_bounded-strongly-corr_01.ttp", apfsPath + "24-08-15_berlin26_n25_bounded-strongly-corr_01_merged.csv"),
//            new Pair<>(problemPath + "eil51_n250_bounded-strongly-corr_01.ttp", apfsPath + "24-06-11_eil51_n250_bounded-strongly-corr_01_merged.csv"),
//            new Pair<>(problemPath + "eil51_n250_uncorr_01.ttp", apfsPath + "24-06-11_eil51_n250_uncorr_01_merged.csv"),
//            new Pair<>(problemPath + "eil51_n250_uncorr-similar-weights_01.ttp", apfsPath + "24-06-11_eil51_n250_uncorr-similar-weights_01_merged.csv"),
//            new Pair<>(problemPath + "eil51_n500_bounded-strongly-corr_01.ttp", apfsPath + "24-06-11_eil51_n500_bounded-strongly-corr_01_merged.csv"),
//            new Pair<>(problemPath + "eil51_n500_uncorr-similar-weights_01.ttp", apfsPath + "24-06-11_eil51_n500_uncorr-similar-weights_01_merged.csv"),
//            new Pair<>(problemPath + "eil51_n500_uncorr_01.ttp", apfsPath + "24-06-11_eil51_n500_uncorr_01_merged.csv")
    );

    public static void main(String[] args) {
        run(args);
//        ParameterFunctions pf1 = new ParameterFunctions(250, ParameterFunctions.FUNCTION_TYPE.EXPONENTIAL, 40, 100, 0.00001);
//        ParameterFunctions pf2 = new ParameterFunctions(250, ParameterFunctions.FUNCTION_TYPE.EXPONENTIAL, 40, 100, 0.5);
//        ParameterFunctions pf3 = new ParameterFunctions(250, ParameterFunctions.FUNCTION_TYPE.EXPONENTIAL, 40, 100, 1);
//        ParameterFunctions pf4 = new ParameterFunctions(250, ParameterFunctions.FUNCTION_TYPE.EXPONENTIAL, 40, 100, 2);
//        ParameterFunctions pf5 = new ParameterFunctions(250, ParameterFunctions.FUNCTION_TYPE.EXPONENTIAL, 40, 100, 3);
//        ParameterFunctions pf6 = new ParameterFunctions(250, ParameterFunctions.FUNCTION_TYPE.EXPONENTIAL, 40, 100, 4);
//        ParameterFunctions pf7 = new ParameterFunctions(250, ParameterFunctions.FUNCTION_TYPE.EXPONENTIAL, 40, 100, 5);
//        ParameterFunctions pf8 = new ParameterFunctions(250, ParameterFunctions.FUNCTION_TYPE.EXPONENTIAL, 40, 100, -0.00001);
//        ParameterFunctions pf9 = new ParameterFunctions(250, ParameterFunctions.FUNCTION_TYPE.EXPONENTIAL, 40, 100, -0.5);
//        ParameterFunctions pf10 = new ParameterFunctions(250, ParameterFunctions.FUNCTION_TYPE.EXPONENTIAL, 40, 100, -1);
//        ParameterFunctions pf11 = new ParameterFunctions(250, ParameterFunctions.FUNCTION_TYPE.EXPONENTIAL, 40, 100, -2);
//        ParameterFunctions pf12 = new ParameterFunctions(250, ParameterFunctions.FUNCTION_TYPE.EXPONENTIAL, 40, 100, -3);
//        ParameterFunctions pf13 = new ParameterFunctions(250, ParameterFunctions.FUNCTION_TYPE.EXPONENTIAL, 40, 100, -4);
//        ParameterFunctions pf14 = new ParameterFunctions(250, ParameterFunctions.FUNCTION_TYPE.EXPONENTIAL, 40, 100, -5);
//
//        System.out.println("decay_0.00001;decay_0.5;decay_1;decay_2;decay_3;decay_4;decay_5;decay_-0.00001;decay_-0.5;decay_-1;decay_-2;decay_-3;decay_-4;decay_-5");
//        for(int i  = 0; i <= 250; i++) {
//            System.out.println(pf1.getVal(i) + ";" + pf2.getVal(i) + ";" + pf3.getVal(i) + ";" + pf4.getVal(i) + ";" + pf5.getVal(i) + ";" + pf6.getVal(i) + ";" + pf7.getVal(i)
//                       + ";" + pf8.getVal(i) + ";" + pf9.getVal(i) + ";" + pf10.getVal(i) + ";" + pf11.getVal(i) + ";" + pf12.getVal(i) + ";" + pf13.getVal(i) + ";" + pf14.getVal(i));
//        }
    }

    private static List<BaseIndividual<Integer, TTP>> run(String[] args) {
        for (int k = 0; k < instanceWithOPF.size(); k++) {
            String instanceName = instanceWithOPF.get(k).getKey();
            System.out.println("File " + (k+1) + "/" + instanceWithOPF.size());
            TTP ttp = readFile(k);
            if (ttp == null) return null;

            ParameterSet<Integer, TTP> parameters = setParameters(ttp);
            List<BaseIndividual<Integer, TTP>> optimalParetoFront = readAPF(instanceWithOPF.get(k).getValue(), ttp, parameters.evaluator);

            QualityMeasure[] clusterWeightMeasureList = new QualityMeasure[] {
//                new FlatCalinskiHarabasz(new Euclidean()), //this measures is sensitive to useSubtree toggle
//                new FlatDaviesBouldin(new Euclidean()), //this measures is sensitive to useSubtree toggle
//                new FlatDunn1(new Euclidean()), //this measures is sensitive to useSubtree toggle
//                new FlatDunn4(new Euclidean()), //this measures is sensitive to useSubtree toggle
                    new FlatWithinBetweenIndex(new Euclidean()), //this measures is sensitive to useSubtree toggle
//                new FlatDunn2(new Euclidean()),
//                new FlatDunn3(new Euclidean())
            };

            int NUMBER_OF_REPEATS = 3;
            int[] generationLimitList = new int[] {250_000};//{50_000};//{250_000};//{5_000};//{5_000};//{25_000, 12_500, 5_000, 2_500, 1_666, 1_250, 500, 250};//500};
            int[] populationSizeList = new int[] {10};//{10, 50, 100, 150, 500}; //{10};//{10};//{20};//{10, 100};//{20};//{10, 20, 50, 100};//{50};// 100};
            double[] TSPmutationProbabilityList = new double[] {0.6};//{0.0, 0.001, 0.005, 0.01, 0.015, 0.02, 0.03, 0.05, 0.07, 0.1};//{0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.5};//{0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.5};//{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.25};//{0.0, 0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9, 0.95, 1.0};//{0.25};//{0.3};//{0.4};//}{0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, {0.4};//{0.4};//{0.1, 0.2, 0.3, 0.4, 0.5};//{0.01};//{0.007};//{0.002, 0.004, 0.006, 0.008};//{0.004};//{0.0, 0.0001, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.9};//{0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.0, 0.0001, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6}; //{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
            double[] KNAPmutationProbabilityList = new double[] {0.3};//{0.6};//{1.0, 0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.1};//{0.005};//{0.005, 0.01, 0.015, 0.02, 0.025, 0.03, 0.035, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09, 0.1};//{0.0027};//{0.0001, 0.0002, 0.0003, 0.0004, 0.0005, 0.0006, 0.0007, 0.0008, 0.0009, 0.0011, 0.0012, 0.0013, 0.0014, 0.0015, 0.0016, 0.0017, 0.0018, 0.0019, 0.0021, 0.0022, 0.0023, 0.0024, 0.0025, 0.0026, 0.0027, 0.0028, 0.0029, 0.0031, 0.0032, 0.0033, 0.0034, 0.0035, 0.0036, 0.0037, 0.0038, 0.0039};//, 0.004, 0.005, 0.006, 0.007, 0.008, 0.009, 0.01, 0.011, 0.012, 0.013, 0.014, 0.015, 0.016, 0.017, 0.018, 0.019};//{0.0031};//{0.0001, 0.0003, 0.0005, 0.0007, 0.0009, 0.0011, 0.0013, 0.0015, 0.0017, 0.0019, 0.0021, 0.0023, 0.0025, 0.0027, 0.0029, 0.0031, 0.0033, 0.0035, 0.0037, 0.0039};//{0.0024};//0.04};//{0.001, 0.005, 0.01, 0.015, 0.02, 0.03, 0.04, 0.05, 0.1, 0.125, 0.15};//{0.006};//, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.034};//{0.006};//{0.006};//{0.006};//{0.8, 0.9, 1.0};//{0.01};//{0.006};//{0.004, 0.005, 0.006, 0.007};//{0.01};//{0.01, 0.02, 0.03, 0.04};//, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.0, 0.0025, 0.005, 0.0075}; //{0.005, 0.01, 0.015};//, 0.005, 0.015};
            double[] TSPcrossoverProbabilityList = new double[] {0.3};//{0.3};//{0.75};//{0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.75};//{0.3, 0.35};//{0.35, 0.4};//{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.6};//{0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.6};//{0.0, 0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9, 0.95, 1.0};//{0.05, 0.15, 0.25, 0.35, 0.45, 0.55, 0.65, 0.75, 0.85, 0.95};//{0.4};//{0.0, 0.1, 0.3, 0.5, 0.7, 0.9};//{0.25, 0.35, 0.45, 0.55, 0.65, 0.75, 0.85, 0.95};//}{0.0, 0.15, 0.25, 0.35, 0.45, 0.55, 0.65, 0.75, 0.85, 0.95};//{0.45};{0.8};//}{0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9, 0.95, 1.0};//{0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.8};//{0.2};//{0.2};//{0.0, 0.05, 0.1, 0.15, 0.2}; //{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
            double[] KNAPcrossoverProbabilityList = new double[] {1.0};//{1.0};//{0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{1.0};//{1.0, 0.8, 0.6, 0.4, 0.2};//{0.8};//{0.6, 0.7, 0.8, 0.9, 1.0};//}{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.95};//{0.95};//{0.95};//{0.95};//{0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9, 0.95, 1.0};//{0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.95};//{0.95};//{0.75, 0.8, 0.85, 0.9, 0.95, 1.0, 1.5};//{0.7};//{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.05, 0.1, 0.2, 0.3, 0.4, 0.5};//{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
            int[] numberOfClusterList = new int[] {2};//{3}; //{11, 12, 13, 14, 16, 17, 18, 19, 21, 22, 23, 24, 25};//{2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20};//{5};//{2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 15, 17, 20, 22, 25, 30};//{5};//{2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 15, 17, 20, 22, 25, 30};//{2};//{2, 3, 4, 5, 10, 20};//{3};
            int[] clusterisationAlgorithmIterList = new int[]{50};//100};
            /*if negative, it will disable that function */ int[] clusteringRunFrequencyInCostList = {20};//{10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160, 170, 180, 190, 200};//{10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110, 115, 120, 125, 130, 135, 140, 145, 150, 155, 160, 165, 170, 175, 180, 185, 190, 195, 200};//, 250, 500, 750, 1000, 2500, 5000, 7500, 10000, 15000, 20000, 30000, 50000};
            boolean[] isRecalculateCentresList = {true};
            boolean[] isClusteringEveryXCostList = {true};
            boolean[] isPopulationUsedList = {true};
            double[] edgeClustersDispersion = new double[] {3};//{3, 3.5};//{3.0, 2.5, 3.5};//{/*0.5, 1.0, */2.0/*, 3.0, 5.0, 10.0*/};//3.5, 4.5, 5.5, 6.5, 7.5, 8.5, 9.5};//{2};//{0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10, 20, 50, 1000};//{2.5};//{0.0, 0.5, 1.5, 2.5, 3.5, 4.5, 7.0};//{4.0};//{0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10, 20, 50, 1000};//{4.0};//{0.5, 1.0, 1.5, 2.5, 3.5, 4.5, 5.5, 6.5, 7.5, 8.5, 9.5, 20, 50};//{4.0};//{0.5, 1.5, 2.5, 3.5, 4.5, 5.5, 6.5, 7.5, 8.5, 9.5, 20, 50};//{4};//{0.5};//{4};//{0.1, 0.5, 1, 2, 4, 10, 100};//{4}//{0.05, 0.1, 0.3, 0.5, 0.7, 0.9, 1, 4, 5, 10.0, 50, 100, 1_000, 5_000}; //{4};//, 10_000, 15_000, 20_000, 50_000, 100_000};//{0.1, 0.2, 0.3, 0.4, 0.5, 1.0, 1.5, 2.0};//{0.5, 1.0, 1.5, 2.0}; //}{0.1, 0.2, 0.3, 0.4, 0.5, 1.0, 1.5, 2.0};
            int[] tournamentSizeList = new int[] {70}; // {50};//{10, 20, 30, 40, 50}; //{15};//{10, 5, 15}; //{100};//{60};//{20, 40, 60, 80, 100}; //{0.95};////{200};//{10, 30, 50, 70, 90, 120, 200}; //{150};//{60, 70, 80, 90, 100}; //{80};//{10};//{80};//{10, 20, 30, 40, 50, 60, 70, 80, 90, 100}; //{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30, 50, 100}; //{90};
            int[] populationTurPropList = new int[]{100}; //{50};
            int[] KNAPmutationVersionList = new int[] {2}; //{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27};//{2};//{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27};//{2}; // {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27};
            int[] KNAPcrossoverVersionList = new int[] {1};//{1, 2, 3}; // {1}; // {1, 2, 3};
            int[] TSPmutationVersionList = new int[] {1};// {3 9 10};//{1, 2, 4, 5, 6, 7, 8, 11};//{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}; // // {1};
            int[] TSPcrossoverVersionList = new int[] {6};//{1, 2, 3, 4, 5, 6}; //{6};
            int[] indExclusionUsageLimitList = new int[] {250_000};//{750};//{300, 400, 500, 600, 700, 800, 900, 1000};//{250};//{100, 200, 300, 400, 500, 600, 700, 800, 900, 1000};//{550, 600, 650, 700, 750, 800, 850, 900, 950, 1000};//}{50, 100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 750, 800, 850, 900, 950, 1000};//{50, 100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 750, 800, 850, 900, 1000};//{25, 50, 75, 100, 125, 150, 175, 200, 225, 250, 275, 300, 325, 350, 375, 400, 425, 450, 475, 500, 525, 550, 575, 600, 625, 650, 675, 700};
            int[] indExclusionGenDurationList = new int[] {250_000};//{650};//{50, 100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 750, 800, 850, 900, 950, 1000};//}{150};//{100, 300, 500, 700, 900};//{150};//{{550};//{520, 540, 560, 580, 600, 620, 640, 660, 680};//{50, 150, 250, 350, 450, 550, 650};//{50, 100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600};
            double[] turDecayParamList = new double[] {-5};//{-0.5, -1.5, -3, -4, -5, -6, -7, -8, -9, -10, -11, -12.5, -13  .5, -14.5, -15.5};//{-6, -8, -15, -100};
            double[] localSearchPropList = {0.001, 0.005, 0.01, 0.03, 0.06, 0.1};//{0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//
            double[] knapLocalSearchArchivePropList = {0.0, 0.1, 0.3, 0.6, 1.0};//
            double[] tspLocalSearchArchivePropList = {0.0, 0.1, 0.3, 0.6, 1.0};//
            /*if negative, no decay will be applied!*/ int[] minTournamentSizeList = new int[] {-666};//{-666};//{15};//{30, 40, 50, 60, 70};
            IndividualsPairingMethod[] individualsPairingMethodsList = new IndividualsPairingMethod[]{IndividualsPairingMethod.DISTANT_IMMEDIATE_NEIGHBOUR_PAIR_SIMPLIFIED};//ALL_POSSIBLE_PAIRS CROSS_CLUSTER_ALL_POSSIBLE_PAIRS DISTANT_IMMEDIATE_NEIGHBOUR_PAIR DISTANT_IMMEDIATE_NEIGHBOUR_PAIR_SIMPLIFIED
            boolean shuffleParams = true;
            FILE_OUTPUT_LEVEL saveResultFiles = FILE_OUTPUT_LEVEL.ALL;//FILE_OUTPUT_LEVEL.NONE FILE_OUTPUT_LEVEL.ALL FILE_OUTPUT_LEVEL.MINIMAL;
            String summaryOutputFileName = "24-10-12_kroa_localSearch.csv";

            if(shuffleParams) {
                generationLimitList = shuffleIntArray(generationLimitList);
                populationSizeList = shuffleIntArray(populationSizeList);
                TSPmutationProbabilityList = shuffleDoubleArray(TSPmutationProbabilityList);
                KNAPmutationProbabilityList = shuffleDoubleArray(KNAPmutationProbabilityList);
                TSPcrossoverProbabilityList = shuffleDoubleArray(TSPcrossoverProbabilityList);
                KNAPcrossoverProbabilityList = shuffleDoubleArray(KNAPcrossoverProbabilityList);
                numberOfClusterList = shuffleIntArray(numberOfClusterList);
                clusterisationAlgorithmIterList = shuffleIntArray(clusterisationAlgorithmIterList);
                edgeClustersDispersion = shuffleDoubleArray(edgeClustersDispersion);
                tournamentSizeList = shuffleIntArray(tournamentSizeList);
                minTournamentSizeList = shuffleIntArray(minTournamentSizeList);
                populationTurPropList = shuffleIntArray(populationTurPropList);
                KNAPmutationVersionList = shuffleIntArray(KNAPmutationVersionList);
                KNAPcrossoverVersionList = shuffleIntArray(KNAPcrossoverVersionList);
                TSPmutationVersionList = shuffleIntArray(TSPmutationVersionList);
                TSPcrossoverVersionList = shuffleIntArray(TSPcrossoverVersionList);
                indExclusionUsageLimitList = shuffleIntArray(indExclusionUsageLimitList);
                indExclusionGenDurationList = shuffleIntArray(indExclusionGenDurationList);
                turDecayParamList = shuffleDoubleArray(turDecayParamList);
                clusteringRunFrequencyInCostList = shuffleIntArray(clusteringRunFrequencyInCostList);
                isRecalculateCentresList = shuffleBooleanArray(isRecalculateCentresList);
                isClusteringEveryXCostList = shuffleBooleanArray(isClusteringEveryXCostList);
                knapLocalSearchArchivePropList = shuffleDoubleArray(knapLocalSearchArchivePropList);
                tspLocalSearchArchivePropList = shuffleDoubleArray(tspLocalSearchArchivePropList);
                localSearchPropList = shuffleDoubleArray(localSearchPropList);
            }


            int numberOfParamConfigs = clusterWeightMeasureList.length*generationLimitList.length*populationSizeList.length*TSPmutationProbabilityList.length*KNAPmutationProbabilityList.length
                    *TSPcrossoverProbabilityList.length*KNAPcrossoverProbabilityList.length*numberOfClusterList.length*clusterisationAlgorithmIterList.length*edgeClustersDispersion.length*tournamentSizeList.length
                    *populationTurPropList.length*KNAPmutationVersionList.length*KNAPcrossoverVersionList.length*TSPmutationVersionList.length*TSPcrossoverVersionList.length*indExclusionUsageLimitList.length
                    *indExclusionGenDurationList.length*turDecayParamList.length*clusteringRunFrequencyInCostList.length*minTournamentSizeList.length*individualsPairingMethodsList.length*isRecalculateCentresList.length
                    *isClusteringEveryXCostList.length*isPopulationUsedList.length*tspLocalSearchArchivePropList.length*knapLocalSearchArchivePropList.length*localSearchPropList.length;
            System.out.println("Number of param configurations: " + numberOfParamConfigs);
            String header = "dataset;counter;measure;no of repeats;avgIGD;stdev;uber pareto purity;runs with purity;mnd;tpfs;uber pareto IGD;uber pareto GD"
                    + ";uber pareto HV;uber pareto size;avgPurity;stdev;avgGD;stdev;avgHV;stdev;avgPFS;stdev"
                    + ";AvgAfterCrossParentDominationCounter"
                    + ";AvgAfterCrossParentDominationProp"
                    + ";AvgAfterCrossAndMutParentDominationCounter"
                    + ";AvgAfterCrossAndMutParentDominationProp"
                    + ";AvgAfterCrossAfterCrossAndMutDominationCounter"
                    + ";AvgAfterCrossAfterCrossAndMutDominationProp"
                    + ";AvgAfterCrossAndMutAfterCrossDominationCounter"
                    + ";AvgAfterCrossAndMutAfterCrossDominationProp"
                    + ";generationLimit;populationSize;TSPmutationProbability" +
                    ";KNAPmutationProbability;TSPcrossoverProbability;KNAPcrossoverProbability" +
                    ";tspLocalSearchArchiveProp;knapLocalSearchArchiveProp;localSearchProp;numberOfClusters" +
                    ";clusterIterLimit;isClusteringEveryXCostEnabled;isCentresRecalculated;clusteringRunFrequencyInCost" +
                    ";isPopulationUsed;edgeClustersProb;tournamentSize;populationTurProp;KNAPmutationVersion;KNAPcrossoverVersion" +
                    ";TSPmutationVersion;TSPcrossoverVersion;indExclusionUsageLimit;indExclusionGenDuration" +
                    ";turDecayParam;minTournamentSize;IndPairing";

            System.out.println(header);
            try {
                File f = new File(baseDir + summaryOutputFileName);
                if(!f.exists()) {
                    FileWriter fw = new FileWriter(baseDir + summaryOutputFileName, true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(header);
                    bw.newLine();
                    bw.close();
                }
            } catch(IOException e) {
                e.printStackTrace();
            }

            int paramCounter = 0;
            for(int wmNum = 0; wmNum < clusterWeightMeasureList.length; wmNum++) {
                QualityMeasure clusterWeightMeasure = clusterWeightMeasureList[wmNum];
                for (int i = 0; i < generationLimitList.length; i++) {
                    int generationLimit = generationLimitList[i];
                    for (int j = 0; j < populationSizeList.length; j++) {
                        int populationSize = populationSizeList[j];
                        for (int l = 0; l < TSPmutationProbabilityList.length; l++) {
                            double TSPmutationProbability = TSPmutationProbabilityList[l];
                            for (int x = 0; x < KNAPmutationProbabilityList.length; x++) {
                                double KNAPmutationProbability = KNAPmutationProbabilityList[x];
                                for (int m = 0; m < TSPcrossoverProbabilityList.length; m++) {
                                    double TSPcrossoverProbability = TSPcrossoverProbabilityList[m];
                                    for (int y = 0; y < KNAPcrossoverProbabilityList.length; y++) {
                                        double KNAPcrossoverProbability = KNAPcrossoverProbabilityList[y];
                                        for (int n = 0; n < numberOfClusterList.length; n++) {
                                            int numberOfClusters = numberOfClusterList[n];
                                            for (int o = 0; o < clusterisationAlgorithmIterList.length; o++) {
                                                int clusterIterLimit = clusterisationAlgorithmIterList[o];
                                                for (int p = 0; p < edgeClustersDispersion.length; p++) {
                                                    double edgeClustersDispVal = edgeClustersDispersion[p];
                                                    for (int q = 0; q < tournamentSizeList.length; q++) {
                                                        int tournamentSize = tournamentSizeList[q];
                                                        for( int r = 0; r < populationTurPropList.length; r++) {
                                                            int populationTurProp = populationTurPropList[r];
                                                            for (int ii = 0; ii < KNAPmutationVersionList.length; ii++) {
                                                                int KNAPmutationVersion = KNAPmutationVersionList[ii];
                                                                for (int iii = 0; iii < KNAPcrossoverVersionList.length; iii++) {
                                                                    int KNAPcrossoverVersion = KNAPcrossoverVersionList[iii];
                                                                    for (int jjj = 0; jjj < TSPmutationVersionList.length; jjj++) {
                                                                        int TSPmutationVersion = TSPmutationVersionList[jjj];
                                                                        for (int jj = 0; jj < TSPcrossoverVersionList.length; jj++) {
                                                                            int TSPcrossoverVersion = TSPcrossoverVersionList[jj];
                                                                            for (int kk = 0; kk < indExclusionUsageLimitList.length; kk++) {
                                                                                int indExclusionUsageLimit = indExclusionUsageLimitList[kk];
                                                                                for (int ll = 0; ll < indExclusionGenDurationList.length; ll++) {
                                                                                    int indExclusionGenDuration = indExclusionGenDurationList[ll];
                                                                                    for (int mm = 0; mm < turDecayParamList.length; mm++) {
                                                                                        double turDecayParam = turDecayParamList[mm];
                                                                                        for (int nn = 0; nn < minTournamentSizeList.length; nn++) {
                                                                                            int minTournamentSize = minTournamentSizeList[nn];
                                                                                            for (int oo = 0; oo < individualsPairingMethodsList.length; oo++) {
                                                                                                IndividualsPairingMethod indPairingMethod = individualsPairingMethodsList[oo];
                                                                                                for (int pp = 0; pp < clusteringRunFrequencyInCostList.length; pp++) {
                                                                                                    int clusteringRunFrequencyInCost = clusteringRunFrequencyInCostList[pp];
                                                                                                    for (int qq = 0; qq < isRecalculateCentresList.length; qq++) {
                                                                                                        boolean isRecalculateCentres = isRecalculateCentresList[qq];
                                                                                                        for (int rr = 0; rr < isClusteringEveryXCostList.length; rr++) {
                                                                                                            boolean isClusteringEveryXCost = isClusteringEveryXCostList[rr];
                                                                                                            for (int ss = 0; ss < isClusteringEveryXCostList.length; ss++) {
                                                                                                                boolean isPopulationUsed = isPopulationUsedList[ss];
                                                                                                                for (int tt = 0; tt < knapLocalSearchArchivePropList.length; tt++) {
                                                                                                                    double knapLocalSearchArchiveProp = knapLocalSearchArchivePropList[tt];
                                                                                                                    for (int uu = 0; uu < tspLocalSearchArchivePropList.length; uu++) {
                                                                                                                        double tspLocalSearchArchiveProp = tspLocalSearchArchivePropList[uu];
                                                                                                                        for (int vv = 0; vv < localSearchPropList.length; vv++) {
                                                                                                                            double localSearchProp = localSearchPropList[vv];

                                                                                                                            var eachRepeatHV = new ArrayList<Double>();
                                                                                                                            var eachRepeatND = new ArrayList<Integer>();
                                                                                                                            var eachRepeatOptimisationResult = new ArrayList<OptimisationResult>();
                                                                                                                            var eachRepeatResult = new ArrayList<List<BaseIndividual<Integer, TTP>>>();
                                                                                                                            var eachRepeatIGD = new ArrayList<Double>();
                                                                                                                            var eachRepeatGD = new ArrayList<Double>();
                                                                                                                            var eachRepeatPurity = new ArrayList<Double>();
                                                                                                                            paramCounter += 1;

                                                                                                                            int maxAdditionalPopulationSize = populationSize / 2;
                                                                                                                            int minAdditionalPopulationSize = populationSize / 10;

                                                                                                                            List<BaseIndividual<Integer, TTP>> bestAPF = null;
                                                                                                                            double bestAPFHV = -Double.MIN_VALUE;

                                                                                                                            String outputFilename = "." + File.separator + "out" + File.separator
                                                                                                                                    + removePrefixAndTtpPostFixFromFileName(problemPath, instanceWithOPF.get(k).getKey())
                                                                                                                                    + "_m-" + clusterWeightMeasure.getName()
                                                                                                                                    + "_g" + generationLimit + "_p-" + populationSize
                                                                                                                                    + "_Tm" + TSPmutationProbability + "_Km" + KNAPmutationProbability
                                                                                                                                    + "_Tc" + TSPcrossoverProbability + "_Kc" + KNAPcrossoverProbability
                                                                                                                                    + "_cN" + numberOfClusters + "_cI" + clusterIterLimit + "_edgC"
                                                                                                                                    + edgeClustersDispVal + "_t" + tournamentSize + "_popT" + populationTurProp
                                                                                                                                    + "_eL" + indExclusionUsageLimit + "_eg" + indExclusionGenDuration + "_d"
                                                                                                                                    + turDecayParam + "_mt" + minTournamentSize + "_" + indPairingMethod.getName()
                                                                                                                                    + "_km" + KNAPmutationVersion + "_kc" + KNAPcrossoverVersion
                                                                                                                                    + "_tm" + TSPmutationVersion + "_tc" + TSPcrossoverVersion + "_cc" + isClusteringEveryXCost
                                                                                                                                    + "_cf" + clusteringRunFrequencyInCost + "_cr" + isRecalculateCentres + "_p" + isPopulationUsed
                                                                                                                                    + "_lt" + tspLocalSearchArchiveProp + "_lk" + knapLocalSearchArchiveProp + "_ls" + localSearchProp;

                                                                                                                            String bestAPFoutputFile = "bestAPF";
                                                                                                                            int bestIterNumber = 0;
//                                                                                        if(saveResultFiles) {
                                                                                                                            File theDir = new File(outputFilename);
                                                                                                                            if (!theDir.exists()) {
                                                                                                                                theDir.mkdirs();
                                                                                                                            }
//                                                                                        }

                                                                                                                            List<BaseIndividual<Integer, TTP>> uberPareto = new ArrayList<>();
                                                                                                                            List<BaseIndividual<Integer, TTP>> optimalApfWithUberPareto = new ArrayList<>();
                                                                                                                            CGA<TTP> geneticAlgorithm = null;
                                                                                                                            for (int xx = 0; xx < NUMBER_OF_REPEATS; xx++) {
                                                                                                                                parameters.KNAPmutationVersion = KNAPmutationVersion;
                                                                                                                                parameters.KNAPcrossoverVersion = KNAPcrossoverVersion;
                                                                                                                                parameters.TSPmutationVersion = TSPmutationVersion;
                                                                                                                                parameters.TSPcrossoverVersion = TSPcrossoverVersion;
                                                                                                                                HVMany hv = new HVMany(parameters.evaluator.getNadirPoint());
                                                                                                                                geneticAlgorithm = new CGA<>(
                                                                                                                                        ttp,
                                                                                                                                        clusterWeightMeasure,
                                                                                                                                        populationSize,
                                                                                                                                        generationLimit,
                                                                                                                                        parameters,
                                                                                                                                        TSPmutationProbability,
                                                                                                                                        KNAPmutationProbability,
                                                                                                                                        TSPcrossoverProbability,
                                                                                                                                        KNAPcrossoverProbability,
                                                                                                                                        instanceWithOPF.get(k).getKey().split("\\.")[0],
                                                                                                                                        numberOfClusters,
                                                                                                                                        clusterIterLimit,
                                                                                                                                        edgeClustersDispVal,
                                                                                                                                        tournamentSize,
                                                                                                                                        maxAdditionalPopulationSize,
                                                                                                                                        minAdditionalPopulationSize,
                                                                                                                                        populationTurProp,
                                                                                                                                        -666,
                                                                                                                                        true,
                                                                                                                                        hv,
                                                                                                                                        optimalParetoFront,
                                                                                                                                        outputFilename,
                                                                                                                                        saveResultFiles,
                                                                                                                                        xx,
                                                                                                                                        indExclusionUsageLimit,
                                                                                                                                        indExclusionGenDuration,
                                                                                                                                        turDecayParam,
                                                                                                                                        minTournamentSize,
                                                                                                                                        indPairingMethod,
                                                                                                                                        clusteringRunFrequencyInCost,
                                                                                                                                        isClusteringEveryXCost,
                                                                                                                                        isRecalculateCentres,
                                                                                                                                        isPopulationUsed,
                                                                                                                                        tspLocalSearchArchiveProp,
                                                                                                                                        knapLocalSearchArchiveProp,
                                                                                                                                        localSearchProp
                                                                                                                                );

                                                                                                                                var result = geneticAlgorithm.optimize();
                                                                                                                                geneticAlgorithm.removeDuplicatesAndDominated(result, uberPareto);
//                    uberPareto = geneticAlgorithm.getNondominatedFromTwoLists(result, uberPareto);
                                                                                                                                //            printResults(result);

                                                                                                                                eachRepeatOptimisationResult.add(geneticAlgorithm.getOptimisationResult());
                                                                                                                                eachRepeatResult.add(result);

                                                                                                                                String instanceNameForFile = removePrefixAndTtpPostFixFromFileName(problemPath, instanceName);
                                                                                                                                if (saveResultFiles.getLevel() >= 1) {
                                                                                                                                    try {
                                                                                                                                        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename
                                                                                                                                                + File.separator + instanceNameForFile + "_config0_run" + xx + "_archive.csv"));
                                                                                                                                        writer.write(printResultsForComparison(result, false));
                                                                                                                                        writer.close();
                                                                                                                                    } catch (
                                                                                                                                            IOException e) {
                                                                                                                                        e.printStackTrace();
                                                                                                                                    }
                                                                                                                                }

                                                                                                                                System.out.print(xx + ", ");
                                                                                                                            }
                                                                                                                            System.out.println("");

                                                                                                                            optimalApfWithUberPareto = new ArrayList<>(optimalParetoFront);
                                                                                                                            geneticAlgorithm.removeDuplicatesAndDominated(uberPareto, optimalApfWithUberPareto);

                                                                                                                            int mnd = geneticAlgorithm.getNumberOfNotDominated(uberPareto, optimalApfWithUberPareto);

                                                                                                                            Pair<Pair<List<BaseIndividual<Integer, TTP>>, List<BaseIndividual<Integer, TTP>>>
                                                                                                                                    , ArrayList<List<BaseIndividual<Integer, TTP>>>> normalisedApfAndResults
                                                                                                                                    = normaliseParetoFrontsByMinMax(optimalApfWithUberPareto, uberPareto, eachRepeatResult, ttp,
                                                                                                                                    parameters.evaluator);
                                                                                                                            List<BaseIndividual<Integer, TTP>> normalisedOptimalPftWithUberPareto = normalisedApfAndResults.getKey().getKey();
                                                                                                                            List<BaseIndividual<Integer, TTP>> normalisedUberPareto = normalisedApfAndResults.getKey().getValue();
                                                                                                                            ArrayList<List<BaseIndividual<Integer, TTP>>> normalisedResults = normalisedApfAndResults.getValue();

//                        optimalApfWithUberPareto = geneticAlgorithm.getNondominatedFromTwoLists(optimalParetoFront, uberPareto);
                                                                                                                            InvertedGenerationalDistance igdCalculator = new InvertedGenerationalDistance(normalisedOptimalPftWithUberPareto);
                                                                                                                            GenerationalDistance gdCalculator = new GenerationalDistance(normalisedOptimalPftWithUberPareto);
                                                                                                                            Purity purityCalculator = new Purity(normalisedOptimalPftWithUberPareto);

                                                                                                                            BaseIndividual<Integer, TTP> normalisedHvNadirPoint = new BaseIndividual<>(ttp, new ArrayList<>(), parameters.evaluator);
                                                                                                                            normalisedHvNadirPoint.setObjectives(new double[]{1.0, 1.0});
                                                                                                                            normalisedHvNadirPoint.setNormalObjectives(new double[]{1.0, 1.0});
                                                                                                                            normalisedHvNadirPoint.setHashCode();
                                                                                                                            HVMany hvCalculator = new HVMany(normalisedHvNadirPoint);

                                                                                                                            for (int yy = 0; yy < normalisedResults.size(); yy++) {
                                                                                                                                var normRes = normalisedResults.get(yy);
                                                                                                                                var result = eachRepeatResult.get(yy);

                                                                                                                                var hvValue = hvCalculator.getMeasure(normRes);
                                                                                                                                eachRepeatHV.add(hvValue);
                                                                                                                                eachRepeatND.add(result.size());

                                                                                                                                if (hvValue > bestAPFHV) {
                                                                                                                                    bestAPFHV = hvValue;
                                                                                                                                    bestAPF = result;
                                                                                                                                    bestIterNumber = yy;
                                                                                                                                }

                                                                                                                                var igdValue = igdCalculator.getMeasure(normRes);
                                                                                                                                eachRepeatIGD.add(igdValue);

                                                                                                                                var gdValue = gdCalculator.getMeasure(normRes);
                                                                                                                                eachRepeatGD.add(gdValue);

                                                                                                                                var purityValue = purityCalculator.getMeasure(normRes);
                                                                                                                                eachRepeatPurity.add(purityValue);
                                                                                                                            }

                                                                                                                            String instanceNameForFile = removePrefixAndTtpPostFixFromFileName(problemPath, instanceName);
                                                                                                                            try {
                                                                                                                                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename
                                                                                                                                        + File.separator + instanceNameForFile + "_UBER_PARETO.csv"));
                                                                                                                                writer.write(printParetos("uber", uberPareto, "apf", optimalParetoFront, false));
                                                                                                                                writer.close();
                                                                                                                            } catch (
                                                                                                                                    IOException e) {
                                                                                                                                e.printStackTrace();
                                                                                                                            }

                                                                                                                            if (saveResultFiles.getLevel() > 1) {
                                                                                                                                try {
                                                                                                                                    BufferedWriter writer = null;

                                                                                                                                    writer = new BufferedWriter(new FileWriter(outputFilename
                                                                                                                                            + File.separator + instanceNameForFile + "_apf.csv"));
                                                                                                                                    writer.write(printParetos("uber", uberPareto, "uber+apf", optimalApfWithUberPareto, false));
                                                                                                                                    writer.close();

                                                                                                                                    writer = new BufferedWriter(new FileWriter(outputFilename
                                                                                                                                            + File.separator + instanceNameForFile + "_genes_UBER_PARETO.csv"));
                                                                                                                                    writer.write(printGenes(uberPareto, ttp));
                                                                                                                                    writer.close();
                                                                                                                                } catch (
                                                                                                                                        IOException e) {
                                                                                                                                    e.printStackTrace();
                                                                                                                                }
                                                                                                                            }

                                                                                                                            OptionalDouble NDaverage = eachRepeatND
                                                                                                                                    .stream()
                                                                                                                                    .mapToDouble(a -> a)
                                                                                                                                    .average();
                                                                                                                            var avgPFS = NDaverage.isPresent() ? NDaverage.getAsDouble() : -666.0;

                                                                                                                            double NDstandardDeviation = 0.0;
                                                                                                                            for (double num : eachRepeatND) {
                                                                                                                                NDstandardDeviation += Math.pow(num - avgPFS, 2);
                                                                                                                            }
                                                                                                                            NDstandardDeviation = Math.sqrt(NDstandardDeviation / eachRepeatND.size());

                                                                                                                            OptionalDouble average = eachRepeatHV
                                                                                                                                    .stream()
                                                                                                                                    .mapToDouble(a -> a)
                                                                                                                                    .average();
                                                                                                                            var avgHV = average.isPresent() ? average.getAsDouble() : -666.0;

                                                                                                                            double standardDeviation = 0.0;
                                                                                                                            for (double num : eachRepeatHV) {
                                                                                                                                standardDeviation += Math.pow(num - avgHV, 2);
                                                                                                                            }

                                                                                                                            standardDeviation = Math.sqrt(standardDeviation / eachRepeatHV.size());

                                                                                                                            OptionalDouble averageIGD = eachRepeatIGD
                                                                                                                                    .stream()
                                                                                                                                    .mapToDouble(a -> a)
                                                                                                                                    .average();
                                                                                                                            var averageIGDVal = averageIGD.isPresent() ? averageIGD.getAsDouble() : -666.0;
                                                                                                                            double averageIGDValStdev = 0.0;
                                                                                                                            for (double num : eachRepeatIGD) {
                                                                                                                                averageIGDValStdev += Math.pow(num - averageIGDVal, 2);
                                                                                                                            }
                                                                                                                            averageIGDValStdev = Math.sqrt(averageIGDValStdev / eachRepeatIGD.size());

                                                                                                                            OptionalDouble averageGD = eachRepeatGD
                                                                                                                                    .stream()
                                                                                                                                    .mapToDouble(a -> a)
                                                                                                                                    .average();
                                                                                                                            var averageGDVal = averageGD.isPresent() ? averageGD.getAsDouble() : -666.0;
                                                                                                                            double averageGDStdev = 0.0;
                                                                                                                            for (double num : eachRepeatGD) {
                                                                                                                                averageGDStdev += Math.pow(num - averageGDVal, 2);
                                                                                                                            }
                                                                                                                            averageGDStdev = Math.sqrt(averageGDStdev / eachRepeatGD.size());

                                                                                                                            OptionalDouble averagePurity = eachRepeatPurity
                                                                                                                                    .stream()
                                                                                                                                    .mapToDouble(a -> a)
                                                                                                                                    .average();
                                                                                                                            var averagePurityVal = averagePurity.isPresent() ? averagePurity.getAsDouble() : -666.0;
                                                                                                                            double averagePurityStdev = 0.0;
                                                                                                                            for (double num : eachRepeatPurity) {
                                                                                                                                averagePurityStdev += Math.pow(num - averagePurityVal, 2);
                                                                                                                            }
                                                                                                                            averagePurityStdev = Math.sqrt(averagePurityStdev / eachRepeatPurity.size());
                                                                                                                            long runsWithPurity = eachRepeatPurity.stream().filter(value -> value > 0).count();


                                                                                                                            String runResult = instanceNameForFile + ";" + paramCounter + "/" + numberOfParamConfigs + ";"
                                                                                                                                    + clusterWeightMeasure.getClass().getName() + ";" + NUMBER_OF_REPEATS
                                                                                                                                    + ";" + averageIGDVal + ";" + averageIGDValStdev
                                                                                                                                    + ";" + purityCalculator.getMeasure(normalisedUberPareto)
                                                                                                                                    + ";" + runsWithPurity
                                                                                                                                    + ";" + mnd + ";" + optimalApfWithUberPareto.size()
                                                                                                                                    + ";" + igdCalculator.getMeasure(normalisedUberPareto)
                                                                                                                                    + ";" + gdCalculator.getMeasure(normalisedUberPareto)
//                        + ";" + new HVMany(parameters.evaluator.getNadirPoint()).getMeasure(normalisedUberPareto)
                                                                                                                                    + ";" + hvCalculator.getMeasure(normalisedUberPareto)
                                                                                                                                    + ";" + normalisedUberPareto.size()
                                                                                                                                    + ";" + averagePurityVal + ";" + averagePurityStdev
                                                                                                                                    + ";" + averageGDVal + ";" + averageGDStdev
                                                                                                                                    + ";" + avgHV + ";" + standardDeviation
                                                                                                                                    + ";" + avgPFS + ";" + NDstandardDeviation
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
                                                                                                                                    + ";" + tspLocalSearchArchiveProp + ";" + knapLocalSearchArchiveProp + ";" + localSearchProp
                                                                                                                                    + ";" + numberOfClusters + ";" + clusterIterLimit + ";" + isClusteringEveryXCost + ";" + isRecalculateCentres
                                                                                                                                    + ";" + clusteringRunFrequencyInCost + ";" + isPopulationUsed + ";" + edgeClustersDispVal + ";" + tournamentSize
                                                                                                                                    + ";" + populationTurProp + ";" + KNAPmutationVersion + ";" + KNAPcrossoverVersion + ";" + TSPmutationVersion
                                                                                                                                    + ";" + TSPcrossoverVersion + ";" + indExclusionUsageLimit + ";" + indExclusionGenDuration
                                                                                                                                    + ";" + turDecayParam + ";" + minTournamentSize + ";" + indPairingMethod;
                                                                                                                            System.out.println(runResult);
                                                                                                                            try {
                                                                                                                                FileWriter fw = new FileWriter(baseDir + summaryOutputFileName, true);
                                                                                                                                BufferedWriter bw = new BufferedWriter(fw);
                                                                                                                                bw.write(runResult);
                                                                                                                                bw.newLine();
                                                                                                                                bw.close();
                                                                                                                            } catch (
                                                                                                                                    IOException e) {
                                                                                                                                e.printStackTrace();
                                                                                                                            }

                                                                                                                            if (saveResultFiles.getLevel() > 1) {
                                                                                                                                try {
                                                                                                                                    BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename + File.separator
                                                                                                                                            + bestAPFoutputFile + bestIterNumber + ".csv"));
                                                                                                                                    writer.write(printResultsForComparison(bestAPF, false));
                                                                                                                                    writer.close();
                                                                                                                                } catch (
                                                                                                                                        IOException e) {
                                                                                                                                    e.printStackTrace();
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
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private static int[] shuffleIntArray(int[] intArray) {
        Random rand = new Random();
        for (int i = intArray.length - 1; i > 0; i--) {
            // Generate a random index between 0 and i (inclusive)
            int j = rand.nextInt(i + 1);

            // Swap the elements at indices i and j
            int temp = intArray[i];
            intArray[i] = intArray[j];
            intArray[j] = temp;
        }

        return intArray;
    }

    private static double[] shuffleDoubleArray(double[] doubleArray) {
        Random rand = new Random();
        for (int i = doubleArray.length - 1; i > 0; i--) {
            // Generate a random index between 0 and i (inclusive)
            int j = rand.nextInt(i + 1);

            // Swap the elements at indices i and j
            double temp = doubleArray[i];
            doubleArray[i] = doubleArray[j];
            doubleArray[j] = temp;
        }

        return doubleArray;
    }

    public static boolean[] shuffleBooleanArray(boolean[] booleanArray) {
        Random rand = new Random();
        for (int i = booleanArray.length - 1; i > 0; i--) {
            // Generate a random index between 0 and i (inclusive)
            int j = rand.nextInt(i + 1);

            // Swap the elements at indices i and j
            boolean temp = booleanArray[i];
            booleanArray[i] = booleanArray[j];
            booleanArray[j] = temp;
        }

        return booleanArray;
    }

    private static Pair<Pair<List<BaseIndividual<Integer, TTP>>, List<BaseIndividual<Integer, TTP>>>, ArrayList<List<BaseIndividual<Integer, TTP>>>>
    normaliseParetoFrontsByMinMax(List<BaseIndividual<Integer, TTP>> optimalApfWithUberPareto,
                                  List<BaseIndividual<Integer, TTP>> uberPareto,
                                  ArrayList<List<BaseIndividual<Integer, TTP>>> eachRepeatResult,
                                  TTP ttp,
                                  BaseEvaluator<Integer, TTP> evaluator) {
        List<BaseIndividual<Integer, TTP>> normalisedOptimalApfWithUberPareto = new ArrayList<>();
        List<BaseIndividual<Integer, TTP>> normalisedUberPareto = new ArrayList<>();
        ArrayList<List<BaseIndividual<Integer, TTP>>> normalisedEachRepeatResult = new ArrayList<>();

        int noOfDims = optimalApfWithUberPareto.get(0).getObjectives().length;

        // Initialize Min and Max values
        List<Double> minValues = new ArrayList<>(Collections.nCopies(noOfDims, 0.0));
        List<Double> maxValues = new ArrayList<>(Collections.nCopies(noOfDims, 0.0));
        List<Double> minNormValues = new ArrayList<>(Collections.nCopies(noOfDims, 0.0));
        List<Double> maxNormValues = new ArrayList<>(Collections.nCopies(noOfDims, 0.0));
        for (int v = 0; v < noOfDims; ++v) {
            maxValues.set(v, (-1)*Double.MAX_VALUE);
            minValues.set(v, Double.MAX_VALUE);
            maxNormValues.set(v, (-1)*Double.MAX_VALUE);
            minNormValues.set(v, Double.MAX_VALUE);
        }

        getMinMax(optimalApfWithUberPareto, noOfDims, minValues, maxValues, minNormValues, maxNormValues);
        getMinMax(uberPareto, noOfDims, minValues, maxValues, minNormValues, maxNormValues);

        for(var res: eachRepeatResult) {
            getMinMax(res, noOfDims, minValues, maxValues, minNormValues, maxNormValues);
        }

        normalisedOptimalApfWithUberPareto = normaliseByMinMax(optimalApfWithUberPareto,
                minValues, maxValues, minNormValues, maxNormValues, ttp, evaluator);
        normalisedUberPareto = normaliseByMinMax(uberPareto,
                minValues, maxValues, minNormValues, maxNormValues, ttp, evaluator);

        for(var res: eachRepeatResult) {
            normalisedEachRepeatResult.add(normaliseByMinMax(res,
                    minValues, maxValues, minNormValues, maxNormValues, ttp, evaluator));
        }

        return new Pair<>(new Pair<>(normalisedOptimalApfWithUberPareto, normalisedUberPareto), normalisedEachRepeatResult);
    }

    private static void getMinMax(List<BaseIndividual<Integer, TTP>> front, int noOfDims, List<Double> minValues, List<Double> maxValues, List<Double> minNormValues, List<Double> maxNormValues) {
        for (int i = 0; i < front.size(); ++i) {
            BaseIndividual<Integer, TTP> sol = front.get(i);
            for (int v = 0; v < noOfDims; ++v) {
                double evalValue = sol.getObjectives()[v];
                minValues.set(v, Math.min(minValues.get(v), evalValue));
                maxValues.set(v, Math.max(maxValues.get(v), evalValue));

                double evalNormValue = sol.getNormalObjectives()[v];
                minNormValues.set(v, Math.min(minNormValues.get(v), evalNormValue));
                maxNormValues.set(v, Math.max(maxNormValues.get(v), evalNormValue));
            }
        }
    }

    public static double roundNumberToTwoDecimalPlaces(double number) {
        return Math.round(number * 100.0) / 100.0;
    }

    public static List<BaseIndividual<Integer, TTP>> normaliseByMinMax(List<BaseIndividual<Integer, TTP>> front,
                                                                       List<Double> minValues,
                                                                       List<Double> maxValues,
                                                                       List<Double> minNormValues,
                                                                       List<Double> maxNormValues,
                                                                       TTP ttp,
                                                                       BaseEvaluator<Integer, TTP> evaluator) {
        if (front.size() <= 0) {
            System.err.println("Front is empty!");
        }

        int minValSize = minValues.size();
        int maxValSize = maxValues.size();
        int objDims = front.get(0).getObjectives().length;
        int normObjDims = front.get(0).getNormalObjectives().length;

        if (minValSize != maxValSize || minValSize != objDims || minValSize != normObjDims) {
            System.err.println("Objective dimensions do not stack up!");
        }

        List<BaseIndividual<Integer, TTP>> normalisedFront = new ArrayList<>(front.size());
        List<Double> diffVec = new ArrayList<>(Collections.nCopies(objDims, 0.0));
        List<Double> diffNormVec = new ArrayList<>(Collections.nCopies(objDims, 0.0));
        for (int v = 0; v < objDims; ++v) {
            diffVec.set(v, maxValues.get(v) - minValues.get(v));
            diffNormVec.set(v, maxNormValues.get(v) - minNormValues.get(v));
        }

        for(int i = 0; i < front.size(); ++i) {
            BaseIndividual<Integer, TTP> sol = front.get(i);
            List<Double> normObj = new ArrayList<>(Collections.nCopies(objDims, 0.0));
            List<Double> normNormObj = new ArrayList<>(Collections.nCopies(objDims, 0.0));

            for (int v = 0; v < objDims; ++v) {
                normObj.set(v, (sol.getObjectives()[v] - minValues.get(v)) / diffVec.get(v));
                normNormObj.set(v, (sol.getNormalObjectives()[v] - minNormValues.get(v)) / diffNormVec.get(v));
            }

            BaseIndividual<Integer, TTP> normIndividual = new BaseIndividual<>(ttp, new ArrayList<>(), evaluator);
            normIndividual.setObjectives(normObj.stream().mapToDouble(d -> d).toArray());
            normIndividual.setNormalObjectives(normNormObj.stream().mapToDouble(d -> d).toArray());
            normIndividual.setHashCode();

            normalisedFront.add(normIndividual);
        }

        return normalisedFront;
    }

    private static List<BaseIndividual<Integer, TTP>> readAPF(String apfPath, TTP ttp, BaseEvaluator<Integer, TTP> evaluator) {
        File file = new File(apfPath);
        System.out.print("File;" + file.getName());
        List<BaseIndividual<Integer, TTP>> front = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                double firstObj = Double.parseDouble(values[0]);
                double secondObj = Double.parseDouble(values[1]);
//                        System.out.println(firstObj + ", " + secondObj);

                BaseIndividual<Integer, TTP> individual = new BaseIndividual<>(ttp, new ArrayList<>(), evaluator);
                individual.setObjectives(new double[]{firstObj, secondObj});
                individual.setHashCode();

                double normFirstObj = firstObj / (ttp.getMaxTravellingTime() - ttp.getMinTravellingTime());
                double normSecondObj = secondObj / ttp.getMaxProfit();
                individual.setNormalObjectives(new double[]{normFirstObj, normSecondObj});

                front.add(individual);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return front;
    }

    private static String removePrefixAndTtpPostFixFromFileName(String prefixPath, String fileName) {
        if(fileName.endsWith(".ttp")) {
            int prefixPosition = fileName.lastIndexOf(prefixPath) + prefixPath.length();
            return fileName.substring(prefixPosition, fileName.lastIndexOf(".ttp"));
        }
        return fileName;
    }

    private static TTP readFile(int k) {
        var definitionFile = baseDir + instanceWithOPF.get(k).getKey();
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

    private static String printResultsForComparison(List<BaseIndividual<Integer, TTP>> resultIndividuals, boolean isVerbose) {
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

    private static String printParetos(String firstParetoName, List<BaseIndividual<Integer, TTP>> firstPareto,
                                       String secondParetoName, List<BaseIndividual<Integer, TTP>> secondPareto,
                                       boolean isVerbose) {
        String output = ";" + firstParetoName + ";;" + secondParetoName + "\n";
        if(isVerbose) {
//            output += "Profit; Travelling Time\n";
            System.out.println("Profit; Travelling Time");
        }
        for (int i = 0; i < Math.max(firstPareto.size(), secondPareto.size()); ++i) {
            double runProfit = Double.NaN;
            double runTravellingTime = Double.NaN;
            if(firstPareto.size() - 1 >= i) {
                runProfit = 0;
                runTravellingTime = firstPareto.get(i).getProblem().getTravellingTime();
                int[] selection = firstPareto.get(i).getProblem().getSelection();
                for (int j = 0; j < selection.length; ++j) {
                    if (selection[j] > 0) {
                        runProfit += firstPareto.get(i).getProblem().getKnapsack().getItem(j).getProfit();
                    }
                }
            }

            double apfProfit = Double.NaN;
            double apfTravellingTime = Double.NaN;
            if(secondPareto.size() - 1 >= i) {
                apfTravellingTime = secondPareto.get(i).getObjectives()[0];
                apfProfit = (-1)*secondPareto.get(i).getObjectives()[1];
            }

            if(!Double.isNaN(runProfit) && !Double.isNaN(runTravellingTime)) {
                output += runTravellingTime + ";" + (-1)*runProfit + ";";
            } else {
                output += ";;";
            }

            if(!Double.isNaN(apfProfit) && !Double.isNaN(apfTravellingTime)) {
                output += apfTravellingTime + ";" + (-1)*apfProfit;
            } else {
                output += ";;";
            }
            output += "\n";

            if(isVerbose) {
                System.out.println(output);
            }
        }
        return output;
    }

    private static String printGenes(List<BaseIndividual<Integer, TTP>> resultIndividuals, TTP problem) {
        String output = "types;";
        for(int i = 0; i < problem.getSplitPoint(); i++) {
            output += "city" + i + ";";
        }
        for(int i = problem.getSplitPoint(); i < problem.getNumGenes(); i++) {
            output += "k" + (i - problem.getSplitPoint()) + ";";
        }
        output += "\n";

        for (int i = 0; i < resultIndividuals.size(); ++i) {
            BaseIndividual<Integer, TTP> ind = resultIndividuals.get(i);
            output += "genotype;";
            for(int j = 0; j < ind.getGenes().size(); j++) {
                output += ind.getGenes().get(j) + ";";
            }
            output += "\nfenotype;";
            for(int j = 0; j < ind.getProblem().getPath().length; j++) {
                output += ind.getProblem().getPath()[j] + ";";
            }
            for(int j = 0; j < ind.getProblem().getSelection().length; j++) {
                output += ind.getProblem().getSelection()[j] + ";";
            }
            output += "\n";
        }
        return output;
    }
}
