package com.mcit.AdmissionSystem.web.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class AuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(AuthSuccessHandler.class);

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        log.info("Checking authority for user: " + authentication.getPrincipal().toString());
        authentication.getAuthorities().stream().forEach(a -> {
            try {
                if (a.getAuthority().compareTo("ROLE_ADMIN") == 0) {
                    log.info("User has role ROLE_ADMIN - redirecting to: /admin");
                    this.getRedirectStrategy().sendRedirect(request, response, "/admin");
                } else if (a.getAuthority().compareTo("ROLE_PROFESSOR") == 0) {
                    log.info("User has role ROLE_PROFESSOR - redirecting to: /professor/dashboard");
                    this.getRedirectStrategy().sendRedirect(request, response, "/professor/dashboard");
                } else if (a.getAuthority().compareTo("ROLE_STUDENT") == 0) {
                    log.info("User has role ROLE_STUDENT - redirecting to: /student/dashboard");
                    this.getRedirectStrategy().sendRedirect(request, response, "/student/dashboard");
                }
            } catch (Exception e) {
                log.error("Error checking user authority." + e);
            }
        });
    }
    
}


