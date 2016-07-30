package com.stormpath.spring.mvc;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.i18n.MessageContext;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.servlet.ServletContext;

/**
 * @since 1.0.0
 */
public class MessageContextRegistrar implements InitializingBean, DisposableBean {

    private final MessageContext messageContext;
    private final ServletContext servletContext;

    public MessageContextRegistrar(MessageContext messageContext, ServletContext servletContext) {
        Assert.notNull(messageContext, "MessageContext cannot be null.");
        Assert.notNull(servletContext, "ServletContext cannot be null.");
        this.messageContext = messageContext;
        this.servletContext = servletContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        servletContext.setAttribute(MessageContext.class.getName(), messageContext);
    }

    @Override
    public void destroy() throws Exception {
        servletContext.removeAttribute(MessageContext.class.getName());
    }
}
