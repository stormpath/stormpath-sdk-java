package com.stormpath.sdk.resource

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

/**
 *
 * @since 0.8
 */
class ResourceExceptionTest {

    @Test
    void testDefault() {

        def error = new com.stormpath.sdk.error.Error() {

            int getStatus() {
                return 400
            }

            int getCode() {
                return 2000
            }

            String getMessage() {
                return 'foo'
            }

            String getDeveloperMessage() {
                return 'foo bar'
            }

            String getMoreInfo() {
                return 'someUrl'
            }
        }

        def ex = new ResourceException(error);

        assertEquals ex.status, 400
        assertEquals ex.code, 2000
        assertEquals ex.message, 'foo'
        assertEquals ex.developerMessage, 'foo bar'
        assertEquals ex.moreInfo, 'someUrl'
    }


}
