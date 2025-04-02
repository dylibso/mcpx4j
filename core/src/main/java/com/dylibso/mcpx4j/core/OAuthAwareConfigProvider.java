package com.dylibso.mcpx4j.core;

import org.extism.sdk.chicory.ConfigProvider;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class OAuthAwareConfigProvider implements ConfigProvider {
    volatile ServletOAuth oAuth;
    private Map<String, String> map;
    private Supplier<ServletOAuth> oAuthSupplier;

    public OAuthAwareConfigProvider(){
        this.map = Map.of();
    }

    public OAuthAwareConfigProvider(Map<String, String> config, Supplier<ServletOAuth> oAuthSupplier) {
        this.map = config;
        this.oAuthSupplier = oAuthSupplier;
    }

    public ServletOAuth oAuth() {
        return oAuth;
    }

    public void updateOAuth() {
        this.oAuth = oAuthSupplier.get();
    }

    @Override
    public String get(String key) {
        if (oAuth == null || oAuth.maxTimestamp() > System.currentTimeMillis()) {
            updateOAuth();
        }
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
