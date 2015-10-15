package com.stormpath.sdk.servlet.idsite;

/**
 * @since 1.0.RC5
 */
public interface IdSiteOrganizationContext {

    String getOrganizationNameKey();

    Boolean isUseSubdomain();

    Boolean isShowOrganizationField();
}
