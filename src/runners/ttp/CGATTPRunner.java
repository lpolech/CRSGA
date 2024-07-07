package runners.ttp;

import algorithms.evaluation.BaseEvaluator;
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
import algorithms.quality_measure.ApfDistance;
import algorithms.quality_measure.HVMany;
import algorithms.quality_measure.InvertedGenerationalDistance;
import algorithms.quality_measure.Purity;
import distance_measures.Euclidean;
import interfaces.QualityMeasure;
import internal_measures.FlatWithinBetweenIndex;
import javafx.util.Pair;
import util.ParameterFunctions;
import util.random.RandomInt;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CGATTPRunner {
    private static final Logger LOGGER = Logger.getLogger( CGATTPRunner.class.getName() );
    private static final String baseDir = "." + File.separator; //assets/definitions/TTP/selected_01/";
    private static final List<Pair<String, String>> instanceWithOPF = Arrays.asList(
//            new Pair<>("eil51_n50_bounded-strongly-corr_01.ttp", "D:\\Coding\\CGA\\apf\\24-06-11_eil51_n50_bounded-strongly-corr_01_merged.csv")//,
//            new Pair<>("eil51_n50_uncorr-similar-weights_01.ttp", "D:\\Coding\\CGA\\apf\\24-06-11_eil51_n50_uncorr-similar-weights_01_merged.csv"),
//            new Pair<>("eil51_n50_uncorr_01.ttp", "D:\\Coding\\CGA\\apf\\24-06-11_eil51_n50_uncorr_01_merged.csv")//,
//            new Pair<>("eil51_n150_bounded-strongly-corr_01.ttp", "D:\\Coding\\CGA\\apf\\24-06-11_eil51_n150_bounded-strongly-corr_01_merged.csv"),
//            new Pair<>("eil51_n150_uncorr_01.ttp", "D:\\Coding\\CGA\\apf\\24-06-11_eil51_n150_uncorr_01_merged.csv"),
//            new Pair<>("eil51_n150_uncorr-similar-weights_01.ttp", "D:\\Coding\\CGA\\apf\\24-06-11_eil51_n150_uncorr-similar-weights_01_merged.csv"),
//            new Pair<>("eil51_n250_bounded-strongly-corr_01.ttp", "D:\\Coding\\CGA\\apf\\24-06-11_eil51_n250_bounded-strongly-corr_01_merged.csv"),
//            new Pair<>("eil51_n250_uncorr_01.ttp", "D:\\Coding\\CGA\\apf\\24-06-11_eil51_n250_uncorr_01_merged.csv"),
//            new Pair<>("eil51_n250_uncorr-similar-weights_01.ttp", "D:\\Coding\\CGA\\apf\\24-06-11_eil51_n250_uncorr-similar-weights_01_merged.csv"),
            new Pair<>("eil51_n500_bounded-strongly-corr_01.ttp", "D:\\Coding\\CGA\\apf\\24-06-11_eil51_n500_bounded-strongly-corr_01_merged.csv")//,
//            new Pair<>("eil51_n500_uncorr-similar-weights_01.ttp", "D:\\Coding\\CGA\\apf\\24-06-11_eil51_n500_uncorr-similar-weights_01_merged.csv"),
//            new Pair<>("eil51_n500_uncorr_01.ttp", "D:\\Coding\\CGA\\apf\\24-06-11_eil51_n500_uncorr_01_merged.csv"),
//            new Pair<>("kroA100_n99_bounded-strongly-corr_01.ttp", "D:\\Coding\\CGA\\apf\\24-06-30_kroA100_n99_bounded-strongly-corr_01_merged_SingleFlip.csv"),
//            new Pair<>("kroA100_n99_uncorr_01.ttp", "D:\\Coding\\CGA\\apf\\24-06-30_kroA100_n99_uncorr_01_merged_SingleFlip.csv"),
//            new Pair<>("kroA100_n99_uncorr-similar-weights_01.ttp", "D:\\Coding\\CGA\\apf\\24-06-30_kroA100_n99_uncorr-similar-weights_01_merged_SingleFlip.csv"),
//            new Pair<>("pr76_n75_bounded-strongly-corr_01.ttp", "D:\\Coding\\CGA\\apf\\24-06-30_pr76_n75_bounded-strongly-corr_01_merged_SingleFlip.csv"),
//            new Pair<>("pr76_n75_uncorr_01.ttp", "D:\\Coding\\CGA\\apf\\24-06-30_pr76_n75_uncorr_01_merged_SingleFlip.csv"),
//            new Pair<>("pr76_n75_uncorr-similar-weights_01.ttp", "D:\\Coding\\CGA\\apf\\24-06-30_pr76_n75_uncorr-similar-weights_01_merged_SingleFlip.csv"),
//            new Pair<>("rd100_n99_bounded-strongly-corr_01.ttp", "D:\\Coding\\CGA\\apf\\24-06-30_rd100_n99_bounded-strongly-corr_01_merged_SingleFlip.csv"),
//            new Pair<>("rd100_n99_uncorr_01.ttp", "D:\\Coding\\CGA\\apf\\24-06-30_rd100_n99_uncorr_01_merged_SingleFlip.csv"),
//            new Pair<>("rd100_n99_uncorr-similar-weights_01.ttp", "D:\\Coding\\CGA\\apf\\24-06-30_rd100_n99_uncorr-similar-weights_01_merged_SingleFlip.csv"),
//            new Pair<>("berlin52_n51_bounded-strongly-corr_01.ttp", "D:\\Coding\\CGA\\apf\\24-07-01_berlin52_n51_bounded-strongly-corr_01_merged_SingleFlip.csv"),
//            new Pair<>("berlin52_n51_uncorr_01.ttp", "D:\\Coding\\CGA\\apf\\24-07-01_berlin52_n51_uncorr_01_merged_SingleFlip.csv"),
//            new Pair<>("berlin52_n51_uncorr-similar-weights_01.ttp", "D:\\Coding\\CGA\\apf\\24-07-01_berlin52_n51_uncorr-similar-weights_01_merged_SingleFlip.csv")
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
            System.out.println("File " + (k+1) + "/" + instanceWithOPF.size());
            TTP ttp = readFile(k);
            if (ttp == null) return null;

            ParameterSet<Integer, TTP> parameters = setParameters(ttp);
            List<BaseIndividual> optimalParetoFront = readAPF(instanceWithOPF.get(k).getValue(), ttp, parameters.evaluator);
            InvertedGenerationalDistance igdCalculator = new InvertedGenerationalDistance(optimalParetoFront);
            ApfDistance apfDistanceCalculator = new ApfDistance(optimalParetoFront);
            Purity purityCalculator = new Purity(optimalParetoFront);

            QualityMeasure[] clusterWeightMeasureList = new QualityMeasure[] {
//                new FlatCalinskiHarabasz(new Euclidean()), //this measures is sensitive to useSubtree toggle
//                new FlatDaviesBouldin(new Euclidean()), //this measures is sensitive to useSubtree toggle
//                new FlatDunn1(new Euclidean()), //this measures is sensitive to useSubtree toggle
//                new FlatDunn4(new Euclidean()), //this measures is sensitive to useSubtree toggle
                    new FlatWithinBetweenIndex(new Euclidean()), //this measures is sensitive to useSubtree toggle
//                new FlatDunn2(new Euclidean()),
//                new FlatDunn3(new Euclidean())
            };

            int NUMBER_OF_REPEATS = 5;
            int[] generationLimitList = new int[] {250_000};//{250_000};//{50_000};//{250_000};//{5_000};//{5_000};//{25_000, 12_500, 5_000, 2_500, 1_666, 1_250, 500, 250};//500};
            int[] populationSizeList = new int[] {10};//{2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24};//{10, 50, 100, 150, 200, 250, 300, 350, 400, 450, 500};//{10};//{20};//{10, 100};//{20};//{10, 20, 50, 100};//{50};// 100};
            double[] TSPmutationProbabilityList = new double[] {0.45};//{0.0, 0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9, 0.95, 1.0};//{0.25};//{0.3};//{0.4};//}{0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, {0.4};//{0.4};//{0.1, 0.2, 0.3, 0.4, 0.5};//{0.01};//{0.007};//{0.002, 0.004, 0.006, 0.008};//{0.004};//{0.0, 0.0001, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.9};//{0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.0, 0.0001, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6}; //{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
            double[] KNAPmutationProbabilityList = new double[] {0.0027};//{0.0001, 0.0002, 0.0003, 0.0004, 0.0005, 0.0006, 0.0007, 0.0008, 0.0009, 0.0011, 0.0012, 0.0013, 0.0014, 0.0015, 0.0016, 0.0017, 0.0018, 0.0019, 0.0021, 0.0022, 0.0023, 0.0024, 0.0025, 0.0026, 0.0027, 0.0028, 0.0029, 0.0031, 0.0032, 0.0033, 0.0034, 0.0035, 0.0036, 0.0037, 0.0038, 0.0039};//, 0.004, 0.005, 0.006, 0.007, 0.008, 0.009, 0.01, 0.011, 0.012, 0.013, 0.014, 0.015, 0.016, 0.017, 0.018, 0.019};//{0.0031};//{0.0001, 0.0003, 0.0005, 0.0007, 0.0009, 0.0011, 0.0013, 0.0015, 0.0017, 0.0019, 0.0021, 0.0023, 0.0025, 0.0027, 0.0029, 0.0031, 0.0033, 0.0035, 0.0037, 0.0039};//{0.0024};//0.04};//{0.001, 0.005, 0.01, 0.015, 0.02, 0.03, 0.04, 0.05, 0.1, 0.125, 0.15};//{0.006};//, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.034};//{0.006};//{0.006};//{0.006};//{0.8, 0.9, 1.0};//{0.01};//{0.006};//{0.004, 0.005, 0.006, 0.007};//{0.01};//{0.01, 0.02, 0.03, 0.04};//, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.0, 0.0025, 0.005, 0.0075}; //{0.005, 0.01, 0.015};//, 0.005, 0.015};
            double[] TSPcrossoverProbabilityList = new double[] {0.6};//{0.05, 0.15, 0.25, 0.35, 0.45, 0.55, 0.65, 0.75, 0.85, 0.95};//{0.4};//{0.0, 0.1, 0.3, 0.5, 0.7, 0.9};//{0.25, 0.35, 0.45, 0.55, 0.65, 0.75, 0.85, 0.95};//}{0.0, 0.15, 0.25, 0.35, 0.45, 0.55, 0.65, 0.75, 0.85, 0.95};//{0.45};{0.8};//}{0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9, 0.95, 1.0};//{0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.8};//{0.2};//{0.2};//{0.0, 0.05, 0.1, 0.15, 0.2}; //{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
            double[] KNAPcrossoverProbabilityList = new double[] {0.7};//{0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{1.0};//{0.9, 1.0};//{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.8};//{0.6, 0.7, 0.8, 0.9, 1.0};//}{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.95};//{0.95};//{0.95};//{0.95};//{0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9, 0.95, 1.0};//{0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.95};//{0.95};//{0.75, 0.8, 0.85, 0.9, 0.95, 1.0, 1.5};//{0.7};//{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};//{0.05, 0.1, 0.2, 0.3, 0.4, 0.5};//{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
            int[] numberOfClusterList = new int[] {2};//{2, 4, 6, 8, 10, 20};//{5};//{2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 15, 17, 20, 22, 25, 30};//{5};//{2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 15, 17, 20, 22, 25, 30};//{2};//{2, 3, 4, 5, 10, 20};//{3};
            int[] clusterisationAlgorithmIterList = new int[]{50};//100};
            double[] edgeClustersDispersion = new double[] {2};//{0.5, 1.5, 2.5, 3.5, 4.5, 5.5, 6.5, 7.5, 8.5, 9.5};//{2};//{0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10, 20, 50, 1000};//{2.5};//{0.0, 0.5, 1.5, 2.5, 3.5, 4.5, 7.0};//{4.0};//{0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10, 20, 50, 1000};//{4.0};//{0.5, 1.0, 1.5, 2.5, 3.5, 4.5, 5.5, 6.5, 7.5, 8.5, 9.5, 20, 50};//{4.0};//{0.5, 1.5, 2.5, 3.5, 4.5, 5.5, 6.5, 7.5, 8.5, 9.5, 20, 50};//{4};//{0.5};//{4};//{0.1, 0.5, 1, 2, 4, 10, 100};//{4}//{0.05, 0.1, 0.3, 0.5, 0.7, 0.9, 1, 4, 5, 10.0, 50, 100, 1_000, 5_000}; //{4};//, 10_000, 15_000, 20_000, 50_000, 100_000};//{0.1, 0.2, 0.3, 0.4, 0.5, 1.0, 1.5, 2.0};//{0.5, 1.0, 1.5, 2.0}; //}{0.1, 0.2, 0.3, 0.4, 0.5, 1.0, 1.5, 2.0};
            int[] tournamentSizeList = new int[] {100};//{60};//{20, 40, 60, 80, 100}; //{0.95};////{200};//{10, 30, 50, 70, 90, 120, 200}; //{150};//{60, 70, 80, 90, 100}; //{80};//{10};//{80};//{10, 20, 30, 40, 50, 60, 70, 80, 90, 100}; //{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30, 50, 100}; //{90};
            int[] minTournamentSizeList = new int[] {15};//{30, 40, 50, 60, 70};
            int[] populationTurPropList = new int[]{100}; //{50};
            int[] mutationVersionList = new int[]{1};
            int[] crossoverVersionList = new int[]{6}; //{1, 2, 3, 4, 5, 6};//
            int[] indExclusionUsageLimitList = new int[] {750};//{300, 400, 500, 600, 700, 800, 900, 1000};//{250};//{100, 200, 300, 400, 500, 600, 700, 800, 900, 1000};//{550, 600, 650, 700, 750, 800, 850, 900, 950, 1000};//}{50, 100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 750, 800, 850, 900, 950, 1000};//{50, 100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 750, 800, 850, 900, 1000};//{25, 50, 75, 100, 125, 150, 175, 200, 225, 250, 275, 300, 325, 350, 375, 400, 425, 450, 475, 500, 525, 550, 575, 600, 625, 650, 675, 700};
            int[] indExclusionGenDurationList = new int[] {650};//{50, 100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 750, 800, 850, 900, 950, 1000};//}{150};//{100, 300, 500, 700, 900};//{150};//{{550};//{520, 540, 560, 580, 600, 620, 640, 660, 680};//{50, 150, 250, 350, 450, 550, 650};//{50, 100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600};
            double[] turDecayParamList = new double[] {-5};//{-0.5, -1.5, -3, -4, -5, -6, -7, -8, -9, -10, -11, -12.5, -13.5, -14.5, -15.5};//{-6, -8, -15, -100};

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
                                                        for( int r = 0; r < populationTurPropList.length; r++) {
                                                            int populationTurProp = populationTurPropList[r];
                                                            for (int ii = 0; ii < mutationVersionList.length; ii++) {
                                                                int mutationVersion = mutationVersionList[ii];
                                                                for (int jj = 0; jj < crossoverVersionList.length; jj++) {
                                                                    int crossoverVersion = crossoverVersionList[jj];
                                                                    for (int kk = 0; kk < indExclusionUsageLimitList.length; kk++) {
                                                                        int indExclusionUsageLimit = indExclusionUsageLimitList[kk];
                                                                        for (int ll = 0; ll < indExclusionGenDurationList.length; ll++) {
                                                                            int indExclusionGenDuration = indExclusionGenDurationList[ll];
                                                                            for (int mm = 0; mm < turDecayParamList.length; mm++) {
                                                                                double turDecayParamVal = turDecayParamList[mm];
                                                                                for(int nn = 0; nn < minTournamentSizeList.length; nn++) {
                                                                                    int minTournamentSizeVal = minTournamentSizeList[nn];

//                                                                    var cost = generationLimitVal * populationSizeVal;
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
                                                                                    paramsMap.put("populationTurProp", populationTurProp);
                                                                                    paramsMap.put("mutationVersion", mutationVersion);
                                                                                    paramsMap.put("crossoverVersion", crossoverVersion);
                                                                                    paramsMap.put("indExclusionUsageLimit", indExclusionUsageLimit);
                                                                                    paramsMap.put("indExclusionGenDuration", indExclusionGenDuration);
                                                                                    paramsMap.put("turDecayParam", turDecayParamVal);
                                                                                    paramsMap.put("minTournamentSize", minTournamentSizeVal);

                                                                                    cartesianProductOfParams.add(paramsMap);
                                                                                }
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
                                }
                            }
                        }
                    }
                }
            }

            System.out.println("Number of param configurations: " + cartesianProductOfParams.size());
//            Collections.shuffle(cartesianProductOfParams);
            String header = "dataset;counter;measure;no of repeats;uber pareto IGD;uber pareto purity;uber pareto apf dst" +
                    ";avgHV;stdev;avgIGD;stdev;avgApfDst;stdev" +
                    ";avgND;stdev;uber pareto size;final uber pareto HV;avg uber pareto hv;stdev"
                    + ";" + "AvgAfterCrossParentDominationCounter"
                    + ";" + "AvgAfterCrossParentDominationProp"
                    + ";" + "AvgAfterCrossAndMutParentDominationCounter"
                    + ";" + "AvgAfterCrossAndMutParentDominationProp"
                    + ";" + "AvgAfterCrossAfterCrossAndMutDominationCounter"
                    + ";" + "AvgAfterCrossAfterCrossAndMutDominationProp"
                    + ";" + "AvgAfterCrossAndMutAfterCrossDominationCounter"
                    + ";" + "AvgAfterCrossAndMutAfterCrossDominationProp"
                    + ";generationLimit;populationSize;TSPmutationProbability" +
                    ";KNAPmutationProbability;TSPcrossoverProbability;KNAPcrossoverProbability;numberOfClusters" +
                    ";clusterIterLimit;edgeClustersProb;tournamentSize;populationTurProp;mutationVersion;crossoverVersion" +
                    ";indExclusionUsageLimit;indExclusionGenDuration;turDecayParam;minTournamentSize";

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
                var eachRepeatIGD = new ArrayList<Double>();
                var eachRepeatApfDst = new ArrayList<Double>();
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
                int populationTurProp = (int) params.get("populationTurProp");
                int mutationVersion = (int) params.get("mutationVersion");
                int crossoverVersion = (int) params.get("crossoverVersion");
                int indExclusionUsageLimit = (int) params.get("indExclusionUsageLimit");
                int indExclusionGenDuration = (int) params.get("indExclusionGenDuration");
                double turDecayParam = (double) params.get("turDecayParam");
                int minTournamentSize = (int) params.get("minTournamentSize");

                List<BaseIndividual<Integer, TTP>> bestAPF = null;
                double bestAPFHV = -Double.MIN_VALUE;

                String outputFilename = "." + File.separator
                        + removeTtpPostfixFromFileName(instanceWithOPF.get(k).getKey())
                        + "_m-" + clusterWeightMeasure.getName()
                        + "_g" + generationLimit + "_p-" + populationSize
                        + "_Tm" + TSPmutationProbability + "_Km" + KNAPmutationProbability
                        + "_Tc" + TSPcrossoverProbability + "_Kc" + KNAPcrossoverProbability
                        + "_cN" + numberOfClusters + "_cI" + clusterIterLimit + "_edgC"
                        + edgeClustersDispVal + "_t" + tournamentSize + "_popT" + populationTurProp
                        + "_eL" + indExclusionUsageLimit + "_eg" + indExclusionGenDuration + "_d"
                        + turDecayParam + "_mt" + minTournamentSize;

                String bestAPFoutputFile = "bestAPF";
                int bestIterNumber = 0;
                File theDir = new File(outputFilename);
                if (!theDir.exists()){
                    theDir.mkdirs();
                }

                List<BaseIndividual<Integer, TTP>> uberPareto = new ArrayList<>();
                for(int i = 0; i < NUMBER_OF_REPEATS; i++) {
                    parameters.mutationVersion = mutationVersion;
                    parameters.crossoverVersion = crossoverVersion;
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
                            igdCalculator,
                            apfDistanceCalculator,
                            outputFilename,
                            i,
                            indExclusionUsageLimit,
                            indExclusionGenDuration,
                            turDecayParam,
                            minTournamentSize
                    );

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

                    var igdValue = igdCalculator.getMeasure(result);
                    eachRepeatIGD.add(igdValue);

                    var apfDst = apfDistanceCalculator.getMeasure(result);
                    eachRepeatApfDst.add(apfDst);

                    String instanceName = instanceWithOPF.get(k).getKey();
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
                    }
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
                NDstandardDeviation = Math.sqrt(NDstandardDeviation/eachRepeatND.size());

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

                OptionalDouble averageIGD = eachRepeatIGD
                        .stream()
                        .mapToDouble(a -> a)
                        .average();
                var averageIGDVal = averageIGD.isPresent() ? averageIGD.getAsDouble() : -666.0;
                double averageIGDValStdev = 0.0;
                for(double num: eachRepeatIGD) {
                    averageIGDValStdev += Math.pow(num - averageIGDVal, 2);
                }
                averageIGDValStdev = Math.sqrt(averageIGDValStdev/eachRepeatIGD.size());

                OptionalDouble averageApfDst = eachRepeatApfDst
                        .stream()
                        .mapToDouble(a -> a)
                        .average();
                var averageApfDstVal = averageApfDst.isPresent() ? averageApfDst.getAsDouble() : -666.0;
                double averageApfDstValStdev = 0.0;
                for(double num: eachRepeatApfDst) {
                    averageApfDstValStdev += Math.pow(num - averageApfDstVal, 2);
                }
                averageApfDstValStdev = Math.sqrt(averageApfDstValStdev/eachRepeatApfDst.size());

                String runResult = instanceWithOPF.get(k).getKey() + ";" + paramCounter + "/" + numberOfParamConfigs + ";"
                        + clusterWeightMeasure.getClass().getName() + ";" + NUMBER_OF_REPEATS
                        + ";" + igdCalculator.getMeasure(uberPareto) + ";" + purityCalculator.getMeasure(uberPareto)
                        + ";" + apfDistanceCalculator.getMeasure(uberPareto) +  ";" + avgHV + ";" + standardDeviation
                        + ";" + averageIGDVal + ";" + averageIGDValStdev + ";" + averageApfDstVal + ";" + averageApfDstValStdev
                        + ";" + avgND + ";" + NDstandardDeviation + ";" + uberPareto.size()
                        + ";" + eachRepeatUberParetoHV.get(eachRepeatUberParetoHV.size()-1) + ";" + uberParetoHV + ";" + uberParetostdev
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
                        + ";" + numberOfClusters + ";" + clusterIterLimit + ";" + edgeClustersDispVal + ";" + tournamentSize
                        + ";" + populationTurProp + ";" + mutationVersion + ";" + crossoverVersion
                        + ";" + indExclusionUsageLimit + ";" + indExclusionGenDuration
                        + ";" + turDecayParam + ";" + minTournamentSize;
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

    private static List<BaseIndividual> readAPF(String apfPath, TTP ttp, BaseEvaluator<Integer, TTP> evaluator) {
        File file = new File(apfPath);
        System.out.print("File;" + file.getName());
        List<BaseIndividual> front = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                double firstObj = Double.parseDouble(values[0]);
                double secondObj = Double.parseDouble(values[1]);
//                        System.out.println(firstObj + ", " + secondObj);

                BaseIndividual<Integer, TTP> individual = new BaseIndividual<>(ttp, new ArrayList<>(), evaluator);
                individual.setObjectives(new double[]{firstObj, secondObj});

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

    private static String removeTtpPostfixFromFileName(String fileName) {
        if(fileName.endsWith(".ttp")) {
            return fileName.substring(0, fileName.lastIndexOf(".ttp"));
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
