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

package com.stormpath.spring.security.authz.permission.evaluator;

import com.stormpath.spring.security.authz.permission.Permission;
import com.stormpath.spring.security.authz.permission.WildcardPermission;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;

/**
 * A {@link PermissionEvaluator} that determines if a {@link WildcardPermission} matches a given permission.
 * <h3>Usage</h3>
 * In order to use it you need to configure Spring this way:
 * <pre>
 *      <bean id="permissionEvaluator" class="com.stormpath.spring.security.authz.permission.evaluator.WildcardPermissionEvaluator"/>
 *      <bean id="methodExpressionHandler" class="org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler">
 *          <property name="permissionEvaluator" ref="permissionEvaluator"/>
 *      </bean>
 *      <bean id="webExpressionHandler" class="org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler">
 *          <property name="permissionEvaluator" ref="permissionEvaluator"/>
 *      </bean>
 * </pre>
 * And then you can simply evaluate permissions this way using <a href="http://docs.spring.io/spring-security/site/docs/3.0.x/reference/el-access.html">Method Security Expressions</a>:
 * <pre>
        @PreAuthorize("hasPermission(...)")
 * </pre>
 * or using <a href="http://docs.spring.io/spring-security/site/docs/3.0.x/reference/taglibs.html">JSP taglibs</a>
 * <pre>
 *      <sec:authorize access="hasPermission(...)" />
 * </pre>
 *
 * @since 0.2.0
 */
public class WildcardPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {

        String domainObjectString = "";
        if(targetDomainObject != null) {
            domainObjectString = targetDomainObject + WildcardPermission.PART_DIVIDER_TOKEN;
        }

        //Let's construct a WildcardPermission out of the given parameters
        Permission toMatch = new WildcardPermission( domainObjectString + permission);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for(GrantedAuthority authority : authorities) {
            //This evaluator only compares WildcardPermissions
            if (authority instanceof WildcardPermission) {
                WildcardPermission wp = (WildcardPermission) authority;
                //Let's delegate the actual comparison to the WildcardPermission
                if(wp.implies(toMatch)){
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        String targetIdString = "";
        if(targetIdString != null) {
            targetIdString = WildcardPermission.PART_DIVIDER_TOKEN + targetId;
        }

        return hasPermission(authentication, targetType + targetIdString, permission);
    }

}
