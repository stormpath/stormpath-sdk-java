/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.spring.security.provider;

import com.stormpath.spring.security.authz.permission.Permission;

/**
 * A {@code PermisisonResolver} resolves a String value and converts it into a
 * {@link com.stormpath.spring.security.authz.permission.Permission Permission} instance.
 * <p/>
 * The default {@link WildcardPermissionResolver} should be
 * suitable for most purposes, which constructs {@link com.stormpath.spring.security.authz.permission.WildcardPermission} objects.
 * However, any resolver may be configured if an application wishes to use different
 * {@link com.stormpath.spring.security.authz.permission.Permission} implementations.
 * <p/>
 * We suggest to use {@link com.stormpath.spring.security.authz.permission.WildcardPermission WildcardPermission}s.
 * One of the nice things about {@code WildcardPermission}s is that it makes it very easy to
 * store complex permissions in the database - and also makes it very easy to represent permissions in JSP files,
 * annotations, etc., where a simple string representation is useful.
 * <p/>
 * You are of course free to provide custom String-to-Permission conversion by providing Spring Security components any instance
 * of this interface.
 *
 * @since 0.2.0
 */
public interface PermissionResolver {

    /**
     * Resolves a Permission based on the given String representation.
     *
     * @param permissionString the String representation of a permission.
     * @return A Permission object that can be used internally to determine a subject's permissions.
     * @throws InvalidPermissionStringException
     *          if the permission string is not valid for this resolver.
     */
    Permission resolvePermission(String permissionString);

}
