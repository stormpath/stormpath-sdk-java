package com.stormpath.spring.security.provider;

import com.stormpath.sdk.account.Account;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collections;
import java.util.Set;

/**
 * An {@link AccountGrantedAuthorityResolver} that always returns an empty set of granted authorities.  Useful
 * in testing or default configuration scenarios.
 * 
 * @since 1.0.RC6
 */
public class EmptyAccountGrantedAuthorityResolver implements AccountGrantedAuthorityResolver {

    @Override
    public Set<GrantedAuthority> resolveGrantedAuthorities(Account account) {
        return Collections.emptySet();
    }
}
