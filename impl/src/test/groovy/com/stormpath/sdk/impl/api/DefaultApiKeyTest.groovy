package com.stormpath.sdk.impl.api

import com.stormpath.sdk.impl.ds.InternalDataStore
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

import static org.easymock.EasyMock.createMock
import static org.testng.Assert.*
import static org.testng.AssertJUnit.assertTrue

/**
 * Note: Most of DefaultApiKey is covered by other tests. This test fills out the coverage to be 100%
 *
 * @since 1.0.RC9
 */
class DefaultApiKeyTest {
    def defaultApiKey

    @BeforeTest
    public void setup() {

        defaultApiKey = new DefaultApiKey(createMock(InternalDataStore.class))
    }

    @Test
    public void testGetPropertyDescriptors() {

        def descriptors = defaultApiKey.getPropertyDescriptors()

        assertEquals descriptors.size(), 5

        assertEquals descriptors.get(DefaultApiKey.ID.name), DefaultApiKey.ID
        assertEquals descriptors.get(DefaultApiKey.SECRET.name), DefaultApiKey.SECRET
        assertEquals descriptors.get(DefaultApiKey.STATUS.name), DefaultApiKey.STATUS
        assertEquals descriptors.get(DefaultApiKey.ACCOUNT.name), DefaultApiKey.ACCOUNT
        assertEquals descriptors.get(DefaultApiKey.TENANT.name), DefaultApiKey.TENANT
    }

    @Test
    public void testGetStatus_Null() {
        assertNull defaultApiKey.getStatus()
    }

    @Test
    public void testIsPrintableProperty_SECRET_FALSE() {

        assertFalse defaultApiKey.isPrintableProperty("SECRET")
        assertFalse defaultApiKey.isPrintableProperty("secret")
        assertFalse defaultApiKey.isPrintableProperty("sEcReT")
    }

    @Test
    public void testIsPrintableProperty_NOT_SECRET_TRUE() {

        assertTrue defaultApiKey.isPrintableProperty(DefaultApiKey.ID.name)
        assertTrue defaultApiKey.isPrintableProperty("anything_other_than_SECRET_by_itself")
    }
}
