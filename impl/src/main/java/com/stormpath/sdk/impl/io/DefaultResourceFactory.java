package com.stormpath.sdk.impl.io;

import com.stormpath.sdk.lang.Assert;

public class DefaultResourceFactory implements ResourceFactory {

    @Override
    public Resource createResource(String location) {
        Assert.hasText(location, "location argument cannot be null or empty.");

        if (location.startsWith(ClasspathResource.SCHEME_PREFIX)) {
            return new ClasspathResource(location);
        }

        String lcase = location.toLowerCase();

        if (location.startsWith(UrlResource.SCHEME_PREFIX) || lcase.startsWith("http:") || lcase.startsWith("https:")) {
            return new UrlResource(location);
        }

        //otherwise we assume a file:
        return new FileResource(location);
    }
}
