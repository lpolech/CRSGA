package algorithms.evolutionary_algorithms.linkage_learning.LinkageTree;

import java.util.List;

public class LTMask {
    private long clusterId;
    private List<Integer> mask;

    public LTMask(long clusterId, List<Integer> mask) {
        this.clusterId = clusterId;
        this.mask = mask;
    }

    public long getClusterId() {
        return clusterId;
    }

    public List<Integer> getMask() {
        return mask;
    }

    public int getMaskSize() {
        return mask.size();
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("Node ID;" + this.clusterId + ";no of masks;" + mask.size() + ";mask");
        for(Integer val: mask) {
            output.append(";" + val);
        }

        return output.toString();
    }
}
