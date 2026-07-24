package gov.lawenforcement.conversational.controller;

import gov.lawenforcement.conversational.dto.ChatMessageRequest;
import gov.lawenforcement.conversational.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/message")
    public ResponseEntity<Map<String, Object>> sendMessage(@Valid @RequestBody ChatMessageRequest body) {
        Map<String, Object> response = chatService.processMessage(body.getSessionId(), body.getMessage());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<Map<String, Object>> getSuggestions() {
        return ResponseEntity.ok(Map.of(
            "suggestions", new Object[]{
                Map.of("text", "Show me all open cases", "icon", "folder-open", "category", "Cases"),
                Map.of("text", "What are the crime statistics?", "icon", "bar-chart", "category", "Analytics"),
                Map.of("text", "Generate FIR report for case 1", "icon", "file-pdf", "category", "Reports"),
                Map.of("text", "Show recent cases in Bengaluru", "icon", "search", "category", "Search"),
                Map.of("text", "Show financial transactions for case 3", "icon", "dollar", "category", "Financial"),
                Map.of("text", "List all accused persons", "icon", "user-delete", "category", "Persons"),
                Map.of("text", "What cases have chargesheets filed?", "icon", "check-circle", "category", "Cases"),
                Map.of("text", "Show crime head distribution", "icon", "pie-chart", "category", "Analytics")
            }
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "conversational-ai"));
    }
}
