package com.dylibso.mcpx4j.examples.gemini

import com.dylibso.mcpx4j.core.*
import com.google.ai.client.generativeai.type.*
import org.extism.sdk.chicory.*

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
                            .withJsonCodec(JacksonJsonCodec())
                            .withClientAdapter(HttpUrlConnectionClientAdapter()).build())
                        // Configure an alternative, Android-specific logger
                        .withChicoryLogger(AndroidLogger("mcpx4j-runtime"))
                        .build())
                // Configure also the MCPX4J HTTP client to use
                // the Android-compatible implementation
                .withHttpClientAdapter(HttpUrlConnectionClientAdapter())
                .build()

        // Refresh once the list of installations.
        // This can be also scheduled for periodic refresh.
        mcpx.refreshInstallations("default")
        val servlets = mcpx.servlets()

        // Extract the metadata of each `McpxTool` into a `FunctionDeclaration`
        val functionDeclarations =
            servlets.flatMap {
                it.tools().map { it.value.name() to toFunctionDeclaration(it.value) } }.toMap()
        // Create a map name -> McpxTool for quicker lookup
        val mcpxTools =
            servlets.flatMap {
                it.tools().map { it.value.name() to it.value } }.toMap()
        return FunctionRepository(functionDeclarations, mcpxTools)
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
