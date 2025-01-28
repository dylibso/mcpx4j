# MCPX4J

The MCP.RUN client library for Java!


## Installation

```xml
<dependencies>
    <dependency>
       <groupId>com.dylibso.mcpx4j</groupId>
        <artifactId>mcpx4j</artifactId>
        <version>${mcpx4j.version}</version>
    </dependency>
</dependencies>
```

## Quick Start

Create a new `Mcpx` instance with your API key and start invoking your installed tools.

```java
import com.github.dylibso.mcpx4j.Mcpx;

Mcpx mcpx = Mcpx.forApiKey(apiKey).build();
mcpx.refreshInstallations();
var servlet = mcpx.get("my-servlet-id");
var tool = servlet.get("my-tool-id");
var bytes = servlet.call("""
{
    "method": "tools/call",
    "params": {
        "name": "my-tool-id",
        "arguments": {
            ...
        }  
    }
}
""");
```

Detailed examples are available in the [examples](examples) directory.


## Plugin Updates

`Mcpx` keeps a cache of installations. You can refresh the cache by calling `refreshInstallations()`.
The internal store is thread-safe, so you can schedule a refresh in a separate thread.

```java
var scheduler = Executors.newSingleThreadScheduledExecutor();
scheduler.scheduleAtFixedRate(mcpx::refreshInstallations, 0, 5, TimeUnit.MINUTES);
```

## Configuration

It is possible to provide an alternative base URL for the mcp.run service
(defaults to `https://www.mcp.run`).

```java
var mcpx = Mcpx.forApiKey(apiKey)
    .withBaseUrl(jsonDecoder)
    ...
    .build();

```

The `Mcpx` builder allows to customize the JSON decoder and the HTTP client.

### JSON Decoder

```java
var mcpx = Mcpx.forApiKey(apiKey)
    .withBaseUrl("https://localhost:8080")
    ...
    .build();
```

The default JSON decoder is `JacksonDecoder`, using [Jackson Databind][jackson]. We also provide an alternative
implementation based on [Jakarta JSON-P][jakarta-jsonp].

Both dependencies are optional, so you should make sure to include them in your project if you want to use them.

```xml
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>${jackson-databind.version}</version>
    </dependency>

    <!-- Eclipse Parsson is an implementation of the Jakarta JSON-P API  -->
    <dependency>
        <groupId>org.eclipse.parsson</groupId>
        <artifactId>parsson</artifactId>
        <version>${parsson.version}</version>
    </dependency>
```

[jackson]: https://github.com/FasterXML/jackson
[jdk-http]: https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpClient.html
[jakarta-jsonp]: https://github.com/jakartaee/jsonp-api

### HTTP Client

```java
var mcpx = Mcpx.forApiKey(apiKey)
    .withHttpClientAdapter(httpClientAdapter)
    ...
    .build();
```


The default HTTP client is `JdkHttpClientAdapter`, using the [JDK HTTP client][jdk-http],
there is also a lighter-weight implementation using an `HttpURLConnection`, called `HttpUrlConnectionClientAdapter`.

```java
var mcpx = Mcpx.forApiKey(apiKey)
    .withHttpClientAdapter(new JdkHttpClientAdapter())
    .withHttpClientAdapter(new HttpUrlConnectionClientAdapter()) // alternatively
    ...
    .build();
```

The `HttpClientAdapter` interface is currently borrowed from the [Chicory Extism SDK][chicory-sdk].
This might change in the future.


### Servlet Options

It is also possible to provide predefined options for the Servlet implementation: these
config options are propagated to the [Chicory Extism SDK][chicory-sdk] upon creation of
each Servlet.

```java
var mcpx = Mcpx.forApiKey(apiKey)
    .withServletOptions(McpxServletOptions.builder()
        .withChicoryHttpConfig(...)
        .withChicoryLogger(...)
        .withAoT() 
        .build())
    .build();
```

- `ChicoryHttpConfig` controls the HTTP client and Json decoder used by the Chicory SDK:
  usually these should match the ones used by the MCPX4J client.
- `ChicoryLogger` is a logger implementation for the Chicory SDK.
- `AoT` enables the Ahead-of-Time compilation for the Chicory SDK.


[chicory-sdk]: https://github.com/extism/chicory-sdk
