package com.stormpath.sdk.mail.templates;

/**
 */
public interface TemplateRenderer<T extends CharSequence> {

    T render(TemplateRenderRequest request);

}
