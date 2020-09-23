package com.cs203t5.ryverbank.entity.Security;


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
public class SecurityConfig extends WebSecurityConfigurerAdapter{
    private UserDetailsService userDetailsServicer;

    public SecurityConfig(UserDetailsService userDetailsServicer){
        this.userDetailsServicer = userDetailsServicer;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServicer).passwordEncoder(encoder());
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        .httpBasic()
        .and()
        .authorizeRequests()
            .antMatchers(HttpMethod.POST, "/users/{userID}/transactions").authenticated()
            .antMatchers(HttpMethod.GET, "/users/{userId}/transactions").authenticated()
            .antMatchers(HttpMethod.PUT, "/users/{userId}/transactions/{transactionId}").authenticated()
            .antMatchers(HttpMethod.DELETE, "/users/{userID}/transactions/{transactionId}").authenticated()

            .antMatchers(HttpMethod.POST, "/users/{userID}/transactions").hasRole("CLIENT")
            .antMatchers(HttpMethod.GET, "/users/{userId}/transactions").hasAnyRole("STAFF","CLIENT")
            .antMatchers(HttpMethod.PUT, "/users/{userId}/transactions/{transactionId}").hasRole("CLIENT")
            .antMatchers(HttpMethod.DELETE, "/users/{userID}/transactions/{transactionId}").hasRole("CLIENT")
        .and()
        .csrf().disable() // CSRF protection is needed only for browser based attacks
        .formLogin().disable()
        .headers().disable(); // Disable the security headers, as we do not return HTML in our service
    }


    @Bean
    //autogenerate a random salt internally
    public BCryptPasswordEncoder encoder(){
        return new  BCryptPasswordEncoder();
    }
}
