package com.srgbrl.laba.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Set;

@WebFilter("/*")
public class AuthorizationFilter implements Filter {
    
    private static final Set<String> ALLOWED_PATHS = Set.of(
            "/", "/login", "/reg", "/index.jsp", "/login.jsp", "/reg.jsp"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute("user") != null);

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.substring(contextPath.length());

        boolean isAllowedPath = ALLOWED_PATHS.contains(path);
        boolean isResourceRequest = path.startsWith("/resources/");

        if (loggedIn || isAllowedPath || isResourceRequest) {
            chain.doFilter(request, response);
        } else {
            res.sendRedirect(contextPath + "/");
        }
    }
}
