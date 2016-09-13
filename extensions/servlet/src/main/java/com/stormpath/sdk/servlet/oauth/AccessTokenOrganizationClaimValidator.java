package com.stormpath.sdk.servlet.oauth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Validates an access token org claim against the resolved Organization provided by a given organization name key resolver
 * <p>
 * https://github.com/stormpath/stormpath-sdk-java/issues/742
 *
 * @since 1.1.0
 */
public interface AccessTokenOrganizationClaimValidator {
    boolean isValid(HttpServletRequest request, HttpServletResponse response, String token);
}
