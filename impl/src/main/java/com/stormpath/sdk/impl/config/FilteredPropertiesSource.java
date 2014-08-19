package com.stormpath.sdk.impl.config;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;

import java.util.Properties;
import java.util.Set;

public class FilteredPropertiesSource implements PropertiesSource {

    private final PropertiesSource propertiesSource;
    private final Filter filter;

    public FilteredPropertiesSource(PropertiesSource propertiesSource, Filter filter) {
        Assert.notNull(propertiesSource, "source cannot be null.");
        Assert.notNull(filter, "filter cannot be null.");
        this.propertiesSource = propertiesSource;
        this.filter = filter;
    }

    @Override
    public Properties getProperties() {
        Properties props = this.propertiesSource.getProperties();
        Properties retained = new Properties();

        if (!Collections.isEmpty(props)) {
            Set<Object> keys = props.keySet();
            for(Object key : keys) {
                String sKey = key.toString();
                String value = props.getProperty(sKey);
                String[] evaluated = filter.map(sKey, value);
                if (evaluated != null) {
                    Assert.isTrue(2 == evaluated.length, "Filter returned string array must have a length of 2 (key/value pair)");
                    retained.setProperty(evaluated[0], evaluated[1]);
                }
            }
        }

        return retained;
    }

    public static interface Filter {
        String[] map(String key, String value);
    }
}
