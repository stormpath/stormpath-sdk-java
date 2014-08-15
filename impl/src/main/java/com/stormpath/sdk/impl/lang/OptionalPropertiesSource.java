package com.stormpath.sdk.impl.lang;

import com.stormpath.sdk.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class OptionalPropertiesSource implements PropertiesSource {

    private static final Logger log = LoggerFactory.getLogger(OptionalPropertiesSource.class);

    private final PropertiesSource propertiesSource;

    public OptionalPropertiesSource(PropertiesSource source) {
        Assert.notNull(source, "source cannot be null.");
        this.propertiesSource = source;
    }

    @Override
    public Properties getProperties() {
        try {
            return propertiesSource.getProperties();
        } catch (Exception e) {
            log.debug("Unable to obtain properties from optional properties source {}", propertiesSource);
        }
        return new Properties();
    }
}
