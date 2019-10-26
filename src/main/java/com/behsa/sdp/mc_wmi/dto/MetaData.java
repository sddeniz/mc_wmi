package com.behsa.sdp.mc_wmi.dto;

import java.util.List;

public class MetaData {
    private String methodName;
    private List<MetaDataInput> inputs;
    private List<MetaDataOutput> outputs;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<MetaDataInput> getInputs() {
        return inputs;
    }

    public void setInputs(List<MetaDataInput> inputs) {
        this.inputs = inputs;
    }

    public List<MetaDataOutput> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<MetaDataOutput> outputs) {
        this.outputs = outputs;
    }
}
