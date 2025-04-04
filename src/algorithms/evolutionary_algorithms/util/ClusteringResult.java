package algorithms.evolutionary_algorithms.util;

import data.ClustersAndTheirStatistics;
import data.DataStatistics;
import util.FILE_OUTPUT_LEVEL;

import java.util.List;

public class ClusteringResult {
    private final String clusteringResultFilePath;
    private final String clusteringResultFileName;
    private final int maxTravellingTimeClusterId;
    private final int minTravellingTimeClusterId;
    private final FILE_OUTPUT_LEVEL saveResultFiles;
    private final DataStatistics dataStats;
    ClustersAndTheirStatistics clustersAndTheirStatistics;
    List<Double> clustersDispersion;
    List<Double> clusterWeights;
    List<IndividualCluster> clustersWithIndDstToCentre;

    public String getClusteringResultFilePath() {
        return clusteringResultFilePath;
    }

    public String getClusteringResultFileName() { return clusteringResultFileName; }

    public ClusteringResult(ClustersAndTheirStatistics clustersAndTheirStatistics, List<Double> clustersDispersion,
                            List<Double> clusterWeights,
                            List<IndividualCluster> clustersWithIndDstToCentre, String clusteringResultFilePath,
                            String clusteringResultFileName, int minTravellingTimeClusterId, int maxTravellingTimeClusterId,
                            FILE_OUTPUT_LEVEL saveResultFiles, DataStatistics dataStats) {
        this.clustersAndTheirStatistics = clustersAndTheirStatistics;
        this.clustersDispersion = clustersDispersion;
        this.clusterWeights = clusterWeights;
        this.clustersWithIndDstToCentre = clustersWithIndDstToCentre;
        this.clusteringResultFilePath = clusteringResultFilePath;
        this.clusteringResultFileName = clusteringResultFileName;
        this.minTravellingTimeClusterId = minTravellingTimeClusterId;
        this.maxTravellingTimeClusterId = maxTravellingTimeClusterId;
        this.saveResultFiles = saveResultFiles;
        this.dataStats = dataStats;
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

    public DataStatistics getDataStats() {
        return dataStats;
    }

    public void toFile(){
        if(saveResultFiles.getLevel() > 1) {
            this.clustersAndTheirStatistics.toFile(this.clusteringResultFilePath, this.clusteringResultFileName,
                    this.minTravellingTimeClusterId, this.maxTravellingTimeClusterId, this.clustersDispersion,
                    this.clusterWeights);
        }
    }
}
