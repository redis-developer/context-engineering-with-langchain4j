package io.redis.devrel.workshop.controller;

import io.redis.devrel.workshop.services.BasicChatAssistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    @Autowired
    private BasicChatAssistant assistant;

    @GetMapping("/ai/chat/string")
    public Flux<String> chat(@RequestParam("query") String query) {
        return assistant.chat(SYSTEM_PROMPT, query);
    }

    private static final String SYSTEM_PROMPT = """
            You are an AI assistant that should act, talk, and behave as if you were J.A.R.V.I.S AI
            from the Iron Man movies. Be formal but friendly, and add personality. You are going to
            be the brains behind this AI project. While providing answers, be informative but maintain
            the J.A.R.V.I.S personality.

            Also, make sure to:

            1. Keep your answer concise with three sentences top. Avoid listing items and bullet points.
            2. Use gender-neutral language - avoid terms like 'sir' or 'madam'.
            3. When talking about dates, use the format Month Day, Year (e.g., January 1, 2020).
            """;
}
