package com.stormpath.sdk.servlet.utils

import java.lang.reflect.Field

/**
 * @since 1.0.4
 */
class ConfigTestUtils {

    // From http://stackoverflow.com/a/496849
    static void setEnv(Map<String, String> newenv) throws Exception {
        Class[] classes = Collections.class.getDeclaredClasses()
        Map<String, String> env = System.getenv()
        for (Class cl : classes) {
            if ('java.util.Collections$UnmodifiableMap'.equals(cl.getName())) {
                Field field = cl.getDeclaredField('m')
                field.setAccessible(true)
                Object obj = field.get(env)
                Map<String, String> map = (Map<String, String>) obj
                map.clear()
                map.putAll(newenv)
            }
        }
    }
}
