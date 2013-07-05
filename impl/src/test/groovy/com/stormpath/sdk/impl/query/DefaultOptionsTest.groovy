package com.stormpath.sdk.impl.query

import com.stormpath.sdk.group.Groups
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

        options.expand(Groups.ACCOUNTS, 10);

        assertEquals 1, options.expansions.size()
        def exp = options.expansions[0]
        assertTrue exp instanceof CollectionExpansion
        assertEquals exp.name, Groups.ACCOUNTS.name
        assertEquals exp.limit, 10
        assertEquals exp.offset, 0
    }


}
