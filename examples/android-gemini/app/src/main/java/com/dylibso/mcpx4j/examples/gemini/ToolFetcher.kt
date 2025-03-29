package com.dylibso.mcpx4j.examples.gemini

import com.dylibso.mcpx4j.core.*
import com.google.ai.client.generativeai.type.*
import org.extism.sdk.chicory.*
import org.json.JSONObject

object ToolFetcher {
    fun fetchFunctions(): FunctionRepository {
        val mcpx =
            // Configure the MCP.RUN Session
            Mcpx.forApiKey(BuildConfig.mcpRunKey)
                .withServletOptions(
                    McpxServletOptions.builder()
                        // Setup an HTTP client compatible with Android
                        // on the Chicory runtime
                        .withChicoryHttpConfig(HttpConfig.builder()
                            .withJsonCodec({ JacksonJsonCodec() })
                            .withClientAdapter({ HttpUrlConnectionClientAdapter() }).build())
                        // Configure an alternative, Android-specific logger
                        .withChicoryLogger(AndroidLogger("mcpx4j-runtime"))
                        .build())
                // Configure also the MCPX4J HTTP client to use
                // the Android-compatible implementation
                .withHttpClientAdapter(HttpUrlConnectionClientAdapter())
                .withProfile("telegram")
                .build()

        // Refresh once the list of installations.
        // This can be also scheduled for periodic refresh.
        mcpx.refreshInstallations()

        // Extract the metadata of each `McpxTool` into a `FunctionDeclaration`
        val functionDeclarations =
            mcpx.servletFactories().toList()
                .flatMap  { toFunctionDeclarationList(it.schema()) }
                .toList()
        // Create a map name -> McpxTool for quicker lookup
        val mcpxTools =
            mcpx.servletFactories().flatMap {
                it.tools().map { it.value.name() to it.value } }.toMap()
        return FunctionRepository(functionDeclarations, mcpxTools)
    }

    private fun toFunctionDeclarationList(toolSchemas: String): List<FunctionDeclaration> {
        val tools = JSONObject(toolSchemas).getJSONObject("tools")
        val declarations = mutableListOf<FunctionDeclaration>()
        for (name in tools.keys()) {

            val tool = tools.getJSONObject(name)
            val parsedSchema = ParsedSchema.parseObject(tool.getJSONObject("inputSchema"))
            val f = defineFunction(
                name = tool.getString("name"),
                description = tool.getString("description"),
                parameters = parsedSchema.parameters,
                requiredParameters = parsedSchema.requiredParameters
            )
            declarations.add(f)
        }
        return declarations;
    }

    private fun toFunctionDeclaration(tool: McpxTool): FunctionDeclaration {
        val parsedSchema = ParsedSchema.parse(tool.inputSchema())
        val f = defineFunction(
            name = tool.name(),
            description = tool.description(),
            parameters = parsedSchema.parameters,
            requiredParameters = parsedSchema.requiredParameters
        )
        return f
    }
}
