package org.example.aiagent.computeruse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ComputerUseService {
    private static final Logger logger = LoggerFactory.getLogger(ComputerUseService.class);

    private final Robot robot;
    private final ScreenCaptureService screenCaptureService;

    public ComputerUseService() throws AWTException {
        this.robot = new Robot();
        this.screenCaptureService = new ScreenCaptureService();
        logger.info("ComputerUseService initialized successfully");
    }

    public String executeCommand(String command) {
        try {
            logger.info("Executing computer use command: {}", command);

            String lowerCommand = command.toLowerCase();

            if (lowerCommand.contains("click") || lowerCommand.contains("点击")) {
                return executeClick(command);
            } else if (lowerCommand.contains("type") || lowerCommand.contains("输入") || lowerCommand.contains("打字")) {
                return executeTypeText(command);
            } else if (lowerCommand.contains("move") || lowerCommand.contains("移动")) {
                return executeMouseMove(command);
            } else if (lowerCommand.contains("screenshot") || lowerCommand.contains("截图")) {
                return executeScreenshot();
            } else if (lowerCommand.contains("open") || lowerCommand.contains("打开")) {
                return executeOpenApplication(command);
            } else {
                return "I don't understand how to execute that computer command. Please be more specific.";
            }

        } catch (Exception e) {
            logger.error("Error executing computer use command: " + command, e);
            return "Error executing command: " + e.getMessage();
        }
    }

    private String executeClick(String command) {
        try {
            // 简单实现：点击屏幕中心
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int centerX = screenSize.width / 2;
            int centerY = screenSize.height / 2;

            robot.mouseMove(centerX, centerY);
            robot.delay(500);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(100);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

            return String.format("Clicked at center of screen (%d, %d)", centerX, centerY);

        } catch (Exception e) {
            logger.error("Error executing click", e);
            return "Failed to execute click: " + e.getMessage();
        }
    }

    private String executeTypeText(String command) {
        try {
            String textToType = extractTextFromCommand(command);
            if (textToType.isEmpty()) {
                return "I couldn't find any text to type in your command.";
            }

            // 模拟键盘输入
            for (char c : textToType.toCharArray()) {
                int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
                if (keyCode != KeyEvent.VK_UNDEFINED) {
                    robot.keyPress(keyCode);
                    robot.delay(50);
                    robot.keyRelease(keyCode);
                    robot.delay(50);
                }
            }

            return String.format("Typed: %s", textToType);

        } catch (Exception e) {
            logger.error("Error typing text", e);
            return "Failed to type text: " + e.getMessage();
        }
    }

    private String executeMouseMove(String command) {
        try {
            // 简单实现：移动到屏幕右上角
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int targetX = screenSize.width - 100;
            int targetY = 100;

            robot.mouseMove(targetX, targetY);

            return String.format("Moved mouse to position (%d, %d)", targetX, targetY);

        } catch (Exception e) {
            logger.error("Error moving mouse", e);
            return "Failed to move mouse: " + e.getMessage();
        }
    }

    private String executeScreenshot() {
        try {
            BufferedImage screenshot = screenCaptureService.captureScreen();
            String filePath = screenCaptureService.saveScreenshot(screenshot);
            return "Screenshot saved to: " + filePath;

        } catch (Exception e) {
            logger.error("Error taking screenshot", e);
            return "Failed to take screenshot: " + e.getMessage();
        }
    }

    private String executeOpenApplication(String command) {
        try {
            String appName = extractAppName(command);
            if (appName.isEmpty()) {
                return "I couldn't find an application name in your command.";
            }

            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                // Windows
                String cmd = "cmd /c start " + appName;
                Runtime.getRuntime().exec(cmd);
                return "Opening " + appName + " on Windows";
            } else if (os.contains("mac")) {
                // macOS
                String cmd = "open " + appName;
                Runtime.getRuntime().exec(cmd);
                return "Opening " + appName + " on macOS";
            } else {
                // Linux
                String cmd = appName;
                Runtime.getRuntime().exec(cmd);
                return "Opening " + appName + " on Linux";
            }

        } catch (Exception e) {
            logger.error("Error opening application", e);
            return "Failed to open application: " + e.getMessage();
        }
    }

    private String extractTextFromCommand(String command) {
        // 简单的文本提取逻辑
        String lowerCommand = command.toLowerCase();

        if (lowerCommand.contains("输入") || lowerCommand.contains("type")) {
            int typeIndex = Math.max(command.indexOf("输入"), command.indexOf("type"));
            if (typeIndex != -1) {
                String afterType = command.substring(typeIndex + 2).trim();
                // 移除引号
                afterType = afterType.replaceAll("[\"']", "");
                return afterType;
            }
        }

        return "";
    }

    private String extractAppName(String command) {
        // 简单的应用名提取逻辑
        String lowerCommand = command.toLowerCase();

        if (lowerCommand.contains("打开") || lowerCommand.contains("open")) {
            int openIndex = Math.max(command.indexOf("打开"), command.indexOf("open"));
            if (openIndex != -1) {
                String afterOpen = command.substring(openIndex + 2).trim();
                // 移除常见的冠词
                afterOpen = afterOpen.replaceAll("^(the |a |an )", "");
                return afterOpen;
            }
        }

        // 常见应用名称映射
        if (lowerCommand.contains("记事本") || lowerCommand.contains("notepad")) {
            return "notepad";
        } else if (lowerCommand.contains("计算器") || lowerCommand.contains("calculator")) {
            return "calc";
        } else if (lowerCommand.contains("浏览器") || lowerCommand.contains("browser")) {
            return System.getProperty("os.name").toLowerCase().contains("win") ? "chrome" : "firefox";
        }

        return "";
    }
}