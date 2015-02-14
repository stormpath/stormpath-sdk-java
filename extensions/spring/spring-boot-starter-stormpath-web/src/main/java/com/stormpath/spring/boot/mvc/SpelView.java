/*
 * Copyright 2015 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.spring.boot.mvc;

import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.web.servlet.View;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class SpelView implements View {

    private final String template;

    private final StandardEvaluationContext context = new StandardEvaluationContext();

    private PropertyPlaceholderHelper helper;

    private PropertyPlaceholderHelper.PlaceholderResolver resolver;

    public SpelView(String template) {
        this.template = template;
        this.context.addPropertyAccessor(new MapAccessor());
        this.helper = new PropertyPlaceholderHelper("${", "}");
        this.resolver = new SpelPlaceholderResolver(this.context);
    }

    @Override
    public String getContentType() {
        return "text/html";
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request,
                       HttpServletResponse response) throws Exception {
        if (response.getContentType() == null) {
            response.setContentType(getContentType());
        }
        Map<String, Object> map = new HashMap<String, Object>(model);
        map.put("path", request.getContextPath());
        this.context.setRootObject(map);
        String result = this.helper.replacePlaceholders(this.template, this.resolver);
        response.getWriter().append(result);
    }

    /**
     * SpEL based {@link org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver}.
     */
    private static class SpelPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {

        private final SpelExpressionParser parser = new SpelExpressionParser();

        private final StandardEvaluationContext context;

        public SpelPlaceholderResolver(StandardEvaluationContext context) {
            this.context = context;
        }

        @Override
        public String resolvePlaceholder(String name) {
            Expression expression = this.parser.parseExpression(name);
            try {
                Object value = expression.getValue(this.context);
                return HtmlUtils.htmlEscape(value == null ? null : value.toString());
            }
            catch (Exception ex) {
                return null;
            }
        }

    }

}
