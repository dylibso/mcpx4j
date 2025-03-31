package com.dylibso.mcpx4j.examples.gemini

import android.util.Log
import com.dylibso.mcpx4j.core.Mcpx
import com.dylibso.mcpx4j.core.McpxServlet
import com.dylibso.mcpx4j.core.McpxTool
import com.google.ai.client.generativeai.type.FunctionDeclaration
import org.json.JSONObject
import java.util.WeakHashMap

data class FunctionRepository(
    val mcpx: Mcpx,
    val functionDeclarations: Map<String, FunctionDeclaration>,
    val mcpxTools: Map<String, String>) {

    val servlets = mutableMapOf<String, McpxServlet>()

    fun call(name: String, args: Map<String, String?>): JSONObject {
        val toolName = mcpxTools[name]
            ?: return JSONObject(mapOf("result" to "$name is not a valid function"))

        val jargs = JSONObject(args)
        val jsargs = JSONObject(mapOf(
            "method" to "tools/call",
            "params" to mapOf(
                "name" to name,
                "arguments" to jargs
            )))

        Log.i("mcpx4j-tool", "invoking $name with args = $jargs")
        // Invoke the mcp.run tool with the given arguments
        val servletName = mcpxTools[toolName]
        val servlet = servlets.getOrPut(servletName!!) {
            val servletFactory = mcpx[servletName]
            servletFactory.create()
        }

        val tool = servlet.tools()[toolName]!!

        val res = tool.call(jsargs.toString())
        Log.i("mcpx4j-tool", "$name returned: $res")

        // Ensure we always return a map
        return JSONObject(mapOf("result" to res))
    }
}