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

package com.stormpath.spring.security.provider;

import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.lang.Assert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;

/**
 * Default implementation of the {@code GroupGrantedAuthorityResolver} interface that allows a Stormpath
 * {@link com.stormpath.sdk.group.Group} to be translated into Spring Security granted authorities based on
 * custom preferences.
 * <h2>Overview</h2>
 * This implementation converts a Group into one or more granted authorities based on one or more configured
 * {@link com.stormpath.spring.security.provider.DefaultGroupGrantedAuthorityResolver.Mode Mode}s:
 * <table>
 *     <thead>
 *         <tr>
 *             <th>Mode (case insensitive)</th>
 *             <th>Behavior</th>
 *             <th>Example</th>
 *         </tr>
 *     </thead>
 *     <tbody>
 *         <tr>
 *             <td>HREF</td>
 *             <td>Returns the Group's fully qualified HREF as the granted authority</td>
 *             <td>{@code https://api.stormpath.com/v1/groups/upXiExAmPlEfA5L1G5ZaSQ}</td>
 *         </tr>
 *         <tr>
 *             <td>ID</td>
 *             <td>Returns Group's globally unique identifier as the granted authority</td>
 *             <td>{@code upXiExAmPlEfA5L1G5ZaSQ}</td>
 *         </tr>
 *         <tr>
 *             <td>NAME</td>
 *             <td>Returns Group's name as the granted authority</td>
 *             <td>{@code administrators}</td>
 *         </tr>
 *     </tbody>
 * </table>
 * <h2>Usage</h2>
 * You can choose one or more modes either by referencing the {@link com.stormpath.spring.security.provider.DefaultGroupGrantedAuthorityResolver.Mode Mode} enum values directly, or by using
 * the mode string names.
 * <p/>
 * For example, in code:
 * <pre>
 * Set&lt;DefaultGroupGrantedAuthorityResolver.Mode&gt; modes = Collections.toSet(
 *     DefaultGroupGrantedAuthorityResolver.Mode.HREF, DefaultGroupGrantedAuthorityResolver.Mode.ID
 * );
 * groupRoleResolver.setModes(modes);
 * </pre>
 * <p/>
 * Or maybe in spring.xml:
 * <pre>
 * <beans:bean id="groupGrantedAuthorityResolver" class="com.stormpath.spring.security.provider.DefaultGroupGrantedAuthorityResolver" >
 *      <beans:property name="modeNames" >
 *          <beans:set>
 *              <beans:value>href</beans:value>
 *              <beans:value>id</beans:value>
 *          </beans:set>
 *      </beans:property>
 * </beans:bean>
 * </pre>
 * In the above configuration, each Group translates into two granted authorities: one is the raw href itself,
 * the other one is the Group ID.  You can specify one or more modes to translate into one or more granted authorities
 * names respectively.
 * <i>modeNames are case-insensitive</i>.
 * <p/>
 * <b>WARNING:</b> Group Names, while easier to read in code, can change at any time via a REST API call or by using
 * the Stormpath UI Console.  It is <em>strongly</em> recommended to use only the HREF or ID modes as these values
 * will never change.  Acquiring group names might also incur a REST server call, whereas the the HREF is guaranteed
 * to be present.
 *
 */
public class DefaultGroupGrantedAuthorityResolver implements GroupGrantedAuthorityResolver {

    private Set<Mode> modes;

    public DefaultGroupGrantedAuthorityResolver() {
        this.modes = new HashSet<Mode>();
        this.modes.add(Mode.HREF);
    }

    public Set<Mode> getModes() {
        return modes;
    }

    public void setModes(Set<Mode> modes) {
        if (modes == null || modes.isEmpty()) {
            throw new IllegalArgumentException("modes property cannot be null or empty.");
        }
        this.modes = modes;
    }

    @Override
    public Set<GrantedAuthority> resolveGrantedAuthorities(Group group) {

        Set<GrantedAuthority> set = new HashSet<GrantedAuthority>();

        Set<Mode> modes = getModes();

        String groupHref = group.getHref();

        //REST resource hrefs should never ever be null:
        if (groupHref == null) {
            throw new IllegalStateException("Group does not have an href property.  This should never happen.");
        }

        if (modes.contains(Mode.HREF)) {
            set.add(new SimpleGrantedAuthority(groupHref));
        }
        if (modes.contains(Mode.ID)) {
            String instanceId = getInstanceId(groupHref);
            if (instanceId != null) {
                set.add(new SimpleGrantedAuthority(instanceId));
            }
        }
        if (modes.contains(Mode.NAME)) {
            String name = group.getName();
            if (name != null) {
                set.add(new SimpleGrantedAuthority(name));
            }
        }

        return set;
    }

    private String getInstanceId(String href) {
        int i = href.lastIndexOf('/');
        if (i >= 0) {
            return href.substring(i + 1);
        }
        return null;
    }

    public enum Mode {

        HREF,
        ID,
        NAME;

        public static Mode fromString(final String name) {
            Assert.notNull(name);

            String upper = name.toUpperCase();
            for (Mode mode : values()) {
                if (mode.name().equals(upper)) {
                    return mode;
                }
            }
            throw new IllegalArgumentException("There is no Mode name '" + name + "'");
        }
    }
}
