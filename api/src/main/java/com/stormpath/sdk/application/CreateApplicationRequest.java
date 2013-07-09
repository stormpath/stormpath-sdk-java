package com.stormpath.sdk.application;

/**
 * Represents an attempt to create a new {@link Application} record in Stormpath.
 *
 * @see com.stormpath.sdk.tenant.Tenant#createApplication(CreateApplicationRequest)
 * @since 0.8
 */
public interface CreateApplicationRequest {

    /**
     * Returns the Application instance for which a new record will be created in Stormpath.
     *
     * @return the Application instance for which a new record will be created in Stormpath.
     */
    Application getApplication();
}
