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
package com.stormpath.sdk.servlet.authz.policy

import com.stormpath.sdk.servlet.authz.policy.impl.FilterChainDefiningUriAuthorizationPolicyBuilder
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail


class UriAuthorizationPolicyBuilderTest {

    static protected FilterChainDefiningUriAuthorizationPolicyBuilder ensureRequests() {
        return new FilterChainDefiningUriAuthorizationPolicyBuilder();
    }

    @Test
    void testNoRules() {
        try {
            ensureRequests().getChainDefinitions()
            fail()
        } catch (IllegalArgumentException iae) {
            assertEquals iae.message, 'No patterns or rules have been specified.'
        }
    }

    static void testAuthzBeforePattern(Closure c) {
        assertEx(c, 'You must specify one or more URI patterns first before defining an authorization rule.')
    }

    static void assertEx(Closure c, String msg) {
        try {
            c()
            fail()
        } catch (IllegalArgumentException iae) {
            assertEquals iae.message, msg
        }
    }

    static void testRestrictable(Closure c) {
        try {
            c()
            fail()
        } catch (IllegalArgumentException iae) {
            assertEquals iae.message, FilterChainDefiningUriAuthorizationPolicyBuilder.UNRESTRICTED_RULE_MSG
        }
    }

    @Test
    void testAreBeforeDefiningAPattern() {
        testAuthzBeforePattern {ensureRequests().are()}
    }

    //@Test
    //void testNotBeforeDefiningAPattern() {
    //    testAuthzBeforePattern {ensureRequests().not()}
    //}

    @Test
    void testFromAccountsBeforeDefiningAPattern() {
        testAuthzBeforePattern {ensureRequests().fromAccounts()}
    }

    @Test
    void testUnrestrictedBeforeDefiningAPattern() {
        testAuthzBeforePattern {ensureRequests().unrestricted()}
    }

    @Test
    void testWhereBeforeDefiningAPattern() {
        testAuthzBeforePattern {ensureRequests().where('')}
    }

    @Test
    void testAuthenticatedBeforeDefiningAPattern() {
        testAuthzBeforePattern {ensureRequests().authenticated()}
    }

    @Test
    void testAndBeforeDefiningAPattern() {
        testAuthzBeforePattern {ensureRequests().and()}
    }

    @Test
    void testUnrestricted() {
        def defs = ensureRequests().to('/', '/static/**').are().unrestricted().getChainDefinitions()
        assertEquals 2, defs.size()
        assertEquals defs['/'], 'anon'
        assertEquals defs['/static/**'], 'anon'
    }

    @Test
    void testUnrestrictedThenAre() {
        def defs = ensureRequests().to('/').are().unrestricted().are().getChainDefinitions()
        assertEquals 1, defs.size()
        assertEquals defs['/'], 'anon'
    }


    //@Test
    //void testUnrestrictedThenNot() {
    //    testRestrictable {ensureRequests().to('/').are().unrestricted().not()}
    //}

    @Test
    void testUnrestrictedThenFromAccounts() {
        testRestrictable {ensureRequests().to('/').are().unrestricted().fromAccounts()}
    }

    @Test
    void testUnrestrictedThenUnrestricted() {
        //second call should be ignored
        def defs = ensureRequests().to('/').are().unrestricted().unrestricted().getChainDefinitions()
        assertEquals 1, defs.size()
        assertEquals defs['/'], 'anon'
    }

    @Test
    void testUnrestrictedThenWhere() {
        testRestrictable {ensureRequests().to('/').are().unrestricted().where('')}
    }

    @Test
    void testUnrestrictedThenAuthenticated() {
        testRestrictable {ensureRequests().to('/').are().unrestricted().authenticated()}
    }

    @Test
    void testUnrestrictedThenAnd() {
        //second call should be ignored
        def defs = ensureRequests().to('/').are().unrestricted().and().getChainDefinitions()
        assertEquals 1, defs.size()
        assertEquals defs['/'], 'anon'
    }

    @Test
    void testToWithNullArg() {
        assertEx({ensureRequests().to(null)}, 'URI pattern argument(s) cannot be null or empty.')
    }

    @Test
    void testToWithWhitespaceArg() {
        assertEx({ensureRequests().to(' ')}, 'URI pattern array element cannot be null or whitespace.')
    }

    @Test
    void testToWithoutRules() {
        assertEx({ensureRequests().to('/').getChainDefinitions()}, 'You must specify one or more authorization rules for URI(s) [/].')
    }

    @Test
    void testToAnythingElse() {
        def defs = ensureRequests().toAnythingElse().are().authenticated().getChainDefinitions();
        assertEquals 1, defs.size()
        assertEquals defs['/**'], 'authc'
    }

    @Test
    void testToAnythingElseFollowedByTo() {
        assertEx({ensureRequests().toAnythingElse().are().authenticated().and().to('/')},
                'Cannot define more rules after \'toAnythingElse\'')
    }

    @Test
    void testToAnythingElseMultipleCalls() {
        assertEx({ensureRequests().toAnythingElse().are().authenticated().and().toAnythingElse()},
                'Cannot specify \'toAnythingElse\' multiple times.')
    }

    @Test
    void testFromAccounts() {
        def defs = ensureRequests().to('/account/**').are().fromAccounts().getChainDefinitions()
        assertEquals 1, defs.size()
        assertEquals defs['/account/**'], 'account'
    }

    @Test
    void testFromAccountsWhere() {
        def defs = ensureRequests().to('/account/**').are().fromAccounts().where('foo=bar').getChainDefinitions()
        assertEquals 1, defs.size()
        assertEquals defs['/account/**'], 'account(foo=bar)'
    }

    @Test
    void testCompositeRule() {
        def defs = ensureRequests().to('/account/**').are().authenticated().and().fromAccounts().where('foo=bar').getChainDefinitions()
        assertEquals 1, defs.size()
        assertEquals defs['/account/**'], 'authc,account(foo=bar)'
    }
}
