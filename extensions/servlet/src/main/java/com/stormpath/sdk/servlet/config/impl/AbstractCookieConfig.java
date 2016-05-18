package com.stormpath.sdk.servlet.config.impl;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.config.CookieConfig;
import com.stormpath.sdk.servlet.config.CookieProperties;

/**
 * @since 1.0.RC10
 */
public abstract class AbstractCookieConfig implements CookieConfig {
    protected static final String NAME = "name";
    protected static final String COMMENT = "comment";
    protected static final String DOMAIN = "domain";
    protected static final String MAX_AGE = "maxAge";
    protected static final String PATH = "path";
    protected static final String SECURE = "secure";
    protected static final String HTTP_ONLY = "httpOnly";

    protected final String name;
    protected final String comment;
    protected final String domain;
    protected final int maxAge;
    protected final String path;
    protected final boolean secure;
    protected final boolean httpOnly;

    /**
     * Config prefix should return the properties prefix for this cookie config.
     * <p/>
     * For example: stormpath.web.[accessTokenCookie|refreshTokenCookie]
     *
     * @return The config prefix
     */
    protected abstract String configPrefix();

    public AbstractCookieConfig(ConfigReader configReader) {
        Assert.notNull(configReader);
        this.name = configReader.getString(configKeyFor(NAME));
        Assert.hasText(this.name, NAME + " cannot be null or empty.");
        this.comment = configReader.getString(configKeyFor(COMMENT));
        this.domain = configReader.getString(configKeyFor(DOMAIN));
        this.path = configReader.getString(configKeyFor(PATH));
        this.secure = configReader.getBoolean(configKeyFor(SECURE));
        this.httpOnly = configReader.getBoolean(configKeyFor(HTTP_ONLY));
        this.maxAge = 1;
    }

    public AbstractCookieConfig(String name,
                                String comment,
                                String domain,
                                int maxAge,
                                String path,
                                boolean secure,
                                boolean httpOnly) {
        this.name = name;
        Assert.hasText(this.name, NAME + " cannot be null or empty.");
        this.comment = comment;
        this.domain = domain;
        this.maxAge = Math.max(-1, maxAge);
        this.path = path;
        this.secure = secure;
        this.httpOnly = httpOnly;
    }

    public AbstractCookieConfig(CookieProperties cookieProperties) {
        this(cookieProperties.getCookieName(),
                cookieProperties.getCookieComment(),
                cookieProperties.getCookieDomain(),
                cookieProperties.getCookieMaxAge(),
                cookieProperties.getCookiePath(),
                cookieProperties.isCookieSecure(),
                cookieProperties.isCookieHttpOnly());
    }

    protected String configKeyFor(String propertyName) {
        return configPrefix() + "." + propertyName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public int getMaxAge() {
        return maxAge;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public boolean isHttpOnly() {
        return httpOnly;
    }
}
