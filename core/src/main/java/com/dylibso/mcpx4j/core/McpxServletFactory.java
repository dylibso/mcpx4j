package com.dylibso.mcpx4j.core;

import com.dylibso.chicory.wasi.WasiOptions;
import org.extism.sdk.chicory.ExtismHostFunction;
import org.extism.sdk.chicory.ExtismValType;
import org.extism.sdk.chicory.Manifest;
import org.extism.sdk.chicory.ManifestWasm;
import org.extism.sdk.chicory.Plugin;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class McpxServletFactory {
    private final ServletInstall install;
    private final Manifest manifest;
    private final McpxServletOptions config;
    private final JsonDecoder jsonDecoder;

    public McpxServletFactory(ServletInstall install, Manifest manifest, McpxServletOptions config, JsonDecoder jsonDecoder) {
        this.install = install;
        this.manifest = manifest;
        this.config = config;
        this.jsonDecoder = jsonDecoder;
    }

    public ServletInstall install() {
        return install;
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
                .withHostFunctions(DEPRECATED_CONFIG_GET.get())
                .build();

        var bytes = plugin.call("describe", new byte[0]);
        var descriptors = jsonDecoder.toolDescriptors(bytes);
        Map<String, McpxTool> tools = descriptors.stream()
                .map(descriptor -> new McpxPluginTool(plugin, descriptor))
                .collect(Collectors.toMap(McpxPluginTool::name, tool -> tool));
        return new McpxServlet(this.install.name(), this.install, tools);
    }

    public static McpxServletFactory create(byte[] bytes, String name, ServletInstall install, McpxServletOptions config, JsonDecoder jsonDecoder) {
        var wasm = ManifestWasm.fromBytes(bytes).build();
        Manifest.Options opts = new Manifest.Options()
                .withConfig(install.settings().config())
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

        return new McpxServletFactory(install, manifest, config, jsonDecoder);
    }

    private static final Supplier<ExtismHostFunction> DEPRECATED_CONFIG_GET = () -> ExtismHostFunction.of(
            "config_get",
            List.of(ExtismValType.I64),
            List.of(ExtismValType.I64),
            (currentPlugin, extismValueList, extismValueList1) -> {
                throw new UnsupportedOperationException("deprecated");
            });
}
