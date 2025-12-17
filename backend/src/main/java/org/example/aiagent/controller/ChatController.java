package org.example.aiagent.controller;

import org.example.aiagent.dto.ChatRequest;
import org.example.aiagent.dto.ChatResponse;
import org.example.aiagent.service.AiAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    private final AiAgentService aiAgentService;

    @Autowired
    public ChatController(AiAgentService aiAgentService) {
        this.aiAgentService = aiAgentService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            String response = aiAgentService.processMessage(request.getUserInput());
            ChatResponse chatResponse = new ChatResponse(response);
            return ResponseEntity.ok(chatResponse);
        } catch (Exception e) {
            ChatResponse errorResponse = new ChatResponse("Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Agent is running");
    }
}