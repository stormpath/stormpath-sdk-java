package com.stormpath.spring.config;

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.spring.security.token.JwtProviderAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 */
public class OktaLoginSuccessHandler extends StormpathLoginSuccessHandler {

    final private Saver<AuthenticationResult> authenticationResultSaver;

    public OktaLoginSuccessHandler(Client client, Saver<AuthenticationResult> saver, String produces) {
        super(client, saver, produces);
        this.authenticationResultSaver = saver;
    }

    @Override
    protected void saveAccount(HttpServletRequest request, HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {

        JwtProviderAuthenticationToken token = (JwtProviderAuthenticationToken) authentication;
        authenticationResultSaver.set(request, response, token.getAuthenticationResult());
    }
}
