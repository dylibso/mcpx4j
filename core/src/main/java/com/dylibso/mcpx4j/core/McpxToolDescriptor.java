package com.dylibso.mcpx4j.core;

import java.util.Objects;

public final class McpxToolDescriptor {
    String name;
    String description;
    String inputschema;

    McpxToolDescriptor() {}

    McpxToolDescriptor(String name, String description, String inputschema)  {
        this.name = name;
        this.description = description;
        this.inputschema = inputschema;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public String inputSchema() {
        return inputschema;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof McpxToolDescriptor)) return false;
        McpxToolDescriptor that = (McpxToolDescriptor) o;
        return Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(inputschema, that.inputschema);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, inputschema);
    }

    @Override
    public String toString() {
        return "McpxToolDescriptor{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", inputschema='" + inputschema + '\'' +
                '}';
    }
}

