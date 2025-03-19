package com.dylibso.mcpx4j.core.builtins;

import com.dylibso.mcpx4j.core.McpxServlet;
import com.dylibso.mcpx4j.core.McpxServletFactory;

public interface McpxBuiltInServlet extends McpxServlet {
    default McpxServletFactory asFactory() {
        var self = this;
        return new McpxServletFactory() {
            @Override
            public String name() {
                return self.name();
            }

            @Override
            public McpxServlet create() {
                return self;
            }
        };
    }
}
