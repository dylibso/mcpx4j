package com.dylibso.mcpx4j.core.builtins;

import com.dylibso.mcpx4j.core.HttpClient;
import com.dylibso.mcpx4j.core.JsonDecoder;
import com.dylibso.mcpx4j.core.McpxTool;

import java.util.Map;

public class McpRunServlet implements McpxBuiltInServlet {

    private final McpRunSearchTool searchTool;

    public McpRunServlet(HttpClient client, JsonDecoder jsonDecoder) {
        this.searchTool = new McpRunSearchTool(client, jsonDecoder);
    }

    @Override
    public String name() {
        return "mcp_run";
    }

    @Override
    public Map<String, McpxTool> tools() {
        return Map.of("search_servlets", searchTool);
    }
}
