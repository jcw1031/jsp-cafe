package woopaca.jspcafe.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import woopaca.jspcafe.model.Authentication;
import woopaca.jspcafe.resolver.RequestParametersResolver;
import woopaca.jspcafe.service.AuthService;
import woopaca.jspcafe.servlet.dto.request.LoginRequest;

import java.io.IOException;

@WebServlet("/auth/*")
public class AuthServlet extends HttpServlet {

    private AuthService authService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext servletContext = config.getServletContext();
        this.authService = (AuthService) servletContext.getAttribute("authService");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String pathInfo = request.getPathInfo();
        if (pathInfo.equals("/login")) {
            login(request, response);
        } else if (pathInfo.equals("/logout")) {
            logout(request, response);
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPatch(req, resp);
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            LoginRequest loginRequest = RequestParametersResolver.resolve(request.getParameterMap(), LoginRequest.class);
            Authentication authentication = authService.authenticate(loginRequest);
            HttpSession session = request.getSession();
            session.setAttribute("authentication", authentication);
            response.sendRedirect("/");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", true);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            request.getRequestDispatcher("/user/login.jsp")
                    .forward(request, response);
        }
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) {
        // TODO 로그아웃 처리
    }
}
