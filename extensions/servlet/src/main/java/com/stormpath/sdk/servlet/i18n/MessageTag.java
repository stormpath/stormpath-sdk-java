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
package com.stormpath.sdk.servlet.i18n;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * @since 1.0.RC3
 */
public class MessageTag extends TagSupport {

    private String key;
    private String var;
    private String scope = "page";
    private List<Object> nestedArguments;

    /**
     * Sets the i18n message key for this tag.
     *
     * @param key the i18n message key for this tag.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Set PageContext attribute name under which to expose a variable that contains the resolved message.
     * @see #setScope
     * @see javax.servlet.jsp.PageContext#setAttribute
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * Set the scope to export the variable to.  Default is <code>page</code>.
     * @see #setVar
     * @see javax.servlet.jsp.PageContext#setAttribute
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    protected Config getConfig() throws JspException {
        ServletRequest request = pageContext.getRequest();
        return ConfigResolver.INSTANCE.getConfig(request.getServletContext());
    }

    protected MessageSource getMessageSource() throws JspException {
        Config config = getConfig();
        try {
            return config.getInstance("stormpath.web.message.source");
        } catch (ServletException e) {
            throw new JspException(e.getMessage(), e);
        }
    }

    protected Resolver<Locale> getLocaleResolver() throws JspException {
        Config config = getConfig();
        try {
            return config.getInstance("stormpath.web.locale.resolver");
        } catch (ServletException e) {
            throw new JspException(e.getMessage(), e);
        }
    }

    public int doStartTag() throws JspException {
        this.nestedArguments = new LinkedList<Object>();
        return EVAL_BODY_INCLUDE;
    }

    public int getScopeCode() {

        Assert.hasText(this.scope, "Scope cannot be null or empty.");

        if ("request".equals(this.scope)) {
            return PageContext.REQUEST_SCOPE;
        }
        if ("session".equals(this.scope)) {
            return PageContext.SESSION_SCOPE;
        }
        if ("application".equals(this.scope)) {
            return PageContext.APPLICATION_SCOPE;
        }

        //default:
        return PageContext.PAGE_SCOPE;
    }

    @Override
    public int doEndTag() throws JspException {

        try {

            String msg = resolveMessage();

            // Expose as variable if requested:
            if (this.var != null) {
                pageContext.setAttribute(this.var, msg, getScopeCode());
            } else {
                writeMessage(msg);
            }

            return EVAL_PAGE;

        } catch (IOException ex) {
            throw new JspTagException(ex.getMessage(), ex);
        }
    }

    public void addArgument(Object argument) throws JspTagException {
        this.nestedArguments.add(argument);
    }

    private String resolveMessage() throws JspException {

        MessageSource msgSrc = getMessageSource();
        Resolver<Locale> localeResolver = getLocaleResolver();

        HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
        HttpServletResponse response = (HttpServletResponse) this.pageContext.getResponse();

        Locale locale = localeResolver.get(request, response);

        String key = this.key;

        if (!Strings.hasText(key)) {
            String msg = "Message tag 'key' attribute is required.";
            throw new JspException(msg);
        }

        if (this.nestedArguments == null) {
            return msgSrc.getMessage(key, locale);
        } else {
            return msgSrc.getMessage(key, locale, this.nestedArguments.toArray());
        }
    }

    /**
     * Write the message to the page. <p>Can be overridden in subclasses, e.g. for testing purposes.
     *
     * @param msg the message to write
     * @throws java.io.IOException if writing failed
     */
    protected void writeMessage(String msg) throws IOException {
        pageContext.getOut().write(msg);
    }
}
