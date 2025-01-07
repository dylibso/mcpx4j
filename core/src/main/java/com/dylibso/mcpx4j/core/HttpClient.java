package com.dylibso.mcpx4j.core;

import org.extism.sdk.chicory.HttpClientAdapter;

import java.net.URI;
import java.util.Map;

public class HttpClient {
    private final String baseUrl;
    private final String apiKey;
    private final HttpClientAdapter httpAdapter;
    private final JsonDecoder jsonDecoder;

    HttpClient(
            String baseUrl,
            String apiKey,
            HttpClientAdapter httpAdapter,
            JsonDecoder jsonDecoder) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.httpAdapter = httpAdapter;
        this.jsonDecoder = jsonDecoder;
    }

    // GET /api/profiles/~/default/installations
    public Map<String, ServletInstall> installations(String profileId) {
        var uri = URI.create(baseUrl + "/api/profiles/~/" + profileId + "/installations");
        byte[] bytes = httpAdapter.request("GET", uri, Map.of("Cookie", "sessionId=" + apiKey), new byte[0]);
        return jsonDecoder.servletInstalls(bytes);
    }

    // GET /c/{contentAddress}
    public byte[] fetch(ServletInstall servletInstall) {
        var uri = URI.create(baseUrl + "/api/c/" + servletInstall.servlet().meta().lastContentAddress());
        return httpAdapter.request("GET", uri, Map.of("Cookie", "sessionId=" + apiKey), new byte[0]);
    }

    // GET /api/servlets
    public byte[] search(String query) {
        var uri = URI.create(baseUrl + "/api/servlets");
        return httpAdapter.request("GET", uri, Map.of("Cookie", "sessionId=" + apiKey), new byte[0]);
    }

}
