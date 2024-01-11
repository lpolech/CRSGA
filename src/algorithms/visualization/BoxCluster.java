package algorithms.visualization;

import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BoxCluster<PROBLEM extends BaseProblemRepresentation> {

    public List<Pair<Double, List<BaseIndividual<Integer, PROBLEM>>>> clustering(List<BaseIndividual<Integer,PROBLEM>> population, int clusterSize) {
        List<Pair<Double, List<BaseIndividual<Integer, PROBLEM>>>> clustersResult = new ArrayList();
        for (int i = 0; i < population.size(); i += clusterSize) {
            var maxCrowdingDistance = 0.0;
            var subList = new ArrayList<>(population.subList(i, Math.min(i + clusterSize, population.size())));
            for (int j = 0; j < subList.size(); j++) {
                var individual = subList.get(j);
                var individualDistance = individual.getDistance();
                if(individualDistance > maxCrowdingDistance) {
                    maxCrowdingDistance = individualDistance;
                }
            }
            clustersResult.add(new Pair<>(maxCrowdingDistance, subList));
        }

        Collections.sort(clustersResult, Comparator.comparing(p -> -p.getKey()));
        return clustersResult;
    }


}
