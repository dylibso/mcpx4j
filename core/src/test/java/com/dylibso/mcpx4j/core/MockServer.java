package com.dylibso.mcpx4j.core;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class MockServer {
    static HttpServer installations(InetSocketAddress address, String profileId) throws IOException {
        byte[] mockBody = MockServer.class.getClassLoader().getResourceAsStream("installations.json").readAllBytes();
        HttpServer server = HttpServer.create(address, 0);
        server.createContext("/api/profiles/~/" + profileId + "/installations", t -> {
            t.sendResponseHeaders(200, mockBody.length);
            OutputStream os = t.getResponseBody();
            os.write(mockBody);
            os.close();
        });
        return server;
    }
    static HttpServer fetch(InetSocketAddress address, String profileId) throws IOException {
        byte[] wasm = MockServer.class.getClassLoader().getResourceAsStream("fetch.wasm").readAllBytes();
        var caddress = "abcdefg";

        HttpServer server = installations(address, profileId);
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
