/*
 * Copyright 2013 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.resource

import org.testng.annotations.Test

import static org.testng.Assert.*

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
                return 'bar'
            }

            String getMoreInfo() {
                return 'someUrl'
            }
        }

        def ex = new ResourceException(error);

        assertEquals ex.status, 400
        assertEquals ex.code, 2000
        assertEquals ex.message, 'HTTP 400, Stormpath 2000 (someUrl): bar'
        assertEquals ex.developerMessage, 'bar'
        assertEquals ex.moreInfo, 'someUrl'
    }


}
