package algorithms.evolutionary_algorithms.selection;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.util.ClusteringResult;
import algorithms.evolutionary_algorithms.util.IndividualWithDstToItsCentre;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;
import javafx.util.Pair;

public class ClusterDensityBasedSelection<GENE extends Number, PROBLEM extends BaseProblemRepresentation> {
    /* independent cluster selection, random individual in each. Remember to modify the execution part as well! */
    public BaseIndividual<Integer, PROBLEM> selectRandomWheeleBothClusters(
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
        var chosenIndividualIndex = parameters.random.nextInt(chosenCluster.getCluster().size());
        var chosenIndividual = (IndividualWithDstToItsCentre)chosenCluster.getCluster().get(chosenIndividualIndex);
        chosenClusteringCluster.getPoints()[chosenIndividualIndex].recordUsage();

        return chosenIndividual.getIndividual();
    }

    /* Same cluster, tournament cluster selection */
//    public Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>> tournamentClusterSelectionSameClusterRandomIndividualSelect(
//            ClusteringResult clusteringResult,
//            ParameterSet<GENE, BaseProblemRepresentation> parameters) {
//        int numberOfClusters = clusteringResult.getClustersDispersion().size();
//        int parentClusterIndex = -1;
//        parentClusterIndex = chooseCluster((int) (parameters.random.nextDouble() * numberOfClusters),
//                (int) (parameters.random.nextDouble() * numberOfClusters), clusteringResult );
//        for (int i = 0; i < tournamentSize - 2; ++i) {
//            parent = chooseCluster(parent, population.get( (int) (parameters.random.nextDouble() * population.size()) ), clusteringResult);
//        }
//
//        dispersionSum = 0.0;
//        int chosenClusterIndex = -1;
//        for(int i = 0; i < clusteringResult.getClustersWithIndDstToCentre().size() && chosenClusterIndex == -1; i++) {
//            dispersionSum += clusteringResult.getClustersDispersion().get(i);
//            if(dispersionSum >= clusterSelectionRandom) {
//                chosenClusterIndex = i;
//            }
//        }
//
//        var chosenCluster = clusteringResult.getClustersWithIndDstToCentre().get(chosenClusterIndex);
//        var chosenClusteringCluster = clusteringResult.getClustersAndTheirStatistics().getClusters()[chosenClusterIndex];
//        chosenClusteringCluster.getCenter().recordUsage();
//        var chosenFirstIndividualIndex = parameters.random.nextInt(chosenCluster.getCluster().size());
//        var chosenSecondIndividualIndex = chosenFirstIndividualIndex;
//        while(chosenFirstIndividualIndex == chosenSecondIndividualIndex && chosenCluster.getCluster().size() > 1) {
//            chosenSecondIndividualIndex = parameters.random.nextInt(chosenCluster.getCluster().size());
//        }
//
//        var chosenFirstIndividual = (IndividualWithDstToItsCentre)chosenCluster.getCluster().get(chosenFirstIndividualIndex);
//        chosenClusteringCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();
//
//        var chosenSecondIndividual = (IndividualWithDstToItsCentre)chosenCluster.getCluster().get(chosenSecondIndividualIndex);
//        chosenClusteringCluster.getPoints()[chosenSecondIndividualIndex].recordUsage();
//
//        return new Pair<>(chosenFirstIndividual.getIndividual(), chosenSecondIndividual.getIndividual());
//    }

    private int chooseCluster(int firstClusterIndex, int secondClusterIndex, ClusteringResult clusteringResult) {
        double firstClusterDispersion = clusteringResult.getClustersDispersion().get(firstClusterIndex);
        double secondClusterDispersion = clusteringResult.getClustersDispersion().get(secondClusterIndex);
        if (firstClusterDispersion > secondClusterDispersion) {
            return firstClusterIndex;
        } else {
            return secondClusterIndex;
        }
    }

    /* Same cluster random wheel selection */
    public Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>> select(
            ClusteringResult clusteringResult,
            ParameterSet<GENE, BaseProblemRepresentation> parameters) {
        double dispersionSum = 0.0;
        for(var ind: clusteringResult.getClustersDispersion()) {
            dispersionSum += ind;
        }
        double clusterSelectionRandom = parameters.random.nextDouble() * dispersionSum;
        dispersionSum = 0.0;
        int chosenClusterIndex = -1;
        for(int i = 0; i < clusteringResult.getClustersWithIndDstToCentre().size() && chosenClusterIndex == -1; i++) {
            dispersionSum += clusteringResult.getClustersDispersion().get(i);
            if(dispersionSum >= clusterSelectionRandom) {
                chosenClusterIndex = i;
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
}
