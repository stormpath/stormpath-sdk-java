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
package com.stormpath.sdk.servlet.filter.account;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.account.RequestAccountResolver;
import com.stormpath.sdk.servlet.filter.AccessControlFilter;
import com.stormpath.sdk.servlet.http.UserAgent;
import com.stormpath.sdk.servlet.http.impl.DefaultUserAgent;
import com.stormpath.sdk.servlet.util.ServletUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AccountAuthorizationFilter extends AccessControlFilter {

    public static final String PATH_CONFIG_INIT_PARAM_NAME = "pathConfig";

    private Expression expression;

    protected String getPathConfig() {
        return getFilterConfig().getInitParameter(PATH_CONFIG_INIT_PARAM_NAME);
    }

    @Override
    protected void onInit() throws ServletException {
        super.onInit();
        String pathConfig = Strings.clean(getPathConfig());
        if (pathConfig != null) {
            try {
                this.expression = createExpression(pathConfig);
            } catch (Exception e) {
                String msg = "Unable to compile authorization expression [" + pathConfig + "]: " + e.getMessage();
                throw new ServletException(msg, e);
            }
        }
        super.onInit();
    }

    protected Expression createExpression(String pathConfig) {
        SpelCompilerMode mode = SpelCompilerMode.MIXED;
        ClassLoader cl = getClass().getClassLoader();
        SpelParserConfiguration config = new SpelParserConfiguration(mode, cl);
        SpelExpressionParser parser = new SpelExpressionParser(config);
        return parser.parseExpression(pathConfig);
    }

    protected String getUnauthorizedUrl() {
        return getConfig().getUnauthorizedUrl();
    }

    @Override
    protected boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response) throws Exception {

        if (request.getRemoteUser() != null) {

            //an account is present, let's ensure that they are authorized according to the expression:
            final Account requestAccount = RequestAccountResolver.INSTANCE.getAccount(request);

            //ensure that the expression can't modify the account:
            final Account account = new ImmutableAccount(requestAccount);

            if (this.expression != null) {

                StandardEvaluationContext ctx = new StandardEvaluationContext(account);
                ctx.setVariable("request", request);
                ctx.setVariable("response", response);

                Object value = this.expression.getValue(ctx);

                if (value instanceof Boolean) {
                    return (Boolean) value;
                } else {
                    String msg = "Specified authorization expression [" + getPathConfig() + "] must result in a " +
                                 "boolean return value.";
                    throw new ServletException(msg);
                }
            } else {
                //no expression provided so just let the request pass (i.e. the 'authorization' is that the
                //current request is made by a known user account):
                return true;
            }
        }

        return false;
    }

    @Override
    protected boolean onAccessDenied(HttpServletRequest request, HttpServletResponse response) throws Exception {

        UserAgent ua = new DefaultUserAgent(request);

        if (request.getRemoteUser() == null) { //not authenticated - can't determine access control rights:
            if (ua.isRestClient()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            } else {
                return redirectToLogin(request, response, "unauthorized");
            }
        }

        //authenticated but still not allowed to proceed:
        if (ua.isRestClient()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            String url = getUnauthorizedUrl();
            ServletUtils.issueRedirect(request, response, url, null, true, true);
        }

        return false;
    }
}
