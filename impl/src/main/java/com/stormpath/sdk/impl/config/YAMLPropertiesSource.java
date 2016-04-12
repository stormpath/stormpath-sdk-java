package com.stormpath.sdk.impl.config;

import com.stormpath.sdk.impl.io.Resource;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class YAMLPropertiesSource implements PropertiesSource {

    private static final Logger log = LoggerFactory.getLogger(YAMLPropertiesSource.class);
    private final Resource resource;

    public YAMLPropertiesSource(Resource resource) {
        Assert.notNull(resource, "resource argument cannot be null.");
        this.resource = resource;
    }

    @Override
    public Map<String, String> getProperties() {
        try (InputStream in = resource.getInputStream()) {
            // check to see if file exists
            if (in != null) {
                try {
                    // test to see if Yaml is on the classpath
                    Class.forName("org.yaml.snakeyaml.Yaml");
                    Yaml yaml = new Yaml();
                    Map config = yaml.loadAs(in, Map.class);
                    return getFlattenedMap(config);
                } catch (ClassNotFoundException e) {
                    log.warn("YAML not found in classpath, add 'org.yaml:snakeyaml' to support YAML configuration");
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read resource [" + resource + "]: " + e.getMessage(), e);
        }
        return new LinkedHashMap<>();
    }

    /**
     * Return a flattened version of the given map, recursively following any nested Map
     * or Collection values. Entries from the resulting map retain the same order as the
     * source.
     *
     * Copied from https://github.com/spring-projects/spring-framework/blob/master/spring-beans/src/main/java/org/springframework/beans/factory/config/YamlProcessor.java
     *
     * @param source the source map
     * @return a flattened map
     * @since 1.0
     */
    protected final Map<String, Object> getFlattenedMap(Map<String, Object> source) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        buildFlattenedMap(result, source, null);
        return result;
    }

    private void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, String path) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            if (Strings.hasText(path)) {
                if (key.startsWith("[")) {
                    key = path + key;
                }
                else {
                    key = path + "." + key;
                }
            }
            Object value = entry.getValue();
            if (value instanceof String) {
                result.put(key, value);
            }
            else if (value instanceof Map) {
                // Need a compound key
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) value;
                buildFlattenedMap(result, map, key);
            }
            else if (value instanceof Collection) {
                // Need a compound key
                @SuppressWarnings("unchecked")
                Collection<Object> collection = (Collection<Object>) value;
                int count = 0;
                for (Object object : collection) {
                    buildFlattenedMap(result,
                            Collections.singletonMap("[" + (count++) + "]", object), key);
                }
            }
            else {
                result.put(key, value != null ? value : "");
            }
        }
    }
}
