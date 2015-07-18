package com.stormpath.sdk.impl.http;

import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.http.HttpRequestBuilder;
import com.stormpath.sdk.lang.Assert;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * DefaultHttpRequestBuilder
 *
 * @since 1.0.RC
 */
public class DefaultHttpRequestBuilder implements HttpRequestBuilder {

    private HttpMethod method;
    private Map<String, String[]> headers;
    private InputStream body;
    private Map<String, String[]> parameters;
    private String queryParameters;
    private String uri;

    public DefaultHttpRequestBuilder(HttpMethod method) {
        Assert.notNull(method);
        this.method = method;
        this.headers = new HashMap<String, String[]>();
        this.parameters = new HashMap<String, String[]>();
    }

    public DefaultHttpRequestBuilder(String key, String[] value, String type) {
        Assert.notNull(type);
        Assert.notNull(value);

        this.headers = new HashMap<String, String[]>();
        this.parameters = new HashMap<String, String[]>();

        if (type.equals("headers")){
            this.headers.put(key, value);
        }

        if (type.equals("parameters")){
            this.parameters.put(key, value);
        }
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
    public HttpRequestBuilder header(String key, String[] value) throws IllegalArgumentException {
        Assert.notNull(key, "key argument is required.");
        Assert.notNull(value, "value argument is required.");

        this.headers.put(key, value);
        return this;
    }

    @Override
    public HttpRequestBuilder parameter(String key, String[] value) throws IllegalArgumentException {
        Assert.notNull(key, "key argument is required.");
        Assert.notNull(value, "value argument is required.");

        this.parameters.put(key, value);
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
