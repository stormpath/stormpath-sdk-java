package com.stormpath.sdk.impl.ds

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.easymock.IAnswer
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

import static org.easymock.EasyMock.createMock
import static org.easymock.EasyMock.eq
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.isA
import static org.powermock.api.easymock.PowerMock.replay
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue
import static org.testng.Assert.fail

/**
 * @since 1.0.RC9
 */
class JacksonMapMarshallerTest {

    def mapMarshaller

    @BeforeTest
    void setup() {
        mapMarshaller = new JacksonMapMarshaller()
    }

    @Test
    void testGetAndSetObjectMapper() {

        def objectMapper = new ObjectMapper()
        mapMarshaller.setObjectMapper(objectMapper)

        assertEquals mapMarshaller.getObjectMapper(), objectMapper
    }

    @Test
    void testSetPrettyPrint() {

        def objectMapper = new ObjectMapper()
        mapMarshaller.setObjectMapper(objectMapper)

        mapMarshaller.setPrettyPrint(true)

        assertTrue objectMapper.getSerializationConfig().isEnabled(SerializationFeature.INDENT_OUTPUT)
        assertTrue mapMarshaller.isPrettyPrint()
    }

    @Test
    void testMarshalException() {

        def objectMapper = createMock(ObjectMapper)
        expect(objectMapper.writeValueAsString(null)).andAnswer(new IAnswer<String>() {
            @Override
            String answer() throws Throwable {
                throw new IOException("kaboom")
            }
        })
        mapMarshaller.setObjectMapper(objectMapper)

        replay objectMapper

        try {
            mapMarshaller.marshal(null)
            fail("shouldn't be here")
        } catch (MarshalingException e) {
            assertEquals e.getMessage(), "Unable to convert Map to JSON String."
        }
    }

    @Test
    void testUnmarshalStringException() {

        def objectMapper = createMock(ObjectMapper)
        expect(objectMapper.readValue((String)eq("a value"), (TypeReference)isA(TypeReference)))
            .andAnswer(new IAnswer<Map>() {
                @Override
                Map answer() throws Throwable {
                    throw new IOException("kaboom")
                }
            })
        mapMarshaller.setObjectMapper(objectMapper)

        replay objectMapper

        try {
            mapMarshaller.unmarshal("a value")
            fail("shouldn't be here")
        } catch (MarshalingException e) {
            assertEquals e.getMessage(), "Unable to convert JSON String to Map."
        }
    }

    @Test
    void testUnmarshalInputStreamException() {

        def objectMapper = createMock(ObjectMapper)
        def inputStream = createMock(InputStream)
        expect(objectMapper.readValue((InputStream)eq(inputStream), (TypeReference)isA(TypeReference)))
            .andAnswer(new IAnswer<Map>() {
                @Override
                Map answer() throws Throwable {
                    throw new IOException("kaboom")
                }
            })
        mapMarshaller.setObjectMapper(objectMapper)

        replay objectMapper

        try {
            mapMarshaller.unmarshall(inputStream)
            fail("shouldn't be here")
        } catch (MarshalingException e) {
            assertEquals e.getMessage(), "Unable to convert InputStream String to Map."
        }
    }
}
