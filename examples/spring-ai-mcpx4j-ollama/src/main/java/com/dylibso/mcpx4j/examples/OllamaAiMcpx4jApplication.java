package com.dylibso.mcpx4j.examples;

import com.dylibso.mcpx4j.core.Mcpx;
import com.dylibso.mcpx4j.core.McpxServletFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

@SpringBootApplication
public class OllamaAiMcpx4jApplication {

    private final static Logger LOGGER = Logger.getLogger("mcpx");

    public static void main(String[] args) {
        SpringApplication.run(OllamaAiMcpx4jApplication.class, args);
    }

    // Load the configuration values from application.properties
    @Value("${mcpx.api-key}")
    private String apiKey;

    @Value("${mcpx.base-url}")
    private String baseUrl;

    @Value("${mcpx.profile-id}")
    private String profileId;

    @Bean
    public CommandLineRunner startup(ChatClient.Builder chatClientBuilder,
                                     ConfigurableApplicationContext context) throws IOException {

        // Instantiate a new Mcpx client with the configuration values.
        var mcpx = Mcpx.forApiKey(apiKey).withBaseUrl(baseUrl).withProfile(profileId).build();
        LOGGER.info("Refreshing installations");
        // Refresh the installed servlets definitions from mcp.run.
        // This will load the configuration once. You can schedule this invocation
        // periodically to refresh such configuration.
        mcpx.refreshInstallations();
        // Instantiate each servlet and expose it as a `FunctionCallback`
        var functions = functionsFromMcpxServlets(mcpx.servletFactories());

        return args -> {
            var chatClient = chatClientBuilder
                    // Load the `FunctionCallback`s into the chat interface
                    .defaultFunctions(functions)
                    // Define system prompt for AI behavior
                    // Note: this can be whatever you want, but it's recommended to give the LLM
                    // as much context as you can here while remaining generic for your use case.
                    .defaultSystem("""
You are a helpful AI assistant with access to various external tools and APIs. Your goal is to complete tasks thoroughly and autonomously by making full use of these tools. Here are your core operating principles:

1. Take initiative - Don't wait for user permission to use tools. If a tool would help complete the task, use it immediately.
2. Chain multiple tools together - Many tasks require multiple tool calls in sequence. Plan out and execute the full chain of calls needed to achieve the goal.
3. Handle errors gracefully - If a tool call fails, try alternative approaches or tools rather than asking the user what to do.
4. Make reasonable assumptions - When tool calls require parameters, use your best judgment to provide appropriate values rather than asking the user.
5. Show your work - After completing tool calls, explain what you did and show relevant results, but focus on the final outcome the user wanted.
6. Be thorough - Use tools repeatedly as needed until you're confident you've fully completed the task. Don't stop at partial solutions.

Your responses should focus on results rather than asking questions. Only ask the user for clarification if the task itself is unclear or impossible with the tools available.
                    """)
                    .build();

            System.out.println("Chat started. Type 'exit' to quit.");
            while (true) {
                String input = System.console().readLine("YOU: ");
                if (input.equals("exit")) {
                    System.out.println("Goodbye!");
                    break;
                }
                System.out.println("ASSISTANT: " + chatClient.prompt(input).call().content());
            }

            context.close();
        };
    }

    // Converts the servlets into FunctionCallbacks.
    McpxToolFunctionCallback[] functionsFromMcpxServlets(
            Collection<McpxServletFactory> servlets) {
        return servlets.stream()
                .peek(s -> LOGGER.info("Initializing Servlet " + s.name()))
                .map(McpxServletFactory::create)
                .peek(s -> LOGGER.info("Initialized Servlet " + s.name()))
                .flatMap(servlet -> servlet.tools().values().stream())
                .map(McpxToolFunctionCallback::new)
                .toArray(McpxToolFunctionCallback[]::new);
    }

}
