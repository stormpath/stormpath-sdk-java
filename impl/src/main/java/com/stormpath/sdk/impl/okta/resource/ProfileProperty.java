package com.stormpath.sdk.impl.okta.resource;

import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.okta.Profile;


/**
 * This is a {@link com.stormpath.sdk.okta.Profile} backed subclass of a {@link Property}.
 */
public class ProfileProperty extends Property<Profile> {

    public ProfileProperty(String name) {
        super(name, Profile.class);
    }
}
