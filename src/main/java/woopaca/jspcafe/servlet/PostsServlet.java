package woopaca.jspcafe.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import woopaca.jspcafe.model.Authentication;
import woopaca.jspcafe.resolver.InputStreamResolver;
import woopaca.jspcafe.resolver.QueryStringResolver;
import woopaca.jspcafe.service.PostService;
import woopaca.jspcafe.servlet.dto.request.PostEditRequest;
import woopaca.jspcafe.servlet.dto.response.PostDetailsResponse;

import java.io.IOException;
import java.io.InputStream;

@MultipartConfig
@WebServlet("/posts/*")
public class PostsServlet extends HttpServlet {

    private PostService postService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext servletContext = config.getServletContext();
        this.postService = (PostService) servletContext.getAttribute("postService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        Long postId = Long.parseLong(pathInfo.substring(1));
        PostDetailsResponse post = postService.getPostDetails(postId);
        request.setAttribute("post", post);
        request.setAttribute("separator", '\n');
        request.getRequestDispatcher("/post/details.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String pathInfo = request.getPathInfo();
        Long postId = Long.parseLong(pathInfo.substring(1));
        InputStream inputStream = request.getInputStream();
        String queryString = InputStreamResolver.convertToString(inputStream);
        PostEditRequest postEditRequest = QueryStringResolver.resolve(queryString, PostEditRequest.class);
        HttpSession session = request.getSession();
        Authentication authentication = (Authentication) session.getAttribute("authentication");
        postService.updatePost(postId, postEditRequest, authentication);
    }
}
