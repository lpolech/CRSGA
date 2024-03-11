package algorithms.evolutionary_algorithms.util;

import data.ClustersAndTheirStatistics;

import java.util.List;

public class ClusteringResult {
    private final String clusteringResultFilePath;
    private final String clusteringResultFileName;
    private final int maxTravellingTimeClusterId;
    private final int minTravellingTimeClusterId;
    ClustersAndTheirStatistics clustersAndTheirStatistics;
    List<Double> clustersDispersion;
    List<Double> clusterWeights;
    List<IndividualCluster> clustersWithIndDstToCentre;

    public ClusteringResult(ClustersAndTheirStatistics clustersAndTheirStatistics, List<Double> clustersDispersion,
                            List<Double> clusterWeights,
                            List<IndividualCluster> clustersWithIndDstToCentre, String clusteringResultFilePath,
                            String clusteringResultFileName, int minTravellingTimeClusterId, int maxTravellingTimeClusterId) {
        this.clustersAndTheirStatistics = clustersAndTheirStatistics;
        this.clustersDispersion = clustersDispersion;
        this.clusterWeights = clusterWeights;
        this.clustersWithIndDstToCentre = clustersWithIndDstToCentre;
        this.clusteringResultFilePath = clusteringResultFilePath;
        this.clusteringResultFileName = clusteringResultFileName;
        this.minTravellingTimeClusterId = minTravellingTimeClusterId;
        this.maxTravellingTimeClusterId = maxTravellingTimeClusterId;
    }

    public ClustersAndTheirStatistics getClustersAndTheirStatistics() {
        return clustersAndTheirStatistics;
    }

    public void setClustersAndTheirStatistics(ClustersAndTheirStatistics clustersAndTheirStatistics) {
        this.clustersAndTheirStatistics = clustersAndTheirStatistics;
    }

    public List<Double> getClustersDispersion() {
        return clustersDispersion;
    }

    public List<Double> getClusterWeights() {
        return clusterWeights;
    }

    public void setClustersDispersion(List<Double> clustersDispersion) {
        this.clustersDispersion = clustersDispersion;
    }

    public List<IndividualCluster> getClustersWithIndDstToCentre() {
        return clustersWithIndDstToCentre;
    }

    public void setClustersWithIndDstToCentre(List<IndividualCluster> clustersWithIndDstToCentre) {
        this.clustersWithIndDstToCentre = clustersWithIndDstToCentre;
    }

    public void toFile(){
        this.clustersAndTheirStatistics.toFile(this.clusteringResultFilePath, this.clusteringResultFileName,
                this.minTravellingTimeClusterId, this.maxTravellingTimeClusterId);
    }
}
