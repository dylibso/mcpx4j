package com.dylibso.mcpx4j.core;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public final class ServletSettings {
    private Map<String, String> config;
    private Permissions permissions;

    ServletSettings() {
    }

    public ServletSettings(Map<String, String> config, Permissions permissions) {
        this.config = config;
        this.permissions = permissions;
    }

    public Map<String, String> config() {
        return config;
    }

    public Permissions permissions() {
        return permissions;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (ServletSettings) obj;
        return Objects.equals(this.config, that.config) &&
                Objects.equals(this.permissions, that.permissions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(config, permissions);
    }

    @Override
    public String toString() {
        return "ServletSettings[" +
                "config=" + config + ", " +
                "permissions=" + permissions + ']';
    }

    public static final class Permissions {
        Network network;
        FileSystem filesystem;

        Permissions() {
        }

        public Permissions(Network network, FileSystem filesystem) {
            this.network = network;
            this.filesystem = filesystem;
        }

        public Network network() {
            return network;
        }

        public FileSystem filesystem() {
            return filesystem;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (obj == null || obj.getClass() != this.getClass())
                return false;
            var that = (Permissions) obj;
            return Objects.equals(this.network, that.network) &&
                    Objects.equals(this.filesystem, that.filesystem);
        }

        @Override
        public int hashCode() {
            return Objects.hash(network, filesystem);
        }

        @Override
        public String toString() {
            return "Permissions[" +
                    "network=" + network + ", " +
                    "filesystem=" + filesystem + ']';
        }

        public static final class Network {
            String[] domains;

            Network() {
            }

            public Network(String... domains) {
                this.domains = domains;
            }

            public String[] domains() {
                return domains;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == this)
                    return true;
                if (obj == null || obj.getClass() != this.getClass())
                    return false;
                var that = (Network) obj;
                return Arrays.equals(this.domains, that.domains);
            }

            @Override
            public int hashCode() {
                return Arrays.hashCode(domains);
            }

            @Override
            public String toString() {
                return "Network[" +
                        "domains=" + Arrays.toString(domains) + ']';
            }

        }

        public static final class FileSystem {
            Map<String, String> volumes;

            FileSystem() {
            }

            public FileSystem(Map<String, String> volumes) {
                this.volumes = volumes;
            }

            public Map<String, String> volumes() {
                return volumes;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == this)
                    return true;
                if (obj == null || obj.getClass() != this.getClass())
                    return false;
                var that = (FileSystem) obj;
                return Objects.equals(this.volumes, that.volumes);
            }

            @Override
            public int hashCode() {
                return Objects.hash(volumes);
            }

            @Override
            public String toString() {
                return "FileSystem[" +
                        "volumes=" + volumes + ']';
            }

        }
    }
}
