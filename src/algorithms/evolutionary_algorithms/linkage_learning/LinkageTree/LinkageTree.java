package algorithms.evolutionary_algorithms.linkage_learning.LinkageTree;

import algorithms.evolutionary_algorithms.linkage_learning.LTDistanceMeasureMatrix;
import algorithms.evolutionary_algorithms.linkage_learning.LTHierarchicalClustering;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LinkageTree {
    private LTNode root;
    private List<LTNode> leaves;
    private LTDistanceMeasureMatrix distanceMatrix;

    public LinkageTree(LTDistanceMeasureMatrix distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
    }

    public void create() {
        Pair<LTNode, List<LTNode>> rootWithleaves= LTHierarchicalClustering.buildTree(distanceMatrix);
        this.root = rootWithleaves.getKey();
        this.leaves = rootWithleaves.getValue();
    }

}
