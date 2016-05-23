package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.lang.Strings;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 1.0.RC8
 */
public class RequestParameterFieldValueResolver implements RequestFieldValueResolver {

    @Override
    public String getValue(HttpServletRequest request, String fieldName) {
        String val = request.getParameter(fieldName);
        return Strings.clean(val);
    }

    @Override
    public Map<String, Object> getAllFields(HttpServletRequest request) {
        //If it was a form POST we don't need to know all the fields that where posted
        return new LinkedHashMap<String, Object>();
    }
}
