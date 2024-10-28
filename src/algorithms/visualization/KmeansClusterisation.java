package algorithms.visualization;

import algorithms.Kmeans;
import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.util.ClusteringResult;
import algorithms.evolutionary_algorithms.util.IndividualCluster;
import algorithms.evolutionary_algorithms.util.IndividualWithDstToItsCentre;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;
import algorithms.problem.TTP;
import center.method.Centroid;
import data.*;
import distance.measures.L2Norm;
import interfaces.QualityMeasure;
import util.FILE_OUTPUT_LEVEL;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class KmeansClusterisation<PROBLEM extends BaseProblemRepresentation> {


    private HashMap<String, Double> qualityMeasureValues;

    private boolean disableCostEdgePromotion;

    private boolean disableTravelEdgePromotion;
    public KmeansClusterisation(boolean disableCostEdgePromotion, boolean disableTravelEdgePromotion) {
        this.disableCostEdgePromotion = disableCostEdgePromotion;
        this.disableTravelEdgePromotion = disableTravelEdgePromotion;
    }

    public ClusteringResult clustering(
            ClusteringResult gaClusteringResults,
            QualityMeasure clusterWeightMeasure,
            List<BaseIndividual<Integer,PROBLEM>> archive,
            int clusterSize,
            int clusterIterLimit,
            double edgeClustersWeightMultiplier,
            int generationNum,
            ParameterSet<Integer, TTP> parameters,
            int indExclusionUsageLimit,
            int indExclusionGenDuration,
            List<BaseIndividual<Integer, PROBLEM>> excludedPopulation,
            FILE_OUTPUT_LEVEL saveResultFiles,
            List<BaseIndividual<Integer, PROBLEM>> population,
            boolean isClusterinRun,
            boolean isRecalculateCentres,
            boolean isPopulationUsed,
            String clusteringResultFilePath) {

        Parameters.setNumberOfClusterisationAlgIterations(clusterIterLimit);
        Parameters.setClassAttribute(false);
        Parameters.setInstanceName(true);
        var measure = new L2Norm();
        Kmeans.setMeasure(measure);
        Centroid centreMethod = new Centroid();
        Kmeans.setCenterMethod(centreMethod);
        Cluster.setAlgorithm(new Kmeans());
        List<BaseIndividual<Integer, PROBLEM>> dataToConsider = null;
        if(isClusterinRun) {
            dataToConsider = archive;
        } else {
            dataToConsider = new ArrayList<>(archive);
            dataToConsider.addAll(population);
        }
        int dataLength = dataToConsider.size();
        var populationMapping = new HashMap<String, BaseIndividual<Integer, PROBLEM>>(dataLength);
        DataPoint[] dataToCluster = new DataPoint[dataLength];
        for (int i = 0; i < dataLength; i++) {
            var ind = dataToConsider.get(i);
            int indUsage = ind.getAdjustedUsageCounter();
            var individualName = ((i < archive.size())? "ParetoFront_": "Population_") + i;
            dataToCluster[i] = new DataPoint(ind.getObjectives(), ind.getObjectives(), individualName, null);
            dataToCluster[i].setGlobalUsageCounter(indUsage);
            populationMapping.put(individualName, ind);
        }

        HashMap<Integer, String> dimensionNumberAndItsName = new HashMap<>();
        dimensionNumberAndItsName.put(0, "TravellingTime");
        dimensionNumberAndItsName.put(1, "KnapsackProfit");

        ClustersAndTheirStatistics clustering = null;
        DataStatistics dataStats = null;
        Data data = null;
        if(isClusterinRun || gaClusteringResults == null) {
            dataStats = DataReader.calculateDataStatistics(dataToCluster, archive.get(0).getObjectives().length, null);
            data = new Data(dataToCluster, dataToCluster.length, archive.get(0).getObjectives().length, dataStats, dimensionNumberAndItsName);
            Cluster dataCluster = centreMethod.makeCluster(data, measure);
            int dynamicClusterSize = Integer.max(1, (int) (archive.size() / (double) clusterSize));
            clustering = dataCluster.performSplit(dynamicClusterSize, -1);
            clustering = updateArchiveAndExcludedIndividualsBasedOnClusters(clustering, populationMapping, indExclusionUsageLimit, indExclusionGenDuration, archive, excludedPopulation);
        } else {
            dataStats = gaClusteringResults.getDataStats(); // we'll use the initial normalised coordinates, but it should be ok as most of the points should be covered by the pareto front area
            data = new Data(dataToCluster, dataToCluster.length, dataToConsider.get(0).getObjectives().length, dataStats, dimensionNumberAndItsName);
            Cluster dataCluster = centreMethod.makeCluster(data, measure);
            clustering = dataCluster.assignPointsToClustersAndUpdateCentres(gaClusteringResults.getClustersAndTheirStatistics().getClusters(), isRecalculateCentres);
        }

        clustering.calculateInternalMeasures(clusterWeightMeasure, parameters);

        int minTravellingTimeClusterNumber = -1;
        double minTravellingTimeVal = Double.MAX_VALUE;
        int minTravellingTimeClusterId = -1;

        int minProfitClusterNumber = -1;
        double minProfitVal = Double.MAX_VALUE;
        int maxTravellingTimeClusterId = -1;

        double maxClusteringDispersion = -1.0;
        List<Double> clustersDispersion = new ArrayList<>(clustering.getClusters().length);

        double extremeClusterWeight;
        if(!clusterWeightMeasure.shouldMeasureBeMaximised()) { // we want worse clustering to get better weight
            extremeClusterWeight = (-1)*Double.MAX_VALUE;
        } else {
            extremeClusterWeight = Double.MAX_VALUE;
        }
        List<Double> clusterWeights = new ArrayList<>(clustering.getClusters().length);

        List<IndividualCluster> individualClusters = new ArrayList<>(clustering.getClusters().length);
        for(int i = 0; i < clustering.getClusters().length; i++) {
            var cluster = clustering.getClusters()[i];
            double travellingTime = Arrays.stream(cluster.getPoints())
                    .mapToDouble(pts -> pts.getCoordinate(0))  // Map each person to their age
                    .min()                     // Get the minimum value
                    .orElseThrow(() -> new IllegalArgumentException("Array is empty"));
            double profit = Arrays.stream(cluster.getPoints())
                    .mapToDouble(pts -> pts.getCoordinate(1))  // Map each person to their age
                    .min()                     // Get the minimum value
                    .orElseThrow(() -> new IllegalArgumentException("Array is empty"));

            if(travellingTime < minTravellingTimeVal) {
                minTravellingTimeVal = travellingTime;
                minTravellingTimeClusterNumber = i;
                minTravellingTimeClusterId = cluster.getClusterId();
            }

            if(profit < minProfitVal) {
                minProfitVal = profit;
                minProfitClusterNumber = i;
                maxTravellingTimeClusterId = cluster.getClusterId();
            }

            var clusterDispersion = clustering.getClustersAvgVariances()[i];
            clustersDispersion.add(clusterDispersion);
            maxClusteringDispersion = Math.max(maxClusteringDispersion, clusterDispersion);

            var clusterWeight = clustering.getClustersWeights()[i];
            clusterWeights.add(clusterWeight);
            if(!clusterWeightMeasure.shouldMeasureBeMaximised()) {
                extremeClusterWeight = Math.max(extremeClusterWeight, clusterWeight);
            } else {
                extremeClusterWeight = Math.min(extremeClusterWeight, clusterWeight);
            }

            List<IndividualWithDstToItsCentre> individualCluster = new ArrayList<>(cluster.getNumberOfPoints());
            for(var point: cluster.getPoints()) {
                var ind = populationMapping.get(point.getInstanceName());
                double indDistToTheCentre = measure.distance(cluster, point);
                individualCluster.add(new IndividualWithDstToItsCentre(indDistToTheCentre, ind));
            }
            individualClusters.add(new IndividualCluster(individualCluster, cluster.getClusterId()));
        }

        double weightsMultiplier = (!clusterWeightMeasure.shouldMeasureBeMaximised()? edgeClustersWeightMultiplier: 1/edgeClustersWeightMultiplier); // we want worse clustering to get better weight
        if(!disableCostEdgePromotion) {
            clustersDispersion.set(minProfitClusterNumber, maxClusteringDispersion * edgeClustersWeightMultiplier);
            clusterWeights.set(minProfitClusterNumber, extremeClusterWeight * weightsMultiplier);
//            clusterWeights.set(minProfitClusterNumber, clusterWeights.get(minProfitClusterNumber) * weightsMultiplier);
        }

        if(!disableTravelEdgePromotion) {
            clustersDispersion.set(minTravellingTimeClusterNumber, maxClusteringDispersion * edgeClustersWeightMultiplier);
            clusterWeights.set(minTravellingTimeClusterNumber, extremeClusterWeight * weightsMultiplier);
//            clusterWeights.set(minTravellingTimeClusterNumber, clusterWeights.get(minTravellingTimeClusterNumber) * weightsMultiplier);
        }

        String clusteringResultFileName = "clusteringRes_" + generationNum + ".csv";
        return new ClusteringResult(clustering, clustersDispersion, clusterWeights, individualClusters,
                clusteringResultFilePath, clusteringResultFileName, minTravellingTimeClusterId, maxTravellingTimeClusterId,
                saveResultFiles, dataStats);
    }

    private ClustersAndTheirStatistics updateArchiveAndExcludedIndividualsBasedOnClusters(ClustersAndTheirStatistics clustering,
                                                                    HashMap<String, BaseIndividual<Integer, PROBLEM>> populationMapping,
                                                                    int indExclusionUsageLimit,
                                                                    int indExclusionGenDuration,
                                                                    List<BaseIndividual<Integer, PROBLEM>> population,
                                                                    List<BaseIndividual<Integer, PROBLEM>> excludedPopulation) {

        // Step 1: Filter clusters whose avg usageCounter exceeds the indExclusionUsageLimit
        ArrayList<Integer> clusterIndicesToExclude = new ArrayList<>();
        for(int i = 0; i < clustering.getClusters().length; i++) {
            Cluster cls = clustering.getClusters()[i];
            double avgUnsucUsageCnt = 0.0;
            for(DataPoint point: cls.getPoints()) {
                var ind = populationMapping.get(point.getInstanceName());
                avgUnsucUsageCnt += ind.getAdjusterUnsuccessfulUsageCounter();
            }
            avgUnsucUsageCnt /= (double) cls.getNumberOfPoints();
            if(avgUnsucUsageCnt > indExclusionUsageLimit) {
                clusterIndicesToExclude.add(i);
            }
        }

        if(clusterIndicesToExclude.size() >= clustering.getClusters().length) {
            System.err.println("Method decided to exclude ALL clusters as they exceed the exclusion thresholds. Consider tuning the exclusion parameters!");
        } else {
            ArrayList<Cluster> newClusters = new ArrayList<>();
            for (int i = 0; i < clustering.getClusters().length; i++) {
                Cluster cls = clustering.getClusters()[i];
                if (!clusterIndicesToExclude.contains(i)) {
                    newClusters.add(cls); // Keep this cluster
                } else {
                    // Remove individuals in the excluded clusters from the population
                    for (DataPoint point : cls.getPoints()) {
                        BaseIndividual<Integer, PROBLEM> individual = populationMapping.get(point.getInstanceName());
                        individual.excludeFromArchive(indExclusionGenDuration);
                        population.remove(individual);
                        excludedPopulation.add(individual);
                    }
                }
            }
            Cluster[] newClusterClusters = newClusters.toArray(new Cluster[0]);
            return new ClustersAndTheirStatistics(newClusterClusters, Kmeans.getMeasure().calculateClusterisationStatistic(newClusterClusters), true);
        }
        return clustering;
    }
}
