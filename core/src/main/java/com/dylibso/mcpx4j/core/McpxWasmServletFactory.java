package com.dylibso.mcpx4j.core;

import com.dylibso.chicory.wasi.WasiOptions;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.extism.sdk.chicory.ConfigProvider;
import org.extism.sdk.chicory.ExtismHostFunction;
import org.extism.sdk.chicory.ExtismValType;
import org.extism.sdk.chicory.Manifest;
import org.extism.sdk.chicory.ManifestWasm;
import org.extism.sdk.chicory.Plugin;

public class McpxWasmServletFactory implements McpxServletFactory {
    private final ServletInstall install;
    private final Manifest manifest;
    private final McpxServletOptions config;
    private final JsonDecoder jsonDecoder;

    McpxWasmServletFactory(ServletInstall install, Manifest manifest, McpxServletOptions config, JsonDecoder jsonDecoder) {
        this.install = install;
        this.manifest = manifest;
        this.config = config;
        this.jsonDecoder = jsonDecoder;
    }

    public String name() {
        return install.name();
    }

    public ServletInstall install() {
        return install;
    }

    @Override
    public String schema() {
        return install.servlet.meta.schema;
    }

    public Manifest manifest() {
        return manifest;
    }

    public McpxServletOptions config() {
        return config;
    }

    public McpxServlet create() {
        var plugin = Plugin.ofManifest(manifest)
                .withLogger(config.logger)
                .withHostFunctions(DEPRECATED_CONFIG_GET.get()).build();

        var descriptors = jsonDecoder.toolDescriptors(schema().getBytes(StandardCharsets.UTF_8));
        Map<String, McpxTool> tools = descriptors.stream()
                .map(descriptor -> new McpxPluginTool(plugin, descriptor))
                .collect(Collectors.toMap(McpxPluginTool::name, tool -> tool));
        return new McpxWasmServlet(this.install.name(), this.install, tools);
    }

    public static McpxWasmServletFactory create(byte[] bytes, String name, ServletInstall install,
            ConfigProvider configProvider, McpxServletOptions config, JsonDecoder jsonDecoder) {
        var wasm = ManifestWasm.fromBytes(bytes).build();
        Manifest.Options opts = new Manifest.Options()
                .withConfigProvider(configProvider)
                .withAllowedHosts(install.settings().permissions().network().domains())
                .withHttpConfig(config.chicoryHttpConfig)
                .withWasi(WasiOptions.builder()
                        .withArguments(List.of(name))
                        .build());

        if (config.aot) {
            opts = opts.withAoT();
        }

        var manifest = Manifest.ofWasms(wasm)
                .withOptions(opts).build();

        return new McpxWasmServletFactory(install, manifest, config, jsonDecoder);
    }

    private static final Supplier<ExtismHostFunction> DEPRECATED_CONFIG_GET = () -> ExtismHostFunction.of(
            "config_get",
            List.of(ExtismValType.I64),
            List.of(ExtismValType.I64),
            (currentPlugin, extismValueList, extismValueList1) -> {
                throw new UnsupportedOperationException("deprecated");
            });
}
