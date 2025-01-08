package com.dylibso.mcpx4j.core;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JacksonDecoder implements JsonDecoder {
    private final ObjectMapper mapper;

    public JacksonDecoder() {
        this.mapper = new ObjectMapper()
                .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
                .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Map<String, ServletInstall> servletInstalls(byte[] bytes) {
        try {
            var installs = mapper.readTree(bytes).get("installs");
            var typeToken = new TypeReference<List<ServletInstall>>() {
            };
            var servletInstalls = mapper.treeToValue(installs, typeToken);
            return servletInstalls.stream()
                    .collect(Collectors.toMap(ServletInstall::name, s -> s));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public List<McpxToolDescriptor> toolDescriptors(byte[] bytes) {
        try {
            var root = mapper.readTree(bytes);
            var tools = root.get("tools");
            if (tools == null) {
                // Then it must be a single-tool servlet
                return List.of(McpxToolReader.readToolDescriptor(root));
            } else {
                var result = new ArrayList<McpxToolDescriptor>();
                for (JsonNode tool : tools) {
                    result.add(McpxToolReader.readToolDescriptor(tool));
                }
                return result;
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static class McpxToolReader {
        private static McpxToolDescriptor readToolDescriptor(JsonNode toolObject) {
            var name = toolObject.get("name").asText();
            var description = toolObject.get("description").asText();
            var inputSchema = toolObject.get("inputSchema").toPrettyString();
            return new McpxToolDescriptor(name, description, inputSchema);
        }
    }

    @Override
    public String parseSearchRequest(byte[] bytes) {
        try {
            return new ObjectMapper()
                .readTree(bytes)
                .get("params")
                .get("arguments")
                .get("q").asText();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
