package io.redis.devrel.workshop.memory;

import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.injector.ContentInjector;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import io.redis.devrel.workshop.services.MemoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class LongTermMemory {

    @Autowired
    private MemoryService memoryService;

    @Bean
    public RetrievalAugmentor getRetrievalAugmentor() {
        // TODO: Implement a content injector and a query router to build the
        // RetrievalAugmentor correctly.
        return DefaultRetrievalAugmentor.builder()
                .build();
    }

    private ContentRetriever getGeneralKnowledgeBase() {
        // TODO: Implement a ContentRetriever to retrieve the knowledge base
        return null;
    }
}
