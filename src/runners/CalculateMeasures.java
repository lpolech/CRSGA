package runners;

import algorithms.evaluation.BaseEvaluator;
import algorithms.evaluation.EvaluatorType;
import algorithms.factories.EvaluatorFactory;
import algorithms.io.TTPIO;
import algorithms.problem.BaseIndividual;
import algorithms.problem.TTP;
import algorithms.quality_measure.HVMany;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;

public class CalculateMeasures {
    private static BaseEvaluator<Integer, TTP> evaluator;
    private static final String inputDir = "D:\\Coding\\CGA\\bntga\\results";//"D:/Coding/CGA/bntga/test/";
    private static final String instanceName =
//            "eil51_n50_bounded-strongly-corr_01_config0_run";
//            "eil51_n50_uncorr-similar-weights_01_config0_run";
//            "eil51_n50_uncorr_01_config0_run";
//            "eil51_n150_bounded-strongly-corr_01_config0_run";
//            "eil51_n150_uncorr_01_config0_run";
//            "eil51_n150_uncorr-similar-weights_01_config0_run";
//            "eil51_n250_bounded-strongly-corr_01_config0_run";
//            "eil51_n250_uncorr_01_config0_run";
//            "eil51_n250_uncorr-similar-weights_01_config0_run";
//            "eil51_n500_bounded-strongly-corr_01_config0_run";
//            "eil51_n500_uncorr_01_config0_run";
            "eil51_n500_uncorr-similar-weights_01_config0_run";
    private static final String instanceDefinitionFile =
//        "D:\\Coding\\CGA\\eil51_n50_bounded-strongly-corr_01.ttp";
//        "D:\\Coding\\CGA\\eil51_n50_uncorr-similar-weights_01.ttp";
//        "D:\\Coding\\CGA\\eil51_n50_uncorr_01.ttp";
//        "D:\\Coding\\CGA\\eil51_n150_bounded-strongly-corr_01.ttp";
//        "D:\\Coding\\CGA\\eil51_n150_uncorr_01.ttp";
//        "D:\\Coding\\CGA\\eil51_n150_uncorr-similar-weights_01.ttp";
//        "D:\\Coding\\CGA\\eil51_n250_bounded-strongly-corr_01.ttp";
//        "D:\\Coding\\CGA\\eil51_n250_uncorr_01.ttp";
//        "D:\\Coding\\CGA\\eil51_n250_uncorr-similar-weights_01.ttp";
//        "D:\\Coding\\CGA\\eil51_n500_bounded-strongly-corr_01.ttp";
//        "D:\\Coding\\CGA\\eil51_n500_uncorr_01.ttp";
        "D:\\Coding\\CGA\\eil51_n500_uncorr-similar-weights_01.ttp";

    public static void main(String[] args) {
        TTP ttp = readFile(instanceDefinitionFile);
        File dir = new File(inputDir);
        evaluator = new EvaluatorFactory().createEvaluator(EvaluatorType.MULTI_OBJECTIVE_TTP_EVALUATOR, 1.0);
        evaluator.setIndividual(new BaseIndividual<>(ttp, evaluator));
        HVMany hv = new HVMany(evaluator.getNadirPoint());

        List<Double> hvs = new ArrayList<>();
        List<Integer> nds = new ArrayList<>();
        for(File file : dir.listFiles()) {
            if(file.getName().startsWith(instanceName) && file.getName().endsWith("_archive.csv")) {
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
                    var hvValue = hv.getMeasure(front);
                    System.out.println(";" + hvValue + ";" + front.size());
                    hvs.add(hvValue);
                    nds.add(front.size());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        OptionalDouble HVaverage = hvs
                .stream()
                .mapToDouble(a -> a)
                .average();
        var avgHV = HVaverage.isPresent() ? HVaverage.getAsDouble() : -666.0;

        double HVstandardDeviation = 0.0;
        for(double num: hvs) {
            HVstandardDeviation += Math.pow(num - avgHV, 2);
        }
        HVstandardDeviation = Math.sqrt(HVstandardDeviation/hvs.size());

        OptionalDouble NDaverage = nds
                .stream()
                .mapToDouble(a -> a)
                .average();
        var avgND = NDaverage.isPresent() ? NDaverage.getAsDouble() : -666.0;

        double NDstandardDeviation = 0.0;
        for(double num: nds) {
            NDstandardDeviation += Math.pow(num - avgND, 2);
        }
        NDstandardDeviation = Math.sqrt(NDstandardDeviation/nds.size());

        System.out.println("Summary;" + instanceName + ";" + hvs.size() + ";" + avgHV + ";" + HVstandardDeviation + ";" + avgND + ";" + NDstandardDeviation);
    }

    private static TTP readFile(String definitionFilePath) {
        TTPIO reader = new TTPIO();
        TTP ttp = reader.readDefinition(definitionFilePath);
        if (null == ttp) {
            System.out.println("Could not read the Definition " + definitionFilePath);
            return null;
        }
        return ttp;
    }
}
