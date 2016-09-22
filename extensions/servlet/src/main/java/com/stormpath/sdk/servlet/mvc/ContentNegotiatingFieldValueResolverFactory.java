package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.http.MediaType;

import javax.servlet.ServletContext;
import java.util.List;

/**
 * @since 1.1.0
 */
public class ContentNegotiatingFieldValueResolverFactory extends ConfigSingletonFactory<ContentNegotiatingFieldValueResolver> {
    @Override
    protected ContentNegotiatingFieldValueResolver createInstance(ServletContext servletContext) throws Exception {
        List<MediaType> producedMediaTypes = getConfig().getProducedMediaTypes();

        ContentNegotiatingFieldValueResolver contentNegotiatingFieldValueResolver = new ContentNegotiatingFieldValueResolver();
        contentNegotiatingFieldValueResolver.setProduces(producedMediaTypes);
        return contentNegotiatingFieldValueResolver;
    }
}
