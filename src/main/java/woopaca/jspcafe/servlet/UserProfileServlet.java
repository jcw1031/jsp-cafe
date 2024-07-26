package woopaca.jspcafe.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import woopaca.jspcafe.resolver.RequestParametersResolver;
import woopaca.jspcafe.service.UserService;
import woopaca.jspcafe.servlet.dto.request.UpdateProfileRequest;
import woopaca.jspcafe.servlet.dto.response.UserProfile;

import java.io.IOException;

@WebServlet("/users/profile/*")
public class UserProfileServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext servletContext = config.getServletContext();
        this.userService = (UserService) servletContext.getAttribute("userService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        Long userId = Long.parseLong(pathInfo.substring(1));
        UserProfile profile = userService.getUserProfile(userId);
        request.setAttribute("profile", profile);
        request.getRequestDispatcher("/user/profile.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UpdateProfileRequest updateProfileRequest =
                RequestParametersResolver.resolve(request.getParameterMap(), UpdateProfileRequest.class);
        String pathInfo = request.getPathInfo();
        Long userId = Long.parseLong(pathInfo.substring(1));
        userService.updateUserProfile(userId, updateProfileRequest);
        response.sendRedirect("/users/" + userId);
    }
}