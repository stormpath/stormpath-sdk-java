package com.stormpath.sdk.saml;

import com.stormpath.sdk.resource.Resource;

public interface AuthnVerificationRequest extends Resource {
    String getSamlRequest();
    AuthnVerificationRequest setSamlRequest(String samlRequest);
    String getRelayState();
    AuthnVerificationRequest setRelayState(String relayState);
    String getSigAlg();
    AuthnVerificationRequest setSigAlg(String sigAlg);
    String getSignature();
    AuthnVerificationRequest setSignature(String signature);
    String getQueryString();
    AuthnVerificationRequest setQueryString(String queryString);
}
