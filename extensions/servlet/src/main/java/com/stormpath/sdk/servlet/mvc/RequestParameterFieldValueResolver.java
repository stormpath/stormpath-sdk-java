package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.lang.Strings;

import javax.servlet.http.HttpServletRequest;

/**
 * 1.0.RC8
 */
public class RequestParameterFieldValueResolver implements RequestFieldValueResolver {

    @Override
    public String getValue(HttpServletRequest request, String fieldName) {
        String val = request.getParameter(fieldName);
        return Strings.clean(val);
    }
}
