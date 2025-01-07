package com.dylibso.mcpx4j.core;

import java.util.Objects;

public final class ServletInstall {
    String name;
    ServletDescriptor servlet;
    ServletSettings settings;

    ServletInstall() {}

    public ServletInstall(String name, ServletDescriptor servlet, ServletSettings settings) {
        this.name = name;
        this.servlet = servlet;
        this.settings = settings;
    }

    public String name() {
        return name;
    }

    public ServletDescriptor servlet() {
        return servlet;
    }

    public ServletSettings settings() {
        return settings;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ServletInstall) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.servlet, that.servlet) &&
                Objects.equals(this.settings, that.settings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, servlet, settings);
    }

    @Override
    public String toString() {
        return "ServletInstall[" +
                "name=" + name + ", " +
                "servlet=" + servlet + ", " +
                "settings=" + settings + ']';
    }
}
