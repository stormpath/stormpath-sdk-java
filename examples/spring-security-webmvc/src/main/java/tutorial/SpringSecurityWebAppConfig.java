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

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.spring.config.EnableStormpath;
import com.stormpath.spring.config.EnableStormpathSpringSecurityWebMvc;
import com.stormpath.spring.config.StormpathConfiguration;
import com.stormpath.spring.config.StormpathWebMvcConfiguration;
import com.stormpath.spring.security.provider.StormpathAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.web.access.expression.WebExpressionVoter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableStormpathSpringSecurityWebMvc //Stormpath Spring Security web mvc beans plus out-of-the-box views
@ComponentScan
@PropertySource("classpath:application.properties")
public class SpringSecurityWebAppConfig extends WebSecurityConfigurerAdapter {

    //Let's specify some role here so we can later grant it access to restricted resources
    final String roleA = "https://api.stormpath.com/v1/groups/46O9eBTN4oLtjJMSWLxnwJ";

    @Autowired
    private StormpathAuthenticationProvider stormpathAuthenticationProvider;

    @Autowired
    private StormpathWebMvcConfiguration stormpathWebMvcConfiguration;

    /**
     * This bean and the @PropertySource annotation above allow you to configure Stormpath beans with properties
     * prefixed with {@code stormpath.}, i.e. {@code stormpath.application.href}, {@code stormpath.apiKey.file}, etc.
     *
     * <p>Combine this with Spring's Profile support to override property values based on specific runtime environments,
     * e.g. development, production, etc.</p>
     *
     * @return the application's PropertySourcesPlaceholderConfigurer that enables property placeholder substitution.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    //The access control settings are defined here
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                    //.accessDecisionManager(accessDecisionManager())
                    .antMatchers("/restricted").hasAuthority(roleA)
//                    .antMatchers(stormpathWebMvcConfiguration.stormpathInternalConfig().getRegisterUrl()).permitAll()
//                    .antMatchers(stormpathWebMvcConfiguration.stormpathInternalConfig().getForgotPasswordUrl()).permitAll()
//                    .antMatchers(stormpathWebMvcConfiguration.stormpathInternalConfig().getChangePasswordUrl()).permitAll()
                      //.antMatchers("/WEB-INF/jsp/stormpath/*").permitAll()
                      //.antMatchers("/assets/css/*").permitAll()
                    .and()
                .formLogin()
                    .loginPage(stormpathWebMvcConfiguration.stormpathInternalConfig().getLoginUrl())
                    .defaultSuccessUrl(stormpathWebMvcConfiguration.stormpathInternalConfig().getLoginNextUrl())
                    .usernameParameter("login")
                    .passwordParameter("password")
                    .and()
                .logout()
                    .logoutUrl(stormpathWebMvcConfiguration.stormpathInternalConfig().getLogoutUrl())
                .logoutSuccessUrl(stormpathWebMvcConfiguration.stormpathInternalConfig().getLogoutNextUrl())
                    .and()
                .httpBasic()
                    .and()
                    .csrf().disable();
    }

//    @Override
//    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(this.stormpathAuthenticationProvider)
//                .parentAuthenticationManager(this.authenticationManagerBean())
//                .
//
//        formLogin().loginPage("/login").usernameParameter("login").passwordParameter("password")
//                .and().authorizeRequests().accessDecisionManager(accessDecisionManager())
//                //.antMatchers("/WEB-INF/jsp/stormpath/*").permitAll()
//                //.antMatchers("/assets/css/*").permitAll()
//                .antMatchers("/restricted/*").hasAuthority(roleA)
//                .and()
//                .logout()
//                .logoutUrl("/logout")
//                .logoutSuccessUrl("/")
//                .and()
//                .httpBasic()
//                .and()
//                .csrf().disable();
//    }


    @Bean
    public AuthenticationManager getAuthenticationManager() throws Exception {
        return this.authenticationManagerBean();
    }

    //Let's add the StormpathAuthenticationProvider to the AuthenticationManager
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(stormpathAuthenticationProvider);
    }

//    //Prevents the addition of the "ROLE_" prefix in authorities
//    @Bean
//    public WebExpressionVoter webExpressionVoter() {
//        WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
//        return webExpressionVoter;
//    }

//    @Bean
//    public AffirmativeBased accessDecisionManager() {
//        List decisionVoters = Arrays.asList(webExpressionVoter());
//        AffirmativeBased affirmativeBased = new AffirmativeBased(decisionVoters);
//        affirmativeBased.setAllowIfAllAbstainDecisions(false);
//        return affirmativeBased;
//    }

//    @Bean
//    public String getApplicationRestUrl() {
//        return this.applicationRestUrl;
//    }

}
