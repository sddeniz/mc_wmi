package com.behsa.sdp.mcwmi.dto;

import java.io.Serializable;

public class TreeInfoDto implements Serializable {

    private long treeId;
    private String inputs;
    private String outputs;


    public  TreeInfoDto(long treeId, String inputs, String outputs) {
        this.treeId = treeId;
        this.inputs = inputs;
        this.outputs = outputs;
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

    public String getOutputs() {
        return outputs;
    }

    public void setOutputs(String outputs) {
        this.outputs = outputs;
    }

    @Override
    public String toString() {
        return "TreeInfoDto{" +
                "treeId=" + treeId +
                ", inputs='" + inputs + '\'' +
                ", outputs='" + outputs + '\'' +
                '}';
    }
}
