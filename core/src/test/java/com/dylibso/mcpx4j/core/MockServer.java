package com.dylibso.mcpx4j.core;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class MockServer {
    static HttpServer installations(InetSocketAddress address) throws IOException {
        byte[] mockBody = MockServer.class.getClassLoader().getResourceAsStream("installations.json").readAllBytes();
        HttpServer server = HttpServer.create(address, 0);

        HttpHandler httpHandler = t -> {
            t.sendResponseHeaders(200, mockBody.length);
            OutputStream os = t.getResponseBody();
            os.write(mockBody);
            os.close();
        };

        server.createContext("/api/profiles/~/default/installations", httpHandler);
        server.createContext("/api/profiles/foo/bar/installations", httpHandler);
        server.createContext("/api/profiles/~/default/installations/fetch/oauth", t -> {
            // {
            //   "oauth_info": {
            //      "config_name": "OAUTH_TOKEN",
            //      "access_token": "ABCDFGHILMN123456789",
            //   }
            // }

            byte[] resp = ("{\n" +
                    "  \"oauth_info\": {\n" +
                    "     \"config_name\": \"OAUTH_TOKEN\",\n" +
                    "     \"access_token\": \"ABCDFGHILMN123456789\"\n" +
                    "  }\n" +
                    "}").getBytes(StandardCharsets.UTF_8);

            t.sendResponseHeaders(200, resp.length);
            OutputStream responseBody = t.getResponseBody();

            responseBody.write(resp);
            responseBody.close();
        });

        return server;
    }

    static HttpServer fetch(InetSocketAddress address) throws IOException {
        byte[] wasm = MockServer.class.getClassLoader().getResourceAsStream("fetch.wasm").readAllBytes();
        var caddress = "abcdefg";

        HttpServer server = installations(address);
        server.createContext("/api/c/" + caddress, t -> {
            byte[] bytes = wasm;
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();
        });

        return server;
    }

}
