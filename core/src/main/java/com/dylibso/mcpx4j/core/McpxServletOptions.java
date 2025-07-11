package com.dylibso.mcpx4j.core;

import com.dylibso.chicory.log.Logger;
import com.dylibso.chicory.log.SystemLogger;
import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.runtime.Machine;
import java.util.function.Function;
import org.extism.sdk.chicory.http.HttpConfig;
import org.extism.sdk.chicory.http.config.generic.GenericHttpConfig;

public class McpxServletOptions {
    final boolean aot;
    Function<Instance, Machine> machineFactory;
    public boolean oAuthAutoRefresh;
    final HttpConfig chicoryHttpConfig;
    final Logger logger;

    McpxServletOptions(boolean aot, Function<Instance, Machine> machineFactory, boolean oAuthAutoRefresh,
            HttpConfig chicoryHttpConfig, Logger logger) {
        this.aot = aot;
        this.machineFactory = machineFactory;
        this.oAuthAutoRefresh = oAuthAutoRefresh;
        this.chicoryHttpConfig = chicoryHttpConfig;
        this.logger = logger;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        boolean aot;
        Function<Instance, Machine> machineFactory;
        boolean oAuthAutoRefresh;
        HttpConfig chicoryHttpConfig;
        Logger chicoryLogger;

        private Builder() {
        }

        public Builder withAot() {
            this.aot = true;
            return this;
        }

        public Builder withMachineFactory(Function<Instance, Machine> machineFactory) {
            this.machineFactory = machineFactory;
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
                    machineFactory,
                    oAuthAutoRefresh,
                    chicoryHttpConfig == null ? GenericHttpConfig.get() : chicoryHttpConfig,
                    chicoryLogger == null ? new SystemLogger() : chicoryLogger);
        }
    }
}
