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
package com.stormpath.sdk.impl.query

import com.stormpath.sdk.impl.group.DefaultGroup
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

/**
 *
 * @since 0.8
 */
class DefaultOptionsTest {

    @Test
    void testWithLimit() {
        DefaultOptions options = new DefaultOptions();

        options.expand(DefaultGroup.ACCOUNTS, 10);

        assertEquals 1, options.expansions.size()
        def exp = options.expansions[0]
        assertTrue exp instanceof CollectionExpansion
        assertEquals exp.name, DefaultGroup.ACCOUNTS.name
        assertEquals exp.limit, 10
        assertEquals exp.offset, 0
    }


}
