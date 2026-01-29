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
    private String userId;

    @Autowired
    private MemoryService memoryService;

    @Bean
    public RetrievalAugmentor getRetrievalAugmentor() {
        ContentInjector contentInjector = DefaultContentInjector.builder()
                .promptTemplate(PromptTemplate.from("{{userMessage}}\n\n[Context]\n{{contents}}"))
                .build();

        QueryRouter queryRouter = new DefaultQueryRouter(List.of(getGeneralKnowledgeBase()));

        return DefaultRetrievalAugmentor.builder()
                .contentInjector(contentInjector)
                .queryRouter(queryRouter)
                .build();
    }

    private ContentRetriever getGeneralKnowledgeBase() {
        return query -> memoryService.searchKnowledgeBase(query.text())
                .stream()
                .map(Content::from)
                .toList();
    }
}
