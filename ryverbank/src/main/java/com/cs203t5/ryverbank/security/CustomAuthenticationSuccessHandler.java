package com.cs203t5.ryverbank.security;

import java.io.IOException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Implementation of the AuthenticationSuccessHandler class.
 * 
 * @see AuthenticationSuccessHandler
 */
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if(roles.contains("USER")){
            //Returns the current HttpSession associated with this request or, if there is no current session and create is true, returns a new session.
            //If create is false and the request has no valid HttpSession, this method returns null.
            request.getSession(false).setMaxInactiveInterval(240);
        }
        else if(roles.contains("MANAGER")){
            request.getSession(false).setMaxInactiveInterval(1800);
        }
        //login success url goes here, currently login success url="/"
        response.sendRedirect(request.getContextPath());
	}
}
