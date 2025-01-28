package com.dylibso.mcpx4j.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class McpxTest {
    @Test
    void testSlug() {
        var cases = List.of(
                Map.entry(s(), "~/default"),
                Map.entry(s("default"), "~/default"),
                Map.entry(s("~", "default"), "~/default"),
                Map.entry(s("user", "foo"), "user/foo")
        );

        for (var c : cases) {
            var input = c.getKey();
            var expected = c.getValue();
            var actual = Mcpx.Builder.profileIdToSlug(input);
            assertEquals(expected, actual);
        }

        var errors = List.of(
                s("~", "abc"),
                s("x"),
                s("x", "y", "z"),
                s("x/y"),
                s("x/y/z"),
                s("x/y", "z"),
                s("x", "y/z")
        );

        for (var e : errors) {
            assertThrows(IllegalArgumentException.class, () -> Mcpx.Builder.profileIdToSlug(e), Arrays.toString(e));
        }
    }

    private String[] s(String... ss) {
        return ss;
    }
}