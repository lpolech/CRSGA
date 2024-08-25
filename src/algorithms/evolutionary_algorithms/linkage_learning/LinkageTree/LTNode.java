package algorithms.evolutionary_algorithms.linkage_learning.LinkageTree;

import java.util.Iterator;
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

    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("Node;" + id + ";no of masks;" + geneIndexes.size() + ";");// + "parent;");

//        if(parent != null) {
//            output.append(parent.id + ";no od masks;" + parent.getGeneIndexes().size() + ";");
//        } else {
//            output.append("no parent;");
//        }
//
//        output.append("children;");
//        if(children != null && children.size() > 0) {
//            for(LTNode child: children) {
//                output.append("child;" + child.getId() + ";no of masks;" + child.getGeneIndexes().size() + ";");
//            }
//        } else {
//            output.append("no children;");
//        }

        output.append("mask;");
        for(Integer mask: geneIndexes) {
            output.append(mask + ";");
        }

        return output.toString();
    }

    public void print(StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append(toString());
        buffer.append('\n');
        if(children != null && !children.isEmpty()) {
        for (Iterator<LTNode> it = children.iterator(); it.hasNext();) {
            LTNode next = it.next();
            if (it.hasNext()) {
                next.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
            } else {
                next.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
            }
        }
        }
    }
}
