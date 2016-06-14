package com.stormpath.sdk.servlet.mvc;

import javax.servlet.http.HttpServletRequest;

/**
 * 1.0.RC8
 */
public class InternalResourceViewResolver implements ViewResolver {

    private String prefix;
    private String suffix;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public View getView(ViewModel model, HttpServletRequest request) {
        String url = toResourcePath(model.getViewName());
        return new InternalResourceView(url);
    }

    protected String toResourcePath(String viewName) {

        String prefix = getPrefix();
        String suffix = getSuffix();

        if (prefix == null && suffix == null) {
            return viewName;
        }

        StringBuilder sb = new StringBuilder();

        if (prefix != null) {
            sb.append(prefix);
        }

        sb.append(viewName);

        if (suffix != null) {
            sb.append(suffix);
        }

        return sb.toString();
    }
}
