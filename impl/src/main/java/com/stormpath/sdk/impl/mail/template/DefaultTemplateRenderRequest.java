package com.stormpath.sdk.impl.mail.template;

import com.stormpath.sdk.mail.templates.TemplateRenderRequest;

import java.util.Map;

/**
 */
public class DefaultTemplateRenderRequest implements TemplateRenderRequest {

    private String template;
    private Map<String, Object> context;

    @Override
    public String getTemplate() {
        return template;
    }

    @Override
    public TemplateRenderRequest setTemplate(String template) {
        this.template = template;
        return this;
    }

    @Override
    public Map<String, Object> getContext() {
        return context;
    }

    @Override
    public TemplateRenderRequest setContext(Map<String, Object> context) {
        this.context = context;
        return this;
    }
}
