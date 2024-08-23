package algorithms.evolutionary_algorithms.linkage_learning.LinkageTree;

import java.util.List;

public class LTNode {
    private List<Integer> geneIndexes;
    private LTNode parent;
    private List<LTNode> children;
    private long id;
    private static long idCounter = 0;

    public LTNode(List<Integer> geneIndexes, LTNode parent, List<LTNode> children) {
        this.geneIndexes = geneIndexes;
        this.parent = parent;
        this.children = children;
        idCounter++;
        this.id = idCounter;
    }

    public List<Integer> getGeneIndexes() {
        return geneIndexes;
    }

    public void setGeneIndexes(List<Integer> geneIndexes) {
        this.geneIndexes = geneIndexes;
    }

    public LTNode getParent() {
        return parent;
    }

    public void setParent(LTNode parent) {
        this.parent = parent;
    }

    public List<LTNode> getChildren() {
        return children;
    }

    public void setChildren(List<LTNode> children) {
        this.children = children;
    }

    public long getId() {
        return id;
    }

    public static long getIdCounter() {
        return idCounter;
    }
}
