package com.dylibso.mcpx4j.core;

import java.util.List;
import java.util.Map;

public interface JsonDecoder {
    Map<String, ServletInstall> servletInstalls(byte[] bytes);

    List<McpxToolDescriptor> toolDescriptors(byte[] bytes);

    String parseSearchRequest(byte[] bytes);
}
