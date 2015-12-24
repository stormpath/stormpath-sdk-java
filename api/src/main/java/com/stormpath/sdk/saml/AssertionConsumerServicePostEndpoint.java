/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.saml;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.resource.Resource;

/**
 * The Stormpath endpoint that will process SAML Assertions sent by the SAML Identity Provider for a specific
 * Stormpath Directory.  The endpoint href is the resource href, i.e. {@link #getHref()}.
 *
 * <p>When Stormpath receives a SAML assertion at this endpoint, the assertion will be converted to a JWT that
 * represents the authenticated Stormpath account.  Stormpath will then relay this JWT to your application by
 * redirecting the user agent (browser) to your Application's
 * {@link Application#getAuthorizedCallbackUris() authorized callback URI}.</p>
 *
 * <p><b>NOTE:</b> this is *not* a REST endpoint - it is specific to the SAML protocol and it does not produce or
 * consume JSON data.  Do not attempt to resolve this href as an SDK or JSON resource.</p>
 *
 * @see #getHref()
 * @since 1.0.RC8
 */
public interface AssertionConsumerServicePostEndpoint extends Resource {
}
