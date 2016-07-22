package com.stormpath.sdk.servlet.config;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.BiPredicate;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.application.DefaultApplicationResolver;
import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.0
 */
public class RegisterEnabledResolver implements Resolver<Boolean> {

    private final boolean registerEnabled;
    private final ApplicationResolver applicationResolver;
    private final BiPredicate<Boolean, Application> predicate;

    public RegisterEnabledResolver(boolean registerEnabled) {
        this(registerEnabled, new DefaultApplicationResolver());
    }

    public RegisterEnabledResolver(boolean registerEnabled, ApplicationResolver applicationResolver) {
        this(registerEnabled, applicationResolver, new RegisterEnabledPredicate());
    }

    public RegisterEnabledResolver(boolean registerEnabled, ApplicationResolver applicationResolver, BiPredicate<Boolean, Application> predicate) {
        this.registerEnabled = registerEnabled;
        this.applicationResolver = applicationResolver;
        this.predicate = predicate;
        Assert.notNull(applicationResolver, "applicationResolver cannot be null.");
        Assert.notNull(predicate, "predicate cannot be null.");
    }

    /**
     * Only enable registration if:
     * 1) the user has enabled it via configuration and
     * 2) the application has been mapped with a default account store.
     *
     * @param request  current request - ignored in this implementation
     * @param response current response - ignored in this implementation
     * @return true if registerEnabled is true and the application has a default account store.
     * @see <a href="https://github.com/stormpath/stormpath-sdk-java/issues/333">Issue 333</a>
     * @see <a href="https://github.com/stormpath/stormpath-sdk-java/issues/772">Issue 772</a>
     * @since 1.0.0
     */
    @Override
    public Boolean get(HttpServletRequest request, HttpServletResponse response) {

        if (!this.registerEnabled) {
            return false;
        }

        final Application application = applicationResolver.getApplication(request);
        return predicate.test(true, application);
    }
}
