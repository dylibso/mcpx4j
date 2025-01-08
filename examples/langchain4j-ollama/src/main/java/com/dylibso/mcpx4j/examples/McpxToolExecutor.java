package com.dylibso.mcpx4j.examples;

import com.dylibso.mcpx4j.core.McpxTool;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.service.tool.ToolExecutor;

import java.util.logging.Logger;

public class McpxToolExecutor implements ToolExecutor {
    private final McpxTool tool;

    public McpxToolExecutor(McpxTool tool) {
        this.tool = tool;
    }

    @Override
    public String execute(ToolExecutionRequest toolExecutionRequest, Object o) {
        Logger.getLogger(tool.name())
                .info("invoking Wasm MCP.RUN function, " +
                        toolExecutionRequest.toString());
        // Splice the function invocation into the JSON representation
        // the MCPX tool expects.
        String adapted = """
                {
                    "method":"tools/call",
                    "params": {
                        "name": "%s",
                        "arguments" : %s
                    }
                }""".formatted(
                toolExecutionRequest.name(),
                toolExecutionRequest.arguments());

        return tool.call(adapted);
    }

}
