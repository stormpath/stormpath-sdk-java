package com.stormpath.sdk.impl.http

import com.stormpath.sdk.http.HttpHeaders
import com.stormpath.sdk.http.HttpMethod
import com.stormpath.sdk.http.MediaType
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import java.nio.charset.Charset
import java.text.SimpleDateFormat

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertFalse
import static org.testng.Assert.assertNull
import static org.testng.Assert.assertTrue
import static org.testng.Assert.fail

/**
 * @since 1.0.RC9
 */
class HttpHeadersTest {

    def httpHeaders

    @BeforeMethod
    void setup() {

        httpHeaders = new HttpHeaders()
    }

    @Test
    void testReadOnly() {

        httpHeaders.add("GOOD", "GOOD")
        httpHeaders = HttpHeaders.readOnlyHttpHeaders(httpHeaders)

        try {
            httpHeaders.add("NO_GOOD", "NO_GOOD")
            fail("shouldn't be here")
        } catch (Exception e) {
            assertTrue e instanceof UnsupportedOperationException
        }
    }

    @Test
    void testAccept() {

        def mediaTypes = [MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED]
        httpHeaders.setAccept(mediaTypes)

        def acceptMediaTypes = httpHeaders.getAccept()
        assertEquals acceptMediaTypes.size(), 2
        assertTrue acceptMediaTypes.contains(MediaType.APPLICATION_JSON)
        assertTrue acceptMediaTypes.contains(MediaType.APPLICATION_FORM_URLENCODED)
    }

    @Test
    void testAcceptCharsetEmpty() {

        def acceptCharSets = httpHeaders.getAcceptCharset()
        assertEquals acceptCharSets.size(), 0
    }

    @Test
    void testAcceptCharset() {

        def charSets = [Charset.forName("UTF-8"), Charset.forName("UTF-16")]
        httpHeaders.setAcceptCharset(charSets)

        def acceptCharSets = httpHeaders.getAcceptCharset()
        assertEquals acceptCharSets.size(), 2
        assertTrue acceptCharSets.contains(Charset.forName("UTF-8"))
        assertTrue acceptCharSets.contains(Charset.forName("UTF-16"))
    }

    @Test
    void testAllowMethodEmpty() {

        def allowedMethods = httpHeaders.getAllow()
        assertEquals allowedMethods.size(), 0
    }

    @Test
    void testAllowMethod() {

        def methods = [HttpMethod.GET, HttpMethod.POST] as Set
        httpHeaders.setAllow(methods)

        def allowedMethods = httpHeaders.getAllow()
        assertEquals allowedMethods.size(), 2
        assertTrue allowedMethods.contains(HttpMethod.GET)
        assertTrue allowedMethods.contains(HttpMethod.POST)
    }

    @Test
    void testCacheControl() {

        httpHeaders.setCacheControl("private, max-age=0, no-cache")

        assertEquals httpHeaders.getCacheControl(), "private, max-age=0, no-cache"
        assertEquals httpHeaders.get("Cache-Control"), ["private, max-age=0, no-cache"]
    }

    @Test
    void testContentDispositionFormData() {

        httpHeaders.setContentDispositionFormData("CONTROL_NAME", "myfile.pdf")

        assertEquals httpHeaders.get("Content-Disposition"), ['form-data; name="CONTROL_NAME"; filename="myfile.pdf"']
    }

    @Test
    void testContentDispositionFormDataNullFile() {

        httpHeaders.setContentDispositionFormData("CONTROL_NAME", null)

        assertEquals httpHeaders.get("Content-Disposition"), ['form-data; name="CONTROL_NAME"']
    }

    @Test
    void testContentLength() {

        httpHeaders.setContentLength(10) // sets it
        httpHeaders.setContentLength(-1) // removes it
        assertNull httpHeaders.get("Content-Length")
        assertEquals httpHeaders.getContentLength(), -1

        httpHeaders.setContentLength(10)
        assertEquals httpHeaders.get("Content-Length"), ["10"]
        assertEquals httpHeaders.getContentLength(), 10
    }

    @Test
    void testContentType() {

        assertNull httpHeaders.getContentType()

        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED)
        assertEquals httpHeaders.getContentType(), MediaType.APPLICATION_FORM_URLENCODED
    }

    @Test
    void testDate() {

        assertEquals httpHeaders.getDate(), -1

        def dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        def now = new Date().time
        httpHeaders.setDate(now)

        assertEquals httpHeaders.get("Date"), [dateFormat.format(new Date(now))]

        long droppedMillis = 1000 * (now/1000).longValue()
        assertEquals httpHeaders.getDate(), droppedMillis
    }

    @Test
    void testETag() {

        httpHeaders.setETag('"123456789"')
        assertEquals httpHeaders.get("ETag"), ['"123456789"']
        assertEquals httpHeaders.getETag(), '"123456789"'

        httpHeaders.setETag('W/"123456789"')
        assertEquals httpHeaders.get("ETag"), ['W/"123456789"']
        assertEquals httpHeaders.getETag(), 'W/"123456789"'

        httpHeaders.setETag(null)
        assertEquals httpHeaders.get("ETag"), [null]
        assertEquals httpHeaders.getETag(), null
    }

    @Test
    void testExpires() {

        def dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        def now = new Date().time
        httpHeaders.setExpires(now)

        assertEquals httpHeaders.get("Expires"), [dateFormat.format(new Date(now))]

        long droppedMillis = 1000 * (now/1000).longValue()
        assertEquals httpHeaders.getExpires(), droppedMillis
    }

    @Test
    void testIfModifiedSince() {

        def dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        def now = new Date().time
        httpHeaders.setIfModifiedSince(now)

        assertEquals httpHeaders.get("If-Modified-Since"), [dateFormat.format(new Date(now))]

        long droppedMillis = 1000 * (now/1000).longValue()
        assertEquals httpHeaders.getIfNotModifiedSince(), droppedMillis
    }

    @Test
    void testIfNoneMatch() {

        assertEquals httpHeaders.get("If-None-Match"), null
        assertEquals httpHeaders.getIfNoneMatch(), []

        httpHeaders.setIfNoneMatch("blarg")
        assertEquals httpHeaders.get("If-None-Match"), ["blarg"]
        assertEquals httpHeaders.getIfNoneMatch(), ["blarg"]

        httpHeaders.setIfNoneMatch(["foo", "bar"])
        assertEquals httpHeaders.get("If-None-Match"), ["foo, bar"]
        assertEquals httpHeaders.getIfNoneMatch(), ["foo", "bar"]
    }

    @Test
    void testLastModified() {

        def dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        def now = new Date().time
        httpHeaders.setLastModified(now)

        assertEquals httpHeaders.get("Last-Modified"), [dateFormat.format(new Date(now))]

        long droppedMillis = 1000 * (now/1000).longValue()
        assertEquals httpHeaders.getLastModified(), droppedMillis
    }

    @Test
        void testLocation() {

        assertEquals httpHeaders.get("Location"), null
        assertEquals httpHeaders.getLocation(), null

        def location = "https://mylocation.com"
        def uri = new URI(location)

        httpHeaders.setLocation(uri)
        assertEquals httpHeaders.get("Location"), [location]
        assertEquals httpHeaders.getLocation(), uri
    }

    @Test
    void testPragma() {

        httpHeaders.setPragma("no-cache")
        assertEquals httpHeaders.get("Pragma"), ["no-cache"]
        assertEquals httpHeaders.getPragma(), "no-cache"
    }

    @Test
    void testSetAll() {

        def headerMap = [
            "Pragma":"no-cache",
            "Content-Type":MediaType.APPLICATION_FORM_URLENCODED_VALUE
        ]
        httpHeaders.setAll(headerMap)
        assertEquals httpHeaders.getPragma(), "no-cache"
        assertEquals httpHeaders.getContentType(), MediaType.APPLICATION_FORM_URLENCODED
    }

    @Test
    void testSingleValueMap() {

        def headerMap = [
            "Pragma":"no-cache",
            "Content-Type":MediaType.APPLICATION_FORM_URLENCODED_VALUE
        ]
        httpHeaders.setAll(headerMap)

        headerMap = httpHeaders.toSingleValueMap()
        assertEquals headerMap.size(), 2
        assertEquals headerMap.get("Pragma"), "no-cache"
        assertEquals headerMap.get("Content-Type"), MediaType.APPLICATION_FORM_URLENCODED_VALUE
    }

    @Test
    void testEmpty() {

        assertTrue httpHeaders.isEmpty()

        httpHeaders.setLocation(new URI("https://mylocation.com"))

        assertFalse httpHeaders.isEmpty()

        httpHeaders.clear()

        assertTrue httpHeaders.isEmpty()
    }

    @Test
    void testContainsKey() {

        httpHeaders.setLocation(new URI("https://mylocation.com"))
        assertTrue httpHeaders.containsKey("Location")
    }

    @Test
    void testContainsValue() {

        httpHeaders.setLocation(new URI("https://mylocation.com"))
        assertTrue httpHeaders.containsValue(["https://mylocation.com"])
    }

    @Test
    void testPut() {

        httpHeaders.put("Pragma", ["no-cache"])
        assertEquals httpHeaders.getPragma(), "no-cache"
    }

    @Test
    void testPutAll() {

        httpHeaders.putAll([
            "Pragma":["no-cache"],
            "ETag":["123456789"]
        ])
        assertEquals httpHeaders.getPragma(), "no-cache"
        assertEquals httpHeaders.getETag(), "123456789"
    }

    @Test
    void testKeySet() {

        httpHeaders.putAll([
            "Pragma":["no-cache"],
            "ETag":["123456789"]
        ])

        def keySet = httpHeaders.keySet()
        assertEquals keySet.size(), 2
        assertTrue keySet.contains("Pragma")
        assertTrue keySet.contains("ETag")
    }

    @Test
    void testValues() {

        httpHeaders.putAll([
            "Pragma":["no-cache"],
            "ETag":["123456789"]
        ])

        def values = httpHeaders.values()
        assertEquals values.size(), 2
        assertTrue values.contains(["no-cache"])
        assertTrue values.contains(["123456789"])
    }

    @Test
    void testToString() {

        httpHeaders.putAll([
            "Pragma":["no-cache"],
            "ETag":["123456789"]
        ])
        assertEquals httpHeaders.toString(), "{Pragma=[no-cache], ETag=[123456789]}"
    }

    @Test
    void testEquals() {

        assertTrue httpHeaders.equals(httpHeaders)

        def notHeaders = "NOT_HEADERS"
        assertFalse httpHeaders.equals(notHeaders)

        def other = new HttpHeaders()
        other.setExpires(3600)
        httpHeaders.setExpires(3600)
        assertTrue other.equals(httpHeaders)
    }

    @Test
    void testHashCode() {

        def location = "https://mylocation.com"

        def other = new HttpHeaders()
        other.setExpires(3600)
        other.setLocation(new URI(location))

        httpHeaders.setExpires(3600)
        httpHeaders.setLocation(new URI(location))

        assertEquals httpHeaders.hashCode(), other.hashCode()
    }
}
