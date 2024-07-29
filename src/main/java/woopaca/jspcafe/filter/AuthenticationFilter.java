package woopaca.jspcafe.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import woopaca.jspcafe.service.AuthService;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@WebFilter("*")
public class AuthenticationFilter implements Filter {

    private final Set<RequestMatcher> excludeMatchers = new HashSet<>();

    private AuthService authService;

    @Override
    public void init(FilterConfig filterConfig) {
        excludeMatchers.add(new RequestMatcher("GET", "/static/*"));
        excludeMatchers.add(new RequestMatcher("GET", "/favicon.ico"));
        excludeMatchers.add(new RequestMatcher("GET", "/"));
        excludeMatchers.add(new RequestMatcher("GET", "/users/login"));
        excludeMatchers.add(new RequestMatcher("POST", "/auth/login"));
        excludeMatchers.add(new RequestMatcher("GET", "/users/signup"));
        excludeMatchers.add(new RequestMatcher("POST", "/users/signup"));

        ServletContext servletContext = filterConfig.getServletContext();
        this.authService = (AuthService) servletContext.getAttribute("authService");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        for (RequestMatcher matcher : excludeMatchers) {
            if (matcher.matches(method, requestURI)) {
                chain.doFilter(request, response);
                return;
            }
        }

        HttpSession session = httpRequest.getSession();
        Object authentication = session.getAttribute("authentication");
        if (authentication == null) {
            ((HttpServletResponse) response).sendRedirect("/users/login");
            return;
        }

        chain.doFilter(request, response);
    }
}
