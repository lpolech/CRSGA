package algorithms.evolutionary_algorithms.selection;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;
import javafx.util.Pair;

import java.util.List;

public class ClusterDensityBasedSelection<GENE extends Number, PROBLEM extends BaseProblemRepresentation> {
    public Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>> select(List<Pair<Double, List<Pair<Double, BaseIndividual<Integer, PROBLEM>>>>> dispersionWithIndDstToCentreAndTheInd, ParameterSet<GENE, BaseProblemRepresentation> parameters) {
        double dispersionSum = 0.0;
        for(var ind: dispersionWithIndDstToCentreAndTheInd) {
            dispersionSum += ind.getKey();
        }
        double clusterSelectionRandom = parameters.random.nextDouble() * dispersionSum;
        dispersionSum = 0.0;
        List<Pair<Double, BaseIndividual<Integer, PROBLEM>>> chosenCluster = null;
        for(int i = 0; i < dispersionWithIndDstToCentreAndTheInd.size() && chosenCluster == null; i++) {
            var cluster = dispersionWithIndDstToCentreAndTheInd.get(i);
            dispersionSum += cluster.getKey();
            if(dispersionSum >= clusterSelectionRandom) {
                chosenCluster = cluster.getValue();
            }
        }

        var chosenFirstIndividualIndex = -1;
        double maxDistance = -Double.MIN_VALUE;
        for(int i = 0; i < chosenCluster.size(); i++) {
            if(chosenCluster.get(i).getKey() > maxDistance) {
                maxDistance = chosenCluster.get(i).getKey();
                chosenFirstIndividualIndex = i;
            }
        }
        var chosenFirstIndividual = chosenCluster.get(chosenFirstIndividualIndex).getValue();

        var chosenSecondIndividualIndex = parameters.random.nextInt(chosenCluster.size());
        while(chosenFirstIndividualIndex == chosenSecondIndividualIndex && chosenCluster.size() > 1) {
            chosenSecondIndividualIndex = parameters.random.nextInt(chosenCluster.size());
        }
        var chosenSecondIndividual = chosenCluster.get(chosenSecondIndividualIndex).getValue();

        return new Pair<>(chosenFirstIndividual, chosenSecondIndividual);
    }

    private BaseIndividual<Integer, PROBLEM> findIndividual(double individualValue, List<BaseIndividual<Integer, PROBLEM>> cluster, List<Pair<Double, List<BaseIndividual<Integer, PROBLEM>>>> clusters, int clusterValue) {
        if(cluster == null || cluster.size() == 0)
            return null;
        if(individualValue == 1.0)
            return cluster.get(cluster.size() - 1);

        double step = 1.0/cluster.size();
        double individualIndex = individualValue /step;

        return cluster.get((int) individualIndex);
    }

    private List<BaseIndividual<Integer, PROBLEM>> findCluster(List<Pair<Double, List<BaseIndividual<Integer, PROBLEM>>>> clusters, int clusterValue) {
        int index = 0;
        int sum = 0;
        while(sum <= clusterValue){
            index++;
            sum += index;
        }
        return clusters.get(index - 1).getValue();
    }

    private List<BaseIndividual<Integer, PROBLEM>> getCluster(List<Pair<Double, List<BaseIndividual<Integer, PROBLEM>>>> clusters, int clustersNumber, int rankSum, int clusterValue) {
        var clusterIndex = subtractNumbers(rankSum, clustersNumber, clusterValue);
        return clusters.get(clusterIndex - 1).getValue();
    }
    public static int subtractNumbers(int num, int index, int clusterValue) {
        if (num <= clusterValue) {
            return index;
        }
        return subtractNumbers(num - index, index - 1, clusterValue);
    }
}
