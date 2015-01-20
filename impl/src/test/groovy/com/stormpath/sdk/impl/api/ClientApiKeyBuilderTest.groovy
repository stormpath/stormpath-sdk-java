package com.stormpath.sdk.impl.api

import com.stormpath.sdk.impl.util.StringInputStream
import org.testng.annotations.Test

import static org.testng.Assert.*

class ClientApiKeyBuilderTest {

    @Test
    void testBuildWithUnresolvedId() {

        def builder = new NoDefaultsClientApiKeyBuilder()

        try {
            builder.build();
            fail("Exception expected.");
        } catch (IllegalStateException expected) {
            String msg = expected.getMessage()
            assertTrue msg.startsWith("Unable to find an API Key 'id'")
        }
    }

    @Test
    void testBuildWithUnresolvedSecret() {

        def builder = new NoDefaultsClientApiKeyBuilder()

        builder.setId('foo')

        try {
            builder.build();
            fail("Exception expected.");
        } catch (IllegalStateException expected) {
            String msg = expected.getMessage()
            assertTrue msg.startsWith("Unable to find an API Key 'secret'")
        }
    }

    @Test
    void testBuildWithManuallyConfiguredIdAndSecret() {
        def builder = new NoDefaultsClientApiKeyBuilder()
        def apiKey = builder.setId('foo').setSecret('bar').build()
        assertEquals apiKey.id, 'foo'
        assertEquals apiKey.secret, 'bar'
    }

    @Test
    void testEnvVarsOverrideDefaultFile() {

        def builder = new ClientApiKeyBuilder() {
            @Override
            protected Properties getDefaultApiKeyFileProperties() {
                def props = new Properties()
                props.putAll(['apiKey.id': 'a', 'apiKey.secret': 'as'])
                return props;
            }

            @Override
            protected Properties getEnvironmentVariableProperties() {
                def props = new Properties()
                props.putAll(['apiKey.id': 'b', 'apiKey.secret': 'bs'])
                return props;
            }

            @Override
            protected Properties getSystemProperties() {
                return new Properties()
            }
        }

        def apiKey = builder.build();

        assertEquals apiKey.id, 'b'
        assertEquals apiKey.secret, 'bs'
    }

    @Test
    void testSystemPropertiesOverridePrevious() {

        def builder = new ClientApiKeyBuilder() {
            @Override
            protected Properties getDefaultApiKeyFileProperties() {
                def props = new Properties()
                props.putAll(['apiKey.id': 'a', 'apiKey.secret': 'as'])
                return props;
            }

            @Override
            protected Properties getEnvironmentVariableProperties() {
                def props = new Properties()
                props.putAll(['apiKey.id': 'b', 'apiKey.secret': 'bs'])
                return props;
            }

            @Override
            protected Properties getSystemProperties() {
                def props = new Properties()
                props.putAll(['apiKey.id': 'c', 'apiKey.secret': 'cs'])
                return props;
            }
        }

        def apiKey = builder.build();

        assertEquals apiKey.id, 'c'
        assertEquals apiKey.secret, 'cs'
    }

    @Test
    void testReadApiKeyFileLocation() {

        def builder = new ClientApiKeyBuilder() {

            @Override
            protected Properties getDefaultApiKeyFileProperties() {
                return new Properties();
            }

            @Override
            protected Properties getEnvironmentVariableProperties() {
                return new Properties();
            }

            @Override
            protected Properties getSystemProperties() {
                return new Properties();
            }

            @Override
            protected Reader createFileReader(String apiKeyFileLocation) throws IOException {
                assertEquals apiKeyFileLocation, 'whatever'
                return new StringReader("apiKey.id = foo\napiKey.secret=bar")
            }
        }

        def apiKey = builder.setFileLocation('whatever').build()

        assertEquals apiKey.id, 'foo'
        assertEquals apiKey.secret, 'bar'
    }

    @Test
    void testReadApiKeyFileLocationError() {

        def builder = new ClientApiKeyBuilder() {

            @Override
            protected Properties getDefaultApiKeyFileProperties() {
                return new Properties();
            }

            @Override
            protected Properties getEnvironmentVariableProperties() {
                return new Properties();
            }

            @Override
            protected Properties getSystemProperties() {
                return new Properties();
            }

            @Override
            protected Reader createFileReader(String apiKeyFileLocation) throws IOException {
                assertEquals apiKeyFileLocation, 'whatever'
                throw new IOException('broken')
            }
        }

        try {
            builder.setFileLocation('whatever').build()
            fail()
        } catch (IllegalArgumentException iae) {
            assertEquals iae.message, 'Unable to read properties from specified apiKeyFileLocation [whatever].'
        }
    }

    @Test
    void testReadApiKeyInputStream() {

        def builder = new NoDefaultsClientApiKeyBuilder()
        def is = new StringInputStream("apiKey.id = foo\napiKey.secret=bar")

        def apiKey = builder.setInputStream(is).build()

        assertEquals apiKey.id, 'foo'
        assertEquals apiKey.secret, 'bar'
    }

    @Test
    void testReadApiKeyInputStreamError() {

        def builder = new NoDefaultsClientApiKeyBuilder()
        def is = new ByteArrayInputStream(new byte[0]) {
            @Override
            synchronized int read() {
                throw new IOException('broken')
            }

            @Override
            synchronized int read(byte[] b, int off, int len) {
                throw new IOException('broken')
            }
        }

        try {
            builder.setInputStream(is).build()
            fail()
        } catch (IllegalArgumentException iae) {
            assertEquals iae.message, 'Unable to read properties from specified apiKeyInputStream.'
        }
    }

    @Test
    void testReadApiKeyReader() {

        def builder = new NoDefaultsClientApiKeyBuilder()
        def reader = new StringReader("apiKey.id = foo\napiKey.secret=bar")

        def apiKey = builder.setReader(reader).build()

        assertEquals apiKey.id, 'foo'
        assertEquals apiKey.secret, 'bar'
    }

    @Test
    void testReadApiKeyReaderError() {

        def builder = new NoDefaultsClientApiKeyBuilder()
        def reader = new StringReader('foo') {
            @Override
            int read() throws IOException {
                throw new IOException('broken')
            }

            @Override
            int read(char[] cbuf, int off, int len) throws IOException {
                throw new IOException('broken')
            }
        }

        try {
            builder.setReader(reader).build()
            fail()
        } catch (IllegalArgumentException iae) {
            assertEquals iae.message, 'Unable to read properties from specified apiKeyReader.'
        }
    }

    @Test
    void testReadApiKeyFromPropertiesInstance() {

        def builder = new NoDefaultsClientApiKeyBuilder()
        def props = new Properties()
        props.putAll(['apiKey.id': 'foo', 'apiKey.secret': 'bar'])

        def apiKey = builder.setProperties(props).build()

        assertEquals apiKey.id, 'foo'
        assertEquals apiKey.secret, 'bar'
    }

    @Test
    void testPropertyNames() {

        def builder = new NoDefaultsClientApiKeyBuilder()
        def props = new Properties()
        props.putAll(['foo': 'foo', 'bar': 'bar'])

        def apiKey = builder.setProperties(props).setIdPropertyName('foo').setSecretPropertyName('bar').build()

        assertEquals apiKey.id, 'foo'
        assertEquals apiKey.secret, 'bar'
    }

    class NoDefaultsClientApiKeyBuilder extends ClientApiKeyBuilder {
        @Override
        protected Properties getDefaultApiKeyFileProperties() {
            return new Properties();
        }

        @Override
        protected Properties getEnvironmentVariableProperties() {
            return new Properties();
        }

        @Override
        protected Properties getSystemProperties() {
            return new Properties();
        }

        @Override
        protected Properties getEnvironmentVariableFileProperties() {
            return new Properties();
        }

        @Override
        protected Properties getSystemPropertyFileProperties() {
            return new Properties();
        }
    }
}
