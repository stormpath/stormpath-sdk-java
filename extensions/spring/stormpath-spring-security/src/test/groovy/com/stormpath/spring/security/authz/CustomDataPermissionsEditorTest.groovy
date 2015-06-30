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
package com.stormpath.spring.security.authz

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Assert
import org.junit.Test

class CustomDataPermissionsEditorTest {

    @Test
    void testConstantValue() {
        //This ensures we don't change the constant value - doing so would not be runtime backwards compatible.
        //If the value is changed in code, this test will fail, as expected (DO NOT change the value!)
        Assert.assertEquals "springSecurityPermissions", CustomDataPermissionsEditor.DEFAULT_CUSTOM_DATA_FIELD_NAME
    }

    @Test(expected = IllegalArgumentException)
    void testNewInstanceWithNullArg() {
        new CustomDataPermissionsEditor(null)
    }

    @Test
    void tesNewInstance() {
        def customData = new MockCustomData()
        def editor = new CustomDataPermissionsEditor(customData)
        Assert.assertSame customData, editor.CUSTOM_DATA
        Assert.assertEquals CustomDataPermissionsEditor.DEFAULT_CUSTOM_DATA_FIELD_NAME, editor.getFieldName()
    }

    @Test
    void testSetFieldName() {
        def customData = new MockCustomData()
        def editor = new CustomDataPermissionsEditor(customData)
        def fieldName = 'foo'
        editor.setFieldName(fieldName)
        Assert.assertEquals fieldName, editor.getFieldName()

        editor.append('bar')

        //assert changed
        Assert.assertFalse customData.containsKey(CustomDataPermissionsEditor.DEFAULT_CUSTOM_DATA_FIELD_NAME)
        Assert.assertTrue customData.containsKey('foo')
        Assert.assertEquals 'bar', editor.getPermissionStrings().iterator().next()
    }

    @Test //tests that we can append a value even if there is not yet a field dedicated for storing perms
    void testAppendWhenNonExistent() {
        def customData = new MockCustomData()
        def editor = new CustomDataPermissionsEditor(customData)

        Assert.assertNull customData.springSecurityPermissions

        editor.append('foo:*')

        Assert.assertNotNull customData.springSecurityPermissions
        Assert.assertTrue customData.springSecurityPermissions instanceof LinkedHashSet
        Assert.assertEquals 1, customData.springSecurityPermissions.size()
        Assert.assertEquals 'foo:*', customData.springSecurityPermissions.iterator().next()
    }

    @Test //tests that we can append a value when there is already a perm collection present
    void testAppendWithExistingList() {
        def customData = new MockCustomData()
        customData.springSecurityPermissions = ['foo:*']

        def editor = new CustomDataPermissionsEditor(customData)

        Assert.assertNotNull customData.springSecurityPermissions
        Assert.assertTrue customData.springSecurityPermissions instanceof List

        editor.append('bar:*')

        Assert.assertNotNull customData.springSecurityPermissions
        Assert.assertTrue customData.springSecurityPermissions instanceof LinkedHashSet
        Assert.assertEquals 2, customData.springSecurityPermissions.size()
        def i = customData.springSecurityPermissions.iterator()

        Assert.assertEquals 'foo:*', i.next()
        Assert.assertEquals 'bar:*', i.next()
    }

    @Test
    void testRemoveWithNullArg() {

        def customData = new MockCustomData()
        def editor = new CustomDataPermissionsEditor(customData)

        Assert.assertNull customData.springSecurityPermissions

        editor.remove(null)

        Assert.assertNull customData.springSecurityPermissions
    }

    @Test
    void testRemoveWithEmptyArg() {

        def customData = new MockCustomData()
        def editor = new CustomDataPermissionsEditor(customData)

        Assert.assertNull customData.springSecurityPermissions

        editor.remove('   ')

        Assert.assertNull customData.springSecurityPermissions
    }

    @Test
    void testRemoveWhenNonExistent() {

        def customData = new MockCustomData()
        def editor = new CustomDataPermissionsEditor(customData)

        Assert.assertNull customData.springSecurityPermissions

        editor.remove('foo')

        Assert.assertNull customData.springSecurityPermissions
    }

    @Test
    void testRemoveWithExistingList() {
        def customData = new MockCustomData()
        customData.springSecurityPermissions = ['foo:*', 'bar']
        def editor = new CustomDataPermissionsEditor(customData)

        def result = editor.getPermissionStrings()

        Assert.assertNotNull result
        Assert.assertEquals 2, result.size()
        def i = result.iterator()
        Assert.assertEquals 'foo:*', i.next()
        Assert.assertEquals 'bar', i.next()

        editor.remove('foo:*')

        result = editor.getPermissionStrings()
        Assert.assertNotNull result
        Assert.assertEquals 1, result.size()
        Assert.assertEquals 'bar', result.iterator().next()
    }

    @Test
    void testRemoveWithExistingSet() {
        def customData = new MockCustomData()
        def editor = new CustomDataPermissionsEditor(customData)

        editor.append('foo:*').append('bar')

        def result = editor.getPermissionStrings()

        Assert.assertNotNull result
        Assert.assertEquals 2, result.size()
        def i = result.iterator()
        Assert.assertEquals 'foo:*', i.next()
        Assert.assertEquals 'bar', i.next()

        editor.remove('foo:*')

        result = editor.getPermissionStrings()
        Assert.assertNotNull result
        Assert.assertEquals 1, result.size()
        Assert.assertEquals 'bar', result.iterator().next()
    }

    @Test
    void testGetPermissionStringsWhenNonExistent() {

        def customData = new MockCustomData()
        def editor = new CustomDataPermissionsEditor(customData)

        Assert.assertNull customData.springSecurityPermissions

        def result = editor.getPermissionStrings();

        Assert.assertNotNull result
        Assert.assertTrue result.isEmpty()
    }

    @Test(expected=UnsupportedOperationException)
    void testGetPermissionStringsReturnsImmutableSet() {

        def customData = new MockCustomData()
        def editor = new CustomDataPermissionsEditor(customData)

        Assert.assertNull customData.springSecurityPermissions

        Set result = editor.getPermissionStrings();

        Assert.assertNotNull result
        Assert.assertTrue result.isEmpty()

        result.add('foo')
    }

    @Test
    void testGetPermissionStringsWithExistingList() {

        def customData = new MockCustomData()
        customData.springSecurityPermissions = ['foo:*']
        def editor = new CustomDataPermissionsEditor(customData)

        def result = editor.getPermissionStrings();

        Assert.assertNotNull result
        Assert.assertEquals 1, result.size()
        Assert.assertEquals 'foo:*', result.iterator().next()
    }

    @Test
    void testGetPermissionStringsWithExistingValuesSomeWithNull() {

        def customData = new MockCustomData()
        customData.springSecurityPermissions = ['foo:*', null, 'bar']
        def editor = new CustomDataPermissionsEditor(customData)

        def result = editor.getPermissionStrings();

        Assert.assertNotNull result
        Assert.assertEquals 2, result.size()
        def i = result.iterator()
        Assert.assertEquals 'foo:*', i.next()
        Assert.assertEquals 'bar', i.next()
    }

    @Test
    void testGetPermissionStringsWithExistingValuesSomeNotStrings() {

        def customData = new MockCustomData()
        def nonString = 123
        customData.springSecurityPermissions = ['foo:*', nonString, 'bar'] //tests user erroneous population
        def editor = new CustomDataPermissionsEditor(customData)

        try {
            editor.getPermissionStrings();
        } catch (IllegalArgumentException iae) {
            String expectedMsg = "CustomData field 'springSecurityPermissions' contains an element that is not a String as " +
                    "required. Element type: " + nonString.getClass().getName() + ", element value: " + nonString
            Assert.assertEquals expectedMsg, iae.getMessage()
        }
    }

    @Test
    void testGetPermissionStringsWithExistingStringArray() {

        ObjectMapper mapper = new ObjectMapper()
        def json = '''
        {
            "springSecurityPermissions": [
                "foo:*",
                "bar"
            ]
        }
        '''
        def m = mapper.readValue(json, Map.class)

        def customData = new MockCustomData()
        customData.springSecurityPermissions = m.springSecurityPermissions
        def editor = new CustomDataPermissionsEditor(customData)

        def result = editor.getPermissionStrings();

        Assert.assertNotNull result
        Assert.assertEquals 2, result.size()
        def i = result.iterator()
        Assert.assertEquals 'foo:*', i.next()
        Assert.assertEquals 'bar', i.next()
    }

    @Test
    void testGetPermissionStringsWithNonListProperty() {

        def value = 42
        def customData = new MockCustomData()
        customData.springSecurityPermissions = value
        def editor = new CustomDataPermissionsEditor(customData)

        try {
            editor.getPermissionStrings();
        } catch (IllegalArgumentException iae) {
            String expectedMsg = "Unable to recognize CustomData field 'springSecurityPermissions' value of type " +
                    value.getClass().getName() + ".  Expected type: Set<String> or List<String>."
            Assert.assertEquals expectedMsg, iae.getMessage();
        }
    }

}
