package com.dylibso.mcpx4j.core;

import com.dylibso.chicory.log.Logger;
import com.dylibso.chicory.log.SystemLogger;
import org.extism.sdk.chicory.HttpConfig;

public class McpxServletOptions {
    final boolean aot;
    final HttpConfig chicoryHttpConfig;
    final Logger logger;

    McpxServletOptions(boolean aot, HttpConfig chicoryHttpConfig, Logger logger) {
        this.aot = aot;
        this.chicoryHttpConfig = chicoryHttpConfig;
        this.logger = logger;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        boolean aot;
        HttpConfig chicoryHttpConfig;
        Logger chicoryLogger;

        private Builder() {}

        public McpxServletOptions.Builder withAot() {
            this.aot = true;
            return this;
        }

        public McpxServletOptions.Builder withChicoryHttpConfig(HttpConfig httpConfig) {
            this.chicoryHttpConfig = httpConfig;
            return this;
        }

        public McpxServletOptions.Builder withChicoryLogger(Logger logger) {
            this.chicoryLogger = logger;
            return this;
        }

        public McpxServletOptions build() {
            return new McpxServletOptions(
                    aot,
                    chicoryHttpConfig == null? HttpConfig.defaultConfig() : chicoryHttpConfig,
                    chicoryLogger == null? new SystemLogger() : chicoryLogger);
        }
    }
}
