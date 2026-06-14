package filter;

import entity.User;
import enums.Role;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(filterName = "SessionFilter", urlPatterns = {"/panel/*", "/app/*"})
public class SessionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String loginURI = request.getContextPath() + "/login.xhtml";
        String loggedURI = request.getContextPath() + "/panel/products.xhtml";

        Object userObject = request.getSession().getAttribute("user");
        if (!isLoggedIn(userObject)) {
            redirectToLogin(request, response, loginURI);
            return;
        }

        if (isLoginRequest(request, loginURI)) {
            response.sendRedirect(loggedURI);
            return;
        }

        if (isPanelRequest(request) && !isAdmin(userObject)) {
            response.sendRedirect(request.getContextPath() + "/index.xhtml");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isLoggedIn(Object userObject) {
        return userObject instanceof User;
    }

    private boolean isLoginRequest(HttpServletRequest request, String loginURI) {
        return request.getRequestURI().equals(loginURI);
    }

    private boolean isPanelRequest(HttpServletRequest request) {
        return request.getRequestURI().contains("/panel/");
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response, String loginURI) throws IOException {
        if (isAJAXRequest(request)) {
            response.setContentType("text/xml");
            response.setCharacterEncoding("UTF-8");
            response.getWriter()
                    .write("<?xml version='1.0' encoding='UTF-8'?>"
                            + "<partial-response><redirect url='" + loginURI + "'/></partial-response>");
            return;
        }

        response.sendRedirect(loginURI);
    }

    private boolean isAJAXRequest(HttpServletRequest request) {
        String facesRequest = request.getHeader("Faces-Request");
        return "partial/ajax".equals(facesRequest);
    }

    private boolean isAdmin(Object userObject) {
        if (!(userObject instanceof User)) {
            return false;
        }

        User user = (User) userObject;
        return user.getRole() == Role.ADMIN;
    }
}


