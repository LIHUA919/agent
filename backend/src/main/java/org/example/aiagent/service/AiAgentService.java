package org.example.aiagent.service;

import org.example.aiagent.computeruse.ComputerUseService;
import org.example.aiagent.service.ai.AiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AiAgentService {
    private static final Logger logger = LoggerFactory.getLogger(AiAgentService.class);

    private final AiService aiService;
    private final ComputerUseService computerUseService;

    @Autowired
    public AiAgentService(AiService aiService, ComputerUseService computerUseService) {
        this.aiService = aiService;
        this.computerUseService = computerUseService;
    }

    public String processMessage(String userInput) {
        try {
            logger.info("Processing user input: {}", userInput);

            // 1. 使用AI分析用户意图
            String intent = aiService.analyzeIntent(userInput);
            logger.info("Detected intent: {}", intent);

            // 2. 如果是computer use相关的命令，执行相应操作
            if (isComputerUseCommand(intent)) {
                return computerUseService.executeCommand(userInput);
            }

            // 3. 否则返回AI对话回复
            return aiService.generateResponse(userInput);

        } catch (Exception e) {
            logger.error("Error processing message: " + userInput, e);
            return "Sorry, I encountered an error while processing your request: " + e.getMessage();
        }
    }

    private boolean isComputerUseCommand(String intent) {
        return intent.toLowerCase().contains("computer") ||
               intent.toLowerCase().contains("click") ||
               intent.toLowerCase().contains("type") ||
               intent.toLowerCase().contains("move") ||
               intent.toLowerCase().contains("screenshot") ||
               intent.toLowerCase().contains("open");
    }
}