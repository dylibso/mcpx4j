package com.dylibso.mcpx4j.examples;

import com.dylibso.mcpx4j.core.McpxTool;
import org.springframework.ai.model.function.FunctionCallback;

import java.util.logging.Logger;

public class McpxToolFunctionCallback implements FunctionCallback {

    private final McpxTool tool;

    McpxToolFunctionCallback(McpxTool tool)  {
        this.tool = tool;
    }

    @Override
    public String getName() {
        return tool.name();
    }

    @Override
    public String getDescription() {
        return tool.description();
    }

    @Override
    public String getInputTypeSchema() {
        return tool.inputSchema();
    }

    @Override
    public String call(String functionInput) {
        Logger.getLogger(tool.name()).info("invoking Wasm MCP.RUN function, "+functionInput);
        String adapted = """
                {
                    "method":"tools/call",
                    "params": {
                        "name": "%s",
                        "arguments" : %s
                    }
                }""".formatted(tool.name(), functionInput);
        return tool.call(adapted);
    }
}
