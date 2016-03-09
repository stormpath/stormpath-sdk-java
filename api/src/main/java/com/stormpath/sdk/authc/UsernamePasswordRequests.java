package com.stormpath.sdk.authc;

import com.stormpath.sdk.lang.Classes;

/**
 * Static utility/helper methods for working with username/password-based {@link AuthenticationRequest}s.  Most methods
 * are <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for forming
 * Application-specific <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent DSL</a> queries. For example:
 * <pre>
 * AuthenticationRequest request = <b>UsernamePasswordRequests.builder()</b>
 *                         .setUsernameOrEmail(username)
 *                         .setPassword(submittedRawPlaintextPassword)
 *                         .build();
 * Account authenticated = application.authenticateAccount(request).getAccount();
 * </pre>
 *
 * @see UsernamePasswordRequestBuilder
 *
 * @since 1.0.RC9
 */
public class UsernamePasswordRequests {

    /**
     * Returns a new {@link UsernamePasswordRequestBuilder} instance, used to construct {@link UsernamePasswordRequest}s.
     *
     * @return a new {@link UsernamePasswordRequestBuilder} instance, used to construct {@link UsernamePasswordRequest}s.
     * @since 1.0.RC5
     */
    public static UsernamePasswordRequestBuilder builder() {
        return (UsernamePasswordRequestBuilder) Classes.newInstance("com.stormpath.sdk.impl.authc.DefaultUsernamePasswordRequestBuilder");
    }

    /**
     * Returns a new {@link BasicAuthenticationOptions} instance, that may be used to customize the {@link com.stormpath.sdk.authc.AuthenticationResult
     * AuthenticationResult} resource that will be obtained after a successful authentication.  For example:
     *  <pre>
     * AuthenticationRequest request = UsernamePasswordRequests.builder()
     *                         .setUsernameOrEmail(username)
     *                         .setPassword(submittedRawPlaintextPassword)
     *                         <b>.withResponseOptions(UsernamePasswordRequests.options().withAccount())</b>
     *                         .build();
     * Account authenticated = application.authenticateAccount(request).getAccount();
     * </pre>
     *
     *
     * @return a new {@link BasicAuthenticationOptions} instance, that may be used to customize the {@link com.stormpath.sdk.authc.AuthenticationResult
     * AuthenticationResult} resource that will be obtained after a successful authentication.
     * @see com.stormpath.sdk.authc.UsernamePasswordRequestBuilder#withResponseOptions(BasicAuthenticationOptions)
     * @since 1.0.RC5
     */
    public static BasicAuthenticationOptions options() {
        return (BasicAuthenticationOptions) Classes.newInstance("com.stormpath.sdk.impl.authc.DefaultBasicAuthenticationOptions");
    }
}
