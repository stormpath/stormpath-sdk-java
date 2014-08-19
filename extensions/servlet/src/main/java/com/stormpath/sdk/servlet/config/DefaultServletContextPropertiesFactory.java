package com.stormpath.sdk.servlet.config;

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
import com.stormpath.sdk.servlet.io.ServletContainerResourceFactory;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Scanner;

public class DefaultServletContextPropertiesFactory implements ServletContextPropertiesFactory {

    public static final String STORMPATH_PROPERTIES         = "stormpath.properties";
    public static final String STORMPATH_PROPERTIES_SOURCES = STORMPATH_PROPERTIES + ".sources";

    public static final  String ENVVARS_TOKEN   = "envvars";
    public static final  String SYSPROPS_TOKEN  = "sysprops";
    public static final  String INITPARAM_TOKEN = "initparam";
    private static final String NL              = "\n";

    private static final String REQUIRED_TOKEN = "(required)";

    public static final String DEFAULT_STORMPATH_PROPERTIES_SOURCES =
        ENVVARS_TOKEN + NL +
        ClasspathResource.SCHEME_PREFIX + STORMPATH_PROPERTIES + NL +
        "/WEB-INF/stormpath.properties" + NL +
        INITPARAM_TOKEN + NL +
        SYSPROPS_TOKEN;

    private static final EnvVarNameConverter envVarNameConverter = new DefaultEnvVarNameConverter();

    @Override
    public Properties getProperties(ServletContext servletContext) {

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
            } else if (INITPARAM_TOKEN.equalsIgnoreCase(line)) {
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

        Properties properties = new Properties();

        for(PropertiesSource source : sources) {
            Properties srcProps = source.getProperties();
            properties.putAll(srcProps);
        }

        return properties;
    }
}
