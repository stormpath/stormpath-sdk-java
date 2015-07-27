package com.stormpath.sdk.impl.resource;

import com.stormpath.sdk.impl.ds.InternalDataStore;

import java.util.Date;
import java.util.Map;

/**
 * This is a resource created for testing purposes only.
 *
 * @since 1.0.RC4.6
 */
public class AuditableTestResource extends AbstractExtendableInstanceResource {

    public static final DateProperty CREATED_AT = new DateProperty("createdAt");
    public static final DateProperty MODIFIED_AT = new DateProperty("modifiedAt");

    public AuditableTestResource(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return createPropertyDescriptorMap(CREATED_AT, MODIFIED_AT);
    }

    public Date getCreatedAt() {
        return super.getDateProperty(CREATED_AT);
    }

    public void setCreatedAt(Date date) {
        super.setProperty("createdAt", date);
    }

    public Date getModifiedAt() {
        return super.getDateProperty(MODIFIED_AT);
    }

    public void setModifiedAt(Date date) {
        super.setProperty("modifiedAt", date);
    }
}