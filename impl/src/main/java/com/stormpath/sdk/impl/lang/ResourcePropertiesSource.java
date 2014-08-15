package com.stormpath.sdk.impl.lang;

import com.stormpath.sdk.lang.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

public class ResourcePropertiesSource implements PropertiesSource {

    private final Resource resource;

    public ResourcePropertiesSource(Resource resource) {
        Assert.notNull(resource, "resource argument cannot be null.");
        this.resource = resource;
    }

    @Override
    public Properties getProperties() {
        Properties props = new Properties();
        try {
            InputStream is = resource.getInputStream();
            props.load(new InputStreamReader(is, Charset.forName("UTF-8")));
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read resource [" + resource + "]", e);
        }
        return props;
    }
}
