package algorithms.visualization;

import algorithms.Kmeans;
import algorithms.evolutionary_algorithms.util.ClusteringResult;
import algorithms.evolutionary_algorithms.util.IndividualCluster;
import algorithms.evolutionary_algorithms.util.IndividualWithDstToItsCentre;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;
import center.method.Centroid;
import data.*;
import distance.measures.L2Norm;
import javafx.util.Pair;
import utils.Utils;

import java.io.File;
import java.util.*;

public class KmeansClusterisation<PROBLEM extends BaseProblemRepresentation> {

    public KmeansClusterisation(boolean disableCostEdgePromotion, boolean disableTravelEdgePromotion) {
        this.disableCostEdgePromotion = disableCostEdgePromotion;
        this.disableTravelEdgePromotion = disableTravelEdgePromotion;
    }

    private boolean disableCostEdgePromotion;
    private boolean disableTravelEdgePromotion;
    public ClusteringResult clustering(
            List<BaseIndividual<Integer,PROBLEM>> population, int clusterSize,
            int clusterIterLimit, double edgeClustersDispersionValMultiplier, int generationNum) {
        Parameters.setNumberOfClusterisationAlgIterations(clusterIterLimit);
        Parameters.setClassAttribute(false);
        Parameters.setInstanceName(true);
        var measure = new L2Norm();
        Kmeans.setMeasure(measure);
        Centroid centreMethod = new Centroid();
        Kmeans.setCenterMethod(centreMethod);
        Cluster.setAlgorithm(new Kmeans());
        int dataLength = population.size();
        var populationMapping = new HashMap<String, BaseIndividual<Integer, PROBLEM>>(dataLength);
        DataPoint[] dataToCluster = new DataPoint[dataLength];
        for(int i = 0; i < population.size(); i++) {
            var ind = population.get(i);
            var individualName = "ParetoFront_" + i;
            dataToCluster[i] = new DataPoint(ind.getObjectives(), ind.getObjectives(), individualName, null);
            populationMapping.put(individualName, ind);
        }

        HashMap<Integer,String> dimensionNumberAndItsName = new HashMap<>();
        dimensionNumberAndItsName.put(0, "TravellingTime");
        dimensionNumberAndItsName.put(1, "KnapsackProfit");
        DataStatistics dataStats = DataReader.calculateDataStatistics(dataToCluster, population.get(0).getObjectives().length, null);
        Data data = new Data(dataToCluster, dataToCluster.length, population.get(0).getObjectives().length, dataStats, dimensionNumberAndItsName);
        Cluster dataCluster = centreMethod.makeCluster(data, measure);

        int dynamicClusterSize = Integer.max(1, (int)(population.size()/(double)clusterSize));
        ClustersAndTheirStatistics clustering = dataCluster.performSplit(dynamicClusterSize, -1);
//        clustering = clustering.addGapClusters(measure, centreMethod);

//        var dispersionMax = -Double.MIN_VALUE;
//        var dispersionMin = Double.MAX_VALUE;
//        for(int i = 0; i < clusteringDispersion.length; i++) {
//            var dispersion = clusteringDispersion[i];
//            dispersionMax = Double.max(dispersionMax, dispersion);
//            dispersionMin = Double.min(dispersionMin, dispersion);
//        }
//        var dispersionRange = dispersionMax - dispersionMin;
//        var scaledDispersion = new double[clusteringDispersion.length];
//        for(int i = 0; i < clusteringDispersion.length; i++) {
//            scaledDispersion[i] = (clusteringDispersion[i] - dispersionMin)/dispersionRange;
//        }

        int minTravellingTimeClusterNumber = -1;
        double minTravellingTimeVal = Double.MAX_VALUE;
        int minTravellingTimeClusterId = -1;

        int minProfitClusterNumber = -1;
        double minProfitVal = Double.MAX_VALUE;
        int maxTravellingTimeClusterId = -1;

        double maxClusteringDispersion = -1.0;

        List<Pair<Double, List<Pair<Double, BaseIndividual<Integer, PROBLEM>>>>> clustersWithDispersion = new ArrayList(dataLength);
        List<Double> clustersDispersion = new ArrayList<>(clustering.getClusters().length);
        List<IndividualCluster> individualClusters = new ArrayList<>(clustering.getClusters().length);
        for(int i = 0; i < clustering.getClusters().length; i++) {
            var cluster = clustering.getClusters()[i];
            double travellingTime = cluster.getCenter().getCoordinate(0);
            double profit = cluster.getCenter().getCoordinate(1);

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
            List<IndividualWithDstToItsCentre> individualCluster = new ArrayList<>(cluster.getNumberOfPoints());
            for(var point: cluster.getPoints()) {
                var ind = populationMapping.get(point.getInstanceName());
                double indDistToTheCentre = measure.distance(cluster, point);
                individualCluster.add(new IndividualWithDstToItsCentre(indDistToTheCentre, ind));
            }
            individualClusters.add(new IndividualCluster(individualCluster));
            clustersDispersion.add(clusterDispersion);
            maxClusteringDispersion = Math.max(maxClusteringDispersion, clusterDispersion);
        }

        if(!disableCostEdgePromotion) {
            clustersDispersion.set(minProfitClusterNumber, maxClusteringDispersion * edgeClustersDispersionValMultiplier);
        }

        if(!disableTravelEdgePromotion) {
            clustersDispersion.set(minTravellingTimeClusterNumber, maxClusteringDispersion * edgeClustersDispersionValMultiplier);
        }

        String clusteringResultFilePath = "clustering_res";
        String clusteringResultFileName = "clusteringRes_" + generationNum + ".csv";
        return new ClusteringResult(clustering, clustersDispersion, individualClusters, clusteringResultFilePath,
                clusteringResultFileName, minTravellingTimeClusterId, maxTravellingTimeClusterId);

//        List<Pair<Double, List<BaseIndividual<Integer, PROBLEM>>>> clustersResult = new ArrayList();
//        for (int j = 0; i < population.size(); i += clusterSize) {
//            var maxCrowdingDistance = 0.0;
//            var subList = new ArrayList<>(population.subList(i, Math.min(i + clusterSize, population.size())));
//            for (int ii = 0; j < subList.size(); j++) {
//                var individual = subList.get(j);
//                var individualDistance = individual.getDistance();
//                if(individualDistance > maxCrowdingDistance) {
//                    maxCrowdingDistance = individualDistance;
//                }
//            }
//            clustersResult.add(new Pair<>(maxCrowdingDistance, subList));
//        }
//
//        Collections.sort(clustersResult, Comparator.comparing(p -> -p.getKey()));
//        return clustersResult;
    }


}
