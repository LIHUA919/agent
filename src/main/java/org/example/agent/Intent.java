package org.example.agent;

/**
 * 用户意图类
 */
public class Intent {
    private final IntentType type;
    private final String command;

    public Intent(IntentType type, String command) {
        this.type = type;
        this.command = command;
    }

    public IntentType getType() {
        return type;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return "Intent{" +
                "type=" + type +
                ", command='" + command + '\'' +
                '}';
    }
}