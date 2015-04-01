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
package com.stormpath.sdk.servlet.mvc;

import java.util.Map;

/**
 * A {@code ViewModel} represents a particular view that should be rendered as well as the data model that will be used
 * (if any) when rendering the view.
 *
 * <h4>Redirects</h4>
 *
 * <p>Redirects are a little special: if {@link #isRedirect() isRedirect()} is {@code true}, an actual view won't be
 * rendered.  Instead an HTTP 302 redirect will be sent in the response to indicate that a different view should be
 * rendered.</p>
 *
 * @since 1.0.RC4
 */
public interface ViewModel {

    /**
     * If {@link #isRedirect()} is true, this method returns the logical name of the view (e.g. page template) to be
     * rendered. If {@link #isRedirect()} is false, the return value is a context-relative URI to where the user will be
     * redirected.
     *
     * @return A logical name of the view to be rendered, or, if {@link #isRedirect()}, the context-relative URI to
     * where the user will be redirected.
     */
    String getViewName();

    /**
     * Returns {@code true} if the {@code ViewModel} instance indicates an HTTP redirect should be returned instead of
     * rendering an actual page template, {@code false} if a page template should be rendered using the associated
     * {@link #getModel() model}.
     *
     * @return {@code true} if the {@code ViewModel} instance indicates an HTTP redirect should be returned instead of
     * rendering an actual page template, {@code false} if a page template should be rendered using the associated
     * {@link #getModel() model}.
     */
    boolean isRedirect();

    /**
     * Returns the model used when rendering the view page template or {@code null} if there is no model to render.
     *
     * @return the model used when rendering the view page template or {@code null} if there is no model to render.
     */
    Map<String, ?> getModel();

}
