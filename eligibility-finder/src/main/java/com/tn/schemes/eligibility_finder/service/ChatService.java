package com.tn.schemes.eligibility_finder.service;

import com.tn.schemes.eligibility_finder.repository.SchemeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final SchemeRepository schemeRepository;
    private final RestClient restClient;

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.api.url}")
    private String apiUrl;

    @Value("${ai.api.model}")
    private String configuredModel;

    private String activeModel;

    public ChatService(SchemeRepository schemeRepository) {
        this.schemeRepository = schemeRepository;
        this.restClient = RestClient.create();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private String resolveModel() {
        if (activeModel != null) {
            return activeModel;
        }
        try {
            String modelsUrl = apiUrl.replace("/chat/completions", "/models");
            Map response = restClient.get()
                    .uri(modelsUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .retrieve()
                    .body(Map.class);

            List data = (List) response.get("data");
            List<String> validChatModels = new ArrayList<>();

            for (Object item : data) {
                Map modelObj = (Map) item;
                String id = (String) modelObj.get("id");
                if (id == null) continue;
                String lower = id.toLowerCase();
                if (lower.contains("whisper") || lower.contains("tts") || lower.contains("guard") || lower.contains("orpheus")) {
                    continue;
                }
                validChatModels.add(id);
            }

            // Pick standard non-reasoning chat models first so output tokens aren't wasted on thinking
            for (String preferred : List.of("llama-3.1", "llama-3.3", "gemma", "llama-4", "mistral", "mixtral", "qwen", "gpt-oss")) {
                for (String id : validChatModels) {
                    if (id.toLowerCase().contains(preferred)) {
                        activeModel = id;
                        System.out.println("Selected Groq Model: " + activeModel);
                        return activeModel;
                    }
                }
            }

            if (!validChatModels.isEmpty()) {
                activeModel = validChatModels.get(0);
                System.out.println("Selected Groq Model: " + activeModel);
                return activeModel;
            }
        } catch (Exception ignored) {
        }
        return configuredModel;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public String askAndTranslate(String userMessage, String targetLanguage, List<String> matchedSchemes) {
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("PASTE_YOUR_GROQ_KEY_HERE")) {
            return "Please paste your Groq API key in application.properties to activate the AI assistant.";
        }

        String language = (targetLanguage == null || targetLanguage.isBlank()) ? "English" : targetLanguage;
        String modelToUse = resolveModel();

        String databaseContext = schemeRepository.findAll().stream()
                .map(s -> s.getName() + ": " + s.getBenefitAmount())
                .collect(Collectors.joining("; "));

        String userMatchedContext = (matchedSchemes != null && !matchedSchemes.isEmpty())
                ? "Citizen's matched schemes:\n" + String.join("\n", matchedSchemes)
                : "The citizen has not submitted the eligibility form yet.";

        String systemPrompt = """
                You are a multilingual Tamil Nadu Government Welfare Scheme Translator and Doubt-Clarifying Assistant.

                CRITICAL RULES:
                1. Write your ENTIRE response directly in %s. Do NOT include any thinking process, preamble, or tags.
                2. Keep responses concise (under 450 words) using short bullet points so all items fit cleanly.
                3. When translating multiple schemes, give 2 short lines per scheme: translated name + benefit, and required documents with official certificate names in English brackets (e.g., Income Certificate, Community Certificate, Aadhaar Card, Smart Ration Card).

                %s

                Reference Scheme List: %s
                """.formatted(language, userMatchedContext, databaseContext);

        Map<String, Object> requestBody = Map.of(
                "model", modelToUse,
                "max_tokens", 850,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userMessage)
                )
        );

        try {
            Map response = restClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            List choices = (List) response.get("choices");
            Map firstChoice = (Map) choices.get(0);
            Map message = (Map) firstChoice.get("message");
            String reply = (String) message.get("content");

            if (reply == null || reply.isBlank()) {
                return "The response was too long for the free token limit. Try clicking 'Translate & Explain' on an individual scheme card, or ask a specific question!";
            }

            // Strip <think>...</think> blocks if a reasoning model emits them inline
            reply = reply.replaceAll("(?s)<think>.*?</think>", "").trim();
            return reply;

        } catch (Exception e) {
            activeModel = null;
            return "Unable to reach the AI service right now. (" + e.getMessage() + ")";
        }
    }
}