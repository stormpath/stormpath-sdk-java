package com.stormpath.spring.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormpath.sdk.servlet.filter.ContentNegotiationResolver;
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.UnresolvedMediaTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ContentNegotiationAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(ContentNegotiationAuthenticationFilter.class);

    @Value("#{ @environment['stormpath.web.produces'] ?: 'application/json, text/html' }")
    protected String produces;

    private boolean postOnly = true;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        MediaType mediaType;
        try {
            mediaType = ContentNegotiationResolver.INSTANCE.getContentType(request, response, MediaType.parseMediaTypes(produces));
        } catch (UnresolvedMediaTypeException umt) {
            throw new AuthenticationServiceException("Unresolved media type: " + umt.getMessage(), umt);
        }

        if (!MediaType.APPLICATION_JSON.equals(mediaType)) {
            return super.attemptAuthentication(request, response);
        }

        UsernamePasswordAuthenticationToken authRequest = getUserNamePasswordAuthenticationToken(request);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return getAuthenticationManager().authenticate(authRequest);
    }

    @SuppressWarnings("unchecked")
    private UsernamePasswordAuthenticationToken getUserNamePasswordAuthenticationToken(HttpServletRequest request) {
        String body = getRequestBody(request);

        Map<String, String> loginProps;
        try {
            loginProps = new ObjectMapper().readValue(body, HashMap.class);
        } catch(IOException ex) {
            log.error("Couldn't map request body: '{}': {}", body, ex.getMessage(), ex);
            return null;
        }

        return new UsernamePasswordAuthenticationToken(loginProps.get("login"), loginProps.get("password"));
    }

    private String getRequestBody(HttpServletRequest request) {
        BufferedReader bufferedReader = null;
        StringBuffer sb = new StringBuffer();
        try {
            bufferedReader =  request.getReader();
            char[] charBuffer = new char[128];
            int bytesRead;
            while ( (bytesRead = bufferedReader.read(charBuffer)) != -1 ) {
                sb.append(charBuffer, 0, bytesRead);
            }
        } catch (IOException ex) {
            log.error("Problem reading request body: {}", ex.getMessage(), ex);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    log.error("Problem closing reader: {}", ex.getMessage(), ex);
                }
            }
        }
        return sb.toString();
    }
}