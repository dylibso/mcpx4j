package com.dylibso.mcpx4j.core.builtins;

import com.dylibso.mcpx4j.core.HttpClient;
import com.dylibso.mcpx4j.core.JsonDecoder;
import com.dylibso.mcpx4j.core.McpxServlet;
import com.dylibso.mcpx4j.core.ServletDescriptor;
import com.dylibso.mcpx4j.core.ServletInstall;
import com.dylibso.mcpx4j.core.ServletSettings;

import java.time.OffsetDateTime;
import java.util.Map;

public class McpRunServlet extends McpxServlet {

    public McpRunServlet(HttpClient client, JsonDecoder jsonDecoder) {
        super("mcp_run",
                new ServletInstall("mcp_run",
                        new ServletDescriptor("mcp_run",
                                OffsetDateTime.now(),
                                OffsetDateTime.now(),
                                new ServletDescriptor.Meta("", "")),
                        new ServletSettings(Map.of(), null)),
                Map.of("search_servlets", new McpRunSearchTool(client, jsonDecoder)));
    }
}
