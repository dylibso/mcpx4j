package com.dylibso.mcpx4j.core;

import java.util.Objects;

public class ServletOAuthInfo {
    String config_name;
    String access_token;
    ServletOAuthInfo(){}

    public ServletOAuthInfo(String configName, String accessToken) {
        this.config_name = configName;
        this.access_token = accessToken;
    }

    public String configName() {
        return config_name;
    }
    public String accessToken() {
        return access_token;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ServletOAuthInfo)) return false;
        ServletOAuthInfo that = (ServletOAuthInfo) o;
        return Objects.equals(config_name, that.config_name) && Objects.equals(access_token, that.access_token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(config_name, access_token);
    }
}
