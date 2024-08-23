package algorithms.evolutionary_algorithms.linkage_learning.LinkageTree;

import algorithms.evolutionary_algorithms.linkage_learning.LTDistanceMeasureMatrix;
import algorithms.evolutionary_algorithms.linkage_learning.LTHierarchicalClustering;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LinkageTree {
    private LTNode root;
    private List<LTNode> leaves;
    private LTDistanceMeasureMatrix distanceMatrix;
    private List<LTMask> masks;

    public LinkageTree(LTDistanceMeasureMatrix distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
    }

    public void create() {
        Pair<LTNode, List<LTNode>> rootWithleaves= LTHierarchicalClustering.buildTree(distanceMatrix);
        this.root = rootWithleaves.getKey();
        this.leaves = rootWithleaves.getValue();

        this.masks = getMasksFromRoot(root);
    }

    private List<LTMask> getMasksFromRoot(LTNode root) {
        List<LTMask> masks = new ArrayList<>();
        for(var child: root.getChildren()) {
            masks.addAll(getMasks(child));
        }
        return masks;
    }

    private List<LTMask> getMasks(LTNode child) {
        if(child.getChildren().isEmpty()) {
            LTMask mask = new LTMask(child.getId(), child.getGeneIndexes());
            return new ArrayList<>(List.of(mask));
        } else {
            List<LTMask> masks = new ArrayList<>();
            for(var ch: root.getChildren()) {
                masks.addAll(getMasks(ch));
            }
            return masks;
        }
    }

    public List<LTMask> getShuffledMasks() {
        List<LTMask> copy = new ArrayList<>(masks);
        Collections.shuffle(copy);
        return copy;
    }

}
