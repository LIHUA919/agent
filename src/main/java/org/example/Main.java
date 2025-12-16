package org.example;

import org.example.agent.ComputerUseAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

/**
 * Java AI Agent with Computer Use功能的主入口
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("=== Java AI Agent with Computer Use ===");
        System.out.println("🤖 Java AI Agent with Computer Use 功能已启动");
        System.out.println("输入 'help' 查看可用命令，输入 'quit' 退出程序");
        System.out.println();

        ComputerUseAgent agent = null;
        Scanner scanner = new Scanner(System.in);

        try {
            // 初始化AI Agent
            agent = new ComputerUseAgent();

            // 交互式命令行界面
            while (true) {
                System.out.print("AI Agent > ");
                String command = scanner.nextLine().trim();

                if (command.equalsIgnoreCase("quit") || command.equalsIgnoreCase("exit")) {
                    System.out.println("👋 再见！");
                    break;
                } else if (command.equalsIgnoreCase("help")) {
                    printHelp();
                    continue;
                } else if (command.isEmpty()) {
                    continue;
                }

                // 执行命令
                System.out.println("🔧 正在执行: " + command);
                CompletableFuture<String> future = agent.executeCommand(command);

                // 等待执行结果
                String result = future.get();
                System.out.println("✅ 结果: " + result);
                System.out.println();
            }

        } catch (Exception e) {
            logger.error("Error in main application", e);
            System.err.println("❌ 程序运行出错: " + e.getMessage());
        } finally {
            if (agent != null) {
                agent.shutdown();
            }
            scanner.close();
        }
    }

    /**
     * 打印帮助信息
     */
    private static void printHelp() {
        System.out.println("\n📚 可用命令示例:");
        System.out.println("🖱️  鼠标操作:");
        System.out.println("   - '点击屏幕上的开始按钮' 或 'click the start button'");
        System.out.println("   - '将鼠标移动到右上角' 或 'move mouse to top right'");

        System.out.println("\n⌨️  文本输入:");
        System.out.println("   - '输入Hello World' 或 'type Hello World'");

        System.out.println("\n🚀 应用程序:");
        System.out.println("   - '打开记事本' 或 'open notepad'");
        System.out.println("   - '打开浏览器' 或 'open browser'");

        System.out.println("\n📸 截图功能:");
        System.out.println("   - '截个图' 或 'take screenshot'");

        System.out.println("\n🧠 复杂任务:");
        System.out.println("   - '帮我打开记事本并输入一段话'");
        System.out.println("   - '找到屏幕上的计算器并打开'");

        System.out.println("\n🔧 系统命令:");
        System.out.println("   - 'help' - 显示帮助信息");
        System.out.println("   - 'quit' 或 'exit' - 退出程序");
        System.out.println();

        System.out.println("💡 提示: 确保设置了AI_API_KEY环境变量来使用AI功能");
        System.out.println();
    }
}