package com.stormpath.sdk.impl.config;

import com.stormpath.sdk.impl.io.Resource;
import com.stormpath.sdk.lang.Assert;

import java.io.IOException;
import java.util.Map;

public class ResourcePropertiesSource implements PropertiesSource {

    private final Resource resource;

    public ResourcePropertiesSource(Resource resource) {
        Assert.notNull(resource, "resource argument cannot be null.");
        this.resource = resource;
    }

    @Override
    public Map<String, String> getProperties() {
        try {
            return new DefaultPropertiesParser().parse(resource);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read resource [" + resource + "]: " + e.getMessage(), e);
        }
    }
}
