package com.stormpath.sdk.servlet.idsite;

/**
 * @since 1.0.RC5
 */
public class DefaultIdSiteOrganizationContext implements IdSiteOrganizationContext {

    private final String organizationNameKey;
    private final Boolean useSubdomain;
    private final Boolean showOrganizationField;

    public DefaultIdSiteOrganizationContext(String organizationNameKey, Boolean useSubdomain, Boolean showOrganizationField) {
        this.organizationNameKey = organizationNameKey;
        this.useSubdomain = useSubdomain;
        this.showOrganizationField = showOrganizationField;
    }

    @Override
    public String getOrganizationNameKey() {
        return organizationNameKey;
    }

    @Override
    public Boolean isUseSubdomain() {
        return useSubdomain;
    }

    @Override
    public Boolean isShowOrganizationField() {
        return showOrganizationField;
    }
}
