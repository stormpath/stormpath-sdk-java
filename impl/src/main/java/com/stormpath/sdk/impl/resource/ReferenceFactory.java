package com.stormpath.sdk.impl.resource;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.resource.Resource;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 0.9
 */
public class ReferenceFactory {

    public ReferenceFactory(){}

    public Map<String, String> createReference(Map map) {
        Assert.isTrue(!map.isEmpty() && map.containsKey(AbstractResource.HREF_PROP_NAME),
                "Reference resource must have an 'href' property.");
        String href = String.valueOf(map.get(AbstractResource.HREF_PROP_NAME));

        Map<String, String> reference = new HashMap<String, String>(1);
        reference.put(AbstractResource.HREF_PROP_NAME, href);

        return reference;
    }

    public Map<String, String> createReference(String resourceName, Map map) {
        Assert.isTrue(!map.isEmpty() && map.containsKey(AbstractResource.HREF_PROP_NAME),
                "'" + resourceName + "' resource must have an 'href' property.");
        String href = String.valueOf(map.get(AbstractResource.HREF_PROP_NAME));

        Map<String, String> reference = new HashMap<String, String>(1);
        reference.put(AbstractResource.HREF_PROP_NAME, href);

        return reference;
    }

    public Map<String, String> createReference(String resourceName, Resource resource) {
        Assert.notNull(resource, "Resource argument cannot be null.");
        String href = resource.getHref();
        Assert.hasText(href,  "'" + resourceName + "' resource must have an 'href' property.");

        Map<String, String> reference = new HashMap<String, String>(1);
        reference.put(AbstractResource.HREF_PROP_NAME, href);

        return reference;
    }
}
