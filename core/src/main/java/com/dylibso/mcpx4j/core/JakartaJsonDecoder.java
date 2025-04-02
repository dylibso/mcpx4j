package com.dylibso.mcpx4j.core;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JakartaJsonDecoder implements JsonDecoder {
    @Override
    public Map<String, ServletInstall> servletInstalls(byte[] bytes) {
        var jsonObject = Json.createReader(new ByteArrayInputStream(bytes)).readObject();
        var installs = jsonObject.getJsonArray("installs");
        return installs.stream()
                .map(JsonValue::asJsonObject)
                .map(ServletInstallReader::read)
                .collect(Collectors.toMap(ServletInstall::name, s -> s));
    }

    @Override
    public List<McpxToolDescriptor> toolDescriptors(byte[] bytes) {
        var jsonObject = Json.createReader(new ByteArrayInputStream(bytes)).readObject();
        var tools = jsonObject.getJsonArray("tools");
        if (tools == null) {
            // Then it must be a single-tool servlet
            return List.of(McpxToolReader.readToolDescriptor(jsonObject));
        } else {
            return tools.stream().map(JsonValue::asJsonObject)
                    .map(McpxToolReader::readToolDescriptor).collect(Collectors.toList());
        }
    }

    @Override
    public String parseSearchRequest(byte[] bytes) {
        var jsonObject = Json.createReader(new ByteArrayInputStream(bytes)).readObject();
        return jsonObject.getJsonObject("params").getJsonObject("arguments").getString("q");
    }

    @Override
    public ServletOAuthInfo oauth(byte[] bytes) {
        var jsonObject = Json.createReader(new ByteArrayInputStream(bytes)).readObject().getJsonObject("oauth_info");
        var configName = jsonObject.getString("config_name");
        var accessToken = jsonObject.getString("access_token");
        return new ServletOAuthInfo(configName, accessToken);
    }

    private static class McpxToolReader {
        private static McpxToolDescriptor readToolDescriptor(JsonObject toolObject) {
            var name = toolObject.getString("name");
            var description = toolObject.getString("description");
            var inputSchema = toolObject.getJsonObject("inputSchema");
            return new McpxToolDescriptor(name, description, inputSchema.toString());
        }
    }

}
