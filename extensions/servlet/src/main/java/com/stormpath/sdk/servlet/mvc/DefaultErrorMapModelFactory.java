package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.error.Error;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 1.0
 */
public class DefaultErrorMapModelFactory implements ErrorMapModelFactory{
    @Override
    public Map<String, Object> toErrorMap(Error error) {
        Map<String, Object> errorMap = new LinkedHashMap<String, Object>();

        errorMap.put("status", error.getStatus());
        errorMap.put("message", error.getMessage());

        return errorMap;
    }
}
