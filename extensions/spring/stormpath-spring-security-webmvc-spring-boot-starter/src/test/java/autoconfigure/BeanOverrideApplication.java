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
package autoconfigure;

import com.stormpath.spring.boot.autoconfigure.CustomTestGroupPermissionResolver;
import com.stormpath.spring.security.provider.GroupPermissionResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @since 1.0.RC4.6
 */
@Configuration
@EnableAutoConfiguration
public class BeanOverrideApplication {

    private static final Logger log = LoggerFactory.getLogger(BeanOverrideApplication.class);

    @Bean
    public GroupPermissionResolver stormpathGroupPermissionResolver() {
        //Let's try that the Bean definition order in AbstractStormpathSpringSecurityConfiguration#stormpathAuthenticationProvider actually works
        return new CustomTestGroupPermissionResolver();
    }

    public static void main(String[] args) {
        SpringApplication.run(StormpathSpringSecurityWebMvcAutoConfigurationApplication.class, args);
    }

}
