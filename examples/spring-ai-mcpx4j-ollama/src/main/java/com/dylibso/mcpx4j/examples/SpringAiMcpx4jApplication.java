package com.dylibso.mcpx4j.examples;

import com.dylibso.mcpx4j.core.Mcpx;
import com.dylibso.mcpx4j.core.McpxServlet;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.Collection;

@SpringBootApplication
public class SpringAiMcpx4jApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringAiMcpx4jApplication.class, args);
	}

	@Value("${mcpx.api-key}")
	private String apiKey;

	@Value("${mcpx.base-url}")
	private String baseUrl;

	@Value("${mcpx.profile-id}")
	private String profileId;

	@Bean
	public CommandLineRunner startup(ChatClient.Builder chatClientBuilder,
									 ConfigurableApplicationContext context) throws IOException {

		var mcpx = Mcpx.forApiKey(apiKey).withBaseUrl(baseUrl).build();
		mcpx.refreshInstallations(profileId);
		var functions = functionsFromMcpxServlets(mcpx.servlets());

		return args -> {
			var chatClient = chatClientBuilder
					.defaultFunctions(functions)
					.build();

			String question = "can you read and describe the contents of https://dylibso.com using the fetch text tool?";
			System.out.println("QUESTION: " + question);
			System.out.println("ASSISTANT: " + chatClient.prompt(question).call().content());

			context.close();
		};
	}

	McpxToolFunctionCallback[] functionsFromMcpxServlets(Collection<McpxServlet> servlets) {
		return servlets.stream()
				.flatMap(servlet -> servlet.tools().values().stream())
				.map(McpxToolFunctionCallback::new)
				.toArray(McpxToolFunctionCallback[]::new);
	}

}
