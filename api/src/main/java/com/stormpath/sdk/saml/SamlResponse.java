package com.stormpath.sdk.saml;

import com.stormpath.sdk.resource.Resource;

/**
 * A SamlResponse has a single field whose value is the XML of a SAML response according to the SAML specification.
 *
 * @since 1.3.0
 */
public interface SamlResponse extends Resource {

    /**
     * Returns the XML of the SAML response..
     *
     * @return the XML of the SAML response.
     */
    String getValue();
}
