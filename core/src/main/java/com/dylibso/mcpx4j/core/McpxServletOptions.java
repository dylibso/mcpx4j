package com.dylibso.mcpx4j.core;

import com.dylibso.chicory.log.Logger;
import com.dylibso.chicory.log.SystemLogger;
import org.extism.sdk.chicory.HttpConfig;

public class McpxServletOptions {
    final boolean aot;
    public boolean oAuthAutoRefresh;
    final HttpConfig chicoryHttpConfig;
    final Logger logger;

    McpxServletOptions(boolean aot, boolean oAuthAutoRefresh, HttpConfig chicoryHttpConfig, Logger logger) {
        this.aot = aot;
        this.oAuthAutoRefresh = oAuthAutoRefresh;
        this.chicoryHttpConfig = chicoryHttpConfig;
        this.logger = logger;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        boolean aot;
        boolean oAuthAutoRefresh;
        HttpConfig chicoryHttpConfig;
        Logger chicoryLogger;

        private Builder() {}

        public Builder withAot() {
            this.aot = true;
            return this;
        }
        public Builder withOAuthAutoRefresh() {
            this.oAuthAutoRefresh = true;
            return this;
        }

        public Builder withChicoryHttpConfig(HttpConfig httpConfig) {
            this.chicoryHttpConfig = httpConfig;
            return this;
        }

        public Builder withChicoryLogger(Logger logger) {
            this.chicoryLogger = logger;
            return this;
        }

        public McpxServletOptions build() {
            return new McpxServletOptions(
                    aot,
                    oAuthAutoRefresh,
                    chicoryHttpConfig == null? HttpConfig.defaultConfig() : chicoryHttpConfig,
                    chicoryLogger == null? new SystemLogger() : chicoryLogger);
        }
    }
}
