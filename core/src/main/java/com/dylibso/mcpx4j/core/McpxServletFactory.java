package com.dylibso.mcpx4j.core;

public interface McpxServletFactory {
    String name();

    String schema();

    McpxServlet create();
}
