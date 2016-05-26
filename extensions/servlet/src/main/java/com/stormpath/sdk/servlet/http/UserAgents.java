package com.stormpath.sdk.servlet.http;

import com.stormpath.sdk.servlet.http.impl.DefaultUserAgent;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC8
 */
public class UserAgents {

    public static final String USER_AGENT_REQUEST_ATTRIBUTE_NAME = UserAgents.class.getName() + ".USER_AGENT";

    public static UserAgent get(HttpServletRequest request) {
        DefaultUserAgent ua = (DefaultUserAgent) request.getAttribute(USER_AGENT_REQUEST_ATTRIBUTE_NAME);
        if (ua == null) { //cache for potential later access:
            ua = new DefaultUserAgent(request);
            request.setAttribute(USER_AGENT_REQUEST_ATTRIBUTE_NAME, ua);
        }
        return ua;
    }
}
