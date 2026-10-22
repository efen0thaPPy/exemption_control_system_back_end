package com.efeakif.intibak_control.service.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.efeakif.intibak_control.enums.AiModel;

@Service
public class LLMService {

        public final ChatClient ollamaChatClient;
        private final ChatClient geminiChatClient;

        public LLMService(
                        @Qualifier("ollamaChatModel") ChatModel ollamaChatModel,
                        @Qualifier("openAiChatModel") ChatModel geminiChatModel) {
                this.ollamaChatClient = ChatClient.create(ollamaChatModel);
                this.geminiChatClient = ChatClient.create(geminiChatModel);
        }

        public <T> T converse(AiModel aiModel, String systemPrompt, String userPrompt, Class<T> targetClass) {

                if (aiModel.getIsLocal()) {
                        return ollamaChatClient.prompt()
                                        .options(OllamaOptions.builder().model(aiModel.getModelName())
                                                        .build())
                                        .user(userPrompt).system(systemPrompt).call().entity(targetClass);

                } else
                        return geminiChatClient.prompt()
                                        .options(OpenAiChatOptions.builder().model(aiModel.getModelName())
                                                        .build())
                                        .user(userPrompt).system(systemPrompt).call().entity(targetClass);
        }

        public <T> T converse(AiModel aiModel, String userPrompt, Class<T> targetClass) {

                if (aiModel.getIsLocal()) {
                        return ollamaChatClient.prompt()
                                        .options(OllamaOptions.builder().model(aiModel.getModelName())
                                                        .build())
                                        .user(userPrompt).call().entity(targetClass);

                } else
                        return geminiChatClient.prompt()
                                        .options(OpenAiChatOptions.builder().model(aiModel.getModelName())
                                                        .build())
                                        .user(userPrompt).call().entity(targetClass);

        }

}
