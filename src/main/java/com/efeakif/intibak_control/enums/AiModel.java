package com.efeakif.intibak_control.enums;

public enum AiModel {
    llama31("llama3.1"), qwen25("qwen2.5:7b"), gemini36("gemini-3.6-flash");

    private final String modelName;

    AiModel(String modelName) {
        this.modelName = modelName;

    }

    public String getModelName() {
        return modelName;
    }

}
