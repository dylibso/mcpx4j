package com.dylibso.mcpx4j.core;

import org.extism.sdk.chicory.ConfigProvider;

import java.util.Map;
import java.util.Objects;

public class OAuthAwareConfigProvider implements ConfigProvider {
    volatile ServletOAuth oAuth;
    private Map<String, String> map;

    public OAuthAwareConfigProvider(){
        this.map = Map.of();
    }

    public OAuthAwareConfigProvider(Map<String, String> config) {
        this.map = config;
    }

    public ServletOAuth oAuth() {
        return oAuth;
    }

    public void updateOAuth(ServletOAuth oAuth) {
        this.oAuth = oAuth;
    }

    @Override
    public String get(String key) {
        if (oAuth != null) {
            ServletOAuthInfo info = oAuth.info();
            if (info.configName().equals(key)) {
                return info.accessToken();
            }
        }
        return map.get(key);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OAuthAwareConfigProvider)) return false;
        OAuthAwareConfigProvider that = (OAuthAwareConfigProvider) o;
        return Objects.equals(oAuth, that.oAuth) && Objects.equals(map, that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(oAuth, map);
    }

}
