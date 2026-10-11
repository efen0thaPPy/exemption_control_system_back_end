package com.efeakif.intibak_control.enums;

public enum AiModel {
    llama31("llama3.1", true), qwen257b("qwen2.5:7b", true), gemini36("gemini-3.6-flash", false),
    qwen2514b("qwen2.5:14b", true);

    private final String modelName;
    private final boolean isLocal;

    AiModel(String modelName, boolean isLocal) {
        this.modelName = modelName;
        this.isLocal = isLocal;

    }

    public String getModelName() {
        return modelName;
    }

    public boolean getIsLocal() {
        return isLocal;
    }

}
