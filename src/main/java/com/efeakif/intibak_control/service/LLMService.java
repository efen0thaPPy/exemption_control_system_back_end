package com.efeakif.intibak_control.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import com.efeakif.intibak_control.enums.AiModel;

@Service
public class LLMService {

    public final ChatClient chatClient;

    public LLMService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public <T> T converse(String systemPrompt, String userPrompt, Class<T> targetClass) {

        return chatClient.prompt().system(systemPrompt).user(userPrompt).call().entity(targetClass);
    }

    public <T> T converse(String userPrompt, Class<T> targetClass) {

        return chatClient.prompt().user(userPrompt).call().entity(targetClass);
    }

    // public <T> T converse(AiModel aiModel, String systemPrompt, String
    // userPrompt, Class<T> targetClass) {

    // return
    // chatClient.prompt().options(OpenAiChatOptions.builder().model(aiModel.getModelName()).build())
    // .system(systemPrompt)
    // .user(userPrompt).call().entity(targetClass);
    // }

    // public <T> T converse(AiModel aiModel, String userPrompt, Class<T>
    // targetClass) {

    // return
    // chatClient.prompt().options(OpenAiChatOptions.builder().model(aiModel.getModelName()).build())
    // .user(userPrompt).call().entity(targetClass);
    // }

    public <T> T converse(AiModel aiModel, String systemPrompt, String userPrompt, Class<T> targetClass) {

        return chatClient.prompt().options(OllamaOptions.builder().model(aiModel.getModelName()).build())
                .system(systemPrompt)
                .user(userPrompt).call().entity(targetClass);
    }

    public <T> T converse(AiModel aiModel, String userPrompt, Class<T> targetClass) {

        return chatClient.prompt().options(OllamaOptions.builder().model(aiModel.getModelName()).build())
                .user(userPrompt).call().entity(targetClass);
    }

}
