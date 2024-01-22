package algorithms.visualization;

import algorithms.Kmeans;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;
import center.method.Centroid;
import data.*;
import distance.measures.L2Norm;
import javafx.util.Pair;
import utils.Utils;

import java.util.*;

public class KmeansClusterisation<PROBLEM extends BaseProblemRepresentation> {

    public List<Pair<Double, List<BaseIndividual<Integer, PROBLEM>>>> clustering(List<BaseIndividual<Integer,PROBLEM>> population, int clusterSize, int clusterIterLimit) {
        Parameters.setNumberOfClusterisationAlgIterations(clusterIterLimit);
        Parameters.setClassAttribute(false);
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
        DataStatistics dataStats = DataReader.calculateDataStatistics(dataToCluster, population.getFirst().getObjectives().length, null);
        Data data = new Data(dataToCluster, dataToCluster.length, population.getFirst().getObjectives().length, dataStats, dimensionNumberAndItsName);
        Cluster dataCluster = centreMethod.makeCluster(data, measure);
        ClustersAndTheirStatistics clustering = dataCluster.performSplit(clusterSize, -1);
        var clusteringDispersion = clustering.getClustersAvgVariances();

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

        List<Pair<Double, List<BaseIndividual<Integer, PROBLEM>>>> clustersWithDispersion = new ArrayList(dataLength);
        for(int i = 0; i < clustering.getClusters().length; i++) {
            var cluster = clustering.getClusters()[i];
            var clusterDispersion = clustering.getClustersAvgVariances()[i];
            List<BaseIndividual<Integer, PROBLEM>> clusterPoints = new ArrayList<>();

            for(var point: cluster.getPoints()) {
                var ind = populationMapping.get(point.getInstanceName());
                clusterPoints.add(ind);
            }
            clustersWithDispersion.add(new Pair<>(clusterDispersion, clusterPoints));
        }

        Collections.sort(clustersWithDispersion, Comparator.comparing(p -> -p.getKey()));
        return clustersWithDispersion;

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
