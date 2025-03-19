package com.dylibso.mcpx4j.core;

public interface McpxServletFactory {
    String name();
    McpxServlet create();
}
