package com.dylibso.mcpx4j.core;

import org.extism.sdk.chicory.ConfigProvider;

import java.util.Map;
import java.util.Objects;

public class OAuthAwareConfigProvider implements ConfigProvider {
    volatile ServletOAuthInfo info;
    private final Map<String, String> map;

    public OAuthAwareConfigProvider(Map<String, String> config) {
        this.map = config;
    }

    public void updateOAuth(ServletOAuthInfo info) {
        this.info = info;
    }

    @Override
    public String get(String key) {
        if (info.configName().equals(key)) {
            return info.accessToken();
        }
        return map.get(key);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OAuthAwareConfigProvider)) return false;
        OAuthAwareConfigProvider that = (OAuthAwareConfigProvider) o;
        return Objects.equals(info, that.info) && Objects.equals(map, that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(info, map);
    }
}
