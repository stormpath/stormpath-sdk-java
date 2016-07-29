package com.stormpath.spring.util;

import com.stormpath.sdk.servlet.util.PatternMatcher;
import org.springframework.util.PathMatcher;

/**
 * A {@code PatternMatcher} implementation that delegates to a Spring {@link PathMatcher} instance/
 *
 * @since 1.0.0
 */
public class SpringPatternMatcher implements PatternMatcher {

    private final PathMatcher pathMatcher;

    public SpringPatternMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    @Override
    public boolean matches(String pattern, String source) {
        return this.pathMatcher.match(pattern, source);
    }
}
