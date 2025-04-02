package com.dylibso.mcpx4j.core.builtins;

import com.dylibso.mcpx4j.core.HttpClient;
import com.dylibso.mcpx4j.core.JsonDecoder;
import java.nio.charset.StandardCharsets;

public class McpRunSearchTool extends McpxBuiltInTool {
    private static final String NAME = "search";
    private static final String DESCRIPTION = "Search for tools that might help solve the user's problem. Use single-word searches first (like \"image\" or \"pdf\"). If no results match, try one more related word. Never combine multiple terms in the first search.\n"
            +
            "\n" +
            "For each result found, tell the user to visit https://mcp.run/{owner}/{name} to install it.\n" +
            "\n" +
            "If no tools are found, suggest the user create one at mcp.run.\n" +
            "\n" +
            "Search proactively - don't wait for users to ask about tools. Anytime you're helping with a task that external code could enhance, do a search.\n";
    //language=json
    private static final String INPUT_SCHEMA = "{\n" +
            "      \"type\": \"object\",\n" +
            "      \"properties\": {\n" +
            "        \"q\": {\n" +
            "          \"type\": \"string\",\n" +
            "          \"description\": \"The query of terms to search the mcp.run API for servlets. " +
            "This query string supports:\\n\\n" +
            "* Regular word search: 'fetch markdown'  (finds documents containing both words)\\n" +
            "* Phrase search: '\\\"hello world\\\"' (finds exact phrase)\\n" +
            "* Prefix search: 'fetch*' (finds 'fetch', 'fetching', etc.)\\n" +
            "* Mixed search: 'api \\\"hello world\\\"'\\n" +
            "* Negation: '!javascript' (excludes documents with 'javascript'\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"required\": []\n" +
            "    }";

    private final HttpClient httpClient;
    private final JsonDecoder jsonDecoder;

    public McpRunSearchTool(HttpClient httpClient, JsonDecoder jsonDecoder) {
        super(NAME, DESCRIPTION, INPUT_SCHEMA);
        this.httpClient = httpClient;
        this.jsonDecoder = jsonDecoder;
    }

    @Override
    public String call(String jsonInput) {
        String query = jsonDecoder.parseSearchRequest(jsonInput.getBytes(StandardCharsets.UTF_8));
        return new String(httpClient.search(query), StandardCharsets.UTF_8);
    }
}
