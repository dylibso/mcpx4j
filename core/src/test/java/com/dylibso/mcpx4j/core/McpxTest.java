package com.dylibso.mcpx4j.core;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class McpxTest {
    @Test
    void testSlug() {
        var cases = List.of(
                Map.entry(s(), "~/default"),
                Map.entry(s("default"), "~/default"),
                Map.entry(s("~", "default"), "~/default"),
                Map.entry(s("abc"), "~/abc"),
                Map.entry(s("~", "abc"), "~/abc"),
                Map.entry(s("user", "foo"), "user/foo"),
                Map.entry(s("x/y"), "~/" + encode("x/y", UTF_8)),
                Map.entry(s("x/y/z"), "~/" + encode("x/y/z", UTF_8)),
                Map.entry(s("x/y", "z"), encode("x/y", UTF_8) + "/z"),
                Map.entry(s("x", "y/z"), "x/" + encode("y/z", UTF_8)));

        for (var c : cases) {
            var input = c.getKey();
            var expected = c.getValue();
            var actual = Mcpx.Builder.profileIdToSlug(input);
            assertEquals(expected, actual);
        }

        var errors = new String[][] { s("x", "y", "z") };

        for (var e : errors) {
            assertThrows(IllegalArgumentException.class, () -> Mcpx.Builder.profileIdToSlug(e), Arrays.toString(e));
        }
    }

    private String[] s(String... ss) {
        return ss;
    }
}
