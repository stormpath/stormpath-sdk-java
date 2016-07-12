/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.servlet.config.impl

import com.stormpath.sdk.lang.Classes
import com.stormpath.sdk.servlet.config.Config
import com.stormpath.sdk.servlet.config.ConfigLoader
import org.springframework.mock.web.MockServletContext
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import org.yaml.snakeyaml.Yaml

import java.lang.reflect.Field

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertFalse
import static org.testng.Assert.assertTrue
import static org.testng.Assert.assertNotNull


/**
 * @since 1.0.RC9
 */
class DefaultConfigFactoryTest {

    MockServletContext mockServletContext
    Config config

    @BeforeMethod
    void setup() {
        mockServletContext = new MockServletContext()
        config = new ConfigLoader().createConfig(mockServletContext)
    }

    @Test
    public void testReadFromDefaultProperties() {
        assertEquals config.get('stormpath.web.login.uri'), '/login'
    }

    @Test
    public void testStormPathPropertiesInClasspathOverridesDefault() {
        assertEquals config.get('stormpath.web.login.nextUri'), '/foo'
    }

    @Test
    public void testStormPathYAMLInClasspathOverridesDefault() {
        assertEquals config.get('stormpath.web.logout.uri'), '/getout'
    }

    @Test
    public void testStormPathJSONInClasspathOverridesDefault() {
        assertEquals config.get('stormpath.web.verify.nextUri'), '/home?status=fabulous'
    }

    @Test
    public void testBaseUrlFromEnvironmentVariable() {
        assertEquals config.get('stormpath.baseUrl'), 'http://api.stormpath.com/v100'
        Map<String, String> env = new HashMap<>()
        def baseUrl = 'http://env.stormpath.com/v2'
        env.put('STORMPATH_BASEURL', baseUrl)
        setEnv(env)
        config = new ConfigLoader().createConfig(new MockServletContext())
        assertEquals config.get('stormpath.baseUrl'), baseUrl
    }

    @Test
    public void assertYAMLDependencyIsPresent() {
        assertNotNull(Classes.forName(Yaml.class.getName()))
    }

    @Test
    public void verifyAllEndpointsCanBeDisabled() {
        assertFalse config.isOAuthEnabled()
        assertFalse config.getRegisterControllerConfig().isEnabled()
        assertFalse config.getSendVerificationEmailControllerConfig().isEnabled()
        assertFalse config.getLoginControllerConfig().isEnabled()
        assertFalse config.getLogoutControllerConfig().isEnabled()
        assertFalse config.getForgotPasswordControllerConfig().isEnabled()
        assertFalse config.getChangePasswordControllerConfig().isEnabled()
        assertFalse config.isIdSiteEnabled()
        assertFalse config.isCallbackEnabled()
        assertFalse config.isMeEnabled()
    }

    @Test
    public void verifyAllEndpointsCanBeEnabled() {
        // Enable all endpoints with environment overrides since stormpath.properties has false for all
        Map<String, String> env = new HashMap<>()
        env.put('STORMPATH_WEB_OAUTH2_ENABLED', "true")
        env.put('STORMPATH_WEB_REGISTER_ENABLED', "true")
        env.put('STORMPATH_WEB_VERIFYEMAIL_ENABLED', "true")
        env.put('STORMPATH_WEB_LOGIN_ENABLED', "true")
        env.put('STORMPATH_WEB_LOGOUT_ENABLED', "true")
        env.put('STORMPATH_WEB_FORGOTPASSWORD_ENABLED', "true")
        env.put('STORMPATH_WEB_CHANGEPASSWORD_ENABLED', "true")
        env.put('STORMPATH_WEB_IDSITE_ENABLED', "true")
        env.put('STORMPATH_WEB_CALLBACK_ENABLED', "true")
        env.put('STORMPATH_WEB_ME_ENABLED', "true")
        setEnv(env)
        config = new ConfigLoader().createConfig(new MockServletContext())

        assertTrue config.isOAuthEnabled()
        assertTrue config.getRegisterControllerConfig().isEnabled()
        assertTrue config.getVerifyControllerConfig().isEnabled()
        assertTrue config.getLoginControllerConfig().isEnabled()
        assertTrue config.getLogoutControllerConfig().isEnabled()
        assertTrue config.getForgotPasswordControllerConfig().isEnabled()
        assertTrue config.getChangePasswordControllerConfig().isEnabled()
        assertTrue config.isIdSiteEnabled()
        assertTrue config.isCallbackEnabled()
        assertTrue config.isMeEnabled()
    }

    // From http://stackoverflow.com/a/496849
    private static void setEnv(Map<String, String> newenv) throws Exception {
        Class[] classes = Collections.class.getDeclaredClasses();
        Map<String, String> env = System.getenv();
        for (Class cl : classes) {
            if ('java.util.Collections$UnmodifiableMap'.equals(cl.getName())) {
                Field field = cl.getDeclaredField('m');
                field.setAccessible(true);
                Object obj = field.get(env);
                Map<String, String> map = (Map<String, String>) obj;
                map.clear();
                map.putAll(newenv);
            }
        }
    }
}
