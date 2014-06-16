package com.stormpath.sdk.impl.http;

import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.http.HttpRequestBuilder;
import com.stormpath.sdk.lang.Assert;

import java.io.InputStream;
import java.util.Map;

/**
 * DefaultHttpRequestBuilder
 *
 * @since 1.0.RC
 */
public class DefaultHttpRequestBuilder implements HttpRequestBuilder {

    private final HttpMethod method;

    private Map<String, String[]> headers;
    private InputStream body;
    private Map<String, String[]> parameters;
    private String queryParameters;
    private String uri;

    public DefaultHttpRequestBuilder(HttpMethod method) {
        Assert.notNull(method);
        this.method = method;
    }

    @Override
    public HttpRequestBuilder headers(Map<String, String[]> headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public HttpRequestBuilder parameters(Map<String, String[]> parameters) {
        this.parameters = parameters;
        return this;
    }

    @Override
    public HttpRequestBuilder queryParameters(String queryParameters) {
        this.queryParameters = queryParameters;
        return this;
    }

    @Override
    public HttpRequest build() {
        return new DefaultHttpRequest(headers, method, parameters, queryParameters);
    }
}
