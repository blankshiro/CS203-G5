package com.cs203t5.ryverbank.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userSvc) {
        this.userDetailsService = userSvc;
    }

    /**
     * Attach the user details and password encoder.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        .httpBasic()
        .and()
        .authorizeRequests()
            .antMatchers(HttpMethod.POST, "/customers/{id}").authenticated()
            .antMatchers(HttpMethod.GET, "/customers/{id}").authenticated()
            .antMatchers(HttpMethod.PUT, "/customers/{id}").authenticated()
            .antMatchers(HttpMethod.DELETE, "/customers/{id}").authenticated()
        
            .antMatchers(HttpMethod.GET, "/accounts").hasAnyRole("ROLE_USER", "ROLE_MANAGER")
            .antMatchers(HttpMethod.GET, "/accounts/{accounts_id}/transactions").hasAnyRole("ROLE_USER","ROLE_MANAGER")
            .antMatchers(HttpMethod.GET, "/accounts/{accounts_id}").hasRole("ROLE_USER")
            .antMatchers(HttpMethod.GET, "/accounts/{accounts_id}/transactions").hasRole("ROLE_USER")
        .and()
        .csrf().disable() // CSRF protection is needed only for browser based attacks
        .formLogin().successHandler(new CustomAuthenticationSuccessHandler()) //creates session after successful login
        .failureUrl("/login?error=true").
            and()
        .headers().disable(); // Disable the security headers, as we do not return HTML in our service
        //allow max 1 session , direct to expired url
        http.sessionManagement().maximumSessions(1).expiredUrl("/login?expired=true"); 
    }

    /**
     * @Bean annotation is used to declare a PasswordEncoder bean in the Spring
     *       application context. Any calls to encoder() will then be intercepted to
     *       return the bean instance.
     */
    @Bean
    public BCryptPasswordEncoder encoder() {
        // auto-generate a random salt internally
        return new BCryptPasswordEncoder();
    }
}
