package com.dylibso.mcpx4j.core;

import java.util.Objects;

public final class McpxToolDescriptor {
    String name;
    String description;
    String inputSchema;

    McpxToolDescriptor() {
    }

    McpxToolDescriptor(String name, String description, String inputSchema) {
        this.name = name;
        this.description = description;
        this.inputSchema = inputSchema;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public String inputSchema() {
        return inputSchema;
    }

    void setInputSchema(Object inputSchema) {
        this.inputSchema = inputSchema.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof McpxToolDescriptor))
            return false;
        McpxToolDescriptor that = (McpxToolDescriptor) o;
        return Objects.equals(name, that.name) && Objects.equals(description, that.description)
                && Objects.equals(inputSchema, that.inputSchema);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, inputSchema);
    }

    @Override
    public String toString() {
        return "McpxToolDescriptor{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", inputschema='" + inputSchema + '\'' +
                '}';
    }
}
