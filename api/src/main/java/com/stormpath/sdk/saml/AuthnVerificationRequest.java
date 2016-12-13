package com.stormpath.sdk.saml;

import com.stormpath.sdk.resource.Resource;

/**
 * An AuthnVerificationRequest encapsulates the fields of a SAML AuthnRequest targeted
 * against a particular Stormpath Application.
 *
 * @since 1.3.0
 */
public interface AuthnVerificationRequest extends Resource {

    /**
     * Returns the value of the SAMLRequest parameter provided with the incoming AuthnRequest.
     *
     * @return the value of the SAMLRequest parameter provided with the incoming AuthnRequest.
     */
    String getSamlRequest();

    /**
     * Sets the value of the SAMLRequest parameter provided with the incoming AuthnRequest.
     *
     * @param samlRequest the value of the SAMLRequest parameter from the incoming AuthnRequest.
     * @return this instance for method chaining.
     */
    AuthnVerificationRequest setSamlRequest(String samlRequest);

    /**
     * Returns the value of the RelayState parameter provided with the incoming AuthnRequest.
     *
     * @return the value of the RelayState parameter provided with the incoming AuthnRequest.
     */
    String getRelayState();

    /**
     * Sets the value of the RelayState parameter provided with the incoming AuthnRequest.
     *
     * @param relayState the value of the RelayState parameter from the incoming AuthnRequest.
     * @return this instance for method chaining.
     */
    AuthnVerificationRequest setRelayState(String relayState);

    /**
     * Returns the value of the SigAlg parameter provided with the incoming AuthnRequest.
     *
     * @return the value of the SigAlg parameter provided with the incoming AuthnRequest.
     */
    String getSigAlg();

    /**
     * Sets the value of the SigAlg parameter provided with the incoming AuthnRequest.
     *
     * @param sigAlg the value of the SigAlg parameter from the incoming AuthnRequest.
     * @return this instance for method chaining.
     */
    AuthnVerificationRequest setSigAlg(String sigAlg);

    /**
     * Returns the value of the Signature parameter provided with the incoming AuthnRequest.
     *
     * @return the value of the Signature parameter provided with the incoming AuthnRequest.
     */
    String getSignature();

    /**
     * Sets the value of the Signature parameter provided with the incoming AuthnRequest.
     *
     * @param signature the value of the Signature parameter from the incoming AuthnRequest.
     * @return this instance for method chaining.
     */
    AuthnVerificationRequest setSignature(String signature);

    /**
     * When the AuthnRequest is submitted via HTTP GET, returns the query string of the incoming request.
     * Otherwise null.
     *
     * @return the query string of the incoming AuthnRequest, or null if the AuthnRequest was submitted with HTTP POST.
     */
    String getQueryString();

    AuthnVerificationRequest setQueryString(String queryString);
}
