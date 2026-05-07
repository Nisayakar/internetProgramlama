package com.market.filter;

import com.market.bean.SessionBean;
import jakarta.inject.Inject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebFilter(filterName = "AuthFilter", urlPatterns = {"/panel/*", "/app/*"})
public class AuthFilter implements Filter {

    @Inject
    private SessionBean sessionBean;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = req.getRequestURI();


        if (sessionBean == null || !sessionBean.isGirisYapmis()) {
            res.sendRedirect(req.getContextPath() + "/login.xhtml");
            return;
        }


        if (uri.contains("/panel/") && !sessionBean.isAdmin()) {
            res.sendRedirect(req.getContextPath() + "/index.xhtml");
            return;
        }


        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
