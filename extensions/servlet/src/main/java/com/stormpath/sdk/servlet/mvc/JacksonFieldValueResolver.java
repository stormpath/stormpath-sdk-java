package com.stormpath.sdk.servlet.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormpath.sdk.lang.Strings;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * 1.0.RC8
 */
public class JacksonFieldValueResolver implements RequestFieldValueResolver {

    protected static final String MARSHALLED_OBJECT = JacksonFieldValueResolver.class.getName() + ".MARSHALLED_OBJECT";

    ObjectMapper objectMapper = new ObjectMapper();

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getValue(HttpServletRequest request, String fieldName) {
        Map<String, Object> map = ensureBodyMap(request);
        Object value = map.get(fieldName);
        return value != null ? Strings.clean(value.toString()) : null;
    }

    @Override
    public Map<String, Object> getAllFields(HttpServletRequest request) {
        return ensureBodyMap(request);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> ensureBodyMap(HttpServletRequest request) {
        Map<String, Object> map = (Map<String, Object>) request.getAttribute(MARSHALLED_OBJECT);
        if (map == null) {
            map = Collections.emptyMap();
            boolean bodyExists =
                    request.getContentLength() > 0 ||
                            //https://tools.ietf.org/html/rfc7230#section-3.3.2
                            //TODO not sure about this check contentLength should be enough need to check the RCF ref in the comment above
                            request.getHeader("Transfer-Encoding") != null;
            if (bodyExists) {
                map = readJsonBody(request);
            }
            request.setAttribute(MARSHALLED_OBJECT, map); //cache for repeated access;
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> readJsonBody(HttpServletRequest request) {
        try {
            return (Map<String, Object>) getObjectMapper().readValue(request.getInputStream(), Map.class);
        } catch (IOException e) {
            String msg = "Unable to read JSON value from request body: " + e.getMessage();
            throw new IllegalArgumentException(msg, e);
        }
    }
}
