package com.stormpath.sdk.servlet.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;

import javax.servlet.ServletContext;

/**
 * This factory provides an {@link com.fasterxml.jackson.databind.ObjectMapper} instance to be use for JSON serialization
 *
 * @since 1.0.0
 */
public class ObjectMapperFactory extends ConfigSingletonFactory<ObjectMapper> {

    @Override
    protected ObjectMapper createInstance(ServletContext servletContext) throws Exception {
        return new ObjectMapper();
    }
}
