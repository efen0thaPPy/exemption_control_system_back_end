package com.efeakif.intibak_control.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

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

}
