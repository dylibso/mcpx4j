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
import dev.langchain4j.service.tool.ToolExecutor;
import io.smallrye.config.SmallRyeConfig;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LangChain4jMcpx4jMain {
    public static void main(String[] args) throws IOException {
        SmallRyeConfig config = ConfigProvider.getConfig().unwrap(SmallRyeConfig.class);

        String apiKey = config.getValue("mcpx.api-key", String.class);
        String baseUrl = config.getValue("mcpx.base-url", String.class);
        String profileId = config.getValue("mcpx.profile-id", String.class);

        var mcpx = Mcpx.forApiKey(apiKey).withBaseUrl(baseUrl).build();
        mcpx.refreshInstallations(profileId);

        var servlets = mcpx.servlets();

        Map<ToolSpecification, ToolExecutor> tools = toolsFromMcpxServlets(servlets);

        ChatLanguageModel chatLanguageModel;
        chatLanguageModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2")
                .timeout(Duration.ofMinutes(5))
                .build();

        var services = AiServices.builder(Chat.class)
                .chatLanguageModel(chatLanguageModel)
                .tools(tools)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(200))
                .build();

        String question = "can you read and describe the contents of https://dylibso.com using the fetch text tool?";
        System.out.println("QUESTION: " + question);
        System.out.println("ASSISTANT: " + services.send(question));

    }

    @NotNull
    private static Map<ToolSpecification, ToolExecutor> toolsFromMcpxServlets(Collection<McpxServlet> servlets) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Map<ToolSpecification, ToolExecutor> tools = new HashMap<>();
        for (McpxServlet servlet : servlets) {
            for (McpxTool tool : servlet.tools().values()) {
                JsonNode schema = mapper.readTree(tool.inputSchema());
                JsonSchemaElement jsonSchemaElement = ToolSpecificationHelper.jsonNodeToJsonSchemaElement(schema);
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
        String send(String msg);
    }
}