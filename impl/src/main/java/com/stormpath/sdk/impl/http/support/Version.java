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
package com.stormpath.sdk.impl.http.support;

import java.io.*;

/**
 * @since 1.0.alpha
 */
public class Version {

    private static final String CLIENT_VERSION = lookupClientVersion();

    public static String getClientVersion() {
        return CLIENT_VERSION;
    }

    private static String lookupClientVersion() {
        Class clazz = Version.class;
        String filePath = "/com/stormpath/sdk/version.properties";
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = clazz.getResourceAsStream(filePath);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            do {
                line = reader.readLine();
            } while (line.startsWith("#") || line.isEmpty());
            return line;
        } catch (IOException e) {
            throw new RuntimeException("Unable to obtain version from [" + filePath + "].");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException("Exception while trying to close file [" + filePath + "].");
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException("Exception while trying to close file [" + filePath + "].");
                }
            }
        }
    }
}
