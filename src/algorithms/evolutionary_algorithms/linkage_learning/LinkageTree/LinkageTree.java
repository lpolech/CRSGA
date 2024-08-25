package algorithms.evolutionary_algorithms.linkage_learning.LinkageTree;

import algorithms.evolutionary_algorithms.linkage_learning.LTDistanceMeasureMatrix;
import algorithms.evolutionary_algorithms.linkage_learning.LTHierarchicalClustering;
import algorithms.evolutionary_algorithms.linkage_learning.MatrixUtils;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;
import algorithms.problem.TTP;
import javafx.util.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class LinkageTree<PROBLEM extends BaseProblemRepresentation>  {
    private LTNode root;
    private List<LTNode> leaves;
    private LTDistanceMeasureMatrix distanceMatrix;
    private List<LTMask> masks;

    public LinkageTree(List<BaseIndividual<Integer, PROBLEM>> population) {
        this.distanceMatrix = new LTDistanceMeasureMatrix();
        this.distanceMatrix.calculate(population);
        create();
    }

    private void create() {
        Pair<LTNode, List<LTNode>> rootWithLeaves= LTHierarchicalClustering.buildTree(distanceMatrix);
        this.root = rootWithLeaves.getKey();
        this.leaves = rootWithLeaves.getValue();

        this.masks = getAllMasksFromRoot();
    }

    public List<LTMask> getAllMasksFromRoot() {
        List<LTMask> masks = new ArrayList<>();
        for(var child: root.getChildren()) {
            masks.addAll(getMasks(child));
        }
        return masks;
    }

    private List<LTMask> getMasks(LTNode child) {
        List<LTMask> masks = new ArrayList<>();
        masks.add(new LTMask(child.getId(), child.getGeneIndexes()));

        if(child.getChildren() != null) {
            for(var ch: child.getChildren()) {
                masks.addAll(getMasks(ch));
            }
        }
        return masks;
    }

    public List<LTMask> getShuffledMasks() {
        List<LTMask> copy = new ArrayList<>(masks);
        Collections.shuffle(copy);
        return copy;
    }

    public List<LTMask> getMasksInLengthOrder() {
        List<LTMask> copy = new ArrayList<>(masks);
        copy.sort(Comparator.comparing(LTMask::getMaskSize));

        return copy;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        root.print(buffer, "", "");
        return buffer.toString();
    }

    public void toFile(String fileName, String path) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + File.separator + fileName));
            writer.write("LinkageTree");
            String ltString = toString();
//            System.out.println(ltString);
            writer.write(ltString);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
