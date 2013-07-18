package com.stormpath.sdk.client

import com.stormpath.sdk.cache.Cache
import com.stormpath.sdk.cache.CacheManager
import com.stormpath.sdk.cache.Caches
import com.stormpath.sdk.impl.cache.DefaultCacheManager
import com.stormpath.sdk.impl.cache.DisabledCacheManager
import org.testng.annotations.Test

import static org.testng.Assert.assertSame
import static org.testng.Assert.assertTrue

/**
 *
 * @since 0.8
 */
class ClientBuilderTest {

    @Test
    void testDefault() {

        Client client = new ClientBuilder().setApiKey(new DefaultApiKey("fakeId", "fakeSecret")).build()

        //caching disabled by default (end-user must explicilty enable it based on their caching preferences):
        assertTrue client.dataStore.cacheManager instanceof DisabledCacheManager
    }

    @Test
    void testSetCustomCacheManager() {

        //dummy implementation:
        def cacheManager = new CacheManager() {
            def <K, V> Cache<K, V> getCache(String name) {
                return null
            }
        }

        Client client = new ClientBuilder()
                .setApiKey(new DefaultApiKey("fakeId", "fakeSecret"))
                .setCacheManager(cacheManager)
                .build()

        assertSame client.dataStore.cacheManager, cacheManager
    }

    @Test
    void testDefaultCacheManager() {

        Client client = new ClientBuilder()
            .setApiKey(new DefaultApiKey("fakeId", "fakeSecret"))
            .setCacheManager(Caches.newCacheManager().build())
            .build()

        def cm = client.dataStore.cacheManager

        assertTrue cm instanceof DefaultCacheManager
    }
}
