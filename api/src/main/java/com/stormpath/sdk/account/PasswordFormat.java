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
package com.stormpath.sdk.account;

/**
 * A {@code PasswordFormat} represents the various formats supported for Password Import
 * Read more info about the supported formats <a href="http://docs.stormpath.com/rest/product-guide/#create-an-account-with-an-existing-password-hash">here</a>
 *
 * @since 1.0.RC4.6
 */
public enum PasswordFormat {

    /**
     * Modular Crypt Format, a "$" delimited string.
     */
    MCF
}
