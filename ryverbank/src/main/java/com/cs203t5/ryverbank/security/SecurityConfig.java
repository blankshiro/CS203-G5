package com.cs203t5.ryverbank.security;

import java.text.Normalizer.Form;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;

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

    /*  
    * Note: '*' matches zero or more characters, e.g., /customers/* matches /customers/20
            '**' matches zero or more 'directories' in a path, e.g., /accounts/** matches /accounts/1/transactions
    */

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        .httpBasic()
        .and()
        .authorizeRequests()
            //create a customer profile
            .antMatchers(HttpMethod.POST, "/customers").hasRole("MANAGER")
            //Get all customers
            .antMatchers(HttpMethod.GET, "/customers").hasRole("MANAGER")
            //Get specific customer profile
            .antMatchers(HttpMethod.GET, "/customers/*").hasAnyRole("USER","MANAGER")

            //update specific customer profile
            .antMatchers(HttpMethod.PUT, "/customers/*").hasAnyRole("USER","MANAGER")
            // .antMatchers(HttpMethod.DELETE, "/customers/*").authenticated()
        
            .antMatchers(HttpMethod.GET, "/accounts").hasRole("USER")
            .antMatchers(HttpMethod.GET, "/accounts/*").hasRole("USER")
            .antMatchers(HttpMethod.POST, "/accounts").hasRole("MANAGER")
            .antMatchers(HttpMethod.GET, "/accounts/*/transactions").hasRole("USER")
            .antMatchers(HttpMethod.POST, "/accounts/*").hasRole("USER")
            
            //Following lines are for content
            //Everyone that wants to access the content page needs to be authenticated
            .antMatchers(HttpMethod.GET, "/contents").authenticated()

            //Only managers and analysts can post into this URL
            .antMatchers(HttpMethod.POST, "/contents").hasAnyRole("ANALYST","MANAGER")

            //Only managers and analysts can perform C.R.U.D into this URL
            .antMatchers(HttpMethod.PUT, "/contents").hasAnyRole("ANALYST","MANAGER")
            .antMatchers(HttpMethod.DELETE, "/contents/*").hasAnyRole("ANALYST","MANAGER")

            //only users get to see their portfolio
            .antMatchers(HttpMethod.GET, "/portfolio").hasRole("USER")
            .antMatchers(HttpMethod.GET, "/portfolio").authenticated()
            
        .and()
        .logout()
        .logoutUrl("/logout")
        //This is to allow the session to be set up so that it is not invalidated when a logout occurs
        // Note to self: Find a way to find the session ID if necessary
        // .invalidateHttpSession(true)
        // .deleteCookies(RequestContextHolder.currentRequestAttributes().getSessionId())

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
