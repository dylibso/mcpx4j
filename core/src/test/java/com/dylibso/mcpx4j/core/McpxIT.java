package com.dylibso.mcpx4j.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class McpxIT {
    private static Stream<Arguments> provideJsonDecoder() {
        return Stream.of(
                Arguments.of(new JacksonDecoder()),
                Arguments.of(new JakartaJsonDecoder()));
    }

    @ParameterizedTest
    @MethodSource("provideJsonDecoder")
    void installations(JsonDecoder jsonDecoder) throws IOException {
        var profileId = "default";

        var address = new InetSocketAddress(8080);
        var server = MockServer.fetch(address);
        String baseUrl = "http://" + address.getHostName() + ":" + address.getPort();
        try {
            server.start();

            var mcpx = Mcpx.forApiKey("my-key")
                    .withBaseUrl(baseUrl)
                    .withProfile(profileId)
                    .withJsonDecoder(jsonDecoder)
                    .withServletOptions(
                            McpxServletOptions.builder()
                                    .withOAuthAutoRefresh().build())
                    .build();
            mcpx.refreshInstallations();
            var servletFactory = mcpx.get("fetch");

            assertNotNull(servletFactory);
            McpxServlet servlet = servletFactory.create();

            // {
            //    "method": "tools/call",
            //    "params": {
            //        "name": "fetch",
            //        "arguments": {
            //
            //        }
            //    }
            //}

            ObjectMapper mapper = new ObjectMapper();

            String targetUrl = "https://www.example.com";
            ObjectNode root = mapper.createObjectNode()
                    .put("method", "tools/call");
            root.putObject("params")
                    .put("name", "fetch")
                    .putObject("arguments").put("url", targetUrl);

            String result = servlet.tools().get("fetch").call(root.toString());
            assertNotNull(result);

            var tree = mapper.readTree(result);
            var firstContentItem = tree.get("content").get(0);
            assertEquals("text/markdown", firstContentItem.get("mimeType").asText());
            assertTrue(firstContentItem.get("text").asText().startsWith("Example Domain"));

        } finally {
            server.stop(0);
        }
    }

}
