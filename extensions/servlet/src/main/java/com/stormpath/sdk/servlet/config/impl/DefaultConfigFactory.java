/*
 * Copyright 2015 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.servlet.config.impl;

import com.stormpath.sdk.impl.config.DefaultEnvVarNameConverter;
import com.stormpath.sdk.impl.config.EnvVarNameConverter;
import com.stormpath.sdk.impl.config.EnvironmentVariablesPropertiesSource;
import com.stormpath.sdk.impl.config.FilteredPropertiesSource;
import com.stormpath.sdk.impl.config.OptionalPropertiesSource;
import com.stormpath.sdk.impl.config.PropertiesSource;
import com.stormpath.sdk.impl.config.ResourcePropertiesSource;
import com.stormpath.sdk.impl.config.SystemPropertiesSource;
import com.stormpath.sdk.impl.io.ClasspathResource;
import com.stormpath.sdk.impl.io.Resource;
import com.stormpath.sdk.impl.io.ResourceFactory;
import com.stormpath.sdk.impl.io.StringResource;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigFactory;
import com.stormpath.sdk.servlet.io.ServletContainerResourceFactory;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @since 1.0.RC3
 */
public class DefaultConfigFactory implements ConfigFactory {

    public static final String STORMPATH_PROPERTIES         = "stormpath.properties";
    public static final String STORMPATH_PROPERTIES_SOURCES = STORMPATH_PROPERTIES + ".sources";

    public static final String STORMPATH_YAML        = "stormpath.yml";
    public static final String STORMPATH_YAML_SOURCES = STORMPATH_YAML + ".sources";

    public static final  String ENVVARS_TOKEN       = "envvars";
    public static final  String SYSPROPS_TOKEN      = "sysprops";
    public static final  String CONTEXT_PARAM_TOKEN = "contextParam";
    private static final String NL                  = "\n";

    private static final String REQUIRED_TOKEN = "(required)";

    public static final String DEFAULT_STORMPATH_PROPERTIES_SOURCES =
        //MUST always be first:
        ClasspathResource.SCHEME_PREFIX + "META-INF/com/stormpath/sdk/servlet/default." + STORMPATH_PROPERTIES + NL +
        ClasspathResource.SCHEME_PREFIX + STORMPATH_PROPERTIES + NL +
        "/WEB-INF/stormpath.properties" + NL +
        CONTEXT_PARAM_TOKEN + NL +
        ENVVARS_TOKEN + NL +
        SYSPROPS_TOKEN;

    private static final EnvVarNameConverter envVarNameConverter = new DefaultEnvVarNameConverter();

    @Override
    public Config createConfig(ServletContext servletContext) {

        ResourceFactory resourceFactory = new ServletContainerResourceFactory(servletContext);

        String sourceDefs = servletContext.getInitParameter(STORMPATH_PROPERTIES_SOURCES);
        if (!Strings.hasText(sourceDefs)) {
            sourceDefs = DEFAULT_STORMPATH_PROPERTIES_SOURCES;
        }

        Collection<PropertiesSource> sources = new ArrayList<PropertiesSource>();

        Scanner scanner = new Scanner(sourceDefs);

        while (scanner.hasNextLine()) {

            String line = scanner.nextLine();
            line = Strings.trimWhitespace(line);

            boolean required = false;

            int i = line.lastIndexOf(REQUIRED_TOKEN);
            if (i > 0) {
                required = true;
                line = line.substring(0, i);
                line = Strings.trimWhitespace(line);
            }

            if (ENVVARS_TOKEN.equalsIgnoreCase(line)) {
                sources.add(new FilteredPropertiesSource(
                    new EnvironmentVariablesPropertiesSource(),
                    new FilteredPropertiesSource.Filter() {
                        @Override
                        public String[] map(String key, String value) {
                            if (key.startsWith("STORMPATH_")) {
                                //we want to convert env var naming convention to dotted property convention
                                //to allow overrides.  Overrides work based on overriding identically-named keys:
                                key = envVarNameConverter.toDottedPropertyName(key);
                                return new String[]{key, value};
                            }
                            return null;
                        }
                    }));
            } else if (SYSPROPS_TOKEN.equalsIgnoreCase(line)) {
                sources.add(new FilteredPropertiesSource(
                    new SystemPropertiesSource(),
                    new FilteredPropertiesSource.Filter() {
                        @Override
                        public String[] map(String key, String value) {
                            if (key.startsWith("stormpath.")) {
                                return new String[]{key, value};
                            }
                            return null;
                        }
                    }));
            } else if (CONTEXT_PARAM_TOKEN.equalsIgnoreCase(line)) {
                String value = servletContext.getInitParameter(STORMPATH_PROPERTIES);
                if (Strings.hasText(value)) {
                    sources.add(new ResourcePropertiesSource(new StringResource(value)));
                }
            } else {
                Resource resource = resourceFactory.createResource(line);
                PropertiesSource propertiesSource = new ResourcePropertiesSource(resource);
                if (!required) {
                    propertiesSource = new OptionalPropertiesSource(propertiesSource);
                }
                sources.add(propertiesSource);
            }
        }

        Map<String,String> props = new LinkedHashMap<String, String>();

        for(PropertiesSource source : sources) {
            Map<String,String> srcProps = source.getProperties();
            props.putAll(srcProps);
        }

        Yaml yaml = new Yaml();

        // split string on newline by default
        String[] configFiles = DEFAULT_STORMPATH_PROPERTIES_SOURCES.split(NL);

        for (String configFile : configFiles) {
            if (configFile.endsWith(".properties")) {
                String yamlFile = configFile.replace(".properties", ".yml");
                InputStream in = null;
                try {
                    Resource resource = resourceFactory.createResource(yamlFile);
                    in = resource.getInputStream();
                    if (in != null) {
                        Map config = yaml.loadAs(in, Map.class);
                        props.putAll(getFlattenedMap(config));
                    }
                } catch (IOException io) {
                    //todo: what's the best way to handle this failure
                    io.printStackTrace();
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException io) {
                            // can't close, ignore
                        }
                    }
                }
            }
        }

        return new DefaultConfig(servletContext, props);
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
            if (StringUtils.hasText(path)) {
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
