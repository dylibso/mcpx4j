package com.dylibso.mcpx4j.core;

import com.dylibso.mcpx4j.core.builtins.McpRunServlet;
import org.extism.sdk.chicory.HttpClientAdapter;
import org.extism.sdk.chicory.JdkHttpClientAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Mcpx {
    public static class Builder {
        private final String apiKey;
        private String baseUrl = DEFAULT_BASE_URL;
        private JsonDecoder jsonDecoder;
        private HttpClientAdapter httpClientAdapter;
        private McpxServletOptions config;
        private String profileSlug = "~/default";

        public Builder(String apiKey) {
            Objects.requireNonNull(apiKey, "apiKey is required");
            this.apiKey = apiKey;
        }

        public Builder withBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder withJsonDecoder(JsonDecoder jsonDecoder) {
            this.jsonDecoder = jsonDecoder;
            return this;
        }

        public Builder withHttpClientAdapter(HttpClientAdapter httpClientAdapter) {
            this.httpClientAdapter = httpClientAdapter;
            return this;
        }


        public Builder withServletOptions(McpxServletOptions config) {
            this.config = config;
            return this;
        }

        public Builder withProfile(String... profileSlug) {
            this.profileSlug = profileIdToSlug(profileSlug);
            return this;
        }

        public Mcpx build() {
            Objects.requireNonNull(baseUrl);
            if (config == null) {
                config = McpxServletOptions.builder().build();
            }
            if (jsonDecoder == null) {
                jsonDecoder = new JacksonDecoder();
            }
            if (httpClientAdapter == null) {
                httpClientAdapter = new JdkHttpClientAdapter();
            }
            return new Mcpx(apiKey, baseUrl, profileSlug, jsonDecoder, httpClientAdapter, config);
        }

        /**
         * @param profile is a list of components for a slug. It may be empty (then it defaults to `~/default`),
         *                it may contain one single value `"default"`.
         *                Otherwise, it must contain two components, the first being a username
         *                and the second being a profile name.
         *                Neither the profile name nor the username may contain a slash.
         *                It is also allowed to pass the values `"~", "default"`, yielding `~/default`.
         */
        static String profileIdToSlug(String... profile) {
            if (profile.length == 0) {
                return "~/default";
            }
            if (profile.length == 1) {
                if (profile[0].equals("default")) {
                    return "~/default";
                } else {
                    throw new IllegalArgumentException("Invalid profile path: " + Arrays.toString(profile));
                }
            }
            if (profile.length > 2) {
                throw new IllegalArgumentException("Invalid profile path: " + Arrays.toString(profile));
            }
            if (profile[0].equals("~") && !profile[1].equals("default") || profile[0].contains("/") ||  profile[1].contains("/")) {
                throw new IllegalArgumentException("Invalid profile path: " + Arrays.toString(profile));
            }
            return profile[0] + "/" + profile[1];
        }
    }

    private static final String DEFAULT_BASE_URL = "https://www.mcp.run";

    public static Builder forApiKey(String apiKey) {
        return new Builder(apiKey);
    }

    private final HttpClient client;
    private final ConcurrentHashMap<String, McpxServletFactory> plugins;
    private final ConcurrentHashMap<String, ServletInstall> servletInstalls;
    private final String profileSlug;
    private final McpxServletOptions config;
    private final List<McpxServlet> builtIns;
    private final JsonDecoder jsonDecoder;

    Mcpx(String apiKey, String baseUrl, String profileSlug, JsonDecoder jsonDecoder, HttpClientAdapter httpClientAdapter, McpxServletOptions config) {
        this.profileSlug = profileSlug;
        this.config = config;
        this.jsonDecoder = jsonDecoder;
        this.client = new HttpClient(baseUrl, apiKey, httpClientAdapter, jsonDecoder);
        this.plugins = new ConcurrentHashMap<>();
        this.servletInstalls = new ConcurrentHashMap<>();
        this.builtIns = List.of(new McpRunServlet(client, jsonDecoder));
    }

    public void refreshInstallations() {
        Map<String, ServletInstall> installations = client.installations(profileSlug);
        servletInstalls.putAll(installations);
    }

    public McpxServletFactory get(String name) {
        if (!plugins.containsKey(name)) {
            var install = servletInstalls.get(name);
            var bytes = client.fetch(install);
            plugins.put(name, McpxServletFactory.create(bytes, name, install, config, jsonDecoder));
        } else {
            // Check if the plugin has been updated
            var install = servletInstalls.get(name);
            McpxServletFactory mcpxServletFactory = plugins.get(name);
            if (install.servlet().createdAt().isAfter(mcpxServletFactory.install().servlet().createdAt())) {
                var bytes = client.fetch(install);
                plugins.put(name, McpxServletFactory.create(bytes, name, install, config, jsonDecoder));
            }
        }
        return plugins.get(name);
    }

    public Collection<McpxServlet> servlets() {
        var servlets = new ArrayList<>(builtIns);
        for (var name : servletInstalls.keySet()) {
            servlets.add(this.get(name).create());
        }
        return servlets;
    }

}
