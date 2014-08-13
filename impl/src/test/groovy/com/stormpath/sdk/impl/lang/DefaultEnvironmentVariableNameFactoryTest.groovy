package com.stormpath.sdk.impl.lang

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals


class DefaultEnvironmentVariableNameFactoryTest {

    @Test
    void testDefault() {
        def factory = new DefaultEnvironmentVariableNameFactory()

        def name = 'stormpath.apiKey.id'

        def envVarName = factory.createEnvironmentVariableName(name);

        assertEquals envVarName, 'STORMPATH_API_KEY_ID'
    }
}
