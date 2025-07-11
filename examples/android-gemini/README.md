## Gemini Android Demo

This is a demo of integrating Mcpx4j with Android with Gemini.

Make sure you rename `/local.properties.example` to `/local.properties`
before starting, and set the corresponding values.

Install the top-level `mcpx4j` project with `./mvnw install` before running this example.

### Android Compiler

This demo optionally support the experimental Chicory Android compiler. Configure your GitHub
username and token in `/gradle.properties` and enable it by uncommenting the line in com/dylibso/mcpx4j/examples/gemini/ToolFetcher.kt:19

```kotlin
val mcpx =
    // Configure the MCP.RUN Session
    Mcpx.forApiKey(BuildConfig.mcpRunKey)
        .withServletOptions(
            McpxServletOptions.builder() 
        // Uncomment the following line if you want to try the experimental Android compiler
        .withMachineFactory{ com.dylibso.chicory.experimental.android.aot.AotAndroidMachine(it) }
```

For further details, read more at https://github.com/dylibso/chicory-compiler-android/
