package com.stormpath.sdk.impl.lang;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;

public class DefaultEnvironmentVariableNameFactory implements EnvironmentVariableNameFactory {

    @Override
    public String createEnvironmentVariableName(String name) {
        Assert.hasText(name, "Name argument cannot be null or empty.");
        name = Strings.trimWhitespace(name);

        StringBuilder sb = new StringBuilder();

        for(char c : name.toCharArray()) {
            if (c == '.') {
                sb.append('_');
                continue;
            }
            if (Character.isUpperCase(c)) {
                sb.append('_');
            }
            sb.append(Character.toUpperCase(c));
        }

        return sb.toString();
    }
}
