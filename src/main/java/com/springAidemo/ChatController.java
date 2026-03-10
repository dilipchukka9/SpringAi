package com.springAidemo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;  // ← changed
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public ChatController(ChatClient.Builder builder, ChatMemory chatMemory, GetInfo tools) {
        this.chatMemory = chatMemory;
        this.chatClient = builder
                .defaultTools(tools)
                .build();
    }
    
   String SYSTEM_PROMPT= """
    You are a helpful general-purpose AI assistant.
    You can answer any question using your own knowledge.
    You also have access to tools for real-time data like
    current time, order status, weather, and movie info.
    Use tools only when real-time data is needed.
    For all other questions, answer directly from your knowledge.
    """;

//    @PostMapping
//    public String chat(@RequestParam String message,
//                       @RequestParam String conversationId) {
//        return chatClient.prompt()
//        		  .system("""
//        	                You are a helpful general-purpose AI assistant.
//        	                You can answer any question using your own knowledge.
//        	                You also have access to tools for real-time data like
//        	                current time, order status, weather, and movie info.
//        	                Use tools only when real-time data is needed.
//        	                For all other questions, answer directly from your knowledge.
//        	                """)
//                .user(message)
//                .advisors(PromptChatMemoryAdvisor.builder(chatMemory)  // ← changed
//                        .conversationId(conversationId)
//                        .build())
//                .call()
//                .content();
//    }

    
    @PostMapping
    public String chat(@RequestParam String message,
                       @RequestParam String conversationId) {

        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .advisors(PromptChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(conversationId)
                        .build())
                .call()
                .chatResponse()
                .getResults()
                .stream()
                .map(r -> r.getOutput().getText())
                .reduce("", String::concat);
    }

}