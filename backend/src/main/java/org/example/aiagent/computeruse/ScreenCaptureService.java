package org.example.aiagent.computeruse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenCaptureService {
    private static final String SCREENSHOT_DIR = "screenshots";

    public ScreenCaptureService() {
        // 确保截图目录存在
        File dir = new File(SCREENSHOT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 截取整个屏幕
     */
    public BufferedImage captureScreen() throws AWTException {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return new Robot().createScreenCapture(screenRect);
    }

    /**
     * 保存截图到文件
     */
    public String saveScreenshot(BufferedImage image) throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = SCREENSHOT_DIR + File.separator + "screenshot_" + timestamp + ".png";

        File file = new File(filename);
        ImageIO.write(image, "PNG", file);

        return file.getAbsolutePath();
    }
}