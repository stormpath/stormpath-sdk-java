package com.stormpath.sdk.impl.ds

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

/**
 * @since 1.0.RC9
 */
class EnlistmentTest {

    @Test
    void testInstantiateNull() {

        def enlistment = new Enlistment(null)

        assertEquals enlistment.size(), 0
    }

    @Test
    void testHashCode() {

        def linkedHashMap = new LinkedHashMap<String, Object>()
        linkedHashMap.put("this", "that")

        def enlistment = new Enlistment(["this":"that"])

        assertEquals enlistment.hashCode(), linkedHashMap.hashCode()
    }

    @Test
    void testHashCodeEmpty() {

        def enlistment = new Enlistment(null)

        assertEquals enlistment.hashCode(), 0
    }

    @Test
    void testContainsValue() {

        def enlistment = new Enlistment(["this":"that"])

        assertTrue enlistment.containsValue("that")
    }

    @Test
    void testValues() {

        def enlistment = new Enlistment([
            "this":"that",
            "those":"these"
        ])

        assertEquals enlistment.values().size(), 2
        assertTrue enlistment.values().contains("that")
        assertTrue enlistment.values().contains("these")
    }
}
