package com.stormpath.sdk.servlet.config.impl;

import com.stormpath.sdk.servlet.config.CookieConfig;

public class DefaultCookieConfig implements CookieConfig {

    private String name;
    private String comment;
    private String domain;
    private int maxAge = -1;
    private String path;
    private boolean secure = false;
    private boolean httpOnly = false;

    public DefaultCookieConfig(){}

    public DefaultCookieConfig(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public DefaultCookieConfig setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getComment() {
        return comment;
    }

    public DefaultCookieConfig setComment(String comment) {
        this.comment = comment;
        return this;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    public DefaultCookieConfig setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    @Override
    public int getMaxAge() {
        return maxAge;
    }

    public DefaultCookieConfig setMaxAge(int maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    @Override
    public String getPath() {
        return path;
    }

    public DefaultCookieConfig setPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    public DefaultCookieConfig setSecure(boolean secure) {
        this.secure = secure;
        return this;
    }

    @Override
    public boolean isHttpOnly() {
        return httpOnly;
    }

    public DefaultCookieConfig setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
        return this;
    }
}
