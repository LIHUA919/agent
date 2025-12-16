package org.example.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AI Agent with Computer Use capabilities
 */
public class ComputerUseAgent {
    private static final Logger logger = LoggerFactory.getLogger(ComputerUseAgent.class);

    private final Robot robot;
    private final ExecutorService executor;
    private final AIClient aiClient;
    private final ScreenCapture screenCapture;

    public ComputerUseAgent() throws AWTException {
        this.robot = new Robot();
        this.executor = Executors.newCachedThreadPool();
        this.aiClient = new AIClient();
        this.screenCapture = new ScreenCapture();

        logger.info("ComputerUseAgent initialized successfully");
    }

    /**
     * 执行用户命令
     */
    public CompletableFuture<String> executeCommand(String command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Executing command: {}", command);

                // 1. 分析用户意图
                Intent intent = analyzeIntent(command);

                // 2. 根据意图执行相应操作
                switch (intent.getType()) {
                    case MOUSE_CLICK:
                        return executeMouseClick(intent);
                    case MOUSE_MOVE:
                        return executeMouseMove(intent);
                    case TYPE_TEXT:
                        return executeTypeText(intent);
                    case OPEN_APPLICATION:
                        return executeOpenApplication(intent);
                    case SCREENSHOT:
                        return executeScreenshot();
                    case COMPLEX_TASK:
                        return executeComplexTask(command);
                    default:
                        return "Unknown command type";
                }
            } catch (Exception e) {
                logger.error("Error executing command: " + command, e);
                return "Error: " + e.getMessage();
            }
        }, executor);
    }

    /**
     * 分析用户意图
     */
    private Intent analyzeIntent(String command) {
        // 简单的意图识别逻辑
        String lowerCommand = command.toLowerCase();

        if (lowerCommand.contains("click") || lowerCommand.contains("点击")) {
            return new Intent(IntentType.MOUSE_CLICK, command);
        } else if (lowerCommand.contains("move") || lowerCommand.contains("移动")) {
            return new Intent(IntentType.MOUSE_MOVE, command);
        } else if (lowerCommand.contains("type") || lowerCommand.contains("输入") || lowerCommand.contains("打字")) {
            return new Intent(IntentType.TYPE_TEXT, command);
        } else if (lowerCommand.contains("open") || lowerCommand.contains("打开")) {
            return new Intent(IntentType.OPEN_APPLICATION, command);
        } else if (lowerCommand.contains("screenshot") || lowerCommand.contains("截图")) {
            return new Intent(IntentType.SCREENSHOT, command);
        } else {
            return new Intent(IntentType.COMPLEX_TASK, command);
        }
    }

    /**
     * 执行鼠标点击
     */
    private String executeMouseClick(Intent intent) {
        try {
            // 使用AI分析点击位置
            BufferedImage screen = screenCapture.captureScreen();
            Point clickPoint = aiClient.analyzeClickPosition(screen, intent.getCommand());

            if (clickPoint != null) {
                robot.mouseMove(clickPoint.x, clickPoint.y);
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.delay(100);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

                return String.format("Clicked at position (%d, %d)", clickPoint.x, clickPoint.y);
            } else {
                return "Could not determine click position";
            }
        } catch (Exception e) {
            logger.error("Error executing mouse click", e);
            return "Error executing mouse click: " + e.getMessage();
        }
    }

    /**
     * 执行鼠标移动
     */
    private String executeMouseMove(Intent intent) {
        try {
            BufferedImage screen = screenCapture.captureScreen();
            Point movePoint = aiClient.analyzeMovePosition(screen, intent.getCommand());

            if (movePoint != null) {
                robot.mouseMove(movePoint.x, movePoint.y);
                return String.format("Moved mouse to position (%d, %d)", movePoint.x, movePoint.y);
            } else {
                return "Could not determine move position";
            }
        } catch (Exception e) {
            logger.error("Error executing mouse move", e);
            return "Error executing mouse move: " + e.getMessage();
        }
    }

    /**
     * 执行文本输入
     */
    private String executeTypeText(Intent intent) {
        try {
            String textToType = aiClient.extractTextToType(intent.getCommand());
            if (textToType != null && !textToType.isEmpty()) {
                for (char c : textToType.toCharArray()) {
                    int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
                    if (keyCode != KeyEvent.VK_UNDEFINED) {
                        robot.keyPress(keyCode);
                        robot.delay(50);
                        robot.keyRelease(keyCode);
                        robot.delay(50);
                    }
                }
                return String.format("Typed text: %s", textToType);
            } else {
                return "Could not extract text to type";
            }
        } catch (Exception e) {
            logger.error("Error typing text", e);
            return "Error typing text: " + e.getMessage();
        }
    }

    /**
     * 打开应用程序
     */
    private String executeOpenApplication(Intent intent) {
        try {
            String appName = aiClient.extractAppName(intent.getCommand());
            if (appName != null) {
                // Windows特定的应用打开逻辑
                String command = "cmd /c start " + appName;
                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();

                return String.format("Opened application: %s", appName);
            } else {
                return "Could not determine application name";
            }
        } catch (Exception e) {
            logger.error("Error opening application", e);
            return "Error opening application: " + e.getMessage();
        }
    }

    /**
     * 执行截图
     */
    private String executeScreenshot() {
        try {
            BufferedImage screenshot = screenCapture.captureScreen();
            String filePath = screenCapture.saveScreenshot(screenshot);
            return "Screenshot saved to: " + filePath;
        } catch (Exception e) {
            logger.error("Error taking screenshot", e);
            return "Error taking screenshot: " + e.getMessage();
        }
    }

    /**
     * 执行复杂任务
     */
    private String executeComplexTask(String command) {
        try {
            // 截取当前屏幕
            BufferedImage screen = screenCapture.captureScreen();

            // 使用AI分析屏幕并执行复杂操作
            String result = aiClient.executeComplexTask(screen, command);

            return result;
        } catch (Exception e) {
            logger.error("Error executing complex task", e);
            return "Error executing complex task: " + e.getMessage();
        }
    }

    /**
     * 关闭agent
     */
    public void shutdown() {
        logger.info("Shutting down ComputerUseAgent");
        executor.shutdown();
        aiClient.close();
    }
}