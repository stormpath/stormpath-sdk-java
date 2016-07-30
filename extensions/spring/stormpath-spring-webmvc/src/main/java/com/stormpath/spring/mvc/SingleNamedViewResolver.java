package com.stormpath.spring.mvc;

import com.stormpath.sdk.lang.Assert;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import java.util.Locale;

/**
 * @since 1.0.0
 */
public class SingleNamedViewResolver implements ViewResolver, Ordered {

    private final String viewName;
    private final View view;
    private final int order;

    public SingleNamedViewResolver(String viewName, View view, int order) {
        Assert.hasText(viewName, "viewName cannot be null or empty.");
        Assert.notNull(view, "view cannot be null.");
        this.viewName = viewName;
        this.view = view;
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        if (viewName.equals(this.viewName)) {
            return view;
        }
        return null;
    }
}
