/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.servlet.config;

import com.stormpath.sdk.lang.Assert;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import javax.servlet.ServletContext;
import java.util.Map;

public class ExpressionConfigReader implements ConfigReader {

    private static final SpelExpressionParser EXPR_PARSER = createExpressionParser();
    private static final ParserContext PARSER_CONTEXT = new ParserContext() {
        @Override
        public boolean isTemplate() {
            return true;
        }

        @Override
        public String getExpressionPrefix() {
            return "${";
        }

        @Override
        public String getExpressionSuffix() {
            return "}";
        }
    };

    private final Map<String, String> PROPS;
    private final StandardEvaluationContext EXPR_CONTEXT;

    private static SpelExpressionParser createExpressionParser() {
        SpelCompilerMode mode = SpelCompilerMode.MIXED;
        ClassLoader cl = DefaultConfig.class.getClassLoader();
        SpelParserConfiguration config = new SpelParserConfiguration(mode, cl);
        return new SpelExpressionParser(config);
    }

    public ExpressionConfigReader(ServletContext servletContext, Map<String, String> props) {
        Assert.notNull(servletContext);
        Assert.notNull(props);
        this.PROPS = props;

        StandardEvaluationContext ctx = new StandardEvaluationContext();
        EvaluationModel model = new EvaluationModel(servletContext, props);
        ctx.setRootObject(model);
        this.EXPR_CONTEXT = ctx;
    }

    @Override
    public String getString(String name) {
        String val = PROPS.get(name);
        if (isExpression(val)) {
            Expression exp = EXPR_PARSER.parseExpression(val, PARSER_CONTEXT);
            Object o = exp.getValue(EXPR_CONTEXT);
            return o != null ? String.valueOf(o) : null;
        }
        return val;
    }

    @Override
    public int getInt(String name) {
        String val = PROPS.get(name);
        try {
            if (isExpression(val)) {
                Expression exp = EXPR_PARSER.parseExpression(val, PARSER_CONTEXT);
                Object o = exp.getValue(EXPR_CONTEXT);
                if (o instanceof Integer) {
                    return (Integer) o;
                }
                if (o instanceof Long) {
                    return ((Long) o).intValue();
                }
                throw new IllegalArgumentException("The " + name + " property expression must evaluate to an integer.");
            }
            return Integer.parseInt(val);
        } catch (Exception e) {
            throw new IllegalArgumentException(name + " value must be an integer.", e);
        }
    }

    @Override
    public boolean getBoolean(String name) {
        String val = PROPS.get(name);
        try {
            if (isExpression(val)) {
                Expression exp = EXPR_PARSER.parseExpression(val, PARSER_CONTEXT);
                Object o = exp.getValue(EXPR_CONTEXT);
                if (!(o instanceof Boolean)) {
                    String msg = "The " + name + " property expression must evaluate to a boolean.";
                    throw new IllegalArgumentException(msg);
                }
                return (Boolean) o;
            }
            return Boolean.parseBoolean(val);
        } catch (Exception e) {
            throw new IllegalArgumentException(name + " value must be a boolean.", e);
        }
    }

    private boolean isExpression(String val) {
        return val != null &&
               val.startsWith(PARSER_CONTEXT.getExpressionPrefix()) &&
               val.endsWith(PARSER_CONTEXT.getExpressionSuffix());
    }
}
