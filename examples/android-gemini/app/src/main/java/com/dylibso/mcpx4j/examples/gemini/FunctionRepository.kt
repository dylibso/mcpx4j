package com.dylibso.mcpx4j.examples.gemini

import android.util.Log
import androidx.collection.LruCache
import com.dylibso.mcpx4j.core.Mcpx
import com.dylibso.mcpx4j.core.McpxServlet
import com.google.ai.client.generativeai.type.FunctionDeclaration
import org.json.JSONObject
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.toString

data class FunctionRepository(
    val mcpx: Mcpx,
    val functionDeclarations: Map<String, FunctionDeclaration>,
    val mcpxTools: Map<String, String>
) {

    val servlets = object : LruCache<String, McpxServlet>(5) {
        override fun create(servletName: String): McpxServlet {
            val servletFactory = mcpx[servletName]
            return servletFactory.create()
        }
    }

    val service = Executors.newSingleThreadExecutor {
        Thread(ThreadGroup("chicory"), it, "chicory-thread", 8 * 1024 * 1024) }

    fun call(toolName: String, args: Map<String, String?>): JSONObject {
        val servletName = mcpxTools[toolName]
            ?: return JSONObject(mapOf("result" to "$toolName is not a valid function"))

        val jargs = JSONObject(args)
        val jsargs = JSONObject(
            mapOf(
                "method" to "tools/call",
                "params" to mapOf(
                    "name" to toolName,
                    "arguments" to jargs
                )
            )
        )

        Log.i("mcpx4j-tool", "looking up $toolName")
        // Invoke the mcp.run tool with the given arguments
        val servlet = servlets[servletName]!!

        Log.i("mcpx4j-tool", "invoking $toolName with args = $jargs")
        val tool = servlet.tools()[toolName]!!

        val call: Callable<String> = Callable { tool.call(jsargs.toString()) }
        val f: Future<String> = service.submit(call)
        val res = f.get()
        Log.i("mcpx4j-tool", "$toolName returned: $res")

        // Ensure we always return a map
        return JSONObject(mapOf("result" to res))
    }
}