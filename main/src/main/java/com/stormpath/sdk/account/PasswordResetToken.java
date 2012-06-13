package com.stormpath.sdk.account;

import com.stormpath.sdk.resource.Resource;

/**
 * @author Jeff Wysong
 *         Date: 6/13/12
 *         Time: 2:22 PM
 * @since 0.2
 */
public interface PasswordResetToken extends Resource {

    String getEmail();

    void setEmail(String email);

    Account getAccount();

}
