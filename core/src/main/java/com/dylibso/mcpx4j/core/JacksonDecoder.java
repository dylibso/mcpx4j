package com.dylibso.mcpx4j.core;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
                .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule())
                .addHandler(new DeserializationProblemHandler() {
                    @Override
                    public Object handleUnexpectedToken(DeserializationContext ctxt,
                                                        JavaType targetType,
                                                        JsonToken t,
                                                        JsonParser p,
                                                        String failureMsg) throws IOException {
                        // Check if we're handling the schema field in ServletDescriptor.Meta
                        if (p.getCurrentName() != null &&
                                p.getCurrentName().equals("schema") &&
                                p.getParsingContext().getParent().getCurrentName() != null &&
                                p.getParsingContext().getParent().getCurrentValue() instanceof ServletDescriptor.Meta) {
                            // Return the raw JSON as a string
                            return p.readValueAsTree().toString();
                        }
                        // Otherwise use default handling
                        return NOT_HANDLED;
                    }
                });
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

    @Override
    public ServletOAuthInfo oauth(byte[] bytes) {
        try {
            var installs = mapper.readTree(bytes).get("oauth_info");
            return mapper.treeToValue(installs, ServletOAuthInfo.class);
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

    private static class ServletDescriptorMetaModule extends Module {
        @Override
        public String getModuleName() {
            return "ServletDescriptorMetaModule";
        }

        @Override
        public Version version() {
            return Version.unknownVersion();
        }

        @Override
        public void setupModule(SetupContext setupContext) {
            // keep ServletDescriptor.Meta.schema as string
            setupContext.addDeserializationProblemHandler(new DeserializationProblemHandler() {
                @Override
                public Object handleUnexpectedToken(DeserializationContext ctxt,
                                                    JavaType targetType,
                                                    JsonToken t,
                                                    JsonParser p,
                                                    String failureMsg) throws IOException {
                    // Check if we're handling the schema field in ServletDescriptor.Meta
                    if (p.getCurrentName() != null &&
                            p.getCurrentName().equals("schema") &&
                            p.getParsingContext().getParent().getCurrentName() != null &&
                            p.getParsingContext().getParent().getCurrentValue() instanceof ServletDescriptor.Meta) {
                        // Return the raw JSON as a string
                        return p.getValueAsString();
                    }
                    // Otherwise use default handling
                    return NOT_HANDLED;
                }
            });

        }
    }
}
