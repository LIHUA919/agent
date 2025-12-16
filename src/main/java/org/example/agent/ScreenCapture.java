package org.example.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;

/**
 * 屏幕截图功能类
 */
public class
ScreenCapture {
    private static final Logger logger = LoggerFactory.getLogger(ScreenCapture.class);
    private static final String SCREENSHOT_DIR = "screenshots";

    public ScreenCapture() {
        // 确保截图目录存在
        File dir = new File(SCREENSHOT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 截取整个屏幕
     */
    public BufferedImage captureScreen() {
        try {
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            return new Robot().createScreenCapture(screenRect);
        } catch (AWTException e) {
            logger.error("Error capturing screen", e);
            throw new RuntimeException("Failed to capture screen", e);
        }
    }

    /**
     * 截取指定区域
     */
    public BufferedImage captureArea(Rectangle area) {
        try {
            return new Robot().createScreenCapture(area);
        } catch (AWTException e) {
            logger.error("Error capturing screen area", e);
            throw new RuntimeException("Failed to capture screen area", e);
        }
    }

    /**
     * 保存截图到文件
     */
    public String saveScreenshot(BufferedImage image) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String filename = SCREENSHOT_DIR + File.separator + "screenshot_" + timestamp + ".png";

            File file = new File(filename);
            ImageIO.write(image, "PNG", file);

            logger.info("Screenshot saved to: {}", filename);
            return file.getAbsolutePath();
        } catch (IOException e) {
            logger.error("Error saving screenshot", e);
            throw new RuntimeException("Failed to save screenshot", e);
        }
    }

    /**
     * 获取屏幕尺寸
     */
    public Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }
}