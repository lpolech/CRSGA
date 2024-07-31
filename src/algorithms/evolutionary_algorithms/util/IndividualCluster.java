package algorithms.evolutionary_algorithms.util;

import algorithms.problem.BaseProblemRepresentation;

import java.util.List;

public class IndividualCluster <PROBLEM extends BaseProblemRepresentation> {
    private List<IndividualWithDstToItsCentre> cluster;

    private int clusterId;

    public IndividualCluster(List<IndividualWithDstToItsCentre> cluster, int clusterId) {
        this.cluster = cluster;
        this.clusterId = clusterId;
    }

    public List<IndividualWithDstToItsCentre> getCluster() {
        return cluster;
    }

    public void setCluster(List<IndividualWithDstToItsCentre> cluster) {
        this.cluster = cluster;
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }
}
