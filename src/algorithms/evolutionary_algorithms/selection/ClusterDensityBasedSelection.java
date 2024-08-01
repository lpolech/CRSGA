package algorithms.evolutionary_algorithms.selection;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.util.ClusteringResult;
import algorithms.evolutionary_algorithms.util.IndividualWithDstToItsCentre;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;
import interfaces.QualityMeasure;
import javafx.util.Pair;
import util.ParameterFunctions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
        chosenFirstIndividual.getIndividual().setAdjustedUsageCounter(
                chosenClusteringCluster.getPoints()[chosenFirstIndividualIndex].getGlobalUsageCounter());

        var chosenSecondIndividual = (IndividualWithDstToItsCentre)chosenCluster.getCluster().get(chosenSecondIndividualIndex);
        chosenClusteringCluster.getPoints()[chosenSecondIndividualIndex].recordUsage();
        chosenSecondIndividual.getIndividual().setAdjustedUsageCounter(
                chosenClusteringCluster.getPoints()[chosenSecondIndividualIndex].getGlobalUsageCounter());

        return new Pair<>(chosenFirstIndividual.getIndividual(), chosenSecondIndividual.getIndividual());
    }

    /* Individuals from 2 neighbourhood clusters, dynamic tournament cluster selection based on clustering measures */
    public List<Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>> select(
            ClusteringResult clusteringResult,
            ParameterSet<GENE, BaseProblemRepresentation> parameters,
            QualityMeasure clusterWeightMeasure,
            List<BaseIndividual<Integer, PROBLEM>> population,
            ParameterFunctions turDecayFunction,
            int currCost) {
        List<Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>> returnPairs = new ArrayList<>();
//        returnPairs.addAll(addArchiveCrossClustersAllPossiblePairs(clusteringResult, parameters, clusterWeightMeasure, population, turDecayFunction, currCost));
        returnPairs.addAll(addArchiveAllPossiblePairs(clusteringResult, parameters, clusterWeightMeasure, population, turDecayFunction, currCost));
//        returnPairs.addAll(addArchiveNeigbouringPairs(clusteringResult, parameters, clusterWeightMeasure, population, turDecayFunction, currCost));
//        returnPairs.addAll(addPairsOfArchiveAndPopulation(clusteringResult, parameters, clusterWeightMeasure, population));

        return returnPairs;
    }

    public List<Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>> addArchiveCrossClustersAllPossiblePairs(
            ClusteringResult clusteringResult,
            ParameterSet<GENE, BaseProblemRepresentation> parameters,
            QualityMeasure clusterWeightMeasure,
            List<BaseIndividual<Integer, PROBLEM>> population,
            ParameterFunctions turDecayFunction,
            int currCost) {
        List<Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>> returnPairs = new ArrayList<>();
        int numberOfClusters = clusteringResult.getClustersDispersion().size();
        double decayTurFun = turDecayFunction.getVal(currCost);
        int dynamicTurSize = Math.max(1, (int) Math.round(((decayTurFun * numberOfClusters) /100.0))); // tur size depends on the number of clusters as at the beginning there is not many clusters
//        int dynamicTurSize = Math.max(1, (int) ((this.tournamentSize * numberOfClusters) /100.0));
//        System.out.println(decayTurFun);
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
        chosenClusteringNeighbourCluster.getCenter().recordUsage();

        int chosenClusterSize = chosenClusteringCluster.getNumberOfPoints();
        int chosenClusterNeighbourSize = chosenClusteringNeighbourCluster.getNumberOfPoints();

        for(int i = 0; i < chosenClusterSize; i++) {
            IndividualWithDstToItsCentre chosenFirstIndividual;
            chosenFirstIndividual =
                    (IndividualWithDstToItsCentre) chosenCluster.getCluster()
                            .get(i);
            chosenClusteringCluster.getPoints()[i].recordUsage();
            chosenFirstIndividual.getIndividual().recordUsage();

            for (int j = chosenClusterSize; j < chosenClusterSize + chosenClusterNeighbourSize; j++) {
                IndividualWithDstToItsCentre chosenSecondIndividual;
                chosenSecondIndividual =
                        (IndividualWithDstToItsCentre) chosenClusterNeighbour.getCluster()
                                .get(j - chosenClusterSize);
                chosenClusteringNeighbourCluster.getPoints()[j - chosenClusterSize].recordUsage();
                chosenSecondIndividual.getIndividual().recordUsage();

                returnPairs.add(new Pair<>(chosenFirstIndividual.getIndividual(), chosenSecondIndividual.getIndividual()));
            }
        }

        return returnPairs;
    }

    public List<Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>> addArchiveAllPossiblePairs(
            ClusteringResult clusteringResult,
            ParameterSet<GENE, BaseProblemRepresentation> parameters,
            QualityMeasure clusterWeightMeasure,
            List<BaseIndividual<Integer, PROBLEM>> population,
            ParameterFunctions turDecayFunction,
            int currCost) {
        List<Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>> returnPairs = new ArrayList<>();
        int numberOfClusters = clusteringResult.getClustersDispersion().size();
        double decayTurFun = turDecayFunction.getVal(currCost);
        int dynamicTurSize = Math.max(1, (int) Math.round(((decayTurFun * numberOfClusters) /100.0))); // tur size depends on the number of clusters as at the beginning there is not many clusters
//        int dynamicTurSize = Math.max(1, (int) ((this.tournamentSize * numberOfClusters) /100.0));
//        System.out.println(decayTurFun);
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
        chosenClusteringNeighbourCluster.getCenter().recordUsage();

        int chosenClusterSize = chosenClusteringCluster.getNumberOfPoints();
        int chosenClusterNeighbourSize = chosenClusteringNeighbourCluster.getNumberOfPoints();

        for(int i = 0; i < chosenClusterSize + chosenClusterNeighbourSize; i++) {
            IndividualWithDstToItsCentre chosenFirstIndividual;
            if (i >= chosenClusterSize) {
                chosenFirstIndividual =
                        (IndividualWithDstToItsCentre) chosenClusterNeighbour.getCluster()
                                .get(i - chosenClusterSize);
                chosenClusteringNeighbourCluster.getPoints()[i - chosenClusterSize].recordUsage();
                chosenFirstIndividual.getIndividual().recordUsage();
            } else {
                chosenFirstIndividual =
                        (IndividualWithDstToItsCentre) chosenCluster.getCluster()
                                .get(i);
                chosenClusteringCluster.getPoints()[i].recordUsage();
                chosenFirstIndividual.getIndividual().recordUsage();
            }
            for (int j = 0; j < chosenClusterSize + chosenClusterNeighbourSize; j++) {
                if(i == j) {
                    continue;
                }
                IndividualWithDstToItsCentre chosenSecondIndividual;
                if (j >= chosenClusterSize) {
                    chosenSecondIndividual =
                            (IndividualWithDstToItsCentre) chosenClusterNeighbour.getCluster()
                                    .get(j - chosenClusterSize);
                    chosenClusteringNeighbourCluster.getPoints()[j - chosenClusterSize].recordUsage();
                    chosenSecondIndividual.getIndividual().recordUsage();
                } else {
                    chosenSecondIndividual =
                            (IndividualWithDstToItsCentre) chosenCluster.getCluster()
                                    .get(j);
                    chosenClusteringCluster.getPoints()[j].recordUsage();
                    chosenSecondIndividual.getIndividual().recordUsage();
                }
                returnPairs.add(new Pair<>(chosenFirstIndividual.getIndividual(), chosenSecondIndividual.getIndividual()));
            }
        }

        return returnPairs;
    }

    public List<Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>> addArchiveNeigbouringPairs(
            ClusteringResult clusteringResult,
            ParameterSet<GENE, BaseProblemRepresentation> parameters,
            QualityMeasure clusterWeightMeasure,
            List<BaseIndividual<Integer, PROBLEM>> population,
            ParameterFunctions turDecayFunction,
            int currCost) {
        List<Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>> returnPairs = new ArrayList<>();
        int numberOfClusters = clusteringResult.getClustersDispersion().size();
        double decayTurFun = turDecayFunction.getVal(currCost);
        int dynamicTurSize = Math.max(1, (int) Math.round(((decayTurFun * numberOfClusters) /100.0))); // tur size depends on the number of clusters as at the beginning there is not many clusters
//        int dynamicTurSize = Math.max(1, (int) ((this.tournamentSize * numberOfClusters) /100.0));
//        System.out.println(decayTurFun);
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
        chosenClusteringNeighbourCluster.getCenter().recordUsage();

        int chosenClusterSize = chosenClusteringCluster.getNumberOfPoints();
        int chosenClusterNeighbourSize = chosenClusteringNeighbourCluster.getNumberOfPoints();

        List<Pair<Integer, Double>> pointsIndexWithOneObjectiveVal = new ArrayList<>(chosenClusterSize + chosenClusterNeighbourSize);
        int mainObjectiveNumber = parameters.random.nextInt(parameters.evaluator.getNumObjectives());

        for(int i = 0; i < chosenClusterSize; i++) {
            pointsIndexWithOneObjectiveVal.add(new Pair<>(i, chosenClusteringCluster.getPoints()[i].getCoordinate(mainObjectiveNumber)));
        }

        for(int i = chosenClusterSize; i < chosenClusterSize + chosenClusterNeighbourSize; i++) {
            pointsIndexWithOneObjectiveVal.add(new Pair<>(i, chosenClusteringNeighbourCluster.getPoints()[i-chosenClusterSize].getCoordinate(mainObjectiveNumber)));
        }

        pointsIndexWithOneObjectiveVal.sort(new Comparator<Pair<Integer, Double>>() {
            public int compare(Pair<Integer, Double> o1, Pair<Integer, Double> o2) {
                if (Objects.equals(o1.getValue(), o2.getValue()))
                    return 0;
                return o1.getValue() < o2.getValue() ? -1 : 1;
            }
        });

        for(int i = 0; i < pointsIndexWithOneObjectiveVal.size(); i=i+1) {
            int chosenFirstIndividualIndex = pointsIndexWithOneObjectiveVal.get(i).getKey();
            double chosenFirstIndividualFitness = pointsIndexWithOneObjectiveVal.get(i).getValue();
            int chosenSecondIndividualIndex;

            if (i == (pointsIndexWithOneObjectiveVal.size() - 1)) { // last point
                chosenSecondIndividualIndex = pointsIndexWithOneObjectiveVal.get(i - 1).getKey();
            } else if(i == 0) { //first point
                chosenSecondIndividualIndex = pointsIndexWithOneObjectiveVal.get(i + 1).getKey();
            } else {
                int leftNeighbourIndex = pointsIndexWithOneObjectiveVal.get(i - 1).getKey();
                double leftNeighbourFitness = pointsIndexWithOneObjectiveVal.get(i - 1).getValue();
                int rightNeighbourIndex = pointsIndexWithOneObjectiveVal.get(i + 1).getKey();
                double rightNeighbourFitness = pointsIndexWithOneObjectiveVal.get(i + 1).getValue();

                double distToLeft = chosenFirstIndividualFitness - leftNeighbourFitness;
                double distToRight = rightNeighbourFitness - chosenFirstIndividualFitness;
                if(distToLeft < distToRight) {
                    chosenSecondIndividualIndex = rightNeighbourIndex;
                } else {
                    chosenSecondIndividualIndex = leftNeighbourIndex;
                }
            }
            IndividualWithDstToItsCentre chosenFirstIndividual;
            IndividualWithDstToItsCentre chosenSecondIndividual;

            if(chosenFirstIndividualIndex >= chosenClusterSize) {
                chosenFirstIndividualIndex = chosenFirstIndividualIndex - chosenClusterSize;
                chosenFirstIndividual =
                        (IndividualWithDstToItsCentre)chosenClusterNeighbour.getCluster()
                                .get(chosenFirstIndividualIndex);
                chosenClusteringNeighbourCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();
                chosenFirstIndividual.getIndividual().recordUsage();
            } else {
                chosenFirstIndividual =
                        (IndividualWithDstToItsCentre)chosenCluster.getCluster()
                                .get(chosenFirstIndividualIndex);
                chosenClusteringCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();
                chosenFirstIndividual.getIndividual().recordUsage();
            }

            if(chosenSecondIndividualIndex >= chosenClusterSize) {
                chosenSecondIndividualIndex = chosenSecondIndividualIndex - chosenClusterSize;
                chosenSecondIndividual =
                    (IndividualWithDstToItsCentre)chosenClusterNeighbour.getCluster()
                        .get(chosenSecondIndividualIndex);
                chosenClusteringNeighbourCluster.getPoints()[chosenSecondIndividualIndex].recordUsage();
                chosenSecondIndividual.getIndividual().recordUsage();
            } else {
                chosenSecondIndividual =
                    (IndividualWithDstToItsCentre)chosenCluster.getCluster()
                    .get(chosenSecondIndividualIndex);
                chosenClusteringCluster.getPoints()[chosenSecondIndividualIndex].recordUsage();
                chosenSecondIndividual.getIndividual().recordUsage();
            }

            returnPairs.add(new Pair<>(chosenFirstIndividual.getIndividual(), chosenSecondIndividual.getIndividual()));
        }

        return returnPairs;
    }

    public List<Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>> addPairsOfArchiveAndPopulation(
            ClusteringResult clusteringResult,
            ParameterSet<GENE, BaseProblemRepresentation> parameters,
            QualityMeasure clusterWeightMeasure,
            List<BaseIndividual<Integer, PROBLEM>> population) {
        List<Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>> returnPairs = new ArrayList<>();
        int numberOfClusters = clusteringResult.getClustersDispersion().size();
        int dynamicTurSize = Math.max(1, (int) ((this.tournamentSize * numberOfClusters) /100.0)); // tur size depends on the number of clusters as at the beginning there is not many clusters
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
        chosenClusteringNeighbourCluster.getCenter().recordUsage();

        int chosenClusterSize = chosenClusteringCluster.getNumberOfPoints();
        int chosenClusterNeighbourSize = chosenClusteringNeighbourCluster.getNumberOfPoints();

        List<Pair<Integer, Double>> pointsIndexWithOneObjectiveVal = new ArrayList<>(chosenClusterSize + chosenClusterNeighbourSize);
        int mainObjectiveNumber = parameters.random.nextInt(parameters.evaluator.getNumObjectives());

        for(int i = 0; i < chosenClusterSize; i++) {
            pointsIndexWithOneObjectiveVal.add(new Pair<>(i, chosenClusteringCluster.getPoints()[i].getCoordinate(mainObjectiveNumber)));
        }

        for(int i = chosenClusterSize; i < chosenClusterSize + chosenClusterNeighbourSize; i++) {
            pointsIndexWithOneObjectiveVal.add(new Pair<>(i, chosenClusteringNeighbourCluster.getPoints()[i-chosenClusterSize].getCoordinate(mainObjectiveNumber)));
        }

        pointsIndexWithOneObjectiveVal.sort(new Comparator<Pair<Integer, Double>>() {
            public int compare(Pair<Integer, Double> o1, Pair<Integer, Double> o2) {
                if (Objects.equals(o1.getValue(), o2.getValue()))
                    return 0;
                return o1.getValue() < o2.getValue() ? -1 : 1;
            }
        });

        for(int i = 0; i < pointsIndexWithOneObjectiveVal.size(); i=i+1) {
            int chosenFirstIndividualIndex = pointsIndexWithOneObjectiveVal.get(i).getKey();
            double chosenFirstIndividualFitness = pointsIndexWithOneObjectiveVal.get(i).getValue();
            int chosenSecondIndividualIndex;

            IndividualWithDstToItsCentre chosenFirstIndividual;
            IndividualWithDstToItsCentre chosenSecondIndividual;

            if(chosenFirstIndividualIndex >= chosenClusterSize) {
                chosenFirstIndividualIndex = chosenFirstIndividualIndex - chosenClusterSize;
                chosenFirstIndividual =
                        (IndividualWithDstToItsCentre)chosenClusterNeighbour.getCluster()
                                .get(chosenFirstIndividualIndex);
                chosenClusteringNeighbourCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();
                chosenFirstIndividual.getIndividual().recordUsage();
            } else {
                chosenFirstIndividual =
                        (IndividualWithDstToItsCentre)chosenCluster.getCluster()
                                .get(chosenFirstIndividualIndex);
                chosenClusteringCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();
                chosenFirstIndividual.getIndividual().recordUsage();
            }

            int populationRandomIndividualIndex = parameters.random.nextInt(population.size());
            chosenSecondIndividual = new IndividualWithDstToItsCentre(-666.0, population.get(populationRandomIndividualIndex));

            returnPairs.add(new Pair<>(chosenFirstIndividual.getIndividual(), chosenSecondIndividual.getIndividual()));
        }


        return returnPairs;
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
