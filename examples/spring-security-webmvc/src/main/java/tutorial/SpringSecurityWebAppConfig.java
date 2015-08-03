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
package tutorial;

import com.stormpath.spring.config.EnableStormpathWebSecurity;
import com.stormpath.spring.config.StormpathWebSecurityConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * @since 1.0.RC4.3
 */
@Configuration
@ComponentScan
@EnableStormpathWebSecurity //Stormpath Spring Security web mvc beans plus out-of-the-box views
@PropertySource("classpath:application.properties")
public class SpringSecurityWebAppConfig extends StormpathWebSecurityConfiguration {

    @Override
    protected void doConfigure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/restricted").fullyAuthenticated();
    }

}
