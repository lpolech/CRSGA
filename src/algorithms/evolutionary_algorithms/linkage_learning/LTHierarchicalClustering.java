package algorithms.evolutionary_algorithms.linkage_learning;

import algorithms.evolutionary_algorithms.linkage_learning.LinkageTree.LTNode;
import javafx.util.Pair;

import java.util.*;

public class LTHierarchicalClustering {
    public static Pair<LTNode, List<LTNode>> buildTree(LTDistanceMeasureMatrix distanceMatrix) {
        List<LTNode> leaves = new ArrayList<>();
        LTDistance distance = new LTDistance();

        List<LTNode> nodesToCombine = new ArrayList<>();
        List<Integer> geneIndices = distanceMatrix.getGeneIndices();
        for(int i = 0; i < geneIndices.size(); i++) {
            Integer val = geneIndices.get(i);
            LTNode nodeToAdd = new LTNode(new ArrayList<>(List.of(val)), null, null);
            nodesToCombine.add(nodeToAdd);
            leaves.add(nodeToAdd);
        }

        Map<Pair<Long, Long>, Double> nodesIdWithDistance = addLeaveDistances(nodesToCombine, distanceMatrix);

        while(nodesToCombine.size() > 1) {
            double minDst = Double.MAX_VALUE;
            Pair<LTNode, LTNode> minDstNodes = null;
            for(int i = 0; i < nodesToCombine.size(); i++) {
                LTNode firstNode = nodesToCombine.get(i);
                long firstNodeId = firstNode.getId();
                for(int j = i + 1; j < nodesToCombine.size(); j++) {
                    LTNode secondNode = nodesToCombine.get(j);
                    long secondNodeId = secondNode.getId();

                    double nodesDistance = -123.0;
                    if (nodesIdWithDistance.containsKey(new Pair<>(Math.max(firstNodeId, secondNodeId), Math.min(firstNodeId, secondNodeId)))) { // TODO: check if such pair creation is good for looking in hash map
                        nodesDistance = nodesIdWithDistance.get(new Pair<>(Math.max(firstNodeId, secondNodeId), Math.min(firstNodeId, secondNodeId)));
                    } else {
                        nodesDistance = distance.getDistance(firstNode, secondNode, distanceMatrix);
                        nodesIdWithDistance.put(new Pair<>(Math.max(firstNodeId, secondNodeId), Math.min(firstNodeId, secondNodeId)), nodesDistance);
                    }

                    if (nodesDistance < minDst) {
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

            for(Iterator<Map.Entry<Pair<Long, Long>, Double>> it = nodesIdWithDistance.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Pair<Long, Long>, Double> nodesDst = it.next();
                long firstMeasuredId = nodesDst.getKey().getKey();
                long secondMeasuredId = nodesDst.getKey().getValue();

                if(firstNodeId == firstMeasuredId
                    || firstNodeId == secondMeasuredId
                    || secondNodeId == firstMeasuredId
                    || secondNodeId == secondMeasuredId) {
                    it.remove();
                }
            }

            nodesToCombine.remove(minDstNodes.getKey());
            nodesToCombine.remove(minDstNodes.getValue());
        }

        return new Pair<>(nodesToCombine.get(0), leaves); // TODO: check if leaves are updated after the tree building alg finished
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
                        int minIndex = Math.min(l1.getGeneIndexes().get(0), l2.getGeneIndexes().get(0));
                        int maxIndex = Math.max(l1.getGeneIndexes().get(0), l2.getGeneIndexes().get(0));
                        double distance = distanceMatrix.getMatrixElement(maxIndex, minIndex);
                        leavesDistances.put(new Pair<>(l1.getId(), l2.getId()), distance);
                    }
                }
            }
        }

        return leavesDistances;
    }
}
