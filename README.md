# Lab 4: Implementing Basic RAG with Knowledge Base Data

## 🎯 Learning Objectives

By the end of this lab, you will:
- Implement Retrieval-Augmented Generation (RAG) using your knowledge base
- Implement a content retriever to search for data in your knowledge base
- Set up a retrieval augmentor to inject relevant context
- Enable the AI to answer questions using document knowledge
- Test RAG functionality with document-specific queries

#### 🕗 Estimated Time: 20 minutes

## 🏗️ What You're Building

In this lab, you'll connect your knowledge base to the chat interface, enabling the AI to retrieve and use relevant document information when answering questions. This includes:

- **Content Retriever**: Searches the knowledge base for relevant information
- **Content Injection**: Formats retrieved documents for optimal LLM understanding
- **RAG Pipeline**: Complete flow from query to augmented response using query routing

### Architecture Overview

```
┌─────────────┐     ┌────────────────—──┐     ┌─────────────——┐
│  Frontend   │────▶│  Spring Boot API  │────▶│    OpenAI     │
│  (Node.js)  │     │   + LangChain4J   │◀────│ GPT-3.5 Turbo │
└─────────────┘     │   + RAG Pipeline  │     └────────────——─┘
                    └─────────────────—─┘
                             │
                       [1] Query │ [2] Retrieve
                             ▼
                  ┌───────────——————──────┐
                  │  Agent Memory Server  │
                  │    (Redis Database)   │
                  └───────────────——————──┘
                             │
                      [3] Context ▲
                             │
                      [4] Augmented
                          Response
```

## 📋 Prerequisites Check

Before starting, ensure you have:

- [ ] Completed Lab 3 successfully
- [ ] At least one PDF processed in your knowledge base
- [ ] Redis Agent Memory Server running with stored documents
- [ ] Backend application with document processing enabled

## 🚀 Setup Instructions

### Step 1: Review the LongTermMemory Configuration

Open `backend-layer/src/main/java/io/redis/devrel/workshop/memory/LongTermMemory.java` and review the RAG configuration structure:

```java
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
```

### Step 2: Implement the Knowledge Base Content Retriever

In `LongTermMemory.java`, locate and implement the `getGeneralKnowledgeBase()` method.

Change from this:

```java
private ContentRetriever getGeneralKnowledgeBase() {
    // TODO: Implement retriever that searches the knowledge base
    return null;
}
```

To this:

```java
private ContentRetriever getGeneralKnowledgeBase() {
    return query -> memoryService.searchKnowledgeBase(query.text())
            .stream()
            .map(Content::from)
            .toList();
}
```

### Step 3: Configure the RAG pipeline

In the `getRetrievalAugmentor()` method, implement the content injector configuration.

Change from this:

```java
@Bean
public RetrievalAugmentor getRetrievalAugmentor() {
    // TODO: Implement a content injector and a query router to build the
    // RetrievalAugmentor correctly.
    return DefaultRetrievalAugmentor.builder()
            .build();
}
```

To this:

```java
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
```

### Step 4: Rebuild and Run the Backend

```bash
cd backend-layer
mvn clean package
mvn spring-boot:run
```

### Step 5: Keep the Frontend Running

The frontend should still be running. If not:

```bash
cd frontend-layer
npm start
```

## 🧪 Testing Your RAG Implementation

### Basic RAG Test

1. Open http://localhost:3000 in your browser
2. Ask a question about content from your uploaded PDF
3. Verify the AI uses document information in its response

Example test queries (adjust based on your PDF content):
- "What does the document say about [specific topic]?"
- "Can you summarize the main points from the uploaded documents?"
- "What information is available about [specific term from PDF]?"

For example, consider the following text from a sample PDF:
```txt
The garage door has a wireless touchpad mounted on the doorjamb, left side facing the door.
It is battery operated with a 5 digit code of 70170. It will raise and lower the garage door from
the outside. You should also reprogram your garage door opener in your car. I would guess
the directions are in your car's manual and on the side of the lift motor in the garage. (I think I
would program the car first and then reprogram the push button touchpad.)

Mounted inside the garage on the right side of the door into the kitchen is a lock box. It
contains a key to that door and all the other exterior doors of the house. The code is 1389.
Push the buttons to enter the code and then push down and hold the slider button and pull off
the front of the box. This slider button is also a reset so if you enter an incorrect code, reset
the lock and try again. You must unlock it again to reinstall it onto the lockbox. If you remove
the piece of cardboard inside the lock, it will expose the code and allow you to change it,
using the cardboard "edge" that is there to make the code selections.
```

This is how you can interact with the AI:

![lab-4-verifying-rag-behavior.png](images/lab-4-verifying-rag-behavior.png)

## 🎨 Understanding the Code

### 1. `ContentRetriever`
- Searches the knowledge base using semantic similarity
- Returns only relevant document chunks for the query
- Leverages vector embeddings for accurate retrieval

### 2. `DefaultContentInjector`
- Formats retrieved content for LLM consumption
- Uses template to structure context and query
- Maintains clear separation between context and user message

### 3. `QueryRouter`
- Determines which retriever to use for queries
- Routes to knowledge base for factual questions
- Can be extended for multiple knowledge sources

### 4. `DefaultRetrievalAugmentor`
- Orchestrates the complete RAG pipeline
- Combines retrieval, injection, and generation
- Manages the flow from query to response

### 5. `ChatController`
- Contains an optimized version of the system prompt
- Provides instructions about how to read the context
- Provides guidelines for generating accurate responses

## 🔍 What's Still Missing? (Context Engineering Perspective)

Your application now has basic RAG, but still lacks:
- ❌ **No User Memories**: Can't store personal preferences
- ❌ **No Query Optimization**: No compression or transformation
- ❌ **No Content Reranking**: Retrieved content isn't prioritized
- ❌ **No Dynamic Routing**: Can't choose between memory types

**Next labs will add these advanced features!**

## 🐛 Troubleshooting

### Common Issues and Solutions

<details>
<summary>AI doesn't use document knowledge</summary>

Solution:
- Verify PDFs were successfully processed (check `.processed` files)
- Ensure knowledge base entries exist in Redis
- Check retriever configuration in LongTermMemory
- Review logs for retrieval errors
</details>

<details>
<summary>Retrieved content seems irrelevant</summary>

Solution:
- Check if embeddings were properly generated
- Verify the semantic search is working
- Try with more specific queries
- Ensure document chunks are meaningful
</details>

<details>
<summary>Response time is slow</summary>

Solution:
- Check Redis connection latency
- Verify number of retrieved documents (may be too many)
- Monitor OpenAI API response times
- Consider implementing caching (coming in Lab 9)
</details>

## 🎉 Lab Completion

Congratulations! You've successfully:
- ✅ Implemented Retrieval-Augmented Generation
- ✅ Connected your knowledge base to the chat interface
- ✅ Enabled document-aware AI responses
- ✅ Tested RAG with real document queries

## 📚 Additional Resources

- [LangChain4J RAG Tutorial](https://docs.langchain4j.dev/tutorials/rag)
- [Understanding RAG Systems](https://www.pinecone.io/learn/retrieval-augmented-generation/)
- [Content Injection Strategies](https://docs.langchain4j.dev/tutorials/rag#augmentation)
- [Vector Search Best Practices](https://redis.io/docs/stack/search/reference/vectors/)

## ➡️ Next Steps

You're ready for [Lab 5: Enabling On-demand Context Management for Memories](../lab-5-starter/README.md) where you'll add user-specific long-term memory capabilities.

```bash
git checkout lab-5-starter
```