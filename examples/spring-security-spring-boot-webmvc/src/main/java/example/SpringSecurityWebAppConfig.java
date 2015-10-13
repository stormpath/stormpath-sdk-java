/*
 * Copyright 2015 Stormpath, Inc.
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
package example;

import com.stormpath.spring.boot.autoconfigure.StormpathWebSecurityConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @since 1.0.RC5
 */
@Configuration
@EnableWebSecurity
//@Order(1)
//public class SpringSecurityWebAppConfig extends StormpathWebSecurityConfigurerAdapter {
public class SpringSecurityWebAppConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private StormpathWebSecurityConfigurer stormpathWebSecurityConfigurer;

//    public void init(final WebSecurity web) throws Exception {
//        stormpathWebSecurityConfigurer.init(web);
//        super.init(web);
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //WIP HERE
        http.apply(stormpathWebSecurityConfigurer);
        http
                .authorizeRequests()
                .antMatchers("/restricted").fullyAuthenticated();
    }

    @Bean
    public AuthenticationManager getAuthenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }

}
