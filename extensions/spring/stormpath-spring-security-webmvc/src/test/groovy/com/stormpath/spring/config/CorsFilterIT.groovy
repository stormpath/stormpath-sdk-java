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
package com.stormpath.spring.config

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.lang.Strings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import javax.servlet.http.HttpServletResponse

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @since 1.2.0
 */
@WebAppConfiguration
@ContextConfiguration(classes = [CorsTestAppConfig.class, TwoAppTenantStormpathTestConfiguration.class])
class CorsFilterIT extends AbstractTestNGSpringContextTests {

    @Autowired
    WebApplicationContext context;

    private MockMvc mvc;

    @Autowired
    Client client;

    @Autowired
    Application application;

    @BeforeMethod
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(StormpathMockMvcConfigurers.stormpath())
                .build();
    }

    @Test
    void testSimpleCORSRequestIsOK() {

        mvc.perform(options(new URI("/me"))
                .header("Origin", "http://localhost:3000")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is(HttpServletResponse.SC_OK)); //200
    }

    @Test
    void testAccessControlRequestMethodIsOKForGET() {

        mvc.perform(options(new URI("/me"))
                    .header("Origin", "http://localhost:3000")
                    .header("Access-Control-Request-Method", "GET")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpServletResponse.SC_OK)); //200
    }

    @Test
    void testAccessControlRequestHeadersIsOKForRememberMe() {
        mvc.perform(options(new URI("/me"))
                    .header("Origin", "http://localhost:3000")
                    .header("Access-Control-Request-Method", "GET")
                    .header("Access-Control-Request-Headers", "remember-me"))
                .andExpect(status().is(HttpServletResponse.SC_OK)); //200
    }

    @Test
    void testAccessControlRequestHeadersIsWrong() {
        mvc.perform(options(new URI("/me"))
                    .header("Origin", "http://localhost:3000")
                    .header("Access-Control-Request-Method", "GET")
                    .header("Access-Control-Request-Headers", "XXXX")  //XXXX is not allowed
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpServletResponse.SC_FORBIDDEN)); //403
    }

    @Test
    void testAccessControlRequestMethodRequestFailsForPUT() {
        mvc.perform(options(new URI("/me"))
                    .header("Origin", "http://localhost:3000")
                    .header("Access-Control-Request-Method", "PUT") //PUT is not allowed
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpServletResponse.SC_FORBIDDEN)); //403

    }

    @Test
    void testCORsFailedDueToDifferentOrigin() {
        mvc.perform(options(new URI("/me"))
                    .header("Origin", "http://localhost:3001")  //this origin is not allowed
                    .header("Access-Control-Request-Method", "GET")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpServletResponse.SC_FORBIDDEN)); //403
    }

    @Test
    void testSameOriginIsJustFine() {
        mvc.perform(options(new URI("/me"))
                .header("Access-Control-Request-Method", "PUT") //this header will be just overlooked since CORS Filter will not care about this request because Origin header is not present
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpServletResponse.SC_OK)); //200
    }

    @Test
    void testLoginWorksViaCORS() {

        String email = "randomEmail" + UUID.randomUUID() + "@testmail.stormpath.com"
        String password = "Changeme1!"
        Account account;

        try {
            //Let's create an account so we can try to login with it
            account = client.instantiate(Account)
            account = account.setGivenName('John')
                    .setSurname('DELETEME')
                    .setEmail(email)
                    .setPassword(password)
            account = application.createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(false).build())

            //Let's login now
            mvc.perform(post(new URI("/login"))
                    .header("Origin", "http://localhost:3000")
                    .accept(MediaType.APPLICATION_JSON)
                    .content("{ \"login\":\"" + email + "\", \"password\":\"" + password + "\" }")
            )
            .andExpect(status().is(HttpServletResponse.SC_OK)) //200
            .andExpect(cookie().exists("access_token"))
            .andExpect(cookie().exists("refresh_token"));
        } catch (Exception e) {
            throw e;
        } finally {
            if (account != null && Strings.hasText(account.getHref())) {
                account.delete();
            }
        }

    }

}
