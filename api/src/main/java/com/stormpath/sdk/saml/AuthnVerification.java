package com.stormpath.sdk.saml;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.resource.Resource;

import java.util.Date;

/**
 * An AuthnVerification represents the successful results of verifying the signature of a SAML AuthnRequest targeted
 * against a particular Stormpath {@link Application}.
 *
 * @since 1.3.0
 */
public interface AuthnVerification extends Resource {

    /**
     * Returns the relay state provided with the original SAML AuthnRequest, if any was provided.  Otherwise the default
     * relay state configured for the {@link Application}.
     *
     * @return the relay state associated with the SAML AuthnRequest.
     */
    String getRelayState();

    /**
     * Returns the {@link RegisteredSamlServiceProvider} associated with the entityId of the original AuthnRequest.
     *
     * @return the {@link RegisteredSamlServiceProvider} associated with the entityId of the original AuthnRequest.
     */
    RegisteredSamlServiceProvider getServiceProvider();


    /**
     * Returns the request ID provided with the original SAML AuthnRequest.
     *
     * @return the request ID provided with the original SAML AuthnRequest.
     */
    String getRequestId();


    /**
     * Returns the issue instant of the original SAML AuthnRequest.
     *
     * @return the issue instant of the original SAML AuthnRequest.
     */
    Date getAuthnIssueInstant();
}
