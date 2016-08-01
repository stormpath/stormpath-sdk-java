package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.filter.StaticResourceFilter;

import javax.servlet.ServletContext;

/**
 * @since 1.0.0
 */
public class StaticResourceFilterFactory extends FilterFactory<StaticResourceFilter> {

    @Override
    protected StaticResourceFilter createInstance(ServletContext servletContext, Config config) throws Exception {

        StaticResourceFilter filter = new StaticResourceFilter();

        String defaultFilterName = config.get("stormpath.web.assets.defaultServletName");
        if (Strings.hasText(defaultFilterName)) {
            filter.setDefaultServletName(defaultFilterName);
        }

        filter.init(servletContext);

        return filter;
    }
}
