package com.dylibso.mcpx4j.core;

import java.util.Map;

public interface McpxServlet {
    String name();

    Map<String, McpxTool> tools();
}
