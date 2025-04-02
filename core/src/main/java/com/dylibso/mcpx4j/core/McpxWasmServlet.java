package com.dylibso.mcpx4j.core;

import java.util.Map;
import java.util.Objects;

public class McpxWasmServlet implements McpxServlet {
    private final String name;
    private final ServletInstall config;
    private final Map<String, McpxTool> tools;

    public McpxWasmServlet(String name, ServletInstall config, Map<String, McpxTool> tools) {
        this.name = name;
        this.config = config;
        this.tools = tools;
    }

    @Override
    public String name() {
        return name;
    }

    public ServletInstall config() {
        return config;
    }

    @Override
    public Map<String, McpxTool> tools() {
        return tools;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof McpxWasmServlet))
            return false;
        McpxWasmServlet that = (McpxWasmServlet) o;
        return Objects.equals(name, that.name) && Objects.equals(tools, that.tools);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, tools);
    }

    @Override
    public String toString() {
        return "McpServlet{" +
                "name='" + name + '\'' +
                ", tools=" + tools +
                '}';
    }
}
