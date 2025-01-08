package com.dylibso.mcpx4j.examples;

import com.dylibso.mcpx4j.core.Mcpx;
import com.dylibso.mcpx4j.core.McpxServlet;
import com.dylibso.mcpx4j.core.McpxTool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchemaElement;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.tool.ToolExecutor;
import io.smallrye.config.SmallRyeConfig;
import org.eclipse.microprofile.config.ConfigProvider;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LangChain4jMcpx4jMain {
    public static void main(String[] args) throws IOException {
        // We use SmallRye config to read a property file
        // that contains environment variable placeholders.
        // You can use your favorite configuration library.
        SmallRyeConfig config =
                ConfigProvider.getConfig().unwrap(SmallRyeConfig.class);

        String apiKey = config.getValue("mcpx.api-key", String.class);
        String baseUrl = config.getValue("mcpx.base-url", String.class);
        String profileId = config.getValue("mcpx.profile-id", String.class);

        // Instantiate a new Mcpx client with the configuration values.
        var mcpx = Mcpx.forApiKey(apiKey).withBaseUrl(baseUrl).build();
        // Refresh the installed servlets definitions from mcp.run.
        // This will load the configuration once.
        // You can schedule this invocation periodically to refresh
        // such configuration.
        mcpx.refreshInstallations(profileId);
        // Instantiate each servlet and expose it as a
        // `ToolSpecification`, `ToolExecutor` pair.
        var servlets = mcpx.servlets();
        Map<ToolSpecification, ToolExecutor> tools =
                toolsFromMcpxServlets(servlets);

        ChatLanguageModel chatLanguageModel =
                OllamaChatModel.builder()
                        .baseUrl("http://localhost:11434")
                        .modelName("llama3.2")
                        .timeout(Duration.ofMinutes(5))
                        .build();

        var services = AiServices.builder(Chat.class)
                .chatLanguageModel(chatLanguageModel)
                .tools(tools)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(200))
                .build();

        System.out.println("Chat started. Type 'exit' to quit.");
        while (true) {
            String input = System.console().readLine("YOU: ");
            if (input.equals("exit")) {
                System.out.println("Goodbye!");
                break;
            }
            if (input.isBlank()) {
                continue;
            }
            System.out.println("ASSISTANT: " + services.send(input));
        }
    }

    private static Map<ToolSpecification, ToolExecutor> toolsFromMcpxServlets(
            Collection<McpxServlet> servlets) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Map<ToolSpecification, ToolExecutor> tools = new HashMap<>();
        for (McpxServlet servlet : servlets) {
            for (McpxTool tool : servlet.tools().values()) {
                JsonNode schema = mapper.readTree(tool.inputSchema());
                JsonSchemaElement jsonSchemaElement =
                        ToolSpecificationHelper.jsonNodeToJsonSchemaElement(schema);
                ToolSpecification spec = ToolSpecification.builder()
                        .name(tool.name())
                        .description(tool.description())
                        .parameters((JsonObjectSchema) jsonSchemaElement)
                        .build();
                tools.put(spec, new McpxToolExecutor(tool));
            }
        }
        return tools;
    }

    interface Chat {
        // Define system prompt for AI behavior
        // Note: this can be whatever you want, but it's recommended to give the LLM
        // as much context as you can here while remaining generic for your use case.
        @SystemMessage("""
You are a helpful AI assistant with access to various external tools and APIs. Your goal is to complete tasks thoroughly and autonomously by making full use of these tools. Here are your core operating principles:

1. Take initiative - Don't wait for user permission to use tools. If a tool	 would help complete the task, use it immediately.
2. Chain multiple tools together - Many tasks require multiple tool calls in sequence. Plan out and execute the full chain of calls needed to achieve the goal.
3. Handle errors gracefully - If a tool call fails, try alternative approaches or tools rather than asking the user what to do.
4. Make reasonable assumptions - When tool calls require parameters, use your best judgment to provide appropriate values rather than asking the user.
5. Show your work - After completing tool calls, explain what you did and show relevant results, but focus on the final outcome the user wanted.
6. Be thorough - Use tools repeatedly as needed until you're confident you've fully completed the task. Don't stop at partial solutions.

Your responses should focus on results rather than asking questions. Only ask the user for clarification if the task itself is unclear or impossible with the tools available.
                """)
        String send(String msg);
    }
}
