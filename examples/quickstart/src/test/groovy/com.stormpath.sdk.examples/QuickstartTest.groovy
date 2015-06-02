/*
 * Copyright 2014 Stormpath, Inc.
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
package quickstart

import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * This test validates that the example code in the JSDK Quickstart Guide (http://docs.stormpath.com/java/quickstart) works
 *
 * @since 1.0.RC4.3
 */
class QuickstartTest {

    @Test
    void testCode() {
        try {
            String[] args={}
            Quickstart.main(args)
        } catch(Exception e){
            fail()
        }
    }
}
