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
package com.stormpath.sdk.impl.config;

import com.stormpath.sdk.impl.io.Resource;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class DefaultPropertiesParser implements PropertiesParser {

    private static final Logger log = LoggerFactory.getLogger(DefaultPropertiesParser.class);

    public static final String DEFAULT_CHARSET_NAME = "UTF-8";
    public static final String COMMENT_POUND     = "#";
    public static final String COMMENT_SEMICOLON = ";";
    protected static final char ESCAPE_TOKEN = '\\';

    @Override
    public Map<String, String> parse(String source) {
        Scanner scanner = new Scanner(source);
        return parse(scanner);
    }

    @Override
    public Map<String, String> parse(Resource resource) throws IOException {
        InputStream is = resource.getInputStream();
        Scanner scanner = new Scanner(is, DEFAULT_CHARSET_NAME);
        return parse(scanner);
    }

    /**
     * Loads the .properties-formatted text backed by the given Scanner.  This implementation will close the
     * scanner after it has finished loading.
     *
     * @param scanner the {@code Scanner} from which to read the .properties-formatted text
     */
    public Map<String, String> parse(Scanner scanner) {

        Assert.notNull(scanner, "Scanner argument cannot be null.");

        Map<String, String> props = new LinkedHashMap<String, String>();

        StringBuilder lineBuffer = new StringBuilder();

        while (scanner.hasNextLine()) {

            String rawLine = scanner.nextLine();
            String line = Strings.clean(rawLine);

            if (line == null || line.startsWith(COMMENT_POUND) || line.startsWith(COMMENT_SEMICOLON)) {
                //skip empty lines and comments:
                continue;
            }

            if (isContinued(line)) {
                //strip off the last continuation backslash:
                line = line.substring(0, line.length() - 1);
                lineBuffer.append(line);
                continue;
            } else {
                lineBuffer.append(line);
            }

            line = lineBuffer.toString();
            lineBuffer = new StringBuilder();

            String[] kvPair = splitKeyValue(line);

            props.put(kvPair[0], kvPair[1]);
        }

        return props;
    }

    protected static boolean isContinued(String line) {
        if (!Strings.hasText(line)) {
            return false;
        }
        int length = line.length();
        //find the number of backslashes at the end of the line.  If an even number, the
        //backslashes are considered escaped.  If an odd number, the line is considered continued on the next line
        int backslashCount = 0;
        for (int i = length - 1; i > 0; i--) {
            if (line.charAt(i) == ESCAPE_TOKEN) {
                backslashCount++;
            } else {
                break;
            }
        }
        return backslashCount % 2 != 0;
    }

    private static boolean isKeyValueSeparatorChar(char c) {
        return Character.isWhitespace(c) || c == ':' || c == '=';
    }

    private static boolean isCharEscaped(CharSequence s, int index) {
        return index > 0 && s.charAt(index - 1) == ESCAPE_TOKEN;
    }

    //Protected to access in a test case - NOT considered part of Shiro's public API
    protected static String[] splitKeyValue(String keyValueLine) {
        String line = Strings.clean(keyValueLine);
        if (line == null) {
            return null;
        }
        StringBuilder keyBuffer = new StringBuilder();
        StringBuilder valueBuffer = new StringBuilder();

        boolean buildingKey = true; //we'll build the value next:

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (buildingKey) {
                if (isKeyValueSeparatorChar(c) && !isCharEscaped(line, i)) {
                    buildingKey = false;//now start building the value
                } else {
                    keyBuffer.append(c);
                }
            } else {
                if (valueBuffer.length() == 0 && isKeyValueSeparatorChar(c) && !isCharEscaped(line, i)) {
                    //swallow the separator chars before we start building the value
                } else {
                    valueBuffer.append(c);
                }
            }
        }

        String key = Strings.clean(keyBuffer.toString());
        String value = Strings.clean(valueBuffer.toString());

        if (key == null) {
            String msg = "Line argument must contain a key. None was found.";
            throw new IllegalArgumentException(msg);
        }

        log.trace("Discovered key/value pair: {} = {}", key, value);

        return new String[]{key, value};
    }
}
