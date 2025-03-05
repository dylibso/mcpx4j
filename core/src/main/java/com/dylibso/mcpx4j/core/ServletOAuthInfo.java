package com.dylibso.mcpx4j.core;

import java.util.Objects;

public class ServletOAuthInfo {
    String configName;
    String accessToken;
    ServletOAuthInfo(){}

    public ServletOAuthInfo(String configName, String accessToken) {
        this.configName = configName;
        this.accessToken = accessToken;
    }

    public String configName() {
        return configName;
    }
    public String accessToken() {
        return accessToken;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ServletOAuthInfo)) return false;
        ServletOAuthInfo that = (ServletOAuthInfo) o;
        return Objects.equals(configName, that.configName) && Objects.equals(accessToken, that.accessToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configName, accessToken);
    }
}
