package algorithms.evolutionary_algorithms.util;

import algorithms.problem.BaseProblemRepresentation;

import java.util.List;

public class IndividualCluster <PROBLEM extends BaseProblemRepresentation> {
    private List<IndividualWithDstToItsCentre> cluster;

    public IndividualCluster(List<IndividualWithDstToItsCentre> cluster) {
        this.cluster = cluster;
    }

    public List<IndividualWithDstToItsCentre> getCluster() {
        return cluster;
    }

    public void setCluster(List<IndividualWithDstToItsCentre> cluster) {
        this.cluster = cluster;
    }
}
