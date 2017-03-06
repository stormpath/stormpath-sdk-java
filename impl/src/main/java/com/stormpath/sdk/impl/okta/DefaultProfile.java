package com.stormpath.sdk.impl.okta;

import com.stormpath.sdk.impl.application.ConfigurableProperty;
import com.stormpath.sdk.impl.resource.AbstractPropertyRetriever;
import com.stormpath.sdk.lang.Objects;
import com.stormpath.sdk.okta.Profile;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class DefaultProfile extends ConfigurableProperty implements Profile {

    private static final String LOGIN = "login";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String MIDDLE_NAME = "middleName";
    private static final String EMAIL = "email";
    private static final String DISPLAY_NAME = "displayName";

    public DefaultProfile(String name, Map<String, Object> properties, AbstractPropertyRetriever parent) {
        super(name, properties, parent);
    }

    @Override
    public String getLogin() {
        return Objects.nullSafeToString(get(LOGIN));
    }

    @Override
    public void setLogin(String login) {
        put(LOGIN, login);
    }

    @Override
    public String getFirstName() {
        return Objects.nullSafeToString(get(FIRST_NAME));
    }

    @Override
    public void setFirstName(String firstName) {
        put(FIRST_NAME, firstName);
    }

    @Override
    public String getLastName() {
        return Objects.nullSafeToString(get(LAST_NAME));
    }

    @Override
    public void setLastName(String lastName) {
        put(LAST_NAME, lastName);
    }

    @Override
    public String getMiddleName() {
        return Objects.nullSafeToString(get(MIDDLE_NAME));
    }

    @Override
    public void setMiddleName(String middleName) {
        put(MIDDLE_NAME, middleName);
    }

    @Override
    public String getEmail() {
        return Objects.nullSafeToString(get(EMAIL));
    }

    @Override
    public void setEmail(String email) {
        put(EMAIL, email);
    }

    @Override
    public String getDisplayName() {
        return Objects.nullSafeToString(get(DISPLAY_NAME));
    }

    @Override
    public void setDisplayName(String displayName) {
        put(DISPLAY_NAME, displayName);
    }



    @Override
    public int size() {
        return properties.size();
    }

    @Override
    public boolean isEmpty() {
        return properties.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return properties.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return properties.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return properties.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return properties.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return properties.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        properties.putAll(m);
    }

    @Override
    public void clear() {
        properties.clear();
    }

    @Override
    public Set<String> keySet() {
        return properties.keySet();
    }

    @Override
    public Collection<Object> values() {
       return properties.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return properties.entrySet();
    }

}
