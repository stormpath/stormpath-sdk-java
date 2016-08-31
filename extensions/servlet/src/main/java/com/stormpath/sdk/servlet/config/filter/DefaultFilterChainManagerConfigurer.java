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
package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.UriCleaner;
import com.stormpath.sdk.servlet.config.impl.ConfigReader;
import com.stormpath.sdk.servlet.config.impl.DefaultUriCleaner;
import com.stormpath.sdk.servlet.config.impl.ExpressionConfigReader;
import com.stormpath.sdk.servlet.filter.DefaultFilter;
import com.stormpath.sdk.servlet.filter.FilterChainManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 1.0.0
 */
public class DefaultFilterChainManagerConfigurer {

    public static final String ROUTE_CONFIG_NAME_PREFIX = "stormpath.web.uris.";

    private static final UriCleaner URI_CLEANER = new DefaultUriCleaner();

    private final FilterChainManager mgr;
    private final ServletContext servletContext;
    private final Config config;

    public DefaultFilterChainManagerConfigurer(FilterChainManager mgr, ServletContext servletContext, Config config) {
        Assert.notNull(mgr, "FilterChainManager cannot be null.");
        this.mgr = mgr;
        this.config = config;
        this.servletContext = servletContext;
    }

    protected String cleanUri(String uri) {
        return URI_CLEANER.clean(uri);
    }

    public FilterChainManager configure() throws ServletException {

        if (!config.isStormpathWebEnabled()) {
            return mgr;
        }

        //Too much copy-and-paste. YUCK.
        //TODO: refactor this method to be more generic

        ConfigReader reader = new ExpressionConfigReader(servletContext, config);

        boolean assetsEnabled = reader.getBoolean("stormpath.web.assets.enabled");
        boolean jsEnabled = reader.getBoolean("stormpath.web.assets.js.enabled");
        boolean cssEnabled = reader.getBoolean("stormpath.web.assets.css.enabled");

        //Ensure handlers are registered:
        String loginUrl = config.getLoginConfig().getUri();
        String loginUrlPattern = cleanUri(loginUrl);
        boolean loginChainSpecified = false;
        boolean loginEnabled = config.getLoginConfig().isEnabled();

        String logoutUrl = config.getLogoutConfig().getUri();
        String logoutUrlPattern = cleanUri(logoutUrl);
        boolean logoutChainSpecified = false;
        boolean logoutEnabled = config.getLogoutConfig().isEnabled();

        String forgotUrl = config.getForgotPasswordConfig().getUri();
        String forgotUrlPattern = cleanUri(forgotUrl);
        boolean forgotChainSpecified = false;
        boolean forgotPasswordEnabled = config.getForgotPasswordConfig().isEnabled();

        String changeUrl = config.getChangePasswordConfig().getUri();
        String changeUrlPattern = cleanUri(changeUrl);
        boolean changeChainSpecified = false;
        boolean changePasswordEnabled = config.getChangePasswordConfig().isEnabled();

        String registerUrl = config.getRegisterConfig().getUri();
        String registerUrlPattern = cleanUri(registerUrl);
        boolean registerChainSpecified = false;
        boolean registerEnabled = config.getRegisterConfig().isEnabled();
        registerEnabled = config.getRegisterEnabledPredicate().test(registerEnabled,
            config.getApplicationResolver().getApplication(servletContext));

        String verifyUrl = config.getVerifyConfig().getUri();
        String verifyUrlPattern = cleanUri(verifyUrl);
        boolean verifyChainSpecified = false;
        boolean verifyEmailEnabled = config.getVerifyConfig().isEnabled();

        String accessTokenUrl = config.getAccessTokenUrl();
        String accessTokenUrlPattern = cleanUri(accessTokenUrl);
        boolean accessTokenChainSpecified = false;
        boolean oauthEnabled = config.isOAuthEnabled();

        String unauthorizedUrl = config.getUnauthorizedUrl();
        String unauthorizedUrlPattern = cleanUri(unauthorizedUrl);
        boolean unauthorizedChainSpecified = false;

        //TODO 542: what is this?
        String samlUrl = "/saml";
        String samlUrlPattern = cleanUri(samlUrl);
        String samlCallbackUrl = config.getCallbackUri();
        String samlCallbackPattern = cleanUri(samlCallbackUrl);
        boolean samlChainSpecified = false;
        boolean callbackEnabled = config.isCallbackEnabled();

        String meUrl = config.getMeUrl();
        String meUrlPattern = cleanUri(meUrl);
        boolean meChainSpecified = false;
        boolean meEnabled = config.isMeEnabled();

        boolean idSiteEnabled = config.isIdSiteEnabled();

        //IDSite Result
        String idSiteResultUrl = config.getCallbackUri();
        String idSiteResultUrlPattern = cleanUri(idSiteResultUrl);

        String googleCallbackUrl = config.get("stormpath.web.social.google.uri");
        String googleCallbackUrlPattern = cleanUri(googleCallbackUrl);
        boolean googleCallbackChainSpecified = false;

        String githubCallbackUrl = config.get("stormpath.web.social.github.uri");
        String githubCallbackUrlPattern = cleanUri(githubCallbackUrl);
        boolean githubCallbackChainSpecified = false;

        String facebookCallbackUrl = config.get("stormpath.web.social.facebook.uri");
        String facebookCallbackUrlPattern = cleanUri(facebookCallbackUrl);
        boolean facebookCallbackChainSpecified = false;

        String linkedinCallbackUrl = config.get("stormpath.web.social.linkedin.uri");
        String linkedinCallbackUrlPattern = cleanUri(linkedinCallbackUrl);
        boolean linkedinCallbackChainSpecified = false;

        //uriPattern-to-chainDefinition:
        Map<String, String> patternChains = new LinkedHashMap<String, String>();

        for (String key : config.keySet()) {

            if (key.startsWith(ROUTE_CONFIG_NAME_PREFIX)) {

                String uriPattern = key.substring(ROUTE_CONFIG_NAME_PREFIX.length());
                String chainDefinition = config.get(key);

                if (uriPattern.startsWith(linkedinCallbackUrl)) {
                    linkedinCallbackChainSpecified = true;

                    String filterName = DefaultFilter.linkedinCallback.name();
                    if (!chainDefinition.contains(filterName)) {
                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
                    }
                } else if (uriPattern.startsWith(facebookCallbackUrl)) {
                    facebookCallbackChainSpecified = true;

                    String filterName = DefaultFilter.facebookCallback.name();
                    if (!chainDefinition.contains(filterName)) {
                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
                    }
                } else if (uriPattern.startsWith(githubCallbackUrl)) {
                    githubCallbackChainSpecified = true;

                    String filterName = DefaultFilter.githubCallback.name();
                    if (!chainDefinition.contains(filterName)) {
                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
                    }
                } else if (uriPattern.startsWith(googleCallbackUrl)) {
                    googleCallbackChainSpecified = true;

                    String filterName = DefaultFilter.googleCallback.name();
                    if (!chainDefinition.contains(filterName)) {
                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
                    }
                } else if (uriPattern.startsWith(loginUrlPattern)) {
                    loginChainSpecified = true;

                    //did they specify the filter as a handler in the chain?  If not, append it:
                    String filterName = DefaultFilter.login.name();
                    if (!chainDefinition.contains(filterName)) {
                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
                    }
                } else if (uriPattern.startsWith(logoutUrlPattern)) {
                    logoutChainSpecified = true;

                    //did they specify the filter as a handler in the chain?  If not, append it:
                    String filterName = DefaultFilter.logout.name();
                    if (!chainDefinition.contains(filterName)) {
                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
                    }
                } else if (uriPattern.startsWith(forgotUrlPattern)) {
                    forgotChainSpecified = true;

                    //did they specify the filter as a handler in the chain?  If not, append it:
                    String filterName = DefaultFilter.forgot.name();
                    if (!chainDefinition.contains(filterName)) {
                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
                    }
                } else if (uriPattern.startsWith(changeUrlPattern)) {
                    changeChainSpecified = true;

                    //did they specify the filter as a handler in the chain?  If not, append it:
                    String filterName = DefaultFilter.change.name();
                    if (!chainDefinition.contains(filterName)) {
                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
                    }
                } else if (uriPattern.startsWith(registerUrlPattern)) {
                    registerChainSpecified = true;

                    //did they specify the filter as a handler in the chain?  If not, append it:
                    String filterName = DefaultFilter.register.name();
                    if (!chainDefinition.contains(filterName)) {
                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
                    }
                } else if (uriPattern.startsWith(verifyUrlPattern)) {
                    verifyChainSpecified = true;

                    //did they specify the filter as a handler in the chain?  If not, append it:
                    String filterName = DefaultFilter.verify.name();
                    if (!chainDefinition.contains(filterName)) {
                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
                    }
                } else if (uriPattern.startsWith(accessTokenUrlPattern)) {
                    accessTokenChainSpecified = true;

                    String filterName = DefaultFilter.accessToken.name();
                    if (!chainDefinition.contains(filterName)) {
                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
                    }

                } else if (uriPattern.startsWith(unauthorizedUrlPattern)) {
                    unauthorizedChainSpecified = true;

                    //did they specify the unauthorized filter as a handler in the chain?  If not, append it:
                    String filterName = DefaultFilter.unauthorized.name();
                    if (!chainDefinition.contains(filterName)) {
                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
                    }
                } else if (uriPattern.startsWith(samlUrlPattern)) {
                    samlChainSpecified = true;
                    String filterName = DefaultFilter.saml.name();
                    if (!chainDefinition.contains(filterName)) {
                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
                    }
                } else if (uriPattern.startsWith(meUrlPattern)) {
                    meChainSpecified = true;

                    String filterName = DefaultFilter.me.name();
                    if (!chainDefinition.contains(filterName)) {
                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
                    }
                }

                patternChains.put(uriPattern, chainDefinition);
            }
        }

        //https://github.com/stormpath/stormpath-sdk-java/issues/765
        if (assetsEnabled && (jsEnabled || cssEnabled)) {
            if (jsEnabled) {
                mgr.createChain("/assets/js/stormpath.js", "staticResource");
            }
            if (cssEnabled) {
                mgr.createChain("/assets/css/stormpath.css", "staticResource");
                mgr.createChain("/assets/css/custom.stormpath.css", "staticResource");
                mgr.createChain("/assets/css/override.stormpath.css", "staticResource");
            }
        }

        //register configured request handlers if not yet specified:
        if (!unauthorizedChainSpecified) {
            mgr.createChain(unauthorizedUrlPattern, DefaultFilter.unauthorized.name());
        }
        if (!loginChainSpecified && loginEnabled) {
            if (!idSiteEnabled) {
                mgr.createChain(loginUrlPattern, DefaultFilter.login.name());
            } else {
                mgr.createChain(loginUrlPattern, DefaultFilter.idSite.name());
                mgr.createChain(idSiteResultUrlPattern, DefaultFilter.idSiteResult.name());
            }
        }
        if (!logoutChainSpecified && logoutEnabled) {
            if (!idSiteEnabled) {
                mgr.createChain(logoutUrlPattern, DefaultFilter.logout.name());
            } else {
                mgr.createChain(logoutUrlPattern, DefaultFilter.idSiteLogout.name());
            }
        }
        if (!forgotChainSpecified && forgotPasswordEnabled) {
            if (!idSiteEnabled) {
                mgr.createChain(forgotUrlPattern, DefaultFilter.forgot.name());
            } else {
                mgr.createChain(forgotUrlPattern, DefaultFilter.idSiteForgot.name());
            }
        }
        if (!changeChainSpecified && changePasswordEnabled) {
            mgr.createChain(changeUrlPattern, DefaultFilter.change.name());
        }
        if (!registerChainSpecified && registerEnabled) {
            if (!idSiteEnabled) {
                mgr.createChain(registerUrlPattern, DefaultFilter.register.name());
            } else {
                mgr.createChain(registerUrlPattern, DefaultFilter.idSiteRegister.name());
            }
        }
        if (!verifyChainSpecified) {
            mgr.createChain(verifyUrlPattern, DefaultFilter.verify.name());
        }
        if (!accessTokenChainSpecified && oauthEnabled) {
            mgr.createChain(accessTokenUrlPattern, DefaultFilter.accessToken.name());
        }
        if (!samlChainSpecified && callbackEnabled) {
            mgr.createChain(samlUrlPattern, DefaultFilter.saml.name());
            mgr.createChain(samlCallbackPattern, DefaultFilter.samlResult.name());
        }
        if (!meChainSpecified && meEnabled) {
            mgr.createChain(meUrlPattern, "authc," + DefaultFilter.me.name());
        }
        if (!googleCallbackChainSpecified) {
            mgr.createChain(googleCallbackUrlPattern, DefaultFilter.googleCallback.name());
        }
        if (!githubCallbackChainSpecified) {
            mgr.createChain(githubCallbackUrlPattern, DefaultFilter.githubCallback.name());
        }
        if (!facebookCallbackChainSpecified) {
            mgr.createChain(facebookCallbackUrlPattern, DefaultFilter.facebookCallback.name());
        }
        if (!linkedinCallbackChainSpecified) {
            mgr.createChain(linkedinCallbackUrlPattern, DefaultFilter.linkedinCallback.name());
        }

        //register all specified chains:
        for (String pattern : patternChains.keySet()) {
            String chainDefinition = patternChains.get(pattern);
            mgr.createChain(pattern, chainDefinition);
        }

        return mgr;
    }
}
