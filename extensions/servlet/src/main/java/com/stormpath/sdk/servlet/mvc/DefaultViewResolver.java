package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.filter.ContentNegotiationResolver;
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.UnresolvedMediaTypeException;
import com.stormpath.sdk.servlet.http.UserAgent;
import com.stormpath.sdk.servlet.http.UserAgents;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 1.0.RC8
 */
public class DefaultViewResolver implements ViewResolver {

    private final ViewResolver delegateViewResolver;

    protected final List<MediaType> producesMediaTypes;

    private final View jsonView;

    public DefaultViewResolver(ViewResolver delegate, View jsonView, List<MediaType> producesMediaTypes) {
        Assert.notNull(delegate, "Delegate ViewResolver cannot be null.");
        Assert.notNull(jsonView, "JSON View cannot be null.");
        this.delegateViewResolver = delegate;
        this.jsonView = jsonView;
        this.producesMediaTypes = producesMediaTypes;
    }

    @Override
    public View getView(ViewModel model, HttpServletRequest request) {

        try {
            MediaType mediaType = ContentNegotiationResolver.INSTANCE.getContentType(request, null, producesMediaTypes);
            if (mediaType.equals(MediaType.APPLICATION_JSON)) {
                return jsonView;
            }
            if (mediaType.equals(MediaType.TEXT_HTML)) {
                return delegateViewResolver.getView(model, request);
            }
        } catch (UnresolvedMediaTypeException e) {
            //No MediaType could be resolved for this request based on the produces setting. Let's return null
        }

        return null;

    }
}
