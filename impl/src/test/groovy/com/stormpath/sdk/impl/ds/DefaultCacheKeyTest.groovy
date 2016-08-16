package com.stormpath.sdk.impl.ds

import com.stormpath.sdk.http.QueryString
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertFalse
import static org.testng.Assert.assertTrue

/**
 * @since 1.0.RC9
 */
class DefaultCacheKeyTest {

    @Test
    void testWithQueryString() {

        def qs = new QueryString([
            "key_one":"value_one",
            "key_two":"value_two"
        ])
        def cacheKey = new DefaultCacheKey("https://mysite.com", qs)

        assertEquals cacheKey.toString(), "https://mysite.com?key_one=value_one&key_two=value_two"
    }

    @Test
    void testWithQueryStringOnURl() {

        // saved as alpha order
        def qs = new QueryString([
            "key_three":"value_three",
            "key_two":"value_two"
        ])
        def cacheKey = new DefaultCacheKey("https://mysite.com?key_one=value_one", qs)

        assertEquals cacheKey.toString(), "https://mysite.com?key_one=value_one&key_three=value_three&key_two=value_two"
    }

    @Test
    void testHashCode() {

        def qs = new QueryString(["key_one":"value_one"])
        def cacheKey = new DefaultCacheKey("https://mysite.com", qs)

        assertEquals cacheKey.hashCode(), "https://mysite.com?key_one=value_one".hashCode()
    }

    @Test
    void testEquals() {

        def qs = new QueryString(["key_one":"value_one"])
        def cacheKey1 = new DefaultCacheKey("https://mysite.com", qs)
        assertTrue cacheKey1.equals(cacheKey1)

        def cacheKey2 = new DefaultCacheKey("https://mysite.com", qs)
        assertTrue cacheKey1.equals(cacheKey2)

        assertFalse cacheKey1.equals("not the right type")
    }
}
