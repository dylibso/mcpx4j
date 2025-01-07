package com.dylibso.mcpx4j.core;

import java.time.OffsetDateTime;
import java.util.Objects;

public class ServletDescriptor {
    String slug;
    OffsetDateTime createdAt;
    OffsetDateTime modifiedAt;
    Meta meta;

    ServletDescriptor() {}

    public ServletDescriptor(String slug, OffsetDateTime createdAt, OffsetDateTime modifiedAt, Meta meta) {
        this.slug = slug;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.meta = meta;
    }

    public String slug() {
        return slug;
    }

    public OffsetDateTime createdAt() {
        return createdAt;
    }

    public OffsetDateTime modifiedAt() {
        return modifiedAt;
    }

    public Meta meta() {
        return meta;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ServletDescriptor)) return false;
        ServletDescriptor that = (ServletDescriptor) o;
        return Objects.equals(slug, that.slug) && Objects.equals(createdAt, that.createdAt) && Objects.equals(modifiedAt, that.modifiedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slug, createdAt, modifiedAt);
    }

    @Override
    public String toString() {
        return "ServletDescriptor{" +
                "slug='" + slug + '\'' +
                ", createdAt=" + createdAt +
                ", modifiedAt=" + modifiedAt +
                '}';
    }

    public static class Meta {
        String schema;
        String lastContentAddress;

        Meta() {}

        public Meta(String schema, String lastContentAddress) {
            this.schema = schema;
            this.lastContentAddress = lastContentAddress;
        }

        void setSchema(Object schema) {
            this.schema = schema.toString();
        }

        public String schema() {
            return schema;
        }

        public String lastContentAddress() {
            return lastContentAddress;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Meta)) return false;
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
