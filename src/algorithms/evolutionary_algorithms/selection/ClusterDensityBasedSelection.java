package algorithms.evolutionary_algorithms.selection;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;
import javafx.util.Pair;

import java.util.List;

public class ClusterDensityBasedSelection<GENE extends Number, PROBLEM extends BaseProblemRepresentation> {
    public BaseIndividual<Integer, PROBLEM> select(List<Pair<Double, Pair<Double, List<BaseIndividual<Integer, PROBLEM>>>>> clusters, ParameterSet<GENE, BaseProblemRepresentation> parameters) {
        double dispersionSum = 0.0;
        for(var ind: clusters) {
            dispersionSum += ind.getKey();
        }
        double clusterSelectionRandom = parameters.random.nextDouble() * dispersionSum;
        dispersionSum = 0.0;
        List<BaseIndividual<Integer, PROBLEM>> chosenCluster = null;
        for(int i = 0; i < clusters.size() && chosenCluster == null; i++) {
            var cluster = clusters.get(i).getValue();
            dispersionSum += cluster.getKey();
            if(dispersionSum >= clusterSelectionRandom) {
                chosenCluster = cluster.getValue();
            }
        }

        var chosenIndividualIndex = parameters.random.nextInt(chosenCluster.size());
        var chosenIndividual = chosenCluster.get(chosenIndividualIndex);
        return chosenIndividual;
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
