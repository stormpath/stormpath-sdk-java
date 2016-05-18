package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.error.Error;

import java.util.Map;

/**
 * @since 1.0.0
 */
public interface ErrorMapModelFactory {
    Map<String, Object> toErrorMap(Error error);
}