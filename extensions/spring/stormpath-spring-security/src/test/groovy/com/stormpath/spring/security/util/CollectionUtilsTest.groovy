package com.stormpath.spring.security.util

import org.junit.Assert
import org.junit.Test

/**
 * @since 0.2.0
 */
class CollectionUtilsTest {

    @Test
    public void testEmptyList() {

        def list1 = CollectionUtils.asList(null)
        Assert.assertTrue(list1 instanceof List);
        Assert.assertTrue(list1.size() == 0);

        def list2 = CollectionUtils.asList()
        Assert.assertTrue(list2 instanceof List);
        Assert.assertTrue(list2.size() == 0);

    }


}
