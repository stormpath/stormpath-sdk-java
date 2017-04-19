package com.stormpath.sdk.mail.templates;

import java.util.Map;

/**
 *
 */
public interface TemplateRenderRequest {

    String getTemplate();
    TemplateRenderRequest setTemplate(String template);

    Map<String, Object> getContext();
    TemplateRenderRequest setContext(Map<String, Object> context);

}
