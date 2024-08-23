package algorithms.evolutionary_algorithms.linkage_learning;

import algorithms.evolutionary_algorithms.linkage_learning.LinkageTree.LTNode;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LTHierarchicalClustering {
    public static Pair<LTNode, List<LTNode>> buildTree(LTDistanceMeasureMatrix distanceMatrix) {
        List<LTNode> leaves = new ArrayList<>();

        List<LTNode> nodesToCombine = new ArrayList<>();
        for(int index: distanceMatrix.getGeneIndexes()) {
            LTNode nodeToAdd = new LTNode(new ArrayList<>(List.of(index)), null, null);
            nodesToCombine.add(nodeToAdd);
            leaves.add(nodeToAdd);
        }

        Map<Pair<Long, Long>, Double> nodesIdWithDistance = addLeaveDistances(nodesToCombine, distanceMatrix);

        while(nodesToCombine.size() > 1) {

        }

        return new Pair<>(nodesToCombine.getFirst(), leaves);
    }

    private static Map<Pair<Long, Long>, Double> addLeaveDistances(List<LTNode> leaves, LTDistanceMeasureMatrix distanceMatrix) {
        Map<Pair<Long, Long>, Double> leavesDistances = new HashMap<>();
        for(LTNode l1: leaves) {
            for(LTNode l2: leaves) {
                if(l1.getId() > l2.getId()) { // Symmetrical measure
                    if(l1.getGeneIndexes().size() > 1 || l2.getGeneIndexes().size() > 1) {
                        System.err.println("LTHierarchicalClustering.addLeaveDistances I got internal nodes! Sizes - l1: " + l1.getGeneIndexes().size() + " l2: " + l2.getGeneIndexes().size());
                    } else {
                        int minIndex = Math.min(l1.getGeneIndexes().getFirst(), l2.getGeneIndexes().getFirst());
                        int maxIndex = Math.max(l1.getGeneIndexes().getFirst(), l2.getGeneIndexes().getFirst());
                        double distance = distanceMatrix.getMatrixElement(maxIndex, minIndex);
                        leavesDistances.put(new Pair<>(l1.getId(), l2.getId()), distance);
                    }
                }
            }
        }

        return leavesDistances;
    }
}
