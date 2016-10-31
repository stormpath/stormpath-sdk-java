package com.stormpath.matchers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;

@SuppressWarnings("unchecked")
public class JwtMatchers {

    public static Matcher<Jwt<Header, Claims>> hasHeader(final String header) {
        final Matcher<Map<? extends String, ?>> hasKeyMatcher = hasKey(header);
        return new BaseMatcher<Jwt<Header, Claims>>() {
            @Override
            public boolean matches(Object item) {
                Jwt<Header, Claims> jwt = (Jwt<Header, Claims>) item;
                return hasKeyMatcher.matches(jwt.getHeader());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("jwt with headers as ")
                        .appendDescriptionOf(hasKeyMatcher);
            }
        };
    }

    public static Matcher<Jwt<Header, Claims>> hasHeader(final String header, final Object value) {
        final Matcher<Map<? extends String, ?>> hasEntryMatcher = hasEntry(header, value);
        return new BaseMatcher<Jwt<Header, Claims>>() {
            @Override
            public boolean matches(Object item) {
                Jwt<Header, Claims> jwt = (Jwt<Header, Claims>) item;
                return hasEntryMatcher.matches(jwt.getHeader());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("jwt with headers as ")
                        .appendDescriptionOf(hasEntryMatcher);
            }
        };
    }

    public static Matcher<Jwt<Header, Claims>> hasClaim(String key) {
        final Matcher<Map<? extends String, ?>> hasKeyMatcher = hasKey(key);
        return new BaseMatcher<Jwt<Header, Claims>>() {
            @Override
            public boolean matches(Object item) {
                Jwt<Header, Claims> jwt = (Jwt<Header, Claims>) item;
                return hasKeyMatcher.matches(jwt.getBody());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("jwt with claims as ")
                        .appendDescriptionOf(hasKeyMatcher);
            }

        };
    }

    public static Matcher<Jwt<Header, Claims>> hasClaim(String key, Object value) {
        final Matcher<Map<? extends String, ?>> hasEntryMatcher = hasEntry(key, value);
        return new BaseMatcher<Jwt<Header, Claims>>() {
            @Override
            public boolean matches(Object item) {
                Jwt<Header, Claims> jwt = (Jwt<Header, Claims>) item;
                return hasEntryMatcher.matches(jwt.getBody());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("jwt with claims as ")
                        .appendDescriptionOf(hasEntryMatcher);
            }
        };
    }

    public static Matcher<Jwt<Header, Claims>> hasClaim(String key, Matcher<Object> valueMatcher) {
        final Matcher<Map<? extends String, ?>> hasEntryMatcher = hasEntry(equalTo(key), valueMatcher);
        return new BaseMatcher<Jwt<Header, Claims>>() {
            @Override
            public boolean matches(Object item) {
                Jwt<Header, Claims> jwt = (Jwt<Header, Claims>) item;
                return hasEntryMatcher.matches(jwt.getBody());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("jwt with claims as ")
                        .appendDescriptionOf(hasEntryMatcher);
            }
        };
    }
}
