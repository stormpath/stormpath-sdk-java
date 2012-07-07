/*
 * Copyright 2012 Stormpath, Inc.
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

import java.io.IOException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * @since 0.1
 */
public class Version {

    private static final String CLIENT_VERSION = lookupClientVersion();

    public static String getClientVersion() {
        return CLIENT_VERSION;
    }

    private static String lookupClientVersion() {
        Class clazz = Version.class;
        String className = clazz.getSimpleName() + ".class";
        String classPath = clazz.getResource(className).toString();
        if (!classPath.startsWith("jar")) {
            // Class not from JAR
            return "NOT-FROM-JAR";
        }
        String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
        Manifest manifest = getManifest(manifestPath);
        Attributes attr = manifest.getMainAttributes();
        String value = attr.getValue("Implementation-Version");
        if (value == null) {
            throw new IllegalStateException("Unable to obtain 'Implementation-Version' property from manifest.");
        }
        return value;
    }

    private static Manifest getManifest(String path) {
        try {
            return new Manifest(new URL(path).openStream());
        } catch (IOException e) {
            throw new RuntimeException("Unable to obtain version from manifest path [" + path + "]");
        }
    }
}
