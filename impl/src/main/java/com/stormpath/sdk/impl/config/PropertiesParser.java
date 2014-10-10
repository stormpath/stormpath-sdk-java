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

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

/**
 * Parses a {@code .properties} source (string, file, stream, etc) and returns a <code>Map&lt;String,String&gt;</code>
 * that reflects the parsed properties.
 * <p>This implementation differs from default Java property loading behavior in that the returned Map's iteration
 * order is guaranteed to be the same as the order in which the properties are read.  This can be beneficial if it is
 * desired to assign meaning to property definition order.</p>
 */
public interface PropertiesParser {

    /**
     * Parses the specified {@code .properties}-formatted String and returns a map of the parsed properties or an
     * empty map if no properties were found.
     *
     * @param source the String to parse
     * @return a map of the parsed properties or an empty map if no properties were found.
     */
    Map<String, String> parse(String source);

    /**
     * Parses the specified {@code .properties} resource and returns a map of the parsed properties or an
     * empty map if no properties were found.
     *
     * @param resource the resource to parse.
     * @return a map of the parsed properties or an empty map if no properties were found.
     * @throws IOException if unable to obtain the resource's {@link com.stormpath.sdk.impl.io.Resource#getInputStream()
     *                     inputStream}.
     */
    Map<String, String> parse(Resource resource) throws IOException;

    /**
     * Parses the scanned content according to {@code .properties} formatting rules and returns a map of the parsed
     * properties or an empty map if no properties were found.
     *
     * @param source the Scanner to use to parse the content
     * @return a map of the parsed properties or an empty map if no properties were found.
     */
    Map<String, String> parse(Scanner source);
}
