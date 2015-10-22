package com.stormpath.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

public class StormpathWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    protected StormpathWebSecurityConfigurer stormpathWebSecurityConfigurer;

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void configure(HttpSecurity http) throws Exception {
        stormpathWebSecurityConfigurer.configure(http);
        doConfigure(http);
    }

    protected void doConfigure(HttpSecurity http) throws Exception {

    }

    @Override
    protected final void configure(AuthenticationManagerBuilder auth) throws Exception {
        stormpathWebSecurityConfigurer.configure(auth);
        doConfigure(auth);
    }

    protected void doConfigure(AuthenticationManagerBuilder auth) throws Exception {

    }

    @Override
    public final void configure(WebSecurity web) throws Exception {
        stormpathWebSecurityConfigurer.configure(web);
        doConfigure(web);
    }

    protected void doConfigure(WebSecurity web) throws Exception {

    }

}
