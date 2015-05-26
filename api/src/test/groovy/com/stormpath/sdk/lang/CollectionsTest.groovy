/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.lang

import org.junit.Test

class CollectionsTest {

    @Test
    public void testEmptyList() {

        def list1 = Collections.toList(null)
        org.junit.Assert.assertTrue(list1 instanceof List);
        org.junit.Assert.assertTrue(list1.size() == 0);

        def list2 = Collections.toList()
        org.junit.Assert.assertTrue(list2 instanceof List);
        org.junit.Assert.assertTrue(list2.size() == 0);

    }
}
