package com.dylibso.mcpx4j.core.builtins;

import com.dylibso.mcpx4j.core.McpxTool;

public abstract class McpxBuiltInTool implements McpxTool {

    private final String name;
    private final String description;
    private final String inputSchema;

    McpxBuiltInTool(String name, String description, String inputSchema) {
        this.name = name;
        this.description = description;
        this.inputSchema = inputSchema;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public String inputSchema() {
        return inputSchema;
    }

    @Override
    public abstract String call(String jsonInput);
}
