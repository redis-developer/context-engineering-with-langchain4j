package io.redis.devrel.workshop.services;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

@AiService
public interface BasicChatAssistant {

    @SystemMessage("""
            {{systemPrompt}}
            """)
    Flux<String> chat(@V("systemPrompt") String systemPrompt, @UserMessage String query);

}
