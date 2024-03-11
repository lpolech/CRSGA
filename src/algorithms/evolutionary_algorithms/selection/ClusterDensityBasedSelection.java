package algorithms.evolutionary_algorithms.selection;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.util.ClusteringResult;
import algorithms.evolutionary_algorithms.util.IndividualWithDstToItsCentre;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;
import interfaces.QualityMeasure;
import javafx.util.Pair;

public class ClusterDensityBasedSelection<GENE extends Number, PROBLEM extends BaseProblemRepresentation> {
    public ClusterDensityBasedSelection(int tournamentSize) {
        this.tournamentSize = tournamentSize;
    }

    private int tournamentSize;
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

    /* Same cluster, dynamic tournament cluster selection based on clustering measures */
    public Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>> selectClsMeasureRandomIndSameCls(
            ClusteringResult clusteringResult,
            ParameterSet<GENE, BaseProblemRepresentation> parameters,
            QualityMeasure clusterWeightMeasure) {
        int numberOfClusters = clusteringResult.getClustersDispersion().size();
        int dynamicTurSize = Math.max(1, (int) ((this.tournamentSize * numberOfClusters) /100.0)); // tur size depents on the number of clusters as at the beginning there is not many clusters
        int chosenClusterIndex = (int) (parameters.random.nextDouble() * numberOfClusters);

        for (int i = 0; i < dynamicTurSize - 1; ++i) {
            chosenClusterIndex = chooseCluster(chosenClusterIndex,
                    (int) (parameters.random.nextDouble() * numberOfClusters),
                    clusteringResult, clusterWeightMeasure);
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

    /* Individuals from 2 neighbourhood clusters, dynamic tournament cluster selection based on clustering measures */
    public Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>> select(
            ClusteringResult clusteringResult,
            ParameterSet<GENE, BaseProblemRepresentation> parameters,
            QualityMeasure clusterWeightMeasure) {
        int numberOfClusters = clusteringResult.getClustersDispersion().size();
        int dynamicTurSize = Math.max(1, (int) ((this.tournamentSize * numberOfClusters) /100.0)); // tur size depents on the number of clusters as at the beginning there is not many clusters
        int chosenClusterIndex = (int) (parameters.random.nextDouble() * numberOfClusters);

        for (int i = 0; i < dynamicTurSize - 1; ++i) {
            chosenClusterIndex = chooseCluster(chosenClusterIndex,
                    (int) (parameters.random.nextDouble() * numberOfClusters),
                    clusteringResult, clusterWeightMeasure);
        }

        var chosenCluster = clusteringResult.getClustersWithIndDstToCentre().get(chosenClusterIndex);
        var chosenClusteringCluster = clusteringResult.getClustersAndTheirStatistics().getClusters()[chosenClusterIndex];
        chosenClusteringCluster.getCenter().recordUsage();
        var chosenClusterNeighbourIndex = chosenClusterIndex;
        if(!clusteringResult.getClustersAndTheirStatistics().getClusterChosenNeighbourIndicies().get(chosenClusterIndex).isEmpty()) {
            chosenClusterNeighbourIndex = clusteringResult.getClustersAndTheirStatistics()
                    .getClusterChosenNeighbourIndicies().get(chosenClusterIndex).get(0);
        }
        var chosenClusterNeighbour = clusteringResult.getClustersWithIndDstToCentre().get(chosenClusterNeighbourIndex);
        var chosenClusteringNeighbourCluster = clusteringResult.getClustersAndTheirStatistics().getClusters()[chosenClusterNeighbourIndex];

        int chosenClusterSize = chosenClusteringCluster.getNumberOfPoints();
        int chosenClusterNeighbourSize = chosenClusteringNeighbourCluster.getNumberOfPoints();
        int totalNumberOfIndividuals = chosenClusterSize + chosenClusterNeighbourSize;

        var chosenFirstIndividualIndex = parameters.random.nextInt(totalNumberOfIndividuals);
        var chosenSecondIndividualIndex = chosenFirstIndividualIndex;
        while(chosenFirstIndividualIndex == chosenSecondIndividualIndex && totalNumberOfIndividuals > 1) {
            chosenSecondIndividualIndex = parameters.random.nextInt(totalNumberOfIndividuals);
        }

        IndividualWithDstToItsCentre chosenFirstIndividual = null;
        if(chosenFirstIndividualIndex >= chosenClusterSize) {
            chosenFirstIndividualIndex = chosenFirstIndividualIndex - chosenClusterSize;
            chosenFirstIndividual =
                    (IndividualWithDstToItsCentre)chosenClusterNeighbour.getCluster()
                            .get(chosenFirstIndividualIndex);
            chosenClusteringNeighbourCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();
        } else {
            chosenFirstIndividual =
                    (IndividualWithDstToItsCentre)chosenCluster.getCluster()
                            .get(chosenFirstIndividualIndex);
            chosenClusteringCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();
        }

        IndividualWithDstToItsCentre chosenSecondIndividual = null;
        if(chosenSecondIndividualIndex >= chosenClusterSize) {
            chosenSecondIndividualIndex = chosenSecondIndividualIndex - chosenClusterSize;
            chosenSecondIndividual =
                    (IndividualWithDstToItsCentre)chosenClusterNeighbour.getCluster()
                            .get(chosenSecondIndividualIndex);
            chosenClusteringNeighbourCluster.getPoints()[chosenSecondIndividualIndex].recordUsage();
        } else {
            chosenSecondIndividual =
                    (IndividualWithDstToItsCentre)chosenCluster.getCluster()
                            .get(chosenSecondIndividualIndex);
            chosenClusteringCluster.getPoints()[chosenSecondIndividualIndex].recordUsage();
        }

        return new Pair<>(chosenFirstIndividual.getIndividual(), chosenSecondIndividual.getIndividual());
    }

    /* Same cluster, STATIC tournament cluster selection */
    public Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>> staticTurSelect(
            ClusteringResult clusteringResult,
            ParameterSet<GENE, BaseProblemRepresentation> parameters) {
        int numberOfClusters = clusteringResult.getClustersDispersion().size();
        int dynamicTurSize = Math.max(1, this.tournamentSize); // tur size depents on the number of clusters as at the beginning there is not many clusters
        int chosenClusterIndex = (int) (parameters.random.nextDouble() * numberOfClusters);

        for (int i = 0; i < dynamicTurSize - 1; ++i) {
            chosenClusterIndex = chooseClusterBasedOnDispersion(chosenClusterIndex,
                    (int) (parameters.random.nextDouble() * numberOfClusters),
                    clusteringResult);
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

    private int chooseClusterBasedOnDispersion(int firstClusterIndex, int secondClusterIndex, ClusteringResult clusteringResult) {
        double firstClusterDispersion = clusteringResult.getClustersDispersion().get(firstClusterIndex);
        double secondClusterDispersion = clusteringResult.getClustersDispersion().get(secondClusterIndex);
        if (firstClusterDispersion > secondClusterDispersion) {
            return firstClusterIndex;
        } else {
            return secondClusterIndex;
        }
    }

    private int chooseCluster(int firstClusterIndex, int secondClusterIndex, ClusteringResult clusteringResult,
                              QualityMeasure clusterWeightMeasure) {
        double firstClusterWeight = clusteringResult.getClusterWeights().get(firstClusterIndex);
        double secondClusterWeight = clusteringResult.getClusterWeights().get(secondClusterIndex);
        if (clusterWeightMeasure.isFirstMeasureBetterThanSecond(firstClusterWeight, secondClusterWeight)) {
            return secondClusterIndex; //we prefer worse clusters
        } else {
            return firstClusterIndex;
        }
    }

    /* Same cluster random wheel selection, dynamic edges enabled/disbaled in KmeasClustering class */
    public Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>> wheelSelect(
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
