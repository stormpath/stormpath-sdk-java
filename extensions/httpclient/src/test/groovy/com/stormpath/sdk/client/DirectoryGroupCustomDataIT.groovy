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
package com.stormpath.sdk.client

import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.Groups
import org.testng.annotations.Test

/**
 * @since 0.9
 */
class DirectoryGroupCustomDataIT extends AbstractCustomDataIT {

    Directory directory

    @Test
    void testCreateGroupWithCustomData() {

        def app = createApplication();
        directory = retrieveAppDirectory(app);
        deleteOnTeardown(directory);
        deleteOnTeardown(app)

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

        builder = expand ? builder.withResponseOptions(Groups.options().withCustomData()) : builder

        directory.createGroup(builder.build());

        assertValidCustomData(group.href + "/customData", postedCustomData, group.customData, expand)

        deleteOnTeardown(group)

        return group
    }
}
