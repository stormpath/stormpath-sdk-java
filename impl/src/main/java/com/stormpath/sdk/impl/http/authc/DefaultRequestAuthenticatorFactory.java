package com.stormpath.sdk.impl.http.authc;

import com.stormpath.sdk.client.AuthenticationScheme;
import com.stormpath.sdk.impl.http.support.RequestAuthenticationException;
import com.stormpath.sdk.lang.Classes;

/**
 * This default factory is responsible of creating a {@link RequestAuthenticator} out of a given {@link AuthenticationScheme}
 * </pre>
 * This implementation returns a {@link SAuthc1RequestAuthenticator} when the authentication scheme is undefined.
 *
 * @since 0.9.3
 */
public class DefaultRequestAuthenticatorFactory implements RequestAuthenticatorFactory {

    /**
     * Creates a {@link RequestAuthenticator} out of the given {@link AuthenticationScheme}.
     *
     * @param scheme the authentication scheme enum defining the request authenticator to be created
     * @return the corresponding `RequestAuthenticator` for the given `AuthenticationScheme`. Returns `SAuthc1RequestAuthenticator` if
     * the authentication scheme is undefined.
     */
    public RequestAuthenticator create(AuthenticationScheme scheme) {

        if (scheme == null) {
            //By default, this factory creates a digest authentication when a scheme is not defined
            return new SAuthc1RequestAuthenticator();
        }

        RequestAuthenticator requestAuthenticator;

        try {
            requestAuthenticator = (RequestAuthenticator) Classes.newInstance(scheme.getRequestAuthenticatorClassName());
        } catch (RuntimeException ex) {
            throw new RequestAuthenticationException("There was an error instantiating " + scheme.getRequestAuthenticatorClassName());
        }

        return requestAuthenticator;
    }

}
