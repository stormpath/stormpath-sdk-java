package com.stormpath.sdk.impl.cache.impl

import com.stormpath.sdk.impl.util.Duration
import org.junit.Test

import java.util.concurrent.TimeUnit

import static org.junit.Assert.*

/**
 * @since 0.8
 */
class DefaultCacheTest {

    @Test
    void testGetReturningNull() {
        def cache = new DefaultCache('foo');
        assertNull cache.get('key')
    }

    @Test
    void testSetTtl() {
        def cache = new DefaultCache('foo')
        def ttl = new Duration(30, TimeUnit.MILLISECONDS)
        cache.setTimeToLive(ttl)
        assertSame ttl, cache.timeToLive
    }

    @Test
    void testSetTti() {
        def cache = new DefaultCache('foo')
        def tti = new Duration(30, TimeUnit.MILLISECONDS)
        cache.setTimeToIdle(tti)
        assertSame tti, cache.timeToIdle
    }

    @Test
    void testClear() {
        def cache = new DefaultCache('foo')
        cache.put('key', 'value')
        assertEquals 1, cache.size()
        cache.clear()
        assertEquals 0, cache.size()
    }

    @Test
    void testToString() {
        def cache = new DefaultCache('foo')
        assertEquals 'DefaultCache \'foo\' (0 entries)', cache.toString()
        cache.put('key', 'value')
        assertEquals 'DefaultCache \'foo\' (1 entries)', cache.toString()
    }

    @Test
    void testPutReplacesPreviousValue() {
        def cache = new DefaultCache('foo', [:], null, null);

        def key = 'key'
        def value1 = 'value1'
        def value2 = 'value2'

        assertNull cache.put(key, value1)

        def prev = cache.put(key, value2)

        assertEquals value1, prev
        assertEquals value2, cache.get(key)
    }

    @Test
    void testTimeToLive() {

        def cache = new DefaultCache('foo', [:], new Duration(10, TimeUnit.MILLISECONDS), null)

        def key = 'key'
        def value = 'value'

        def prev = cache.put(key, value)
        assertNull prev

        def found = cache.get(key)
        assertEquals value, found
        assertEquals 1, cache.size()

        Thread.sleep(15)

        found = cache.get(key)

        assertNull found
        assertEquals 0, cache.size()
    }

    @Test
    void testTimeToIdle() {

        def cache = new DefaultCache('foo', [:], null, new Duration(20, TimeUnit.MILLISECONDS))

        def key = 'key'
        def value = 'value'

        def prev = cache.put(key, value)
        assertNull prev

        def found = cache.get(key)
        assertEquals value, found
        assertEquals 1, cache.size()

        Thread.sleep(10)

        found = cache.get(key)
        assertEquals(value, found)
        assertEquals 1, cache.size()

        Thread.sleep(30)

        found = cache.get(key)
        assertNull found
        assertEquals 0, cache.size()
    }

    @Test
    void testTimeToLiveAndTimeToIdle() {

        def cache = new DefaultCache('foo', [:], new Duration(50, TimeUnit.MILLISECONDS), new Duration(20, TimeUnit.MILLISECONDS))

        def key = 'key'
        def value = 'value'

        def prev = cache.put(key, value)
        assertNull prev

        def found = cache.get(key)
        assertEquals value, found
        assertEquals 1, cache.size()

        //each time we access after sleeping 15 seconds, we should always acquire the value since the last
        //access timestamp is being updated, preventing expunging due to idle.

        Thread.sleep(10)
        found = cache.get(key)
        assertEquals(value, found)
        assertEquals 1, cache.size()

        Thread.sleep(10)
        found = cache.get(key)
        assertEquals(value, found)
        assertEquals 1, cache.size()

        Thread.sleep(10)
        found = cache.get(key)
        assertEquals(value, found)
        assertEquals 1, cache.size()

        //Now we need to ensure that no matter how frequently the value is used (not idle), we still need to remove
        //the value if older than the TTL

        //10 + 10 + 10 = 30.  Add another 30 millis, and we'll be ~ 60 millis, which is older than the TTL of 50 above.
        Thread.sleep(30)

        found = cache.get(key)
        assertNull found
        assertEquals 0, cache.size()
    }
}
