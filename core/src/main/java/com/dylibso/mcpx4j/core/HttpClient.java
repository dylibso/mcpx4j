package com.dylibso.mcpx4j.core;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.extism.sdk.chicory.http.HttpClientAdapter;

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
        var uri = URI.create(baseUrl + "/api/profiles/" + profileId + "/installations");
        byte[] bytes = httpAdapter.request("GET", uri, Map.of("Cookie", "sessionId=" + apiKey), new byte[0]);
        if (httpAdapter.statusCode() != 200) {
            throw new HttpClientException("Failed to fetch installations: " + httpAdapter.statusCode());
        }
        return jsonDecoder.servletInstalls(bytes);
    }

    // GET /api/profiles/{user}/{profile}/installations/{installation}/oauth
    public ServletOAuth oauth(String profileId, ServletInstall servletInstall) {
        var uri = URI.create(baseUrl + "/api/profiles/" + profileId + "/installations/" + servletInstall.name() + "/oauth");
        byte[] bytes = httpAdapter.request("GET", uri, Map.of("Cookie", "sessionId=" + apiKey), new byte[0]);
        if (httpAdapter.statusCode() != 200) {
            throw new HttpClientException("Failed to fetch OAuth info: " + httpAdapter.statusCode());
        }
        var headers = httpAdapter.headers();
        int maxAge = 0;
        long time = System.currentTimeMillis();
        List<String> cacheControl = headers.get("Cache-Control");
        if (cacheControl != null) {
            for (String cc : cacheControl) {
                var tok = new StringTokenizer(cc, ",=");
                if (tok.nextToken().equals("max-age")) {
                    maxAge = Integer.parseInt(tok.nextToken());
                }
            }
        }

        ServletOAuthInfo oauthInfo = jsonDecoder.oauth(bytes);
        ServletOAuth servletOAuth = new ServletOAuth(oauthInfo, time + maxAge, maxAge);

        return servletOAuth;
    }

    // GET /c/{contentAddress}
    public byte[] fetch(ServletInstall servletInstall) {
        var uri = URI.create(baseUrl + "/api/c/" + servletInstall.servlet().meta().lastContentAddress());
        byte[] bytes = httpAdapter.request("GET", uri, Map.of("Cookie", "sessionId=" + apiKey), new byte[0]);
        if (httpAdapter.statusCode() != 200) {
            throw new HttpClientException("Failed to fetch installations: " + httpAdapter.statusCode());
        }
        return bytes;
    }

    // GET /api/servlets
    public byte[] search(String query) {
        var uri = URI.create(baseUrl + "/api/servlets");
        byte[] bytes = httpAdapter.request("GET", uri, Map.of("Cookie", "sessionId=" + apiKey), new byte[0]);
        if (httpAdapter.statusCode() != 200) {
            throw new HttpClientException("Failed to fetch installations: " + httpAdapter.statusCode());
        }
        return bytes;
    }

}
