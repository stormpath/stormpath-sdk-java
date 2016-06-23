package com.stormpath.sdk.impl.http;

import java.util.List;
import java.util.Map;

/**
 * This class is used to hold key/value pairs in a ThreadLocal.
 */
public abstract class HttpHeadersHolder {
    private static ThreadLocal<Map<String, List<String>>> current = new ThreadLocal<>();

    public static void set(Map<String, List<String>> headers) {
        current.set(headers);
    }

    public static Map<String, List<String>> get() {
        return current.get();
    }

    public static void clear() {
        current.remove();
    }
}
