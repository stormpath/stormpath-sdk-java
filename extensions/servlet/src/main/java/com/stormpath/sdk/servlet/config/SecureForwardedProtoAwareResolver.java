package com.stormpath.sdk.servlet.config;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SecureForwardedProtoAwareResolver implements Resolver<Boolean> {

    private final Resolver<Boolean> isHTTPSForwardedProtoResolver;
    private final Resolver<Boolean> secureRequiredExceptForLocalhostResolver;

    public SecureForwardedProtoAwareResolver(Resolver<Boolean> isHTTPSForwardedProtoResolver, Resolver<Boolean> secureRequiredExceptForLocalhostResolver) {
        Assert.notNull(isHTTPSForwardedProtoResolver, "isHTTPSForwardedProtoResolver resolver cannot be null.");
        Assert.notNull(secureRequiredExceptForLocalhostResolver, "secureRequiredExceptForLocalhost resolver cannot be null.");
        this.isHTTPSForwardedProtoResolver = isHTTPSForwardedProtoResolver;
        this.secureRequiredExceptForLocalhostResolver = secureRequiredExceptForLocalhostResolver;
    }

    @Override
    public Boolean get(HttpServletRequest request, HttpServletResponse response) {
        if (this.isHTTPSForwardedProtoResolver.get(request, response)) {
            return false;
        }
        return this.secureRequiredExceptForLocalhostResolver.get(request, response);
    }
}
