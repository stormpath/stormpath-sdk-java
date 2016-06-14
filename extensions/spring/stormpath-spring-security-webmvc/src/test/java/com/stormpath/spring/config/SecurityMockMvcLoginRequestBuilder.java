package com.stormpath.spring.config;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import javax.servlet.ServletContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public final class SecurityMockMvcLoginRequestBuilder {

    public static FormLoginRequestBuilder formLogin() {
        return new FormLoginRequestBuilder();
    }

    static final class FormLoginRequestBuilder implements RequestBuilder {
        private String password = "password";
        private RequestPostProcessor postProcessor = csrf();

        public MockHttpServletRequest buildRequest(ServletContext servletContext) {
            MockHttpServletRequest request = post("/login")
                .param("username", "user").param("password", password)
                .accept(MediaType.APPLICATION_FORM_URLENCODED)
                .buildRequest(servletContext);
            return postProcessor.postProcessRequest(request);
        }

        public FormLoginRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        private FormLoginRequestBuilder() {
        }
    }
}
