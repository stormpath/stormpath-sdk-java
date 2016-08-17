package com.stormpath.sdk.servlet.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Function;

/**
 * @since 1.1.0
 */
public class JsonFunction<T> implements Function<T, String> {

    private ObjectMapper objectMapper;

    public JsonFunction() {
        this(new ObjectMapper());
    }

    public JsonFunction(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "ObjectMapper cannot be null.");
        this.objectMapper = objectMapper;
    }

    @Override
    public String apply(T value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            String msg = "Cannot convert object value [" + value + "] to JSON string: " + e.getMessage();
            throw new IllegalArgumentException(msg, e);
        }
    }
}
