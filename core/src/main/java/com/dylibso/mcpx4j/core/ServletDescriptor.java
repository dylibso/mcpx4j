package com.dylibso.mcpx4j.core;

import java.time.OffsetDateTime;
import java.util.Objects;

public class ServletDescriptor {
    String slug;
    OffsetDateTime created_at;
    OffsetDateTime modified_at;
    Meta meta;
    boolean has_client;

    ServletDescriptor() {
    }

    public ServletDescriptor(String slug, OffsetDateTime createdAt, OffsetDateTime modifiedAt, Meta meta, boolean hasClient) {
        this.slug = slug;
        this.created_at = createdAt;
        this.modified_at = modifiedAt;
        this.meta = meta;
        this.has_client = hasClient;
    }

    public String slug() {
        return slug;
    }

    public OffsetDateTime createdAt() {
        return created_at;
    }

    public OffsetDateTime modifiedAt() {
        return modified_at;
    }

    public Meta meta() {
        return meta;
    }

    public boolean hasClient() {
        return has_client;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ServletDescriptor))
            return false;
        ServletDescriptor that = (ServletDescriptor) o;
        return Objects.equals(slug, that.slug) && Objects.equals(created_at, that.created_at)
                && Objects.equals(modified_at, that.modified_at);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slug, created_at, modified_at);
    }

    @Override
    public String toString() {
        return "ServletDescriptor{" +
                "slug='" + slug + '\'' +
                ", createdAt=" + created_at +
                ", modifiedAt=" + modified_at +
                ", hasClient=" + has_client +
                '}';
    }

    public static class Meta {
        String schema;
        String lastContentAddress;

        Meta() {
        }

        public Meta(String schema, String lastContentAddress) {
            this.schema = schema;
            this.lastContentAddress = lastContentAddress;
        }

        void setSchema(String schema) {
            this.schema = schema;
        }

        public String schema() {
            return schema;
        }

        public String lastContentAddress() {
            return lastContentAddress;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Meta))
                return false;
            Meta meta = (Meta) o;
            return Objects.equals(schema, meta.schema) && Objects.equals(lastContentAddress, meta.lastContentAddress);
        }

        @Override
        public int hashCode() {
            return Objects.hash(schema, lastContentAddress);
        }

        @Override
        public String toString() {
            return "Meta{" +
                    "schema='" + schema + '\'' +
                    ", lastContentAddress='" + lastContentAddress + '\'' +
                    '}';
        }
    }
}
