package com.stormpath.sdk.servlet.application;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.lang.BiPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.0.0
 */
public class RegisterEnabledPredicate implements BiPredicate<Boolean, Application> {

    private static final Logger log = LoggerFactory.getLogger(RegisterEnabledPredicate.class);

    private boolean warned = false;

    @Override
    public boolean test(Boolean stormpathWebRegisterEnabled, Application application) {
        if (!stormpathWebRegisterEnabled) {
            return false;
        }

        boolean hasDefaultAccountStore = application.getDefaultAccountStore() != null;

        // https://github.com/stormpath/stormpath-sdk-java/issues/772:
        if (!hasDefaultAccountStore) {
            if (!warned && log.isWarnEnabled()) {
                String msg = "stormpath.web.register.enabled = true, but the application does not have anywhere to " +
                    "save new accounts: the application has not been assigned a default account store (either a " +
                    "Directory, Group or Organization).  You must specify the default account store where your " +
                    "application's newly created accounts will be reside.  As a result, registration will be disabled " +
                    "until an account store is assigned as the application's default account store (you can do this " +
                    "via the REST API or in the Stormpath Administration console).";
                log.warn(msg);
                warned = true; //don't spam the logs on subsequent checks
            }
        } else {
            warned = false; //ensure we warn if the app changes to an invalid state
        }

        return hasDefaultAccountStore;
    }
}
