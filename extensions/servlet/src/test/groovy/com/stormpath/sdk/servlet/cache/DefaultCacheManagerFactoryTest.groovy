package com.stormpath.sdk.servlet.cache

import com.stormpath.sdk.impl.cache.DefaultCacheManager
import com.stormpath.sdk.impl.cache.DisabledCacheManager
import org.testng.annotations.Test

import static org.testng.Assert.*

class DefaultCacheManagerFactoryTest {

    @Test
    void testWithNullArg() {
        def factory = new DefaultCacheManagerFactory()
        assertNull factory.createCacheManager(null);
    }

    @Test
    void testWithEmptyArg() {
        def factory = new DefaultCacheManagerFactory()
        assertNull factory.createCacheManager(new HashMap<String, String>());
    }

    @Test
    void testDefaultCreatedWithNullArg() {
        def factory = new DefaultCacheManagerFactory(true)
        assertNotNull factory.createCacheManager();
    }

    @Test
    void testDefaultCreatedWithEmptyArg() {
        def factory = new DefaultCacheManagerFactory(true)
        assertNotNull factory.createCacheManager(new HashMap<String, String>());
    }

    @Test
    void testEnabledConfig() {
        def factory = new DefaultCacheManagerFactory()
        def mgr = factory.createCacheManager(['stormpath.cache.enabled': 'true']);
        assertNotNull mgr
        assertTrue mgr instanceof DefaultCacheManager
    }

    @Test
    void testDisabledConfig() {
        def factory = new DefaultCacheManagerFactory()
        def mgr = factory.createCacheManager(['stormpath.cache.enabled': 'false']);
        assertNotNull mgr
        assertTrue mgr instanceof DisabledCacheManager
    }

    @Test
    void testEnabledConfigWithInvalidValue() {
        def factory = new DefaultCacheManagerFactory()
        try {
            factory.createCacheManager(['stormpath.cache.enabled': 'whatever']);
            fail('expected IllegalArgumentException')
        } catch (IllegalArgumentException iae) {
            assertEquals iae.message, 'stormpath.cache.enabled value must equal true or false'
        }
    }

    @Test
    void testWithDefaultTti() {
        def factory = new DefaultCacheManagerFactory()
        def mgr = factory.createCacheManager(['stormpath.cache.tti': '1001']);
        assertNotNull mgr
        assertEquals mgr.defaultTimeToIdle.value, 1001
    }

    @Test
    void testWithDefaultTtl() {
        def factory = new DefaultCacheManagerFactory()
        def mgr = factory.createCacheManager(['stormpath.cache.ttl': '2002']);
        assertNotNull mgr
        assertEquals mgr.defaultTimeToLive.value, 2002
    }

    @Test
    void testCacheRegionTtl() {
        def factory = new DefaultCacheManagerFactory()
        def mgr = factory.createCacheManager(['stormpath.cache.foo.ttl': '3003']);
        assertNotNull mgr
        def cache = mgr.getCache('foo');
        assertNotNull cache
        assertEquals cache.timeToLive.value, 3003
    }

    @Test
    void testCacheRegionTtlWithNonLongValue() {
        def factory = new DefaultCacheManagerFactory()
        try {
            factory.createCacheManager(['stormpath.cache.foo.ttl': 'whatever']);
            fail('expected IllegalArgumentException')
        } catch (IllegalArgumentException iae) {
            assertEquals iae.message, 'Unable to parse stormpath.cache.foo.ttl value to a long (milliseconds).'
        }
    }

    @Test
    void testCacheRegionTti() {
        def factory = new DefaultCacheManagerFactory()
        def mgr = factory.createCacheManager(['stormpath.cache.foo.tti': '4004']);
        assertNotNull mgr
        def cache = mgr.getCache('foo');
        assertNotNull cache
        assertEquals cache.timeToIdle.value, 4004
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testCacheRegionWithNoSuffix() {
        def factory = new DefaultCacheManagerFactory()
        factory.createCacheManager(['stormpath.cache.foo': '4004']);
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testCacheRegionWithUnrecognizedSuffix() {
        def factory = new DefaultCacheManagerFactory()
        factory.createCacheManager(['stormpath.cache.foo.whatever': '4004']);
    }
}
