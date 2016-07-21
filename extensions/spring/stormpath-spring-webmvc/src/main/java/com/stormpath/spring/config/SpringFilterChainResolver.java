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
package com.stormpath.spring.config;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.DefaultFilterChainResolverFactory;
import com.stormpath.sdk.servlet.filter.ControllerConfigResolver;
import com.stormpath.sdk.servlet.filter.DefaultFilter;
import com.stormpath.sdk.servlet.filter.FilterChainManager;
import com.stormpath.sdk.servlet.filter.FilterChainResolver;
import com.stormpath.sdk.servlet.filter.PathMatchingFilterChainResolver;
import com.stormpath.sdk.servlet.filter.ProxiedFilterChain;
import com.stormpath.sdk.servlet.filter.account.AccountResolverFilter;
import com.stormpath.sdk.servlet.mvc.LogoutController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0.RC3
 */
class SpringFilterChainResolver extends DefaultFilterChainResolverFactory {

//    @Autowired
//    AccountResolverFilter stormpathAccountResolverFilter;

//    @Autowired
//    ControllerConfigResolver stormpathLogoutControllerConfigResolver;

//    @Autowired
    protected Config stormpathInternalConfig;

//    @Autowired
    protected Filter stormpathAccountResolverFilter;

    protected ServletContext servletContext;

    public void setConfig(Config stormpathInternalConfig) {
        this.stormpathInternalConfig = stormpathInternalConfig;
    }

    public void setAccountResolverFilter(Filter stormpathAccountResolverFilter) {
        this.stormpathAccountResolverFilter = stormpathAccountResolverFilter;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

//    @Autowired
//    SpringFilterChainResolver(Config stormpathInternalConfig, Filter stormpathAccountResolverFilter) {
//        this.stormpathInternalConfig = stormpathInternalConfig;
//        this.stormpathAccountResolverFilter = stormpathAccountResolverFilter;
//    }

    protected FilterChainResolver getFilterChainResolver() throws ServletException {

        final PathMatchingFilterChainResolver resolver = new PathMatchingFilterChainResolver(servletContext);
//
//        //now register any configured chains:
//        Config config = getConfig();
//
//        //ensure the always-on AccountResolverFilter is available:
//        Filter accountFilter = Filters.builder().setServletContext(getServletContext())
//                .setName(Strings.uncapitalize(AccountResolverFilter.class.getSimpleName()))
//                .setFilterClass(AccountResolverFilter.class).build();
//
        final List<Filter> immediateExecutionFilters = Arrays.asList((Filter) stormpathAccountResolverFilter);
//
//        //Too much copy-and-paste. YUCK.
//        //TODO: refactor this method to be more generic
//
        //Ensure handlers are registered:
        String loginUrl = stormpathInternalConfig.getLoginControllerConfig().getUri();
        String loginUrlPattern = cleanUri(loginUrl);
        boolean loginChainSpecified = false;
        boolean loginEnabled = stormpathInternalConfig.getLoginControllerConfig().isEnabled();
//
        String logoutUrl = stormpathInternalConfig.getLogoutControllerConfig().getUri();
        String logoutUrlPattern = cleanUri(logoutUrl);
        boolean logoutChainSpecified = false;
        boolean logoutEnabled = stormpathInternalConfig.getLogoutControllerConfig().isEnabled();
//
        String forgotUrl = stormpathInternalConfig.getForgotPasswordControllerConfig().getUri();
        String forgotUrlPattern = cleanUri(forgotUrl);
        boolean forgotChainSpecified = false;
        boolean forgotPasswordEnabled = stormpathInternalConfig.getForgotPasswordControllerConfig().isEnabled();
//
        String changeUrl = stormpathInternalConfig.getChangePasswordControllerConfig().getUri();
        String changeUrlPattern = cleanUri(changeUrl);
        boolean changeChainSpecified = false;
        boolean changePasswordEnabled = stormpathInternalConfig.getChangePasswordControllerConfig().isEnabled();
//
        String registerUrl = stormpathInternalConfig.getRegisterControllerConfig().getUri();
        String registerUrlPattern = cleanUri(registerUrl);
        boolean registerChainSpecified = false;
        boolean registerEnabled = stormpathInternalConfig.getRegisterControllerConfig().isEnabled();

        String verifyUrl = stormpathInternalConfig.getVerifyControllerConfig().getUri();
        String verifyUrlPattern = cleanUri(verifyUrl);
        boolean verifyChainSpecified = false;
        boolean verifyEmailEnabled = stormpathInternalConfig.getVerifyControllerConfig().isEnabled();

        String sendVerificationUrl = stormpathInternalConfig.getSendVerificationEmailControllerConfig().getUri();
        String sendVerificationUrlPattern = cleanUri(sendVerificationUrl);
        boolean sendVerificationEmailChainSpecified = false;

        String accessTokenUrl = stormpathInternalConfig.getAccessTokenUrl();
        String accessTokenUrlPattern = cleanUri(accessTokenUrl);
        boolean accessTokenChainSpecified = false;
        boolean oauthEnabled = stormpathInternalConfig.isOAuthEnabled();
//
//        String unauthorizedUrl = config.getUnauthorizedUrl();
//        String unauthorizedUrlPattern = cleanUri(unauthorizedUrl);
//        boolean unauthorizedChainSpecified = false;
//
        //TODO: saml uri?
//        String samlUrl = "/saml";
//        String samlUrlPattern = cleanUri(samlUrl);
//        boolean samlChainSpecified = false;
//        boolean callbackEnabled = stormpathInternalConfig.isCallbackEnabled();

//        String meUrl = stormpathInternalConfig.getMeUrl();
//        String meUrlPattern = cleanUri(meUrl);
//        boolean meChainSpecified = false;
//        boolean meEnabled = stormpathInternalConfig.isMeEnabled();
//
//        // todo: figure out where idsite is added to the filter chain and allow disabling
//        boolean isIdSiteEnabled = config.isIdSiteEnabled();
//

        String googleCallbackUrl = stormpathInternalConfig.getGoogleControllerConfig().getUri();
        String googleCallbackUrlPattern = cleanUri(googleCallbackUrl);
        boolean googleCallbackChainSpecified = false;

        String facebookCallbackUrl = stormpathInternalConfig.getFacebookControllerConfig().getUri();
        String facebookCallbackUrlPattern = cleanUri(facebookCallbackUrl);
        boolean facebookCallbackChainSpecified = false;

        String githubCallbackUrl = stormpathInternalConfig.getGithubControllerConfig().getUri();
        String githubCallbackUrlPattern = cleanUri(githubCallbackUrl);
        boolean githubCallbackChainSpecified = false;

        String linkedinCallbackUrl = stormpathInternalConfig.getLinkedinControllerConfig().getUri();
        String linkedinCallbackUrlPattern = cleanUri(linkedinCallbackUrl);
        boolean linkedinCallbackChainSpecified = false;

        //uriPattern-to-chainDefinition:
        Map<String, String> patternChains = new LinkedHashMap<String, String>();
//
//        for (String key : config.keySet()) {
//
//            if (key.startsWith(ROUTE_CONFIG_NAME_PREFIX)) {
//
//                String uriPattern = key.substring(ROUTE_CONFIG_NAME_PREFIX.length());
//                String chainDefinition = config.get(key);
//
//                if (uriPattern.startsWith(linkedinCallbackUrl)) {
//                    linkedinCallbackChainSpecified = true;
//
//                    String filterName = DefaultFilter.linkedinCallback.name();
//                    if (!chainDefinition.contains(filterName)) {
//                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
//                    }
//                } else if (uriPattern.startsWith(facebookCallbackUrl)) {
//                    facebookCallbackChainSpecified = true;
//
//                    String filterName = DefaultFilter.facebookCallback.name();
//                    if (!chainDefinition.contains(filterName)) {
//                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
//                    }
//                } else if (uriPattern.startsWith(githubCallbackUrl)) {
//                    githubCallbackChainSpecified = true;
//
//                    String filterName = DefaultFilter.githubCallback.name();
//                    if (!chainDefinition.contains(filterName)) {
//                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
//                    }
//                } else if (uriPattern.startsWith(googleCallbackUrl)) {
//                    googleCallbackChainSpecified = true;
//
//                    String filterName = DefaultFilter.googleCallback.name();
//                    if (!chainDefinition.contains(filterName)) {
//                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
//                    }
//                } else if (uriPattern.startsWith(loginUrlPattern)) {
//                    loginChainSpecified = true;
//
//                    //did they specify the filter as a handler in the chain?  If not, append it:
//                    String filterName = DefaultFilter.login.name();
//                    if (!chainDefinition.contains(filterName)) {
//                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
//                    }
//                } else if (uriPattern.startsWith(logoutUrlPattern)) {

                    //TODO: ???
                    //did they specify the filter as a handler in the chain?  If not, append it:
//                    String filterName = DefaultFilter.logout.name();
//                    if (!chainDefinition.contains(filterName)) {
//                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
////                    }
//                } else if (uriPattern.startsWith(forgotUrlPattern)) {
//                    forgotChainSpecified = true;
//
//                    //did they specify the filter as a handler in the chain?  If not, append it:
//                    String filterName = DefaultFilter.forgot.name();
//                    if (!chainDefinition.contains(filterName)) {
//                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
//                    }
//                } else if (uriPattern.startsWith(changeUrlPattern)) {
//                    changeChainSpecified = true;
//
//                    //did they specify the filter as a handler in the chain?  If not, append it:
//                    String filterName = DefaultFilter.change.name();
//                    if (!chainDefinition.contains(filterName)) {
//                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
//                    }
//                } else if (uriPattern.startsWith(registerUrlPattern)) {
//                    registerChainSpecified = true;
//
//                    //did they specify the filter as a handler in the chain?  If not, append it:
//                    String filterName = DefaultFilter.register.name();
//                    if (!chainDefinition.contains(filterName)) {
//                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
//                    }
//                } else if (uriPattern.startsWith(verifyUrlPattern)) {
//                    verifyChainSpecified = true;
//
//                    //did they specify the filter as a handler in the chain?  If not, append it:
//                    String filterName = DefaultFilter.verify.name();
//                    if (!chainDefinition.contains(filterName)) {
//                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
//                    }
//                } else if (uriPattern.startsWith(sendVerificationUrlPattern)) {
//                    sendVerificationEmailChainSpecified = true;
//
//                    //did they specify the filter as a handler in the chain?  If not, append it:
//                    String filterName = DefaultFilter.sendVerificationEmail.name();
//                    if (!chainDefinition.contains(filterName)) {
//                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
//                    }
//                } else if (uriPattern.startsWith(accessTokenUrlPattern)) {
//                    accessTokenChainSpecified = true;
//
//                    String filterName = DefaultFilter.accessToken.name();
//                    if (!chainDefinition.contains(filterName)) {
//                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
//                    }
//
//                } else if (uriPattern.startsWith(unauthorizedUrlPattern)) {
//                    unauthorizedChainSpecified = true;
//
//                    //did they specify the unauthorized filter as a handler in the chain?  If not, append it:
//                    String filterName = DefaultFilter.unauthorized.name();
//                    if (!chainDefinition.contains(filterName)) {
//                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
//                    }
//                } else if (uriPattern.startsWith(samlUrlPattern)) {
//                    samlChainSpecified = true;
//                    String filterName = DefaultFilter.saml.name();
//                    if (!chainDefinition.contains(filterName)) {
//                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
//                    }
//                } else if (uriPattern.startsWith(meUrlPattern)) {
//                    meChainSpecified = true;
//
//                    String filterName = DefaultFilter.me.name();
//                    if (!chainDefinition.contains(filterName)) {
//                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
//                    }
//                }
//
//                patternChains.put(uriPattern, chainDefinition);
//            }
//        }
//
        //register configured request handlers if not yet specified:
        FilterChainManager fcManager = resolver.getFilterChainManager();
//
//        if (!unauthorizedChainSpecified) {
//            fcManager.createChain(unauthorizedUrlPattern, DefaultFilter.unauthorized.name());
//        }
        if (!loginChainSpecified && loginEnabled) {
            fcManager.createChain(loginUrlPattern, DefaultFilter.login.name());
        }
        if (!logoutChainSpecified && logoutEnabled) {
            fcManager.createChain(logoutUrlPattern, DefaultFilter.logout.name());
        }
        if (!forgotChainSpecified && forgotPasswordEnabled) {
            fcManager.createChain(forgotUrlPattern, DefaultFilter.forgot.name());
        }
        if (!changeChainSpecified && changePasswordEnabled) {
            fcManager.createChain(changeUrlPattern, DefaultFilter.change.name());
        }
        if (!registerChainSpecified && registerEnabled) {
            fcManager.createChain(registerUrlPattern, DefaultFilter.register.name());
        }
        if (!verifyChainSpecified) {
            fcManager.createChain(verifyUrlPattern, DefaultFilter.verify.name());
        }
        if (!sendVerificationEmailChainSpecified && verifyEmailEnabled) {
            fcManager.createChain(sendVerificationUrlPattern, DefaultFilter.sendVerificationEmail.name());
        }
        if (!accessTokenChainSpecified && oauthEnabled) {
            fcManager.createChain(accessTokenUrlPattern, DefaultFilter.accessToken.name());
        }
//        if (!samlChainSpecified && callbackEnabled) {
//            fcManager.createChain(samlUrlPattern, DefaultFilter.saml.name());
//        }
//        if (!meChainSpecified && meEnabled) {
//            fcManager.createChain(meUrlPattern, "authc," + DefaultFilter.me.name());
//        }

        if (!googleCallbackChainSpecified) {
            fcManager.createChain(googleCallbackUrlPattern, DefaultFilter.googleCallback.name());
        }
        if (!facebookCallbackChainSpecified) {
            fcManager.createChain(facebookCallbackUrlPattern, DefaultFilter.facebookCallback.name());
        }
        if (!githubCallbackChainSpecified) {
            fcManager.createChain(githubCallbackUrlPattern, DefaultFilter.githubCallback.name());
        }
        if (!linkedinCallbackChainSpecified) {
            fcManager.createChain(linkedinCallbackUrlPattern, DefaultFilter.linkedinCallback.name());
        }
//
        //register all specified chains:
        for (String pattern : patternChains.keySet()) {
            String chainDefinition = patternChains.get(pattern);
            resolver.getFilterChainManager().createChain(pattern, chainDefinition);
        }
//
//        //we wrap the above constructed resolver with one that will always guarantee the account filter executes
//        //first before all others:
//
        return new FilterChainResolver() {

            @Override
            public FilterChain getChain(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {

                FilterChain target = resolver.getChain(request, response, chain);

                if (target == null) {
                    target = chain;
                }

                //The account resolver filter always executes before any other configured filters in the chain:
                target = new ProxiedFilterChain(target, immediateExecutionFilters);

                return target;
            }
        };
    }
}
