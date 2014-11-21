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
package com.stormpath.sdk.servlet.http.impl;

import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.impl.ds.DefaultResourceFactory;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.servlet.Servlets;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;

@SuppressWarnings("deprecation")
public class StormpathHttpSession implements HttpSession {

    private final HttpSession session;

    public StormpathHttpSession(HttpSession session) {
        Assert.notNull(session, "Session argument cannot be null.");
        this.session = session;
    }

    protected Client getClient() {
        return Servlets.getClient(getServletContext());
    }

    @Override
    public long getCreationTime() {
        return session.getCreationTime();
    }

    @Override
    public String getId() {
        return session.getId();
    }

    @Override
    public long getLastAccessedTime() {
        return session.getLastAccessedTime();
    }

    @Override
    public ServletContext getServletContext() {
        return session.getServletContext();
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        session.setMaxInactiveInterval(interval);
    }

    @Override
    public int getMaxInactiveInterval() {
        return session.getMaxInactiveInterval();
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return session.getSessionContext();
    }

    @Override
    public Object getAttribute(String name) {

        //this logic complements the setAttribute implementation:

        Object o = session.getAttribute(name);
        if (o instanceof ResourceReference) {
            ResourceReference ref = (ResourceReference) o;
            String href = ref.getHref();
            Class clazz = Classes.forName(ref.getResourceClassName());
            Client client = getClient();
            o = client.getResource(href, clazz);
        }
        return o;
    }

    @Override
    public Object getValue(String name) {

        //this logic complements the putValue implementation:

        Object o = session.getValue(name);
        if (o instanceof ResourceReference) {
            ResourceReference ref = (ResourceReference) o;
            String href = ref.getHref();
            Class clazz = Classes.forName(ref.getResourceClassName());
            Client client = getClient();
            o = client.getResource(href, clazz);
        }
        return o;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return session.getAttributeNames();
    }

    @Override
    public String[] getValueNames() {
        return session.getValueNames();
    }

    @Override
    public void setAttribute(String name, Object value) {

        //we intercept this implementation to ensure that the session stores an efficient (small) storage representation
        //of the resource.  This ensures that the session can be serialized and de-serialized with Resource references
        //even though Resource instances are not serializable themselves.  Especially necessary if sessions are
        //clustered.

        if (value instanceof Resource) {
            Resource resource = (Resource) value;
            Class ifaceClass = DefaultResourceFactory.getInterfaceClass(resource.getClass());
            value = new ResourceReference(ifaceClass.getName(), resource.getHref());
        }
        session.setAttribute(name, value);
    }

    @Override
    public void putValue(String name, Object value) {

        //we intercept this implementation to ensure that the session stores an efficient (small) storage representation
        //of the resource.  This ensures that the session can be serialized and de-serialized with Resource references
        //even though Resource instances are not serializable themselves.  Especially necessary if sessions are
        //clustered.

        if (value instanceof Resource) {
            Resource resource = (Resource) value;
            Class ifaceClass = DefaultResourceFactory.getInterfaceClass(resource.getClass());
            value = new ResourceReference(ifaceClass.getName(), resource.getHref());
        }
        session.putValue(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        session.removeAttribute(name);
    }

    @Override
    public void removeValue(String name) {
        session.removeValue(name);
    }

    @Override
    public void invalidate() {
        session.invalidate();
    }

    @Override
    public boolean isNew() {
        return session.isNew();
    }
}
