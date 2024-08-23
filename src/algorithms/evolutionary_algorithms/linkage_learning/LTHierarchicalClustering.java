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
            double minDst = Double.MAX_VALUE;
            Pair<LTNode, LTNode> minDstNodes = null;
            for(var firstNode: nodesToCombine) {
                long firstNodeId = firstNode.getId();
                for(var secondNode: nodesToCombine) {
                    long secondNodeId = secondNode.getId();
                    double nodesDistance = -666.0;
                    if(nodesIdWithDistance.containsKey(new Pair<>(Math.max(firstNodeId, secondNodeId), Math.min(firstNodeId, secondNodeId)))) { // TODO: check if such pair creation is good for looking in hash map
                        nodesDistance = nodesIdWithDistance.get(new Pair<>(Math.max(firstNodeId, secondNodeId), Math.min(firstNodeId, secondNodeId)));
                    } else {
                        nodesDistance = LTDistance.getDistance(firstNode, secondNode, distanceMatrix);
                        nodesIdWithDistance.put(new Pair<>(Math.max(firstNodeId, secondNodeId), Math.min(firstNodeId, secondNodeId)), nodesDistance);
                    }

                    if(nodesDistance < minDst) {
                        minDst = nodesDistance;
                        minDstNodes = new Pair<>(firstNode, secondNode);
                    }
                }
            }

            if(minDstNodes == null) {
                System.err.println("LTHierarchicalClustering.buildTree no nodes with minimal distance?! Empty tree?");
            }

            LTNode mergedNodes = merge(minDstNodes.getKey(), minDstNodes.getValue());
            nodesToCombine.add(mergedNodes);

            // remove distances relating to now combined nodes
            long firstNodeId = minDstNodes.getKey().getId();
            long secondNodeId = minDstNodes.getValue().getId();

            for(var nodesDst: nodesIdWithDistance.entrySet()) {
                long firstMeasuredId = nodesDst.getKey().getKey();
                long secondMeasuredId = nodesDst.getKey().getValue();

                if(firstNodeId == firstMeasuredId
                    || firstNodeId == secondMeasuredId
                    || secondNodeId == firstMeasuredId
                    || secondNodeId == secondMeasuredId) {
                    nodesIdWithDistance.remove(nodesDst.getKey());
                }
            }

            nodesToCombine.remove(minDstNodes.getKey());
            nodesToCombine.remove(minDstNodes.getValue());
        }

        return new Pair<>(nodesToCombine.getFirst(), leaves); // TODO: check if leaves are updated after the tree building alg finished
    }

    private static LTNode merge(LTNode firstNode, LTNode secondNode) {
        List<Integer> newIndices = new ArrayList<>(firstNode.getGeneIndexes());
        newIndices.addAll(secondNode.getGeneIndexes());

        List<LTNode> children = new ArrayList<>();
        children.add(firstNode);
        children.add(secondNode);

        LTNode newNode = new LTNode(newIndices, null, children);

        firstNode.setParent(newNode);
        secondNode.setParent(newNode);

        return newNode;
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
