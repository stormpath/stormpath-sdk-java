package com.stormpath.sdk.servlet.json;

import com.stormpath.sdk.lang.Function;
import com.stormpath.sdk.resource.Resource;

import java.util.Map;

/**
 * @since 1.1.0
 */
public class ResourceJsonFunction<T extends Resource> implements Function<T, String> {

    private Function<T, Map<String, Object>> mapFunction;

    private Function<Object, String> jsonFunction;

    public ResourceJsonFunction(Function<T, Map<String, Object>> mapFunction, Function<Object, String> jsonFunction) {
        this.mapFunction = mapFunction;
        this.jsonFunction = jsonFunction;
    }

    @Override
    public String apply(T resource) {
        if (resource == null) {
            return null;
        }
        Object o = mapFunction.apply(resource);
        return jsonFunction.apply(o);
    }
}
