package com.dylibso.mcpx4j.core;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

public class ServletInstallReader {

    static ServletInstall read(JsonObject installObj) {
        String name = installObj.getString("name");
        JsonObject servlet = installObj.getJsonObject("servlet");
        JsonObject settings = installObj.getJsonObject("settings");

        ServletSettings servletSettings = readSettings(settings);
        ServletDescriptor servletDescriptor = readDescriptor(servlet);
        ServletInstall servletInstall = new ServletInstall(name, servletDescriptor, servletSettings);

        return servletInstall;
    }

    private static ServletDescriptor readDescriptor(JsonObject servlet) {
        return new ServletDescriptor(
                servlet.getString("slug"),
                OffsetDateTime.parse(servlet.getString("created_at")),
                OffsetDateTime.parse(servlet.getString("modified_at")),
                readServletMeta(servlet.getJsonObject("meta")));
    }

    private static ServletDescriptor.Meta readServletMeta(JsonObject meta) {
        return new ServletDescriptor.Meta(
                meta.getJsonObject("schema").toString(),
                meta.getString("lastContentAddress"));
    }

    static ServletSettings readSettings(JsonObject settings) {
        ServletSettings servletSettings = new ServletSettings(
                asMap(settings.getJsonObject("config")),
                readPermissions(settings.getJsonObject("permissions")));
        return servletSettings;
    }

    static ServletSettings.Permissions readPermissions(JsonObject permissions) {
        if (permissions == null) {
            return new ServletSettings.Permissions(
                    readPermissionsNetwork(null),
                    new ServletSettings.Permissions.FileSystem(Map.of())
            );
        }
        return new ServletSettings.Permissions(
                readPermissionsNetwork(permissions.getJsonObject("network")),
                readPermissionsFilesystem(permissions.getJsonObject("filesystem"))
        );
    }

    private static ServletSettings.Permissions.FileSystem readPermissionsFilesystem(JsonObject filesystem) {
        if (filesystem == null) {
            return new ServletSettings.Permissions.FileSystem(Map.of());
        }
        return new ServletSettings.Permissions.FileSystem(
                asMap(filesystem.getJsonObject("volumes")));
    }

    private static ServletSettings.Permissions.Network readPermissionsNetwork(JsonObject network) {
        if (network == null) {
            return new ServletSettings.Permissions.Network();
        }
        JsonArray domains = network.getJsonArray("domains");

        if (domains == null) {
            return new ServletSettings.Permissions.Network();
        }
        String[] ss = new String[domains.size()];
        for (int i = 0; i < domains.size(); i++) {
            ss[i] = domains.getString(i);
        }
        return new ServletSettings.Permissions.Network(ss);
    }

    private static Map<String, String> asMap(JsonObject config) {
        if (config == null) {
            return Map.of();
        }
        var map = new HashMap<String, String>();
        for (var key : config.keySet()) {
            map.put(key, config.getString(key));
        }
        return map;
    }
}