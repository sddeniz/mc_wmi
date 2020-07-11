package com.behsa.sdp.mc_wmi.dto;

import java.io.Serializable;

public class TreeInfoDto implements Serializable {

    private long treeId;
    private String inputs;

    public TreeInfoDto(long treeId, String inputs) {
        this.treeId = treeId;
        this.inputs = inputs;
    }

    public long getTreeId() {
        return treeId;
    }

    public void setTreeId(long treeId) {
        this.treeId = treeId;
    }

    public String getInputs() {
        return inputs;
    }

    public void setInputs(String inputs) {
        this.inputs = inputs;
    }

    @Override
    public String toString() {
        return "TreeInfoDto{" +
                "treeId=" + treeId +
                ", inputs=" + inputs +
                '}';
    }
}
