package algorithms.evolutionary_algorithms.selection;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.util.ClusteringResult;
import algorithms.evolutionary_algorithms.util.IndividualWithDstToItsCentre;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;
import interfaces.QualityMeasure;
import javafx.util.Pair;
import util.ParameterFunctions;
import util.config.IndividualPairingMethodConfig;
import util.config.SimulatedAnnealingConfig;
import util.config.TournamentSelectionConfig;

import java.util.*;
import java.util.stream.Collectors;

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
        for (var ind : clusteringResult.getClustersDispersion()) {
            dispersionSum += ind;
        }
        double clusterSelectionRandom = parameters.random.nextDouble() * dispersionSum;
        dispersionSum = 0.0;
//        Pair<Cluster, List<Pair<Double, BaseIndividual<Integer, PROBLEM>>>> chosenCluster = null;
        int chosenClusterIndex = -1;
        for (int i = 0; i < clusteringResult.getClustersWithIndDstToCentre().size() && chosenClusterIndex == -1; i++) {
//            Pair<Double, List<Pair<Double, BaseIndividual<Integer, PROBLEM>>>> cluster = dispersionWithIndDstToCentreAndTheInd.getValue().get(i);
            dispersionSum += clusteringResult.getClustersDispersion().get(i);
            if (dispersionSum >= clusterSelectionRandom) {
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
        var chosenIndividual = (IndividualWithDstToItsCentre) chosenCluster.getCluster().get(chosenIndividualIndex);
        chosenClusteringCluster.getPoints()[chosenIndividualIndex].recordUsage();

        return chosenIndividual.getIndividual();
    }

    /* Same cluster, dynamic tournament cluster selection based on clustering measures */
    public Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>> selectClsMeasureRandomIndSameCls(
            ClusteringResult clusteringResult,
            ParameterSet<GENE, BaseProblemRepresentation> parameters,
            QualityMeasure clusterWeightMeasure) {
        int numberOfClusters = clusteringResult.getClustersDispersion().size();
        int dynamicTurSize = Math.max(1, (int) ((this.tournamentSize * numberOfClusters) / 100.0)); // tur size depents on the number of clusters as at the beginning there is not many clusters
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
        while (chosenFirstIndividualIndex == chosenSecondIndividualIndex && chosenCluster.getCluster().size() > 1) {
            chosenSecondIndividualIndex = parameters.random.nextInt(chosenCluster.getCluster().size());
        }

        var chosenFirstIndividual = (IndividualWithDstToItsCentre) chosenCluster.getCluster().get(chosenFirstIndividualIndex);
        chosenClusteringCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();
        chosenFirstIndividual.getIndividual().setAdjustedUsageCounter(
                chosenClusteringCluster.getPoints()[chosenFirstIndividualIndex].getGlobalUsageCounter());

        var chosenSecondIndividual = (IndividualWithDstToItsCentre) chosenCluster.getCluster().get(chosenSecondIndividualIndex);
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
            ParameterFunctions turDecayFunction,
            int currCost,
            IndividualsPairingMethod pairingMethod) {
        List<Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>> returnPairs = new ArrayList<>();

        switch (pairingMethod) {
            case ALL_POSSIBLE_PAIRS:
                returnPairs.addAll(addArchiveAllPossiblePairs(clusteringResult, parameters, clusterWeightMeasure, turDecayFunction, currCost));
                break;
            case CROSS_CLUSTER_ALL_POSSIBLE_PAIRS:
                returnPairs.addAll(addArchiveCrossClustersAllPossiblePairs(clusteringResult, parameters, clusterWeightMeasure, turDecayFunction, currCost));
                break;
            case DISTANT_IMMEDIATE_NEIGHBOUR_PAIR:
                returnPairs.addAll(addArchiveNeigbouringPairs(clusteringResult, parameters, clusterWeightMeasure, turDecayFunction, currCost));
                break;
            case DISTANT_IMMEDIATE_NEIGHBOUR_PAIR_PERCENT:
                // Check the config value to control whether to run this case
                if (IndividualPairingMethodConfig.ENABLE_DISTANT_NEIGHBOUR_PAIR_PERCENT) {
                    // Enable logic for DISTANT_IMMEDIATE_NEIGHBOUR_PAIR_PERCENT
                    returnPairs.addAll(addArchiveNeigbouringPairsPercent(clusteringResult, parameters, clusterWeightMeasure, turDecayFunction, currCost));
                }
                break;
            case DISTANT_IMMEDIATE_NEIGHBOUR_PAIR_SIMPLIFIED:
                returnPairs.addAll(addArchiveNeigbouringPairsSimplified(clusteringResult, parameters, clusterWeightMeasure, turDecayFunction, currCost));
                break;
            default:
                System.err.println("UNKNOWN POINT PAIRING METHOD! PLEASE CHECK!");
        }
        // Without Replacement
        if (!TournamentSelectionConfig.ALLOW_DUPLICATE_SELECTION) {
            Set<Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>> withoutPlacement = new HashSet<>(returnPairs);
            returnPairs = new ArrayList<>(withoutPlacement);
        }
//        returnPairs.addAll(addPairsOfArchiveAndPopulation(clusteringResult, parameters, clusterWeightMeasure, population));

        return returnPairs;
    }

    public List<Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>> addArchiveCrossClustersAllPossiblePairs(
            ClusteringResult clusteringResult,
            ParameterSet<GENE, BaseProblemRepresentation> parameters,
            QualityMeasure clusterWeightMeasure,
            ParameterFunctions turDecayFunction,
            int currCost) {
        List<Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>> returnPairs = new ArrayList<>();
        int numberOfClusters = clusteringResult.getClustersDispersion().size();

        int dynamicTurSize = -666;
        if (turDecayFunction != null) {
            double decayTurFun = turDecayFunction.getVal(currCost);
            dynamicTurSize = Math.max(1, (int) Math.round(((decayTurFun * numberOfClusters) / 100.0))); // tur size depends on the number of clusters as at the beginning there is not many clusters
        } else {
            dynamicTurSize = Math.max(1, (int) ((this.tournamentSize * numberOfClusters) / 100.0));
        }
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
        if (!getClusterNeighbourIndeces(clusteringResult, chosenClusterIndex).isEmpty()) {
            chosenClusterNeighbourIndex = getClusterNeighbourIndeces(clusteringResult, chosenClusterIndex).get(0);
        }
        var chosenClusterNeighbour = clusteringResult.getClustersWithIndDstToCentre().get(chosenClusterNeighbourIndex);
        var chosenClusteringNeighbourCluster = clusteringResult.getClustersAndTheirStatistics().getClusters()[chosenClusterNeighbourIndex];
        chosenClusteringNeighbourCluster.getCenter().recordUsage();

        int chosenClusterSize = chosenClusteringCluster.getNumberOfPoints();
        int chosenClusterNeighbourSize = chosenClusteringNeighbourCluster.getNumberOfPoints();

        for (int i = 0; i < chosenClusterSize; i++) {
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

    private static List<Integer> getClusterNeighbourIndeces(ClusteringResult clusteringResult, int chosenClusterIndex) {
        return (List<Integer>) clusteringResult.getClustersAndTheirStatistics().getClusterChosenNeighbourIndicies().get(chosenClusterIndex);
    }

    public List<Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>> addArchiveAllPossiblePairs(
            ClusteringResult clusteringResult,
            ParameterSet<GENE, BaseProblemRepresentation> parameters,
            QualityMeasure clusterWeightMeasure,
            ParameterFunctions turDecayFunction,
            int currCost) {
        List<Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>> returnPairs = new ArrayList<>();
        int numberOfClusters = clusteringResult.getClustersDispersion().size();
        int dynamicTurSize = -666;
        if (turDecayFunction != null) {
            double decayTurFun = turDecayFunction.getVal(currCost);
            dynamicTurSize = Math.max(1, (int) Math.round(((decayTurFun * numberOfClusters) / 100.0))); // tur size depends on the number of clusters as at the beginning there is not many clusters
        } else {
            dynamicTurSize = Math.max(1, (int) ((this.tournamentSize * numberOfClusters) / 100.0));
        }
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
        if (!getClusterNeighbourIndeces(clusteringResult, chosenClusterIndex).isEmpty()) {
            chosenClusterNeighbourIndex = getClusterNeighbourIndeces(clusteringResult, chosenClusterIndex).get(0);
        }
        var chosenClusterNeighbour = clusteringResult.getClustersWithIndDstToCentre().get(chosenClusterNeighbourIndex);
        var chosenClusteringNeighbourCluster = clusteringResult.getClustersAndTheirStatistics().getClusters()[chosenClusterNeighbourIndex];
        chosenClusteringNeighbourCluster.getCenter().recordUsage();

        int chosenClusterSize = chosenClusteringCluster.getNumberOfPoints();
        int chosenClusterNeighbourSize = chosenClusteringNeighbourCluster.getNumberOfPoints();

        for (int i = 0; i < chosenClusterSize + chosenClusterNeighbourSize; i++) {
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
                if (i == j) {
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

    public List<Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>> addArchiveNeigbouringPairsSimplified(
            ClusteringResult clusteringResult,
            ParameterSet<GENE, BaseProblemRepresentation> parameters,
            QualityMeasure clusterWeightMeasure,
            ParameterFunctions turDecayFunction,
            int currCost) {
        List<Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>> returnPairs = new ArrayList<>();
        int numberOfClusters = clusteringResult.getClustersDispersion().size();

        int dynamicTurSize;
        if (SimulatedAnnealingConfig.SIMULATED_ANNEALING) {
            // Simulated Annealing
            double initialTemperature = 100.0;
            double temperatureDecayRate = 0.995;
            double temperature = initialTemperature * Math.pow(temperatureDecayRate, currCost);

            if (currCost % 100 == 0 && returnPairs.size() == 0) {
                temperature = initialTemperature;
            }

            if (turDecayFunction != null) {
                dynamicTurSize = Math.max(3, (int) Math.round(((turDecayFunction.getVal(currCost) * numberOfClusters) / 100.0) * temperature));
            } else {
                dynamicTurSize = Math.max(3, (int) ((this.tournamentSize * numberOfClusters) / 100.0 * temperature));
            }

        } else {
            if (turDecayFunction != null) {
                double decayTurFun = turDecayFunction.getVal(currCost);
                dynamicTurSize = Math.max(1, (int) Math.round(((decayTurFun * numberOfClusters) / 100.0)));
            } else {
                dynamicTurSize = Math.max(1, (int) ((this.tournamentSize * numberOfClusters) / 100.0));
            }
        }

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
        if (!getClusterNeighbourIndeces(clusteringResult, chosenClusterIndex).isEmpty()) {
            chosenClusterNeighbourIndex = getClusterNeighbourIndeces(clusteringResult, chosenClusterIndex).get(0);
        }
        var chosenClusterNeighbour = clusteringResult.getClustersWithIndDstToCentre().get(chosenClusterNeighbourIndex);
        var chosenClusteringNeighbourCluster = clusteringResult.getClustersAndTheirStatistics().getClusters()[chosenClusterNeighbourIndex];
        chosenClusteringNeighbourCluster.getCenter().recordUsage();

        int chosenClusterSize = chosenClusteringCluster.getNumberOfPoints();
        int chosenClusterNeighbourSize = chosenClusteringNeighbourCluster.getNumberOfPoints();
        // TODO: euclidean value instead of chosing one objective
        List<Pair<Integer, Double>> pointsIndexWithOneObjectiveVal = new ArrayList<>(chosenClusterSize + chosenClusterNeighbourSize);
        int mainObjectiveNumber = parameters.random.nextInt(parameters.evaluator.getNumObjectives());

        for (int i = 0; i < chosenClusterSize; i++) {
            pointsIndexWithOneObjectiveVal.add(new Pair<>(i, chosenClusteringCluster.getPoints()[i].getCoordinate(mainObjectiveNumber)));
        }

        for (int i = chosenClusterSize; i < chosenClusterSize + chosenClusterNeighbourSize; i++) {
            pointsIndexWithOneObjectiveVal.add(new Pair<>(i, chosenClusteringNeighbourCluster.getPoints()[i - chosenClusterSize].getCoordinate(mainObjectiveNumber)));
        }

        pointsIndexWithOneObjectiveVal.sort(new Comparator<Pair<Integer, Double>>() {
            public int compare(Pair<Integer, Double> o1, Pair<Integer, Double> o2) {
                if (Objects.equals(o1.getValue(), o2.getValue()))
                    return 0;
                return o1.getValue() < o2.getValue() ? -1 : 1;
            }
        });

        boolean crossClusterPairCreated = false;
        for (int i = 0; i < pointsIndexWithOneObjectiveVal.size(); i = i + 1) {
            int chosenFirstIndividualIndex = pointsIndexWithOneObjectiveVal.get(i).getKey();
            int chosenSecondIndividualIndex;

            if (i == (pointsIndexWithOneObjectiveVal.size() - 1)) { // last point
                chosenSecondIndividualIndex = pointsIndexWithOneObjectiveVal.get(i - 1).getKey();
            } else {
                chosenSecondIndividualIndex = pointsIndexWithOneObjectiveVal.get(i + 1).getKey();
                if ((chosenFirstIndividualIndex < chosenClusterSize && chosenSecondIndividualIndex >= chosenClusterSize)
                        || (chosenFirstIndividualIndex >= chosenClusterSize && chosenSecondIndividualIndex < chosenClusterSize)) { // cross cluster pair
                    crossClusterPairCreated = true;
                }
                i++;
            }

            IndividualWithDstToItsCentre chosenFirstIndividual;
            IndividualWithDstToItsCentre chosenSecondIndividual;

            if (chosenFirstIndividualIndex >= chosenClusterSize) {
                chosenFirstIndividualIndex = chosenFirstIndividualIndex - chosenClusterSize;
                chosenFirstIndividual =
                        (IndividualWithDstToItsCentre) chosenClusterNeighbour.getCluster()
                                .get(chosenFirstIndividualIndex);
                chosenClusteringNeighbourCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();
                chosenFirstIndividual.getIndividual().recordUsage();
            } else {
                chosenFirstIndividual =
                        (IndividualWithDstToItsCentre) chosenCluster.getCluster()
                                .get(chosenFirstIndividualIndex);
                chosenClusteringCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();
                chosenFirstIndividual.getIndividual().recordUsage();
            }

            if (chosenSecondIndividualIndex >= chosenClusterSize) {
                chosenSecondIndividualIndex = chosenSecondIndividualIndex - chosenClusterSize;
                chosenSecondIndividual =
                        (IndividualWithDstToItsCentre) chosenClusterNeighbour.getCluster()
                                .get(chosenSecondIndividualIndex);
                chosenClusteringNeighbourCluster.getPoints()[chosenSecondIndividualIndex].recordUsage();
                chosenSecondIndividual.getIndividual().recordUsage();
            } else {
                chosenSecondIndividual =
                        (IndividualWithDstToItsCentre) chosenCluster.getCluster()
                                .get(chosenSecondIndividualIndex);
                chosenClusteringCluster.getPoints()[chosenSecondIndividualIndex].recordUsage();
                chosenSecondIndividual.getIndividual().recordUsage();
            }

            returnPairs.add(new Pair<>(chosenFirstIndividual.getIndividual(), chosenSecondIndividual.getIndividual()));
        }

        IndividualWithDstToItsCentre chosenFirstIndividual;
        IndividualWithDstToItsCentre chosenSecondIndividual;
        for (int i = 0; i < pointsIndexWithOneObjectiveVal.size() && !crossClusterPairCreated && pointsIndexWithOneObjectiveVal.size() >= i + 1; i++) {
            int chosenFirstIndividualIndex = pointsIndexWithOneObjectiveVal.get(i).getKey();
            int chosenSecondIndividualIndex = pointsIndexWithOneObjectiveVal.get(i + 1).getKey();
            if (chosenFirstIndividualIndex < chosenClusterSize && chosenSecondIndividualIndex >= chosenClusterSize) { // cross cluster pair
                chosenFirstIndividual =
                        (IndividualWithDstToItsCentre) chosenCluster.getCluster()
                                .get(chosenFirstIndividualIndex);
                chosenClusteringCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();
                chosenFirstIndividual.getIndividual().recordUsage();

                chosenSecondIndividualIndex = chosenSecondIndividualIndex - chosenClusterSize;
                chosenSecondIndividual =
                        (IndividualWithDstToItsCentre) chosenClusterNeighbour.getCluster()
                                .get(chosenSecondIndividualIndex);
                chosenClusteringNeighbourCluster.getPoints()[chosenSecondIndividualIndex].recordUsage();
                chosenSecondIndividual.getIndividual().recordUsage();

                returnPairs.add(new Pair<>(chosenFirstIndividual.getIndividual(), chosenSecondIndividual.getIndividual()));
                crossClusterPairCreated = true;
            } else if (chosenFirstIndividualIndex >= chosenClusterSize && chosenSecondIndividualIndex < chosenClusterSize) { // cross cluster pair
                chosenFirstIndividualIndex = chosenFirstIndividualIndex - chosenClusterSize;
                chosenFirstIndividual =
                        (IndividualWithDstToItsCentre) chosenClusterNeighbour.getCluster()
                                .get(chosenFirstIndividualIndex);
                chosenClusteringNeighbourCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();
                chosenFirstIndividual.getIndividual().recordUsage();

                chosenSecondIndividual =
                        (IndividualWithDstToItsCentre) chosenCluster.getCluster()
                                .get(chosenSecondIndividualIndex);
                chosenClusteringCluster.getPoints()[chosenSecondIndividualIndex].recordUsage();
                chosenSecondIndividual.getIndividual().recordUsage();

                returnPairs.add(new Pair<>(chosenFirstIndividual.getIndividual(), chosenSecondIndividual.getIndividual()));
                crossClusterPairCreated = true;
            }
        }

        return returnPairs;
    }

    //CHEN
    public List<Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>> addArchiveNeigbouringPairsPercent(
            ClusteringResult clusteringResult,
            ParameterSet<GENE, BaseProblemRepresentation> parameters,
            QualityMeasure clusterWeightMeasure,
            ParameterFunctions turDecayFunction,
            int currCost) {
        List<Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>> returnPairs = new ArrayList<>();
        int numberOfClusters = clusteringResult.getClustersDispersion().size();

        int dynamicTurSize = -666;
        if (turDecayFunction != null) {
            double decayTurFun = turDecayFunction.getVal(currCost);
            dynamicTurSize = Math.max(1, (int) Math.round(((decayTurFun * numberOfClusters) / 100.0))); // tur size depends on the number of clusters as at the beginning there is not many clusters
        } else {
            dynamicTurSize = Math.max(1, (int) ((this.tournamentSize * numberOfClusters) / 100.0));
        }
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
        if (!getClusterNeighbourIndeces(clusteringResult, chosenClusterIndex).isEmpty()) {
            chosenClusterNeighbourIndex = getClusterNeighbourIndeces(clusteringResult, chosenClusterIndex).get(0);
        }
        var chosenClusterNeighbour = clusteringResult.getClustersWithIndDstToCentre().get(chosenClusterNeighbourIndex);
        var chosenClusteringNeighbourCluster = clusteringResult.getClustersAndTheirStatistics().getClusters()[chosenClusterNeighbourIndex];
        chosenClusteringNeighbourCluster.getCenter().recordUsage();

        int chosenClusterSize = chosenClusteringCluster.getNumberOfPoints();
        int chosenClusterNeighbourSize = chosenClusteringNeighbourCluster.getNumberOfPoints();
        // TODO: euclidean value instead of chosing one objective
        List<Pair<Integer, Double>> pointsIndexWithOneObjectiveVal = new ArrayList<>(chosenClusterSize + chosenClusterNeighbourSize);
        int mainObjectiveNumber = parameters.random.nextInt(parameters.evaluator.getNumObjectives());

        for (int i = 0; i < chosenClusterSize; i++) {
            pointsIndexWithOneObjectiveVal.add(new Pair<>(i, chosenClusteringCluster.getPoints()[i].getCoordinate(mainObjectiveNumber)));
        }

        for (int i = chosenClusterSize; i < chosenClusterSize + chosenClusterNeighbourSize; i++) {
            pointsIndexWithOneObjectiveVal.add(new Pair<>(i, chosenClusteringNeighbourCluster.getPoints()[i - chosenClusterSize].getCoordinate(mainObjectiveNumber)));
        }

        pointsIndexWithOneObjectiveVal.sort(new Comparator<Pair<Integer, Double>>() {
            public int compare(Pair<Integer, Double> o1, Pair<Integer, Double> o2) {
                if (Objects.equals(o1.getValue(), o2.getValue()))
                    return 0;
                return o1.getValue() < o2.getValue() ? -1 : 1;
            }
        });

        List<Object[]> allPairs = new ArrayList<>();
        for (int i = 0; i < pointsIndexWithOneObjectiveVal.size(); i++) {
            int chosenFirstIndividualIndex = pointsIndexWithOneObjectiveVal.get(i).getKey();
            double chosenFirstIndividualFitness = pointsIndexWithOneObjectiveVal.get(i).getValue();
            int chosenSecondIndividualIndex;

            if (i == (pointsIndexWithOneObjectiveVal.size() - 1)) { // last point
                chosenSecondIndividualIndex = pointsIndexWithOneObjectiveVal.get(i - 1).getKey();
                // Store both distances (distToLeft and distToRight) as you don't need to compare them
                double distToLeft = Math.abs(chosenFirstIndividualFitness - pointsIndexWithOneObjectiveVal.get(i - 1).getValue());
                allPairs.add(new Object[]{chosenFirstIndividualIndex, chosenSecondIndividualIndex, distToLeft});
            } else if (i == 0) { // first point
                chosenSecondIndividualIndex = pointsIndexWithOneObjectiveVal.get(i + 1).getKey();
                // Store both distances (distToLeft and distToRight) as you don't need to compare them
                double distToRight = Math.abs(chosenFirstIndividualFitness - pointsIndexWithOneObjectiveVal.get(i + 1).getValue());
                allPairs.add(new Object[]{chosenFirstIndividualIndex, chosenSecondIndividualIndex, distToRight});
            } else {
                int leftNeighbourIndex = pointsIndexWithOneObjectiveVal.get(i - 1).getKey();
                double leftNeighbourFitness = pointsIndexWithOneObjectiveVal.get(i - 1).getValue();
                int rightNeighbourIndex = pointsIndexWithOneObjectiveVal.get(i + 1).getKey();
                double rightNeighbourFitness = pointsIndexWithOneObjectiveVal.get(i + 1).getValue();

                // Calculate both distances without comparing them
                double distToLeft = Math.abs(chosenFirstIndividualFitness - leftNeighbourFitness);
                double distToRight = Math.abs(rightNeighbourFitness - chosenFirstIndividualFitness);

                // Add both distances to the list
                allPairs.add(new Object[]{chosenFirstIndividualIndex, leftNeighbourIndex, distToLeft});
                allPairs.add(new Object[]{chosenFirstIndividualIndex, rightNeighbourIndex, distToRight});
            }
        }


        allPairs.sort(Comparator.comparingDouble(o -> (double) o[2]));


        int topK = (int) (allPairs.size() * 0.9);
        List<Object[]> selectedPairs = allPairs.subList(0, topK);



        for (Object[] pair : selectedPairs) {
            int chosenFirstIndividualIndex = (int) pair[0];
            int chosenSecondIndividualIndex = (int) pair[1];

            IndividualWithDstToItsCentre chosenFirstIndividual;
            IndividualWithDstToItsCentre chosenSecondIndividual;

            if (chosenFirstIndividualIndex >= chosenClusterSize) {
                chosenFirstIndividualIndex -= chosenClusterSize;
                chosenFirstIndividual = (IndividualWithDstToItsCentre) chosenClusterNeighbour.getCluster().get(chosenFirstIndividualIndex);
                chosenClusteringNeighbourCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();
            } else {
                chosenFirstIndividual = (IndividualWithDstToItsCentre) chosenCluster.getCluster().get(chosenFirstIndividualIndex);
                chosenClusteringCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();
            }
            chosenFirstIndividual.getIndividual().recordUsage();

            if (chosenSecondIndividualIndex >= chosenClusterSize) {
                chosenSecondIndividualIndex -= chosenClusterSize;
                chosenSecondIndividual = (IndividualWithDstToItsCentre) chosenClusterNeighbour.getCluster().get(chosenSecondIndividualIndex);
                chosenClusteringNeighbourCluster.getPoints()[chosenSecondIndividualIndex].recordUsage();
            } else {
                chosenSecondIndividual = (IndividualWithDstToItsCentre) chosenCluster.getCluster().get(chosenSecondIndividualIndex);
                chosenClusteringCluster.getPoints()[chosenSecondIndividualIndex].recordUsage();
            }
            chosenSecondIndividual.getIndividual().recordUsage();

            returnPairs.add(new Pair<>(chosenFirstIndividual.getIndividual(), chosenSecondIndividual.getIndividual()));
        }

        return returnPairs;
//        boolean crossClusterPairCreated = false;
//
//        for (Object[] pair : selectedPairs) {
//            int chosenFirstIndividualIndex = (int) pair[0];
//            int chosenSecondIndividualIndex = (int) pair[1];
//
//            if ((chosenFirstIndividualIndex < chosenClusterSize && chosenSecondIndividualIndex >= chosenClusterSize)
//                    || (chosenFirstIndividualIndex >= chosenClusterSize && chosenSecondIndividualIndex < chosenClusterSize)) {
//                crossClusterPairCreated = true;
//            }
//
//            IndividualWithDstToItsCentre chosenFirstIndividual;
//            IndividualWithDstToItsCentre chosenSecondIndividual;
//
//            if (chosenFirstIndividualIndex >= chosenClusterSize) {
//                chosenFirstIndividualIndex = chosenFirstIndividualIndex - chosenClusterSize;
//                chosenFirstIndividual =
//                        (IndividualWithDstToItsCentre) chosenClusterNeighbour.getCluster()
//                                .get(chosenFirstIndividualIndex);
//                chosenClusteringNeighbourCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();
//                chosenFirstIndividual.getIndividual().recordUsage();
//            } else {
//                chosenFirstIndividual =
//                        (IndividualWithDstToItsCentre) chosenCluster.getCluster()
//                                .get(chosenFirstIndividualIndex);
//                chosenClusteringCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();
//                chosenFirstIndividual.getIndividual().recordUsage();
//            }
//
//            if (chosenSecondIndividualIndex >= chosenClusterSize) {
//                chosenSecondIndividualIndex = chosenSecondIndividualIndex - chosenClusterSize;
//                chosenSecondIndividual =
//                        (IndividualWithDstToItsCentre) chosenClusterNeighbour.getCluster()
//                                .get(chosenSecondIndividualIndex);
//                chosenClusteringNeighbourCluster.getPoints()[chosenSecondIndividualIndex].recordUsage();
//                chosenSecondIndividual.getIndividual().recordUsage();
//            } else {
//                chosenSecondIndividual =
//                        (IndividualWithDstToItsCentre) chosenCluster.getCluster()
//                                .get(chosenSecondIndividualIndex);
//                chosenClusteringCluster.getPoints()[chosenSecondIndividualIndex].recordUsage();
//                chosenSecondIndividual.getIndividual().recordUsage();
//            }
//
//            returnPairs.add(new Pair<>(chosenFirstIndividual.getIndividual(), chosenSecondIndividual.getIndividual()));
//        }
//
//        IndividualWithDstToItsCentre chosenFirstIndividual;
//        IndividualWithDstToItsCentre chosenSecondIndividual;
//        for (int i = 0; i < pointsIndexWithOneObjectiveVal.size() && !crossClusterPairCreated && pointsIndexWithOneObjectiveVal.size() >= i + 1; i++) {
//            int chosenFirstIndividualIndex = pointsIndexWithOneObjectiveVal.get(i).getKey();
//            int chosenSecondIndividualIndex = pointsIndexWithOneObjectiveVal.get(i + 1).getKey();
//            if (chosenFirstIndividualIndex < chosenClusterSize && chosenSecondIndividualIndex >= chosenClusterSize) { // cross cluster pair
//                chosenFirstIndividual =
//                        (IndividualWithDstToItsCentre) chosenCluster.getCluster()
//                                .get(chosenFirstIndividualIndex);
//                chosenClusteringCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();
//                chosenFirstIndividual.getIndividual().recordUsage();
//
//                chosenSecondIndividualIndex = chosenSecondIndividualIndex - chosenClusterSize;
//                chosenSecondIndividual =
//                        (IndividualWithDstToItsCentre) chosenClusterNeighbour.getCluster()
//                                .get(chosenSecondIndividualIndex);
//                chosenClusteringNeighbourCluster.getPoints()[chosenSecondIndividualIndex].recordUsage();
//                chosenSecondIndividual.getIndividual().recordUsage();
//
//                returnPairs.add(new Pair<>(chosenFirstIndividual.getIndividual(), chosenSecondIndividual.getIndividual()));
//                crossClusterPairCreated = true;
//            } else if (chosenFirstIndividualIndex >= chosenClusterSize && chosenSecondIndividualIndex < chosenClusterSize) { // cross cluster pair
//                chosenFirstIndividualIndex = chosenFirstIndividualIndex - chosenClusterSize;
//                chosenFirstIndividual =
//                        (IndividualWithDstToItsCentre) chosenClusterNeighbour.getCluster()
//                                .get(chosenFirstIndividualIndex);
//                chosenClusteringNeighbourCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();
//                chosenFirstIndividual.getIndividual().recordUsage();
//
//                chosenSecondIndividual =
//                        (IndividualWithDstToItsCentre) chosenCluster.getCluster()
//                                .get(chosenSecondIndividualIndex);
//                chosenClusteringCluster.getPoints()[chosenSecondIndividualIndex].recordUsage();
//                chosenSecondIndividual.getIndividual().recordUsage();
//
//                returnPairs.add(new Pair<>(chosenFirstIndividual.getIndividual(), chosenSecondIndividual.getIndividual()));
//                crossClusterPairCreated = true;
//            }
//        }
//
//        return returnPairs;
    }


    public List<Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>> addArchiveNeigbouringPairs(
            ClusteringResult clusteringResult,
            ParameterSet<GENE, BaseProblemRepresentation> parameters,
            QualityMeasure clusterWeightMeasure,
            ParameterFunctions turDecayFunction,
            int currCost) {
        List<Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>> returnPairs = new ArrayList<>();
        int numberOfClusters = clusteringResult.getClustersDispersion().size();

        int dynamicTurSize = -666;
        if (turDecayFunction != null) {
            double decayTurFun = turDecayFunction.getVal(currCost);
            dynamicTurSize = Math.max(1, (int) Math.round(((decayTurFun * numberOfClusters) / 100.0))); // tur size depends on the number of clusters as at the beginning there is not many clusters
        } else {
            dynamicTurSize = Math.max(1, (int) ((this.tournamentSize * numberOfClusters) / 100.0));
        }
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
        if (!getClusterNeighbourIndeces(clusteringResult, chosenClusterIndex).isEmpty()) {
            chosenClusterNeighbourIndex = getClusterNeighbourIndeces(clusteringResult, chosenClusterIndex).get(0);
        }
        var chosenClusterNeighbour = clusteringResult.getClustersWithIndDstToCentre().get(chosenClusterNeighbourIndex);
        var chosenClusteringNeighbourCluster = clusteringResult.getClustersAndTheirStatistics().getClusters()[chosenClusterNeighbourIndex];
        chosenClusteringNeighbourCluster.getCenter().recordUsage();

        int chosenClusterSize = chosenClusteringCluster.getNumberOfPoints();
        int chosenClusterNeighbourSize = chosenClusteringNeighbourCluster.getNumberOfPoints();

        List<Pair<Integer, Double>> pointsIndexWithOneObjectiveVal = new ArrayList<>(chosenClusterSize + chosenClusterNeighbourSize);
        int mainObjectiveNumber = parameters.random.nextInt(parameters.evaluator.getNumObjectives());

        for (int i = 0; i < chosenClusterSize; i++) {
            pointsIndexWithOneObjectiveVal.add(new Pair<>(i, chosenClusteringCluster.getPoints()[i].getCoordinate(mainObjectiveNumber)));
        }

        for (int i = chosenClusterSize; i < chosenClusterSize + chosenClusterNeighbourSize; i++) {
            pointsIndexWithOneObjectiveVal.add(new Pair<>(i, chosenClusteringNeighbourCluster.getPoints()[i - chosenClusterSize].getCoordinate(mainObjectiveNumber)));
        }

        pointsIndexWithOneObjectiveVal.sort(new Comparator<Pair<Integer, Double>>() {
            public int compare(Pair<Integer, Double> o1, Pair<Integer, Double> o2) {
                if (Objects.equals(o1.getValue(), o2.getValue()))
                    return 0;
                return o1.getValue() < o2.getValue() ? -1 : 1;
            }
        });

        for (int i = 0; i < pointsIndexWithOneObjectiveVal.size(); i = i + 1) {
            int chosenFirstIndividualIndex = pointsIndexWithOneObjectiveVal.get(i).getKey();
            double chosenFirstIndividualFitness = pointsIndexWithOneObjectiveVal.get(i).getValue();
            int chosenSecondIndividualIndex;

            if (i == (pointsIndexWithOneObjectiveVal.size() - 1)) { // last point
                chosenSecondIndividualIndex = pointsIndexWithOneObjectiveVal.get(i - 1).getKey();
            } else if (i == 0) { //first point
                chosenSecondIndividualIndex = pointsIndexWithOneObjectiveVal.get(i + 1).getKey();
            } else {
                int leftNeighbourIndex = pointsIndexWithOneObjectiveVal.get(i - 1).getKey();
                double leftNeighbourFitness = pointsIndexWithOneObjectiveVal.get(i - 1).getValue();
                int rightNeighbourIndex = pointsIndexWithOneObjectiveVal.get(i + 1).getKey();
                double rightNeighbourFitness = pointsIndexWithOneObjectiveVal.get(i + 1).getValue();

                double distToLeft = chosenFirstIndividualFitness - leftNeighbourFitness;
                double distToRight = rightNeighbourFitness - chosenFirstIndividualFitness;
                if (distToLeft < distToRight) {
                    chosenSecondIndividualIndex = rightNeighbourIndex;
                } else {
                    chosenSecondIndividualIndex = leftNeighbourIndex;
                }
            }
            IndividualWithDstToItsCentre chosenFirstIndividual;
            IndividualWithDstToItsCentre chosenSecondIndividual;

            if (chosenFirstIndividualIndex >= chosenClusterSize) {
                chosenFirstIndividualIndex = chosenFirstIndividualIndex - chosenClusterSize;
                chosenFirstIndividual =
                        (IndividualWithDstToItsCentre) chosenClusterNeighbour.getCluster()
                                .get(chosenFirstIndividualIndex);
                chosenClusteringNeighbourCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();
                chosenFirstIndividual.getIndividual().recordUsage();
            } else {
                chosenFirstIndividual =
                        (IndividualWithDstToItsCentre) chosenCluster.getCluster()
                                .get(chosenFirstIndividualIndex);
                chosenClusteringCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();
                chosenFirstIndividual.getIndividual().recordUsage();
            }

            if (chosenSecondIndividualIndex >= chosenClusterSize) {
                chosenSecondIndividualIndex = chosenSecondIndividualIndex - chosenClusterSize;
                chosenSecondIndividual =
                        (IndividualWithDstToItsCentre) chosenClusterNeighbour.getCluster()
                                .get(chosenSecondIndividualIndex);
                chosenClusteringNeighbourCluster.getPoints()[chosenSecondIndividualIndex].recordUsage();
                chosenSecondIndividual.getIndividual().recordUsage();
            } else {
                chosenSecondIndividual =
                        (IndividualWithDstToItsCentre) chosenCluster.getCluster()
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
        int dynamicTurSize = Math.max(1, (int) ((this.tournamentSize * numberOfClusters) / 100.0)); // tur size depends on the number of clusters as at the beginning there is not many clusters
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
        if (!getClusterNeighbourIndeces(clusteringResult, chosenClusterIndex).isEmpty()) {
            chosenClusterNeighbourIndex = getClusterNeighbourIndeces(clusteringResult, chosenClusterIndex).get(0);
        }
        var chosenClusterNeighbour = clusteringResult.getClustersWithIndDstToCentre().get(chosenClusterNeighbourIndex);
        var chosenClusteringNeighbourCluster = clusteringResult.getClustersAndTheirStatistics().getClusters()[chosenClusterNeighbourIndex];
        chosenClusteringNeighbourCluster.getCenter().recordUsage();

        int chosenClusterSize = chosenClusteringCluster.getNumberOfPoints();
        int chosenClusterNeighbourSize = chosenClusteringNeighbourCluster.getNumberOfPoints();

        List<Pair<Integer, Double>> pointsIndexWithOneObjectiveVal = new ArrayList<>(chosenClusterSize + chosenClusterNeighbourSize);
        int mainObjectiveNumber = parameters.random.nextInt(parameters.evaluator.getNumObjectives());

        for (int i = 0; i < chosenClusterSize; i++) {
            pointsIndexWithOneObjectiveVal.add(new Pair<>(i, chosenClusteringCluster.getPoints()[i].getCoordinate(mainObjectiveNumber)));
        }

        for (int i = chosenClusterSize; i < chosenClusterSize + chosenClusterNeighbourSize; i++) {
            pointsIndexWithOneObjectiveVal.add(new Pair<>(i, chosenClusteringNeighbourCluster.getPoints()[i - chosenClusterSize].getCoordinate(mainObjectiveNumber)));
        }

        pointsIndexWithOneObjectiveVal.sort(new Comparator<Pair<Integer, Double>>() {
            public int compare(Pair<Integer, Double> o1, Pair<Integer, Double> o2) {
                if (Objects.equals(o1.getValue(), o2.getValue()))
                    return 0;
                return o1.getValue() < o2.getValue() ? -1 : 1;
            }
        });

        for (int i = 0; i < pointsIndexWithOneObjectiveVal.size(); i = i + 1) {
            int chosenFirstIndividualIndex = pointsIndexWithOneObjectiveVal.get(i).getKey();
            double chosenFirstIndividualFitness = pointsIndexWithOneObjectiveVal.get(i).getValue();
            int chosenSecondIndividualIndex;

            IndividualWithDstToItsCentre chosenFirstIndividual;
            IndividualWithDstToItsCentre chosenSecondIndividual;

            if (chosenFirstIndividualIndex >= chosenClusterSize) {
                chosenFirstIndividualIndex = chosenFirstIndividualIndex - chosenClusterSize;
                chosenFirstIndividual =
                        (IndividualWithDstToItsCentre) chosenClusterNeighbour.getCluster()
                                .get(chosenFirstIndividualIndex);
                chosenClusteringNeighbourCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();
                chosenFirstIndividual.getIndividual().recordUsage();
            } else {
                chosenFirstIndividual =
                        (IndividualWithDstToItsCentre) chosenCluster.getCluster()
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
        while (chosenFirstIndividualIndex == chosenSecondIndividualIndex && chosenCluster.getCluster().size() > 1) {
            chosenSecondIndividualIndex = parameters.random.nextInt(chosenCluster.getCluster().size());
        }

        var chosenFirstIndividual = (IndividualWithDstToItsCentre) chosenCluster.getCluster().get(chosenFirstIndividualIndex);
        chosenClusteringCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();

        var chosenSecondIndividual = (IndividualWithDstToItsCentre) chosenCluster.getCluster().get(chosenSecondIndividualIndex);
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
        for (var ind : clusteringResult.getClustersDispersion()) {
            dispersionSum += ind;
        }
        double clusterSelectionRandom = parameters.random.nextDouble() * dispersionSum;
        dispersionSum = 0.0;
        int chosenClusterIndex = -1;
        for (int i = 0; i < clusteringResult.getClustersWithIndDstToCentre().size() && chosenClusterIndex == -1; i++) {
            dispersionSum += clusteringResult.getClustersDispersion().get(i);
            if (dispersionSum >= clusterSelectionRandom) {
                chosenClusterIndex = i;
            }
        }

        var chosenCluster = clusteringResult.getClustersWithIndDstToCentre().get(chosenClusterIndex);
        var chosenClusteringCluster = clusteringResult.getClustersAndTheirStatistics().getClusters()[chosenClusterIndex];
        chosenClusteringCluster.getCenter().recordUsage();
        var chosenFirstIndividualIndex = parameters.random.nextInt(chosenCluster.getCluster().size());
        var chosenSecondIndividualIndex = chosenFirstIndividualIndex;
        while (chosenFirstIndividualIndex == chosenSecondIndividualIndex && chosenCluster.getCluster().size() > 1) {
            chosenSecondIndividualIndex = parameters.random.nextInt(chosenCluster.getCluster().size());
        }

        var chosenFirstIndividual = (IndividualWithDstToItsCentre) chosenCluster.getCluster().get(chosenFirstIndividualIndex);
        chosenClusteringCluster.getPoints()[chosenFirstIndividualIndex].recordUsage();

        var chosenSecondIndividual = (IndividualWithDstToItsCentre) chosenCluster.getCluster().get(chosenSecondIndividualIndex);
        chosenClusteringCluster.getPoints()[chosenSecondIndividualIndex].recordUsage();

        return new Pair<>(chosenFirstIndividual.getIndividual(), chosenSecondIndividual.getIndividual());
    }
}
