# Java AI Agent with Computer Use

一个基于Java开发的AI Agent，具备Computer Use功能，可以通过自然语言控制计算机操作。

## 功能特性

🤖 **自然语言交互** - 支持中英文命令
🖱️ **鼠标控制** - 点击、移动等鼠标操作
⌨️ **键盘输入** - 自动文本输入
📸 **屏幕截图** - 屏幕捕获和分析
🚀 **应用启动** - 打开各种应用程序
🧠 **复杂任务** - 多步骤复杂操作

## 项目架构

```
src/main/java/org/example/agent/
├── ComputerUseAgent.java    # 主Agent类
├── AIClient.java            # AI服务客户端
├── ScreenCapture.java       # 屏幕截图功能
├── Intent.java              # 用户意图封装
└── IntentType.java          # 意图类型枚举
```

## 环境要求

- Java 21+
- Maven 3.6+

## 快速开始

### 1. 安装依赖

```bash
mvn clean install
```

### 2. 配置AI服务

设置环境变量：

```bash
# OpenAI API配置
export AI_API_KEY="your-openai-api-key"
export AI_API_URL="https://api.openai.com/v1/chat/completions"

# 或者使用其他兼容的AI服务
```

### 3. 运行程序

```bash
mvn exec:java -Dexec.mainClass="org.example.Main"
```

## 使用示例

### 鼠标操作
```
AI Agent > 点击屏幕上的开始按钮
AI Agent > 将鼠标移动到右上角
AI Agent > click the confirm button
```

### 键盘输入
```
AI Agent > 输入Hello World
AI Agent > type my email address
```

### 应用程序
```
AI Agent > 打开记事本
AI Agent > 打开浏览器
AI Agent > open calculator
```

### 截图功能
```
AI Agent > 截个图
AI Agent > take screenshot
```

### 复杂任务
```
AI Agent > 帮我打开记事本并输入一段话
AI Agent > 找到屏幕上的计算器并打开
```

## API集成

### 支持的AI服务

- OpenAI GPT-4 Vision
- 其他兼容OpenAI API格式的服务

### API调用流程

1. **屏幕捕获** - 获取当前屏幕截图
2. **意图分析** - 分析用户自然语言命令
3. **视觉理解** - AI分析屏幕内容和用户意图
4. **操作执行** - 执行相应的计算机操作
5. **结果反馈** - 返回操作结果

## 配置选项

### AIClient配置

```java
// 环境变量配置
AI_API_KEY=your-api-key
AI_API_URL=https://api.openai.com/v1/chat/completions

// 代码配置
AIClient client = new AIClient();
```

### 日志配置

日志文件位于 `logs/agent.log`，支持日志轮转。

## 安全注意事项

⚠️ **重要提醒**：

1. **API密钥安全** - 请妥善保管AI API密钥
2. **权限控制** - 确保程序有适当的系统权限
3. **屏幕隐私** - 屏幕截图可能包含敏感信息
4. **操作验证** - 重要操作前建议人工确认

## 开发指南

### 添加新功能

1. 在`IntentType.java`中添加新的意图类型
2. 在`ComputerUseAgent.java`中实现对应的方法
3. 更新`analyzeIntent()`方法以支持新的命令类型

### 自定义AI服务

继承`AIClient`类并重写相应方法：

```java
public class CustomAIClient extends AIClient {
    @Override
    public Point analyzeClickPosition(BufferedImage screen, String command) {
        // 自定义实现
    }
}
```

## 故障排除

### 常见问题

1. **API密钥错误**
   ```
   错误: Unexpected code 401
   解决: 检查AI_API_KEY环境变量设置
   ```

2. **屏幕捕获权限**
   ```
   错误: AWTException
   解决: 确保程序有屏幕访问权限
   ```

3. **依赖冲突**
   ```
   错误: ClassNotFoundException
   解决: mvn clean install 重新构建
   ```

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证

MIT License

## 更新日志

### v1.0.0
- 初始版本发布
- 基本的Computer Use功能
- 支持中英文命令
- 集成OpenAI Vision API
