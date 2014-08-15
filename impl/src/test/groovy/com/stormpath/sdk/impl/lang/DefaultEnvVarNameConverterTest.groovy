package com.stormpath.sdk.impl.lang

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals


class DefaultEnvVarNameConverterTest {

    @Test
    void testToEnvVarName() {
        def factory = new DefaultEnvVarNameConverter()

        def name = 'stormpath.apiKey.id'

        def envVarName = factory.toEnvVarName(name);

        assertEquals envVarName, 'STORMPATH_API_KEY_ID'
    }

    @Test
    void testPropNameForApiKeyIdEnvVar() {
        def factory = new DefaultEnvVarNameConverter()

        def name = 'STORMPATH_API_KEY_ID'

        def propName = factory.toDottedPropertyName(name);

        assertEquals propName, 'stormpath.apiKey.id'
    }

    @Test
    void testPropNameForApiKeySecretEnvVar() {
        def factory = new DefaultEnvVarNameConverter()

        def name = 'STORMPATH_API_KEY_SECRET'

        def propName = factory.toDottedPropertyName(name);

        assertEquals propName, 'stormpath.apiKey.secret'
    }

    @Test
    void testPropNameForApiKeyFileEnvVar() {
        def factory = new DefaultEnvVarNameConverter()

        def name = 'STORMPATH_API_KEY_FILE'

        def propName = factory.toDottedPropertyName(name);

        assertEquals propName, 'stormpath.apiKey.file'
    }

    @Test
    void testPropNameForNormalEnvVar() {
        def factory = new DefaultEnvVarNameConverter()

        def name = 'STORMPATH_AUTHENTICATION_SCHEME'

        def propName = factory.toDottedPropertyName(name);

        assertEquals propName, 'stormpath.authentication.scheme'
    }
}
