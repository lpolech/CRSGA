package algorithms.evolutionary_algorithms.selection;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.util.ClusteringResult;
import algorithms.evolutionary_algorithms.util.IndividualCluster;
import algorithms.evolutionary_algorithms.util.IndividualWithDstToItsCentre;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;
import data.Cluster;
import data.ClustersAndTheirStatistics;
import javafx.util.Pair;

import java.util.List;

public class ClusterDensityBasedSelection<GENE extends Number, PROBLEM extends BaseProblemRepresentation> {
    public Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>> select(
            ClusteringResult clusteringResult,
            ParameterSet<GENE, BaseProblemRepresentation> parameters) {
        double dispersionSum = 0.0;
//        Pair<ClustersAndTheirStatistics, List<Pair<Double, List<Pair<Double, BaseIndividual<Integer, PROBLEM>>>>>>
        for(var ind: clusteringResult.getClustersDispersion()) {
            dispersionSum += ind;
        }
        double clusterSelectionRandom = parameters.random.nextDouble() * dispersionSum;
        dispersionSum = 0.0;
//        Pair<Cluster, List<Pair<Double, BaseIndividual<Integer, PROBLEM>>>> chosenCluster = null;
        int chosenClusterIndex = -1;
        for(int i = 0; i < clusteringResult.getClustersWithIndDstToCentre().size() && chosenClusterIndex == -1; i++) {
//            Pair<Double, List<Pair<Double, BaseIndividual<Integer, PROBLEM>>>> cluster = dispersionWithIndDstToCentreAndTheInd.getValue().get(i);
            dispersionSum += clusteringResult.getClustersDispersion().get(i);
            if(dispersionSum >= clusterSelectionRandom) {
                chosenClusterIndex = i;
//                var clust = dispersionWithIndDstToCentreAndTheInd.getKey().getClusters()[i];
//                chosenCluster = new Pair<>(clust, cluster.getValue());
//                clust.recordUsage();
            }
        }

        var chosenCluster = clusteringResult.getClustersWithIndDstToCentre().get(chosenClusterIndex);
        var chosenClusteringCluster = clusteringResult.getClustersAndTheirStatistics().getClusters()[chosenClusterIndex];
        chosenClusteringCluster.getCenter().recordUsage();
        var chosenFirstIndividualIndex = parameters.random.nextInt(chosenCluster.getCluster().size());
        var chosenSecondIndividualIndex = chosenFirstIndividualIndex;
        while(chosenFirstIndividualIndex == chosenSecondIndividualIndex && chosenCluster.getCluster().size() > 1) {
            chosenSecondIndividualIndex = parameters.random.nextInt(chosenCluster.getCluster().size());
        }

        var chosenFirstIndividual = (IndividualWithDstToItsCentre)chosenCluster.getCluster().get(chosenFirstIndividualIndex);
        chosenClusteringCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();

        var chosenSecondIndividual = (IndividualWithDstToItsCentre)chosenCluster.getCluster().get(chosenSecondIndividualIndex);
        chosenClusteringCluster.getPoints()[chosenSecondIndividualIndex].recordUsage();

        return new Pair<>(chosenFirstIndividual.getIndividual(), chosenSecondIndividual.getIndividual());
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
