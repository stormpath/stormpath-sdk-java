package com.stormpath.sdk.impl.config;

import com.stormpath.sdk.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class OptionalPropertiesSource implements PropertiesSource {

    private static final Logger log = LoggerFactory.getLogger(OptionalPropertiesSource.class);

    private final PropertiesSource propertiesSource;

    public OptionalPropertiesSource(PropertiesSource source) {
        Assert.notNull(source, "source cannot be null.");
        this.propertiesSource = source;
    }

    @Override
    public Map<String,String> getProperties() {
        try {
            return propertiesSource.getProperties();
        } catch (Exception e) {
            log.debug("Unable to obtain properties from optional properties source {}", propertiesSource);
        }
        return new LinkedHashMap<String, String>();
    }
}
