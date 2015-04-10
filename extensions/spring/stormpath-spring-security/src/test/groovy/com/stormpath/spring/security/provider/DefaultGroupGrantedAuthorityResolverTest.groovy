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


package com.stormpath.spring.security.provider

import com.stormpath.sdk.group.Group
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.springframework.security.core.GrantedAuthority

import static org.easymock.EasyMock.*
import static org.hamcrest.core.IsInstanceOf.instanceOf
import static org.junit.Assert.*

class DefaultGroupGrantedAuthorityResolverTest {

    DefaultGroupGrantedAuthorityResolver resolver

    @Before
    void setUp() {
        resolver = new DefaultGroupGrantedAuthorityResolver()
    }

    @Test
    void testDefaultInstance() {
        Assert.assertEquals 1, resolver.modes.size()
        Assert.assertSame DefaultGroupGrantedAuthorityResolver.Mode.HREF, resolver.modes.iterator().next()
    }

    @Test
    void testSetModes() {
        resolver.setModes([DefaultGroupGrantedAuthorityResolver.Mode.ID] as Set)
        Assert.assertEquals 1, resolver.modes.size()
        Assert.assertSame DefaultGroupGrantedAuthorityResolver.Mode.ID, resolver.modes.iterator().next()
    }

    @Test(expected=IllegalArgumentException)
    void testSetNullModes() {
        resolver.setModes(null)
    }

    @Test(expected=IllegalArgumentException)
    void testSetEmptyModes() {
        resolver.setModes(Collections.emptySet())
    }

    @Test
    void testResolveGrantedAuthorityWithHref() {

        def group = createStrictMock(Group)

        def href = 'https://api.stormpath.com/groups/foo'

        expect(group.href).andReturn(href)

        replay group

        def roleNames = resolver.resolveGrantedAuthorities(group)

        Assert.assertEquals 1, roleNames.size()
        def retrievedRole = roleNames.iterator().next()
        assertThat retrievedRole, instanceOf(GrantedAuthority.class)
        assertEquals href, retrievedRole.toString()

        verify group
    }

    @Test(expected=IllegalStateException)
    void testResolveGrantedAuthorityWithMissingHref() {

        def group = createStrictMock(Group)

        expect(group.href).andReturn null

        replay group

        try {
            resolver.resolveGrantedAuthorities(group)
        } finally {
            verify group
        }
    }

    @Test
    void testResolveGrantedAuthorityWithId() {

        def group = createStrictMock(Group)

        def href = 'https://api.stormpath.com/groups/foo'

        expect(group.href).andReturn(href)

        replay group

        resolver.modes = [DefaultGroupGrantedAuthorityResolver.Mode.ID] as Set
        def roleNames = resolver.resolveGrantedAuthorities(group)

        Assert.assertEquals 1, roleNames.size()
        def retrievedRole = roleNames.iterator().next()
        assertThat retrievedRole, instanceOf(GrantedAuthority.class)
        assertEquals 'foo', retrievedRole.toString()

        verify group
    }

    @Test
    void testResolveGrantedAuthorityWithIdAndInvalidHref() {

        def group = createStrictMock(Group)

        def href = 'whatever'

        expect(group.href).andReturn(href)

        replay group

        resolver.modes = [DefaultGroupGrantedAuthorityResolver.Mode.ID] as Set
        def roleNames = resolver.resolveGrantedAuthorities(group)

        assertNotNull roleNames
        assertTrue roleNames.isEmpty()

        verify group
    }

    @Test
    void testResolveGrantedAuthorityWithName() {

        def group = createStrictMock(Group)

        def href = 'https://api.stormpath.com/groups/foo'

        expect(group.href).andReturn(href)
        expect(group.name).andReturn('bar')

        replay group

        resolver.modes = [DefaultGroupGrantedAuthorityResolver.Mode.NAME] as Set
        def roleNames = resolver.resolveGrantedAuthorities(group)

        Assert.assertEquals 1, roleNames.size()
        def retrievedRole = roleNames.iterator().next()
        assertThat retrievedRole, instanceOf(GrantedAuthority.class)
        assertEquals 'bar', retrievedRole.toString()

        verify group
    }

    @Test(expected = IllegalArgumentException)
    void testResolveGrantedAuthorityFromEmptyString() {
        DefaultGroupGrantedAuthorityResolver.Mode.fromString("")
    }

    @Test(expected = IllegalArgumentException)
    void testResolveGrantedAuthorityFromNull() {
        DefaultGroupGrantedAuthorityResolver.Mode.fromString(null)
    }

    @Test(expected = IllegalArgumentException)
    void testResolveGrantedAuthorityFromUnknownName() {
        DefaultGroupGrantedAuthorityResolver.Mode.fromString("foo")
    }

    @Test
    void testResolveGrantedAuthorityFromString() {

        def mode = DefaultGroupGrantedAuthorityResolver.Mode.fromString("href")
        assertEquals(DefaultGroupGrantedAuthorityResolver.Mode.HREF, mode)

        mode = DefaultGroupGrantedAuthorityResolver.Mode.fromString("HREF")
        assertEquals(DefaultGroupGrantedAuthorityResolver.Mode.HREF, mode)

        mode = DefaultGroupGrantedAuthorityResolver.Mode.fromString("Name")
        assertEquals(DefaultGroupGrantedAuthorityResolver.Mode.NAME, mode)
    }


}
