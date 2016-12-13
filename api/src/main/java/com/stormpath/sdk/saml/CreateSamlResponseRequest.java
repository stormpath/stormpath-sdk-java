package com.stormpath.sdk.saml;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.resource.Resource;

import java.util.Date;

/**
 * A SamlResponseRequest encapsulates the information needed to prepare a SAML response according to the SAML specification.
 *
 * @since 1.3.0
 */
public interface CreateSamlResponseRequest extends Resource {

    /**
     * Returns the {@link Account} to be represented in this SAML response.
     *
     * @return the {@link Account} to be represented in this SAML response.
     */
    Account getAccount();

    /**
     * Sets the {@link Account} to be represented in this SAML response.
     *
     * @param account the {@link Account} to be represented in this SAML response.
     * @return this instance for method chaining.
     */
    CreateSamlResponseRequest setAccount(Account account);

    /**
     * Returns the {@link RegisteredSamlServiceProvider} to be represented in this SAML response.
     *
     * @return the {@link RegisteredSamlServiceProvider} to be represented in this SAML response.
     */
    RegisteredSamlServiceProvider getServiceProvider();

    /**
     * Sets the {@link RegisteredSamlServiceProvider} to be represented in this SAML response.
     *
     * @param serviceProvider the {@link RegisteredSamlServiceProvider} to be represented in this SAML response.
     * @return this instance for method chaining.
     */
    CreateSamlResponseRequest setServiceProvider(RegisteredSamlServiceProvider serviceProvider);

    /**
     * Returns the request ID to be represented in this SAML response, which should match the request ID
     * that was provided in the initiating SAML AuthnRequest.
     *
     * @return the request ID to be represented in this SAML response.
     */
    String getRequestId();

    /**
     * Sets the request ID to be represented in this SAML response, which should match the request ID
     * that was provided in the initiating SAML AuthnRequest.
     *
     * @param requestId the request ID to be represented in this SAML response.
     * @return this instance for method chaining.
     */
    CreateSamlResponseRequest setRequestId(String requestId);

    /**
     * Returns the issue instant to be represented in this SAML response, which should match the issue instant
     * that was provided in the initiating SAML AuthnRequest.
     *
     * @return the issue instant to be represented in this SAML response.
     */
    Date getAuthnIssueInstant();

    /**
     * Sets the issue instant to be represented in this SAML response, which should match the issue instant
     * that was provided in the initiating SAML AuthnRequest.
     *
     * @param authnIssueInstant the issue instant to be represented in this SAML response.
     * @return this instance for method chaining.
     */
    CreateSamlResponseRequest setAuthnIssueInstant(Date authnIssueInstant);
}
