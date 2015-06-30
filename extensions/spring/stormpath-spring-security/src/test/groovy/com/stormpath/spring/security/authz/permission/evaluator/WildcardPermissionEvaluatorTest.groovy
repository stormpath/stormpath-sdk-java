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

package com.stormpath.spring.security.authz.permission.evaluator

import com.stormpath.spring.security.authz.permission.Permission
import com.stormpath.spring.security.authz.permission.WildcardPermission
import org.junit.Test
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

import static org.easymock.EasyMock.*
import static org.junit.Assert.assertEquals


class WildcardPermissionEvaluatorTest {

    @Test
    public void testNamed() {

        // Case insensitive, same
        doTest(null, "something", constructPermissionSet(["something"] as String[]),  true)

        // Case insensitive, different case
        doTest(null, "something", constructPermissionSet(["SOMETHING"] as String[]),  true)

        // Case insensitive, different case
        doTest(null, "SOMETHING", constructPermissionSet(["something"] as String[]),  true)

        // Case insensitive, different word
        doTest(null, "something", constructPermissionSet(["else"] as String[], false),  false)

        // Case sensitive same
        doTest(null, "BLAHBLAH", constructPermissionSet(["BLAHBLAH"] as String[], true),  true)

        // Case sensitive, different case
        doTest(null, "BLAHBLAH", constructPermissionSet(["bLAHBLAH"] as String[], true),  false)

        // Case sensitive, different case
        doTest(null, "bLAHBLAH", constructPermissionSet(["BLAHBLAH"] as String[], true),  false)

        // Case sensitive, different word
        doTest(null, "BLAHBLAH", constructPermissionSet(["whatwhat"] as String[], true),  false)
    }

    @Test
    public void testLists() {

        doTest(null, "one,two", constructPermissionSet(["one"] as String[]),  false)
        doTest(null, "one", constructPermissionSet(["one,two"] as String[]),  true)

        doTest(null, "one,two,three", constructPermissionSet(["one,three"] as String[]),  false)
        doTest(null, "one,three", constructPermissionSet(["one,two,three"] as String[]),  true)

        doTest("one,two", "one,two,three", constructPermissionSet(["one:three"] as String[]),  false)
        doTest(null, "one:three", constructPermissionSet(["one,two:one,two,three"] as String[]),  true)

        doTest("one,two", "one,two,three", constructPermissionSet(["one:two,three"] as String[]),  false)
        doTest(null, "one:two,three", constructPermissionSet(["one,two:one,two,three"] as String[]),  true)

        doTest(null, "one:three", constructPermissionSet(["one:two,three"] as String[]),  true)
        doTest(null, "one:two,three", constructPermissionSet(["one:three"] as String[]),  false)

        doTest("one,two,three", "one,two,three:one,two", constructPermissionSet(["one:three:two"] as String[]),  false)
        doTest("one:three", "two", constructPermissionSet(["one,two,three:one,two,three:one,two"] as String[]),  true)

        doTest(null, "one", constructPermissionSet(["one:two,three,four"] as String[]),  false)
        doTest("one", "two,three,four", constructPermissionSet(["one"] as String[]),  true)

        doTest(null, "one", constructPermissionSet(["one:two,three,four:five:six:seven"] as String[]),  false)
        doTest("one:two,three,four", "five:six:seven", constructPermissionSet(["one"] as String[]),  true)

        doTest(null, "one:two,three,four", constructPermissionSet(["one:two,three,four:five:six:seven"] as String[]), false)
        doTest(null, "one:two,three,four:five:six:seven", constructPermissionSet(["one:two,three,four"] as String[]), true)
    }

    @Test
    public void testWildcards() {

        doTest(null, "one", constructPermissionSet(["*"] as String[]), true)
        doTest(null, "one:two", constructPermissionSet(["*"] as String[]), true)
        doTest("one", "one", constructPermissionSet(["*"] as String[]), true)
        doTest(null, "one,two:three,four", constructPermissionSet(["*"] as String[]), true)
        doTest("one,two", "three,four", constructPermissionSet(["*"] as String[]), true)
        doTest(null, "one,two:three,four,five:six:seven,eight", constructPermissionSet(["*"] as String[]), true)
        doTest("one,two:three,four,five", "six:seven,eight", constructPermissionSet(["*"] as String[]), true)

        doTest("newsletter", "read", constructPermissionSet(["newsletter:*"] as String[]), true)
        doTest("newsletter", "read,write", constructPermissionSet(["*"] as String[]), true)
        doTest("newsletter", "write:*", constructPermissionSet(["*"] as String[]), true)

    }

    @Test
    public void testWithFourParameters() {

        doTestFourParameters("123", "newsletter", "read", constructPermissionSet(["newsletter:*:read"] as String[]), true)
        doTestFourParameters("123,456", "newsletter", "read,write", constructPermissionSet(["newsletter:*:read"] as String[]), false)
        doTestFourParameters("123,456", "newsletter", "read,write", constructPermissionSet(["newsletter:*:read,write"] as String[]), true)
        doTestFourParameters("read", "newsletter", "", constructPermissionSet(["newsletter:*:read"] as String[]), false)
        doTestFourParameters("", "newsletter", "read", constructPermissionSet(["newsletter:*:read"] as String[]), true)
        doTestFourParameters("read,write", "newsletter", "", constructPermissionSet(["newsletter:*:read"] as String[]), false)
        doTestFourParameters("", "newsletter", "read,write", constructPermissionSet(["newsletter:*:read"] as String[]), false)
        doTestFourParameters("123", "newsletter", "read:write", constructPermissionSet(["newsletter:*:read"] as String[]), true)
    }

    private void doTestFourParameters(Serializable targetId, String targetType, Object permission, Collection<? extends GrantedAuthority> gaList, Boolean expected) {

        def authentication = createMock(Authentication)

        expect(authentication.getAuthorities()) andReturn gaList

        replay authentication

        WildcardPermissionEvaluator wpe = new WildcardPermissionEvaluator();
        assertEquals(expected, wpe.hasPermission(authentication, targetId, targetType, permission))

        verify authentication

    }


    private void doTest(Object targetDomainObject, Object permission, Collection<? extends GrantedAuthority> gaList, Boolean expected) {

        def authentication = createMock(Authentication)

        expect(authentication.getAuthorities()) andReturn gaList

        replay authentication

        WildcardPermissionEvaluator wpe = new WildcardPermissionEvaluator();
        assertEquals(expected, wpe.hasPermission(authentication, targetDomainObject, permission))

        verify authentication

    }

    private Set<Permission> constructPermissionSet(String[] permissions, Boolean caseSensitive=false) {
        Set<Permission> permissionSet = new HashSet<Permission>()
        for (String permission : permissions) {
            if(caseSensitive) {
                permissionSet.add(new WildcardPermission(permission, true))
            } else {
                permissionSet.add(new WildcardPermission(permission))
            }
        }
        return permissionSet
    }

}
