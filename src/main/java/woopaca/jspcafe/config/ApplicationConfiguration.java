package woopaca.jspcafe.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import woopaca.jspcafe.repository.UserMemoryRepository;
import woopaca.jspcafe.repository.UserRepository;
import woopaca.jspcafe.service.UserService;

@WebListener
public class ApplicationConfiguration implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        servletContext.setAttribute("userService", userService());
    }

    private UserService userService() {
        return new UserService(userRepository());
    }

    private UserRepository userRepository() {
        return new UserMemoryRepository();
    }
}
