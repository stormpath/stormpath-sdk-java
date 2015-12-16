package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.http.UserAgent;
import com.stormpath.sdk.servlet.http.UserAgents;

import javax.servlet.http.HttpServletRequest;

/**
 * 1.0.RC8
 */
public class DefaultViewResolver implements ViewResolver {

    private final ViewResolver delegateViewResolver;

    private final View jsonView;

    public DefaultViewResolver(ViewResolver delegate, View jsonView) {
        Assert.notNull(delegate, "Delegate ViewResolver cannot be null.");
        Assert.notNull(jsonView, "JSON View cannot be null.");
        this.delegateViewResolver = delegate;
        this.jsonView = jsonView;
    }

    @Override
    public View getView(ViewModel model, HttpServletRequest request) {

        UserAgent ua = UserAgents.get(request);

        if (ua.isJsonPreferred()) {
            return jsonView;
        }

        return delegateViewResolver.getView(model, request);
    }
}
