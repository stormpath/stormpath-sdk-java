package com.stormpath.sdk.impl.mail.template;

import com.stormpath.sdk.mail.templates.TemplateRenderRequest;
import com.stormpath.sdk.mail.templates.TemplateRenderer;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.StringWriter;

/**
 */
public class VelocityTemplateRenderer implements TemplateRenderer<String> {

    @Override
    public String render(TemplateRenderRequest request) {

        VelocityEngine velocity = new VelocityEngine();
        velocity.init();
        VelocityContext context = new VelocityContext(request.getContext());

        StringWriter writer = new StringWriter();
        velocity.evaluate(context, writer, "render-template", request.getTemplate());

        return writer.toString();
    }

}
