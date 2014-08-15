package com.stormpath.sdk.servlet.cache;

import com.stormpath.sdk.cache.Cache;
import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.impl.cache.DisabledCacheManager;
import com.stormpath.sdk.lang.Assert;

import javax.servlet.ServletContext;

public class ServletContextAttributeCacheManager implements CacheManager {

    private static final String SERVLET_CONTEXT_ATTRIBUTE_NAME = CacheManager.class.getName();

    private final ServletContext servletContext;
    private final boolean        replacementAllowed;

    private transient volatile CacheManager targetCacheManager;
    private transient volatile boolean      discovered;

    public ServletContextAttributeCacheManager(ServletContext servletContext, CacheManager defaultCacheManager,
                                               boolean replacementAllowed) {

        Assert.notNull(servletContext, "servletContext cannot be null.");
        Assert.notNull(defaultCacheManager, "default CacheManager cannot be null.  If you want to disable caching, " +
                                            "configure a " + DisabledCacheManager.class.getName() + " instance.");
        this.servletContext = servletContext;
        this.targetCacheManager = defaultCacheManager;
        this.discovered = false;
        this.replacementAllowed = replacementAllowed;
    }

    @Override
    public <K, V> Cache<K, V> getCache(String name) {
        CacheManager cacheManager = getTargetCacheManager();
        return cacheManager.getCache(name);
    }

    public CacheManager getTargetCacheManager() {
        CacheManager cacheManager = this.targetCacheManager;

        if (!discovered) {
            cacheManager = lookupCacheManager(this.targetCacheManager);
        }

        return cacheManager;
    }

    private CacheManager lookupCacheManager(CacheManager defaultCacheManager) {
        Object value = servletContext.getAttribute(SERVLET_CONTEXT_ATTRIBUTE_NAME);
        if (value != null) {

            Assert.isInstanceOf(CacheManager.class, value,
                                "ServletContext attribute value named [" + SERVLET_CONTEXT_ATTRIBUTE_NAME + "] does not " +
                                "implement the " + CacheManager.class.getName() + " interface.  This is required for " +
                                "CacheManager discovery and reflects an invalid application configuration.");
            discovered = true;

            if (!replacementAllowed) {
                String msg = "Discovered ServletContext attribute-based CacheManager [" + value + "] but another " +
                             "CacheManager has already been configured and is in use.  Configured CacheManager: [" +
                             this.targetCacheManager + "].  This is an invalid application configuration - " +
                             "please ensure you configure only one CacheManager.";
                throw new IllegalStateException(msg);
            }

            this.targetCacheManager = (CacheManager)value;
            return this.targetCacheManager;
        }

        return defaultCacheManager;
    }


}
