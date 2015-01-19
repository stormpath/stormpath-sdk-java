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
package com.stormpath.sdk.servlet.i18n;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * @since 1.0.RC3
 */
public class MessageParamTag extends BodyTagSupport {

    private Object value;

    private boolean valueSet;

    @Override
    public int doEndTag() throws JspException {

        Object arg = null;

        if (this.valueSet) {
            arg = this.value;
        } else if (getBodyContent() != null) {
            // get the value from the tag body
            arg = getBodyContent().getString().trim();
        }

        MessageTag parent = (MessageTag) findAncestorWithClass(this, MessageTag.class);
        if (parent == null) {
            throw new JspException("The param tag must be a descendant of a tag that supports parameters");
        }

        parent.addArgument(arg);

        return EVAL_PAGE;
    }

    /**
     * Sets the value of the argument.  This is optional - if not set, the tag's body content is evaluated.
     *
     * @param value the parameter value
     */
    public void setValue(Object value) {
        this.value = value;
        this.valueSet = true;
    }

    @Override
    public void release() {
        super.release();
        this.value = null;
        this.valueSet = false;
    }
}
