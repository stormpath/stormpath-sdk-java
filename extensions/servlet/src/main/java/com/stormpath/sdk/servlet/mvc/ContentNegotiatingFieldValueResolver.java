package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.servlet.http.UserAgent;
import com.stormpath.sdk.servlet.http.UserAgents;
import javax.servlet.http.HttpServletRequest;

/**
 * 1.0.RC8
 */
public class ContentNegotiatingFieldValueResolver implements RequestFieldValueResolver {

    private final RequestFieldValueResolver JSON_BODY_RESOLVER = new JacksonFieldValueResolver();
    private final RequestFieldValueResolver REQ_PARAM_RESOLVER = new RequestParameterFieldValueResolver();

    @Override
    public String getValue(HttpServletRequest request, String fieldName) {

        UserAgent ua = UserAgents.get(request);

        if (ua.isJsonPreferred()) {
            return JSON_BODY_RESOLVER.getValue(request, fieldName);
        }

        return REQ_PARAM_RESOLVER.getValue(request, fieldName);
    }
}
