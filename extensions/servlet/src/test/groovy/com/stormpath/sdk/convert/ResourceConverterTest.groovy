/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.convert

import com.stormpath.sdk.impl.account.DefaultAccount
import com.stormpath.sdk.impl.api.ApiKeyResolver
import com.stormpath.sdk.impl.authc.credentials.ApiKeyCredentials
import com.stormpath.sdk.impl.directory.DefaultCustomData
import com.stormpath.sdk.impl.ds.DefaultDataStore
import com.stormpath.sdk.impl.group.DefaultGroupList
import com.stormpath.sdk.impl.http.RequestExecutor
import org.testng.annotations.Test

import static com.stormpath.sdk.convert.Conversions.withField
import static com.stormpath.sdk.convert.Conversions.withStrategy
import static org.easymock.EasyMock.createMock
import static org.testng.Assert.*

/**
 * @since 1.3.0
 */
class ResourceConverterTest {

    @Test
    void testGetConfig() {

        def c = new Conversion().withField("href", new Conversion().setEnabled(false))

        def fn = new ResourceConverter(config: c)

        assertSame fn.getConfig(), c
    }

    @Test
    void testSingleFieldValue() {

        def config = new Conversion(strategy: ConversionStrategyName.SINGLE, field: 'email')

        def fn = new ResourceConverter(config: config)

        def props = ['username' : 'jsmith',
                     'email'    : 'jsmith@nowhere.com',
                     'givenName': 'John',
                     'surname'  : 'Smith']

        def account = new DefaultAccount(null, props)

        def result = fn.apply(account)

        assertEquals result, 'jsmith@nowhere.com'
    }

    @Test
    void testSingleFieldValueFallbackToHref() {

        def config = new Conversion(strategy: ConversionStrategyName.SINGLE, field: 'THIS_FIELD_DOES_NOT_EXIST')

        def fn = new ResourceConverter(config: config)

        def props = ['href'     : 'https://wherever.com',
                     'username' : 'jsmith',
                     'email'    : 'jsmith@nowhere.com',
                     'givenName': 'John',
                     'surname'  : 'Smith']

        def account = new DefaultAccount(null, props)

        def result = fn.apply(account)

        assertEquals result, 'https://wherever.com'
    }

    @Test
    void testSingleFieldValueDoesNotExistAndNoFallback() {

        def fieldName = 'THIS_FIELD_DOES_NOT_EXIST'

        def config = withField("customData", withField("foo", withStrategy(ConversionStrategyName.SINGLE).setField(fieldName)));

        def fn = new ResourceConverter(config: config)

        def ds = new DefaultDataStore(createMock(RequestExecutor),
                createMock(ApiKeyCredentials), createMock(ApiKeyResolver))

        def cd = new DefaultCustomData(ds, [
                'href'      : 'https://example.io/users/1/customData',
                'createdAt' : '2016-09-29T17:43:03.887Z',
                'modifiedAt': '2016-09-29T17:43:03.887Z',
                'foo'       : [
                        'bar': 'baz'
                ]
        ])

        def props = [
                'href'      : 'https://example.io/users/1',
                'username'  : 'jsmith',
                'email'     : 'jsmith@example.com',
                'customData': cd
        ]
        def account = new DefaultAccount(ds, props)

        def result = fn.apply(account)

        assertNotNull result.customData
        assertNull result.customData.foo
    }

    @Test
    void testScalarFields() {

        def config = new Conversion()

        //def config = ['fields': ['username', 'email', 'givenName', 'surname']]

        def fn = new ResourceConverter(config: config)

        def props = ['href'     : 'https://wherever.com',
                     'username' : 'jsmith',
                     'email'    : 'jsmith@nowhere.com',
                     'givenName': 'John',
                     'surname'  : 'Smith',
                     // compound property: ensure it does not show up since 'scalars' is the default:
                     'groups'   : new DefaultGroupList(null, ['href': 'https://somewhereelse.com'])
        ]

        def account = new DefaultAccount(null, props)

        def result = fn.apply(account)

        assertEquals result.size(), props.size() - 1 //no compound values
        assertEquals result.href, props.href
        assertEquals result.username, props.username
        assertEquals result.email, props.email
        assertEquals result.givenName, props.givenName
        assertEquals result.surname, props.surname
    }

    @Test
    void testScalarFieldsWithExplicitExclusion() {

        def config = new Conversion().withField("href", new Conversion().setEnabled(false))

        def fn = new ResourceConverter(config: config)

        def props = ['href'     : 'https://wherever.com',
                     'username' : 'jsmith',
                     'email'    : 'jsmith@nowhere.com',
                     'givenName': 'John',
                     'surname'  : 'Smith']

        def account = new DefaultAccount(null, props)

        def result = fn.apply(account)

        assertEquals result.size(), props.size() - 1
        assertFalse result.containsKey('href')

        assertEquals result.username, props.username
        assertEquals result.email, props.email
        assertEquals result.givenName, props.givenName
        assertEquals result.surname, props.surname
    }


    @Test
    void testUnknownField() {

        def config = new Conversion(strategy: ConversionStrategyName.DEFINED).withField("doesNotExist", new Conversion())

        def fn = new ResourceConverter(config: config)

        def props = ['href'    : 'https://wherever.com',
                     'username': 'jsmith',
                     'email'   : 'jsmith@nowhere.com']

        def account = new DefaultAccount(null, props)

        def result = fn.apply(account)

        assertEquals result.size(), 1
        assertEquals result.href, props.href
    }

    @Test
    void testScalarAndObjectField() {

        def config = new Conversion().withField("customData",
                new Conversion(strategy: ConversionStrategyName.ALL).withField("href", new Conversion().setEnabled(false)))

        def fn = new ResourceConverter(config: config)

        def ds = new DefaultDataStore(createMock(RequestExecutor),
                createMock(ApiKeyCredentials), createMock(ApiKeyResolver))

        def cd = new DefaultCustomData(ds, [
                'href'      : 'https://example.io/users/1/customData',
                'createdAt' : '2016-09-29T17:43:03.887Z',
                'modifiedAt': '2016-09-29T17:43:03.887Z'
        ])

        def props = [
                'href'      : 'https://example.io/users/1',
                'username'  : 'jsmith',
                'email'     : 'jsmith@example.com',
                'customData': cd
        ]
        def account = new DefaultAccount(ds, props)

        def result = fn.apply(account)

        assertEquals result.size(), 4
        assertEquals result.href, props.href
        assertEquals result.username, props.username
        assertEquals result.email, props.email
        assertEquals result.customData.size(), 2
        assertEquals result.customData.createdAt, props.customData.createdAt
        assertEquals result.customData.updatedAt, props.customData.updatedAt
    }

    @Test
    void testScalarAndCollectionField() {

        def config = new Conversion().withField("groups",
                new Conversion(
                        strategy: ConversionStrategyName.DEFINED,
                        elements: new ElementsConversion(
                                each: new Conversion().withField("href", new Conversion().setEnabled(false)))))

        /*def config = '''
        strategy: scalars
        fields:
          groups:
            strategy: defined
            elements:
              name: items
              each:
                strategy: scalars
                fields:
                  href:
                    enabled: false
         '''.parseConversionYaml() */

        def fn = new ResourceConverter(config: config)

        def ds = new DefaultDataStore(createMock(RequestExecutor),
                createMock(ApiKeyCredentials), createMock(ApiKeyResolver))

        def cd = new DefaultCustomData(ds, [
                'href'      : 'https://example.io/users/1/customData',
                'createdAt' : '2016-09-29T17:43:03.887Z',
                'modifiedAt': '2016-09-29T17:43:03.887Z'
        ])

        def groups = new DefaultGroupList(ds, [
                'href'  : 'https://example.io/users/1/groups',
                'offset': 0,
                'limit' : 25,
                'size'  : 3,
                'items' : [[href: 'https://example.io/groups/1',
                            name: 'foo'],
                           [href: 'https://example.io/groups/2',
                            name: 'bar'],
                           [href: 'https://example.io/groups/3',
                            name: 'baz']
                ]

        ])

        def props = [
                'href'    : 'https://example.io/users/1',
                'username': 'jsmith',
                'email'   : 'jsmith@example.com',
                'groups'  : groups
        ]
        def account = new DefaultAccount(ds, props)

        def result = fn.apply(account)

        assertEquals result.size(), 4
        assertEquals result.href, props.href
        assertEquals result.username, props.username
        assertEquals result.email, props.email
        assertEquals result.groups.size(), 1
        assertEquals result.groups.items.size(), 3
        def i = result.groups.items.iterator()
        def g = i.next()
        assertEquals g.size(), 1
        assertEquals g.name, 'foo'
        g = i.next()
        assertEquals g.size(), 1
        assertEquals g.name, 'bar'
        g = i.next()
        assertEquals g.size(), 1
        assertEquals g.name, 'baz'

    }

    @Test
    void testScalarsAndCollectionSingleValues() {

        def config = new Conversion().withField("groups",
                new Conversion(
                        strategy: ConversionStrategyName.DEFINED,
                        elements: new ElementsConversion(
                                each: new Conversion(
                                        strategy: ConversionStrategyName.SINGLE,
                                        field: 'name'))))

        /*
        def config = '''
        strategy: scalars
        fields:
          groups:
            strategy: defined
            elements:
              name: items
              each:
                strategy: single
                field: name
         '''.parseConversionYaml()
         */

        def fn = new ResourceConverter(config: config)

        def ds = new DefaultDataStore(createMock(RequestExecutor),
                createMock(ApiKeyCredentials), createMock(ApiKeyResolver))

        def cd = new DefaultCustomData(ds, [
                'href'      : 'https://example.io/users/1/customData',
                'createdAt' : '2016-09-29T17:43:03.887Z',
                'modifiedAt': '2016-09-29T17:43:03.887Z'
        ])

        def groups = new DefaultGroupList(ds, [
                'href'  : 'https://example.io/users/1/groups',
                'offset': 0,
                'limit' : 25,
                'size'  : 3,
                'items' : [[href: 'https://example.io/groups/1',
                            name: 'foo'],
                           [href: 'https://example.io/groups/2',
                            name: 'bar'],
                           [href: 'https://example.io/groups/3',
                            name: 'baz']
                ]

        ])

        def props = [
                'href'    : 'https://example.io/users/1',
                'username': 'jsmith',
                'email'   : 'jsmith@example.com',
                'groups'  : groups
        ]
        def account = new DefaultAccount(ds, props)

        def result = fn.apply(account)

        assertEquals result.size(), 4
        assertEquals result.href, props.href
        assertEquals result.username, props.username
        assertEquals result.email, props.email
        assertEquals result.groups.size(), 1
        assertEquals result.groups.items.size(), 3
        def i = result.groups.items.iterator()
        assertEquals i.next(), 'foo'
        assertEquals i.next(), 'bar'
        assertEquals i.next(), 'baz'
    }

    @Test
    void testCollectionCustomItemsName() {

        def config = new Conversion().withField("groups",
                new Conversion(
                        strategy: ConversionStrategyName.DEFINED,
                        elements: new ElementsConversion(
                                name: 'myelements', //custom name
                                each: new Conversion(
                                        strategy: ConversionStrategyName.SINGLE,
                                        field: 'name'))))

        /*
        def config = '''
        strategy: scalars
        fields:
          groups:
            strategy: defined
            elements:
              name: items
              each:
                strategy: single
                field: name
         '''.parseConversionYaml()
         */

        def fn = new ResourceConverter(config: config)

        def ds = new DefaultDataStore(createMock(RequestExecutor),
                createMock(ApiKeyCredentials), createMock(ApiKeyResolver))

        def cd = new DefaultCustomData(ds, [
                'href'      : 'https://example.io/users/1/customData',
                'createdAt' : '2016-09-29T17:43:03.887Z',
                'modifiedAt': '2016-09-29T17:43:03.887Z'
        ])

        def groups = new DefaultGroupList(ds, [
                'href'  : 'https://example.io/users/1/groups',
                'offset': 0,
                'limit' : 25,
                'size'  : 3,
                'items' : [[href: 'https://example.io/groups/1',
                            name: 'foo'],
                           [href: 'https://example.io/groups/2',
                            name: 'bar'],
                           [href: 'https://example.io/groups/3',
                            name: 'baz']
                ]

        ])

        def props = [
                'href'    : 'https://example.io/users/1',
                'username': 'jsmith',
                'email'   : 'jsmith@example.com',
                'groups'  : groups
        ]
        def account = new DefaultAccount(ds, props)

        def result = fn.apply(account)

        assertEquals result.size(), 4
        assertEquals result.href, props.href
        assertEquals result.username, props.username
        assertEquals result.email, props.email
        assertEquals result.groups.size(), 1
        assertEquals result.groups.myelements.size(), 3 //'myelements' = custom name
        def i = result.groups.myelements.iterator() //iterate over 'myelements'
        assertEquals i.next(), 'foo'
        assertEquals i.next(), 'bar'
        assertEquals i.next(), 'baz'
    }

    @Test
    void testCollectionElementsDisabled() {

        def config = new Conversion().withField("groups",
                new Conversion(elements: new ElementsConversion(enabled: false)))

        def fn = new ResourceConverter(config: config)

        def ds = new DefaultDataStore(createMock(RequestExecutor),
                createMock(ApiKeyCredentials), createMock(ApiKeyResolver))

        def groups = new DefaultGroupList(ds, [
                'href'  : 'https://example.io/users/1/groups',
                'offset': 0,
                'limit' : 25,
                'size'  : 3,
                'items' : [[href: 'https://example.io/groups/1',
                            name: 'foo'],
                           [href: 'https://example.io/groups/2',
                            name: 'bar'],
                           [href: 'https://example.io/groups/3',
                            name: 'baz']
                ]

        ])

        def props = [
                'href'    : 'https://example.io/users/1',
                'username': 'jsmith',
                'email'   : 'jsmith@example.com',
                'groups'  : groups
        ]
        def account = new DefaultAccount(ds, props)

        def result = fn.apply(account)

        assertEquals result.size(), 4
        assertEquals result.href, props.href
        assertEquals result.username, props.username
        assertEquals result.email, props.email
        assertNotNull result.groups
        assertEquals result.groups.size(), 4
        assertEquals result.groups.offset, 0
        assertEquals result.groups.limit, 25
        assertEquals result.groups.size, 3
        assertNull result.groups.items //no items - should be disabled
    }

    @Test
    void testScalarsAndDirectCollection() {

        def config = new Conversion().withField("groups", new Conversion(
                strategy: ConversionStrategyName.LIST,
                elements: new ElementsConversion(
                        each: new Conversion(
                                strategy: ConversionStrategyName.SINGLE,
                                field: 'name'
                        )
                )
        ))

        /*
        def config = '''
        strategy: scalars
        fields:
          groups:
            strategy: list
            elements:
              each:
                strategy: single
                field: name
         '''.parseConversionYaml()
         */

        def fn = new ResourceConverter(config: config)

        def ds = new DefaultDataStore(createMock(RequestExecutor),
                createMock(ApiKeyCredentials), createMock(ApiKeyResolver))

        def cd = new DefaultCustomData(ds, [
                'href'      : 'https://example.io/users/1/customData',
                'createdAt' : '2016-09-29T17:43:03.887Z',
                'modifiedAt': '2016-09-29T17:43:03.887Z'
        ])

        def groups = new DefaultGroupList(ds, [
                'href'  : 'https://example.io/users/1/groups',
                'offset': 0,
                'limit' : 25,
                'size'  : 3,
                'items' : [[href: 'https://example.io/groups/1',
                            name: 'foo'],
                           [href: 'https://example.io/groups/2',
                            name: 'bar'],
                           [href: 'https://example.io/groups/3',
                            name: 'baz']
                ]

        ])

        def props = [
                'href'    : 'https://example.io/users/1',
                'username': 'jsmith',
                'email'   : 'jsmith@example.com',
                'groups'  : groups
        ]
        def account = new DefaultAccount(ds, props)

        def result = fn.apply(account)

        assertEquals result.size(), 4
        assertEquals result.href, props.href
        assertEquals result.username, props.username
        assertEquals result.email, props.email
        assertEquals result.groups.size(), 3
        def i = result.groups.iterator()
        assertEquals i.next(), 'foo'
        assertEquals i.next(), 'bar'
        assertEquals i.next(), 'baz'
    }

    @Test
    void testDefaultConfig() {

        def fn = new ResourceConverter()

        def ds = new DefaultDataStore(createMock(RequestExecutor),
                createMock(ApiKeyCredentials), createMock(ApiKeyResolver))

        def cd = new DefaultCustomData(ds, [
                'href'      : 'https://example.io/users/1/customData',
                'createdAt' : '2016-09-29T17:43:03.887Z',
                'modifiedAt': '2016-09-29T17:43:03.887Z'
        ])

        def groups = new DefaultGroupList(ds, [
                'href'  : 'https://example.io/users/1/groups',
                'offset': 0,
                'limit' : 25,
                'size'  : 3,
                'items' : [[href: 'https://example.io/groups/1',
                            name: 'foo'],
                           [href: 'https://example.io/groups/2',
                            name: 'bar'],
                           [href: 'https://example.io/groups/3',
                            name: 'baz']
                ]

        ])

        def props = [
                'href'      : 'https://example.io/users/1',
                'username'  : 'jsmith',
                'email'     : 'jsmith@example.com',
                'customData': cd,
                'groups'    : groups
        ]
        def account = new DefaultAccount(ds, props)

        def result = fn.apply(account)

        assertEquals result.size(), props.size()
        assertEquals result.href, 'https://example.io/users/1'
        assertEquals result.username, props.username
        assertEquals result.email, props.email

        assertEquals result.customData.size(), cd.size()
        assertEquals result.customData.href, 'https://example.io/users/1/customData'
        assertTrue result.customData.containsKey('createdAt')
        assertTrue result.customData.containsKey('modifiedAt')

        assertEquals result.groups.size(), 1
        assertEquals result.groups.items.size(), 3
        def i = result.groups.items.iterator()

        def g = i.next()
        assertEquals g.size(), 2
        assertEquals g.href, 'https://example.io/groups/1'
        assertEquals g.name, 'foo'

        g = i.next()
        assertEquals g.size(), 2
        assertEquals g.href, 'https://example.io/groups/2'
        assertEquals g.name, 'bar'

        g = i.next()
        assertEquals g.size(), 2
        assertEquals g.href, 'https://example.io/groups/3'
        assertEquals g.name, 'baz'
    }

    @Test
    void testRenamedScalar() {

        def config = new Conversion().withField("username", new Conversion().setName("uname"))

        def fn = new ResourceConverter(config: config)

        def props = ['href'    : 'https://wherever.com',
                     'username': 'jsmith',
                     'email'   : 'jsmith@nowhere.com']

        def account = new DefaultAccount(null, props)

        def result = fn.apply(account)

        assertEquals result.size(), 3
        assertEquals result.href, props.href
        assertEquals result.email, props.email
        assertEquals result.uname, props.username

    }

    @Test
    void testRenamedComposite() {

        def config = new Conversion().withField("customData", new Conversion().setName("cd"))

        def fn = new ResourceConverter(config: config)

        def ds = new DefaultDataStore(createMock(RequestExecutor),
                createMock(ApiKeyCredentials), createMock(ApiKeyResolver))

        def cd = new DefaultCustomData(ds, [
                'href'      : 'https://example.io/users/1/customData',
                'createdAt' : '2016-09-29T17:43:03.887Z',
                'modifiedAt': '2016-09-29T17:43:03.887Z'
        ])

        def props = [
                'href'      : 'https://example.io/users/1',
                'username'  : 'jsmith',
                'email'     : 'jsmith@example.com',
                'customData': cd
        ]
        def account = new DefaultAccount(ds, props)

        def result = fn.apply(account)

        assertEquals result.size(), 4
        assertEquals result.href, props.href
        assertEquals result.email, props.email
        assertNotNull result.cd
    }

    @Test
    void testRenamedCollectionObject() {

        def config = new Conversion().withField("groups", new Conversion().setName("roles"))

        def fn = new ResourceConverter(config: config)

        def ds = new DefaultDataStore(createMock(RequestExecutor),
                createMock(ApiKeyCredentials), createMock(ApiKeyResolver))

        def groups = new DefaultGroupList(ds, [
                'href'  : 'https://example.io/users/1/groups',
                'offset': 0,
                'limit' : 25,
                'size'  : 3,
                'items' : [[href: 'https://example.io/groups/1',
                            name: 'foo'],
                           [href: 'https://example.io/groups/2',
                            name: 'bar'],
                           [href: 'https://example.io/groups/3',
                            name: 'baz']
                ]

        ])

        def props = [
                'href'    : 'https://example.io/users/1',
                'username': 'jsmith',
                'email'   : 'jsmith@example.com',
                'groups'  : groups
        ]
        def account = new DefaultAccount(ds, props)

        def result = fn.apply(account)

        assertEquals result.size(), 4
        assertEquals result.href, props.href
        assertEquals result.email, props.email
        assertNotNull result.roles //should be 'roles' not 'groups' due to rename
        assertEquals result.roles.size(), 4 //default strategy is SCALARS, so elements/items won't be in the result
    }

}
