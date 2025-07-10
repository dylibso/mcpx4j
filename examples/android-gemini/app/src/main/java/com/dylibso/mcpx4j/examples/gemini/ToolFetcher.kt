package com.dylibso.mcpx4j.examples.gemini

import com.dylibso.chicory.experimental.android.aot.AotAndroidMachine
import com.dylibso.mcpx4j.core.*
import com.google.ai.client.generativeai.type.*
import org.extism.sdk.chicory.*
import org.extism.sdk.chicory.http.client.urlconnection.HttpUrlConnectionClientAdapter
import org.extism.sdk.chicory.http.config.android.AndroidHttpConfig
import org.json.JSONObject
import kotlin.collections.mutableMapOf

object ToolFetcher {
    fun fetchFunctions(): FunctionRepository {
        val mcpx =
            // Configure the MCP.RUN Session
            Mcpx.forApiKey(BuildConfig.mcpRunKey)
                .withServletOptions(
                    McpxServletOptions.builder()
                        .withMachineFactory{ AotAndroidMachine(it) }
                        // Setup an HTTP client compatible with Android
                        // on the Chicory runtime
                        .withChicoryHttpConfig(AndroidHttpConfig.get())
                        // Configure an alternative, Android-specific logger
                        .withChicoryLogger(AndroidLogger("mcpx4j-runtime"))
                        .build())
                // Configure also the MCPX4J HTTP client to use
                // the Android-compatible implementation
                .withHttpClientAdapter(HttpUrlConnectionClientAdapter())
                .withProfile(BuildConfig.profile)
                .build()

        // Refresh once the list of installations.
        // This can be also scheduled for periodic refresh.
        mcpx.refreshInstallations()

        // Extract the metadata of each `McpxTool` into a `FunctionDeclaration`
        val factories = mcpx.servletFactories()
        val functionDeclarations =
            factories.toList()
                .associate { it.name() to toFunctionDeclarationList(it.schema()) }
        // Create a map name -> McpxServlet name for quicker lookup
        val mcpxTools =
            functionDeclarations.flatMap { e -> e.value.map { it.name to e.key } }.toMap()
        return FunctionRepository(mcpx, functionDeclarations.flatMap { e -> e.value.map { it.name to it } }.toMap(), mcpxTools)
    }

    private fun toFunctionDeclarationList(toolSchemas: String): List<FunctionDeclaration> {
        val schema = JSONObject(toolSchemas)
        if (!schema.has("tools")) {
            val parsedSchema = ParsedSchema.parseObject(schema.getJSONObject("inputSchema"))
            return listOf(defineFunction(
                name = schema.getString("name"),
                description = schema.getString("description"),
                parameters = parsedSchema.parameters,
                requiredParameters = parsedSchema.requiredParameters)
            )
        }
        val tools = schema.getJSONArray("tools")
        val declarations = mutableListOf<FunctionDeclaration>()
        for (i in 0..<tools.length()) {
            val tool = tools.getJSONObject(i)
            val f = toFunctionDeclaration(tool)
            declarations.add(f)
        }
        return declarations;
    }

    private fun toFunctionDeclaration(json: JSONObject): FunctionDeclaration {
        val parsedSchema = ParsedSchema.parseObject(json.getJSONObject("inputSchema"))
        val f = defineFunction(
            name = json.getString("name"),
            description = json.getString("description"),
            parameters = parsedSchema.parameters,
            requiredParameters = parsedSchema.requiredParameters
        )
        return f
    }
}
