package com.stormpath.sdk.client

import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.Groups
import org.testng.annotations.Test

import static org.junit.Assert.assertEquals

/**
 * @since 0.9
 */
class DirectoryGroupCustomDataIT extends AbstractCustomDataIT {

    Directory directory

    @Test
    void testCreateGroupWithCustomData() {

        directory = retrieveDirectory()

        assertEquals(directoryHref, directory.href)

        def postedCustomData = createComplexData()
        def group1 = createGroup(directory, postedCustomData, false)
        updateGroup(group1, postedCustomData, createDataForUpdate(), false)
        updateGroup(group1, postedCustomData, createDataForUpdate(), true)
        updateGroup(group1, postedCustomData,[:], false)

        postedCustomData = createComplexData()
        def group2 = createGroup(directory, postedCustomData, true)
        updateGroup(group2, postedCustomData, createDataForUpdate(), true)
        updateGroup(group2, postedCustomData, createDataForUpdate(), false)
        updateGroup(group2, postedCustomData,[:], true)

        postedCustomData = [:]
        def group3 = createGroup(directory, postedCustomData, false)
        updateGroup(group3, postedCustomData,[:], false)
        updateGroup(group3, postedCustomData, createDataForUpdate(), false)

        postedCustomData = [:]
        def group4 = createGroup(directory, postedCustomData, true)
        updateGroup(group4, postedCustomData,[:], true)
        updateGroup(group4, postedCustomData, createDataForUpdate(), true)
    }

    def Group createGroup(Directory directory, Map postedCustomData, boolean expand) {
        def group = newGroupData()

        group.customData.putAll(postedCustomData)

        def builder = Groups.newCreateRequestFor(group)

        builder = expand ? builder.withCustomData() : builder

        directory.createGroup(builder.build());

        assertValidCustomData(group.href + "/customData", postedCustomData, group.customData, expand)

        deleteOnTeardown(group)

        return group
    }
}
