package com.dylibso.mcpx4j.core;

import org.extism.sdk.chicory.Plugin;

import java.nio.charset.StandardCharsets;

public class McpxPluginTool implements McpxTool {

    private final Plugin plugin;
    private final String name;
    private final String description;
    private final String inputschema;

    McpxPluginTool(Plugin plugin, McpxToolDescriptor descriptor) {
        this.plugin = plugin;
        this.name = descriptor.name();
        this.description = descriptor.description();
        this.inputschema = descriptor.inputSchema();
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
        return inputschema;
    }

    @Override
    public String call(String jsonInput) {
        return new String(plugin.call(
                "call",
                jsonInput.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }
}
