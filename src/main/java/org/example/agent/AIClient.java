package org.example.agent;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * AI客户端类，用于与AI服务交互
 */
public class AIClient {
    private static final Logger logger = LoggerFactory.getLogger(AIClient.class);

    private final OkHttpClient httpClient;
    private final Gson gson;
    private final String apiKey;
    private final String apiUrl;

    public AIClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        this.gson = new Gson();

        // 从环境变量获取API配置
        this.apiKey = System.getenv().getOrDefault("AI_API_KEY", "your-api-key-here");
        this.apiUrl = System.getenv().getOrDefault("AI_API_URL", "https://api.openai.com/v1/chat/completions");

        logger.info("AIClient initialized with API URL: {}", apiUrl);
    }

    /**
     * 分析屏幕并确定点击位置
     */
    public Point analyzeClickPosition(BufferedImage screen, String command) {
        try {
            String base64Image = encodeImageToBase64(screen);

            Map<String, Object> requestBody = Map.of(
                "model", "gpt-4-vision-preview",
                "messages", new Object[]{
                    Map.of(
                        "role", "user",
                        "content", new Object[]{
                            Map.of(
                                "type", "text",
                                "text", "请分析这张截图，并根据以下指令确定需要点击的位置: " + command +
                                      "\n\n请只返回JSON格式的坐标信息，格式: {\"x\": 数字, \"y\": 数字}"
                            ),
                            Map.of(
                                "type", "image_url",
                                "image_url", Map.of(
                                    "url", "data:image/png;base64," + base64Image
                                )
                            )
                        }
                    )
                },
                "max_tokens", 100
            );

            String response = sendApiRequest(requestBody);
            return parseCoordinatesFromResponse(response);

        } catch (Exception e) {
            logger.error("Error analyzing click position", e);
            return null;
        }
    }

    /**
     * 分析屏幕并确定移动位置
     */
    public Point analyzeMovePosition(BufferedImage screen, String command) {
        // 与点击位置分析类似，但针对移动操作
        return analyzeClickPosition(screen, command);
    }

    /**
     * 从命令中提取要输入的文本
     */
    public String extractTextToType(String command) {
        try {
            Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", new Object[]{
                    Map.of(
                        "role", "user",
                        "content", "请从以下指令中提取需要输入的文本内容，只返回文本内容，不要包含其他文字: " + command
                    )
                },
                "max_tokens", 50
            );

            String response = sendApiRequest(requestBody);
            return parseTextFromResponse(response);

        } catch (Exception e) {
            logger.error("Error extracting text to type", e);
            return null;
        }
    }

    /**
     * 从命令中提取应用名称
     */
    public String extractAppName(String command) {
        try {
            Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", new Object[]{
                    Map.of(
                        "role", "user",
                        "content", "请从以下指令中提取需要打开的应用程序名称，只返回应用名称: " + command
                    )
                },
                "max_tokens", 30
            );

            String response = sendApiRequest(requestBody);
            return parseTextFromResponse(response);

        } catch (Exception e) {
            logger.error("Error extracting app name", e);
            return null;
        }
    }

    /**
     * 执行复杂任务
     */
    public String executeComplexTask(BufferedImage screen, String command) {
        try {
            String base64Image = encodeImageToBase64(screen);

            Map<String, Object> requestBody = Map.of(
                "model", "gpt-4-vision-preview",
                "messages", new Object[]{
                    Map.of(
                        "role", "user",
                        "content", new Object[]{
                            Map.of(
                                "type", "text",
                                "text", "请分析这张截图，并根据以下指令执行相应的操作: " + command +
                                      "\n\n请描述你需要执行的具体步骤和操作结果。"
                            ),
                            Map.of(
                                "type", "image_url",
                                "image_url", Map.of(
                                    "url", "data:image/png;base64," + base64Image
                                )
                            )
                        }
                    )
                },
                "max_tokens", 500
            );

            String response = sendApiRequest(requestBody);
            return parseTextFromResponse(response);

        } catch (Exception e) {
            logger.error("Error executing complex task", e);
            return "Error executing complex task: " + e.getMessage();
        }
    }

    /**
     * 发送API请求
     */
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

    /**
     * 将图片编码为Base64
     */
    private String encodeImageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", outputStream);
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    /**
     * 从响应中解析坐标
     */
    private Point parseCoordinatesFromResponse(String response) {
        try {
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
            String content = jsonResponse
                .getAsJsonArray("choices")
                .get(0)
                .getAsJsonObject()
                .getAsJsonObject("message")
                .getAsJsonArray("content")
                .get(0)
                .getAsJsonObject()
                .get("text")
                .getAsString();

            // 解析JSON格式的坐标
            JsonObject coordinates = gson.fromJson(content, JsonObject.class);
            int x = coordinates.get("x").getAsInt();
            int y = coordinates.get("y").getAsInt();

            return new Point(x, y);

        } catch (Exception e) {
            logger.error("Error parsing coordinates from response: {}", response, e);
            return null;
        }
    }

    /**
     * 从响应中解析文本
     */
    private String parseTextFromResponse(String response) {
        try {
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
            return jsonResponse
                .getAsJsonArray("choices")
                .get(0)
                .getAsJsonObject()
                .getAsJsonObject("message")
                .getAsJsonArray("content")
                .get(0)
                .getAsJsonObject()
                .get("text")
                .getAsString()
                .trim();

        } catch (Exception e) {
            logger.error("Error parsing text from response: {}", response, e);
            return null;
        }
    }

    /**
     * 关闭客户端
     */
    public void close() {
        httpClient.dispatcher().executorService().shutdown();
        httpClient.connectionPool().evictAll();
    }
}