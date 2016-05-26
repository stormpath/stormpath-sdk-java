package com.stormpath.sdk.servlet.mvc;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 1.0.RC8
 */
public interface RequestFieldValueResolver {

    String getValue(HttpServletRequest request, String fieldName);

    Map<String, Object> getAllFields(HttpServletRequest request);
}
