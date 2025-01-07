package com.dylibso.mcpx4j.core;

public interface McpxTool {
    String name();

    String description();

    String inputSchema();

    String call(String jsonInput);
}
