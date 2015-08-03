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
package tutorial;

import com.stormpath.sdk.account.Account;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * @since 1.0.RC4.4
 */
@Service
public class HelloService {

    //Let's just specify some role here so we can grant it access to restricted resources
    public static final String roleA = "GROUP_HREF_HERE";

    /**
     * Only users who have a Custom Data entry in their Stormpath Account or Group containing something like
     * <code>"springSecurityPermissions":["play:*"]</code> or <code>"springSecurityGrantedAuthorities":["play:outside"]</code>
     * will be allowed to execute this method.
     */
    @PreAuthorize("hasRole('" + roleA + "') and hasPermission('play', 'outside')")
    public String sayHello(Account account) {
        return account.getGivenName() + ". You are allowed to say hello!";
    }


}
