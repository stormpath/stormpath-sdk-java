package com.stormpath.sdk.servlet.config;

import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IsHTTPSForwardedProtoResolver implements Resolver<Boolean> {

    private static final String HEADER_FORWARDED_PROTO = "X-Forwarded-Proto";

    @Override
    public Boolean get(HttpServletRequest request, HttpServletResponse response) {
        String protocol = request.getHeader(HEADER_FORWARDED_PROTO);
        return protocol != null && protocol.equalsIgnoreCase("https");
    }
}
