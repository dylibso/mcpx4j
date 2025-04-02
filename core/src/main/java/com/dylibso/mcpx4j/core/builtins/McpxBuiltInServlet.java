package com.dylibso.mcpx4j.core.builtins;

import com.dylibso.mcpx4j.core.McpxServlet;
import com.dylibso.mcpx4j.core.McpxServletFactory;
import java.util.stream.Collectors;

public interface McpxBuiltInServlet extends McpxServlet {
    default McpxServletFactory asFactory() {
        var self = this;
        return new McpxServletFactory() {
            @Override
            public String name() {
                return self.name();
            }

            @Override
            public String schema() {
                return self.tools().entrySet().stream()
                        .map(e -> '"' + e.getKey() + '"' + ':' + e.getValue())
                        .collect(Collectors.joining(",", "{\"tools\":{", "}}"));
            }

            @Override
            public McpxServlet create() {
                return self;
            }
        };
    }
}
