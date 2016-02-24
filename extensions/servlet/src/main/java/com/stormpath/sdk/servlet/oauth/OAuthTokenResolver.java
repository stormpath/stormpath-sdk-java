package com.stormpath.sdk.servlet.oauth;

import com.stormpath.sdk.servlet.oauth.impl.AccessTokenResolver;

import javax.servlet.ServletRequest;

/**
 * An OAuthTokenResolver can inspect a {@link javax.servlet.ServletRequest ServletRequest} and return either
 * an Access Token or Refresh Token that may be associated with the request due to a previous
 * authentication.
 *
 * @since 1.0.RC10
 */
public interface OAuthTokenResolver {
    String REQUEST_ATTR_NAME = AccessTokenResolver.class.getName();

    /**
     * Returns {@code true} if the specified request has an associated user OAuth Token, {@code false} otherwise.
     * Often used as a guard/check before executing {@link #getRequiredToken(javax.servlet.ServletRequest)}.
     *
     * @param request the current servlet request.
     * @return {@code true} if the specified request has an associated user OAuth Token, {@code false} otherwise.
     * @see #getRequiredToken(javax.servlet.ServletRequest)
     */
    boolean hasToken(ServletRequest request);

    /**
     * Returns the current user OAuth Token (access or refresh) associated with the specified request or {@code null} if no token is
     * associated with the request.
     * <p>In security-sensitive workflows, it might be better to use {@link #getRequiredToken(javax.servlet.ServletRequest)} to help eliminate NullPointerExceptions and conditional branching bugs.</p>
     *
     *
     * @param request the current servlet request.
     * @return the current user OAuth Token (access or refresh) associated with the specified request or {@code null} if no OAuth Token is
     * associated with the request.
     * @see #hasToken(javax.servlet.ServletRequest)
     * @see #getRequiredToken(javax.servlet.ServletRequest)
     */
    String getToken(ServletRequest request);

    /**
     * Returns the current user OAuth Token (access or refresh) associated with the request or throws an IllegalArgumentException
     * exception if there is no account associated with the request.
     *
     * <p>Often this method will be used within a conditional, first checking that {@link
     * #hasToken(javax.servlet.ServletRequest)} returns {@code true}, for example:</p>
     * <pre>
     *     if (AccessTokenResolver.INSTANCE.hasToken(servletRequest)) {
     *         AccessToken accessToken = AccessTokenResolver.INSTANCE.getRequiredToken(servletRequest);
     *         //do something with the account
     *     }
     * </pre>
     *
     * This <em>check-then-use</em> pattern helps eliminate NullPointerExceptions and conditional branching bugs when
     * working with user identities - often desirable in sensitive logic.
     *
     * @param request the current servlet request.
     * @return the current user OAuth Token associated with the request
     */
    String getRequiredToken(ServletRequest request) throws IllegalArgumentException;
}
