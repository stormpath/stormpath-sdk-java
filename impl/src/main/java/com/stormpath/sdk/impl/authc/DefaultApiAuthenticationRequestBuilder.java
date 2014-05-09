package com.stormpath.sdk.impl.authc;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.ApiAuthenticationRequestBuilder;
import com.stormpath.sdk.authc.ApiAuthenticationResult;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.lang.Assert;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC
 */
public class DefaultApiAuthenticationRequestBuilder implements ApiAuthenticationRequestBuilder {

    private final HttpServletRequest httpServletRequest;

    private final HttpRequest httpRequest;

    private final Application application;

    public DefaultApiAuthenticationRequestBuilder(Application application, HttpServletRequest httpServletRequest) {
        this(application, httpServletRequest, null);
        Assert.notNull(httpServletRequest);
    }

    public DefaultApiAuthenticationRequestBuilder(Application application, HttpRequest httpRequest) {
        this(application, null, httpRequest);
        Assert.notNull(httpServletRequest);
    }

    private DefaultApiAuthenticationRequestBuilder(Application application, HttpServletRequest httpServletRequest, HttpRequest httpRequest) {
        Assert.notNull(application, "application cannot be null.");
        this.application = application;
        this.httpServletRequest = httpServletRequest;
        this.httpRequest = httpRequest;
    }

    @Override
    public ApiAuthenticationResult execute() {

        AuthenticationRequest request;

        if (httpServletRequest != null) {
            request = new ApiAuthenticationRequestFactory().createFrom(httpServletRequest);
        } else {
            request =  new ApiAuthenticationRequestFactory().createFrom(httpRequest);
        }

        AuthenticationResult result = application.authenticateAccount(request);

        Assert.isInstanceOf(ApiAuthenticationResult.class, result);

        return (ApiAuthenticationResult) result;
    }
}
