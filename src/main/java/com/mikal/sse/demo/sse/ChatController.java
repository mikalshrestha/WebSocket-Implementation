package com.mikal.sse.demo.sse;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @PostMapping("/send-message")
    public ResponseEntity<String> sendMessage(@RequestBody ChatMessage chatMessage) {
        System.out.println("Received message: " + chatMessage.getMessage());
        // Save or process the message here
        return ResponseEntity.ok("Message received: " + chatMessage.getMessage());
    }

    @GetMapping(value = "/stream-response", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamResponse(@RequestParam String conversationId) {
        SseEmitter emitter = new SseEmitter();
        executor.execute(() -> {
            try {
                // Simulate AI-generated responses
                String[] responses = {
                        "Hello! How can I assist with conversation " + conversationId + "?",
                        "This is a real-time response using Server-Sent Events.",
                        "Feel free to ask me anything!"
                };

                for (String response : responses) {
                    emitter.send(SseEmitter.event().data(response));
                    Thread.sleep(1000); // Simulate delay for each response chunk
                }

                // Indicate completion
                emitter.send(SseEmitter.event().data("[DONE]"));
                emitter.complete();
            } catch (IOException | InterruptedException e) {
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }
}

