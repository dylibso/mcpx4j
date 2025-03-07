package com.dylibso.mcpx4j.core;

import com.dylibso.chicory.wasm.Parser;
import com.dylibso.chicory.wasm.WasmModule;
import com.sun.net.httpserver.HttpServer;
import org.extism.sdk.chicory.JdkHttpClientAdapter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class HttpClientIT {
    private static Stream<Arguments> provideJsonDecoder() {
        return Stream.of(
                Arguments.of(new JacksonDecoder()),
                Arguments.of(new JakartaJsonDecoder()));
    }

    @ParameterizedTest
    @MethodSource("provideJsonDecoder")
    void installations(JsonDecoder jsonDecoder) throws IOException {
        var profileId = "~/default";

        var address = new InetSocketAddress(8080);
        HttpServer server = MockServer.installations(address);
        String baseUrl = "http://" + address.getHostName() + ":" + address.getPort();
        try {

            server.start();

            var httpClient = new HttpClient(
                    baseUrl, "my-key", new JdkHttpClientAdapter(), jsonDecoder);
            var installations = httpClient.installations(profileId);
            assertFalse(installations.isEmpty());
            var cfg = installations.get("fetch");
            assertNotNull(cfg);
            ServletSettings settings = cfg.settings();
            assertEquals(new ServletSettings(
                    Map.of("username", "foo"),
                    new ServletSettings.Permissions(
                            new ServletSettings.Permissions.Network("*.example.com"),
                            new ServletSettings.Permissions.FileSystem(Map.of("/home/foo", "${HOME}")),
                            true
                    )), settings);
        } finally {
            server.stop(0);
        }
    }

    @ParameterizedTest
    @MethodSource("provideJsonDecoder")
    void fetch(JsonDecoder jsonDecoder) throws IOException {
        var profileId = "~/default";

        var address = new InetSocketAddress(8080);
        HttpServer server = MockServer.fetch(address);
        String baseUrl = "http://" + address.getHostName() + ":" + address.getPort();

        try {
            server.start();

            var httpClient = new HttpClient(
                    baseUrl, "my-key", new JdkHttpClientAdapter(), jsonDecoder);
            var installations = httpClient.installations(profileId);
            var binary = httpClient.fetch(installations.get("fetch"));

            WasmModule module = Parser.parse(binary);

        } finally {
            server.stop(0);
        }

    }
}