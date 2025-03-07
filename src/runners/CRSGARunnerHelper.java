package runners;

import algorithms.evaluation.BaseEvaluator;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;
import algorithms.problem.scheduling.Schedule;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CRSGARunnerHelper<GENE extends Number, PROBLEM extends BaseProblemRepresentation> {
    protected Pair<Pair<List<BaseIndividual<GENE, PROBLEM>>, List<BaseIndividual<GENE, PROBLEM>>>, ArrayList<List<BaseIndividual<GENE, PROBLEM>>>>
    normaliseParetoFrontsByMinMax(List<BaseIndividual<GENE, PROBLEM>> optimalApfWithUberPareto,
                                  List<BaseIndividual<GENE, PROBLEM>> uberPareto,
                                  ArrayList<List<BaseIndividual<GENE, PROBLEM>>> eachRepeatResult,
                                  PROBLEM problem,
                                  BaseEvaluator<GENE, PROBLEM> evaluator) {
        List<BaseIndividual<GENE, PROBLEM>> normalisedOptimalApfWithUberPareto = new ArrayList<>();
        List<BaseIndividual<GENE, PROBLEM>> normalisedUberPareto = new ArrayList<>();
        ArrayList<List<BaseIndividual<GENE, PROBLEM>>> normalisedEachRepeatResult = new ArrayList<>();

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
                minValues, maxValues, minNormValues, maxNormValues, problem, evaluator);
        normalisedUberPareto = normaliseByMinMax(uberPareto,
                minValues, maxValues, minNormValues, maxNormValues, problem, evaluator);

        for(var res: eachRepeatResult) {
            normalisedEachRepeatResult.add(normaliseByMinMax(res,
                    minValues, maxValues, minNormValues, maxNormValues, problem, evaluator));
        }

        return new Pair<>(new Pair<>(normalisedOptimalApfWithUberPareto, normalisedUberPareto), normalisedEachRepeatResult);
    }

    protected void getMinMax(List<BaseIndividual<GENE, PROBLEM>> front, int noOfDims, List<Double> minValues, List<Double> maxValues, List<Double> minNormValues, List<Double> maxNormValues) {
        for (int i = 0; i < front.size(); ++i) {
            BaseIndividual<GENE, PROBLEM> sol = front.get(i);
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

    protected static double roundNumberToTwoDecimalPlaces(double number) {
        return Math.round(number * 100.0) / 100.0;
    }

    protected List<BaseIndividual<GENE, PROBLEM>> normaliseByMinMax(List<BaseIndividual<GENE, PROBLEM>> front,
                                                                       List<Double> minValues,
                                                                       List<Double> maxValues,
                                                                       List<Double> minNormValues,
                                                                       List<Double> maxNormValues,
                                                                       PROBLEM problem,
                                                                       BaseEvaluator<GENE, PROBLEM> evaluator) {
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

        List<BaseIndividual<GENE, PROBLEM>> normalisedFront = new ArrayList<>(front.size());
        List<Double> diffVec = new ArrayList<>(Collections.nCopies(objDims, 0.0));
        List<Double> diffNormVec = new ArrayList<>(Collections.nCopies(objDims, 0.0));
        for (int v = 0; v < objDims; ++v) {
            diffVec.set(v, maxValues.get(v) - minValues.get(v));
            diffNormVec.set(v, maxNormValues.get(v) - minNormValues.get(v));
        }

        for(int i = 0; i < front.size(); ++i) {
            BaseIndividual<GENE, PROBLEM> sol = front.get(i);
            List<Double> normObj = new ArrayList<>(Collections.nCopies(objDims, 0.0));
            List<Double> normNormObj = new ArrayList<>(Collections.nCopies(objDims, 0.0));

            for (int v = 0; v < objDims; ++v) {
                normObj.set(v, (sol.getObjectives()[v] - minValues.get(v)) / diffVec.get(v));
                normNormObj.set(v, (sol.getNormalObjectives()[v] - minNormValues.get(v)) / diffNormVec.get(v));
            }

            BaseIndividual<GENE, PROBLEM> normIndividual = new BaseIndividual<>(problem, new ArrayList<>(), evaluator);
            normIndividual.setObjectives(normObj.stream().mapToDouble(d -> d).toArray());
            normIndividual.setNormalObjectives(normNormObj.stream().mapToDouble(d -> d).toArray());
            normIndividual.setHashCode();

            normalisedFront.add(normIndividual);
        }

        return normalisedFront;
    }

    protected String printResultsForComparison(List<BaseIndividual<GENE, PROBLEM>> resultIndividuals, String[] objNames, boolean isVerbose) {
        if(!resultIndividuals.isEmpty() && resultIndividuals.get(0).getObjectives().length != objNames.length) {
            System.err.println("printResultsForComparison: objective names do not match the number of objectives!");
        }

        String output = "";
//        for(int i = 0; i < objNames.length; i++) {
//            output += objNames[i] + ";";
//        }
//        if(isVerbose) {
//            System.out.println(output);
//        }
//        output += "\n";

        for (int i = 0; i < resultIndividuals.size(); ++i) {
            BaseIndividual<GENE, PROBLEM> ind = resultIndividuals.get(i);

            for(int j = 0; j < ind.getObjectives().length; j++) {
                output += ind.getObjectives()[j] + ";";
            }
            if(isVerbose) {
                System.out.println(output);
            }
            output += "\n";
        }
        return output;
    }

    protected String printParetos(String firstParetoName, List<BaseIndividual<Integer, Schedule>> firstPareto,
                                String secondParetoName, List<BaseIndividual<Integer, Schedule>> secondPareto,
                                String[] objNames, boolean isVerbose, boolean negateTTPCost) {
        if(!firstPareto.isEmpty() && firstPareto.get(0).getObjectives().length != objNames.length) {
            System.err.println("printParetos: objective names do not match the number of objectives in the first pareto front!");
        }

        if(!secondPareto.isEmpty() && secondPareto.get(0).getObjectives().length != objNames.length) {
            System.err.println("printParetos: objective names do not match the number of objectives in the second pareto front!");
        }

        if(!firstPareto.isEmpty() && !secondPareto.isEmpty() && firstPareto.get(0).getObjectives().length != secondPareto.get(0).getObjectives().length) {
            System.err.println("printParetos: pareto fronts do not have the same number of objectives!");
        }
        int noOfObj = firstPareto.get(0).getObjectives().length;

        String output = ";" + firstParetoName;
        String emptyEntry = "";
        for(int i = 0; i < noOfObj; i++) {
            emptyEntry += ";";
        }
        output += emptyEntry + secondParetoName + "\n";

//        for(int i = 0; i < objNames.length; i++) {
//            output += objNames[i] + ";";
//        }
//        if(isVerbose) {
//            System.out.println(output);
//        }
//        output += "\n";

        for (int i = 0; i < Math.max(firstPareto.size(), secondPareto.size()); ++i) {
            List<Double> firstParetoObjVals = new ArrayList<>(noOfObj);
            if(firstPareto.size() - 1 >= i) {
                for(int j = 0; j < noOfObj; j++) {
                    firstParetoObjVals.add(firstPareto.get(i).getObjectives()[j]);
                }
                if(negateTTPCost) {
                    firstParetoObjVals.set(1, (-1)*firstParetoObjVals.get(1));
                }
            }

            List<Double> secondParetoObjVals = new ArrayList<>(noOfObj);
            if(secondPareto.size() - 1 >= i) {
                for(int j = 0; j < noOfObj; j++) {
                    secondParetoObjVals.add(secondPareto.get(i).getObjectives()[j]);
                }
                if(negateTTPCost) {
                    secondParetoObjVals.set(1, (-1)*secondParetoObjVals.get(1));
                }
            }

            if(!firstParetoObjVals.isEmpty()) {
                for(int j = 0; j < noOfObj; j++) {
                    output += firstParetoObjVals.get(j) + ";";
                }
            } else {
                output += emptyEntry;
            }

            if(!secondParetoObjVals.isEmpty()) {
                for(int j = 0; j < noOfObj; j++) {
                    output += secondParetoObjVals.get(j) + ";";
                }
            } else {
                output += emptyEntry;
            }
            output += "\n";

            if(isVerbose) {
                System.out.println(output);
            }
        }
        return output;
    }

    protected static String removePrefixAndPostFixFromFileName(String prefixPath, String postFix, String fileName) {
        if(fileName.endsWith(postFix)) {
            int prefixPosition = fileName.lastIndexOf(prefixPath) + prefixPath.length();
            return fileName.substring(prefixPosition, fileName.lastIndexOf(postFix));
        }
        return fileName;
    }
}
