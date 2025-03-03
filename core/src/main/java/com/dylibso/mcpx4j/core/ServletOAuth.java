package com.dylibso.mcpx4j.core;

public class ServletOAuth {
    ServletOAuthInfo info;
    long maxTimestamp;
    int maxAge;

    ServletOAuth() {}

    ServletOAuth(ServletOAuthInfo info, long maxTimestamp, int maxAge) {
        this.info = info;
        this.maxTimestamp = maxTimestamp;
        this.maxAge = maxAge;
    }

    public ServletOAuthInfo info() {
        return info;
    }

    public long maxTimestamp() {
        return maxTimestamp;
    }

    public int maxAge() {
        return maxAge;
    }


}
