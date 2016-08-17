package com.stormpath.zuul.account;

import com.stormpath.sdk.lang.Function;
import com.stormpath.sdk.resource.Resource;

/**
 * @since 1.1.0
 */
public class ResourceHrefFunction implements Function<Resource, String> {

    @Override
    public String apply(Resource r) {
        return r.getHref();
    }
}
