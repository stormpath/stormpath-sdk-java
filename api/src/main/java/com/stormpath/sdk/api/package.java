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
/**
 * Support for securing your application's REST API with {@link com.stormpath.sdk.api.ApiKey ApiKeys}.
 *
 * <p>HTTP requests authenticated with {@link com.stormpath.sdk.api.ApiKey ApiKeys} and sent to your application are
 * asserted via your
 * {@link com.stormpath.sdk.application.Application Application} instance's
 * {@link com.stormpath.sdk.application.Application#authenticateApiRequest(Object) authenticateApiRequest(httpRequest)}
 * and {@link com.stormpath.sdk.application.Application#authenticateOauthRequest(Object)
 * authenticateOauthRequest(httpRequest)} methods.</p>
 *
 * @see com.stormpath.sdk.api.ApiKey ApiKey
 * @see com.stormpath.sdk.account.Account#getApiKeys() account.getApiKeys()
 * @see com.stormpath.sdk.account.Account#createApiKey() account.createApiKey()
 * @see com.stormpath.sdk.application.Application#authenticateApiRequest(Object)
 * @see com.stormpath.sdk.application.Application#authenticateOauthRequest(Object)
 * @since 1.0.RC
 */


