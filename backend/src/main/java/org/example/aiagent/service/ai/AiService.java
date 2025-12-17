package org.example.aiagent.service.ai;

import com.google.gson.Gson;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AiService {
    private static final Logger logger = LoggerFactory.getLogger(AiService.class);

    @Value("${ai.api.key:}")
    private String apiKey;

    @Value("${ai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    private final OkHttpClient httpClient;
    private final Gson gson;

    public AiService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    public String analyzeIntent(String userInput) {
        if (apiKey == null || apiKey.isEmpty()) {
            // 如果没有配置API key，使用简单的规则匹配
            return analyzeIntentByRules(userInput);
        }

        try {
            Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", new Object[]{
                    Map.of(
                        "role", "user",
                        "content", "Analyze the user's intent and determine if they want to perform computer operations like clicking, typing, taking screenshots, or opening applications. Respond with 'computer_use' if it's a computer operation, or 'chat' if it's just a conversation. User input: " + userInput
                    )
                },
                "max_tokens", 10
            );

            String response = sendApiRequest(requestBody);
            return parseIntentFromResponse(response);

        } catch (Exception e) {
            logger.error("Error analyzing intent with AI, falling back to rules", e);
            return analyzeIntentByRules(userInput);
        }
    }

    public String generateResponse(String userInput) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "AI service not configured. Please set AI_API_KEY environment variable.";
        }

        try {
            Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", new Object[]{
                    Map.of(
                        "role", "user",
                        "content", userInput
                    )
                },
                "max_tokens", 500
            );

            String response = sendApiRequest(requestBody);
            return parseTextFromResponse(response);

        } catch (Exception e) {
            logger.error("Error generating AI response", e);
            return "Sorry, I'm having trouble generating a response right now.";
        }
    }

    private String analyzeIntentByRules(String userInput) {
        String lowerInput = userInput.toLowerCase();

        if (lowerInput.contains("click") || lowerInput.contains("点击") ||
            lowerInput.contains("type") || lowerInput.contains("输入") ||
            lowerInput.contains("move") || lowerInput.contains("移动") ||
            lowerInput.contains("screenshot") || lowerInput.contains("截图") ||
            lowerInput.contains("open") || lowerInput.contains("打开")) {
            return "computer_use";
        }

        return "chat";
    }

    private String sendApiRequest(Map<String, Object> requestBody) throws IOException {
        RequestBody body = RequestBody.create(
            gson.toJson(requestBody),
            MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            ResponseBody responseBody = response.body();
            return responseBody != null ? responseBody.string() : "";
        }
    }

    private String parseIntentFromResponse(String response) {
        try {
            Map<String, Object> jsonResponse = gson.fromJson(response, Map.class);
            String content = (String) ((Map<String, Object>) ((java.util.List<?>) jsonResponse.get("choices"))
                    .get(0))
                    .get("message")
                    .toString()
                    .split("\"content\":\"")[1]
                    .split("\"")[0];

            return content.trim().toLowerCase().contains("computer") ? "computer_use" : "chat";
        } catch (Exception e) {
            logger.error("Error parsing intent from response", e);
            return "chat";
        }
    }

    private String parseTextFromResponse(String response) {
        try {
            Map<String, Object> jsonResponse = gson.fromJson(response, Map.class);
            return (String) ((Map<String, Object>) ((java.util.List<?>) jsonResponse.get("choices"))
                    .get(0))
                    .get("message")
                    .toString()
                    .split("\"content\":\"")[1]
                    .split("\"")[0];
        } catch (Exception e) {
            logger.error("Error parsing text from response", e);
            return "I'm sorry, I couldn't understand that.";
        }
    }
}