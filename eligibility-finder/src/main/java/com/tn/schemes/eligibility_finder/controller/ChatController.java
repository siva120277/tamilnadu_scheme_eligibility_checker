package com.tn.schemes.eligibility_finder.controller;

import com.tn.schemes.eligibility_finder.model.ChatRequest;
import com.tn.schemes.eligibility_finder.model.ChatResponse;
import com.tn.schemes.eligibility_finder.service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String reply = chatService.askAndTranslate(
                request.getMessage(),
                request.getTargetLanguage(),
                request.getMatchedSchemes()
        );
        return new ChatResponse(reply);
    }
}
